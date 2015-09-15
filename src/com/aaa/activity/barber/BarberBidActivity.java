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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.OfferStatus;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class BarberBidActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<OfferView> datas;
	private MyAdapter adapter;
	
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.barber_bString1);
		getDatas();
	}
	
	private void getDatas() {
		showWaitDialog();
		bidService.getBarberBid(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<OfferView> bids = (ArrayList<OfferView>) response.getData();
					adapter = new MyAdapter(BarberBidActivity.this, bids, getCHApplication());
					list.setAdapter(adapter);
				}
				hideAllDialog();
				return null;
			}
		});
	}

	private class MyAdapter extends BaseAdapter{

		private Context context;
		private ArrayList<OfferView> list;
		private CHBitmapCacheWork imageFetcher;
		
		public MyAdapter(Context context, ArrayList<OfferView> list, CHApplication app){
			this.context = context;
			this.list = list;
			imageFetcher = SalonTools.getImageFetcher(context, app, false, 0);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public OfferView getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		
		private void setPrice(int offerId, int price){
			for(OfferView offer : list){
				if(offer.getId() == offerId){
					offer.setPrice(price);
					notifyDataSetChanged();
					break;
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final OfferView item = list.get(position);
			if(convertView == null){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_barber_bid, null);
				vh.photo = (ImageView) convertView.findViewById(R.id.photo);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				vh.price = (TextView) convertView.findViewById(R.id.price);
				vh.state = (TextView) convertView.findViewById(R.id.state);
				vh.order_msg = (Button) convertView.findViewById(R.id.order_msg);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			
			try {
				vh.name.setText(item.getuserNick());
				vh.price.setText(String.valueOf(item.getPrice()));
				
				if(item.getBarberId() == CacheManager.INSTANCE.getCurrentUser().getId()){
					vh.state.setText(R.string.barber_bString8);
					vh.order_msg.setVisibility(View.VISIBLE);
					vh.order_msg.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(BarberBidActivity.this, BarberOrderActivity.class);
							context.startActivity(intent);
						}
					});
				}else{
					switch (item.getOfferStatus()) {
					case OfferStatus.Pending:
					case OfferStatus.Full:
						vh.state.setText(R.string.barber_bString7);
						vh.order_msg.setVisibility(View.INVISIBLE);
						break;
						
					case OfferStatus.Cancel:
					case OfferStatus.Done:
						vh.state.setText(R.string.barber_bString9);
						vh.order_msg.setVisibility(View.INVISIBLE);
						break;
					}
				}
				
				imageFetcher.loadFormCache(item.getPics(), vh.photo);
				
				if(item.getPrice() == 0){
					getPrice(item.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public TextView name;
			public TextView price;
			public TextView state;
			public Button order_msg;
		}
	}
	
	private void getPrice(final int offerId){
		bidService.getBid(offerId, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<OfferBiddingView> bidBarbers = (ArrayList<OfferBiddingView>) response.getData();
					for(OfferBiddingView bid : bidBarbers){
						if(bid.getBarberId() == CacheManager.INSTANCE.getCurrentUser().getId()){
							adapter.setPrice(offerId, (int)bid.getPrice());
							break;
						}
					}
				}
				return null;
			}
			
		});
	}
}
