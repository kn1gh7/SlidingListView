package com.android.slidinglistview.library;

import com.android.slidinglistview.sample.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SlidingListView extends ListView {
	int slideFrontView, slideBackView;
	boolean openSlidingWhenLongPressed, closeAllItemsOnListScroll, closeOpenItemsOnTouchDown = false;
	
	private static final int NO_SCROLLING = -1;
	private static final int SCROLLING_X = 1;
	private static final int SCROLLING_Y = 2;
	private int scrollState = NO_SCROLLING;
    public final static String SLIDE_DEFAULT_FRONT_VIEW = "slidelist_frontview";

    public final static String SLIDE_DEFAULT_BACK_VIEW = "slidelist_backview";
    SlidingListViewTouchListener touchListener = null;
    
    private float lastX, lastY;
    
    private int touchSlop;
    
	public SlidingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	
	public SlidingListView(Context context, int slideFrontView, int slideBackView) {
        super(context);
        this.slideFrontView = slideFrontView;
        this.slideBackView = slideBackView;
        init(null);
    }
	
	private void init(AttributeSet attrs) {
        openSlidingWhenLongPressed = true;
        closeAllItemsOnListScroll = true;
        closeOpenItemsOnTouchDown = false;
        
        if (attrs != null) {
        	TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SlidingListView);
        	slideFrontView = styled.getResourceId(R.styleable.SlidingListView_frontView, 0);
            slideBackView = styled.getResourceId(R.styleable.SlidingListView_backView, 0);
            closeOpenItemsOnTouchDown = styled.getBoolean(R.styleable.SlidingListView_closeOpenItemsOnDown, false);
        }
        
        if (slideFrontView == 0 || slideBackView == 0) {
        	slideFrontView = getContext().getResources().getIdentifier(SLIDE_DEFAULT_FRONT_VIEW, "id", getContext().getPackageName());
        	slideBackView = getContext().getResources().getIdentifier(SLIDE_DEFAULT_BACK_VIEW, "id", getContext().getPackageName());

            if (slideFrontView == 0 || slideBackView == 0) {
                throw new RuntimeException(String.format("You forgot the attributes swipeFrontView or swipeBackView. You can add this attributes or use '%s' and '%s' identifiers", SLIDE_DEFAULT_FRONT_VIEW, SLIDE_DEFAULT_BACK_VIEW));
            }
        }
        touchListener = new SlidingListViewTouchListener(this, slideFrontView, slideBackView);
        setOnTouchListener(touchListener);
        setOnScrollListener(touchListener.createScrollListener());
	}
	/*
	 * abc_screen_toolbar
	 * android:touchscreenBlocksFocus="true"
	 * */
	
	private void showToast(String msg) {
		Toast.makeText(getContext().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	public boolean isViewOpen(int positionInAdapter) {
		return touchListener.isItemOpen(positionInAdapter);
	}
	
	public void resetScrollState() {
		showLog("Scroll State Reset Done");
		scrollState = NO_SCROLLING;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stubs
		super.setAdapter(adapter);
		if (adapter != null) {
			touchListener.resetItems();
		
			adapter.registerDataSetObserver(new DataSetObserver() {
				
				public void onChanged() {
					super.onChanged();
					touchListener.resetItems();
				};
			});
			showLog("set Adapter not null");
		} else {
			showLog("Adapter Not set NULL");
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		if (scrollState == SCROLLING_X) {
			return touchListener.onTouch(this, ev);
		}
		
		switch (ev.getActionMasked()) {
			
			case MotionEvent.ACTION_DOWN:
				showLog("ACTION DOWN Before onInterceptTouchEvent");
				super.onInterceptTouchEvent(ev);
				showLog("ACTION DOWN After onInterceptTouchEvent");
				showLog("ACTION DOWN Before onTouch");
				touchListener.onTouch(this, ev);
				showLog("ACTION DOWN After onTouch");
				lastX = ev.getRawX();
				lastY = ev.getRawY();
				scrollState = NO_SCROLLING;
				return false;
			case MotionEvent.ACTION_MOVE:
				checkInMoving(ev.getRawX(), ev.getRawY());
				return scrollState == SCROLLING_Y;
			case MotionEvent.ACTION_UP:
				showLog("ACTION UP Before onTouch");
				touchListener.onTouch(this, ev);
				showLog("ACTION UP After onTouch");
                return scrollState == SCROLLING_Y;
			default:
				scrollState = NO_SCROLLING;
				break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	private void checkInMoving(float x, float y) {
        final int xDiff = (int) Math.abs(x - lastX);
        final int yDiff = (int) Math.abs(y - lastY);

        final int touchSlop = this.touchSlop;
        boolean xMoved = xDiff > touchSlop;
        boolean yMoved = yDiff > touchSlop;

        if (xMoved) {
            scrollState = SCROLLING_X;
        }

        if (yMoved) {
            scrollState = SCROLLING_Y;
        }
    }
	
	public void resetScroll() {
		scrollState = NO_SCROLLING;
	}
	
	private void showLog(String msg) {
		Log.d("List View", msg);
	}
}