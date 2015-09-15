package com.aaa.activity.barber;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.IDNumUtil;
import com.aaa.util.PhotoType;
import com.aaa.util.SalonTools;
import com.aaa.util.Status;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

public class BarberRegisterActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.barber_re_title)
	private TextView barber_re_title;
	@CHInjectView(id = R.id.name)
	private EditText name;
	@CHInjectView(id = R.id.id_num)
	private EditText id_num;
	@CHInjectView(id = R.id.phone)
	private EditText phone;
	@CHInjectView(id = R.id.nick)
	private EditText nick;
	@CHInjectView(id = R.id.exp)
	private EditText exp;
	@CHInjectView(id = R.id.district)
	private Button district;
	@CHInjectView(id = R.id.salon)
	private EditText salon;
	@CHInjectView(id = R.id.recommend)
	private EditText recommend;
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
	
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.health_photo)
	private ImageView health_photo;
	
	@CHInjectView(id = R.id.fengcheng37)
	private LinearLayout fengcheng37;
	@CHInjectView(id = R.id.fengcheng46)
	private LinearLayout fengcheng46;
	@CHInjectView(id = R.id.fengcheng55)
	private LinearLayout fengcheng55;
	@CHInjectView(id = R.id.fengcheng37_point)
	private ImageView fengcheng37_point;
	@CHInjectView(id = R.id.fengcheng46_point)
	private ImageView fengcheng46_point;
	@CHInjectView(id = R.id.fengcheng55_point)
	private ImageView fengcheng55_point;
	@CHInjectView(id = R.id.jian_point)
	private ImageView jian_point;
	@CHInjectView(id = R.id.tang_point)
	private ImageView tang_point;
	@CHInjectView(id = R.id.ran_point)
	private ImageView ran_point;
	@CHInjectView(id = R.id.huli_point)
	private ImageView huli_point;
	
	@CHInjectView(id = R.id.done)
	private Button done;
	
	@CHInjectView(id = R.id.zuopin_list)
	private HorizontalListView zuopin_list;
	@CHInjectView(id = R.id.zhengshu_list)
	private HorizontalListView zhengshu_list;
	@CHInjectView(id = R.id.product_list)
	private ListView product_list;
	
	public static final int MASK_JIAN = 1;
	public static final int MASK_TANG = 2;
	public static final int MASK_RAN = 4;
	public static final int MASK_HULI = 8;
	
	private final int BARBER_PHOTO_MAX = 3;
	private final int PRODUCT_MAX = 6 + 1;
	private final int PHOTO = 1;
	private final int HEALTH_PHOTO = 2;
	private final int ZUO_PIN = 3;
	private final int ZHENG_SHU = 4;
	private final int SELECT_DISTRICT = 16546;
	
	private int clickWhich = 0;
	
	private PhotoSelectPopupView mPopupAltView;
	private PhotoAdapter zuopin_adapter;
	private PhotoAdapter zhengshu_adapter;
	private ProductAdapter productAdapter;
	
	private ArrayList<ImageView> fenchengList = new ArrayList<ImageView>();
	private ArrayList<EditText> prices = new ArrayList<EditText>();
	private String photoPath = null;
	private String healthPath = null;
	private byte fengcheng = 3;
	
	private boolean hasHealth = true;
	private Uri mPhotoUri;
	private SalonUser user;
	
	private boolean jianSelected = false;
	private boolean tangSelected = false;
	private boolean ranSelected = false;
	private boolean huliSelected = false;
	
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
		setTitile(R.string.salon_rbString1);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		mPopupAltView = new PhotoSelectPopupView(this);
		fenchengList.add(fengcheng37_point);
		fenchengList.add(fengcheng46_point);
		fenchengList.add(fengcheng55_point);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		district.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(BarberRegisterActivity.this, DistrictActivity.class);
				intent2.putExtra("address", district.getText().toString());//不传参数也可以，就不会有默认选中某个值
				startActivityForResult(intent2, SELECT_DISTRICT);
			}
		});
		
		has_health.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHealth(true);
			}
		});
		
		has_no_health.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHealth(false);
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
		
		fengcheng37.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fengcheng = 3;
				SalonTools.setSelection(0, fenchengList);
			}
		});
		
		fengcheng46.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fengcheng = 4;
				SalonTools.setSelection(1, fenchengList);
			}
		});
		
		fengcheng55.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fengcheng = 5;
				SalonTools.setSelection(2, fenchengList);
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

	private void initView() {
		prices.add(tang_duan);
		prices.add(tang_zhong);
		prices.add(tang_chang);
		prices.add(ran_duan);
		prices.add(ran_zhong);
		prices.add(ran_chang);
		prices.add(jian_duan);
		prices.add(jian_zhong);
		prices.add(jian_chang);
		
		user = CacheManager.INSTANCE.getCurrentUser();
		
		if(user.getStatus() == Status.NORMAL){
			ArrayList<EditText> notModify = new ArrayList<EditText>();
			notModify.add(name);//llwtest
			notModify.add(id_num);
			notModify.add(exp);
			
			barber_re_title.setText(R.string.salon_reString40);
			
			int color = getResources().getColor(R.color.gray_txt);
			for(EditText edit : notModify){
				edit.setEnabled(false);
				edit.setTextColor(color);
			}
			district.setEnabled(false);
			district.setTextColor(color);
		}
		
		recommend.setText(user.getDesc());
		setAdept(user.getAdept());
		name.setText(user.getPerson_Name());
		id_num.setText(user.getPerson_Id());
		phone.setText(user.getTel());
		nick.setText(user.getNick() == null ? null : user.getNick());
		exp.setText(user.getWorkYears() == 0 ? null : String.valueOf(user.getWorkYears()));
		district.setText(SalonTools.getDisplayArea(this, user.getAreas()));
		setHealth(user.getHealth() == null ? false : true);
		healthPath = user.getHealth();
		fengcheng = user.getRatio();
		photoPath = user.getPhoto();
		SalonTools.setSelection(fengcheng - 3, fenchengList);
		try {
			imgFetcher.loadFormCache(user.getPhoto(), photo);
			imgFetcher.loadFormCache(user.getHealth(), health_photo);
			for(int i = 0; i < 9; i++){
				this.prices.get(i).setText(String.valueOf(user.getPrices()[i]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		zuopin_adapter = new PhotoAdapter(user.getWorks(), this, ZUO_PIN);
		zhengshu_adapter = new PhotoAdapter(user.getCerts(), this, ZHENG_SHU);
		zuopin_list.setAdapter(zuopin_adapter);
		zhengshu_list.setAdapter(zhengshu_adapter);
		productAdapter = new ProductAdapter(user.getProducts(), this);
		productAdapter.refreshHeight();
		product_list.setAdapter(productAdapter);
	}

	private void setAdept(int adaept) {
		if((adaept & MASK_JIAN) != 0){
			jianSelected = true;
			jian_point.setImageResource(R.drawable.online);
		}
		if((adaept & MASK_TANG) != 0){
			tangSelected = true;
			tang_point.setImageResource(R.drawable.online);
		}
		if((adaept & MASK_RAN) != 0){
			ranSelected = true;
			ran_point.setImageResource(R.drawable.online);
		}
		if((adaept & MASK_HULI) != 0){
			huliSelected = true;
			huli_point.setImageResource(R.drawable.online);
		}
	}

	private void setHealth(boolean b){
		hasHealth = b;
		SalonTools.healthEffect(b, health_photo);
		if(b){
			has_health_point.setImageResource(R.drawable.online);
			has_no_health_point.setImageResource(R.drawable.unonline);
		}else{
			has_health_point.setImageResource(R.drawable.unonline);
			has_no_health_point.setImageResource(R.drawable.online);
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
		if(! SalonTools.editNotNull(id_num) || ! IDNumUtil.IDCardValidate(id_num.getText().toString())){
			Toast.makeText(this, R.string.salon_rbString21, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(district.getText() == null || district.getText().toString().trim().length() == 0){
			Toast.makeText(this, R.string.salon_rbString22, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(CacheManager.INSTANCE.getCurrentUser().getStatus() == Status.ELSE && hasHealth && healthPath == null){
			Toast.makeText(this, R.string.salon_rbString18, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(photoPath != null && SalonTools.editNotNull(name) && SalonTools.editNotNull(exp) 
				&& SalonTools.editNotNull(phone) && phone.getText().toString().trim().length() == 11
				&& SalonTools.editNotNull(tang_duan) && SalonTools.editNotNull(tang_zhong)&& SalonTools.editNotNull(tang_chang)
				&& SalonTools.editNotNull(ran_duan) && SalonTools.editNotNull(ran_zhong)&& SalonTools.editNotNull(ran_chang)
				&& SalonTools.editNotNull(jian_duan) && SalonTools.editNotNull(jian_zhong)&& SalonTools.editNotNull(jian_chang)
				&& fengcheng != 0){
			
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
		if(photoPath.equals(user.getPhoto())){
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
							user.setPhoto(photos.get(0));
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
				if(! user.getWorks().contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			user.setWorks(newPhotos);
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
							user.setWorks(newPhotos);
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
				if(! user.getCerts().contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			user.setCerts(newPhotos);
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
							user.setCerts(newPhotos);
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
		if(hasHealth && healthPath != null && ! healthPath.equals(user.getHealth())){
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
							user.setHealth(photos.get(0));
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
			user.setHealth(null);
			uploadBarber();
		}
		else{
			uploadBarber();
		}
	}
	
	private void uploadBarber(){
		CHLogger.d(this, "uploadBarber");
		user.setPerson_Name(SalonTools.getText(name));
		user.setPerson_Id(SalonTools.getText(id_num));
		user.setTel(SalonTools.getText(phone));
		user.setNick(SalonTools.getText(nick));
		int exp = Integer.valueOf(SalonTools.getText(this.exp));
		user.setWorkYears((byte) exp);
		user.setAreas(SalonTools.getUploadArea(this, SalonTools.getText(district)));
		user.setProducts(productAdapter.getProducts());
		
		int[] price = new int[10];
		for(int i = 0; i < prices.size(); i++){
			price[i] = Integer.valueOf(prices.get(i).getText().toString().trim());
		}
		price[9] = getAdept();
		
		user.setPrices(price);
		user.setRatio(fengcheng);
		user.setVersion((byte)1);
		try {
			user.setDesc(recommend.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showWaitDialog();
		accountService.updateSelfMg(user, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					if(CacheManager.INSTANCE.getCurrentUser().getStatus() != Status.NORMAL){
						Toast.makeText(BarberRegisterActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
						user.setStatus(Status.VERIFYING);
					}else{
						Toast.makeText(BarberRegisterActivity.this, R.string.upload_success2, Toast.LENGTH_SHORT).show();
					}
					CacheManager.INSTANCE.setCurrentUser(user.clone());
					hideAllDialog();
					finish();
				}else{
					Toast.makeText(BarberRegisterActivity.this, R.string.upload_fail, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private int getAdept() {
		int ret = 0;
		if(jianSelected){
			ret |= MASK_JIAN;
		}
		if(tangSelected){
			ret |= MASK_TANG;
		}
		if(ranSelected){
			ret |= MASK_RAN;
		}
		if(huliSelected){
			ret |= MASK_HULI;
		}
		
		return ret;
	}

	private class PhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		private int clickWhich = 0;
		
		public PhotoAdapter(ArrayList<String> list, Context context, int clickWhich) {
			if(list != null){
				this.list = (ArrayList<String>) list.clone();
			}else{
				this.list = new ArrayList<String>();
			}
			this.context = context;
			this.clickWhich = clickWhich;
			if(this.list.size() < BARBER_PHOTO_MAX){
				this.list.add(this.list.size(), null);
			}
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
			}else if(list.size() < BARBER_PHOTO_MAX){
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
						BarberRegisterActivity.this.clickWhich = clickWhich;
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
	
	private class ProductAdapter extends BaseAdapter{

		private ArrayList<Product> list;
		private Context context;
		
		public ProductAdapter(ArrayList<Product> list, Context context) {
			if(list == null){
				this.list = new ArrayList<Product>();
			}else{
				this.list = (ArrayList<Product>) list.clone();
				try {
					for(int i = 0; i < this.list.size(); i++){
						Product p = this.list.get(i);
						if(p == null){
							list.remove(i);
							i--;
						}
					}
				} catch (Exception e) {
				}
			}
			this.context = context;
			this.list.add(0, null);
			if(this.list.size() < PRODUCT_MAX){
				this.list.add(null);
			}
		}
		
		public ProductAdapter(Context context) {
			list = new ArrayList<Product>();
			list.add(null);
			list.add(null);
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Product getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private ArrayList<Product> getProducts(){
			ArrayList<Product> ret = (ArrayList<Product>) list.clone();
			if(ret.size() > 0){
				for(int i = 0; i < ret.size(); i++){
					if(ret.get(i) == null){
						ret.remove(i);
						i--;
					}
				}
			}
			return ret;
		}
		
		
		private void addProduct(Product p){
			if(list.size() == PRODUCT_MAX){
				list.remove(PRODUCT_MAX - 1);
				list.add(p);
			}else if(list.size() < PRODUCT_MAX){
				list.add(list.size() - 1, p);
			}
			
			refreshHeight();
			productAdapter.notifyDataSetChanged();
			product_list.setSelection(list.size() - 1);
		}
		
		private void removeProduct(Product p){
			if(list.size() == PRODUCT_MAX){
				list.remove(p);
				if(list.get(list.size() - 1) != null){
					list.add(null);
				}
			}else if(list.size() < PRODUCT_MAX){
				list.remove(p);
			}
			
			refreshHeight();
			productAdapter.notifyDataSetChanged();
			product_list.setSelection(list.size() - 1);
		}
		
		private void refreshHeight(){
			ViewGroup.LayoutParams params = product_list.getLayoutParams();
			int hdp = SalonTools.getProductHeight(list.size());
			params.height = DMUtil.getHeight(BarberRegisterActivity.this, hdp);
			product_list.setLayoutParams(params);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final Product p = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_product_list, null);
				viewHolder.band = (EditText) convertView.findViewById(R.id.band);
				viewHolder.usage = (EditText) convertView.findViewById(R.id.usage);
				viewHolder.price = (EditText) convertView.findViewById(R.id.price);
				viewHolder.origin = (EditText) convertView.findViewById(R.id.origin);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
				viewHolder.button = (RelativeLayout) convertView.findViewById(R.id.button);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			final ViewHolder vh2 = viewHolder;
			
			if(position == 0){
				vh2.button.setVisibility(View.INVISIBLE);
				vh2.band.setText(R.string.salon_reString20);
				vh2.usage.setText(R.string.salon_reString21);
				vh2.price.setText(R.string.salon_reString22);
				vh2.origin.setText(R.string.salon_reString42);
				vh2.band.setEnabled(false);
				vh2.usage.setEnabled(false);
				vh2.price.setEnabled(false);
				vh2.origin.setEnabled(false);
				vh2.band.setBackgroundColor(Color.TRANSPARENT);
				vh2.usage.setBackgroundColor(Color.TRANSPARENT);
				vh2.price.setBackgroundColor(Color.TRANSPARENT);
				vh2.origin.setBackgroundColor(Color.TRANSPARENT);
			}else{
				vh2.button.setVisibility(View.VISIBLE);
				vh2.band.setBackgroundColor(Color.WHITE);
				vh2.usage.setBackgroundColor(Color.WHITE);
				vh2.price.setBackgroundColor(Color.WHITE);
				vh2.origin.setBackgroundColor(Color.WHITE);
				vh2.band.setText(p == null ? null : p.band);
				vh2.usage.setText(p == null ? null : p.usage);
				vh2.price.setText(p == null ? null : p.price);
				vh2.origin.setText(p == null ? null : p.origin);
			}
			
			if(p == null && position != 0){
				vh2.band.setEnabled(true);
				vh2.usage.setEnabled(true);
				vh2.price.setEnabled(true);
				vh2.origin.setEnabled(true);
				vh2.img.setImageResource(R.drawable.add_product);
			}else{
				vh2.band.setEnabled(false);
				vh2.usage.setEnabled(false);
				vh2.price.setEnabled(false);
				vh2.origin.setEnabled(false);
				vh2.img.setImageResource(R.drawable.delete_product);
			}
			
			
			vh2.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(p == null){
						String band = vh2.band.getText().toString();
						String usage = vh2.usage.getText().toString();
						String price = vh2.price.getText().toString();
						String origin = vh2.origin.getText().toString();
						
						if(band != null && ! band.equals("") && usage != null && ! usage.equals("") 
								&& price != null && ! price.equals("") && origin != null && ! origin.equals("")){
							Product product = new Product(band, usage, price, origin);
							addProduct(product);
						}else{
							Toast.makeText(context, R.string.salon_reString25, Toast.LENGTH_SHORT).show();
						}
					}else{
						final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
						dialog.withTitle(R.string.dialog_title)
						.withMessage(R.string.salon_reString26)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								removeProduct(p);
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show();
					}
				}
			});
			
			return convertView;
		}
		
		final class ViewHolder{
			private EditText band;
			private EditText usage;
			private EditText price;
			private EditText origin;
			private ImageView img;
			private RelativeLayout button;
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
				
				if(requestCode == SELECT_DISTRICT){
					district.setText(data.getStringExtra("address"));
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
