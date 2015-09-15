package com.aaa.activity.barber;

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
import cn.changhong.chcare.core.webapi.server.ISalonBarberService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.salon.SalonActivity;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.bitmap.CHBitmapCallBackHanlder;
import com.changhong.util.bitmap.CHDownloadBitmapHandler;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class BarberSalonsActivity extends LlwTitleActivity{
	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<SalonUser> datas = new ArrayList<SalonUser>();
	private MyAdapter adapter;
	
	private ISalonBarberService barberService = (ISalonBarberService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BARBER_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.barber_saString1);
		doGetDatas();
	}

	private void doGetDatas() {
		showWaitDialog();
		barberService.getBindSalons(CacheManager.INSTANCE.getCurrentUser().getId(), 
				new AsyncResponseCompletedHandler<String>() {

					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						
						if(response.getState() >= 0 && response.getData() != null){
							datas = (ArrayList<SalonUser>) response.getData();
							adapter = new MyAdapter(BarberSalonsActivity.this, datas, getCHApplication());
							list.setAdapter(adapter);
						}
						hideAllDialog();
						return null;
					}
				});
	}
	
	private void doUnbind(final int salonId){
		showWaitDialog();
		barberService.unbindSalon(salonId, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					adapter.remove(salonId);
					Toast.makeText(BarberSalonsActivity.this, R.string.barber_saString3, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(BarberSalonsActivity.this, R.string.barber_saString4, Toast.LENGTH_SHORT).show();
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
			
			imageFetcher = new CHBitmapCacheWork(context);
			
			CHBitmapCallBackHanlder taBitmapCallBackHanlder = new CHBitmapCallBackHanlder();
			taBitmapCallBackHanlder.setCircleParams(false);
			taBitmapCallBackHanlder
					.setLoadingImage(context, R.drawable.default_family_bg);
			
			Bitmap loading = taBitmapCallBackHanlder.getmLoadingBitmap();
			if(loading != null){
				int width = taBitmapCallBackHanlder.getmLoadingBitmap().getWidth();
				int height = taBitmapCallBackHanlder.getmLoadingBitmap().getHeight();
				CHDownloadBitmapHandler downloadBitmapFetcher = new CHDownloadBitmapHandler(
						context, width, height);
				imageFetcher.setProcessDataHandler(downloadBitmapFetcher);
			}
			
			imageFetcher.setCallBackHandler(taBitmapCallBackHanlder);
			imageFetcher.setFileCache(app.getFileCache());
		}
		
		public void remove(int salonId) {
			for(SalonUser user : list){
				if(user.getId() == salonId){
					list.remove(user);
					notifyDataSetChanged();
				}
			}
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
				viewHolder.name.setText(item.getNick());
				viewHolder.rate.setRating(item.getAvgScore());
				viewHolder.fencheng.setText(getString(R.string.salon_baString2, getRatio(item.getRatio())));
				viewHolder.grade.setText(getString(R.string.salon_baString11, item.getLevel()));
				imageFetcher.loadFormCache(item.getPhotos().get(0), viewHolder.photo);
				
				viewHolder.delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
						dialog.withTitle(R.string.barber_orString16)
						.withMessage(R.string.barber_saString2)
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
						Intent intent = new Intent(BarberSalonsActivity.this, SalonActivity.class);
						intent.putExtra(SalonActivity.SALON, item);
						startActivity(intent);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		private String getRatio(byte salonRatio) {
			byte customRatio = CacheManager.INSTANCE.getCurrentUser().getRatio();
			byte ratio = SalonTools.getActualRatio(salonRatio, customRatio);
			return SalonTools.getRatioString(BarberSalonsActivity.this, ratio);
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
