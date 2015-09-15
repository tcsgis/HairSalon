package com.aaa.activity.custom;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.DMUtil;
import com.aaa.util.PhotoType;
import com.aaa.util.SalonTools;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class CustomRegisterActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.set_nick)
	private TextView set_nick;
	@CHInjectView(id = R.id.nick)
	private EditText nick;
	
	private PhotoSelectPopupView mPopupAltView;
	private Uri mPhotoUri;
	private String newPhotoPath;
	private CHBitmapCacheWork imgFetcher;
	
	private ISalonFileService fileService = (ISalonFileService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_FILE_SERVER);
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.cus_reString1);
		
		mPopupAltView = new PhotoSelectPopupView(this);
		
		photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupAltView.show();
			}
		});
		
		findViewById(R.id.done).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checkFinish()){
					doUpload();
				}else{
					Toast.makeText(CustomRegisterActivity.this, R.string.cus_reString5, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		if(CacheManager.INSTANCE.getCurrentUser().getNick() != null){
			set_nick.setText(R.string.cus_reString3);
			nick.setText(CacheManager.INSTANCE.getCurrentUser().getNick());
		}
		
		if(CacheManager.INSTANCE.getCurrentUser().getPhoto() != null){
			imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
			try {
				imgFetcher.loadFormCache(CacheManager.INSTANCE.getCurrentUser().getPhoto(), photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkFinish(){
		if(newPhotoPath != null){
			return true;
		}
		
		if(SalonTools.editNotNull(nick) 
				&& ! SalonTools.getText(nick).equals(CacheManager.INSTANCE.getCurrentUser().getNick())){
			return true;
		}
		
		return false;
	}
	
	private void doUpload(){
		//upload photo
		if(newPhotoPath != null){
			ArrayList<String> newPhotos = new ArrayList<String>();
			newPhotos.add(newPhotoPath);
			
			showWaitDialog();
			fileService.uploadFiles(newPhotos, DMUtil.getBidPhotoWidth(this), DMUtil.getBidPhotoHeight(this), 
					new AsyncResponseCompletedHandler<String>() {
				
				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					if(response.getState() >= 0){
						try {
							ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
							uploadUser(photos);
						} catch (Exception e) {
							e.printStackTrace();
							showToast(R.string.upload_fail);
							hideAllDialog();
						}
					}else{
						showToast(R.string.upload_fail);
						hideAllDialog();
					}
					return null;
				}
			});
		}else{
			uploadUser(null);
		}
	}
	
	private void uploadUser(ArrayList<String> photos){
		final SalonUser user = CacheManager.INSTANCE.getCurrentUser().clone();
		user.setNick(SalonTools.getText(nick));
		if(photos != null && photos.size() > 0){
			user.setPhoto(photos.get(0));
		}
		
		accountService.updateSelfMg(user, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response != null && response.getState() >= 0){
					Toast.makeText(CustomRegisterActivity.this, R.string.set_success, Toast.LENGTH_LONG).show();
					CacheManager.INSTANCE.setCurrentUser(user.clone());
					hideAllDialog();
					finish();
				}else{
					Toast.makeText(CustomRegisterActivity.this, R.string.set_fail, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_LOCAL){
					if (data != null) {
						String filepath = data.getStringExtra("filepath");
						Uri uri = Uri.fromFile(new File(filepath));
						mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_BID);
					}
				}
				else if(requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_CAMERA) {
					Uri uri = mPopupAltView.getPhotoUri();
					mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_BID);
				}
				else if(requestCode == PhotoSelectPopupView.CUT_PHOTO){
					mPhotoUri = mPopupAltView.getPhotoUri();
					if(mPhotoUri != null && mPhotoUri.getPath() != null){
						File file = new File(mPhotoUri.getPath());
						if(file.exists() && file.isFile()){
							newPhotoPath = file.getPath();
							photo.setImageBitmap(PictureUtil.
									decodeSampledBitmapFromFile(file.getPath(), 
											photo.getWidth(), 
											photo.getHeight()));
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
