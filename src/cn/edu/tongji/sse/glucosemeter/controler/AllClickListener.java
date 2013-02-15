package cn.edu.tongji.sse.glucosemeter.controler;


import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AllClickListener implements OnClickListener{
	
	
	private View view;
	public boolean clickFlag = false;// 记录是否已经执行过鼠标双击事件
	public int clickNum = 0; // 判断是否执行双击事件
	public AllClickListener(View view){
		super();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		clickFlag = false;
		if (clickNum == 1) {
			//Double Click
			//Log.d("double","double");
			onDoubleClick();
			 clickFlag = true;   
	            clickNum = 0;  
		}
		java.util.Timer timer = new java.util.Timer();// define a Timer
		
		timer.schedule(new java.util.TimerTask() {
			int n = 0;// Record times.

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (clickFlag) {
					// Cancel the SINGLE_CLICK event
					clickNum = 0;
					n = 0;
					this.cancel();
					return;

				}
				if (n == 1) {
					// If there are no click during the defined
					// time.->SINGLE_CLICK.
					onSingleClick();
					clickFlag = true;
					clickNum = 0;
					n = 0;
					this.cancel();
					return;
				}
				n++;
				clickNum++;

			}
		}, new java.util.Date(), 200);
	}
	
	public boolean onSingleClick(){
		return true;
	}
	public boolean onDoubleClick(){
		return true;
	}
	
	
}
