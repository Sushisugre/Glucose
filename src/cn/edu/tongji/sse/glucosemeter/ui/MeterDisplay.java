/**
 * 
 */
package cn.edu.tongji.sse.glucosemeter.ui;

import cn.edu.tongji.sse.glucosemeter.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Marco Schwanengel
 * 
 */
public class MeterDisplay extends LinearLayout {
	/**
	 * sub-layouts
	 */
	private LinearLayout topSubLayout;
	private LinearLayout middleSubLayout;
	private LinearLayout bottomSubLayout;

	/**
	 * class variables - TextView
	 */
	private TextView textViewAC;
	private TextView textViewTMode;
	private TextView textViewBMode;
	private TextView textViewUMode;
	private TextView textViewEMode;
	private TextView textViewUnit;
	private TextView textViewDateSlash1;
	private TextView textViewDateSlash2;
	private TextView textViewTimeColon;

	/**
	 * class variables - custom views
	 */
	private BatterySymbol battery;
	private ProgressBar progressBar;

	/**
	 * 
	 */
	private Handler blinkHandler;
	private Runnable blinkHandlerTask;
	private int blinkingObjects;
	private boolean objectVisible;
	private boolean mComma;

	/**
	 * 
	 */
	int width;
	int height;

	/**
	 * class default values
	 */
	private static final int DEFAUL_COLOR = 0xFF000000;

	/**
	 * segment state ON OFF
	 */
	public static final int ON = 0x00000001;
	public static final int OFF = 0x00000000;

	/**
	 * operation modes
	 */
	public static final int MODE_T = 0x00000002; // testing mode
	public static final int MODE_B = 0x00000004; // browsing mode
	public static final int MODE_S = 0x00000008; // setup mode
	public static final int MODE_U = 0x00000010; // uploading mode
	public static final int MODE_E = 0x00000020; // error mode

	/**
	 * two units for displaying the result convert mmol/l to mg/dl, multiply by
	 * 18 convert mg/dl to mmol/l, divide by 18
	 */
	public static final int UNIT_L = 0x00000040; // mmol/l
	public static final int UNIT_DL = 0x00000080; // mg/dl

	/**
	 * additional object on the display, that can be animated by blinking
	 */
	public static final int OBJ_TIME_COLON = 0x00000100; // colon between hour
	// and minutes
	public static final int OBJ_BATTERY = 0x00000200; // battery symbol

	public static final int OBJ_YEAR = 0x00000C00; // year in date
	public static final int OBJ_YEAR_D1 = 0x00000400; // only digit 1
	public static final int OBJ_YEAR_D2 = 0x00000800; // only digit 2

	public static final int OBJ_MONTH = 0x00003000; // month in date
	public static final int OBJ_MONTH_D1 = 0x00001000; // only digit 1
	public static final int OBJ_MONTH_D2 = 0x00002000; // only digit 2

	public static final int OBJ_DAY = 0x0000C000; // day in date
	public static final int OBJ_DAY_D1 = 0x00004000; // only digit 1
	public static final int OBJ_DAY_D2 = 0x00008000; // only digit 2

	public static final int OBJ_HOUR = 0x00003000; // hour in time
	public static final int OBJ_HOUR_D1 = 0x00010000; // only digit 1
	public static final int OBJ_HOUR_D2 = 0x00020000; // only digit 2

	public static final int OBJ_MINUTE = 0x000C0000; // minutes in time
	public static final int OBJ_MINUTE_D1 = 0x00040000; // only digit 1
	public static final int OBJ_MINUTE_D2 = 0x00080000; // only digit 2

	public static final int OBJ_AC = 0x00100000; // AC symbol
	public static final int OBJ_UNIT = 0x002000000; // unit symbol

	/**
	 * horizontal dash lines
	 */
	private DashLine[] dashLines;

	private static final int LINE_COUNT = 3;
	private static final int TOP_LINE = 0;
	private static final int MIDDLE_LINE = 1;
	private static final int BOTTOM_LINE = 2;

	/**
	 * convenient defines for result segments
	 */
	private SevenSegmentDigit[] result;

	private static final int DIGIT_COUNT = 3;
	private static final int DIGIT_0 = 0;
	private static final int DIGIT_1 = 1;
	private static final int DIGIT_2 = 2;

	/**
	 * date
	 */
	private SevenSegmentDigit[] year;
	private SevenSegmentDigit[] month;
	private SevenSegmentDigit[] day;
	private View gapView;

	/**
	 * time
	 */
	private SevenSegmentDigit[] hour;
	private SevenSegmentDigit[] minute;

	/**
	 * @param context
	 */
	public MeterDisplay(Context context) {
		super(context);
		initMeterDisplay(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public MeterDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMeterDisplay(context);
	}

	private void initMeterDisplay(Context context) {
		setBackgroundResource(R.drawable.lcd_screen);

		// return earlier if view is in edit mode
		// avoid view designer issues
		if (isInEditMode()) {
			return;
		}

		// allocate memory for dash line array
		dashLines = new DashLine[LINE_COUNT];

		setOrientation(VERTICAL);

		// lay out top layout
		topSubLayout = new LinearLayout(context);
		topSubLayout.setGravity(Gravity.CENTER_VERTICAL);
		addView(topSubLayout);

		// battery symbol
		battery = new BatterySymbol(context);
		// TODO bug: don't set size in constructor
		battery.setLayoutParams(new LayoutParams(0, 0));
		topSubLayout.addView(battery);

		// AC symbol
		textViewAC = new TextView(context);
		textViewAC.setText("~");
		textViewAC.setTextColor(DEFAUL_COLOR);
		textViewAC.setTextSize(20);
		textViewAC.setPadding(20, 0, 10, 0);
		textViewAC.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, 1));
		topSubLayout.addView(textViewAC);

		// T mode
		textViewTMode = new TextView(context);
		textViewTMode.setText("T");
		textViewTMode.setTextColor(DEFAUL_COLOR);
		textViewTMode.setTextSize(20);
		textViewTMode.setPadding(10, 0, 10, 0);
		topSubLayout.addView(textViewTMode);

		// B mode
		textViewBMode = new TextView(context);
		textViewBMode.setText("B");
		textViewBMode.setTextColor(DEFAUL_COLOR);
		textViewBMode.setTextSize(20);
		textViewBMode.setPadding(10, 0, 10, 0);
		topSubLayout.addView(textViewBMode);

		// U mode
		textViewUMode = new TextView(context);
		textViewUMode.setText("U");
		textViewUMode.setTextColor(DEFAUL_COLOR);
		textViewUMode.setTextSize(20);
		textViewUMode.setPadding(10, 0, 10, 0);
		topSubLayout.addView(textViewUMode);

		// E mode
		textViewEMode = new TextView(context);
		textViewEMode.setText("E");
		textViewEMode.setTextColor(DEFAUL_COLOR);
		textViewEMode.setTextSize(20);
		textViewEMode.setPadding(10, 0, 10, 0);
		topSubLayout.addView(textViewEMode);

		// top dash line
		dashLines[TOP_LINE] = new DashLine(context);
		addView(dashLines[TOP_LINE]);

		// lay out middle layout
		middleSubLayout = new LinearLayout(context);
		middleSubLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		middleSubLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
		addView(middleSubLayout);

		// result digits
		// allocate memory for result digits array
		result = new SevenSegmentDigit[DIGIT_COUNT];
		for (int i = 0; i < DIGIT_COUNT; i++) {
			result[i] = new SevenSegmentDigit(context);
		}
		for (int i = DIGIT_COUNT - 1; i >= 0; i--) {
			middleSubLayout.addView(result[i]);
		}

		// unit symbol
		textViewUnit = new TextView(context);
		textViewUnit.setTextColor(DEFAUL_COLOR);
		textViewUnit.setTextSize(40);
		textViewUnit.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		textViewUnit.setGravity(Gravity.RIGHT);
		middleSubLayout.addView(textViewUnit);

		// middle dash line
		dashLines[MIDDLE_LINE] = new DashLine(context);
		addView(dashLines[MIDDLE_LINE]);

		// progress bar
		progressBar = new ProgressBar(context);
		// TODO bug: don't set size in constructor
		progressBar.setPadding(60, 0, 60, 0);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				40));
		addView(progressBar);

		// bottom dash line
		dashLines[BOTTOM_LINE] = new DashLine(context);
		addView(dashLines[BOTTOM_LINE]);

		// layout bottom layout
		bottomSubLayout = new LinearLayout(context);
		bottomSubLayout.setGravity(Gravity.BOTTOM);
		addView(bottomSubLayout);

		// date
		month = new SevenSegmentDigit[2];
		for (int i = 0; i < 2; i++) {
			month[i] = new SevenSegmentDigit(context);
		}
		for (int i = 2 - 1; i >= 0; i--) {
			bottomSubLayout.addView(month[i]);
		}

		textViewDateSlash1 = new TextView(context);
		textViewDateSlash1.setTextColor(DEFAUL_COLOR);
		textViewDateSlash1.setTextSize(14);
		textViewDateSlash1.setText("/");
		bottomSubLayout.addView(textViewDateSlash1);

		day = new SevenSegmentDigit[2];
		for (int i = 0; i < 2; i++) {
			day[i] = new SevenSegmentDigit(context);
		}
		for (int i = 2 - 1; i >= 0; i--) {
			bottomSubLayout.addView(day[i]);
		}

		textViewDateSlash2 = new TextView(context);
		textViewDateSlash2.setTextColor(DEFAUL_COLOR);
		textViewDateSlash2.setTextSize(14);
		textViewDateSlash2.setText("/");
		bottomSubLayout.addView(textViewDateSlash2);

		year = new SevenSegmentDigit[2];
		for (int i = 0; i < 2; i++) {
			year[i] = new SevenSegmentDigit(context);
		}
		for (int i = 2 - 1; i >= 0; i--) {
			bottomSubLayout.addView(year[i]);
		}

		// empty view to make the floating gap between date and time
		gapView = new View(context);
		bottomSubLayout.addView(gapView);

		// time
		hour = new SevenSegmentDigit[2];
		for (int i = 0; i < 2; i++) {
			hour[i] = new SevenSegmentDigit(context);
		}
		for (int i = 2 - 1; i >= 0; i--) {
			bottomSubLayout.addView(hour[i]);
		}

		textViewTimeColon = new TextView(context);
		textViewTimeColon.setTextColor(DEFAUL_COLOR);
		textViewTimeColon.setTextSize(14);
		textViewTimeColon.setText(":");
		bottomSubLayout.addView(textViewTimeColon);

		minute = new SevenSegmentDigit[2];
		for (int i = 0; i < 2; i++) {
			minute[i] = new SevenSegmentDigit(context);
		}
		for (int i = 2 - 1; i >= 0; i--) {
			bottomSubLayout.addView(minute[i]);
		}

		// one second timer for blinking
		blinkHandler = new Handler();
		blinkHandlerTask = new Runnable() {

			@Override
			public void run() {
				long currentPost = SystemClock.uptimeMillis();
				int visibility;

				objectVisible = !objectVisible;
				if (objectVisible) {
					visibility = VISIBLE;
				} else {
					visibility = INVISIBLE;
				}

				// change visibility of objects
				// order as in the display appears
				if ((blinkingObjects & OBJ_BATTERY) == OBJ_BATTERY) {
					battery.setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_AC) == OBJ_AC) {
					textViewAC.setVisibility(visibility);
				}
				if ((blinkingObjects & MODE_T) == MODE_T) {
					textViewTMode.setVisibility(visibility);
				}
				if ((blinkingObjects & MODE_B) == MODE_B) {
					textViewBMode.setVisibility(visibility);
				}
				if ((blinkingObjects & MODE_U) == MODE_U) {
					textViewUMode.setVisibility(visibility);
				}
				if ((blinkingObjects & MODE_E) == MODE_E) {
					textViewEMode.setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_UNIT) == OBJ_UNIT) {
					textViewUnit.setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_YEAR_D1) == OBJ_YEAR_D1) {
					year[0].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_YEAR_D2) == OBJ_YEAR_D2) {
					year[1].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_MONTH_D1) == OBJ_MONTH_D1) {
					month[0].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_MONTH_D2) == OBJ_MONTH_D2) {
					month[1].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_DAY_D1) == OBJ_DAY_D1) {
					day[0].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_DAY_D2) == OBJ_DAY_D2) {
					day[1].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_HOUR_D1) == OBJ_HOUR_D1) {
					hour[0].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_HOUR_D2) == OBJ_HOUR_D2) {
					hour[1].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_TIME_COLON) == OBJ_TIME_COLON) {
					textViewTimeColon.setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_MINUTE_D1) == OBJ_MINUTE_D1) {
					minute[0].setVisibility(visibility);
				}
				if ((blinkingObjects & OBJ_MINUTE_D2) == OBJ_MINUTE_D2) {
					minute[1].setVisibility(visibility);
				}

				blinkHandler.postAtTime(blinkHandlerTask, currentPost + 1000);
			}
		};

		blinkHandler.removeCallbacks(blinkHandlerTask);
		blinkHandler.postDelayed(blinkHandlerTask, 1000);

		turnDisplayOff();
	}

	/**
	 * 
	 * @param mode
	 * @param state
	 */
	public void setMode(int mode, int state) {
		switch (mode) {
		case MODE_B:
			if (state == ON)
				textViewBMode.setVisibility(VISIBLE);
			else
				textViewBMode.setVisibility(INVISIBLE);
			break;
		case MODE_T:
			if (state == ON)
				textViewTMode.setVisibility(VISIBLE);
			else
				textViewTMode.setVisibility(INVISIBLE);
			break;
		case MODE_U:
			if (state == ON)
				textViewUMode.setVisibility(VISIBLE);
			else
				textViewUMode.setVisibility(INVISIBLE);
			break;
		}
	}

	public void setBatteryVisibility(int visibility) {
		battery.setVisibility(visibility);
	}

	/**
	 * 
	 * @param level
	 */
	public void setBatteryLevel(int level) {
		battery.setBatteryLevel(level);
	}

	/**
	 * 
	 * @param state
	 */
	public void setAC(int state) {
		if (state == ON)
			textViewAC.setVisibility(VISIBLE);
		else
			textViewAC.setVisibility(INVISIBLE);
	}

	/**
	 * 
	 * @param state
	 */
	public void setError(int state) {
		if (state == ON) {
			textViewUnit.setVisibility(INVISIBLE);
			result[DIGIT_1].setComma(false);
			textViewEMode.setVisibility(VISIBLE);
		} else {
			textViewEMode.setVisibility(INVISIBLE);
		}
	}

	/**
	 * 
	 * @param result
	 * @param comma
	 */
	public void setResult(int result) {
		// check bounds
		if (result < 0)
			result = 0;
		if (result > 999)
			result = 999;

		// set digits from smallest to biggest
		this.result[DIGIT_0].setDigit(result % 10);
		if (result > 9) {
			this.result[DIGIT_1].setDigit((result / 10) % 10);
			if (result > 99) {
				this.result[DIGIT_2].setDigit((result / 100) % 10);
			} else {
				this.result[DIGIT_2].setDigit(-1);
			}
		} else {
			if (mComma)
				this.result[DIGIT_1].setDigit(0);
			else
				this.result[DIGIT_1].setDigit(-1);
			this.result[DIGIT_2].setDigit(-1);
		}
	}

	/**
	 * @param result
	 * @param comma
	 * @param unit
	 */
	public void setResult(int result, int unit) {
		setResult(result);
		setUnit(unit);
	}

	/**
	 * 
	 * @param unit
	 */
	public void setUnit(int unit) {
		if (unit == UNIT_L) {
			textViewUnit.setText("L");
			mComma = true;
		} else if (unit == UNIT_DL) {
			textViewUnit.setText("dL");
			mComma = false;
		}
		result[DIGIT_1].setComma(mComma);
	}

	/**
	 * 
	 * @param level
	 */
	public void setProgressBarLevel(int level) {
		progressBar.setProgressLevel(level);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setDate(int year, int month, int day) {
		// check bounds
		if (day < 0)
			day = 0;
		if (day > 99)
			day = 99;

		// set digits from smallest to biggest
		this.day[DIGIT_0].setDigit(day % 10);
		if (year > -1) {
			this.day[DIGIT_1].setDigit((day / 10) % 10);
		} else {
			this.day[DIGIT_1].setDigit(0);
		}

		// check bounds
		if (month < 0)
			month = 0;
		if (month > 99)
			month = 99;

		// set digits from smallest to biggest
		this.month[DIGIT_0].setDigit(month % 10);
		if (month > 9) {
			this.month[DIGIT_1].setDigit((month / 10) % 10);
		} else {
			this.month[DIGIT_1].setDigit(0);
		}

		// check bounds
		if (year < 0)
			year = 0;
		if (year > 99)
			year = 99;

		// set digits from smallest to biggest
		this.year[DIGIT_0].setDigit(year % 10);
		if (year > 9) {
			this.year[DIGIT_1].setDigit((year / 10) % 10);
		} else {
			this.year[DIGIT_1].setDigit(0);
		}
	}

	/**
	 * 
	 * @param hour
	 * @param minute
	 */
	public void setTime(int hour, int minute) {
		// check bounds
		if (minute < 0)
			minute = 0;
		if (minute > 99)
			minute = 99;

		// set digits from smallest to biggest
		this.minute[DIGIT_0].setDigit(minute % 10);
		if (minute > 9) {
			this.minute[DIGIT_1].setDigit((minute / 10) % 10);
		} else {
			this.minute[DIGIT_1].setDigit(0);
		}

		// check bounds
		if (hour < 0)
			hour = 0;
		if (hour > 99)
			hour = 99;

		// set digits from smallest to biggest
		this.hour[DIGIT_0].setDigit(hour % 10);
		if (hour > 9) {
			this.hour[DIGIT_1].setDigit((hour / 10) % 10);
		} else {
			this.hour[DIGIT_1].setDigit(0);
		}
	}

	public void setSingleDigit(int digit, int value) {
		// check bounds 0-9
		if (value < 0)
			value = 0;
		if (value > 9)
			value = 9;

		// set digit
		if ((digit & OBJ_YEAR_D1) == OBJ_YEAR_D1) {
			year[0].setDigit(value);
		}
		if ((digit & OBJ_YEAR_D2) == OBJ_YEAR_D2) {
			year[1].setDigit(value);
		}
		if ((digit & OBJ_MONTH_D1) == OBJ_MONTH_D1) {
			month[0].setDigit(value);
		}
		if ((digit & OBJ_MONTH_D2) == OBJ_MONTH_D2) {
			month[1].setDigit(value);
		}
		if ((digit & OBJ_DAY_D1) == OBJ_DAY_D1) {
			day[0].setDigit(value);
		}
		if ((digit & OBJ_DAY_D2) == OBJ_DAY_D2) {
			day[1].setDigit(value);
		}
		if ((digit & OBJ_HOUR_D1) == OBJ_HOUR_D1) {
			hour[0].setDigit(value);
		}
		if ((digit & OBJ_HOUR_D2) == OBJ_HOUR_D2) {
			hour[1].setDigit(value);
		}
		if ((digit & OBJ_MINUTE_D1) == OBJ_MINUTE_D1) {
			minute[0].setDigit(value);
		}
		if ((digit & OBJ_MINUTE_D2) == OBJ_MINUTE_D2) {
			minute[1].setDigit(value);
		}
	}

	/**
	 * 
	 * @param color
	 */
	public void setSegmentColor(int color) {
		battery.setColor(color);
		textViewAC.setTextColor(color);
		textViewTMode.setTextColor(color);
		textViewBMode.setTextColor(color);
		textViewUMode.setTextColor(color);
		textViewEMode.setTextColor(color);
		textViewUnit.setTextColor(color);
	}

	/**
	 * 
	 * @param object
	 * @param animated
	 */
	public void animateObject(int object, int animated) {
		if (animated == ON) {
			blinkingObjects |= object;
		} else {
			blinkingObjects &= ~object;
		}
	}

	/**
	 * 
	 * @param animated
	 */
	public void progressbarAnimation(int animated) {
		progressBar.setAnimation(animated);
	}

	/**
	 * 
	 * @param visibility
	 */
	private void setVisibilityDefaultSegments(int visibility) {
		// top view
		battery.setVisibility(visibility);

		dashLines[TOP_LINE].setVisibility(visibility);

		// middle view
		result[DIGIT_0].setVisibility(visibility);
		result[DIGIT_1].setVisibility(visibility);
		result[DIGIT_2].setVisibility(visibility);
		textViewUnit.setVisibility(visibility);

		dashLines[MIDDLE_LINE].setVisibility(visibility);

		// progress bar
		progressBar.setVisibility(visibility);

		dashLines[BOTTOM_LINE].setVisibility(visibility);

		// bottom view
		day[DIGIT_0].setVisibility(visibility);
		day[DIGIT_1].setVisibility(visibility);
		textViewDateSlash1.setVisibility(visibility);
		month[DIGIT_0].setVisibility(visibility);
		month[DIGIT_1].setVisibility(visibility);
		textViewDateSlash2.setVisibility(visibility);
		year[DIGIT_0].setVisibility(visibility);
		year[DIGIT_1].setVisibility(visibility);

		hour[DIGIT_0].setVisibility(visibility);
		hour[DIGIT_1].setVisibility(visibility);
		textViewTimeColon.setVisibility(visibility);
		minute[DIGIT_0].setVisibility(visibility);
		minute[DIGIT_1].setVisibility(visibility);
	}

	/**
	 * 
	 */
	public void resetDisplay() {
		turnDisplayOff();

		setResult(0, UNIT_L);
		progressBar.setProgressLevel(ProgressBar.PROG_LEVEL_0);

		turnDisplayOn();
	}

	/**
	 * 
	 */
	public void turnDisplayOn() {
		setVisibilityDefaultSegments(VISIBLE);
	}

	/**
	 * 
	 */
	public void turnDisplayOff() {
		setVisibilityDefaultSegments(INVISIBLE);

		textViewAC.setVisibility(INVISIBLE);
		textViewTMode.setVisibility(INVISIBLE);
		textViewBMode.setVisibility(INVISIBLE);
		textViewUMode.setVisibility(INVISIBLE);
		textViewEMode.setVisibility(INVISIBLE);
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

		// return earlier if view is in edit mode
		// avoid view designer issues
		if (isInEditMode()) {
			return;
		}

		// scale battery symbol for screen size
		battery.setLayoutParams(new LayoutParams(getWidth() / 7,
				getHeight() / 12));

		// set right padding around unit symbol for screen size
		textViewUnit.setLayoutParams(new LayoutParams(getWidth() / 4,
				LayoutParams.WRAP_CONTENT));
		textViewUnit.setPadding(0, 0, getWidth() / 15, 0);

		// scale progress bar for screen size
		progressBar.setPadding(getWidth() / 7, 0, getWidth() / 7, 0);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				getHeight() / 8));

		// set height for bottom view elements ; date and time
		for (int i = 0; i < 2; i++) {
			year[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					textViewDateSlash1.getHeight()));
		}

		for (int i = 0; i < 2; i++) {
			month[i].setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, textViewDateSlash1.getHeight()));
		}
		month[1].setPadding(getWidth() / 20, 0, 0, 0);

		gapView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				textViewDateSlash1.getHeight(), 1));

		for (int i = 0; i < 2; i++) {
			day[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					textViewDateSlash1.getHeight()));
		}

		for (int i = 0; i < 2; i++) {
			hour[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					textViewDateSlash1.getHeight()));
		}

		for (int i = 0; i < 2; i++) {
			minute[i].setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, textViewDateSlash1.getHeight()));
		}
		minute[0].setPadding(0, 0, getWidth() / 10, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.LinearLayout#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureDimension(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
