package com.changhong.activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.llw.salon.R;

public class PopupFoot extends LinearLayout {
	private LayoutInflater inflater;
	private TextView pop_time_text;
	public PopupFoot(Context context){
		this(context,null);
		inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.popup_foot, this, true);
		pop_time_text = (TextView) findViewById(R.id.text_history);
	}
	public PopupFoot(Context context,AttributeSet attrs){
		super(context,attrs);
	}	
	public void setTimeText(String text){
		pop_time_text.setText(text);
	}
}  

