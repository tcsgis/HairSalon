package com.aaa.activity.admin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class AdminSalonBarberActivity extends LlwTitleActivity{
	@CHInjectView(id = R.id.grid)
	private GridView grid;
	
	private PhotoAdapter photoAdapter;
	private ArrayList<SalonBarberInfoView> barbers = new ArrayList<SalonBarberInfoView>();
	private CHBitmapCacheWork imgFetcher;
	
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.admin_sbString1);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		doGetDatas();
	}
	
	private void doGetDatas() {
		int salonId = getIntent().getIntExtra("ID", 0);
		showWaitDialog();
		salonService.getMyBarber(salonId,
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

	private void initView() {
		photoAdapter = new PhotoAdapter(barbers, this);
		grid.setAdapter(photoAdapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(AdminSalonBarberActivity.this, AdminSalonBarber2Activity.class);
				intent.putExtra("barber", photoAdapter.getItem(position));
				startActivity(intent);
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
			final SalonBarberInfoView item = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_salon_add_barber, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.nick = (TextView) convertView.findViewById(R.id.nick);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			try {
				imgFetcher.loadFormCache(item.getPhoto(), viewHolder.photo);
				viewHolder.nick.setText(item.getNick());
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public TextView nick;
		}
	}
}
