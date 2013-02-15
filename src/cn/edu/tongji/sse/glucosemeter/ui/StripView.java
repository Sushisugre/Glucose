package cn.edu.tongji.sse.glucosemeter.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.edu.tongji.sse.glucosemeter.R;

public class StripView extends ImageView{
		
	private Drawable stripDrawable;
	private DeviceListener stripListener;
	public static final String STRIPTAG="stripTAG";

	public StripView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initStripView(context, attrs);
	}
	
	public void initStripView(Context context, AttributeSet attrs)
	{
		Resources r = this.getContext().getResources();
		stripDrawable=r.getDrawable(R.drawable.strip);
		this.setTag(STRIPTAG);
	}
	
	public void setDeviceListener(DeviceListener listener)
	{
		stripListener=listener;
	}
	
	public void stripInserted()
	{
		stripListener.onStripInserted();
	}
	
	public void stripPullOut()
	{
		stripListener.onStripPullOut();
	}
	
	public boolean isInserted() {
		if (getBottom() < 235) {
			return false;
		}
		if (getLeft() < 190 ||getLeft() > 205) {
			return false;
		}
		return true;
	}

}
