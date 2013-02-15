package cn.edu.tongji.sse.glucosemeter.controler;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import cn.edu.tongji.sse.glucosemeter.GlucoseMeterActivity;
import cn.edu.tongji.sse.glucosemeter.R;
import cn.edu.tongji.sse.glucosemeter.data.DataHandler;
import cn.edu.tongji.sse.glucosemeter.data.Record;
import cn.edu.tongji.sse.glucosemeter.ui.MeterDisplay;

public class testThread extends Thread {

	private MeterDisplay display;
	private DataHandler dataHandler;

	public testThread(MeterDisplay display, DataHandler dataHandler) {
		this.display = display;
		this.dataHandler = dataHandler;
	}

	private int year = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 0;
	private int minute = 0;
	private int unit = 0;
	private MediaPlayer mediaPlayer;

	public void setAllDateTime(int year, int month, int day, int hour,
			int minute, int unit,MediaPlayer mediaPlayer) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.unit = unit;
		this.mediaPlayer = mediaPlayer;
	}

	public void run() {

		try {
			sleep(3000);
			myHandler.sendEmptyMessage(0);
			sleep(4000);
			myHandler.sendEmptyMessage(1);
			myHandler.sendEmptyMessage(3);
			myHandler.sendEmptyMessage(2);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				display.progressbarAnimation(MeterDisplay.ON);
				break;
			case 1:
				display.progressbarAnimation(MeterDisplay.OFF);
				break;
			case 2:
				Record record = dataHandler.generateRandomTestStrip(year,
						month, day, hour, minute);
				if (unit == 0) {
					display.setResult(record.getL_Record_Integer() * 10
							+ record.getL_Record_Decimal(), 1);
				} else if (unit == 1) {
					display.setResult(record.getdL_Record(), 0);
				}
				display
						.setProgressBarLevel(cn.edu.tongji.sse.glucosemeter.ui.ProgressBar.PROG_LEVEL_5);
				GlucoseMeterActivity.isTestRun = false;
				mediaPlayer.start();
				break;
			case 3:
				display.invalidate();
				break;
			default:
				break;
			}
		}
	};
}
