package cn.edu.tongji.sse.glucosemeter.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.edu.tongji.sse.glucosemeter.R;

public class ACView extends ImageView{
	private DeviceListener ACListener;
	private Drawable ACDrawable;
	public static final String ACTAG="acTAG";

	public ACView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initACView(context, attrs);
	}
	
	public void initACView(Context context, AttributeSet attrs)
	{
		Resources r = this.getContext().getResources();
		ACDrawable=r.getDrawable(R.drawable.ac);
		this.setTag(ACTAG);
	}
	
	public void setDeviceListener(DeviceListener listener)
	{
		ACListener=listener;
	}
	
	public void acPlugin()
	{
		ACListener.onACPlugin();
	}
	
	public void acPullOut()
	{
		ACListener.onACPullOut();
	}

}
