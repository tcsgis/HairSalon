package com.aaa.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.config.CHIConfig;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class LoginActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.phone)
	private TextView phone;
	@CHInjectView(id = R.id.mima)
	private TextView mima;
	
	private String username;
	private String mimatxt;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.login_string20);
		
		findViewById(R.id.done).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				username = phone.getEditableText().toString();
				mimatxt = mima.getEditableText().toString();
				if(username != null && username.length() == 11){
					if(mimatxt != null && mimatxt.length() > 5){
						doLogin();
					}else{
						Toast.makeText(LoginActivity.this, R.string.login_string10, Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(LoginActivity.this, R.string.login_string9, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		findViewById(R.id.forget).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				intent.putExtra("applyType", RegisterActivity.RESET);
				startActivity(intent);
			}
		});
	}

	protected void doLogin() {
		showWaitDialog();
		accountService.login(username, mimatxt, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					CHIConfig config = LoginActivity.this.getCHApplication().getPreferenceConfig();
					config.setString(WelcomeActivity.USERNAME, username);
					config.setString(WelcomeActivity.PASSWD, mimatxt);
					SalonUser user = (SalonUser) response.getData();
					CacheManager.INSTANCE.setCurrentUser(user);
					finish();
				}else{
					if(response.getState() == -3)
						Toast.makeText(LoginActivity.this, R.string.login_string14, Toast.LENGTH_SHORT).show();
					if(response.getState() == -4)
						Toast.makeText(LoginActivity.this, R.string.login_string21, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
}
