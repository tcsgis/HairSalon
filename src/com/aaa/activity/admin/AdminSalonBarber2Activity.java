package com.aaa.activity.admin;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.SalonTools;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class AdminSalonBarber2Activity extends LlwTitleActivity{

	@CHInjectView(id = R.id.nick)
	private TextView nick;
	@CHInjectView(id = R.id.exp)
	private TextView exp;
	@CHInjectView(id = R.id.tang_duan)
	private TextView tang_duan;
	@CHInjectView(id = R.id.tang_zhong)
	private TextView tang_zhong;
	@CHInjectView(id = R.id.tang_chang)
	private TextView tang_chang;
	@CHInjectView(id = R.id.ran_duan)
	private TextView ran_duan;
	@CHInjectView(id = R.id.ran_zhong)
	private TextView ran_zhong;
	@CHInjectView(id = R.id.ran_chang)
	private TextView ran_chang;
	@CHInjectView(id = R.id.jian_duan)
	private TextView jian_duan;
	@CHInjectView(id = R.id.jian_zhong)
	private TextView jian_zhong;
	@CHInjectView(id = R.id.jian_chang)
	private TextView jian_chang;
	@CHInjectView(id = R.id.health_txt)
	private TextView health_txt;
	
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.health_photo)
	private ImageView health_photo;
	
	@CHInjectView(id = R.id.zuopin_list)
	private HorizontalListView zuopin_list;
	@CHInjectView(id = R.id.zhengshu_list)
	private HorizontalListView zhengshu_list;
	
	@CHInjectView(id = R.id.back)
	private Button back;
	
	private ArrayList<TextView> prices = new ArrayList<TextView>();
	private CHBitmapCacheWork imageFetcher;
	private zuopinPhotoAdapter zuopinAdapter;
	private zuopinPhotoAdapter zhengshu_adapter;
	private SalonBarberInfoView barber;

	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.admin_sbString1);
		barber = (SalonBarberInfoView) getIntent().getSerializableExtra("barber");
		if(barber != null){
			initView();
		}
	}
	
	private void initView() {
		String nick = barber.getNick();
		String exp = String.valueOf(barber.getWorkYears());
		
		String photo = barber.getPhoto();
		String health_photo = barber.getHealth();
		
		int[] prices = barber.getPrices();
		
		ArrayList<String> zhengshuPaths = barber.getCerts();
		ArrayList<String> zuopinPaths = barber.getWorks();
		
		this.prices.add(tang_duan);
		this.prices.add(tang_zhong);
		this.prices.add(tang_chang);
		this.prices.add(ran_duan);
		this.prices.add(ran_zhong);
		this.prices.add(ran_chang);
		this.prices.add(jian_duan);
		this.prices.add(jian_zhong);
		
		this.nick.setText(nick);
		this.exp.setText(exp);
		
		this.prices.add(jian_chang);
		for(int i = 0; i < 9; i++){
			this.prices.get(i).setText(String.valueOf(prices[i]));
		}
		
		imageFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		try {
			imageFetcher.loadFormCache(photo, this.photo);
			if(health_photo == null || health_photo.trim().equals("")){
				health_txt.setText(R.string.admin_vbString12);
				this.health_photo.setVisibility(View.GONE);
			}else{
				health_txt.setText(R.string.admin_vbString11);
				imageFetcher.loadFormCache(health_photo, this.health_photo);
			}
			imageFetcher.loadFormCache(photo, this.photo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(zuopinPaths != null && zuopinPaths.size() > 0){
			zuopinAdapter = new zuopinPhotoAdapter(zuopinPaths, this);
			zuopin_list.setAdapter(zuopinAdapter);
		}
		
		if(zhengshuPaths != null && zhengshuPaths.size() > 0){
			zhengshu_adapter = new zuopinPhotoAdapter(zhengshuPaths, this);
			zhengshu_list.setAdapter(zhengshu_adapter);
		}
		
		SalonTools.scrollToHead(this.nick);
	}
	
	private class zuopinPhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		private CHBitmapCacheWork imgFetcher;
		
		public zuopinPhotoAdapter(ArrayList<String> list, Context context) {
			this.list = list;
			this.context = context;
			imgFetcher = SalonTools.getImageFetcher(this.context, getCHApplication(), false, R.drawable.default_else);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final String url = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_show_list, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
				
			try {
				this.imgFetcher.loadFormCache(url, viewHolder.photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
		}
	}
}
