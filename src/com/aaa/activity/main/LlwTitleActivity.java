package com.aaa.activity.main;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.activity.BaseActivity;
import com.llw.salon.R;

public class LlwTitleActivity extends BaseActivity{

	private Button mBtnLeft;
	private Button mBtnMid;
	private Button mBtnRight;
	private RelativeLayout mLeft;
	private RelativeLayout mMid;
	private RelativeLayout mRight;
	private TextView mTitle;
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onPreOnCreate(savedInstanceState);
		
		mBtnLeft = (Button) findViewById(R.id.btn_left);
		mBtnMid = (Button) findViewById(R.id.btn_mid);
		mBtnRight = (Button) findViewById(R.id.btn_right);
		mLeft = (RelativeLayout) findViewById(R.id.rl_left);
		mMid = (RelativeLayout) findViewById(R.id.rl_mid);
		mRight = (RelativeLayout) findViewById(R.id.rl_right);
		mTitle = (TextView) findViewById(R.id.txt_title);
		
		OnClickListener left = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickLeft();
			}
		};
		
		OnClickListener mid = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickMid();
			}
		};
		
		OnClickListener right = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickRight();
			}
		};
		
		mBtnLeft.setOnClickListener(left);
		mBtnMid.setOnClickListener(mid);
		mBtnRight.setOnClickListener(right);
		
		mLeft.setOnClickListener(left);
		mMid.setOnClickListener(mid);
		mRight.setOnClickListener(right);
	}
	
	protected void clickLeft() {
		finish();
	}
	
	protected void clickMid(){
		
	}
	
	protected void clickRight(){
		
	}
	
	protected void setTitile(int id){
		mTitle.setText(id);
	}
	
	protected void setTitile(String s){
		mTitle.setText(s);
	}
	
	protected void setLeftBtn(boolean visible, int src){
		if(visible){
			mLeft.setVisibility(View.VISIBLE);
			mBtnLeft.setBackgroundResource(src);
		}else{
			mLeft.setVisibility(View.GONE);
		}
	}
	
	protected void setMidBtn(boolean visible, int src){
		if(visible){
			mMid.setVisibility(View.VISIBLE);
			mBtnMid.setBackgroundResource(src);
		}else{
			mMid.setVisibility(View.GONE);
		}
	}
	
	protected void setRightBtn(boolean visible, int src){
		if(visible){
			mRight.setVisibility(View.VISIBLE);
			mBtnRight.setBackgroundResource(src);
		}else{
			mRight.setVisibility(View.GONE);
		}
	}
}
