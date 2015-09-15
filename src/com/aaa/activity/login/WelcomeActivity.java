package com.aaa.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.MainActivity;
import com.changhong.activity.BaseActivity;
import com.changhong.util.config.CHIConfig;
import com.changhong.util.db.bean.CacheManager;

public class WelcomeActivity extends BaseActivity{

	public static final String USERNAME = "username";
	public static final String PASSWD = "password";
	public static final String DISTRICT_SEARCH = "DISTRICT_SEARCH";
	
	private String phone;
	private String mima;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		CHIConfig config = this.getCHApplication().getPreferenceConfig();
		phone = config.getString(USERNAME, "");
		mima = config.getString(PASSWD, "");
		
		if(phone.length() == 11 && mima.length() > 5 && mima.length() < 17){
			doLogin();
		}else{
			toMainActivity();
		}
	}
	
	private void doLogin() {
		accountService.login(phone, mima, new AsyncResponseCompletedHandler<String>() {
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					SalonUser user = (SalonUser) response.getData();
					CacheManager.INSTANCE.setCurrentUser(user);
				}
				
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				return null;
			}
		});
	}
	
	private Handler handler = new Handler( );
	private void toMainActivity(){
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
			}
		};
		
		handler.postDelayed(runnable, 1500);
	}
}
