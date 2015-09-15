package com.aaa.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberRegisterActivity;
import com.aaa.activity.custom.CustomRegisterActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.main.MainActivity;
import com.aaa.activity.salon.SalonRegisterActivity;
import com.aaa.util.Role;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.config.CHIConfig;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class RegisterActivity extends LlwTitleActivity implements OnClickListener{

	@CHInjectView(id = R.id.custom_point)
	private ImageView customPoint;
	@CHInjectView(id = R.id.salon_point)
	private ImageView salonPoint;
	@CHInjectView(id = R.id.barber_point)
	private ImageView barberPoint;
	
	@CHInjectView(id = R.id.phone)
	private TextView phone;
	@CHInjectView(id = R.id.mima)
	private TextView mima;
	@CHInjectView(id = R.id.mima2)
	private TextView mima2;
	@CHInjectView(id = R.id.yanzhengma)
	private TextView yanzhengma;
	@CHInjectView(id = R.id.set_mima)
	private TextView set_mima;
	@CHInjectView(id = R.id.set_mima2)
	private TextView set_mima2;
	
	@CHInjectView(id = R.id.btn_yanzheng)
	private Button btnVerify;
	@CHInjectView(id = R.id.done)
	private Button btnNext;
	
	public static final int REGISTER = 0;//注册
	public static final int RESET = 1;//重置密码
	public static final int CHANGE_SECRET = 2;
	
	private final int TIME = 60;
	
	private byte selection = Role.UNDIFINED;
	private int applyType = -1;
	private boolean getVerify = false;
	private MyHandler handler = new MyHandler();
	private int time = 0;
	private String username;
	private String pwd;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		btnNext.setOnClickListener(this);
		btnVerify.setOnClickListener(this);
		
		applyType = getIntent().getIntExtra("applyType", -1);
		CHLogger.d(this, "applyType " + applyType);
		
		if(applyType == REGISTER){
			setTitile(R.string.login_string7);
			findViewById(R.id.custom).setOnClickListener(this);
			findViewById(R.id.salon).setOnClickListener(this);
			findViewById(R.id.barber).setOnClickListener(this);
		}
		else if(applyType == RESET){
			setTitile(R.string.login_string13);
			btnNext.setText(R.string.login_string12);
			set_mima.setText(R.string.login_string16);
			findViewById(R.id.view1).setVisibility(View.GONE);
			findViewById(R.id.view2).setVisibility(View.GONE);
		}
		else if(applyType == CHANGE_SECRET){
			setTitile(R.string.login_string22);
			btnNext.setText(R.string.login_string12);
			set_mima.setText(R.string.login_string23);
			set_mima2.setText(R.string.login_string16);
			findViewById(R.id.view1).setVisibility(View.GONE);
			findViewById(R.id.view2).setVisibility(View.GONE);
			findViewById(R.id.view3).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custom:
			selection = Role.CUSTOM;
			customPoint.setImageResource(R.drawable.online);
			salonPoint.setImageResource(R.drawable.unonline);
			barberPoint.setImageResource(R.drawable.unonline);
			break;

		case R.id.salon:
			selection = Role.SALON;
			salonPoint.setImageResource(R.drawable.online);
			customPoint.setImageResource(R.drawable.unonline);
			barberPoint.setImageResource(R.drawable.unonline);
			break;
		
		case R.id.barber:
			selection = Role.BARBER;
			barberPoint.setImageResource(R.drawable.online);
			salonPoint.setImageResource(R.drawable.unonline);
			customPoint.setImageResource(R.drawable.unonline);
			break;
			
		case R.id.btn_yanzheng:
			if(applyType == REGISTER){
				if(validSelection() && validPhone() && validSecret()){
					doGetVerify();
				}
			}
			if(applyType == RESET){
				if(validPhone() && validSecret()){
					doGetVerify();
				}
			}
			break;
			
		case R.id.done:
			if(applyType == REGISTER){
				if(validSelection() && validPhone() && validSecret() && getVerify && validYanzhengma()){
					doVerity();
				}
			}
			if(applyType == RESET){
				if(validPhone() && validSecret() && getVerify && validYanzhengma()){
					doResetSecret();
				}
			}
			if(applyType == CHANGE_SECRET){
				if(validSecret()){
					doVerifyOldSecret();
				}
			}
			break;
		}
	}

	private void doGetVerify() {
		showWaitDialog();
		getVerify = false;
		accountService.getVetifyCode(phone.getEditableText().toString(), applyType, selection, 
				new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				response.getState();
				if(response.getState() == 0){
					handler.sendEmptyMessage(100);
					getVerify = true;
					hideAllDialog();
					toast(R.string.login_string26);
				}else{
					doResponseInfo(response);
				}
				return null;
			}
		});
	}

	private void doVerity() {
		showWaitDialog();
		accountService.verifyCode(phone.getEditableText().toString(), yanzhengma.getEditableText().toString(), applyType,
				new AsyncResponseCompletedHandler<String>(){

					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						if(response.getState() == 0){
							if(applyType == REGISTER){
								doRegister();
							}
							else if(applyType == RESET){
								doResetSecret();
							}
						}else{
							doResponseInfo(response);
						}
						return null;
					}
			
		});
	}

	private void doRegister() {
		username = phone.getText().toString();
		pwd = mima.getText().toString();
		accountService.register(username, yanzhengma.getEditableText().toString(), mima.getEditableText().toString(), selection, 
				new AsyncResponseCompletedHandler<String>(){
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() == 0){
					save(username, pwd);
					doLogin();
				}else{
					doResponseInfo(response);
				}
				return null;
			}
			
		});
	}
	
	private void doLogin(){
		accountService.login(username, pwd, new AsyncResponseCompletedHandler<String>() {
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					hideAllDialog();
					SalonUser user = (SalonUser) response.getData();
					CacheManager.INSTANCE.setCurrentUser(user);
					nextStep();
				}else{
					doResponseInfo(response);
				}
				return null;
			}
		});
	}
	
	private void doResetSecret() {
		showWaitDialog();
		final String newPwd = mima2.getEditableText().toString();
		accountService.resetSecret("12345666666", yanzhengma.getEditableText().toString(), mima.getEditableText().toString(), 
				new AsyncResponseCompletedHandler<String>(){
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() == 0){
					hideAllDialog();
					save(null, newPwd);
					finish();
				}else{
					doResponseInfo(response);
				}
				return null;
			}
		}	);
	}

	private void doVerifyOldSecret() {
		showWaitDialog();
		accountService.verifyOldSecret("12345666666", mima.getEditableText().toString(),
				new AsyncResponseCompletedHandler<String>(){
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() == 0){
					doChangeSecret();
				}else{
					doResponseInfo(response);
				}
				return null;
			}
		}	);
	}

	protected void doChangeSecret() {
		final String newPwd = mima2.getEditableText().toString();
		accountService.changeSecret("12345666666", newPwd, 
				new AsyncResponseCompletedHandler<String>(){
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() == 0){
					hideAllDialog();
					save(null, newPwd);
					finish();
				}else{
					doResponseInfo(response);
				}
				return null;
			}
		}	);
	}
	
	private boolean validSelection(){
		if(selection == 0){
			toast(R.string.login_string8);
			return false;
		}
		return true;
	}
	
	private boolean validPhone(){
		if(phone.getEditableText() == null || phone.getEditableText().toString().length() != 11){
			toast(R.string.login_string9);
			return false;
		}
		return true;
	}
	
	private boolean validSecret() {
		String mimatxt = mima.getEditableText().toString();
		String mimatxt2 = mima2.getEditableText().toString();
		
		if(mimatxt == null || mimatxt2 == null || mimatxt.length() < 6 || mimatxt2.length() < 6 ){
			toast(R.string.login_string10);
			return false;
		}
		else if(! mimatxt.equals(mimatxt2)){
			toast(R.string.login_string11);
			return false;
		}
		
		return true;
	}
	
	private boolean validYanzhengma(){
		if(yanzhengma.getEditableText().toString() == null || yanzhengma.getEditableText().toString().length() != 6){
			toast(R.string.login_string17);
			return false;
		}
		return true;
	}

	private void doResponseInfo(ResponseBean<?> reBean){
		
		if(reBean.getState() == 0){
			
		}else{
			doVerfiyCodeErr(reBean.getState());
		}
		
		hideAllDialog();
	}
	
	private void doVerfiyCodeErr(int err) {
		if(err == -16){
			showToast(R.string.err_verfiycode_timeout);
		}else if(err == -17){
			showToast(R.string.err_phone_isused);
		}else if(err == -3){
			showToast(R.string.err_forget_phone);
		}else if(err == -8){
			showToast(R.string.err_verfiycode);
		}else if(err == -4){
			showToast(R.string.login_string24);
		}else if(err == -18){
			showToast(R.string.login_string25);
		}
	}

	private void nextStep() {
		Intent intent = null;
		switch (selection) {
		case Role.CUSTOM:
			intent = new Intent(RegisterActivity.this, CustomRegisterActivity.class);
			break;
			
		case Role.SALON:
			intent = new Intent(RegisterActivity.this, SalonRegisterActivity.class);
			break;
			
		case Role.BARBER:
			intent = new Intent(RegisterActivity.this, BarberRegisterActivity.class);
			break;
		}
		if(intent != null){
			startActivity(intent);
		}
		finish();
	}
	
	private void toast(int res){
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				btnVerify.setBackgroundResource(R.drawable.salon_btn_invalid);
				btnVerify.setClickable(false);
				time = TIME;
				handler.sendEmptyMessageDelayed(101, 10);
				break;

			case 101:
				btnVerify.setText(getResources().getString(R.string.login_string18, time));
				time--;
				handler.sendEmptyMessageDelayed(101, 1000);
				if(time == 1){
					handler.removeMessages(101);
					handler.sendEmptyMessageDelayed(102, 1000);
				}
				break;
			
			case 102:
				btnVerify.setBackgroundResource(R.drawable.salon_btn);
				btnVerify.setText(R.string.login_string6);
				btnVerify.setClickable(true);
				break;
			}
		}
	}
	
	private void save(String username, String pwd){
		CHIConfig config = RegisterActivity.this.getCHApplication().getPreferenceConfig();
		if(username != null){
			config.setString(WelcomeActivity.USERNAME, username);
		}
		
		if(pwd != null){
			config.setString(WelcomeActivity.PASSWD, pwd);
		}
	}
}
