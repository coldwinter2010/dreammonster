package com.cold.dreammonster.activity;

import java.util.ArrayList;
import java.util.List;

import com.cold.dreammonster.R;
import com.cold.dreammonster.R.id;
import com.cold.dreammonster.R.layout;
import com.cold.dreammonster.R.menu;
import com.cold.dreammonster.bean.SmallClockInfo;
import com.cold.dreammonster.customview.ClockView;
import com.cold.dreammonster.customview.SmallClockContainer;
import com.cold.dreammonster.customview.SmallClockView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private ClockView mClockView;
	private SmallClockContainer scContainer;
	private List<SmallClockView>scList=new ArrayList<SmallClockView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mClockView=(ClockView)findViewById(R.id.clockView);
		scContainer=(SmallClockContainer)findViewById(R.id.sc_container);
		initSmallClock();
		
		// TODO fortext
		Log.i("MainActivity.onCreate", "测试GitHub同步   By LC");
	}

	
	private void initSmallClock() {
		//TODO �������ݿ⣬����������Ϣ
		SmallClockInfo info=new SmallClockInfo(11, 40, 1, 1, 0, "ff", 0, "");
		 ImageView mIcon = new ImageView(this);
	        mIcon.setImageResource(R.drawable.ic_launcher);
	        mIcon.setTag(info);
		SmallClockView view=new SmallClockView(this, info);
		scList.add(view);
		scContainer.addView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mClockView!=null){
			mClockView.recycle();
			mClockView=null;
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
