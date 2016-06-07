package com.cold.dreammonster.activity;

import com.cold.dreammonster.BaseActivity;
import com.cold.dreammonster.R;
import com.cold.dreammonster.utils.SettingUtils;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * 
 * <p>
 * Title: LoadingActivity
 * </p>
 * <p>
 * Description:loading页面，打开应用后进入此页面
 * </p>
 * 
 * @author HX
 * @date 2016年4月26日 下午9:56:58
 */
public class LoadingActivity extends BaseActivity {

	private final int icons0[] = { R.drawable.monster0_0, R.drawable.monster1_0, R.drawable.monster2_0,
			R.drawable.monster3_0, R.drawable.monster4_0 };
	private final int icons1[] = { R.drawable.monster0_1, R.drawable.monster1_1, R.drawable.monster2_1,
			R.drawable.monster3_1, R.drawable.monster4_1 };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run() {
				goNext();
				
			}
			
		}, 2000);
		
	}
	
	
	private void goNext(){
		Intent it=new Intent();
		if(SettingUtils.getFirstUsing(this)){
			it.setClass(this, GuideActivity.class);
		}else{
			//TODO goto main
			it.setClass(this, MainActivity.class);
		}
		startActivity(it);
		finish();
	}
	
	
	private Handler mHandler=new Handler();

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus)
			loadAnim();// 要执行的动画方法
		super.onWindowFocusChanged(hasFocus);
	}

	@SuppressWarnings("deprecation")
	public void loadAnim() {
		int index = (int) Math.floor((Math.random() * 5));
		ImageView view = (ImageView) findViewById(R.id.imageView_guide1_title);
		AnimationDrawable animationDrawable = new AnimationDrawable();// (AnimationDrawable)view.getDrawable();
		animationDrawable.addFrame(getResources().getDrawable(icons0[index]), 300);
		animationDrawable.addFrame(getResources().getDrawable(icons1[index]), 300);
		animationDrawable.setOneShot(false);
		view.setImageDrawable(animationDrawable);
		animationDrawable.start();
	}

}
