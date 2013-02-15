/**
 * 
 */
package cn.edu.tongji.sse.glucosemeter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Marco Schwanengel
 * 
 */
public class SevenSegmentDigit extends View {

	/**
	 * 
	 */
	private ShapeDrawable[] mDigitSegments;
	private Path[] mSegmentPaths;
	private int mDigit;
	private boolean mComma;

	/**
	 * 
	 */
	private static final int SEGMENT_COUNT = 8;

	private static final int DEFAULT_COLOR = 0xFF000000;

	private static final int segmentA = 0;
	private static final int segmentB = 1;
	private static final int segmentC = 2;
	private static final int segmentD = 3;
	private static final int segmentE = 4;
	private static final int segmentF = 5;
	private static final int segmentG = 6;
	private static final int segmentDP = 7;

	/**
	 * @param context
	 */
	public SevenSegmentDigit(Context context) {
		super(context);
		initSevenSegmentDigit();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SevenSegmentDigit(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSevenSegmentDigit();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SevenSegmentDigit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSevenSegmentDigit();
	}

	private void initSevenSegmentDigit() {
		mDigitSegments = new ShapeDrawable[SEGMENT_COUNT];
		mSegmentPaths = createPaths();
		mDigit = -1;

		for (int i = 0; i < SEGMENT_COUNT; i++) {
			mDigitSegments[i] = new ShapeDrawable(new PathShape(
					mSegmentPaths[i], 60, 90));
		}

		// set default digit color and anti-aliasing
		for (ShapeDrawable sdr : mDigitSegments) {
			sdr.getPaint().setColor(DEFAULT_COLOR);
			sdr.getPaint().setAntiAlias(true);
		}
	}

	private Path[] createPaths() {
		Path[] returnPaths = new Path[SEGMENT_COUNT];

		// segment A
		returnPaths[segmentA] = new Path();
		returnPaths[segmentA].moveTo(14, 5);
		returnPaths[segmentA].lineTo(49, 5);
		returnPaths[segmentA].lineTo(45, 12);
		returnPaths[segmentA].lineTo(16, 12);
		returnPaths[segmentA].lineTo(10, 9);
		returnPaths[segmentA].close();

		// segment B
		returnPaths[segmentB] = new Path();
		returnPaths[segmentB].moveTo(52, 5);
		returnPaths[segmentB].lineTo(56, 8);
		returnPaths[segmentB].lineTo(53, 41);
		returnPaths[segmentB].lineTo(51, 43);
		returnPaths[segmentB].lineTo(46, 37);
		returnPaths[segmentB].lineTo(48, 13);
		returnPaths[segmentB].close();

		// segment C
		returnPaths[segmentC] = new Path();
		returnPaths[segmentC].moveTo(51, 47);
		returnPaths[segmentC].lineTo(52, 48);
		returnPaths[segmentC].lineTo(49, 81);
		returnPaths[segmentC].lineTo(44, 85);
		returnPaths[segmentC].lineTo(42, 77);
		returnPaths[segmentC].lineTo(44, 54);
		returnPaths[segmentC].close();

		// segment D
		returnPaths[segmentD] = new Path();
		returnPaths[segmentD].moveTo(11, 77);
		returnPaths[segmentD].lineTo(39, 77);
		returnPaths[segmentD].lineTo(41, 85);
		returnPaths[segmentD].lineTo(7, 85);
		returnPaths[segmentD].lineTo(3, 80);
		returnPaths[segmentD].close();

		// segment E
		returnPaths[segmentE] = new Path();
		returnPaths[segmentE].moveTo(8, 47);
		returnPaths[segmentE].lineTo(13, 52);
		returnPaths[segmentE].lineTo(11, 74);
		returnPaths[segmentE].lineTo(4, 77);
		returnPaths[segmentE].lineTo(6, 49);
		returnPaths[segmentE].close();

		// segment F
		returnPaths[segmentF] = new Path();
		returnPaths[segmentF].moveTo(10, 13);
		returnPaths[segmentF].lineTo(17, 16);
		returnPaths[segmentF].lineTo(15, 37);
		returnPaths[segmentF].lineTo(9, 43);
		returnPaths[segmentF].lineTo(7, 41);
		returnPaths[segmentF].close();

		// segment G
		returnPaths[segmentG] = new Path();
		returnPaths[segmentG].moveTo(15, 41);
		returnPaths[segmentG].lineTo(45, 41);
		returnPaths[segmentG].lineTo(49, 45);
		returnPaths[segmentG].lineTo(44, 49);
		returnPaths[segmentG].lineTo(14, 49);
		returnPaths[segmentG].lineTo(10, 45);
		returnPaths[segmentG].close();
		
		// segment DP
		returnPaths[segmentDP] = new Path();
		returnPaths[segmentDP].addCircle(56, 80, 4, Path.Direction.CW);

		return returnPaths;
	}

	public void setDigit(int digit) {
		// check for valid digit from 0-15 (hex 0-F)
		// if it is bigger or smaller set -1 (empty)
		if ((digit >= 0) && (digit < 16)) {
			mDigit = digit;
		} else {
			mDigit = -1;
		}

		invalidate();
	}
	
	public void setComma(boolean yesNo) {
		mComma = yesNo;
	}

	public void setDigitColor(int color) {
		for (ShapeDrawable sdr : mDigitSegments) {
			sdr.getPaint().setColor(color);
		}

		invalidate();
	}

	private void drawSegment(int segment, Canvas canvas) {
		mDigitSegments[segment].setBounds(getPaddingLeft(), getPaddingTop(), getWidth()
				- getPaddingRight(), getHeight() - getPaddingBottom());
		mDigitSegments[segment].draw(canvas);
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = specSize;// (int) mTextPaint.measureText(mText) +
								// getPaddingLeft()
			// + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = specSize;// (int) (-mAscent + mTextPaint.descent()) +
								// getPaddingTop()
			// + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (mDigit) {
		case 0:
			drawSegment(segmentA, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentF, canvas);
			break;
		case 1:
			drawSegment(segmentB, canvas);
			drawSegment(segmentC, canvas);
			break;
		case 2:
			drawSegment(segmentA, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 3:
			drawSegment(segmentA, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 4:
			drawSegment(segmentF, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentC, canvas);
			break;
		case 5:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 6:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 7:
			drawSegment(segmentA, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentC, canvas);
			break;
		case 8:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 9:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 10:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentC, canvas);
			break;
		case 11:
			drawSegment(segmentF, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 12:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 13:
			drawSegment(segmentB, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentC, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 14:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			drawSegment(segmentD, canvas);
			break;
		case 15:
			drawSegment(segmentA, canvas);
			drawSegment(segmentF, canvas);
			drawSegment(segmentG, canvas);
			drawSegment(segmentE, canvas);
			break;
		}
		
		if (mComma) drawSegment(segmentDP, canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width;
		int height;
		int width_noPad;
		int height_noPad;

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = measureWidth(widthMeasureSpec);
		height = measureHeight(heightMeasureSpec);
		width_noPad = width - getPaddingLeft() - getPaddingRight();
		height_noPad = height - getPaddingTop() - getPaddingBottom();

		// keep ration of 2 x 3
		if ((width_noPad * 3 / 2) < height_noPad) {
			height = (width_noPad * 3 / 2) + getPaddingTop() + getPaddingBottom();
		} else {
			width = (height_noPad * 2 / 3) + getPaddingLeft() + getPaddingRight();;
		}

		setMeasuredDimension(width, height);
	}
}
