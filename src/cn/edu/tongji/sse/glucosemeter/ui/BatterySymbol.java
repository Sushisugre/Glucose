/**
 * 
 */
package cn.edu.tongji.sse.glucosemeter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Marco Schwanengel
 *
 */
public class BatterySymbol extends View {
	private ShapeDrawable[] mSegments;
	private Path[] mPaths;
	private int mLevel;
	
	private int height;
	private int width;

	/**
	 * class default values
	 */
	private static final int DEFAUL_COLOR = 0xFF000000;
	private static final int SEGMENT_COUNT = 0x00000005;
	
	/**
	 * battery segments
	 */
	private static final int BAT_SEGMENT_0 = 0x00000000;
	private static final int BAT_SEGMENT_1 = 0x00000001;
	private static final int BAT_SEGMENT_2 = 0x00000002;
	private static final int BAT_SEGMENT_3 = 0x00000003;
	private static final int BAT_SEGMENT_4 = 0x00000004;
	
	/**
	 * battery levels
	 */
	public static final int BAT_LEVEL_0 = 0x00000000;	// level 0; zero segments
	public static final int BAT_LEVEL_1 = 0x00000001;	// level 1; one segment
	public static final int BAT_LEVEL_2 = 0x00000002;	// level 2; two segments
	public static final int BAT_LEVEL_3 = 0x00000003;	// level 3; three segments
	public static final int BAT_LEVEL_4 = 0x00000004;	// level 4; four segments
	
	/**
	 * @param context
	 */
	public BatterySymbol(Context context) {
		super(context);
		initBatterySymbol();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BatterySymbol(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBatterySymbol();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BatterySymbol(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBatterySymbol();
	}
	
	private void initBatterySymbol() {		
		mSegments = new ShapeDrawable[SEGMENT_COUNT];
		mPaths = new Path[SEGMENT_COUNT];
		mLevel = BAT_LEVEL_0;
		
		// crate paths for battery segments
		createPaths();
		
		// create shapes based on created paths before
		for (int i = 0; i < SEGMENT_COUNT; i++) {
			mSegments[i] = new ShapeDrawable(new PathShape(
					mPaths[i], 33, 14));
		}

		// set default painting attributes
		for (ShapeDrawable sdr : mSegments) {
			sdr.getPaint().setColor(DEFAUL_COLOR);
			sdr.getPaint().setStyle(Style.FILL);
			sdr.getPaint().setStrokeWidth(1f);
			sdr.getPaint().setAntiAlias(true);
		}
		mSegments[BAT_SEGMENT_0].getPaint().setStyle(Style.STROKE);
	}
	
	private void createPaths() {
		// empty battery
		mPaths[BAT_SEGMENT_0] = new Path();
		mPaths[BAT_SEGMENT_0].moveTo(2, 2);
		mPaths[BAT_SEGMENT_0].lineTo(29, 2);
		mPaths[BAT_SEGMENT_0].lineTo(29, 12);
		mPaths[BAT_SEGMENT_0].lineTo(2, 12);
		mPaths[BAT_SEGMENT_0].close();
		mPaths[BAT_SEGMENT_0].moveTo(29, 5);
		mPaths[BAT_SEGMENT_0].lineTo(31, 5);
		mPaths[BAT_SEGMENT_0].lineTo(31, 9);
		mPaths[BAT_SEGMENT_0].lineTo(29, 9);
		
		// level 1 ; 25%
		mPaths[BAT_SEGMENT_1] = new Path();
		mPaths[BAT_SEGMENT_1].moveTo(3, 4);
		mPaths[BAT_SEGMENT_1].lineTo(10, 11);
		mPaths[BAT_SEGMENT_1].lineTo(3, 11);
		mPaths[BAT_SEGMENT_1].close();
		
		// level 2 ; 50%
		mPaths[BAT_SEGMENT_2] = new Path();
		mPaths[BAT_SEGMENT_2].moveTo(3, 3);
		mPaths[BAT_SEGMENT_2].lineTo(11, 3);
		mPaths[BAT_SEGMENT_2].lineTo(19, 11);
		mPaths[BAT_SEGMENT_2].lineTo(11, 11);
		mPaths[BAT_SEGMENT_2].close();
		
		// level 3 ; 75%
		mPaths[BAT_SEGMENT_3] = new Path();
		mPaths[BAT_SEGMENT_3].moveTo(12, 3);
		mPaths[BAT_SEGMENT_3].lineTo(20, 3);
		mPaths[BAT_SEGMENT_3].lineTo(28, 11);
		mPaths[BAT_SEGMENT_3].lineTo(20, 11);
		mPaths[BAT_SEGMENT_3].close();
		
		// level 4 ; 100%
		mPaths[BAT_SEGMENT_4] = new Path();
		mPaths[BAT_SEGMENT_4].moveTo(21, 3);
		mPaths[BAT_SEGMENT_4].lineTo(28, 3);
		mPaths[BAT_SEGMENT_4].lineTo(28, 10);
		mPaths[BAT_SEGMENT_4].close();
		
	}
	
	public void setBatteryLevel(int level) {
		if (level < BAT_LEVEL_0) mLevel = BAT_LEVEL_0;
		else if (level > BAT_LEVEL_4) mLevel = BAT_LEVEL_4;
		else mLevel = level;
		
		invalidate();
	}
	
	public void setColor(int color) {
		for (ShapeDrawable sdr : mSegments) {
			sdr.getPaint().setColor(color);
		}
		
		invalidate();
	}
	
	private void drawSegment(int segment, Canvas canvas) {
		mSegments[segment].setBounds(getPaddingLeft(), getPaddingTop(), width
				- getPaddingRight(), height - getPaddingBottom());
		mSegments[segment].draw(canvas);
	}
	
	private void measureDimension(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		
		// TODO keep ratio for dimension
		width = widthSpecSize;
		height = heightSpecSize;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		switch (mLevel) {
		case BAT_LEVEL_0:
			drawSegment(BAT_SEGMENT_0, canvas);
			break;
		case BAT_LEVEL_1:
			drawSegment(BAT_SEGMENT_0, canvas);
			drawSegment(BAT_SEGMENT_1, canvas);
			break;
		case BAT_LEVEL_2:
			drawSegment(BAT_SEGMENT_0, canvas);
			drawSegment(BAT_SEGMENT_1, canvas);
			drawSegment(BAT_SEGMENT_2, canvas);
			break;
		case BAT_LEVEL_3:
			drawSegment(BAT_SEGMENT_0, canvas);
			drawSegment(BAT_SEGMENT_1, canvas);
			drawSegment(BAT_SEGMENT_2, canvas);
			drawSegment(BAT_SEGMENT_3, canvas);
			break;
		case BAT_LEVEL_4:
			drawSegment(BAT_SEGMENT_0, canvas);
			drawSegment(BAT_SEGMENT_1, canvas);
			drawSegment(BAT_SEGMENT_2, canvas);
			drawSegment(BAT_SEGMENT_3, canvas);
			drawSegment(BAT_SEGMENT_4, canvas);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureDimension(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
