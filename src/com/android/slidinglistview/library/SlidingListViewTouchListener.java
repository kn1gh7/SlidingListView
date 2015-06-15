package com.android.slidinglistview.library;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;


public class SlidingListViewTouchListener implements OnTouchListener {
	Context context;
	int slidingfrontView, slidingbackView;
	SlidingListView slidingListView;
	View parentView, childView;
	
	public SlidingListViewTouchListener(SlidingListView slidingListView, int frontView, int backView) {
		this.slidingListView = slidingListView;
		this.slidingfrontView = frontView;
		this.slidingbackView = backView;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int childCount = slidingListView.getChildCount();
            int[] listViewCoords = new int[2];
            slidingListView.getLocationOnScreen(listViewCoords);
            int x = (int) event.getRawX() - listViewCoords[0];
            int y = (int) event.getRawY() - listViewCoords[1];
            View child;
            for (int i = 0; i < childCount; i++) {
                child = slidingListView.getChildAt(i);
                Rect rect = new Rect();
                child.getHitRect(rect);

                int childPosition = slidingListView.getPositionForView(child);

                // dont allow swiping if this is on the header or footer or IGNORE_ITEM_VIEW_TYPE or enabled is false on the adapter
                boolean allowSwipe = slidingListView.getAdapter().isEnabled(childPosition) && slidingListView.getAdapter().getItemViewType(childPosition) >= 0;

                if (allowSwipe && rect.contains(x, y)) {
                    setParentView(child);
                    setFrontView(child.findViewById(slidingfrontView), childPosition);

                    downX = event.getRawX();
                    downPosition = childPosition;

                    slidingfrontView.setClickable(!opened.get(downPosition));
                    slidingfrontView.setLongClickable(!opened.get(downPosition));

                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(motionEvent);
                    if (swipeBackView > 0) {
                        setBackView(child.findViewById(swipeBackView));
                    }
                    break;
                }
            }
            v.onTouchEvent(event);
            return true;
			break;

		default:
			break;
		}
		return false;
	}
	
	private void setParentView(View parentView) {
        this.parentView = parentView;
    }
	
	private void setFrontView(View frontView, final int childPosition) {
	    this.frontView = frontView;
	    frontView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            swipeListView.onClickFrontView(downPosition);
	        }
	    });
	
	    frontView.setOnLongClickListener(new View.OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	            if (swipeOpenOnLongPress) {
	            	closeOpenedItems();
	                if (downPosition >= 0) {
	                    openAnimate(childPosition);
	                }
	            } else {
	                swapChoiceState(childPosition);
	            }
	            return false;
	        }
	
	    });
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