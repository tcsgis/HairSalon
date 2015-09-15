package com.aaa.activity.salon;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberRegisterActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.DMUtil;
import com.aaa.util.PhotoType;
import com.aaa.util.SalonTools;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class SalonRegisterBarberActivity extends LlwTitleActivity {
	
	@CHInjectView(id = R.id.nick)
	private EditText nick;
	@CHInjectView(id = R.id.exp)
	private EditText exp;
	@CHInjectView(id = R.id.tang_duan)
	private EditText tang_duan;
	@CHInjectView(id = R.id.tang_zhong)
	private EditText tang_zhong;
	@CHInjectView(id = R.id.tang_chang)
	private EditText tang_chang;
	@CHInjectView(id = R.id.ran_duan)
	private EditText ran_duan;
	@CHInjectView(id = R.id.ran_zhong)
	private EditText ran_zhong;
	@CHInjectView(id = R.id.ran_chang)
	private EditText ran_chang;
	@CHInjectView(id = R.id.jian_duan)
	private EditText jian_duan;
	@CHInjectView(id = R.id.jian_zhong)
	private EditText jian_zhong;
	@CHInjectView(id = R.id.jian_chang)
	private EditText jian_chang;
	
	@CHInjectView(id = R.id.has_health)
	private LinearLayout has_health;
	@CHInjectView(id = R.id.has_no_health)
	private LinearLayout has_no_health;
	@CHInjectView(id = R.id.has_health_point)
	private ImageView has_health_point;
	@CHInjectView(id = R.id.has_no_health_point)
	private ImageView has_no_health_point;
	@CHInjectView(id = R.id.jian_point)
	private ImageView jian_point;
	@CHInjectView(id = R.id.tang_point)
	private ImageView tang_point;
	@CHInjectView(id = R.id.ran_point)
	private ImageView ran_point;
	@CHInjectView(id = R.id.huli_point)
	private ImageView huli_point;
	
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.health_photo)
	private ImageView health_photo;
	
	@CHInjectView(id = R.id.done)
	private Button done;
	
	@CHInjectView(id = R.id.zuopin_list)
	private HorizontalListView zuopin_list;
	@CHInjectView(id = R.id.zhengshu_list)
	private HorizontalListView zhengshu_list;
	
	public static final int BARBER_PHOTO_MAX = 3;
	private final int PHOTO = 1;
	private final int HEALTH_PHOTO = 2;
	private final int ZUO_PIN = 3;
	private final int ZHENG_SHU = 4;
	
	private int clickWhich = 0;
	
	private PhotoSelectPopupView mPopupAltView;
	private PhotoAdapter zuopin_adapter;
	private PhotoAdapter zhengshu_adapter;
	
	private String photoPath = null;
	private String healthPath = null;
	private ArrayList<EditText> prices = new ArrayList<EditText>();
	
	private boolean hasHealth = true;
	private Uri mPhotoUri;
	private SalonBarberInfoView barber;
	private CHBitmapCacheWork imageFetcher;
	
	private boolean jianSelected = false;
	private boolean tangSelected = false;
	private boolean ranSelected = false;
	private boolean huliSelected = false;
	
	private ISalonFileService fileService = (ISalonFileService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_FILE_SERVER);
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_rbString1);
		mPopupAltView = new PhotoSelectPopupView(this);
		
		has_health.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hasHealth = true;
				SalonTools.healthEffect(hasHealth, health_photo);
				has_health_point.setImageResource(R.drawable.online);
				has_no_health_point.setImageResource(R.drawable.unonline);
			}
		});
		
		has_no_health.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hasHealth = false;
				SalonTools.healthEffect(hasHealth, health_photo);
				has_health_point.setImageResource(R.drawable.unonline);
				has_no_health_point.setImageResource(R.drawable.online);
			}
		});
		
		photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickWhich = PHOTO;
				mPopupAltView.show();
			}
		});
		
		health_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickWhich = HEALTH_PHOTO;
				mPopupAltView.show();
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkFinish();
			}
		});
		
		findViewById(R.id.jian).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(jianSelected){
					jianSelected = false;
					jian_point.setImageResource(R.drawable.unonline);
				}else{
					jianSelected = true;
					jian_point.setImageResource(R.drawable.online);
				}
			}
		});
		
		findViewById(R.id.tang).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tangSelected){
					tangSelected = false;
					tang_point.setImageResource(R.drawable.unonline);
				}else{
					tangSelected = true;
					tang_point.setImageResource(R.drawable.online);
				}
			}
		});
		
		findViewById(R.id.ran).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ranSelected){
					ranSelected = false;
					ran_point.setImageResource(R.drawable.unonline);
				}else{
					ranSelected = true;
					ran_point.setImageResource(R.drawable.online);
				}
			}
		});
		
		findViewById(R.id.hu_li).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(huliSelected){
					huliSelected = false;
					huli_point.setImageResource(R.drawable.unonline);
				}else{
					huliSelected = true;
					huli_point.setImageResource(R.drawable.online);
				}
			}
		});
		
		initView();
	}

	private void initView(){
		prices.add(tang_duan);
		prices.add(tang_zhong);
		prices.add(tang_chang);
		prices.add(ran_duan);
		prices.add(ran_zhong);
		prices.add(ran_chang);
		prices.add(jian_duan);
		prices.add(jian_zhong);
		prices.add(jian_chang);
		
		barber = (SalonBarberInfoView) getIntent().getSerializableExtra("barber");
		if(barber == null){
			barber = new SalonBarberInfoView();
			zuopin_adapter = new PhotoAdapter(this, ZUO_PIN);
			zhengshu_adapter = new PhotoAdapter(this, ZHENG_SHU);
			zuopin_list.setAdapter(zuopin_adapter);
			zhengshu_list.setAdapter(zhengshu_adapter);
		}else{
			setAdept(barber.getAdept());
			photoPath = barber.getPhoto(); 
			healthPath = barber.getHealth();
			imageFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
			nick.setText(barber.getNick());
			exp.setText(String.valueOf(barber.getWorkYears()));
			try {
				imageFetcher.loadFormCache(barber.getPhoto(), photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			zuopin_adapter = new PhotoAdapter(barber.getWorks(), this, ZUO_PIN);
			zuopin_list.setAdapter(zuopin_adapter);
			zhengshu_adapter = new PhotoAdapter(barber.getCerts(), this, ZHENG_SHU);
			zhengshu_list.setAdapter(zhengshu_adapter);
			
			for(int i = 0; i < 9; i++){
				this.prices.get(i).setText(String.valueOf(barber.getPrices()[i]));
			}
			
			if(barber.getHealth() != null){
				hasHealth = true;
				SalonTools.healthEffect(hasHealth, health_photo);
				has_health_point.setImageResource(R.drawable.online);
				has_no_health_point.setImageResource(R.drawable.unonline);
				try {
					imageFetcher.loadFormCache(barber.getHealth(), health_photo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				hasHealth = false;
				SalonTools.healthEffect(hasHealth, health_photo);
				has_health_point.setImageResource(R.drawable.unonline);
				has_no_health_point.setImageResource(R.drawable.online);
			}
		}
		
		SalonTools.scrollToHead(this.nick);
	}
	
	private int getAdept() {
		int ret = 0;
		if(jianSelected){
			ret |= BarberRegisterActivity.MASK_JIAN;
		}
		if(tangSelected){
			ret |= BarberRegisterActivity.MASK_TANG;
		}
		if(ranSelected){
			ret |= BarberRegisterActivity.MASK_RAN;
		}
		if(huliSelected){
			ret |= BarberRegisterActivity.MASK_HULI;
		}
		
		return ret;
	}
	
	private void setAdept(int adaept) {
		if((adaept & BarberRegisterActivity.MASK_JIAN) != 0){
			jianSelected = true;
			jian_point.setImageResource(R.drawable.online);
		}
		if((adaept & BarberRegisterActivity.MASK_TANG) != 0){
			tangSelected = true;
			tang_point.setImageResource(R.drawable.online);
		}
		if((adaept & BarberRegisterActivity.MASK_RAN) != 0){
			ranSelected = true;
			ran_point.setImageResource(R.drawable.online);
		}
		if((adaept & BarberRegisterActivity.MASK_HULI) != 0){
			huliSelected = true;
			huli_point.setImageResource(R.drawable.online);
		}
	}
	
	private void checkFinish() {
//		nickTxt = nick.getText().toString();
//		expTxt = exp.getText().toString();
//		
//		String tang_duan_txt = tang_duan.getText().toString();
//		String tang_zhong_txt = tang_zhong.getText().toString();
//		String tang_chang_txt = tang_chang.getText().toString();
//		String ran_duan_txt = ran_duan.getText().toString();
//		String ran_zhong_txt = ran_zhong.getText().toString();
//		String ran_chang_txt = ran_chang.getText().toString();
//		String jian_duan_txt = jian_duan.getText().toString();
//		String jian_zhong_txt = jian_zhong.getText().toString();
//		String jian_chang_txt = jian_chang.getText().toString();
		
		if(hasHealth && healthPath == null){
			Toast.makeText(this, R.string.salon_rbString18, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(photoPath != null && SalonTools.editNotNull(nick) && SalonTools.editNotNull(exp)
				&& SalonTools.editNotNull(tang_duan) && SalonTools.editNotNull(tang_zhong)&& SalonTools.editNotNull(tang_chang)
				&& SalonTools.editNotNull(ran_duan) && SalonTools.editNotNull(ran_zhong)&& SalonTools.editNotNull(ran_chang)
				&& SalonTools.editNotNull(jian_duan) && SalonTools.editNotNull(jian_zhong)&& SalonTools.editNotNull(jian_chang)){
			
			doUpload();
		}else{
			Toast.makeText(this, R.string.salon_rbString18, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doUpload() {
		uploadFacePhoto();
	}

	private void uploadFacePhoto() {
		CHLogger.d(this, "uploadFacePhoto");
		if(photoPath == null || photoPath.equals(barber.getPhoto())){
			uploadZuopinPhoto();
		}else{
			showWaitDialog();
			ArrayList<String> ss = new ArrayList<String>();
			ss.add(photoPath);
			fileService.uploadFiles(ss, DMUtil.getFacePhotoWidth(this), DMUtil.getFacePhotoHeight(this), 
					new AsyncResponseCompletedHandler<String>() {
				
				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					if(response.getState() >= 0){
						try {
							ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
							barber.setPhoto(photos.get(0));
							uploadZuopinPhoto();
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

	private void uploadZuopinPhoto(){
		CHLogger.d(this, "uploadZuopinPhoto");
		final ArrayList<String> newPhotos = zuopin_adapter.getPhotoPaths();
		ArrayList<String> uploadPhotos = new ArrayList<String>(); 
		if(newPhotos.size() > 0){
			for(int i = 0; i < newPhotos.size(); i++){
				if(! barber.getWorks().contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			barber.setWorks(newPhotos);
			uploadZhengshuPhoto();
		}else{
			showWaitDialog();
			fileService.uploadFiles(uploadPhotos, DMUtil.getElsePhotoWidth(this), DMUtil.getElsePhotoWidth(this), 
					new AsyncResponseCompletedHandler<String>() {
				
				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					if(response.getState() >= 0){
						try {
							ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
							newPhotos.addAll(photos);
							barber.setWorks(newPhotos);
							uploadZhengshuPhoto();
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
	
	private void uploadZhengshuPhoto(){
		CHLogger.d(this, "uploadZhengshuPhoto");
		final ArrayList<String> newPhotos = zhengshu_adapter.getPhotoPaths();
		ArrayList<String> uploadPhotos = new ArrayList<String>(); 
		if(newPhotos.size() > 0){
			for(int i = 0; i < newPhotos.size(); i++){
				if(! barber.getCerts().contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			barber.setCerts(newPhotos);
			uploadHealth();
		}else{
			showWaitDialog();
			fileService.uploadFiles(uploadPhotos, DMUtil.getElsePhotoWidth(this), DMUtil.getElsePhotoWidth(this), 
					new AsyncResponseCompletedHandler<String>() {
				
				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					if(response.getState() >= 0){
						try {
							ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
							newPhotos.addAll(photos);
							barber.setCerts(newPhotos);
							uploadHealth();
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
	
	private void uploadHealth(){
		CHLogger.d(this, "uploadHealth");
		if(hasHealth && healthPath != null && ! healthPath.equals(barber.getHealth())){
			showWaitDialog();
			ArrayList<String> ss = new ArrayList<String>();
			ss.add(healthPath);
			fileService.uploadFiles(ss, DMUtil.getElsePhotoWidth(this), DMUtil.getElsePhotoWidth(this), 
					new AsyncResponseCompletedHandler<String>() {
				
				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					if(response.getState() >= 0){
						try {
							ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
							barber.setHealth(photos.get(0));
							uploadBarber();
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
		else if(! hasHealth){
			barber.setHealth(null);
			uploadBarber();
		}
		else{
			uploadBarber();
		}
	}
	
	private void uploadBarber(){
		barber.setNick(SalonTools.getText(nick));
		
		int exp = Integer.valueOf(SalonTools.getText(this.exp));
		barber.setWorkYears((byte) exp);
		
		int[] price = new int[10];
		for(int i = 0; i < prices.size(); i++){
			price[i] = Integer.valueOf(prices.get(i).getText().toString().trim());
		}
		price[9] = getAdept();
		barber.setPrices(price);
		
		showWaitDialog();
		salonService.uploadMyBarber(barber, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				try {
					if(response.getState() >= 0){
						Intent data = new Intent();
						data.putExtra("barber", barber);
						
						if(barber.getId() == 0){
							int id = (Integer) response.getData();
							barber.setId(id);
							setResult(SalonAddBarberActivity.ADD, data);
						}else{
							setResult(SalonAddBarberActivity.MODIFY, data);
						}
						
						hideAllDialog();
						finish();
					}else{
						hideAllDialog();
						showToast(R.string.upload_fail);
					}
				} catch (Exception e) {
					CHLogger.e("salonRe llw", e.getStackTrace().toString());
				}
				return null;
			}
		});
	}
	
	private class PhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		private int clickWhich = 0;
		
		public PhotoAdapter(ArrayList<String> list, Context context, int clickWhich) {
			if(list != null){
				this.list = (ArrayList<String>) list.clone();
				if(list.size() < BARBER_PHOTO_MAX){
					this.list.add(null);
				}
			}else{
				this.list = new ArrayList<String>();
				this.list.add(null);
			}
			this.context = context;
			this.clickWhich = clickWhich;
		}
		
		public PhotoAdapter(Context context, int clickWhich) {
			list = new ArrayList<String>();
			list.add(null);
			this.context = context;
			this.clickWhich = clickWhich;
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

		private void addsalonPhoto(String path){
			if(list.size() == BARBER_PHOTO_MAX){
				list.remove(BARBER_PHOTO_MAX - 1);
				list.add(path);
			}
			
			if(list.size() < BARBER_PHOTO_MAX){
				list.add(list.size() - 1, path);
			}
			
			notifyDataSetChanged();
		}
		
		private void removesalonPhoto(String path){
			if(list.size() == BARBER_PHOTO_MAX){
				list.remove(path);
				if(list.get(list.size() - 1) != null){
					list.add(null);
				}
			}else if(list.size() < BARBER_PHOTO_MAX){
				list.remove(path);
			}
			
			notifyDataSetChanged();
		}
		
		private ArrayList<String> getPhotoPaths(){
			ArrayList<String> ret = new ArrayList<String>();
			ret = (ArrayList<String>) list.clone();
			ret.remove(null);
			return ret;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final String path = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_list, null);
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
						SalonRegisterBarberActivity.this.clickWhich = clickWhich;
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
						imageFetcher.loadFormCache(path, viewHolder.photo);
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
						mPopupAltView.cutPhoto(uri, getCutSize());
					}
				}
				else if(requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_CAMERA) {
					Uri uri = mPopupAltView.getPhotoUri();
					mPopupAltView.cutPhoto(uri, getCutSize());
				}
				else if(requestCode == PhotoSelectPopupView.CUT_PHOTO){
					mPhotoUri = mPopupAltView.getPhotoUri();
					if(mPhotoUri != null && mPhotoUri.getPath() != null){
						File file = new File(mPhotoUri.getPath());
						if(file.exists() && file.isFile()){
							switch (clickWhich) {
							case PHOTO:
								try {
									photoPath = file.getPath();
									photo.setImageBitmap(PictureUtil.
											decodeSampledBitmapFromFile(file.getPath(), 
													photo.getWidth(), 
													photo.getHeight()));
								}catch (Exception e) {
									e.printStackTrace();
								}catch (OutOfMemoryError e) {
									e.printStackTrace();
								}
								break;
								
							case HEALTH_PHOTO:
								try {
									healthPath = file.getPath();
									health_photo.setImageBitmap(PictureUtil.
											decodeSampledBitmapFromFile(file.getPath(), 
													health_photo.getWidth(), 
													health_photo.getHeight()));
								}catch (Exception e) {
									e.printStackTrace();
								}catch (OutOfMemoryError e) {
									e.printStackTrace();
								}
								break;
								
							case ZUO_PIN:
								zuopin_adapter.addsalonPhoto(file.getPath());
								break;
								
							case ZHENG_SHU:
								zhengshu_adapter.addsalonPhoto(file.getPath());
								break;
							}
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int getCutSize(){
		int size = -1;
		switch (clickWhich) {
		case PHOTO:
			size = PhotoType.PHOTO_FACE;
			break;
			
		case HEALTH_PHOTO:
		case ZUO_PIN:
		case ZHENG_SHU:
			size = PhotoType.PHOTO_ELSE;
			break;
		}
		
		return size;
	}
}
