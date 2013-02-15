/**
 * 
 */
package cn.edu.tongji.sse.glucosemeter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Marco Schwanengel
 * 
 */
public class ProgressBar extends View {
	private ShapeDrawable[] mSegments;
	private Path[] mPaths;
	private int mProgress;
	private boolean animated;
	private boolean animationUp;

	private int height;
	private int width;

	/**
	 * 
	 */
	private Handler animationHandler;
	private Runnable animationHandlerTask;

	/**
	 * class default values
	 */
	private static final int DEFAUL_COLOR = 0xFF000000;
	private static final int SEGMENT_COUNT = 0x00000006;

	/**
	 * progress bar segments
	 */
	private static final int SEGMENT_0 = 0x00000000;
	private static final int SEGMENT_1 = 0x00000001;
	private static final int SEGMENT_2 = 0x00000002;
	private static final int SEGMENT_3 = 0x00000003;
	private static final int SEGMENT_4 = 0x00000004;
	private static final int SEGMENT_5 = 0x00000005;

	/**
	 * progress levels
	 */
	public static final int PROG_LEVEL_0 = 0x00000000; // level 0; zero segments
	public static final int PROG_LEVEL_1 = 0x00000001; // level 1; one segment
	public static final int PROG_LEVEL_2 = 0x00000002; // level 2; two segments
	public static final int PROG_LEVEL_3 = 0x00000003; // level 3; three
														// segments
	public static final int PROG_LEVEL_4 = 0x00000004; // level 4; four segments
	public static final int PROG_LEVEL_5 = 0x00000005; // level 5; four segments

	/**
	 * progress bar animation
	 */
	public static final int ON = 0x00000001;
	public static final int OFF = 0x00000000;

	/**
	 * @param context
	 */
	public ProgressBar(Context context) {
		super(context);
		initProgressBar();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initProgressBar();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initProgressBar();
	}

	/**
	 * 
	 */
	private void initProgressBar() {
		mSegments = new ShapeDrawable[SEGMENT_COUNT];
		mPaths = new Path[SEGMENT_COUNT];
		mProgress = PROG_LEVEL_0;

		// crate paths for battery segments
		createPaths();

		// create shapes based on created paths before
		for (int i = 0; i < SEGMENT_COUNT; i++) {
			mSegments[i] = new ShapeDrawable(new PathShape(mPaths[i], 60, 20));
		}

		// set default painting attributes
		for (ShapeDrawable sdr : mSegments) {
			sdr.getPaint().setColor(DEFAUL_COLOR);
			sdr.getPaint().setStyle(Style.FILL);
			sdr.getPaint().setStrokeWidth(1f);
			sdr.getPaint().setAntiAlias(true);
		}
		mSegments[SEGMENT_0].getPaint().setStyle(Style.STROKE);

		animationHandler = new Handler();
		animationHandlerTask = new Runnable() {

			@Override
			public void run() {
				long currentPost = SystemClock.uptimeMillis();
				
				switch (mProgress) {
				case PROG_LEVEL_1:
					mProgress++;
					animationUp = true;
					break;
				case PROG_LEVEL_5:
					mProgress--;
					animationUp = false;
					break;
				default:
					if(animationUp) mProgress++;
					else mProgress--;
					break;
				}
				
				// draw new segment constellation
				invalidate();
				
				animationHandler.postAtTime(animationHandlerTask, currentPost + 1000);
			}
		};

		animationHandler.removeCallbacks(animationHandlerTask);
	}

	/**
	 * 
	 */
	private void createPaths() {
		// Frame of the progress bar
		mPaths[SEGMENT_0] = new Path();
		mPaths[SEGMENT_0].moveTo(2, 2);
		mPaths[SEGMENT_0].lineTo(58, 2);
		mPaths[SEGMENT_0].lineTo(58, 18);
		mPaths[SEGMENT_0].lineTo(2, 18);
		mPaths[SEGMENT_0].close();

		// segment 1
		mPaths[SEGMENT_1] = new Path();
		mPaths[SEGMENT_1].moveTo(3, 3);
		mPaths[SEGMENT_1].lineTo(13, 3);
		mPaths[SEGMENT_1].lineTo(13, 17);
		mPaths[SEGMENT_1].lineTo(3, 17);
		mPaths[SEGMENT_1].close();

		// segment 2
		mPaths[SEGMENT_2] = new Path();
		mPaths[SEGMENT_2].moveTo(14, 3);
		mPaths[SEGMENT_2].lineTo(24, 3);
		mPaths[SEGMENT_2].lineTo(24, 17);
		mPaths[SEGMENT_2].lineTo(14, 17);
		mPaths[SEGMENT_2].close();

		// segment 3
		mPaths[SEGMENT_3] = new Path();
		mPaths[SEGMENT_3].moveTo(25, 3);
		mPaths[SEGMENT_3].lineTo(35, 3);
		mPaths[SEGMENT_3].lineTo(35, 17);
		mPaths[SEGMENT_3].lineTo(25, 17);
		mPaths[SEGMENT_3].close();

		// segment 4
		mPaths[SEGMENT_4] = new Path();
		mPaths[SEGMENT_4].moveTo(36, 3);
		mPaths[SEGMENT_4].lineTo(46, 3);
		mPaths[SEGMENT_4].lineTo(46, 17);
		mPaths[SEGMENT_4].lineTo(36, 17);
		mPaths[SEGMENT_4].close();

		// segment 5
		mPaths[SEGMENT_5] = new Path();
		mPaths[SEGMENT_5].moveTo(47, 3);
		mPaths[SEGMENT_5].lineTo(57, 3);
		mPaths[SEGMENT_5].lineTo(57, 17);
		mPaths[SEGMENT_5].lineTo(47, 17);
		mPaths[SEGMENT_5].close();
	}

	/**
	 * 
	 * @param progress
	 */
	public void setProgressLevel(int progress) {
		if (progress < PROG_LEVEL_0) mProgress = PROG_LEVEL_0;
		else if (progress > PROG_LEVEL_5) mProgress = PROG_LEVEL_5;
		else mProgress = progress;
		
		invalidate();
	}

	/**
	 * 
	 * @param animation
	 */
	public void setAnimation(int animation) {
		if (animation == ON) {
			// start animation
			mProgress = PROG_LEVEL_1;
			animationHandler.removeCallbacks(animationHandlerTask);
			animationHandler.post(animationHandlerTask);
			animated = true;
		}
		if (animation == OFF) {
			// stop animation
			animationHandler.removeCallbacks(animationHandlerTask);
			animated = false;
		}
	}

	/**
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		for (ShapeDrawable sdr : mSegments) {
			sdr.getPaint().setColor(color);
		}

		invalidate();
	}

	/**
	 * 
	 * @param segment
	 * @param canvas
	 */
	private void drawSegement(int segment, Canvas canvas) {
		mSegments[segment].setBounds(getPaddingLeft(), getPaddingTop(), width
				- getPaddingRight(), height - getPaddingBottom());
		mSegments[segment].draw(canvas);
	}

	/**
	 * 
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	private void measureDimension(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		width = widthSpecSize;
		height = heightSpecSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (mProgress) {
		case PROG_LEVEL_0:
			drawSegement(SEGMENT_0, canvas);
			break;
		case PROG_LEVEL_1:
			drawSegement(SEGMENT_0, canvas);
			drawSegement(SEGMENT_1, canvas);
			break;
		case PROG_LEVEL_2:
			drawSegement(SEGMENT_0, canvas);
			if (animated) {
				drawSegement(SEGMENT_2, canvas);
			} else {
				drawSegement(SEGMENT_1, canvas);
				drawSegement(SEGMENT_2, canvas);
			}
			break;
		case PROG_LEVEL_3:
			drawSegement(SEGMENT_0, canvas);
			if (animated) {
				drawSegement(SEGMENT_3, canvas);
			} else {
				drawSegement(SEGMENT_1, canvas);
				drawSegement(SEGMENT_2, canvas);
				drawSegement(SEGMENT_3, canvas);
			}
			break;
		case PROG_LEVEL_4:
			drawSegement(SEGMENT_0, canvas);
			if (animated) {
				drawSegement(SEGMENT_4, canvas);
			} else {
				drawSegement(SEGMENT_1, canvas);
				drawSegement(SEGMENT_2, canvas);
				drawSegement(SEGMENT_3, canvas);
				drawSegement(SEGMENT_4, canvas);
			}
			break;
		case PROG_LEVEL_5:
			drawSegement(SEGMENT_0, canvas);
			if (animated) {
				drawSegement(SEGMENT_5, canvas);
			} else {
				drawSegement(SEGMENT_1, canvas);
				drawSegement(SEGMENT_2, canvas);
				drawSegement(SEGMENT_3, canvas);
				drawSegement(SEGMENT_4, canvas);
				drawSegement(SEGMENT_5, canvas);
			}
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureDimension(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
