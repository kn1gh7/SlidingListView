package com.android.slidinglistview.sample;

import java.util.ArrayList;
import java.util.List;

import com.android.slidinglistview.library.SlidingListView;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
			return convertView;
		}
		
		@Override
		public int getCount() {
			return 10;
		}
	}
	
	private class DataModel {
		String count;
	}
	
	private class ViewHolder {
		public LinearLayout swipelist_backrl;
		public RelativeLayout swipelist_frontrl;
		public TextView front_textview;
		
		public ViewHolder(View convertView) {
			swipelist_backrl = (LinearLayout) convertView.findViewById(R.id.swipelist_backrl);
			swipelist_frontrl = (RelativeLayout) convertView.findViewById(R.id.swipelist_frontrl);
			front_textview = (TextView) convertView.findViewById(R.id.front_textview);
		}
	}
}