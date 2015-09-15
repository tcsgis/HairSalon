package com.aaa.activity.custom;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonOrderService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.OrderStatus;
import com.changhong.CHApplication;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.llw.salon.R;
import com.llw.salon.wxapi.WXEntryActivity;
import com.llw.salon.wxapi.WXEntryActivity.onShareListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

public class CustomOrderActivity extends LlwTitleActivity implements onShareListener{

	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<OrderView> datas = new ArrayList<OrderView>();
	private MyAdapter adapter;
	private IWXAPI api;
	
	private ISalonOrderService orderService = (ISalonOrderService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ORDER_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.cus_orString1);
		doGetData();
		
		api = WXAPIFactory.createWXAPI(this, WXEntryActivity.APP_ID);
		WXEntryActivity.registerListener(this);
	}
	
	private void doGetData() {
		showWaitDialog();
		orderService.getMyOrders(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					datas = (ArrayList<OrderView>) response.getData();
					adapter = new MyAdapter(CustomOrderActivity.this, datas, getCHApplication());
					list.setAdapter(adapter);
				}
				hideAllDialog();
				return null;
			}
		});
		
	}
	
	private class MyAdapter extends BaseAdapter{
		private ArrayList<OrderView> list;
		private Context context;
		
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
		public long getItemId(int position) {
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
		
		private byte getStatus(int orderId){
			for(OrderView order : list){
				if(order.getId() == orderId){
					return order.getOrderStatus();
				}
			}
			return -1;
		}
		
		public void setScore(int orderId, float score) {
			for(OrderView order : list){
				if(order.getId() == orderId){
					order.setOrderStatus(OrderStatus.Done);
					order.setScore(score);
					notifyDataSetChanged();
					break;
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final OrderView item = list.get(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_order, null);
				vh.date = (TextView) convertView.findViewById(R.id.date);
				vh.delete = (Button) convertView.findViewById(R.id.delete);
				vh.time = (TextView) convertView.findViewById(R.id.time);
				vh.barber_name = (TextView) convertView.findViewById(R.id.barber_name);
				vh.salon_name = (TextView) convertView.findViewById(R.id.salon_name);
				vh.phone = (TextView) convertView.findViewById(R.id.phone);
				vh.extra_msg = (TextView) convertView.findViewById(R.id.extra_msg);
				vh.coupon = (TextView) convertView.findViewById(R.id.coupon);
				vh.doing = (LinearLayout) convertView.findViewById(R.id.doing);
				vh.rate_doing = (RatingBar) convertView.findViewById(R.id.rate_doing);
				vh.btn_done = (Button) convertView.findViewById(R.id.btn_done);
				vh.wating = (LinearLayout) convertView.findViewById(R.id.wating);
				vh.share_layout = (LinearLayout) convertView.findViewById(R.id.share_layout);
				vh.wating_txt = (TextView) convertView.findViewById(R.id.wating_txt);
				vh.done = (RelativeLayout) convertView.findViewById(R.id.done);
				vh.rate_done = (RatingBar) convertView.findViewById(R.id.rate_done);
//				vh.share = (ImageView) convertView.findViewById(R.id.share);
				vh.had_share = (TextView) convertView.findViewById(R.id.had_share);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			final ViewHolder vh2 = vh;
			vh = null;
			//commen
			vh2.date.setText(getResources().getString(R.string.cus_orString2, item.getOrderDate()));
			vh2.time.setText(getResources().getString(R.string.cus_orString3, item.getOrderTime()));
			vh2.barber_name.setText(getResources().getString(R.string.cus_orString4, item.getBarberName()));
			vh2.salon_name.setText(getResources().getString(R.string.cus_orString33, item.getSalonName()));
			
			if(item.getOrderStatus() == OrderStatus.Doing){
				String phone = item.getFreeBarberId() == 0 ? item.getSalonTel() : item.getBarberTel();
				vh2.phone.setVisibility(View.VISIBLE);
				vh2.phone.setText(getResources().getString(R.string.cus_orString5, phone));
			}else{
				vh2.phone.setVisibility(View.GONE);
			}
			
			if(item.getDesc() == null || item.getDesc().length() == 0){
				vh2.extra_msg.setVisibility(View.GONE);
			}else{
				vh2.extra_msg.setVisibility(View.VISIBLE);
				vh2.extra_msg.setText(getResources().getString(R.string.cus_orString32, item.getDesc()));
			}
			
			int couponValue = (int) item.getValue();
			if(couponValue <= 0){
				vh2.coupon.setVisibility(View.GONE);
			}else{
				vh2.coupon.setVisibility(View.VISIBLE);
				vh2.coupon.setText(getResources().getString(R.string.cus_orString31, couponValue));
			}
			
			if(item.getOrderStatus() == OrderStatus.Pending 
					|| item.getOrderStatus() == OrderStatus.Wating1
					|| item.getOrderStatus() == OrderStatus.Wating2
					|| item.getOrderStatus() == OrderStatus.Doing){
				vh2.delete.setVisibility(View.VISIBLE);
			}else{
				vh2.delete.setVisibility(View.GONE);
			}
			vh2.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
					switch (item.getOrderStatus()) {
					case OrderStatus.Pending:
					case OrderStatus.Wating1:
					case OrderStatus.Wating2:
					case OrderStatus.Doing:
						dialog.withTitle(R.string.dialog_title)
						.withMessage(R.string.cus_orString23)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								doCancelOrder(item.getId());
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show();
						break;
						
					case OrderStatus.Done:
					case OrderStatus.Cancled:
					case OrderStatus.Reject:
					case OrderStatus.Shared:
						dialog.withTitle(R.string.dialog_title)
						.withMessage(R.string.cus_orString20)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								doDeleteOrder();
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show();
						break;
					}
				}
			});
			
			if(item.getOrderStatus() == OrderStatus.Pending 
					|| item.getOrderStatus() == OrderStatus.Wating1
					|| item.getOrderStatus() == OrderStatus.Wating2){
				vh2.wating.setVisibility(View.VISIBLE);
				vh2.doing.setVisibility(View.GONE);
				vh2.done.setVisibility(View.GONE);
				vh2.wating_txt.setText(item.getFreeBarberId() == 0 ? R.string.cus_orString12 : R.string.cus_orString13);
			}
			
			if(item.getOrderStatus() == OrderStatus.Doing){//doing
				vh2.wating.setVisibility(View.GONE);
				vh2.doing.setVisibility(View.VISIBLE);
				vh2.done.setVisibility(View.GONE);
				vh2.rate_doing.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						if(rating == 0){
							vh2.btn_done.setBackgroundResource(R.drawable.salon_btn_invalid);
						}else{
							vh2.btn_done.setBackgroundResource(R.drawable.salon_btn_valid);
						}
					}
				});
				
				vh2.btn_done.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
						dialog.withTitle(R.string.dialog_title)
						.withMessage(R.string.cus_orString18)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								if(vh2.rate_doing.getRating() == 0){
									Toast.makeText(context, R.string.cus_orString24, Toast.LENGTH_SHORT).show();
								}else{
									doConfirmDone(item.getId(), vh2.rate_doing.getRating());
								}
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show();
					}
				});
			}
			
			if(item.getOrderStatus() == OrderStatus.Done 
					|| item.getOrderStatus() == OrderStatus.Shared){
				vh2.wating.setVisibility(View.GONE);
				vh2.doing.setVisibility(View.GONE);
				vh2.done.setVisibility(View.VISIBLE);
				vh2.rate_done.setRating(item.getScore());
//				if(item.getOrderStatus() == OrderStatus.Shared){
//					vh2.share_layout.setVisibility(View.GONE);
//					vh2.had_share.setVisibility(View.VISIBLE);
//				}else{
					vh2.had_share.setVisibility(View.GONE);
					vh2.share_layout.setVisibility(View.VISIBLE);
					vh2.share_layout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(isWXAppInstalledAndSupported()){
								sendWxMsg(item);
							}
						}
					});
//				}
			}
			
			if(item.getOrderStatus() == OrderStatus.Cancled){//客户自己取消
				vh2.wating.setVisibility(View.VISIBLE);
				vh2.doing.setVisibility(View.GONE);
				vh2.done.setVisibility(View.GONE);
				vh2.wating_txt.setText(R.string.cus_orString21);
			}
			
			if(item.getOrderStatus() == OrderStatus.Reject){//被拒绝
				vh2.wating.setVisibility(View.VISIBLE);
				vh2.doing.setVisibility(View.GONE);
				vh2.done.setVisibility(View.GONE);
				vh2.wating_txt.setText(R.string.cus_orString22);
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			TextView date;
			Button delete;
			TextView time;
			TextView barber_name;
			TextView salon_name;
			TextView phone;
			LinearLayout doing;
			LinearLayout share_layout;
			RatingBar rate_doing;
			Button btn_done;
			LinearLayout wating;
			TextView wating_txt;
			RelativeLayout done;
			RatingBar rate_done;
			TextView extra_msg;
			TextView coupon;
			TextView had_share;
//			ImageView share;
		}
	}
	
	private boolean isWXAppInstalledAndSupported() {
		if(! api.isWXAppInstalled()){
			Toast.makeText(this, R.string.install_wx, Toast.LENGTH_SHORT).show();
		}
		else if(! api.isWXAppSupportAPI()){
			Toast.makeText(this, R.string.update_wx, Toast.LENGTH_SHORT).show();
		}
		
		return api.isWXAppInstalled() && api.isWXAppSupportAPI();
	}
	
	private void sendWxMsg(final OrderView order) {
		String title = "";
		String msgString = "";
		int id = 0;
		if(order.getFreeBarberId() != 0){
			title = getString(R.string.cus_orString34, order.getBarberName());
			msgString = getString(R.string.cus_orString36);
			id = order.getFreeBarberId();
		}else{
			title = getString(R.string.cus_orString35, order.getBarberName());
			msgString = getString(R.string.cus_orString37);
			id = order.getSalonId();
		}
		
		final WXAppExtendObject appdata = new WXAppExtendObject();
		appdata.extInfo = String.valueOf(id);
		final WXMediaMessage msg = new WXMediaMessage();
		msg.title = title;
		msg.description = msgString;
		msg.mediaObject = appdata;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction(String.valueOf(order.getId()) + "-appdata");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		
		CHLogger.d(this, "share orderId " + order.getId());
	}
	
	private void doCancelOrder(final int orderId){
		showWaitDialog();
		orderService.cancelOrder(orderId, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					toast(R.string.cancel_success);
					adapter.setStatus(orderId, OrderStatus.Cancled);
				}else{
					toast(R.string.cancel_fail);
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doDeleteOrder(){
		
	}
	
	private void doConfirmDone(final int orderId, final float score){
		showWaitDialog();
		orderService.finishOrder(orderId, score, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					toast(R.string.cus_orString25);
					adapter.setScore(orderId, score);
				}else{
					toast(R.string.cus_orString26);
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doShare(final int orderId){
		try {
			byte status = adapter.getStatus(orderId);
			if(status != OrderStatus.Shared){
				toast(R.string.cus_orString27);
			}else{
				orderService.shareOrder(orderId, new AsyncResponseCompletedHandler<String>() {
					
					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						
						if(response.getState() >= 0){
							toast(R.string.cus_orString27);
							adapter.setStatus(orderId, OrderStatus.Shared);
						}else{
//					toast(R.string.cus_orString28);
						}
						hideAllDialog();
						return null;
					}
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void toast(int id){
		Toast.makeText(CustomOrderActivity.this, id, Toast.LENGTH_SHORT).show();
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onSuccess(String transaction) {
		if(transaction != null){
			String[] ss = transaction.split("-");
			try {
				int orderId = Integer.valueOf(ss[0]);
				if(adapter.getStatus(orderId) == OrderStatus.Done){
					doShare(orderId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
