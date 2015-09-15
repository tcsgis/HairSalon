package com.aaa.activity.salon;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.activity.custom.CustomSendOrderActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class SalonOwnBarbersActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.grid)
	private GridView grid;
	
	public static final int DONE = 1221;
	
	private final int ORDER = 15564;
	private PhotoAdapter photoAdapter;
	private ArrayList<SalonBarberInfoView> barbers = new ArrayList<SalonBarberInfoView>();
	private CHBitmapCacheWork imgFetcher;
	private int couponId = 0;
	
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_abString5);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		couponId = getIntent().getIntExtra(CustomSendOrderActivity.COUPON_ID, 0);
		int id = getIntent().getIntExtra("ID", 0);
		
		showWaitDialog();
		salonService.getMyBarber(id,
				new AsyncResponseCompletedHandler<String>() {
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response != null && response.getState() >= 0){
					if(response.getData() != null){
						barbers = (ArrayList<SalonBarberInfoView>) response.getData();
						initView();
					}
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void initView(){
		photoAdapter = new PhotoAdapter(barbers, SalonOwnBarbersActivity.this);
		grid.setAdapter(photoAdapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(SalonOwnBarbersActivity.this, BarberActivity.class);
				intent.putExtra(BarberActivity.SALON_BARBER, photoAdapter.getItem(position));
				intent.putExtra(BarberActivity.FREE_BARBER, false);
				intent.putExtra(CustomSendOrderActivity.COUPON_ID, couponId);
				startActivityForResult(intent, ORDER);
			}
		});
	}
	
	private class PhotoAdapter extends BaseAdapter{
		
		private ArrayList<SalonBarberInfoView> list;
		private Context context;
		
		public PhotoAdapter(ArrayList<SalonBarberInfoView> list, Context context) {
			this.list = (ArrayList<SalonBarberInfoView>) list.clone();
			this.context = context;
		}
		
		public PhotoAdapter(Context context) {
			list = new ArrayList<SalonBarberInfoView>();
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public SalonBarberInfoView getItem(int position) {
			return list.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final SalonBarberInfoView barber = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_salon_add_barber, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.nick = (TextView) convertView.findViewById(R.id.nick);
				viewHolder.delete = (ImageView) convertView.findViewById(R.id.button);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			try {
				imgFetcher.loadFormCache(barber.getPhoto(), viewHolder.photo);
				viewHolder.nick.setText(barber.getNick());
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public ImageView delete;
			public TextView nick;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ORDER){
			if(resultCode == DONE){
				
			}
		}
	}
}

