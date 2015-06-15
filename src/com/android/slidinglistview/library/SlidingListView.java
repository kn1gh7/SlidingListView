package com.android.slidinglistview.library;

import com.android.slidinglistview.sample.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.Toast;

public class SlidingListView extends ListView {
	int slideFrontView, slideBackView;
	boolean openSlidingWhenLongPressed, closeAllItemsOnListScroll;
	
    public final static String SLIDE_DEFAULT_FRONT_VIEW = "swipelist_frontrl";

    public final static String SLIDE_DEFAULT_BACK_VIEW = "swipelist_backrl";
    SlidingListViewTouchListener touchListener = null;
	
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
        
        /*
         * <resources>
		    <declare-styleable name="SwipeListView">
		        <attr name="swipeOpenOnLongPress" format="boolean"/>
		        <attr name="swipeAnimationTime" format="integer"/>
		        <attr name="swipeOffsetLeft" format="dimension"/>
		        <attr name="swipeOffsetRight" format="dimension"/>
		        <attr name="swipeCloseAllItemsWhenMoveList" format="boolean"/>
		        <attr name="swipeFrontView" format="reference"/>
		        <attr name="swipeBackView" format="reference"/>
		        <attr name="swipeMode" format="enum">
		            <enum name="none" value="0"/>
		            <enum name="both" value="1"/>
		            <enum name="right" value="2"/>
		            <enum name="left" value="3"/>
		        </attr>
		        <attr name="swipeActionLeft" format="enum">
		            <enum name="reveal" value="0"/>
		            <enum name="dismiss" value="1"/>
		            <enum name="choice" value="2"/>
		        </attr>
		        <attr name="swipeActionRight" format="enum">
		            <enum name="reveal" value="0"/>
		            <enum name="dismiss" value="1"/>
		            <enum name="choice" value="2"/>
		        </attr>
		        <attr name="swipeDrawableChecked" format="reference"/>
		        <attr name="swipeDrawableUnchecked" format="reference"/>
		    </declare-styleable>
		</resources>
         * */
        
        if (attrs != null) {
        	TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SlidingListView);
        	slideFrontView = styled.getResourceId(R.styleable.SlidingListView_frontView, 0);
            slideBackView = styled.getResourceId(R.styleable.SlidingListView_backView, 0);
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
	}
	
	private void showToast(String msg) {
		Toast.makeText(getContext().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			showToast("ACTION DOWN");
			super.onInterceptTouchEvent(ev);
			touchListener.onTouch(this, ev);
			break;
		case MotionEvent.ACTION_UP:
			showToast("ACTION UP");
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}