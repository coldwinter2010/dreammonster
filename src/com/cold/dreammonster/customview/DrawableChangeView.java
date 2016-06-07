package com.cold.dreammonster.customview;
/**
 * 
* <p>Title: DrawableChangeView</p>
* <p>Description: 引导页的渐变背景</p>
* <p>Company: CMRI 2016</p> 
* @author cmri
* @date 2016年5月27日 上午11:14:00
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义view，绘制两层drawable，上层为前一个view的图片，下层为下一个view的图片
 * 每次滑动，将上层图片根据其所占屏幕的比例设置透明度来达到一种简便的效果
 * @author Administrator
 * @date 2013/1/29 17:52
 */
public class DrawableChangeView extends View  {

	private int mPosition;
    private int mPrevPosition=1;
    private float mDegree;
    
    private Drawable[] mDrawables;
    private Drawable mBack;
    
    public DrawableChangeView(Context context) {
        super(context);
        invalidate();
        
    }

    public DrawableChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableChangeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDrawables(Drawable[] drawables) {
        mDrawables = drawables;
        mBack = drawables[0];
    }
    
    public void setPosition(int position) {
        mPosition = position;
    }
    
    public void setDegree(float degree) {
        mDegree = degree;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawables == null) {
            return;
        }
        int alpha = 255 - (int) (mDegree * 255);
        Drawable fore = mDrawables[mPosition];
        fore.setBounds(0, 0, getWidth(), getHeight());
        mBack.setBounds(0, 0, getWidth(), getHeight());
        if (mPrevPosition != mPosition) {
            if (mPosition != mDrawables.length - 1) {
                mBack = mDrawables[mPosition + 1];
            } else {
                mBack = mDrawables[mPosition];
            }
        }
        
        fore.setAlpha(alpha);
        mBack.setAlpha(255);
        mBack.draw(canvas);
        fore.draw(canvas);
        mPrevPosition = mPosition;
        invalidate();
    }
   
}

