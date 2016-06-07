package com.cold.dreammonster.customview;

import com.cold.dreammonster.bean.SmallClockInfo;
import com.cold.dreammonster.constinfo.ScreenInfo;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class SmallClockContainer extends ViewGroup {
	private int totalWidth;
	private int scWidth;
	private int rcWidth;

	public SmallClockContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		 setWillNotDraw(false);  
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int width;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = ScreenInfo.width * 2 / 3;
		}
		totalWidth = width;
		scWidth = totalWidth / 8;
		rcWidth = (totalWidth - 2*scWidth)/2;
		// 计算出所有的childView的宽和高
		int spec=MeasureSpec.makeMeasureSpec(scWidth, MeasureSpec.EXACTLY);
		measureChildren(spec, spec);
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();
		int cl = 0, ct = 0, cr = 0, cb = 0,cWidth=0;
		for (int i = 0; i < cCount; i++) {
			View childView = getChildAt(i);
			if (!(childView.getTag() instanceof SmallClockInfo))
				continue;
			SmallClockInfo sci = (SmallClockInfo) childView.getTag();
			if (sci == null)
				continue;
			// 根据小闹钟的属性计算它要放置的位置
			float rotate = sci.hour % 12 * 30.0f + sci.minute / 60.0f * 30.0f;
			if (rotate > 0 && rotate <= 90) {
				cl = (int) (rcWidth + scWidth+(rcWidth + scWidth / 2) * Math.sin(rotate * Math.PI / 180)
						- (scWidth / 2) * (Math.cos(rotate * Math.PI / 180) + Math.cos(rotate * Math.PI / 180)));
				ct = (int) (rcWidth + scWidth - (rcWidth + scWidth / 2) * Math.cos(rotate * Math.PI / 180)
						- (scWidth / 2) * (Math.cos(rotate * Math.PI / 180) + Math.cos(rotate * Math.PI / 180)));
				cWidth=	(int) (scWidth*(Math.cos(rotate * Math.PI / 180) + Math.sin(rotate * Math.PI / 180)));	
			} else if (rotate > 90 && rotate <= 180) {
				cWidth=	(int) (scWidth*(-Math.cos(rotate * Math.PI / 180) + Math.sin(rotate * Math.PI / 180)));	
				cl = (int) (rcWidth + scWidth+(rcWidth + scWidth / 2) * Math.sin(rotate * Math.PI / 180)
						- cWidth/2);
				ct = (int) (rcWidth + scWidth - (rcWidth + scWidth / 2) * Math.cos(rotate * Math.PI / 180)
						- cWidth/2);
			} else if (rotate > 180 && rotate <= 270) {
				cWidth=	(int) (scWidth*(-Math.cos(rotate * Math.PI / 180) - Math.sin(rotate * Math.PI / 180)));	
				cl = (int) (rcWidth + scWidth+(rcWidth + scWidth / 2) * Math.sin(rotate * Math.PI / 180)
						- cWidth/2);
				ct = (int) (rcWidth + scWidth - (rcWidth + scWidth / 2) * Math.cos(rotate * Math.PI / 180)
						- cWidth/2);
			} else {
				cWidth=	(int) (scWidth*(Math.cos(rotate * Math.PI / 180) - Math.sin(rotate * Math.PI / 180)));	
				cl = (int) (rcWidth + scWidth+(rcWidth + scWidth / 2) * Math.sin(rotate * Math.PI / 180)
						- cWidth/2);
				ct = (int) (rcWidth + scWidth - (rcWidth + scWidth / 2) * Math.cos(rotate * Math.PI / 180)
						- cWidth/2);
			}//算到最后发现cl ct的表达式在4个象限里完全一致，惊讶了
			cr = cl + cWidth;
			cb = cWidth + ct;
			// 根据这个闹钟的属性放置指定的位置
			childView.layout(cl, ct, cr, cb);
//			childView.layout(l, t, r, b);
		}
	}

}
