package com.aaa.activity.barber;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonOrderService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.custom.CustomOrderActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.main.SendCouponActivity;
import com.aaa.activity.salon.SalonOrderActivity;
import com.aaa.util.OrderStatus;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class BarberOrderActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<OrderView> datas = new ArrayList<OrderView>();
	private MyAdapter adapter;
	
	private ISalonOrderService orderService = (ISalonOrderService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ORDER_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.barber_orString1);
		doGetDatas();
	}
	
	private void doGetDatas() {
		showWaitDialog();
		orderService.getMyOrders(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					datas = (ArrayList<OrderView>) response.getData();
					adapter = new MyAdapter(BarberOrderActivity.this, datas, getCHApplication());
					list.setAdapter(adapter);
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doDelete(final int orderId) {
		final AppMainDialog dialog = new AppMainDialog(this, R.style.appdialog);
		dialog.withTitle(R.string.barber_orString16)
		.withMessage(R.string.barber_orString17)
		.setOKClick(R.string.ok_queren, new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//
				dialog.dismiss();
			}
		})
		.setCancelClick(R.string.cancel_quxiao).show();
	}
	
	private void doReject(final int orderId) {
		final AppMainDialog dialog = new AppMainDialog(this, R.style.appdialog);
		dialog.withTitle(R.string.barber_orString16)
		.withMessage(R.string.barber_orString18)
		.setOKClick(R.string.ok_queren, new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showWaitDialog();
				orderService.rejectOrder(orderId, new AsyncResponseCompletedHandler<String>() {

					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						if(response.getState() >= 0){
							toast(R.string.reject_success);
							adapter.setStatus(orderId, OrderStatus.Reject);
						}else{
							toast(R.string.reject_failed);
						}
						hideAllDialog();
						return null;
					}
				});
				dialog.dismiss();
			}
		})
		.setCancelClick(R.string.cancel_quxiao).show();
	}
	
	private void doAccept(final int orderId){
		final AppMainDialog dialog = new AppMainDialog(this, R.style.appdialog);
		dialog.withTitle(R.string.barber_orString16)
		.withMessage(R.string.barber_orString19)
		.setOKClick(R.string.ok_queren, new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showWaitDialog();
				orderService.acceptOrder(orderId, new AsyncResponseCompletedHandler<String>() {

					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						if(response.getState() >= 0){
							toast(R.string.accpet_success);
							OrderView order = adapter.getOrder(orderId);
							if(order.getOrderStatus() == OrderStatus.Pending){
								adapter.setStatus(orderId, OrderStatus.Wating1);
							}
							else if(order.getOrderStatus() == OrderStatus.Wating2){
								adapter.setStatus(orderId, OrderStatus.Doing);
							}
						}else{
							toast(R.string.accpet_failed);
						}
						hideAllDialog();
						return null;
					}
				});
				dialog.dismiss();
			}
		})
		.setCancelClick(R.string.cancel_quxiao).show();
	}
	
	private void doCoupon(final int customId){
		final AppMainDialog dialog = new AppMainDialog(this, R.style.appdialog);
		dialog.withTitle(R.string.barber_orString16)
		.withMessage(R.string.barber_orString20)
		.setOKClick(R.string.barber_orString21, new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BarberOrderActivity.this, SendCouponActivity.class);
				intent.putExtra("customId", customId);
				startActivity(intent);
				dialog.dismiss();
			}
		})
		.setCancelClick(R.string.cancel_quxiao).show();
	}
	
	private class MyAdapter extends BaseAdapter{
		
		private Context context;
		private ArrayList<OrderView> list;
		
		public MyAdapter(Context context, ArrayList<OrderView> list, CHApplication app){
			this.context = context;
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public OrderView getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public void setStatus(int orderId, byte status) {
			for(OrderView order : list){
				if(order.getId() == orderId){
					order.setOrderStatus(status);
					notifyDataSetChanged();
					break;
				}
			}
		}
		
		public OrderView getOrder(int orderId){
			for(OrderView order : list){
				if(order.getId() == orderId){
					return order;
				}
			}
			return null;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final OrderView item = list.get(position);
			if(convertView == null){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_barber_order, null);
				vh.date = (TextView) convertView.findViewById(R.id.date);
				vh.time = (TextView) convertView.findViewById(R.id.time);
				vh.salon = (TextView) convertView.findViewById(R.id.salon);
				vh.custom = (TextView) convertView.findViewById(R.id.custom);
				vh.phone = (TextView) convertView.findViewById(R.id.phone);
				vh.extra_msg = (TextView) convertView.findViewById(R.id.extra_msg);
				vh.state = (TextView) convertView.findViewById(R.id.state);
				vh.ratio = (TextView) convertView.findViewById(R.id.ratio);
				vh.coupon_value = (TextView) convertView.findViewById(R.id.coupon_value);
				vh.accept = (Button) convertView.findViewById(R.id.accept);
				vh.reject = (Button) convertView.findViewById(R.id.reject);
				vh.delete = (Button) convertView.findViewById(R.id.delete);
				vh.coupon = (Button) convertView.findViewById(R.id.coupon);
				vh.phone_ll = (LinearLayout) convertView.findViewById(R.id.phone_ll);
				vh.coupon_ll = (LinearLayout) convertView.findViewById(R.id.coupon_ll);
				vh.ratio_ll = (LinearLayout) convertView.findViewById(R.id.ratio_ll);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			
			try {
				vh.date.setText(item.getOrderDate());
				vh.time.setText(item.getOrderTime());
				vh.salon.setText(item.getSalonName());
				vh.custom.setText(item.getCustomName());
				vh.extra_msg.setText(item.getDesc());
				
				if(item.getValue() <= 0){
					vh.coupon_ll.setVisibility(View.GONE);
				}else{
					vh.coupon_ll.setVisibility(View.VISIBLE);
					vh.coupon_value.setText(getString(R.string.barber_orString23, (int)item.getValue()));
				}
				
				vh.delete.setVisibility(View.GONE);
				vh.delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						doDelete(item.getId());
					}
				});
				
				vh.accept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						doAccept(item.getId());
					}
				});
				
				vh.reject.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						doReject(item.getId());
					}
				});
				
				vh.coupon.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						doCoupon(item.getUserId());
					}
				});
				
				if(item.getOrderStatus() == OrderStatus.Doing){
					vh.phone_ll.setVisibility(View.VISIBLE);
					vh.phone.setText(item.getCustomTel());
				}else{
					vh.phone_ll.setVisibility(View.GONE);
				}
				
				if(item.getOrderStatus() == OrderStatus.Doing
						|| item.getOrderStatus() == OrderStatus.Done
						|| item.getOrderStatus() == OrderStatus.Shared){
					vh.ratio_ll.setVisibility(View.VISIBLE);
					vh.ratio.setText(SalonTools.getRatioString(context, item.getRatio()));
				}else{
					vh.ratio_ll.setVisibility(View.GONE);
				}
				
				switch (item.getOrderStatus()) {
				case OrderStatus.Pending:
				case OrderStatus.Wating2:
//					vh.delete.setVisibility(View.GONE);
					vh.accept.setVisibility(View.VISIBLE);
					vh.reject.setVisibility(View.VISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.GONE);
					break;
					
				case OrderStatus.Wating1:
//					vh.delete.setVisibility(View.GONE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString15);
					break;
					
				case OrderStatus.Doing:
//					vh.delete.setVisibility(View.GONE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString11);
					break;
					
				case OrderStatus.Reject:
//					vh.delete.setVisibility(View.VISIBLE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString13);
					break;
					
				case OrderStatus.Done:
//					vh.delete.setVisibility(View.VISIBLE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString10);
					break;
					
				case OrderStatus.Shared:
//					vh.delete.setVisibility(View.VISIBLE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.VISIBLE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString10);
					break;
					
				case OrderStatus.Cancled:
//					vh.delete.setVisibility(View.VISIBLE);
					vh.accept.setVisibility(View.INVISIBLE);
					vh.reject.setVisibility(View.INVISIBLE);
					vh.coupon.setVisibility(View.GONE);
					vh.state.setVisibility(View.VISIBLE);
					vh.state.setText(R.string.barber_orString12);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public TextView date;
			public TextView time;
			public TextView salon;
			public TextView custom;
			public TextView phone;
			public TextView extra_msg;
			public TextView state;
			public TextView ratio;
			public TextView coupon_value;
			public Button accept;
			public Button reject;
			public Button delete;
			public Button coupon;
			public LinearLayout phone_ll;
			public LinearLayout coupon_ll;
			public LinearLayout ratio_ll;
		}
	}
	
	private void toast(int id){
		Toast.makeText(BarberOrderActivity.this, id, Toast.LENGTH_SHORT).show();
	}
}
