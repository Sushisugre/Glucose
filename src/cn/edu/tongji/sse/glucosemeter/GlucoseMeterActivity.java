package cn.edu.tongji.sse.glucosemeter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import cn.edu.tongji.sse.glucosemeter.controler.AllClickListener;

import cn.edu.tongji.sse.glucosemeter.controler.testThread;
import cn.edu.tongji.sse.glucosemeter.controler.uploadThread;

import cn.edu.tongji.sse.glucosemeter.data.DataHandler;
import cn.edu.tongji.sse.glucosemeter.data.Record;
import cn.edu.tongji.sse.glucosemeter.ui.ACView;
import cn.edu.tongji.sse.glucosemeter.ui.BatterySymbol;
import cn.edu.tongji.sse.glucosemeter.ui.DeviceListener;
import cn.edu.tongji.sse.glucosemeter.ui.MeterDisplay;
import cn.edu.tongji.sse.glucosemeter.ui.ProgressBar;
import cn.edu.tongji.sse.glucosemeter.ui.StripView;
import cn.edu.tongji.sse.glucosemeter.ui.USBView;

public class GlucoseMeterActivity extends Activity {

	private DataHandler dataHandler = new DataHandler();
	private MediaPlayer mediaPlayer;
	private ImageButton needleButton;
	private ImageView needleView;
	private ImageView bloodView;
	private Animation needleAnim;
	private Animation bloodAnim;

	public boolean clickFlag = false;
	public int clickNum = 0;

	/**
	 * Error Code
	 */
	private final int UPLOAD_NO_DATA = 100;
	private final int BROWSE_NO_DATA = 200;
	private final int USB_ON = 300;
	private final int STRIP_ON = 400;

	// These elements is for SetUp date and time.
	int dateFlag = 0;
	final int _YEAR_D2 = 1;
	final int _YEAR_D1 = 2;
	final int _MONTH_D2 = 3;
	final int _MONTH_D1 = 4;
	final int _DAY_D2 = 5;
	final int _DAY_D1 = 6;
	final int _HOUR_D2 = 7;
	final int _HOUR_D1 = 8;
	final int _MINUTE_D2 = 9;
	final int _MINUTE_D1 = 10;
	final int _UNIT_D = 11;
	final int _SAVE_D = 12;

	int _MODE_FLAG = 0;
	private final int _TIME_TO_CLOSE = 10000;

	private final int _MODE_INITIAL = 0;
	private final int _MODE_T = 1;
	private final int _MODE_S = 2;
	private final int _MODE_B = 3;
	private final int _MODE_U = 4;
	private final int _MODE_CLOSE = 5;
	private final int _MODE_ERROR = 6;

	private final int _BROWSE = 6;
	private final int _TOMODE = 7;
	private final int _ADD_MINUTE = 8;
	private final int _SET_UP = 9;
	private final int _ERROR = 10;

	// This is for beep
	private final int _BEEP_LONG = 1;
	private final int _BEEP_SHORT = 2;
	private final int _BEEP_SHORT_SHORT = 3;
	private final int _BEEP_SHORT_LONG = 4;

	private boolean _MODE_RUN = false;
	private java.util.Timer timer = new java.util.Timer();
	private java.util.TimerTask timerTask;

	private java.util.Timer timerForTime = new java.util.Timer();
	private java.util.TimerTask timerTaskForTime;

	// state
	private boolean isACOn = false;
	private boolean isStripOn = false;
	private boolean isUSBON = false;
	private boolean isBloodIn = false;
	public static boolean isTestRun = false;

	// Use to calculate Date and Time and
	private int year_a1 = 0;
	private int year_a2 = 0;
	private int month_a2 = 0;
	private int month_a1 = 1;
	private int day_a2 = 0;
	private int day_a1 = 1;
	private int hour_a2 = 0;
	private int hour_a1 = 0;
	private int minute_a2 = 0;
	private int minute_a1 = 0;

	private int unit_flag = 0;// 0 is L ,1 is DL
	private int year = 0;
	private int month = 1;
	private int day = 1;
	private int hour = 0;
	private int minute = 0;

	// for browse
	public int resultFlag = 0;

	/**
	 * Define different MediaPlayer
	 */
	private MediaPlayer short_short_player;
	private MediaPlayer short_player;
	private MediaPlayer long_player;
	private MediaPlayer short_long_Player;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialAllBeep();
		registerUSB();
		insertTestStrip();
		setAC();
		needleRegiste();
		toMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.meter_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.meter_exit:
			finish();
			return true;
		case R.id.meter_reset:
			// TODO reset meter
			reset();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Define a TimerTask
	 */
	public void initialTask() {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(_MODE_CLOSE);
				timerTaskForTime.cancel();
				initialDateTimeTask();
			}
		};
		timer.schedule(timerTask, _TIME_TO_CLOSE);
	}

	/**
	 * Define DateTime Task Every 60seconds,Minute++
	 */
	public void initialDateTimeTask() {
		timerTaskForTime = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(_ADD_MINUTE);

			}
		};
		timerForTime.schedule(timerTaskForTime, 30000, 60000);
	}

	/**
	 * key: 1 = TestMode; 2 = setUp; 3 = Browse. 4 = Upload 5 = Close
	 */
	public void toMode() {
		int key = _MODE_FLAG;
		switch (key) {
		case 0:
			initial();
			break;
		case 1:
			test();
			break;
		case 2:
			setUp();
			break;
		case 3:
			browse();
			break;
		case 4:
			upload();
			break;
		case 5:
			close();
			break;
		case 6:
			error();
			break;
		default:
			break;
		}
	}

	/**
	 * Error
	 */
	public void error() {

		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		setAllModeNoBink();
		display.turnDisplayOn();
		display.setError(MeterDisplay.ON);
		display.setMode(MeterDisplay.MODE_E, MeterDisplay.ON);
		display.animateObject(MeterDisplay.MODE_E, MeterDisplay.ON);
		beep(_BEEP_SHORT_LONG);

		// timerTask.cancel();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		initialTask();

	}

	/**
	 * Open Device first Time
	 */
	public void initial() {

		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		setAllModeNoBink();
		display.turnDisplayOff();
		display.setDate(year, month, day);
		display.setBatteryLevel(BatterySymbol.BAT_LEVEL_2);
		display.setTime(hour, minute);
		initialDateTimeTask();
		display.setUnit(MeterDisplay.UNIT_L);
		display.setResult(0, 0);
		// initialTask();
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		button.setOnClickListener(new AllClickListener(button) {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
			}

			@Override
			public boolean onDoubleClick() {
				// TODO Auto-generated method stub
				MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);

				display.turnDisplayOn();

				if (_MODE_FLAG == 0 || _MODE_FLAG == 5) {
					_MODE_FLAG = _MODE_S;
					toMode();
				} else {
					toMode();
				}
				return super.onDoubleClick();
			}

			@Override
			public boolean onSingleClick() {
				// TODO Auto-generated method stub

				return super.onSingleClick();
			}

		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				_MODE_FLAG = _MODE_B;
				toMode();
				return false;
			}
		});

	}

	/**
	 * To do the Test
	 */
	public void test() {

		beep(_BEEP_SHORT);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		setAllModeNoBink();
		setNowTime();
		display.turnDisplayOn();
		display.setResult(0, 0);

		if (unit_flag == 0) {
			display.setUnit(MeterDisplay.UNIT_L);
		} else if (unit_flag == 1) {
			display.setUnit(MeterDisplay.UNIT_DL);
		}
		// timerTask.cancel();
		initialTask();

		display.setMode(MeterDisplay.MODE_T, MeterDisplay.ON);

		needleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (_MODE_FLAG == _MODE_T) {
					timerTask.cancel();
					initialTask();
					if (!isBloodIn && isStripOn) {
						needleView.startAnimation(needleAnim);
						bloodView.startAnimation(bloodAnim);
						bloodView.setVisibility(View.VISIBLE);
						isBloodIn = true;

						MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
						testThread thread = new testThread(display, dataHandler);
						thread.setAllDateTime(year, month, day, hour, minute,
								unit_flag, short_player);
						isTestRun = true;
						thread.start();
						
						timerTask.cancel();
						initialTask();
					}
				}
			}
		});

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timerTask.cancel();
				initialTask();
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				/*timerTask.cancel();
				initialTask();*/
				timerTask.cancel();
				myHandler.sendEmptyMessage(_MODE_CLOSE);
				return false;
			}
		});

	}

	/**
	 * To do setUp
	 */
	public void setUp() {
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);

		if (isUSBON) {
			display.setResult(USB_ON);
			_MODE_FLAG = _MODE_ERROR;
			toMode();
		} else if (isStripOn) {
			display.setResult(STRIP_ON);
			_MODE_FLAG = _MODE_ERROR;
			toMode();
		} else {
			// Initial the setUp display.
			beep(_BEEP_SHORT);
			setAllModeNoBink();
			dateFlag = 11;
			setNowTime();
			initialTask();

		}
		button.setOnClickListener(new AllClickListener(button) {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
			}

			@Override
			public boolean onDoubleClick() {
				// TODO Auto-generated method stub

				++dateFlag;
				timerTask.cancel();
				initialTask();
				setBinkDate(dateFlag);
				Log.e("double", "double click");
				return super.onDoubleClick();
			}

			@Override
			public boolean onSingleClick() {
				// TODO Auto-generated method stub
				timerTask.cancel();
				initialTask();
				myHandler.sendEmptyMessage(_SET_UP);
				Log.e("short", "short click");
				return super.onSingleClick();
			}

		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				timerTask.cancel();
				initialTask();
				return false;
			}
		});

	}

	/**
	 * To do Upload
	 */
	public void upload() {

		// beep(_BEEP_SHORT);
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		display.setResult(0, 0);
		display.turnDisplayOn();
		setNowTime();
		setAllModeNoBink();
		display.setMode(MeterDisplay.MODE_U, MeterDisplay.ON);
		initialTask();

		button.setOnClickListener(new OnClickListener() {
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Thread thread = new uploadThread(display, dataHandler,
						_MODE_RUN, short_player);
				thread.start();
				timerTask.cancel();
				initialTask();
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				timerTask.cancel();
				/*_MODE_FLAG = _MODE_CLOSE;
				toMode();*/
				myHandler.sendEmptyMessage(_MODE_CLOSE);
				return false;
			}
		});

	}

	/**
	 * To do Browse
	 */
	public void browse() {

		/**
		 * For different Error
		 */
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		if (isUSBON) {
			display.setResult(USB_ON);
			_MODE_FLAG = _MODE_ERROR;
			toMode();
		} else if (isStripOn) {
			display.setResult(STRIP_ON);
			_MODE_FLAG = _MODE_ERROR;
			toMode();
		} else {
			setAllModeNoBink();
			display.turnDisplayOn();
			
			display.setMode(MeterDisplay.MODE_B, MeterDisplay.ON);

			ArrayList<Record> list = (ArrayList<Record>) dataHandler
					.getRecordQueue();
			resultFlag = 0;
			if (list.size() != 0 && unit_flag == 0) {
				display.setResult(list.get(resultFlag).getL_Record_Integer()
						* 10 + list.get(resultFlag).getL_Record_Decimal(), 1);
				beep(_BEEP_SHORT);
				initialTask();
			} else if (list.size() != 0 && unit_flag == 1) {

				display.setResult(list.get(resultFlag).getdL_Record(), 0);
				beep(_BEEP_SHORT);
				initialTask();
			} else if (list.size() == 0) {
				display.setMode(MeterDisplay.MODE_B, MeterDisplay.OFF);
				display.setResult(BROWSE_NO_DATA);
				_MODE_FLAG = _MODE_ERROR;
				toMode();
			}

		}

		button.setOnClickListener(new AllClickListener(button) {

			ArrayList<Record> list = (ArrayList<Record>) dataHandler
					.getRecordQueue();
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
			}

			@Override
			public boolean onDoubleClick() {
				// TODO Auto-generated method stub

				if (list.size() != 0) {
					if (resultFlag != 0) {
						--resultFlag;
						if (_MODE_FLAG != _MODE_ERROR) {
							timerTask.cancel();
							initialTask();
							myHandler.sendEmptyMessage(_BROWSE);
						}
						
						Log.e("double", "double click");
					}
				}

				return super.onDoubleClick();
			}

			@Override
			public boolean onSingleClick() {
				// TODO Auto-generated method stub
				if (list.size() != 0 && _MODE_FLAG == _MODE_B) {
					if (resultFlag < list.size() - 1) {
						++resultFlag;
						if (_MODE_FLAG != _MODE_ERROR) {
							timerTask.cancel();
							initialTask();
							myHandler.sendEmptyMessage(_BROWSE);
						}
						
						Log.e("short", "short click");
					}
				}

				return super.onSingleClick();
			}

		});

		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (_MODE_FLAG != _MODE_ERROR) {
					timerTask.cancel();
					myHandler.sendEmptyMessage(_MODE_CLOSE);
				}
				return false;
			}
		});

	}

	/**
	 * Close Device
	 */
	public void close() {

		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		setAllModeNoBink();
		display.turnDisplayOff();
		if (isACOn) {
			display.setBatteryVisibility(MeterDisplay.VISIBLE);
		}
		display.setResult(0, 1);
		ImageButton button = (ImageButton) findViewById(R.id.buttonView);
		button.setOnClickListener(new AllClickListener(button) {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
			}

			@Override
			public boolean onDoubleClick() {
				// TODO Auto-generated method stub
				MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
				display.turnDisplayOn();
				if (_MODE_FLAG == 0 || _MODE_FLAG == 5) {
					_MODE_FLAG = _MODE_S;
					toMode();
				} else {
					toMode();
				}
				return super.onDoubleClick();
			}

			@Override
			public boolean onSingleClick() {
				// TODO Auto-generated method stub
				return super.onSingleClick();
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				_MODE_FLAG = _MODE_B;
				toMode();
				return false;
			}
		});
	}

	/**
	 * SetUp All Time one by one
	 * 
	 * @param dateFlag
	 */
	public void setBinkDate(int dateFlag) {

		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);

		switch (dateFlag) {

		case 1:
			display.animateObject(MeterDisplay.OBJ_UNIT, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_YEAR_D2, MeterDisplay.ON);
			display.turnDisplayOn();
			break;
		case 2:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_YEAR_D2, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_YEAR_D1, MeterDisplay.ON);
			break;
		case 3:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_YEAR_D1, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_MONTH_D2, MeterDisplay.ON);
			break;
		case 4:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_MONTH_D2, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_MONTH_D1, MeterDisplay.ON);
			break;
		case 5:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_MONTH_D1, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_DAY_D2, MeterDisplay.ON);
			break;
		case 6:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_DAY_D2, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_DAY_D1, MeterDisplay.ON);
			break;
		case 7:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_DAY_D1, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_HOUR_D2, MeterDisplay.ON);
			break;
		case 8:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_HOUR_D2, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_HOUR_D1, MeterDisplay.ON);
			break;
		case 9:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_HOUR_D1, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_MINUTE_D2, MeterDisplay.ON);
			break;
		case 10:
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_MINUTE_D2, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_MINUTE_D1, MeterDisplay.ON);
			break;
		case 11:
			display.animateObject(MeterDisplay.OBJ_MINUTE_D1, MeterDisplay.OFF);
			display.turnDisplayOn();
			//beep(_BEEP_SHORT);
			timerTask.cancel();
			myHandler.sendEmptyMessage(_MODE_CLOSE);
			break;
		case 12:
			timerTaskForTime.cancel();
			display.turnDisplayOn();
			display.animateObject(MeterDisplay.OBJ_MINUTE_D1, MeterDisplay.OFF);
			display.animateObject(MeterDisplay.OBJ_UNIT, MeterDisplay.ON);
			this.dateFlag = 0;
			break;
		case 13:
			display.animateObject(MeterDisplay.OBJ_UNIT, MeterDisplay.OFF);
			//display.turnDisplayOn();
			//
			//setBinkDate(dateFlag);
			/*
			 * initialDateTimeTask(); timerTask.cancel(); _MODE_FLAG =
			 * _MODE_CLOSE; toMode();
			 */
			break;
		default:
			break;
		}
	}

	/**
	 * SetUp Mode to change Elements
	 */
	public void addDateTime() {
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		switch (dateFlag) {
		case 1:
			if (year_a2 < 9) {
				++year_a2;
			} else {
				year_a2 = 0;
			}
			display.setDate(year_a2 * 10 + year_a1, month, day);

			break;
		case 2:
			if (year_a1 < 9) {
				++year_a1;
			} else {
				year_a1 = 0;
			}
			display.setDate(year_a2 * 10 + year_a1, month, day);
			year = year_a2 * 10 + year_a1;
			break;
		case 3:
			if (month_a2 == 1) {
				month_a2 = 0;
			} else if (month_a2 == 0) {
				month_a2 = 1;
			}
			display.setDate(year, month_a2 * 10 + month_a1, day);
			break;
		case 4:
			if (month_a2 == 1) {
				if (month_a1 < 2) {
					++month_a1;
				} else {
					month_a1 = 0;
				}
			} else if (month_a2 == 0) {
				if (month_a1 < 9) {
					++month_a1;
				} else {
					month_a1 = 0;
				}
			}
			display.setDate(year, month_a2 * 10 + month_a1, day);
			month = month_a2 * 10 + month_a1;
			break;
		case 5:
			if (day_a2 < 3) {
				++day_a2;
			} else {
				day_a2 = 0;
			}
			display.setDate(year, month, day_a2 * 10 + day_a1);
			break;
		case 6:
			if (day_a2 == 3) {
				if (day_a1 < 1) {
					++day_a1;
				} else {
					day_a1 = 0;
				}
			} else {
				if (day_a1 < 9) {
					++day_a1;
				} else {
					day_a1 = 0;
				}
			}
			display.setDate(year, month, day_a2 * 10 + day_a1);
			day = day_a2 * 10 + day_a1;
			break;
		case 7:
			if (hour_a2 < 2) {
				++hour_a2;
			} else {
				hour_a2 = 0;
			}
			display.setTime(hour_a2 * 10 + hour_a1, minute);
			break;
		case 8:
			if (hour_a2 == 2) {
				if (hour_a1 < 4) {
					++hour_a1;
				} else {
					hour_a1 = 0;
				}
			} else {
				if (hour_a1 < 9) {
					++hour_a1;
				} else {
					hour_a1 = 0;
				}
			}
			display.setTime(hour_a2 * 10 + hour_a1, minute);
			hour = hour_a2 * 10 + hour_a1;
			break;
		case 9:
			if (minute_a2 < 6) {
				++minute_a2;
			} else {
				minute_a2 = 0;
			}
			display.setTime(hour, minute_a2 * 10 + minute_a1);

			break;
		case 10:
			if (minute_a2 != 6) {
				if (minute_a1 < 9) {
					++minute_a1;
				} else {
					minute_a2 = 0;
				}
			} else {
				minute_a2 = 0;
			}
			display.setTime(hour, minute_a2 * 10 + minute_a1);
			minute = minute_a2 * 10 + minute_a1;

			break;
		case 11:
			
			break;
		case 0:
			if (unit_flag == 0) {
				display.setUnit(MeterDisplay.UNIT_DL);
				unit_flag = 1;
			} else if (unit_flag == 1) {
				display.setUnit(MeterDisplay.UNIT_L);
				unit_flag = 0;
			}
			break;


		default:
			break;
		}
	}

	/**
	 * Set All Mode NO Bink
	 */
	public void setAllModeNoBink() {

		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		display.animateObject(MeterDisplay.MODE_B, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.MODE_E, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.MODE_U, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.MODE_T, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_YEAR, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_MONTH, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_DAY, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_HOUR, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_MINUTE, MeterDisplay.OFF);
		display.animateObject(MeterDisplay.OBJ_UNIT, MeterDisplay.OFF);
		display.setProgressBarLevel(ProgressBar.PROG_LEVEL_0);
		display.turnDisplayOn();

	}

	/**
	 * Set AC
	 */
	public void setAC() {

		ACView acView = (ACView) findViewById(R.id.acView);
		acView.setOnTouchListener(new DeviceListener());
		acView.setDeviceListener(new DeviceListener() {
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);

			@Override
			public boolean onACPlugin() {

				// TODO Auto-generated method stub
				display.animateObject(MeterDisplay.OBJ_AC, MeterDisplay.ON);
				display.setBatteryVisibility(View.VISIBLE);
				isACOn = true;
				// display.animateObject(display.OBJ_BATTERY, display.ON);
				if (_MODE_FLAG == 0 || _MODE_FLAG == 5) {
					display.turnDisplayOff();
					display.setBatteryVisibility(View.VISIBLE);
				} else {

				}

				return super.onACPlugin();
			}

			@Override
			public boolean onACPullOut() {

				// TODO Auto-generated method stub
				display.animateObject(MeterDisplay.OBJ_AC, MeterDisplay.OFF);
				isACOn = false;
				// display.animateObject(display.OBJ_BATTERY, display.OFF);
				if (_MODE_FLAG == 0 || _MODE_FLAG == 5) {
					display.turnDisplayOff();
				} else {

				}

				return super.onACPullOut();
			}
		});
	}

	/**
	 * Insert Test Strip
	 */
	public void insertTestStrip() {

		StripView stripView1 = (StripView) findViewById(R.id.stripView1);
		stripView1.setOnTouchListener(new DeviceListener());
		stripView1.setDeviceListener(new DeviceListener() {
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
			@Override
			public boolean onStripInserted() {
				// TODO Auto-generated method stub
				isStripOn = true;
				if (_MODE_FLAG == _MODE_INITIAL || _MODE_FLAG == _MODE_CLOSE) {
					if (isUSBON) {
						display.setResult(USB_ON);
						_MODE_FLAG = _MODE_ERROR;
						toMode();
					} else {
						_MODE_FLAG = _MODE_T;
						toMode();
					}
				} else if (_MODE_FLAG == _MODE_T) {

				} else {
					// beep
				}
				return super.onStripInserted();
			}

			@Override
			public boolean onStripPullOut() {
				// TODO Auto-generated method stub
				isStripOn = false;
				bloodView.setVisibility(View.INVISIBLE);
				isBloodIn = false;
				if (isTestRun) {
					// To Error
					// _MODE_FLAG = _MODE_ERROR;
					// myHandler.sendEmptyMessage(_ERROR);
					timerTask.cancel();
					myHandler.sendEmptyMessage(_MODE_CLOSE);

				} else if (!isTestRun && _MODE_FLAG == _MODE_T) {
					// Close Device
					timerTask.cancel();
					myHandler.sendEmptyMessage(_MODE_CLOSE);
				}

				return super.onStripPullOut();
			}

		});

		StripView stripView2 = (StripView) findViewById(R.id.stripView2);
		stripView2.setOnTouchListener(new DeviceListener());
		stripView2.setDeviceListener(new DeviceListener() {
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
			@Override
			public boolean onStripInserted() {
				// TODO Auto-generated method stub
				isStripOn = true;
				if (_MODE_FLAG == _MODE_INITIAL || _MODE_FLAG == _MODE_CLOSE) {
					if (isUSBON) {
						display.setResult(USB_ON);
						_MODE_FLAG = _MODE_ERROR;
						toMode();
					} else {
						_MODE_FLAG = _MODE_T;
						toMode();
					}
				} else if (_MODE_FLAG == _MODE_T) {

				} else {
					// beep
				}
				return super.onStripInserted();
			}

			@Override
			public boolean onStripPullOut() {
				// TODO Auto-generated method stub
				isStripOn = false;
				bloodView.setVisibility(View.INVISIBLE);
				isBloodIn = false;
				if (isTestRun) {
					// To Error
					// _MODE_FLAG = _MODE_ERROR;
					// myHandler.sendEmptyMessage(_ERROR);
					timerTask.cancel();
					myHandler.sendEmptyMessage(_MODE_CLOSE);

				} else if (!isTestRun && _MODE_FLAG == _MODE_T) {
					// Close Device
					timerTask.cancel();
					//_MODE_FLAG = _MODE_CLOSE;
					//toMode();
					myHandler.sendEmptyMessage(_MODE_CLOSE);
				}

				return super.onStripPullOut();
			}
		});
	}

	/**
	 * Register USB
	 */
	public void registerUSB() {

		USBView usbView = (USBView) findViewById(R.id.usbView);
		usbView.setOnTouchListener(new DeviceListener());
		usbView.setDeviceListener(new DeviceListener() {
			@Override
			public boolean onUSBPlugin() {
				// TODO Auto-generated method stub
				// beep(_BEEP_SHORT);
				isUSBON = true;
				MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
				if (_MODE_FLAG == _MODE_INITIAL || _MODE_FLAG == _MODE_CLOSE) {
					if (dataHandler.getRecordQueue().size() != 0) {

						if (_MODE_FLAG == _MODE_CLOSE
								|| _MODE_FLAG == _MODE_INITIAL) {
							beep(_BEEP_SHORT);
						}
						if (isStripOn) {
							display.setResult(STRIP_ON);
							_MODE_FLAG = _MODE_ERROR;
							toMode();
						} else {
							_MODE_FLAG = _MODE_U;
							toMode();
						}
					} else {
						// To Error
						_MODE_FLAG = _MODE_ERROR;
						display.setResult(UPLOAD_NO_DATA, 0);
						toMode();
					}
				} else if (_MODE_FLAG == _MODE_ERROR) {

				} else {
					timerTask.cancel();
					initialTask();
				}

				return super.onUSBPlugin();
			}

			@Override
			public boolean onUSBPullOut() {

				isUSBON = false;
				// TODO Auto-generated method stub
				if (_MODE_FLAG == _MODE_U && _MODE_RUN == true) {
					// Invalid put out
					timerTask.cancel();
					_MODE_FLAG = _MODE_CLOSE;
					toMode();
				} else if (_MODE_FLAG == _MODE_U && _MODE_RUN == false) {
					// Valid put out
					timerTask.cancel();
					//_MODE_FLAG = _MODE_CLOSE;
					//toMode();
					myHandler.sendEmptyMessage(_MODE_CLOSE);
				}

				return super.onUSBPullOut();
			}
		});

	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
			switch (msg.what) {
			case _MODE_CLOSE:
				if (_MODE_FLAG == _MODE_ERROR) {
					display.setError(MeterDisplay.OFF);
				}
				_MODE_FLAG = _MODE_CLOSE;
				beep(_BEEP_LONG);
				toMode();
				break;
			case _BROWSE:
				ArrayList<Record> list = (ArrayList<Record>) dataHandler
						.getRecordQueue();
				Record record = list.get(resultFlag);
				if (unit_flag == 0) {
					display.setResult(record.getL_Record_Integer() * 10
							+ record.getL_Record_Decimal(), 1);

				} else if (unit_flag == 1) {
					display.setResult(record.getdL_Record(), 0);
				}
				display.setDate(record.getYear(), record.getMonth(), record
						.getDay());
				display.setTime(record.getHour(), record.getMinute());
				break;
			case _TOMODE:
				toMode();
				break;
			case _ADD_MINUTE:
				if (_MODE_FLAG != _MODE_B) {
					display.setTime(hour, ++minute);
				} else {
					++minute;
				}
				break;
			case _SET_UP:
				timerTask.cancel();
				initialTask();
				addDateTime();
				break;
			case _ERROR:
				
				//error();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Timer Task
	 */
	public void timerTask() {
		timerTask.cancel();
		initialTask();
	}

	/**
	 * Initial Beep
	 */
	public void initialAllBeep() {
		short_player = MediaPlayer.create(this, R.raw.beep_short);
		long_player = MediaPlayer.create(this, R.raw.beep_long);
		short_short_player = MediaPlayer.create(this, R.raw.beep_short_short);
		short_long_Player = MediaPlayer.create(this, R.raw.beep_short_long);
	}

	/**
	 * Beep flag: for different kinds of beep
	 */
	public void beep(int flag) {
		switch (flag) {
		case _BEEP_LONG:
			mediaPlayer = MediaPlayer.create(this, R.raw.beep_long);
			break;
		case _BEEP_SHORT:
			mediaPlayer = MediaPlayer.create(this, R.raw.beep_short);
			break;
		case _BEEP_SHORT_SHORT:
			mediaPlayer = MediaPlayer.create(this, R.raw.beep_short_short);
			break;
		case _BEEP_SHORT_LONG:
			mediaPlayer = MediaPlayer.create(this, R.raw.beep_short_long);
			break;
		default:
			break;
		}

		mediaPlayer.setLooping(false);
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}
	}

	/**
	 * Initial all beep
	 */
	public void initialBeep() {

	}

	/**
	 * Set Now Time
	 */
	public void setNowTime() {
		MeterDisplay display = (MeterDisplay) findViewById(R.id.screenView);
		display.setTime(hour, minute);
		display.setDate(year, month, day);
	}

	/**
	 * Reset Device
	 */
	public void reset() {

		dataHandler.clearRecords();
		dateFlag = 0;
		resultFlag = 0;
		unit_flag = 0;
		year = 0;
		month = 1;
		day = 1;
		hour = 0;
		minute = 0;
		if (_MODE_FLAG != _MODE_INITIAL&&_MODE_FLAG!=_MODE_CLOSE) {
			timerTask.cancel();
		}
		_MODE_FLAG = 0;
		timerForTime.cancel();
		myHandler.sendEmptyMessage(_MODE_CLOSE);

	}

	/**
	 * Needle Animation
	 */
	public void needleRegiste() {
		needleButton = (ImageButton) findViewById(R.id.needleButton);
		needleView = (ImageView) findViewById(R.id.needleView);
		bloodView = (ImageView) findViewById(R.id.blood);
		needleAnim = AnimationUtils.loadAnimation(this, R.anim.needle_anim);
		bloodAnim = AnimationUtils.loadAnimation(this, R.anim.blood_anim);

	}

}