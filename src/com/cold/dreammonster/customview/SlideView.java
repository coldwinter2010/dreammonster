package com.cold.dreammonster.customview;

import com.cold.dreammonster.R;
import com.cold.dreammonster.constinfo.ScreenInfo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class SlideView extends View {

	private int totalWidth, totalHeight;

	public interface SlideListener {
		void onDone();
	}

	private static final int MSG_REDRAW = 1;
	private static final int DRAW_INTERVAL = 50;
	private static final int STEP_LENGTH = 5;

	private Paint mPaint;
	private VelocityTracker mVelocityTracker;
	private int mMaxVelocity;
	private LinearGradient mGradient;
	private int[] mGradientColors;
	private int mGradientIndex;
	private Interpolator mInterpolator;
	private SlideListener mSlideListener;
	private float mDensity;
	private Matrix mMatrix;
	private ValueAnimator mValueAnimator;
	public boolean done = false;
	private String mText;
	private int mTextSize;
	private int mTextLeft;
	private int mTextTop;

	private int mSlider;
	private Bitmap mSliderBitmap;
	private Bitmap mSliderBg;
	private Bitmap arrow1;
	private Bitmap arrow2;
	Rect dst;
	Rect dst1;
	Rect dst3;
	private int mSliderLeft;
	private int mSliderTop;
	private Rect mSliderRect;
	private int mSlidableLength; // SlidableLength = BackgroundWidth -
									// LeftMagins - RightMagins - SliderWidth
	private int mEffectiveLength; // Suggested length is 20pixels shorter than
									// SlidableLength
	private float mEffectiveVelocity;
	Handler tickHandler;

	private float mStartX;
	private float mStartY;
	private float mLastX;
	private float mMoveX;
	int alpha;
	Rect bounds = new Rect();
	boolean arrow3 = true;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_REDRAW:
				mMatrix.setTranslate(mGradientIndex, 0);
				mGradient.setLocalMatrix(mMatrix);
				invalidate();
				mGradientIndex += STEP_LENGTH * mDensity;
				if (mGradientIndex > mSlidableLength) {
					mGradientIndex = 0;
				}
				mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
				break;
			}
		}
	};

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewConfiguration configuration = ViewConfiguration.get(context);
		mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
		mInterpolator = new AccelerateDecelerateInterpolator();
		mDensity = getResources().getDisplayMetrics().density;
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);

		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
		mText = typeArray.getString(R.styleable.SlideView_maskText);
		mTextSize = typeArray.getDimensionPixelSize(R.styleable.SlideView_maskTextSize, R.dimen.mask_text_size);
		mTextLeft = typeArray.getDimensionPixelSize(R.styleable.SlideView_maskTextMarginLeft,
				R.dimen.mask_text_margin_left);
		mTextTop = typeArray.getDimensionPixelSize(R.styleable.SlideView_maskTextMarginTop,
				R.dimen.mask_text_margin_top);

		mSlider = typeArray.getResourceId(R.styleable.SlideView_slider, R.drawable.slideview_button);
		mSliderLeft = typeArray.getDimensionPixelSize(R.styleable.SlideView_sliderMarginLeft,
				R.dimen.slider_margin_left);
		mSliderTop = typeArray.getDimensionPixelSize(R.styleable.SlideView_sliderMarginTop, R.dimen.slider_margin_top);
		mSliderBg = BitmapFactory.decodeResource(getResources(), R.drawable.slideview_bg);
		mSliderBitmap = BitmapFactory.decodeResource(getResources(), mSlider);
		mSliderRect = new Rect(mSliderLeft, mSliderTop, mSliderLeft + totalHeight, mSliderTop + totalHeight);
		arrow1 = BitmapFactory.decodeResource(getResources(), R.drawable.slideviewarrow1);
		arrow2 = BitmapFactory.decodeResource(getResources(), R.drawable.slideviewarrow2);
		
		
		mEffectiveVelocity = typeArray.getDimensionPixelSize(R.styleable.SlideView_effectiveVelocity,
				R.dimen.effective_velocity);
		typeArray.recycle();
		mGradientColors = new int[] { Color.argb(255, 82, 49, 200), Color.argb(255, 82, 49, 200),
				Color.argb(255, 82, 49, 200) };
		mGradient = new LinearGradient(0, 0, 100 * mDensity, 0, mGradientColors, new float[] { 0, 0.7f, 1 },
				TileMode.MIRROR);
		mGradientIndex = 0;
		mPaint = new Paint();
		mMatrix = new Matrix();
		mPaint.setTextSize(mTextSize);
		dst = new Rect(0, 0, totalHeight, totalHeight);
		dst1 = new Rect(0, 0, totalWidth, totalWidth);
		dst3 = new Rect(totalWidth * 2 / 9, totalHeight * 3 / 8, totalWidth / 3, totalHeight * 3 / 4);
		// mTextLeft = ScreenInfo.height / 12;
		// mTextTop = ScreenInfo.height / 18;
		run();
	}

	public void run() {
		tickHandler = new Handler();
		tickHandler.post(tickRunnable);
	}

	private Runnable tickRunnable = new Runnable() {
		public void run() {
			postInvalidate();

			tickHandler.postDelayed(tickRunnable, 1000);
			arrow3 = !arrow3;
		}
	};

	public void setSlideListener(SlideListener slideListener) {
		mSlideListener = slideListener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setShader(mGradient);
		mPaint.setAntiAlias(true);
		dst.left = (int) mMoveX;
		dst.right = totalHeight + (int) mMoveX;

		// Slider's moving rely on the mMoveX.
		// mPaint.setTextSize(26);
		mPaint.getTextBounds(mText, 0, mText.length(), bounds);
		if (arrow3) {
			canvas.drawBitmap(arrow2, null, dst3, mPaint);

		} else {
			canvas.drawBitmap(arrow1, null, dst3, mPaint);

		}
		canvas.drawBitmap(mSliderBg, null, dst1, null);
		canvas.drawText(mText, (getMeasuredWidth() - bounds.width()) / 2, (getMeasuredHeight() + bounds.height()) / 2,
				mPaint);
		canvas.drawBitmap(mSliderBitmap, null, dst, null);
	}

	public void reset() {
		if (mValueAnimator != null) {
			mValueAnimator.cancel();
		}
		mMoveX = 0;
		mPaint.setAlpha(255);
		mHandler.removeMessages(MSG_REDRAW);
		mHandler.sendEmptyMessage(MSG_REDRAW);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// If the start point is not on the slider, moving slider will not be
		// executed.
		if (event.getAction() != MotionEvent.ACTION_DOWN && !mSliderRect.contains((int) mStartX, (int) mStartY)) {
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
			}
			return super.onTouchEvent(event);
		}
		acquireVelocityTrackerAndAddMovement(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mStartX = event.getX();
			mStartY = event.getY();
			mLastX = mStartX;
			mHandler.removeMessages(MSG_REDRAW);
			break;

		case MotionEvent.ACTION_MOVE:
			mLastX = event.getX();
			if (mLastX > mStartX) { // Can not exceed the left boundary,
									// otherwise, mMoveX will get a minimum
									// value.
				// The transparency of text will be changed along with moving
				// slider
				alpha = (int) (255 - (mLastX - mStartX) * 2 / mDensity);
				mSliderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slideview_button_move);
				if (alpha > 1) {
					mPaint.setAlpha(alpha);
					if (mAlpha != null)
						mAlpha.OnAlpha(alpha);
				} else {
					mPaint.setAlpha(0);
				}
				// Can not exceed the right boundary, otherwise, mMoveX will get
				// a maximum value.
				if (mLastX > mSlidableLength) {
					mLastX = mSlidableLength;
					mMoveX = mSlidableLength;
				} else {
					mMoveX = (int) (mLastX - mStartX);
				}
			} else {
				mLastX = mStartX;
				mMoveX = 0;
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
			float velocityX = mVelocityTracker.getXVelocity();
			if (mLastX > mEffectiveLength || velocityX > mEffectiveVelocity) {
				startAnimator(mLastX, mSlidableLength, velocityX, true);
				if (mDone != null)
					mDone.OnDone(true);
			} else {
				startAnimator(mLastX - mStartX, 0, velocityX, false);
				mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
				if (mDone != null)
					mDone.OnDone(false);
				mSliderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slideview_button);

			}
			releaseVelocityTracker();
			break;
		}
		return true;
		//return super.onTouchEvent(event);
	}

	private void startAnimator(float start, float end, float velocity, boolean isRightMoving) {
		if (velocity < mEffectiveVelocity) {
			velocity = mEffectiveVelocity;
		}
		int duration = (int) (Math.abs(end - start) * 1000 / velocity);
		mValueAnimator = ValueAnimator.ofFloat(start, end);
		mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mMoveX = (Float) animation.getAnimatedValue();
				int alpha = (int) (255 - (mMoveX) * 3 / mDensity);
				if (alpha > 1) {
					mPaint.setAlpha(alpha);
				} else {
					mPaint.setAlpha(0);
				}
				invalidate();
			}
		});
		mValueAnimator.setDuration(duration);
		mValueAnimator.setInterpolator(mInterpolator);
		if (isRightMoving) {
			mValueAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if (mSlideListener != null) {
						mSlideListener.onDone();
					}
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}
			});
		}
		mValueAnimator.start();
	}

	private OnAlphaListener mAlpha = null;

	public void setOnAlphaListener(OnAlphaListener alpha) {
		mAlpha = alpha;
	}

	public interface OnAlphaListener {
		public void OnAlpha(int x);
	}

	private OnDoneListener mDone = null;

	public void setOnDoneListener(OnDoneListener done) {
		mDone = done;
	}

	public interface OnDoneListener {
		public void OnDone(boolean x);
	}

	private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
	}

	private void releaseVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width, height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = ScreenInfo.width / 4 * 3;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = ScreenInfo.height / 12;
		}
		totalWidth = width;
		totalHeight = height;
		mSlidableLength = totalWidth - totalHeight;
		mEffectiveLength = mSlidableLength - 10;
		dst.set(0, 0, totalHeight, totalHeight);
		dst1.set(0, 0, totalWidth, totalHeight);
		dst3.set(totalWidth * 2 / 9, totalHeight * 3 / 8, totalWidth / 3, totalHeight * 3 / 4);
		mSliderRect.set(mSliderLeft, mSliderTop, mSliderLeft + totalHeight, mSliderTop + totalHeight);
		setMeasuredDimension(width, height);
	}
}