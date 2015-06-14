package com.android.slidinglistview.library;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;


public class SlidingListViewTouchListener implements OnTouchListener {
	Context context;
	int frontView, backView;
	SlidingListView slidingListView;
	int slop;
	float downX;
	MotionEvent downEvent;
	ArrayList<Boolean> opened;
	
	public SlidingListViewTouchListener(SlidingListView slidingListView, int frontView, int backView) {
		this.slidingListView = slidingListView;
		this.frontView = frontView;
		this.backView = backView;
		ViewConfiguration vc = ViewConfiguration.get(slidingListView.getContext());
        slop = vc.getScaledTouchSlop();
        opened = new ArrayList<Boolean>();
	}
	
	private void showLog(String msg) {
		Log.d("Touch Log", msg);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		showLog("onTouch SlidingView" + (v.getId() == slidingListView.getId()) + " " + v.getId());
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_MOVE:
			float currentX = event.getRawX();
			int moveX = (int) (downX - currentX);
			if (moveX >0 && moveX > slop) {
				showLog("ACTION_MOVE Open moveX " + moveX);
				openCurrent(v, event);
			} else if (moveX < 0 && -moveX < slop) {
				showLog("ACTION_MOVE Close moveX " + moveX);
				closeCurrent(v, event);
			}
			break;
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downEvent = event;
			showLog("ACTION_DOWN " + downX);
			v.onTouchEvent(event);
			return true;
		case MotionEvent.ACTION_UP:
			currentX = event.getRawX();
			moveX = (int) (downX - currentX);
			if (moveX >0 && moveX > slop) {
				showLog("ACTION_UP Open moveX " + moveX);
				openCurrent(v, event);
			} else if (moveX < 0 && -moveX < slop) {
				showLog("ACTION_UP Close moveX " + moveX);
				closeCurrent(v, event);
			}
			downX = 0;
			v.onTouchEvent(event);
			break;
		default:
			break;
		}
		return false;
	}
	
	public void resetItems() {
		if (slidingListView != null && slidingListView.getAdapter() != null) {
			int count = slidingListView.getAdapter().getCount();
			opened = new ArrayList<Boolean>();
			showLog(count + "");
			for (int i=0; i<count; i++) {
				opened.add(false);
			}
		} else {
			showLog("slidingListView == null: " + (slidingListView == null) + " adapter: " + (slidingListView.getAdapter() == null));
		}
	}
	
	private void openCurrent(View view, MotionEvent event) {
		if (view.getId() == slidingListView.getId()) {
			showLog("Is Sliding View");
			closeAllOpenItems();
			if (downX > 0) {
				int childCount = slidingListView.getChildCount();
                int[] listViewCoords = new int[2];
                slidingListView.getLocationOnScreen(listViewCoords);
                int x = (int) downEvent.getRawX() - listViewCoords[0];
                int y = (int) downEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = slidingListView.getChildAt(i);
                    Rect rect = new Rect();
                    child.getHitRect(rect);

                    int childPosition = slidingListView.getPositionForView(child);

                    // dont allow swiping if this is on the header or footer or IGNORE_ITEM_VIEW_TYPE or enabled is false on the adapter
                    boolean allowSwipe = slidingListView.getAdapter().isEnabled(childPosition) && slidingListView.getAdapter().getItemViewType(childPosition) >= 0;

                    if (allowSwipe && rect.contains(x, y) && !opened.get(childPosition)) {
                    	showLog("Slide Open");
                    	opened.set(i, true);
                    	slideOpen(slidingListView.getChildAt(childPosition-slidingListView.getFirstVisiblePosition()));
                    } else {
                    	showLog("Slide Close");
                    }
                }
			}
		} else {
			showLog("NO Sliding View");
		}
	}
	
	void closeOpenedItems() {
        if (opened != null) {
            int start = slidingListView.getFirstVisiblePosition();
            int end = slidingListView.getLastVisiblePosition();
            for (int i = start; i <= end; i++) {
                if (opened.get(i)) {
                    slideClose(slidingListView.getChildAt(i));
                    opened.set(i, false);
                }
            }
        }
    }
	
	private void slideOpen(View child) {
		float currentX = child.findViewById(frontView).getLeft();
		float currentY = child.findViewById(frontView).getTop();
		TranslateAnimation tAnim = new TranslateAnimation(currentX, -child.findViewById(frontView).getWidth(), currentY, currentY);
		tAnim.setFillAfter(true);
		tAnim.setInterpolator(new DecelerateInterpolator());
		tAnim.setDuration(500);
		child.findViewById(frontView).startAnimation(tAnim);
	}
	
	private void closeCurrent(View view, MotionEvent event) {
		if (view.getId() == slidingListView.getId()) {
			showLog("Is Sliding View");
			if (downX > 0) {
				int childCount = slidingListView.getChildCount();
                int[] listViewCoords = new int[2];
                slidingListView.getLocationOnScreen(listViewCoords);
                int x = (int) downEvent.getRawX() - listViewCoords[0];
                int y = (int) downEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = slidingListView.getChildAt(i);
                    Rect rect = new Rect();
                    child.getHitRect(rect);

                    int childPosition = slidingListView.getPositionForView(child);

                    // dont allow swiping if this is on the header or footer or IGNORE_ITEM_VIEW_TYPE or enabled is false on the adapter
                    boolean allowSwipe = slidingListView.getAdapter().isEnabled(childPosition) && slidingListView.getAdapter().getItemViewType(childPosition) >= 0;

                    if (allowSwipe && rect.contains(x, y) && opened.get(childPosition)) {
                    	showLog("Slide Open");
                    	opened.set(i, false);
                    	slideClose(slidingListView.getChildAt(childPosition-slidingListView.getFirstVisiblePosition()));
                    } else {
                    	showLog("Slide Close");
                    }
                }
                downX = 0;
                downEvent = null;
			}
		} else {
			showLog("NO Sliding View");
		}
	}
	
	private void closeAllOpenItems() {
		for (int i=0; i<slidingListView.getAdapter().getCount(); i++) {
			if (opened.get(i)) {
				slideClose(slidingListView.getChildAt(i));
			}
		}
	}
	
	private void slideClose(View child) {
		float currentX = child.findViewById(frontView).getLeft();
		float currentY = child.findViewById(frontView).getTop();
		showLog(currentX + " " + currentY);
		TranslateAnimation tAnim = new TranslateAnimation(-child.findViewById(frontView).getWidth(), 0, currentY, currentY);
		tAnim.setFillAfter(true);
		tAnim.setInterpolator(new DecelerateInterpolator());
		tAnim.setDuration(500);
		child.findViewById(frontView).startAnimation(tAnim);
	}
	
	public AbsListView.OnScrollListener createScrollListener() {
		return new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		};
	}
}