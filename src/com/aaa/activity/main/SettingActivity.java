package com.aaa.activity.main;

import java.util.ArrayList;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.login.RegisterActivity;
import com.aaa.activity.login.UpdateManager;
import com.aaa.util.Role;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.config.MyProperties;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.notify)
	private CheckBox notify;
	@CHInjectView(id = R.id.sound)
	private CheckBox sound;
	@CHInjectView(id = R.id.update)
	private CheckBox update;
	
	private static ArrayList<onLogOutLisener> listeners;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	public interface onLogOutLisener{
		public void onLogout();
	}
	
	public static void registerListener(onLogOutLisener l){
		if(listeners == null){
			listeners = new ArrayList<onLogOutLisener>();
		}
		
		if(l != null && ! listeners.contains(l)){
			listeners.add(l);
		}
	}
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.setting_string1);
		
		notify.setChecked(getCHApplication().getPreferenceConfig().getBoolean(MyProperties.getMyProperties().getReceiveNotifyKey(), true));
		sound.setChecked(getCHApplication().getPreferenceConfig().getBoolean(MyProperties.getMyProperties().getSoundRemindKey(), true));
		update.setChecked(getCHApplication().getPreferenceConfig().getBoolean(MyProperties.getMyProperties().getRemindUpdateKey(), true));
		
		if(CacheManager.INSTANCE.getCurrentUser() != null && CacheManager.INSTANCE.getCurrentUser().getRole() == Role.ADMIN){
			findViewById(R.id.change_pwd).setVisibility(View.GONE);
		}else{
			findViewById(R.id.change_pwd).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SettingActivity.this, RegisterActivity.class);
					intent.putExtra("applyType", RegisterActivity.CHANGE_SECRET);
					startActivity(intent);
				}
			});
		}
		
		findViewById(R.id.quit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final AppMainDialog dialog = new AppMainDialog(SettingActivity.this, R.style.appdialog);
				dialog.withTitle(R.string.barber_orString16)
				.withMessage(R.string.setting_string6)
				.setOKClick(R.string.ok_queren, new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						doQuit();
						dialog.dismiss();
					}
				})
				.setCancelClick(R.string.cancel_quxiao).show();
			}
		});
		
		findViewById(R.id.check_update).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateManager upman = new UpdateManager(SettingActivity.this);
				upman.doCheckUpdate();
			}
		});
		
		notify.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getReceiveNotifyKey(), true);
//					AlarmMessageReceiver.getInstance().setHaveNotification(true);
				} else {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getReceiveNotifyKey(), false);
//					AlarmMessageReceiver.getInstance().setHaveNotification(false);
				}
			}
		});
		
		sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getSoundRemindKey(), true);
				} else {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getSoundRemindKey(), false);
				}
			}
		});
		
		update.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getRemindUpdateKey(), true);
				} else {
					getCHApplication().getPreferenceConfig().setBoolean(MyProperties.getMyProperties().getRemindUpdateKey(), false);
				}
			}
		});
	}

	protected void doQuit() {
		showWaitDialog();
		accountService.logout(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() == 0){
					CacheManager.INSTANCE.clearAllData();
					hideAllDialog();
					notifyObserver();
					finish();
				}else{
					hideAllDialog();
				}
				return null;
			}
		});
	}

	protected void notifyObserver() {
		if(listeners != null){
			for(onLogOutLisener l : listeners){
				l.onLogout();
			}
		}
	}
}
