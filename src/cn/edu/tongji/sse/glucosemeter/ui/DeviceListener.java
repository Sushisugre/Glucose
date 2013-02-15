package cn.edu.tongji.sse.glucosemeter.ui;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DeviceListener implements OnTouchListener {

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	static final int PADDING_BOTTON_STRIP = 40;
	static final int UPPER_BOUND_CABLE = 725;
	static final int LOWER_BOUND_CABLE = 800;
	int mode = NONE;

	boolean usbPlugIn;
	boolean acPlugIn;
	boolean stripPlugIn;

	public DeviceListener() {
		super();
		usbPlugIn = false;
		acPlugIn = false;
		stripPlugIn = false;
		// newPaddingB_strip
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int _x = (int) event.getRawX();
		int _y = (int) event.getRawY();
		int _view_left = v.getLeft();
		int[] _temp = new int[] { 0, 0 };
		int _newLeft = _x - _temp[0] - 30;
		int _newTop = _y - _temp[1] - 100;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			_temp[0] = (int) event.getX();// first point?
			_temp[1] = (int) event.getY();
			// _temp[1] = _y - _view_top;??????????
			if (v.getTag() == StripView.STRIPTAG) {
				v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
						v.getPaddingRight(), PADDING_BOTTON_STRIP);
			}

			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (v.getTag().equals(StripView.STRIPTAG)) {
				StripView _strip = (StripView) v;
				/**
				 * = =|||
				 */

				if (stripPlugIn) {
					/**
					 * after inserting,not allow moving to left or right
					 */
					if (_newTop + v.getHeight() > 235) {
						v.layout(_newLeft, 235 - v.getHeight(),
								_newLeft + v.getWidth(), 235);
					} else {
						v.layout(_view_left, _newTop,
								_view_left + v.getWidth(),
								_newTop + v.getHeight());
					}

				} else {
					if (_newTop + v.getHeight() > 235) {
						v.layout(_newLeft, 235 - v.getHeight(),
								_newLeft + v.getWidth(), 235);
					} else {
						v.layout(_newLeft, _newTop, _newLeft + v.getWidth(),
								_newTop + v.getHeight());
					}
					// Log.d("Strip","---Button:"+v.getBottom()+" Left:"+_view_left);
				}

				if (_strip.isInserted()) {
					setIn(v);
				} else {
					setOut(v);
				}
			} else // USB OR AC
			{
				if ((_newTop + 100) <= UPPER_BOUND_CABLE) {
					v.layout(_view_left, UPPER_BOUND_CABLE,
							_view_left + v.getWidth(),
							UPPER_BOUND_CABLE + v.getHeight());
					setIn(v);
				} else if ((_newTop + 100) >= LOWER_BOUND_CABLE) {
					v.layout(_view_left, LOWER_BOUND_CABLE,
							_view_left + v.getWidth(),
							LOWER_BOUND_CABLE + v.getHeight());
					setOut(v);
				} else {
					v.layout(_view_left, _newTop + 100,
							_view_left + v.getWidth(),
							_newTop + 100 + v.getHeight());
					setOut(v);
				}

			}
			v.postInvalidate();
			break;
		}
		return true;
	}

	private void setIn(View v) {
		if (v.getTag().equals(USBView.USBTAG)) {
			if (usbPlugIn == false) {
				USBView usb = (USBView) v;
				usb.usbPlugin();
				usbPlugIn = true;
			}
		} else if (v.getTag().equals(ACView.ACTAG)) {
			if (acPlugIn == false) {
				ACView ac = (ACView) v;
				ac.acPlugin();
				acPlugIn = true;
			}
		} else if (v.getTag().equals(StripView.STRIPTAG)) {
			if (stripPlugIn == false) {
				StripView strip = (StripView) v;
				strip.stripInserted();
				stripPlugIn = true;
			}
		}
	}

	private void setOut(View v) {
		if (v.getTag().equals(USBView.USBTAG)) {
			if (usbPlugIn == true) {
				USBView usb = (USBView) v;
				usb.usbPullOut();
				usbPlugIn = false;
			}
		} else if (v.getTag().equals(ACView.ACTAG)) {
			if (acPlugIn == true) {
				ACView ac = (ACView) v;
				ac.acPullOut();
				acPlugIn = false;
			}
		} else if (v.getTag().equals(StripView.STRIPTAG)) {
			if (stripPlugIn == true) {
				StripView strip = (StripView) v;
				strip.stripPullOut();
				stripPlugIn = false;
			}
		}
	}

	public boolean onUSBPlugin() {
		// Log.i("OnUSBPlugin","OnUSBPlugin");
		return true;
	}

	public boolean onUSBPullOut() {
		return true;
	}

	public boolean onACPlugin() {
		return true;
	}

	public boolean onACPullOut() {
		return true;
	}

	public boolean onStripInserted() {
		return true;
	}

	public boolean onStripPullOut() {
		return true;
	}

}
