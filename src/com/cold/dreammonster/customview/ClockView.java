package com.cold.dreammonster.customview;

import java.util.Calendar;
import java.util.TimeZone;

import com.cold.dreammonster.R;
import com.cold.dreammonster.constinfo.ScreenInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/* 大闹钟视图*/
public class ClockView extends View {
	private Bitmap bmpDial;
	private Bitmap bmpHour;
	private Bitmap bmpMinute;
	private Paint mPaint;
	private Handler tickHandler;
	private int hourWidth;
	private int hourHeight;
	private int minuteWidth;
	private int minuteHeight;
	private int centerX;
	private Context mContext;
	private int totalWidth;
	boolean flag = false;
	private String sTimeZoneString;
	Rect dst=new Rect();
	Rect dst1=new Rect();
	Rect dst2=new Rect();

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sTimeZoneString = "GMT+8:00";
		mContext = context;
		bmpHour = BitmapFactory.decodeResource(getResources(), R.drawable.clockview_hour);
		bmpMinute = BitmapFactory.decodeResource(getResources(), R.drawable.clockview_minute);
		bmpDial = BitmapFactory.decodeResource(getResources(), R.drawable.clockview_dial);
		hourWidth = bmpHour.getWidth();
		hourHeight = bmpHour.getHeight();
		minuteWidth = bmpMinute.getWidth();
		minuteHeight = bmpMinute.getHeight();
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		run();
	}

	public void run() {
		tickHandler = new Handler();
		tickHandler.post(tickRunnable);
	}

	
	public void recycle(){
		if(bmpDial!=null){
			bmpDial.recycle();
			bmpDial=null;
		}
		if(bmpHour!=null){
			bmpHour.recycle();
			bmpHour=null;
		}
		if(bmpMinute!=null){
			bmpMinute.recycle();
			bmpMinute=null;
		}
		mPaint=null;
	}
	
	private Runnable tickRunnable = new Runnable() {
		public void run() {
			postInvalidate();
			tickHandler.postDelayed(tickRunnable, 1000);
		}
	};

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
		float minuteRotate = minute * 6.0f;
		dst.set(0, 0,totalWidth, totalWidth);
		dst1.set(totalWidth/2 - hourWidth / 2, totalWidth/2 - hourHeight * 2,
				totalWidth/2 + hourWidth / 2, totalWidth/2);
		dst2.set(totalWidth/2 - minuteWidth / 2, totalWidth/2 - minuteHeight * 2,
				totalWidth/2 + minuteWidth / 2, totalWidth/2);
		centerX = totalWidth/2;
		canvas.drawBitmap(bmpDial, null, dst, null);
		canvas.save();
		canvas.rotate(hourRotate, centerX, totalWidth/2);
		canvas.drawBitmap(bmpHour, null, dst1, null);
		canvas.restore();
		canvas.save();
		canvas.rotate(minuteRotate, centerX, totalWidth/2);
		canvas.drawBitmap(bmpMinute, null, dst2, null);
		canvas.restore();

	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int width;  
	    if (widthMode == MeasureSpec.EXACTLY)  
	    {  
	    	width = widthSize;  
	    } else  
	    {  
	        width=ScreenInfo.width*2/3;
	    }  
	    totalWidth=width;
	    setMeasuredDimension(width, width);  
	}
}
