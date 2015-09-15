package com.aaa.activity.admin;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.ISalonAdminService;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.DMUtil;
import com.aaa.util.PhotoType;
import com.aaa.util.SalonTools;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class AdminAdActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.list)
	private ListView list;
	@CHInjectView(id = R.id.btn_right)
	private Button btn_right;
	
	private final int AD_PHOTO_MAX = 3;
	
	private Uri mPhotoUri;
	private PhotoSelectPopupView mPopupAltView;
	private PhotoAdapter adapter;
	private ArrayList<String> photos = new ArrayList<String>();
	private ArrayList<String> urls = new ArrayList<String>();
	
	private ISalonAdminService adminService = (ISalonAdminService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ADMIN_SERVER);
	private ISalonFileService fileService = (ISalonFileService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_FILE_SERVER);
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.admin_adString1);
		setRightBtn(true, 0);
		btn_right.setText(R.string.done);
		mPopupAltView = new PhotoSelectPopupView(this);
		doGetDatas();
	}

	@Override
	protected void clickRight() {
		doUpload();
	}
	
	private void doGetDatas() {
		showWaitDialog();
		accountService.getBanners(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<BannerPic> banners = (ArrayList<BannerPic>) response.getData();
					photos = banners.get(0).getPhotos();
					urls = banners.get(0).getUrls();
					
					adapter = new PhotoAdapter(photos, AdminAdActivity.this);
					list.setAdapter(adapter);
				}else{
					Toast.makeText(AdminAdActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doUpload() {
		final ArrayList<String> newPhotos = adapter.getPhotoPaths();
		ArrayList<String> uploadPhotos = new ArrayList<String>(); 
		if(newPhotos.size() > 0){
			for(int i = 0; i < newPhotos.size(); i++){
				if(! photos.contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			uploadBanners(newPhotos);
		}else{
			showWaitDialog();
			fileService.uploadFiles(uploadPhotos, DMUtil.getFacePhotoWidth(this), DMUtil.getFacePhotoHeight(this), 
					new AsyncResponseCompletedHandler<String>() {

						@Override
						public String doCompleted(ResponseBean<?> response,
								ChCareWepApiServiceType servieType) {
							if(response.getState() >= 0){
								try {
									ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
									newPhotos.addAll(photos);
									uploadBanners(newPhotos);
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
		}
	}
	
	private void uploadBanners(ArrayList<String> newPhotos){
		showWaitDialog();
		BannerPic bp = new BannerPic();
		bp.setPhotos(SalonTools.composePhotos(newPhotos));
		bp.setUrls(SalonTools.composePhotos(urls));
		adminService.updateAdPics(bp, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					Toast.makeText(AdminAdActivity.this, R.string.admin_adString3, Toast.LENGTH_SHORT).show();
					hideAllDialog();
					finish();
				}else{
					Toast.makeText(AdminAdActivity.this, R.string.admin_adString4, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private class PhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		private CHBitmapCacheWork imgFetcher;
		
		public PhotoAdapter(ArrayList<String> list, Context context) {
			this.list = (ArrayList<String>) list.clone();
			this.context = context;
			imgFetcher = SalonTools.getImageFetcher(context, getCHApplication(), false, 0);
			if(this.list.size() < AD_PHOTO_MAX){
				this.list.add(null);
			}
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private void addPhoto(String path){
			if(list.size() == AD_PHOTO_MAX){
				list.remove(AD_PHOTO_MAX - 1);
				list.add(path);
			}else if(list.size() < AD_PHOTO_MAX){
				list.add(list.size() - 1, path);
			}
			
			notifyDataSetChanged();
		}
		
		private void removesalonPhoto(String path){
			if(list.size() == AD_PHOTO_MAX){
				list.remove(path);
				if(list.get(list.size() - 1) != null){
					list.add(null);
				}
			}else if(list.size() < AD_PHOTO_MAX){
				list.remove(path);
			}
			
			notifyDataSetChanged();
		}
		
		private ArrayList<String> getPhotoPaths(){
			ArrayList<String> ret = (ArrayList<String>) list.clone();
			ret.remove(null);
			return ret;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final String path = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_admin_ad, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
				
			viewHolder.photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(path == null){
						mPopupAltView.show();
					}
				}
			});
			
			viewHolder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
					dialog.withTitle(R.string.dialog_title)
							.withMessage(R.string.salon_reString24)
									.setOKClick(R.string.ok_queren, new View.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											removesalonPhoto(path);
											dialog.dismiss();
										}
									})
									.setCancelClick(R.string.cancel_quxiao).show();
				}
			});
			
			if(path == null){
				viewHolder.delete.setVisibility(View.INVISIBLE);
				viewHolder.photo.setImageResource(R.drawable.add_salon_photo);
			}else{
				viewHolder.delete.setVisibility(View.VISIBLE);
				try {
					File file = new File(path);
					if(file.exists()){
						viewHolder.photo.setImageBitmap(PictureUtil.
								decodeSampledBitmapFromFile(path, 
										viewHolder.photo.getWidth(), 
										viewHolder.photo.getHeight()));
					}else{
						imgFetcher.loadFormCache(path, viewHolder.photo);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public Button delete;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CHLogger.d(this, "requestCode " + requestCode + ",  resultCode " + resultCode);
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_LOCAL){
					if (data != null) {
						String filepath = data.getStringExtra("filepath");
						Uri uri = Uri.fromFile(new File(filepath));
						mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_AD);
					}
				}
				else if(requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_CAMERA) {
					Uri uri = mPopupAltView.getPhotoUri();
					mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_AD);
				}
				else if(requestCode == PhotoSelectPopupView.CUT_PHOTO){
					mPhotoUri = mPopupAltView.getPhotoUri();
					if(mPhotoUri != null && mPhotoUri.getPath() != null){
						File file = new File(mPhotoUri.getPath());
						if(file.exists() && file.isFile()){
							adapter.addPhoto(file.getPath());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
