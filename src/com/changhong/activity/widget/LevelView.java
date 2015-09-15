package com.changhong.activity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.llw.salon.R;

public class LevelView extends View {

	private static Bitmap mBmStar;
	private static Bitmap mBmMoon;
	private static Bitmap mBmSun;
	private int mLevel = 0;

	public LevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmapResource();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SmartLevelView);
		mLevel = a.getInt(R.styleable.SmartLevelView_level, 0);
		a.recycle();
	}

	private void initBitmapResource() {
		mBmStar = BitmapFactory.decodeResource(getResources(), R.drawable.star);
		mBmMoon = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		mBmSun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
	}

	public void setLevel(int level) {
		if (mLevel != level) {
			mLevel = level;
			invalidate();
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = measureHeight(heightMeasureSpec);
		int width = measureWidth(widthMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = calculateWidth();
//		if (specMode == MeasureSpec.AT_MOST) {
//			result = specSize;
//		} else if (specMode == MeasureSpec.EXACTLY) {
//			result = specSize;
//		}
		return result;
	}
	
	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = mBmSun.getHeight();
		// if (specMode == MeasureSpec.AT_MOST) {
		// result = specSize;
		// } else if (specMode == MeasureSpec.EXACTLY) {
		// result = specSize;
		// }
		return result;
	}
	
	private int calculateWidth(){
		int pos = 0;
		if (mLevel > 0) {
			int level = mLevel;
			int star = level % 5;
			int moon = level / 5;
			level = moon;
			if (level > 0) {
				moon = level % 5;
			}
			int sun = level / 5;
			for (int i = 0; i < sun; i++) {
				pos += mBmSun.getWidth() + 10;
			}
			for (int i = 0; i < moon; i++) {
				pos += mBmMoon.getWidth() + 10;
			}
			for (int i = 0; i < star; i++) {
				pos += mBmStar.getWidth() + 10;
			}
		}
		return pos;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLevel > 0) {
			Paint paint = new Paint();
			int pos = 0;
			int level = mLevel;
			int star = level % 5;
			int moon = level / 5;
			level = moon;
			if (level > 0) {
				moon = level % 5;
			}
			int sun = level / 5;
			for (int i = 0; i < sun; i++) {
				canvas.drawBitmap(mBmSun, pos, 0, paint);
				pos += mBmSun.getWidth() + 10;
			}
			for (int i = 0; i < moon; i++) {
				canvas.drawBitmap(mBmMoon, pos, 0, paint);
				pos += mBmMoon.getWidth() + 10;
			}
			for (int i = 0; i < star; i++) {
				canvas.drawBitmap(mBmStar, pos, 0, paint);
				pos += mBmStar.getWidth() + 10;
			}
		}
	}
}
