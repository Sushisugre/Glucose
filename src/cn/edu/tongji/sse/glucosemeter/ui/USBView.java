package cn.edu.tongji.sse.glucosemeter.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.edu.tongji.sse.glucosemeter.R;

public class USBView extends ImageView{
	private DeviceListener usbListener;
	private Drawable usbDrawable;
	public static final String USBTAG="usbTAG";

	public USBView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initUSBView(context, attrs);
	}
	
	public void initUSBView(Context context, AttributeSet attrs)
	{
		Resources r = this.getContext().getResources();
		usbDrawable=r.getDrawable(R.drawable.usb);
		this.setTag(USBTAG);
	}
	
	public void setDeviceListener(DeviceListener listener)
	{
		usbListener=listener;
	}
	
	public void usbPlugin()
	{
		usbListener.onUSBPlugin();
	}
	
	public void usbPullOut()
	{
		usbListener.onUSBPullOut();
	}
	

}
