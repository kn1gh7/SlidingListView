package com.android.slidinglistview.library;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;


public class SlidingListViewTouchListener implements OnTouchListener {
	Context context;
	int frontView, backView;
	
	public SlidingListViewTouchListener(Context context, int frontView, int backView) {
		this.context = context;
		this.frontView = frontView;
		this.backView = backView;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
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

