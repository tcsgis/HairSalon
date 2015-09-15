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
import android.widget.Toast;

public class DateDialog extends Dialog implements android.view.View.OnClickListener{

	private View layout;
	private Context mContext;
	
	private TextView mYear;
	private TextView mMonth;
	private TextView mDay;
	
	private Calendar c = Calendar.getInstance();
	private onDateChangedListener listener = null;
	private Date today = Calendar.getInstance().getTime();
	private Date date = null;
	boolean forwad = false;
	
	public interface onDateChangedListener{
		public void onDateChanged(Date date);
	};
	
	public DateDialog(Context context) {
		super(context);
		mContext = context;
		init(context);
	}
	
	public DateDialog(Context context, int theme, onDateChangedListener l, Date date, boolean forward) {
		super(context, theme);
		mContext = context;
		listener = l;
		init(context);
		this.date = date;
		this.forwad = forward;
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
	
	private void init(Context context) {
		layout = View.inflate(context, R.layout.date_dialog, null);
		
		layout.findViewById(R.id.add_day).setOnClickListener(this);
		layout.findViewById(R.id.add_month).setOnClickListener(this);
		layout.findViewById(R.id.add_year).setOnClickListener(this);
		layout.findViewById(R.id.reduce_day).setOnClickListener(this);
		layout.findViewById(R.id.reduce_month).setOnClickListener(this);
		layout.findViewById(R.id.reduce_year).setOnClickListener(this);
		layout.findViewById(R.id.cancel_btn).setOnClickListener(this);
		layout.findViewById(R.id.confirm_btn).setOnClickListener(this);
		
		mYear = (TextView) layout.findViewById(R.id.txt_year);
		mMonth = (TextView) layout.findViewById(R.id.txt_month);
		mDay = (TextView) layout.findViewById(R.id.txt_day);
		
		setContentView(layout);
		
		initDate();
		setDate();
	}

	private void initDate() {
		if(date != null){
			c.setTime(date);
		}
		setDate();
	}

	private void setDate() {
		mYear.setText(String.valueOf(c.get(Calendar.YEAR)));
		mMonth.setText(String.valueOf(c.get(Calendar.MONTH) + 1));
		mDay.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_day:
			c.add(Calendar.DAY_OF_MONTH, 1);
			setDate();
			break;
			
		case R.id.add_month:
			c.add(Calendar.MONTH, 1);
			setDate();
			break;
			
		case R.id.add_year:
			c.add(Calendar.YEAR, 1);
			setDate();
			break;
			
		case R.id.reduce_day:
			c.add(Calendar.DAY_OF_MONTH, -1);
			setDate();
			break;
			
		case R.id.reduce_month:
			c.add(Calendar.MONTH, -1);
			setDate();
			break;
			
		case R.id.reduce_year:
			c.add(Calendar.YEAR, -1);
			setDate();
			break;
			
		case R.id.cancel_btn:
			cancel();
			break;
			
		case R.id.confirm_btn:
			if(forwad){
				if(c.getTime().before(today)){
					Toast.makeText(mContext, R.string.toast_date, Toast.LENGTH_SHORT).show();
				}else{
					if(listener != null)
						listener.onDateChanged(c.getTime());
					cancel();
				}
			}else{
				if(c.getTime().after(today)){
					Toast.makeText(mContext, R.string.toast_date2, Toast.LENGTH_SHORT).show();
				}else{
					if(listener != null)
						listener.onDateChanged(c.getTime());
					cancel();
				}
			}
			break;
		}
	}

}
