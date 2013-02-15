/**
 * 
 */
package cn.edu.tongji.sse.glucosemeter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author marco
 * 
 */
public class DashLine extends View {

	private int height;
	private int width;
	
	Paint paint;
	
	/**
	 * class default values
	 */
	private static final int DEFAUL_COLOR = 0xFF000000;
	private static final int DASH_COUNT = 10;

	/**
	 * @param context
	 */
	public DashLine(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(DEFAUL_COLOR);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public DashLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DashLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setColor(int color) {
		paint.setColor(color);

		invalidate();
	}

	/**
	 * 
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	private void measureDimension(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		width = widthSpecSize;
		height = width / 20;
		paint.setStrokeWidth(width / 100);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		// calculate dash width
		float dashWidth = (width - getPaddingLeft() - getPaddingRight())
				/ DASH_COUNT;

		// crate dash line
		float[] linePts = new float[4 * DASH_COUNT];
		for (int i = 0; i < DASH_COUNT; i++) {
			linePts[4 * i] = dashWidth * i + getPaddingLeft();
			linePts[4 * i + 1] = height / 2;
			linePts[4 * i + 2] = linePts[4 * i] + dashWidth * 0.8f - getPaddingRight();
			linePts[4 * i + 3] = height / 2;
		}

		// draw dash line
		canvas.drawLines(linePts, paint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureDimension(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
