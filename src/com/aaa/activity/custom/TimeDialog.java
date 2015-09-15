package com.aaa.activity.custom;

import java.util.Calendar;
import java.util.Date;

import com.llw.salon.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class TimeDialog extends Dialog implements android.view.View.OnClickListener{

	private View layout;
	private Context mContext;
	private onTimeChangedListener listener;
	private Calendar c = Calendar.getInstance();
	
	private TextView hour;
	private TextView minute;
	
	public interface onTimeChangedListener{
		public void onTimeChanged(int hour, int minute);
	};
	
	public TimeDialog(Context context) {
		super(context);
	}
	
	public TimeDialog(Context context, int theme, onTimeChangedListener l, Date date){
		super(context, theme);
		mContext = context;
		listener = l;
		init(context);
	}

	private void init(Context context) {
		layout = View.inflate(context, R.layout.time_dialog, null);
		
		layout.findViewById(R.id.hour_add).setOnClickListener(this);
		layout.findViewById(R.id.hour_reduce).setOnClickListener(this);
		layout.findViewById(R.id.minute_add).setOnClickListener(this);
		layout.findViewById(R.id.minute_reduce).setOnClickListener(this);
		layout.findViewById(R.id.cancel_btn).setOnClickListener(this);
		layout.findViewById(R.id.confirm_btn).setOnClickListener(this);
		
		hour = (TextView) layout.findViewById(R.id.hour);
		minute = (TextView) layout.findViewById(R.id.minute);
		
		setContentView(layout);
		
		initDate();
		setDate();
	}

	private void initDate() {
//		c.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
//		c.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
	}

	private void setDate() {
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		
		String hour2 = null;
		if(h < 10){
			hour2 = "0" + h;
		}else{
			hour2 = "" + h;
		}
		hour.setText(hour2);
		
		String minute2 = null;
		if(m < 10){
			minute2 = "0" + m;
		}else{
			minute2 = "" + m;
		}
		minute.setText(minute2);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Dialog布局
		 */
		DisplayMetrics Metrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(Metrics);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = (int) (Metrics.widthPixels * 0.86);
		getWindow().setAttributes(params);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hour_add:
			c.add(Calendar.HOUR_OF_DAY, 1);
			setDate();
			break;
			
		case R.id.hour_reduce:
			c.add(Calendar.HOUR_OF_DAY, -1);
			setDate();
			break;
			
		case R.id.minute_add:
			c.add(Calendar.MINUTE, 1);
			setDate();
			break;
			
		case R.id.minute_reduce:
			c.add(Calendar.MINUTE, -1);
			setDate();
			break;
			
		case R.id.cancel_btn:
			cancel();
			break;
			
		case R.id.confirm_btn:
			if(listener != null)
				listener.onTimeChanged(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
			break;
		}
	}

	public String getTimeString(int h, int m) {
		String time = null;
		if(h < 10){
			time = "0" + h;
		}else{
			time = "" + h;
		}
		
		time += ":";
		
		if(m < 10){
			time += "0" + m;
		}else{
			time += m;
		}
		
		return time;
	}
}
