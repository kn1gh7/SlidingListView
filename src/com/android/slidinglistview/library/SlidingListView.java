package com.android.slidinglistview.library;

import com.android.slidinglistviewlibrarynsample.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.Toast;

public class SlidingListView extends ListView {

    public final static int SLIDE_MODE_NONE = 0;

    public final static int SLIDE_MODE_BOTH = 1;

    public final static int SLIDE_MODE_RIGHT = 2;

    public final static int SLIDE_MODE_LEFT = 3;
	
	int slideFrontView, slideBackView;
	boolean openSlidingWhenLongPressed, openSlideRight, openSlideLeft, multipleViewsOpen, slideMode, closeAllItemsOnListScroll;
	int SLIDEMODE_LEFT, SLIDEMODE_RIGHT, SLIDEMODE_NONE, SLIDEMODE_BOTH;
	
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
	}
}