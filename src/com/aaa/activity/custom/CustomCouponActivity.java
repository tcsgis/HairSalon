package com.aaa.activity.custom;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import cn.changhong.chcare.core.webapi.bean.CouponView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.salon.SalonActivity;
import com.aaa.util.Role;
import com.changhong.CHApplication;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.llw.salon.R;

public class CustomCouponActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.list)
	private ListView list;
	@CHInjectView(id = R.id.rule)
	private TextView rule;
	
	private ArrayList<CusCouponItem> datas = new ArrayList<CusCouponItem>();
	private MyAdapter adapter;
	
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.cus_cuString1);
		doGetData();
		
		rule.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final AppMainDialog dialog = new AppMainDialog(CustomCouponActivity.this, R.style.appdialog);
				dialog.withTitle(R.string.barber_orString16)
				.withMessage(R.string.cus_cuString10)
				.setOKClick(R.string.ok_queding, new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				})
				.setCancelClick(R.string.cancel_quxiao).show();
			}
		});
	}
	
	private void doGetData() {
		showWaitDialog();
		bidService.getCustomCoupon(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<CouponView> cs = (ArrayList<CouponView>) response.getData();
					for(CouponView c : cs){
						CusCouponItem item = new CusCouponItem();
						if(c.getSalesRole() == Role.SALON){
							item.salonID = c.getSalesId();
							item.salonName = c.getSalesName();
						}else{
							item.barberID = c.getSalesId();
							item.barberName = c.getSalesName();
						}
						item.used = c.getUsed();
						item.count = (int) c.getValue();
						item.couponId = c.getId();
						datas.add(item);
					}
					adapter = new MyAdapter(CustomCouponActivity.this, datas, getCHApplication());
					list.setAdapter(adapter);
				}
				hideAllDialog();
				return null;
			}
		});
	}

	private class CusCouponItem{
		public int couponId;
		public int salonID;//salon barber二选其一
		public String salonName;
		public int barberID;
		public String barberName;
		public int count;
		public boolean used;
	}
	
	private class MyAdapter extends BaseAdapter{
		private ArrayList<CusCouponItem> list;
		private Context context;
		
		public MyAdapter(Context context, ArrayList<CusCouponItem> list, CHApplication app){
			this.context = context;
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CusCouponItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final CusCouponItem item = list.get(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_coupon, null);
				vh.count = (TextView) convertView.findViewById(R.id.count);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				vh.order = (Button) convertView.findViewById(R.id.order);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			vh.count.setText(String.valueOf(item.count));
			
			if(! item.used){
				vh.order.setClickable(true);
				vh.order.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = null;
						if(item.salonID != 0){
							intent = new Intent(CustomCouponActivity.this, SalonActivity.class);
							intent.putExtra(SalonActivity.ID, item.salonID);
							intent.putExtra(SalonActivity.NAME, item.salonName);
							intent.putExtra(CustomSendOrderActivity.COUPON_ID, item.couponId);
						}else{
							intent = new Intent(CustomCouponActivity.this, BarberActivity.class);
							intent.putExtra(BarberActivity.ID, item.barberID);
							intent.putExtra(BarberActivity.NAME, item.barberName);
							intent.putExtra(BarberActivity.FREE_BARBER, true);
							intent.putExtra(CustomSendOrderActivity.COUPON_ID, item.couponId);
						}
						if(intent != null){
							startActivity(intent);
						}
					}
				});
			}else{
				vh.order.setClickable(false);
				vh.order.setBackgroundResource(R.drawable.salon_btn_invalid);
				vh.order.setText(R.string.cus_cuString9);
			}
			
			
			if(item.salonID != 0){
				vh.name.setText(getResources().getString(R.string.cus_cuString6, item.salonName));
			}else{
				vh.name.setText(getResources().getString(R.string.cus_cuString7, item.barberName));
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			TextView count;
			TextView name;
			Button order;
		}
	}
	
}
