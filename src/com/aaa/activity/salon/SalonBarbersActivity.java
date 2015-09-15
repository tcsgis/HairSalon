package com.aaa.activity.salon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.activity.barber.BarberSalonsActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.bitmap.CHBitmapCallBackHanlder;
import com.changhong.util.bitmap.CHDownloadBitmapHandler;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class SalonBarbersActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<SalonUser> users;
	private MyAdapter adapter;
	
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_baString1);
		doGetDatas();
	}

	private void doGetDatas() {
		showWaitDialog();
		salonService.getFreeBarber(CacheManager.INSTANCE.getCurrentUser().getId(), 
				new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					users = (ArrayList<SalonUser>) response.getData();
					if(users != null && users.size() > 0){
						adapter = new MyAdapter(SalonBarbersActivity.this, users, getCHApplication());
						list.setAdapter(adapter);
					}
				}else{
					Toast.makeText(SalonBarbersActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doUnbind(final int barberID){
		showWaitDialog();
		salonService.deleteFreeBarber(barberID, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					adapter.remove(barberID);
					Toast.makeText(SalonBarbersActivity.this, R.string.barber_saString3, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SalonBarbersActivity.this, R.string.salon_abString3, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private class MyAdapter extends BaseAdapter{
		private ArrayList<SalonUser> list;
		private Context context;
		private CHBitmapCacheWork imageFetcher;
		
		public MyAdapter(Context context, ArrayList<SalonUser> list, CHApplication app){
			this.context = context;
			this.list = list;
			imageFetcher = SalonTools.getImageFetcher(context, getCHApplication(), false, 0);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public SalonUser getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		public void remove(int barberID) {
			for(SalonUser user : list){
				if(user.getId() == barberID){
					list.remove(user);
					notifyDataSetChanged();
				}
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final SalonUser item = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_salon_barber, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.rate = (RatingBar) convertView.findViewById(R.id.rate);
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.fencheng = (TextView) convertView.findViewById(R.id.fencheng);
				viewHolder.grade = (TextView) convertView.findViewById(R.id.grade);
				viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			try {
				viewHolder.name.setText(item.getPerson_Name());
				viewHolder.rate.setRating(item.getAvgScore());
				viewHolder.fencheng.setText(getString(R.string.salon_baString2, getRatio(item.getRatio())));
				viewHolder.grade.setText(getString(R.string.salon_baString11, item.getLevel()));
				imageFetcher.loadFormCache(item.getPhoto(), viewHolder.photo);
				
				viewHolder.delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
						dialog.withTitle(R.string.barber_orString16)
						.withMessage(R.string.salon_baString13)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								doUnbind(item.getId());
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show(); 
					}
				});
				
				viewHolder.photo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SalonBarbersActivity.this, BarberActivity.class);
						intent.putExtra(BarberActivity.BARBER, item);
						intent.putExtra(BarberActivity.FREE_BARBER, true);
						intent.putExtra(BarberActivity.SALON_VIEW, true);
						startActivity(intent);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		private String getRatio(byte customRatio) {
			byte salonRatio = CacheManager.INSTANCE.getCurrentUser().getRatio();
			byte ratio = SalonTools.getActualRatio(salonRatio, customRatio);
			return SalonTools.getRatioString(SalonBarbersActivity.this, ratio);
		}
		
		final class ViewHolder{
			ImageView photo;
			TextView name;
			TextView fencheng;
			TextView grade;
			RatingBar rate;
			Button delete;
		}
	}
}
