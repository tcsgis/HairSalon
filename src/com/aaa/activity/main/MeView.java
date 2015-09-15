package com.aaa.activity.main;

import com.aaa.activity.admin.AdminAdActivity;
import com.aaa.activity.admin.AdminBarbersActivity;
import com.aaa.activity.admin.AdminMallActivity;
import com.aaa.activity.admin.AdminSalonsActivity;
import com.aaa.activity.barber.BarberBidActivity;
import com.aaa.activity.barber.BarberOrderActivity;
import com.aaa.activity.barber.BarberRegisterActivity;
import com.aaa.activity.barber.BarberSalonsActivity;
import com.aaa.activity.custom.CustomCouponActivity;
import com.aaa.activity.custom.CustomOrderActivity;
import com.aaa.activity.custom.CustomRegisterActivity;
import com.aaa.activity.login.LoginActivity;
import com.aaa.activity.login.RegisterActivity;
import com.aaa.activity.salon.SalonBarbersActivity;
import com.aaa.activity.salon.SalonOrderActivity;
import com.aaa.activity.salon.SalonRegisterActivity;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.aaa.util.Status;
import com.changhong.CHActivity;
import com.changhong.CHApplication;
import com.changhong.annotation.CHInjectView;
import com.changhong.service.msg.MessageType;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.bitmap.CHBitmapCallBackHanlder;
import com.changhong.util.bitmap.CHDownloadBitmapHandler;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeView  extends LinearLayout{

	@CHInjectView(id = R.id.btn_left)
	private Button mBtnLeft;
	@CHInjectView(id = R.id.txt_title)
	private TextView mTitle;
	@CHInjectView(id = R.id.register)
	private Button register;
	
	@CHInjectView(id = R.id.welcome)
	private TextView welcome;
	@CHInjectView(id = R.id.portrait)
	private ImageView portrait;
	@CHInjectView(id = R.id.txt1)
	private TextView txt1;
	@CHInjectView(id = R.id.setting)
	private Button setting;
	
	@CHInjectView(id = R.id.custom)
	private LinearLayout custom;
	@CHInjectView(id = R.id.custom_coupon_point)
	private ImageView custom_coupon_oint;
	@CHInjectView(id = R.id.custom_order_point)
	private ImageView custom_order_point;
	
	@CHInjectView(id = R.id.salon)
	private LinearLayout salon;
	@CHInjectView(id = R.id.salon_order_point)
	private ImageView salon_order_point;
	@CHInjectView(id = R.id.salon_barber_point)
	private ImageView salon_barber_point;
	
	@CHInjectView(id = R.id.barber)
	private LinearLayout barber;
	@CHInjectView(id = R.id.barber_bid_point)
	private ImageView barber_bid_point;
	@CHInjectView(id = R.id.barber_order_point)
	private ImageView barber_order_point;
	@CHInjectView(id = R.id.barber_salon_point)
	private ImageView barber_salon_point;
	
	@CHInjectView(id = R.id.admin)
	private LinearLayout admin;
	@CHInjectView(id = R.id.verify_barber_point)
	private ImageView verify_barber_point;
	@CHInjectView(id = R.id.verify_salon_point)
	private ImageView verify_salon_point;
	
	private Context mContext = null;
	private CHBitmapCacheWork imageFetcher;
	private int role = Role.UNDIFINED;

	public MeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.me_view, this, true);
	}
	
	public void initView(CHActivity activity, CHApplication app){
		mBtnLeft.setVisibility(View.GONE);
		mTitle.setText(R.string.me_title);
		
		imageFetcher = new CHBitmapCacheWork(mContext);
		CHBitmapCallBackHanlder taBitmapCallBackHanlder = new CHBitmapCallBackHanlder();
		taBitmapCallBackHanlder.setCircleParams(true);
		taBitmapCallBackHanlder
				.setLoadingImage(mContext, R.drawable.face_bg);
		
		Bitmap loading = taBitmapCallBackHanlder.getmLoadingBitmap();
		if(loading != null){
			int width = taBitmapCallBackHanlder.getmLoadingBitmap().getWidth();
			int height = taBitmapCallBackHanlder.getmLoadingBitmap().getHeight();
			CHDownloadBitmapHandler downloadBitmapFetcher = new CHDownloadBitmapHandler(
					mContext, width, height);
			imageFetcher.setProcessDataHandler(downloadBitmapFetcher);
		}
		
		imageFetcher.setCallBackHandler(taBitmapCallBackHanlder);
		imageFetcher.setFileCache(app.getFileCache());
	}

	public void refresh(){
		if(CacheManager.INSTANCE.getCurrentUser() == null
				|| CacheManager.INSTANCE.getCurrentUser().getRole() == Role.UNDIFINED){
			reset();
		}else{
			role = CacheManager.INSTANCE.getCurrentUser().getRole();
			welcome.setVisibility(View.VISIBLE);
			welcome.setText(mContext.getResources().getString(R.string.me_string1, SalonTools.getName()));
			txt1.setText(R.string.me_string13);
			register.setVisibility(View.GONE);
			setting.setVisibility(View.VISIBLE);
			setting.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SettingActivity.class);
					mContext.startActivity(intent);
				}
			});
			
			switch (role) {
			case Role.CUSTOM:
				custom.setVisibility(View.VISIBLE);
				barber.setVisibility(View.GONE);
				salon.setVisibility(View.GONE);
				admin.setVisibility(View.GONE);
				initCustom();
				break;
				
			case Role.BARBER:
				barber.setVisibility(View.VISIBLE);
				custom.setVisibility(View.GONE);
				salon.setVisibility(View.GONE);
				admin.setVisibility(View.GONE);
				initBarber();
				break;
				
			case Role.SALON:
				salon.setVisibility(View.VISIBLE);
				barber.setVisibility(View.GONE);
				custom.setVisibility(View.GONE);
				admin.setVisibility(View.GONE);
				initSalon();
				break;
				
			case Role.ADMIN:
				admin.setVisibility(View.VISIBLE);
				custom.setVisibility(View.GONE);
				salon.setVisibility(View.GONE);
				barber.setVisibility(View.GONE);
				initAdmin();
				break;
				
				default:
					reset();
					break;
			}
		}
	}
	
	public void handleMsg(Message msg){
		switch (msg.what) {
		case MessageType.ADMIN_NEW_BARBER:
			verify_barber_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.ADMIN_NEW_SALON:
			verify_salon_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.CUSTOM_ORDER_ACCEPT:
		case MessageType.CUSTOM_ORDER_DENY:
			custom_order_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.CUSTOM_NEW_COUPON:
			custom_coupon_oint.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.BARBER_REGISTER_PASS:
			//todo 更新User审核状态值
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.NORMAL);
			initBarber();
			break;
			
		case MessageType.BARBER_REGISTER_DENY:
			//todo 更新User审核状态值
			deny();
			initBarber();
			break;
			
		case MessageType.BARBER_MODIFY_PASS:
			//todo 更新User审核状态值
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.NORMAL);
			initBarber();
			break;
			
		case MessageType.BARBER_MODIFY_DENY:
			//todo 更新User审核状态值
			deny();
			initBarber();
			break;
			
		case MessageType.BARBER_BID_SUCCESS:
		case MessageType.BARBER_BID_FAIL:
			barber_bid_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.BARBER_NEW_ORDER:
		case MessageType.BARBER_ORDER_ACCEPT:
		case MessageType.BARBER_ORDER_DENY:
		case MessageType.BARBER_ORDER_CANCEL:
		case MessageType.BARBER_ORDER_DONE:
		case MessageType.BARBER_ORDER_SHARE:
			barber_order_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.BARBER_SALON_LOSS:
			barber_salon_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.SALON_REGISTER_PASS:
			//todo 更新User审核状态值
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.NORMAL);
			initSalon();
			break;
			
		case MessageType.SALON_REGISTER_DENY:
			//todo 更新User审核状态值
			deny();
			initSalon();
			break;
			
		case MessageType.SALON_MODIFY_PASS:
			//todo 更新User审核状态值
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.NORMAL);
			initSalon();
			break;
			
		case MessageType.SALON_MODIFY_DENY:
			//todo 更新User审核状态值
			deny();
			initSalon();
			break;
			
		case MessageType.SALON_NEW_ORDER:
		case MessageType.SALON_ORDER_ACCEPT:
		case MessageType.SALON_ORDER_DENY:
		case MessageType.SALON_ORDER_CANCEL:
		case MessageType.SALON_ORDER_DONE:
		case MessageType.SALON_ORDER_SHARE:
			salon_order_point.setVisibility(View.VISIBLE);
			break;
			
		case MessageType.SALON_BARBER_ADD:
		case MessageType.SALON_BARBER_LOSS:
			salon_barber_point.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	private void initCustom() {
		try {
			imageFetcher.loadFormCache(CacheManager.INSTANCE.getCurrentUser().getPhoto(), portrait);
		} catch (Exception e) {
			e.printStackTrace();
		}
		txt1.setOnClickListener(clickCustom);
		findViewById(R.id.custom_order).setOnClickListener(clickCustom);
		findViewById(R.id.custom_coupon).setOnClickListener(clickCustom);
	}
	
	private OnClickListener clickCustom = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.txt1:
				intent = new Intent(mContext, CustomRegisterActivity.class);
				break;
				
			case R.id.custom_order:
				custom_order_point.setVisibility(INVISIBLE);
				intent = new Intent(mContext, CustomOrderActivity.class);
				break;
				
			case R.id.custom_coupon:
				custom_coupon_oint.setVisibility(INVISIBLE);
				intent = new Intent(mContext, CustomCouponActivity.class);
				break;
			}
			if(intent != null){
				mContext.startActivity(intent);
			}
		}
	};

	private void initBarber() {
		try {
			imageFetcher.loadFormCache(CacheManager.INSTANCE.getCurrentUser().getPhoto(), portrait);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//txt1根据审核状态更改
		txt1.setOnClickListener(clickBarber);
		setTxt1(CacheManager.INSTANCE.getCurrentUser().getStatus());
		findViewById(R.id.barber_bid).setOnClickListener(clickBarber);
		findViewById(R.id.barber_order).setOnClickListener(clickBarber);
		findViewById(R.id.barber_salon).setOnClickListener(clickBarber);
	}

	private OnClickListener clickBarber = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.txt1:
				intent = new Intent(mContext, BarberRegisterActivity.class);
				break;
					
			case R.id.barber_bid:
				barber_bid_point.setVisibility(INVISIBLE);
				if(clickValid()){
					intent = new Intent(mContext, BarberBidActivity.class);
				}
				break;
				
			case R.id.barber_order:
				barber_order_point.setVisibility(INVISIBLE);
				if(clickValid()){
					intent = new Intent(mContext, BarberOrderActivity.class);
				}
				break;
				
			case R.id.barber_salon:
				barber_salon_point.setVisibility(INVISIBLE);
				if(clickValid()){
					intent = new Intent(mContext, BarberSalonsActivity.class);
				}
				break;
			}
			if(intent != null){
				mContext.startActivity(intent);
			}
		}
	};
	
	private void initSalon() {
		try {
			imageFetcher.loadFormCache(CacheManager.INSTANCE.getCurrentUser().getPhotos().get(0), portrait);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//txt1根据审核状态更改
		txt1.setOnClickListener(clickSalon);
		setTxt1(CacheManager.INSTANCE.getCurrentUser().getStatus());
		findViewById(R.id.salon_order).setOnClickListener(clickSalon);
		findViewById(R.id.salon_barber).setOnClickListener(clickSalon);
	}

	private OnClickListener clickSalon = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.txt1:
				intent = new Intent(mContext, SalonRegisterActivity.class);
				break;
				
			case R.id.salon_order:
				salon_order_point.setVisibility(INVISIBLE);
				if(clickValid()){
					intent = new Intent(mContext, SalonOrderActivity.class);
				}
				break;
				
			case R.id.salon_barber:
				salon_barber_point.setVisibility(INVISIBLE);
				if(clickValid()){
					intent = new Intent(mContext, SalonBarbersActivity.class);
				}
				break;
			}
			if(intent != null){
				mContext.startActivity(intent);
			}
		}
	};
	
	private void initAdmin() {
		txt1.setVisibility(View.INVISIBLE);
		findViewById(R.id.verify_barber).setOnClickListener(clickAdmin);
		findViewById(R.id.verify_salon).setOnClickListener(clickAdmin);
		findViewById(R.id.upload_mall_pic).setOnClickListener(clickAdmin);
		findViewById(R.id.upload_ad_pic).setOnClickListener(clickAdmin);
	}

	private OnClickListener clickAdmin = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.verify_barber:
				verify_barber_point.setVisibility(INVISIBLE);
				intent = new Intent(mContext, AdminBarbersActivity.class);
				break;
				
			case R.id.verify_salon:
				verify_salon_point.setVisibility(INVISIBLE);
				intent = new Intent(mContext, AdminSalonsActivity.class);
				break;
				
			case R.id.upload_mall_pic:
				intent = new Intent(mContext, AdminMallActivity.class);
				break;
				
			case R.id.upload_ad_pic:
				intent = new Intent(mContext, AdminAdActivity.class);
				break;
			}
			if(intent != null){
				mContext.startActivity(intent);
			}
		}
	};
	
	public void reset(){
		portrait.setImageResource(R.drawable.face_bg);
		custom.setVisibility(View.GONE);
		barber.setVisibility(View.GONE);
		salon.setVisibility(View.GONE);
		admin.setVisibility(View.GONE);
		welcome.setVisibility(View.GONE);
		setting.setVisibility(View.GONE);
		
		custom_coupon_oint.setVisibility(View.INVISIBLE);
		custom_order_point.setVisibility(View.INVISIBLE);
		salon_order_point.setVisibility(View.INVISIBLE);
		salon_barber_point.setVisibility(View.INVISIBLE);
		barber_bid_point.setVisibility(View.INVISIBLE);
		barber_order_point.setVisibility(View.INVISIBLE);
		barber_salon_point.setVisibility(View.INVISIBLE);
		verify_barber_point.setVisibility(View.INVISIBLE);
		verify_salon_point.setVisibility(View.INVISIBLE);
		
		txt1.setText(R.string.me_string2);
		txt1.setVisibility(View.VISIBLE);
		txt1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		register.setVisibility(View.VISIBLE);
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, RegisterActivity.class);
				intent.putExtra("applyType", RegisterActivity.REGISTER);
				mContext.startActivity(intent);
			}
		});
	}
	
	private void setTxt1(byte status){
		if(status == Status.NORMAL){
			txt1.setText(R.string.me_string13);
			txt1.setClickable(true);
			txt1.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		else if(status == Status.VERIFYING){
			txt1.setText(R.string.me_string18);
			txt1.setClickable(false);
			txt1.setTextColor(mContext.getResources().getColor(R.color.gray_txt));
		}
		else if(status == Status.ELSE){
			txt1.setText(R.string.me_string19);
			txt1.setClickable(true);
			txt1.setTextColor(mContext.getResources().getColor(R.color.black));
		}
	}
	
	private void deny(){
		if(CacheManager.INSTANCE.getCurrentUser().getLevel() < 1
				|| CacheManager.INSTANCE.getCurrentUser().getLevel() > 5){
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.ELSE);
		}else{
			CacheManager.INSTANCE.getCurrentUser().setStatus(Status.NORMAL);
		}
	}
	
	private boolean clickValid(){
		boolean ret = true;
		if(CacheManager.INSTANCE.getCurrentUser().getStatus() == Status.ELSE){
			Toast.makeText(mContext, R.string.me_string20, Toast.LENGTH_SHORT).show();
			ret = false;
		}
		else if(CacheManager.INSTANCE.getCurrentUser().getStatus() == Status.VERIFYING){
			Toast.makeText(mContext, R.string.me_string21, Toast.LENGTH_SHORT).show();
			ret = false;
		}
		return ret;
	}
}
