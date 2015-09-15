package com.aaa.activity.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.aaa.activity.custom.CustomOrderActivity;
import com.aaa.activity.main.SalonView;
import com.aaa.activity.main.SettingActivity.onLogOutLisener;
import com.aaa.util.Action;
import com.changhong.activity.BaseActivity;
import com.changhong.activity.util.PollingUtils;
import com.changhong.annotation.CHInjectView;
import com.changhong.service.PollingService;
import com.changhong.service.msg.MessageType;
import com.changhong.service.task.receiver.AlarmMessageReceiver;
import com.llw.salon.R;
import com.llw.salon.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends BaseActivity implements OnClickListener, onLogOutLisener{
	
	@CHInjectView(id = R.id.btn_main)
	private ImageButton mBtnMain;
	@CHInjectView(id = R.id.btn_salon)
	private ImageButton mBtnSalon;
	@CHInjectView(id = R.id.btn_barber)
	private ImageButton mBtnBarber;
	@CHInjectView(id = R.id.btn_bid)
	private ImageButton mBtnBid;
	@CHInjectView(id = R.id.btn_me)
	private ImageButton mBtnMe;
	
	@CHInjectView(id = R.id.img_salon_point)
	private ImageView mPointSalon;
	@CHInjectView(id = R.id.img_barber_point)
	private ImageView mPointBarber;
	@CHInjectView(id = R.id.img_bid_point)
	private ImageView mPointBid;
	@CHInjectView(id = R.id.img_me_point)
	private ImageView mPointMe;
	
	@CHInjectView(id = R.id.top_vf)
	private ViewFlipper mViewFlipper;
	
	@CHInjectView(id = R.id.main_view)
	private MainView mMainView;
	@CHInjectView(id = R.id.salon_view)
	private SalonView mSalonView;
	@CHInjectView(id = R.id.barber_view)
	private BarberView mBarberView;
	@CHInjectView(id = R.id.bid_view)
	private BidView mBidView;
	@CHInjectView(id = R.id.me_view)
	private MeView mMeView;
	
	public static final int SELECT_DISTRICT = 17601;
	
	private final static int PAGE_MAIN = 0;
	private final static int PAGE_SALON = 1;
	private final static int PAGE_BARBER = 2;
	private final static int PAGE_BID = 3;
	private final static int PAGE_ME = 4;
	
	private List<ImageButton> mBtnGrounp = new ArrayList<ImageButton>();
	private int mPage = 0;
	private MyHandler handler = new MyHandler();
	private IWXAPI api;
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		initView();
		doBindService();
		
		api = WXAPIFactory.createWXAPI(this, WXEntryActivity.APP_ID);
		api.registerApp(WXEntryActivity.APP_ID);
		
		SettingActivity.registerListener(this);
	}
	
	private void initView() {
		mMainView.initView(this, getCHApplication(), handler);
		mSalonView.initView(this, getCHApplication());
		mBarberView.initView(this, getCHApplication());
		mMeView.initView(this, getCHApplication());
		mBidView.initView(this, getCHApplication());
		
		mBtnMain.setOnClickListener(this);
		mBtnSalon.setOnClickListener(this);
		mBtnBarber.setOnClickListener(this);
		mBtnBid.setOnClickListener(this);
		mBtnMe.setOnClickListener(this);
		
		mBtnGrounp.add(mBtnMain);
		mBtnGrounp.add(mBtnSalon);
		mBtnGrounp.add(mBtnBarber);
		mBtnGrounp.add(mBtnBid);
		mBtnGrounp.add(mBtnMe);
		
		mBtnMain.setSelected(true);
	}
	
	private void doBindService(){
		try {
			
			AlarmMessageReceiver.getInstance().setmMainHandler(handler);
			PollingUtils.startPollingService(MainActivity.this, 60, PollingService.class, getString(R.string.AlarmAnniServiceNotify));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mMeView.refresh();
		handleAction(getIntent().getAction());
	}
	
	private void handleAction(String action) {
		if(action == null) return;
		
		if(action.equals(Action.createOrder)){
			Intent intent = new Intent(MainActivity.this, CustomOrderActivity.class);
			startActivity(intent);
			getIntent().setAction(null);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt("page", mPage);
		super.onSaveInstanceState(state);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mPage = savedInstanceState.getInt("page");
		if (mPage != 0) {
			switch (mPage) {
			case PAGE_BARBER:
				showBarberView();
				break;
			case PAGE_SALON:
				showSalonView();
				break;
			case PAGE_BID:
				showBidView();
				break;
			case PAGE_ME:
				showMeView();
				break;
			}
		}
	}
	
	private class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			System.out.println("MyHandler===msg.what==>>>>" + msg.what);
			
			if(msg.what == R.string.action_show_salon_view){
				showSalonView();
			}
			else if(msg.what == R.string.action_show_barber_view){
				showBarberView();
			}
			else if(msg.what != MessageType.CUSTOM_NEW_BID){
				if(mViewFlipper.getDisplayedChild() != PAGE_ME){
					mPointMe.setVisibility(View.VISIBLE);
				}
				mMeView.handleMsg(msg);
			}else{
//				mBidView.getBidBarbers();
				if(mViewFlipper.getDisplayedChild() == PAGE_BID){
					mBidView.refresh();
				}else{
					mPointBid.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main:
			showMainView();
			break;
			
		case R.id.btn_salon:
			showSalonView();
			break;
			
		case R.id.btn_barber:
			showBarberView();
			break;
			
		case R.id.btn_bid:
			showBidView();
			break;
			
		case R.id.btn_me:
			showMeView();
			break;
			
		}
	}

	private void showMainView() {
		try {
			mPage = PAGE_MAIN;
			changePage(PAGE_MAIN);
			mMainView.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSalonView() {
		try {
			mPage = PAGE_SALON;
			changePage(PAGE_SALON);
			mSalonView.refresh(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showBarberView() {
		try {
			mPage = PAGE_BARBER;
			changePage(PAGE_BARBER);
			mBarberView.refresh(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showBidView() {
		try {
			mPointBid.setVisibility(View.INVISIBLE);
			mPage = PAGE_BID;
			changePage(PAGE_BID);
			mBidView.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showMeView() {
		try {
			mPointMe.setVisibility(View.INVISIBLE);
			mPage = PAGE_ME;
			changePage(PAGE_ME);
			mMeView.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changePage(int page) {
		int old = mViewFlipper.getDisplayedChild();
		if (page != old) {
			mBtnGrounp.get(old).setSelected(false);
			mBtnGrounp.get(page).setSelected(true);
			mViewFlipper.setDisplayedChild(page);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int child = mViewFlipper.getDisplayedChild();
		switch (child) {
		case PAGE_MAIN:
			mMainView.onActivityResult(requestCode, resultCode, data);
			break;
			
		case PAGE_BARBER:
			mBarberView.onActivityResult(requestCode, resultCode, data);
			break;
			
		case PAGE_BID:
			mBidView.onActivityResult(requestCode, resultCode, data);
			break;
			
		case PAGE_SALON:
			mSalonView.onActivityResult(requestCode, resultCode, data);
			break;
			
		case PAGE_ME:
			
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			AlarmMessageReceiver.getInstance().setmMainHandler(null);
			PollingUtils.stopPollingService(MainActivity.this, PollingService.class, getString(R.string.AlarmAnniServiceNotify));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLogout() {
		mPointMe.setVisibility(View.INVISIBLE);
		mPointBid.setVisibility(View.INVISIBLE);
	}
}
