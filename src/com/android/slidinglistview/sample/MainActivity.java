package com.android.slidinglistview.sample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.slidinglistview.library.SlidingListView;

public class MainActivity extends ActionBarActivity {

	SlidingListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listview = (SlidingListView) findViewById(R.id.listView);
		ArrayList<DataModel> data = new ArrayList<MainActivity.DataModel>();
		MyAdapter adapter = new MyAdapter(this, data);
		listview.setAdapter(adapter);
	}
	
	private class MyAdapter extends ArrayAdapter<DataModel> {
		List<DataModel> dataModel;
		
		public MyAdapter(Context context, List<DataModel> objects) {
			super(context, 0, 0, objects);
			this.dataModel = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item_front_back, null);
				
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.front_textview.setText(position + " Front Text View");
			viewHolder.slidelist_backview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showLog("backView");
				}
			});
			
			return convertView;
		}
		
		@Override
		public int getCount() {
			return 10;
		}
	}
	
	private void showLog(String msg) {
		Log.d("MainActivity", msg);
	}
	
	private class DataModel {
		String count;
	}
	
	private class ViewHolder {
		public LinearLayout slidelist_backview;
		public RelativeLayout slidelist_frontview;
		public TextView front_textview;
		
		public ViewHolder(View convertView) {
			slidelist_backview = (LinearLayout) convertView.findViewById(R.id.slidelist_backview);
			slidelist_frontview = (RelativeLayout) convertView.findViewById(R.id.slidelist_frontview);
			front_textview = (TextView) convertView.findViewById(R.id.front_textview);
		}
	}
}