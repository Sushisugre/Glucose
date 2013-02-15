package cn.edu.tongji.sse.glucosemeter.controler;

import android.R.bool;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import cn.edu.tongji.sse.glucosemeter.data.DataHandler;
import cn.edu.tongji.sse.glucosemeter.data.Record;
import cn.edu.tongji.sse.glucosemeter.ui.MeterDisplay;
import cn.edu.tongji.sse.glucosemeter.ui.ProgressBar;

public class uploadThread extends Thread{
	
	private MeterDisplay display;
	private DataHandler dataHandler;
	private boolean _MODE_RUN;
	private MediaPlayer mediaPlayer;
	
	public uploadThread(MeterDisplay display,DataHandler dataHandler,boolean _MODE_RUN,MediaPlayer mediaPlayer) {
		this.display = display;
		this.dataHandler = dataHandler;
		this._MODE_RUN = _MODE_RUN;
		this.mediaPlayer = mediaPlayer;
	}
	
	public void run(){
		try {
			_MODE_RUN = true;
			myHandler.sendEmptyMessage(0);
			sleep(1000);
			myHandler.sendEmptyMessage(1);
			myHandler.sendEmptyMessage(2);
			myHandler.sendEmptyMessage(3);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			
			_MODE_RUN = false;
		}
	}
	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			
			switch (msg.what) {
			case 0:
				display.progressbarAnimation(MeterDisplay.ON);
				break;
			case 1:
				display.progressbarAnimation(MeterDisplay.OFF);
				break;
			case 2:
				dataHandler.clearRecords();
				break;
			case 3:
				display.setProgressBarLevel(ProgressBar.PROG_LEVEL_5);
				display.invalidate();
				mediaPlayer.start();
				break;
			default:
				break;
			}
		}
	};
}
