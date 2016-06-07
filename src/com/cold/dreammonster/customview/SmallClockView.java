package com.cold.dreammonster.customview;


import com.cold.dreammonster.R;
import com.cold.dreammonster.bean.SmallClockInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class SmallClockView extends View {

	private Bitmap background;
	private Bitmap alarm;
	private int widthSize;//clockViewµÄ¿í&¸ß
	private SmallClockInfo scInfo;
	private Rect dst,dst1;
	int type = 0;
	int mWidth;
	int mHeigh;
	int mTempWidth;
	int mTempHeigh;
	float angle = 0;
	String sHour;
	String sMinute;
	int hour;
	int minute;

	int alarmOn;
	int direction = 0;
	int i = 0;
	Context mContext;
	int id;
	Paint p = new Paint();
	Rect bounds = new Rect();

	public void setClockId(int id) {
		this.id = id;

	}

	public int getClockId() {
		return id;
	}

	public void setType(int type) {
		this.type = type;

	}


	public void setAngle(float angle) {
		this.angle = angle;

	}

	public void setHour(int hour) {
		this.hour = hour;

	}

	public void setMinute(int minute) {
		this.minute = minute;

	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setAlarmOn(int alarmOn) {
		this.alarmOn = alarmOn;
	}

	public int getAlarmOn() {
		return alarmOn;
	}

	public SmallClockView(Context context, SmallClockInfo clockinfo) {
		super(context);
		mContext = context;
		scInfo = clockinfo;
		this.setTag(scInfo);
		this.type = clockinfo.type;
		if (type == 0) {
			background = BitmapFactory.decodeResource(getResources(), R.drawable.smallclockview_bg_pink);
		} else {

			background = BitmapFactory.decodeResource(getResources(), R.drawable.smallclockview_bg_blue);
		}
		setId(clockinfo.id);
		setClockId(clockinfo.id);
		setHour(clockinfo.hour);
		setMinute(clockinfo.minute);
		float rotate = clockinfo.hour % 12 * 30.0f + clockinfo.minute / 60.0f * 30.0f;
		setAngle(rotate);
		setAlarmOn(clockinfo.alarmOn);
		int direction = 0;
		if (rotate > 0 && rotate <= 90) {
			direction = 1;
		} else if (rotate > 90 && rotate <= 180) {
			direction = 2;
		} else if (rotate > 180 && rotate <= 270) {
			direction = 3;
		}
		setDirection(direction);
		dst=new Rect();
		dst1=new Rect();
		this.setWillNotDraw(false);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent();
				// intent.setClass(mContext, AlarmSetActivity.class);
				// intent.putExtra("id", smallClock[currentId].getClockId());
				// intent.putExtra("alarmset", 1);
				// Log.d("TAG", "ACE175" + smallClock[currentId].getClockId());
				// startActivityForResult(intent, 100);
			}

		});
		alarm = BitmapFactory.decodeResource(getResources(), R.drawable.smallclockview_alarm);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.clipRect(0, 0, 10, 10);
//		canvas.drawColor(Color.CYAN);
		canvas.save();
		canvas.rotate(angle, widthSize/2, widthSize/2);
		dst.set(0, 0, widthSize, widthSize);
		canvas.drawBitmap(background, null, dst, null);
		canvas.restore();
		int picwidth=alarm.getWidth();
		dst1.set(widthSize*1/2-picwidth/2, widthSize*1/2-picwidth/2, widthSize*1/2+picwidth/2, widthSize*1/2+picwidth/2);
			if (alarmOn != 0) {
//				canvas.drawBitmap(alarm, null, dst1, null);
//				canvas.drawBitmap(alarm, widthSize*1/2-picwidth/2, widthSize*1/2-picwidth/2, null);
			}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		widthSize = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(widthSize,widthSize);
	}

}
