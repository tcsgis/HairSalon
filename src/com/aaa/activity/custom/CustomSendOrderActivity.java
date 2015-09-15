package com.aaa.activity.custom;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ISalonOrderService;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.activity.custom.DateDialog.onDateChangedListener;
import com.aaa.activity.custom.TimeDialog.onTimeChangedListener;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.main.MainActivity;
import com.aaa.util.Action;
import com.aaa.util.DateUtil;
import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.llw.salon.R;

public class CustomSendOrderActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.date)
	private TextView date;
	@CHInjectView(id = R.id.time)
	private TextView time;
	@CHInjectView(id = R.id.extra_msg)
	private EditText extra_msg;
	
	public static final String CALL_TYPE = "CustomSendOrderActivity.CALL_TYPE";
	public static final String BARBER_ID = "CustomSendOrderActivity.BARBER_ID";
	public static final String SALON_ID = "CustomSendOrderActivity.SALON_ID";
	public static final String COUPON_ID = "CustomSendOrderActivity.COUPON_ID";
	
	public static final int SALON = 1001;
	public static final int FREE_BARBER = 1002;
	
	private static final int TIME_INTERVAL = 1800 * 1000;
	
	private int callType = 0;
	private int salonId = 0;
	private int barberId = 0;
	private int couponId = 0;
	private OfferBiddingView offerBid = null;
	
	private TimeDialog timeDialog = null;
	private DateDialog dateDialog = null;
	private int hour = 0;
	private int minute = 0;
	private Date mDate = null;
	
	private ISalonOrderService orderService = (ISalonOrderService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ORDER_SERVER);
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.cus_soString1);
		
		callType = getIntent().getIntExtra(CALL_TYPE, 0);
		salonId = getIntent().getIntExtra(SALON_ID, 0);
		barberId = getIntent().getIntExtra(BARBER_ID, 0);
		couponId = getIntent().getIntExtra(COUPON_ID, 0);
		offerBid = (OfferBiddingView) getIntent().getSerializableExtra(BarberActivity.OFFER_BID_VIEW);
		
		findViewById(R.id.done).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(date.getText() == null || date.getText().length() == 0){
					Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString7, Toast.LENGTH_SHORT).show();
				}
				else if(time.getText() == null || time.getText().length() == 0){
					Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString8, Toast.LENGTH_SHORT).show();
				}
				else{
					doSendOrder();
				}
			}
		});
		
		time.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(date.getText() == null || date.getText().length() == 0){
					Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString11, Toast.LENGTH_SHORT).show();
				}else{
					if(timeDialog == null){
						Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, hour);
						c.set(Calendar.MINUTE, minute);
						
						timeDialog = new TimeDialog(CustomSendOrderActivity.this, R.style.appdialog, new onTimeChangedListener() {
							
							@Override
							public void onTimeChanged(int h, int m) {
								if(validTime(h, m)){
									hour = h;
									minute = m;
									time.setText(timeDialog.getTimeString(h, m));
									timeDialog.cancel();
								}
							}

						}, c.getTime());
					}
					timeDialog.show();
				}
			}
		});
		
		date.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dateDialog == null){
					mDate = Calendar.getInstance().getTime();
					
					dateDialog = new DateDialog(CustomSendOrderActivity.this, R.style.appdialog, new onDateChangedListener() {
						@Override
						public void onDateChanged(Date date) {
							mDate = date;
							CustomSendOrderActivity.this.date.setText(DateUtil.format(date, "yyyy-MM-dd"));
						}
					}, mDate, true);
				}
				dateDialog.show();
			}
		});
	}
	
	private boolean validTime(int h, int m) {
		Calendar c = Calendar.getInstance();
		c.setTime(mDate);
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, m);
		if(c.getTimeInMillis() - System.currentTimeMillis() >= TIME_INTERVAL){
			return true;
		}else{
			Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString12, Toast.LENGTH_SHORT).show();
			return false;
		}
	}


	protected void doSendOrder() {
		OrderView order = new OrderView();
		order.setOrderDate(date.getText().toString());
		order.setOrderTime(time.getText().toString());
		order.setSalonId(salonId);
		order.setCouponId(couponId);
		if(offerBid != null){
			String desc = getString(R.string.cus_soString14, (int)offerBid.getPrice()) + SalonTools.getText(extra_msg);
			order.setDesc(desc);
		}else{
			order.setDesc(SalonTools.getText(extra_msg));
		}
		
		if(callType == SALON){
			order.setSalonBarberId(barberId);
		}
		else if(callType == FREE_BARBER){
			order.setFreeBarberId(barberId);
		}
		
		showWaitDialog();
		orderService.createOrder(order, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					
					if(offerBid != null){
						doFinishBid();
					}else{
						hideAllDialog();
						Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString9, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(CustomSendOrderActivity.this, MainActivity.class);
						intent.setAction(Action.createOrder);
						startActivity(intent);
						finish();
					}
				}else if(response.getState() == -6){
					hideAllDialog();
					Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString13, Toast.LENGTH_SHORT).show();
				}else{
					hideAllDialog();
					Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString10, Toast.LENGTH_SHORT).show();
				}
				return null;
			}
		});
	}

	protected void doFinishBid() {
		bidService.confirmBid(offerBid.getOfferId(), offerBid.getBarberId(), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				hideAllDialog();
				Toast.makeText(CustomSendOrderActivity.this, R.string.cus_soString9, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(CustomSendOrderActivity.this, MainActivity.class);
				intent.setAction(Action.createOrder);
				startActivity(intent);
				finish();
				return null;
			}
		});
	}
}
