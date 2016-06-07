package com.cold.dreammonster.activity;

import com.cold.dreammonster.BaseActivity;
import com.cold.dreammonster.R;
import com.cold.dreammonster.customview.DrawableChangeView;
import com.cold.dreammonster.utils.SettingUtils;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 
 * <p>
 * Title: GuideActivity
 * </p>
 * <p>
 * Description:引导页面
 * </p>
 * <p>
 * Company: CMRI 2016
 * </p>
 * 
 * @author cmri
 * @date 2016年5月27日 上午11:10:11
 */
public class GuideActivity extends BaseActivity {
	// 背景色
	private static final int BGCOLOR[] = { 0xff0da9a0, 0xff1373fe, 0xff5230cc };
	private static final int IMAGE[] = { R.drawable.one_before_after, R.drawable.two_before,
			R.drawable.three_before_after };
	private DrawableChangeView bgView;
	private ViewPager viewPager;
	private Drawable[] bgDrawables = new Drawable[3];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		viewPager = (ViewPager) this.findViewById(R.id.viewPager);
		bgView = (DrawableChangeView) this.findViewById(R.id.drawableChangeView);
		for (int i = 0; i < 3; i++) {
			bgDrawables[i] = new ColorDrawable(BGCOLOR[i]);
		}
		viewPager.setAdapter(new pagerAdapter());
		viewPager.setPageTransformer(true, new AccordionTransformer());
		bgView.setDrawables(bgDrawables);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				bgView.setPosition(position);
				bgView.setDegree(arg1);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}
	
	private void goNext(){
		Intent it=new Intent(this,MainActivity.class);
		startActivity(it);
		finish();
	}

	private class pagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@SuppressLint("InflateParams")
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getApplicationContext())
					.inflate(R.layout.layout_guide_content, null);
			ImageView imageView = (ImageView) layout.findViewById(R.id.image_guide_content);
			Button btn = (Button) layout.findViewById(R.id.bt_to_main);
			layout.setDrawingCacheEnabled(false);
			imageView.setImageResource(IMAGE[position]);
			switch (position) {
			case 0:
				btn.setVisibility(View.GONE);
				break;
			case 1:
				btn.setVisibility(View.GONE);
				break;
			case 2:
				btn.setVisibility(View.VISIBLE);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						SettingUtils.setFirstUsing(GuideActivity.this, false);
						goNext();
						
					}
				});
				break;

			default:
				break;
			}
			// 把View添加到容器中
			container.addView(layout);
			return layout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
	}

	private class AccordionTransformer implements PageTransformer {

		@Override
		public void transformPage(View view, float position) {
			if (position < -1) {
				ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(view, 1);
			} else if (position <= 0) {
				ViewHelper.setPivotX(view, view.getMeasuredWidth());
				ViewHelper.setPivotY(view, 0);
				ViewHelper.setScaleX(view, 1 + position);
			} else if (position <= 1) {
				ViewHelper.setPivotX(view, 0);
				ViewHelper.setPivotY(view, 0);
				ViewHelper.setScaleX(view, 1 - position);
			} else {
				ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(view, 1);
			}
		}
	}

}
