package com.aaa.activity.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.bean.VerInfo;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.activity.login.UpdateManager;
import com.aaa.activity.salon.SalonActivity;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.activity.BaseActivity;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.config.MyProperties;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MainView  extends LinearLayout implements OnClickListener{
	
	@CHInjectView(id = R.id.txt_area)
	public TextView mArea;
//	@CHInjectView(id = R.id.edit_search)
//	public EditText mSearch;
//	@CHInjectView(id = R.id.btn_search)
//	public ImageView mBtnSearch;
	
	@CHInjectView(id = R.id.salon1)
	private ImageView salon1;
	@CHInjectView(id = R.id.salon2)
	private ImageView salon2;
	@CHInjectView(id = R.id.barber1)
	private ImageView barber1;
	@CHInjectView(id = R.id.barber2)
	private ImageView barber2;
	@CHInjectView(id = R.id.barber3)
	private ImageView barber3;
	
	@CHInjectView(id = R.id.none_mall)
	private TextView none_mall;
	@CHInjectView(id = R.id.none_ad)
	private TextView none_ad;
	@CHInjectView(id = R.id.none_salon)
	private TextView none_salon;
	@CHInjectView(id = R.id.none_barber)
	private TextView none_barber;
	@CHInjectView(id = R.id.salon1_txt)
	private TextView salon1_txt;
	@CHInjectView(id = R.id.salon2_txt)
	private TextView salon2_txt;
	@CHInjectView(id = R.id.baber1_txt)
	private TextView baber1_txt;
	@CHInjectView(id = R.id.baber2_txt)
	private TextView baber2_txt;
	@CHInjectView(id = R.id.baber3_txt)
	private TextView baber3_txt;
	
	@CHInjectView(id = R.id.ad_pager)
	private ViewPager ad_pager;
	@CHInjectView(id = R.id.ad_indicators)
	private LinearLayout ad_indicators;
	@CHInjectView(id = R.id.mall_pager)
	private ViewPager mall_pager;
	@CHInjectView(id = R.id.mall_indicators)
	private LinearLayout mall_indicators;
	
	private static final String MALL_WEB = "http://www.goodkor.com/";
	
	private final int Indicator_unselect = R.drawable.unonline;
	private final int Indicator_select = R.drawable.online;
	
	private Context mContext = null;
	private BaseActivity mActivity;
	private CHBitmapCacheWork imageFetcher;
	private CHBitmapCacheWork imageFetcherRound;

	private ImageView[] imageViewsAd = null; 
	private ImageView[] imageViewsMall = null; 
	private ImageView imageView = null; 
	private AtomicInteger whatAd = new AtomicInteger(0); 
	private AtomicInteger whatMall = new AtomicInteger(0); 
	private boolean isContinueAd = true; 
	private boolean isContinueMall = true; 
	private ArrayList<SalonUser> salons = new ArrayList<SalonUser>();
	private ArrayList<SalonUser> barbers = new ArrayList<SalonUser>();
	private ArrayList<BannerPic> banners = new ArrayList<BannerPic>();
	private ArrayList<ImageView> mallPics = new ArrayList<ImageView>();
	private ArrayList<ImageView> advPics = new ArrayList<ImageView>();
	private int backcolor;
	private String lastArea = null;
	private Handler mainHandler;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.main_view, this, true);
	}
	
	public void initView(BaseActivity activity, CHApplication app, Handler handler){
		mActivity = activity;
		mainHandler = handler;
		
		findViewById(R.id.title_search).setVisibility(View.GONE);
//		mSearch.setOnClickListener(this);
//		mBtnSearch.setOnClickListener(this);
		salon1.setOnClickListener(this);
		salon2.setOnClickListener(this);
		barber1.setOnClickListener(this);
		barber2.setOnClickListener(this);
		barber3.setOnClickListener(this);
		mArea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DistrictActivity.class);
				intent.putExtra("address", SalonTools.getDistrict(mActivity));//不传参数也可以，就不会有默认选中某个值
				mActivity.startActivityForResult(intent, MainActivity.SELECT_DISTRICT);
			}
		});
		
		findViewById(R.id.rl_salon).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					mainHandler.sendEmptyMessage(R.string.action_show_salon_view);
				} catch (Exception e) {
				}
			}
		});
		
		findViewById(R.id.rl_barber).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					mainHandler.sendEmptyMessage(R.string.action_show_barber_view);
				} catch (Exception e) {
				}
			}
		});
		
		mArea.setText(SalonTools.getArea(mActivity));
		imageFetcher = SalonTools.getImageFetcher(mContext, app, false, 0);
		imageFetcherRound = SalonTools.getImageFetcher(mContext, app, true, 0);
		
		backcolor = getResources().getColor(R.color.activity_back);
		
		doGetBannerUsers();
	}

	private void doGetBannerUsers() {
		lastArea = SalonTools.getArea(mActivity);
		mActivity.showWaitDialog();
		accountService.getBannerUsers(SalonTools.getArea(mActivity), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				
				salons.clear();
				barbers.clear();
				
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<SalonUser> users = (ArrayList<SalonUser>) response.getData();
					for(SalonUser user : users){
						if(user.getRole() == Role.SALON){
							salons.add(user);
						}
						else if(user.getRole() == Role.BARBER){
							barbers.add(user);
						}
					}
				}
				
				initSalons();
				initBarbers();
				doGetBanners();
				return null;
			}
		});
	}
	
	private void doGetBanners() {
		mActivity.showWaitDialog();
		accountService.getBanners(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					banners = (ArrayList<BannerPic>) response.getData();
				}
				mActivity.hideAllDialog();
				initAdViewPager();
				initMallViewPager();
				checkUpdate();
				return null;
			}
		});
	}
	
	private void checkUpdate(){
		if(CacheManager.INSTANCE.getCurrentUser() != null 
				&& CacheManager.INSTANCE.getCurrentUser().getRole() != Role.UNDIFINED
				&& mActivity.getCHApplication().getPreferenceConfig().
				getBoolean(MyProperties.getMyProperties().getRemindUpdateKey(), true)){
			
			VerInfo ver = (VerInfo) banners.get(2);
			UpdateManager um = new UpdateManager(mContext);
			um.showUpdateDialog(ver, true);
		}
	}
	
	private void initSalons() {
		//设置没有salon时的文字
//		if(salons.size() == 0){
//			none_salon.setVisibility(VISIBLE);
//		}else{
//			none_salon.setVisibility(GONE);
//		}
		
		if(salons.size() == 0){
			salon1.setImageDrawable(null);
			salon2.setImageDrawable(null);
			salon1_txt.setVisibility(INVISIBLE);
			salon2_txt.setVisibility(INVISIBLE);
		}
		else if(salons.size() == 1){
			try {
				imageFetcher.loadFormCache(salons.get(0).getPhotos().get(0), salon1);
				salon2.setImageDrawable(null);
				salon1_txt.setVisibility(VISIBLE);
				salon2_txt.setVisibility(INVISIBLE);
				salon1_txt.setText(salons.get(0).getNick());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(salons.size() == 2){
			try {
				imageFetcher.loadFormCache(salons.get(0).getPhotos().get(0), salon1);
				imageFetcher.loadFormCache(salons.get(1).getPhotos().get(0), salon2);
				salon1_txt.setVisibility(VISIBLE);
				salon2_txt.setVisibility(VISIBLE);
				salon1_txt.setText(salons.get(0).getNick());
				salon2_txt.setText(salons.get(1).getNick());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initBarbers() {
		//设置没有barber时的文字
//		if(barbers.size() == 0){
//			none_barber.setVisibility(VISIBLE);
//		}else{
//			none_barber.setVisibility(GONE);
//		}
		
		if(barbers.size() == 0){
			barber1.setImageDrawable(null);
			barber2.setImageDrawable(null);
			barber3.setImageDrawable(null);
			baber1_txt.setVisibility(INVISIBLE);
			baber2_txt.setVisibility(INVISIBLE);
			baber3_txt.setVisibility(INVISIBLE);
		}
		else if(barbers.size() == 1){
			try {
				imageFetcher.loadFormCache(barbers.get(0).getPhoto(), barber1);
				baber1_txt.setVisibility(VISIBLE);
				baber1_txt.setText(SalonTools.getName(barbers.get(0)));
				barber2.setImageDrawable(null);
				barber3.setImageDrawable(null);
				baber2_txt.setVisibility(INVISIBLE);
				baber3_txt.setVisibility(INVISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(barbers.size() == 2){
			try {
				imageFetcher.loadFormCache(barbers.get(0).getPhoto(), barber1);
				baber1_txt.setVisibility(VISIBLE);
				baber1_txt.setText(SalonTools.getName(barbers.get(0)));
				imageFetcher.loadFormCache(barbers.get(1).getPhoto(), barber2);
				baber2_txt.setVisibility(VISIBLE);
				baber2_txt.setText(SalonTools.getName(barbers.get(1)));
				barber3.setImageDrawable(null);
				baber3_txt.setVisibility(INVISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(barbers.size() == 3){
			try {
				imageFetcher.loadFormCache(barbers.get(0).getPhoto(), barber1);
				baber1_txt.setVisibility(VISIBLE);
				baber1_txt.setText(SalonTools.getName(barbers.get(0)));
				imageFetcher.loadFormCache(barbers.get(1).getPhoto(), barber2);
				baber2_txt.setVisibility(VISIBLE);
				baber2_txt.setText(SalonTools.getName(barbers.get(1)));
				imageFetcher.loadFormCache(barbers.get(2).getPhoto(), barber3);
				baber3_txt.setVisibility(VISIBLE);
				baber3_txt.setText(SalonTools.getName(barbers.get(2)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void refresh(){
		mArea.setText(SalonTools.getArea(mActivity));
		if(! lastArea.endsWith(SalonTools.getArea(mActivity))){
			doGetBannerUsers();
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.salon1:
			if(salons.size() > 0){
				intent = new Intent(mContext, SalonActivity.class);
				intent.putExtra(SalonActivity.SALON, salons.get(0));
			}
			break;
			
		case R.id.salon2:
			if(salons.size() > 1){
				intent = new Intent(mContext, SalonActivity.class);
				intent.putExtra(SalonActivity.SALON, salons.get(1));
			}
			break;
			
		case R.id.barber1:
			if(barbers.size() > 0){
				intent = new Intent(mContext, BarberActivity.class);
				intent.putExtra(BarberActivity.BARBER, barbers.get(0));
				intent.putExtra(BarberActivity.FREE_BARBER, true);
			}
			break;
			
		case R.id.barber2:
			if(barbers.size() > 1){
				intent = new Intent(mContext, BarberActivity.class);
				intent.putExtra(BarberActivity.BARBER, barbers.get(1));
				intent.putExtra(BarberActivity.FREE_BARBER, true);
			}
			break;
			
		case R.id.barber3:
			if(barbers.size() > 2){
				intent = new Intent(mContext, BarberActivity.class);
				intent.putExtra(BarberActivity.BARBER, barbers.get(2));
				intent.putExtra(BarberActivity.FREE_BARBER, true);
			}
			break;
		}
		if(intent != null){
			mContext.startActivity(intent);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == MainActivity.SELECT_DISTRICT){
				SalonTools.saveDistrict(mActivity, data.getStringExtra("address"));
				mArea.setText(SalonTools.getArea(mActivity));
				if(! lastArea.equals(SalonTools.getArea(mActivity))){
					doGetBannerUsers();
				}
			}
		}
	}

	private void initMallViewPager(){
		ArrayList<String> photos = banners.get(1).getPhotos();
		
		if(photos.size() == 0){
			none_mall.setVisibility(VISIBLE);
			return;
		}
		
		none_mall.setVisibility(GONE);
		
		mallPics.clear();
		for(int i = 0; i < photos.size(); i++){
			ImageView img = new ImageView(mContext); 
			img.setBackgroundColor(backcolor);
			mallPics.add(img); 
			
			try {
				imageFetcher.loadFormCache(photos.get(i), mallPics.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < mallPics.size(); i++){
			mallPics.get(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Uri uri = Uri.parse(MALL_WEB);          
					final Intent it = new Intent(Intent.ACTION_VIEW, uri);          
					mContext.startActivity(it);
				}
			});
		}
		
		imageViewsMall = new ImageView[mallPics.size()]; 
		//小图标 
		for (int i = 0; i < mallPics.size(); i++) { 
			imageView = new ImageView(mContext); 
			imageView.setLayoutParams(new LayoutParams(20, 20)); 
			imageView.setPadding(5, 5, 5, 5); 
			imageViewsMall[i] = imageView; 
			if (i == 0) { 
				imageViewsMall[i].setBackgroundResource(Indicator_select); 
			} else { 
				imageViewsMall[i].setBackgroundResource(Indicator_unselect); 
			} 
			mall_indicators.addView(imageViewsMall[i]); 
		} 
		
		mall_pager.setAdapter(new AdvAdapter(mallPics)); 
		mall_pager.setOnPageChangeListener(new GuidePageChangeListenerMall()); 
		mall_pager.setOnTouchListener(new OnTouchListener() { 
			
			@Override 
			public boolean onTouch(View v, MotionEvent event) { 
				switch (event.getAction()) { 
				case MotionEvent.ACTION_DOWN: 
				case MotionEvent.ACTION_MOVE: 
					isContinueMall = false; 
					break; 
				case MotionEvent.ACTION_UP: 
					isContinueMall = true; 
					break; 
				default: 
					isContinueMall = true; 
					break; 
				} 
				return false; 
			} 
		}); 
		new Thread(new Runnable() { 
			
			@Override 
			public void run() { 
				while (true) { 
					if (isContinueMall) { 
						viewHandlerMall.sendEmptyMessage(whatMall.get()); 
						whatOptionMall(); 
					} 
				} 
			} 
			
		}).start(); 
	}
	
	private void initAdViewPager() { 
		ArrayList<String> photos = banners.get(0).getPhotos();
		
		if(photos.size() == 0){
			none_ad.setVisibility(VISIBLE);
			return;
		}
		
		none_ad.setVisibility(GONE);
		
		advPics.clear();
		for(int i = 0; i < photos.size(); i++){
			ImageView img = new ImageView(mContext); 
			img.setBackgroundColor(backcolor);
			advPics.add(img); 
			
			try {
				imageFetcher.loadFormCache(photos.get(i), advPics.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//	      对imageviews进行填充 
		imageViewsAd = new ImageView[advPics.size()]; 
		//小图标 
		for (int i = 0; i < advPics.size(); i++) { 
			imageView = new ImageView(mContext); 
			imageView.setLayoutParams(new LayoutParams(20, 20)); 
			imageView.setPadding(5, 5, 5, 5); 
			imageViewsAd[i] = imageView; 
			if (i == 0) { 
				imageViewsAd[i].setBackgroundResource(Indicator_select); 
			} else { 
				imageViewsAd[i].setBackgroundResource(Indicator_unselect); 
			} 
			ad_indicators.addView(imageViewsAd[i]); 
		} 
		
		ad_pager.setAdapter(new AdvAdapter(advPics)); 
		ad_pager.setOnPageChangeListener(new GuidePageChangeListenerAd()); 
		ad_pager.setOnTouchListener(new OnTouchListener() { 
			
			@Override 
			public boolean onTouch(View v, MotionEvent event) { 
				switch (event.getAction()) { 
				case MotionEvent.ACTION_DOWN: 
				case MotionEvent.ACTION_MOVE: 
					isContinueAd = false; 
					break; 
				case MotionEvent.ACTION_UP: 
					isContinueAd = true; 
					break; 
				default: 
					isContinueAd = true; 
					break; 
				} 
				return false; 
			} 
		}); 
		new Thread(new Runnable() { 
			
			@Override 
			public void run() { 
				while (true) { 
					if (isContinueAd) { 
						viewHandlerAd.sendEmptyMessage(whatAd.get()); 
						whatOptionAd(); 
					} 
				} 
			} 
			
		}).start(); 
	} 
	
	
	private void whatOptionAd() { 
		whatAd.incrementAndGet(); 
		if (whatAd.get() > imageViewsAd.length - 1) { 
			whatAd.getAndAdd(-4); 
		} 
		try { 
			Thread.sleep(2500); 
		} catch (InterruptedException e) { 
			
		} 
	} 
	
	private void whatOptionMall() { 
		whatMall.incrementAndGet(); 
		if (whatMall.get() > imageViewsMall.length - 1) { 
			whatMall.getAndAdd(-4); 
		} 
		try { 
			Thread.sleep(2500); 
		} catch (InterruptedException e) { 
			
		} 
	} 
	
	private final Handler viewHandlerAd = new Handler() { 
		
		@Override 
		public void handleMessage(Message msg) { 
			ad_pager.setCurrentItem(msg.what); 
			super.handleMessage(msg); 
		} 
		
	}; 
	
	private final Handler viewHandlerMall = new Handler() { 
		
		@Override 
		public void handleMessage(Message msg) { 
			mall_pager.setCurrentItem(msg.what); 
			super.handleMessage(msg); 
		} 
		
	}; 
	
	private final class GuidePageChangeListenerAd implements OnPageChangeListener { 
		
		@Override 
		public void onPageScrollStateChanged(int arg0) { 
			
		} 
		
		@Override 
		public void onPageScrolled(int arg0, float arg1, int arg2) { 
			
		} 
		
		@Override 
		public void onPageSelected(int arg0) { 
			whatAd.getAndSet(arg0); 
			for (int i = 0; i < imageViewsAd.length; i++) { 
				imageViewsAd[arg0].setBackgroundResource(Indicator_select); 
				if (arg0 != i) { 
					imageViewsAd[i].setBackgroundResource(Indicator_unselect); 
				} 
			} 
			
		} 
	} 
	
	private final class GuidePageChangeListenerMall implements OnPageChangeListener { 
		
		@Override 
		public void onPageScrollStateChanged(int arg0) { 
			
		} 
		
		@Override 
		public void onPageScrolled(int arg0, float arg1, int arg2) { 
			
		} 
		
		@Override 
		public void onPageSelected(int arg0) { 
			whatMall.getAndSet(arg0); 
			for (int i = 0; i < imageViewsMall.length; i++) { 
				imageViewsMall[arg0].setBackgroundResource(Indicator_select); 
				if (arg0 != i) { 
					imageViewsMall[i].setBackgroundResource(Indicator_unselect); 
				} 
			} 
			
		} 
	} 
	
	private final class AdvAdapter extends PagerAdapter { 
		private List<ImageView> views = null; 
		
		public AdvAdapter(List<ImageView> views) { 
			this.views = views; 
		} 
		
		@Override 
		public void destroyItem(View arg0, int arg1, Object arg2) { 
			((ViewPager) arg0).removeView(views.get(arg1)); 
		} 
		
		@Override 
		public void finishUpdate(View arg0) { 
			
		} 
		
		@Override 
		public int getCount() { 
			return views.size(); 
		} 
		
		@Override 
		public Object instantiateItem(View arg0, int arg1) { 
			((ViewPager) arg0).addView(views.get(arg1), 0); 
			return views.get(arg1); 
		} 
		
		@Override 
		public boolean isViewFromObject(View arg0, Object arg1) { 
			return arg0 == arg1; 
		} 
		
		@Override 
		public void restoreState(Parcelable arg0, ClassLoader arg1) { 
			
		} 
		
		@Override 
		public Parcelable saveState() { 
			return null; 
		} 
		
		@Override 
		public void startUpdate(View arg0) { 
			
		} 
		
	} 
}
