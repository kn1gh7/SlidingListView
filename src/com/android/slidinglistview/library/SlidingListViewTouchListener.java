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
import android.widget.AbsListView.RecyclerListener;


public class SlidingListViewTouchListener implements OnTouchListener {
	Context context;
	int frontViewId, backViewId;
	View frontView, backView, parentView;
	SlidingListView slidingListView;
	int slop;
	float downX;
	MotionEvent downEvent;
	ArrayList<Boolean> opened;
	boolean isListMoving = false;
	private static int INVALID_POSITION = -1;
	int downPosition;
	
	public SlidingListViewTouchListener(SlidingListView slidingListView, int frontViewId, int backViewId) {
		this.slidingListView = slidingListView;
		this.frontViewId = frontViewId;
		this.backViewId = backViewId;
		ViewConfiguration vc = ViewConfiguration.get(slidingListView.getContext());
        slop = vc.getScaledTouchSlop();
        opened = new ArrayList<Boolean>();
        
        slidingListView.setRecyclerListener(new RecyclerListener() {
			
			@Override
			public void onMovedToScrapHeap(View view) {
				view.findViewById(SlidingListViewTouchListener.this.frontViewId).clearAnimation();
				view.findViewById(SlidingListViewTouchListener.this.frontViewId).setClickable(true);
			}
		});
	}
	
	private void showLog(String msg) {
		Log.d("Touch Log", msg);
	}
	
	private void setParentView(View parentView) {
		this.parentView = parentView;
	}
	
	private void setFrontView(View frontView) {
		this.frontView = frontView;
	}
	
	private void setBackView(View backView) {
		this.backView = backView;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (isListMoving)
				return false;
			
			downX = event.getRawX();
			downEvent = event;
			showLog("ACTION_DOWN " + downX);
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
                boolean allowSlide = slidingListView.getAdapter().isEnabled(childPosition) && slidingListView.getAdapter().getItemViewType(childPosition) >= 0; 
                if (allowSlide && rect.contains(x, y)) {
                	showLog("i: " + i + " downPosition: " + downPosition + " newDown: " + childPosition + " opened?:" + opened.get(childPosition));
                	downPosition = childPosition;
                	setParentView(child);
                	setFrontView(child.findViewById(frontViewId));
                	setBackView(child.findViewById(backViewId));
                	
                	frontView.setClickable(!opened.get(downPosition));
                	frontView.setLongClickable(!opened.get(downPosition));
                }
            }
            v.onTouchEvent(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			if (isListMoving)
				return false;
			float currentX = event.getRawX();
			int moveX = (int) (downX - currentX);
			if (moveX > 0 && moveX > slop && downPosition != INVALID_POSITION) {
				if (!opened.get(downPosition)) {
					slidingListView.requestDisallowInterceptTouchEvent(true);
					//slideOpen(downPosition);
				}
				return true;
			} else if (moveX < 0 && Math.abs(moveX) > slop && downPosition != INVALID_POSITION) {
				if (opened.get(downPosition)) {
					slidingListView.requestDisallowInterceptTouchEvent(true);
					//slideClose(downPosition);
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isListMoving)
				return false;
			currentX = event.getRawX();
			moveX = (int) (downX - currentX);
			if (moveX > 0 && moveX > slop && downPosition != INVALID_POSITION) {
				if (!opened.get(downPosition)) {
					showLog("ACTION_UP Opening Item At:" + downPosition);
					closeAllOpenItems();
					slideOpen(downPosition);
					downX = 0;
					downPosition = INVALID_POSITION;
					slidingListView.resetScroll();
				}
				return true;
			} else if (moveX < 0 && Math.abs(moveX) > slop && downPosition != INVALID_POSITION) {
				if (opened.get(downPosition)) {
					showLog("ACTION_UP Closing Item At: " + downPosition);
					slideClose(downPosition);
					downX = 0;
					downPosition = INVALID_POSITION;
				}
				return true;
			} else {
				int startPosition = slidingListView.getFirstVisiblePosition();
				setParentView(slidingListView.getChildAt(downPosition-startPosition));
				setFrontView(slidingListView.getChildAt(downPosition-startPosition).findViewById(frontViewId));
				setBackView(slidingListView.getChildAt(downPosition-startPosition).findViewById(backViewId));
				
				backView.dispatchTouchEvent(event);
			}
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
	
	/*private void openCurrent(View view, MotionEvent event) {
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
                boolean allowSwipe = slidingListView.getAdapter().isEnabled(childPosition) && slidingListView.getAdapter().getItemViewType(childPosition) >= 0 && !opened.get(childPosition);
                if (allowSwipe && rect.contains(x, y) && !opened.get(childPosition)) {
                	showLog("Slide Open i:" + i + " childPosition:" + childPosition + " " + opened.get(childPosition));
                	opened.set(childPosition, true);
                	slideOpen(slidingListView.getChildAt(childPosition-slidingListView.getFirstVisiblePosition()));
                	slidingListView.getChildAt(childPosition-slidingListView.getFirstVisiblePosition()).findViewById(frontViewId).setClickable(!opened.get(childPosition));;
                } else {
                	showLog("else of Slide Open");
                }
            }
		}
	}*/
	
	/*void closeOpenedItems() {
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
    }*/
	
	private void slideOpen(int downPosition) {
		closeAllOpenItems();
		int startPosition = slidingListView.getFirstVisiblePosition();
		setParentView(slidingListView.getChildAt(downPosition-startPosition));
		setFrontView(slidingListView.getChildAt(downPosition-startPosition).findViewById(frontViewId));
		setBackView(slidingListView.getChildAt(downPosition-startPosition).findViewById(backViewId));
		
		float currentX = frontView.getLeft();
		float currentY = frontView.getTop();
		TranslateAnimation tAnim = new TranslateAnimation(currentX, -frontView.getWidth(), currentY, currentY);
		tAnim.setFillAfter(true);
		tAnim.setInterpolator(new DecelerateInterpolator());
		tAnim.setDuration(150);
		frontView.startAnimation(tAnim);
		opened.set(downPosition, true);
		
		if (slidingListView != null)
			slidingListView.resetScroll();
		
		/*backView.setClickable(true);
		frontView.setClickable(false);*/
	}
	
	/*private void closeCurrent(View view, MotionEvent event) {
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
                    	showLog("Slide Close i:" + i + " childPosition:" + childPosition + " " + opened.get(childPosition));
                    	opened.set(childPosition, false);
                    	slideClose(slidingListView.getChildAt(childPosition-slidingListView.getFirstVisiblePosition()));
                    } else {
                    	showLog("else of Slide Close");
                    }
                }
                downX = 0;
                downEvent = null;
			}
		} else {
			showLog("NO Sliding View");
		}
	}*/
	
	private void closeAllOpenItems() {
		int startPosition = slidingListView.getFirstVisiblePosition();
		for (int i = 0; i< slidingListView.getAdapter().getCount(); i++) {
			if (opened.get(i)) {
				int itemPosInView = i-startPosition;
				if (itemPosInView >=0 && slidingListView.getChildAt(itemPosInView) != null 
						&& slidingListView.getChildAt(itemPosInView).findViewById(frontViewId) != null) {
					setParentView(slidingListView.getChildAt(itemPosInView));
					setFrontView(slidingListView.getChildAt(itemPosInView).findViewById(frontViewId));
					setBackView(slidingListView.getChildAt(itemPosInView).findViewById(backViewId));
					slideClose(i);
				}
			}
		}
	}
	
	private void slideClose(int downPosition) {
		float currentX = frontView.getLeft();
		float currentY = frontView.getTop();
		showLog(currentX + " " + currentY + " Closing Item: " + downPosition);
		TranslateAnimation tAnim = new TranslateAnimation(-frontView.getWidth(), 0, currentY, currentY);
		tAnim.setFillAfter(true);
		tAnim.setInterpolator(new DecelerateInterpolator());
		tAnim.setDuration(150);
		frontView.startAnimation(tAnim);
		opened.set(downPosition, false);
		
		/*backView.setClickable(false);
		frontView.setClickable(true);*/
	}
	
	public AbsListView.OnScrollListener createScrollListener() {
		return new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					showLog("Scrolling state changed");
					closeAllOpenItems();
					isListMoving = true;
				} else {
					showLog("Scroll State None Relevant");
					isListMoving = false;
					if (slidingListView != null)
						slidingListView.resetScroll();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				showLog("onScroll");
			}
		};
	}
}