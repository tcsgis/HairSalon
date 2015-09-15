package com.aaa.activity.admin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.ISalonAdminService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberRegisterActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.SalonTools;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class AdminVerifyBarberActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.name)
	private TextView name;
	@CHInjectView(id = R.id.id_num)
	private TextView id_num;
	@CHInjectView(id = R.id.phone)
	private TextView phone;
	@CHInjectView(id = R.id.nick)
	private TextView nick;
	@CHInjectView(id = R.id.exp)
	private TextView exp;
	@CHInjectView(id = R.id.district)
	private TextView district;
	@CHInjectView(id = R.id.salon)
	private TextView salon;
	@CHInjectView(id = R.id.fencheng)
	private TextView fencheng;
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
	@CHInjectView(id = R.id.dengji_txt)
	private TextView dengji_txt;
	@CHInjectView(id = R.id.adept)
	private TextView adept;
	@CHInjectView(id = R.id.recommend)
	private TextView recommend;
	
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.health_photo)
	private ImageView health_photo;
	
	@CHInjectView(id = R.id.zuopin_list)
	private HorizontalListView zuopin_list;
	@CHInjectView(id = R.id.zhengshu_list)
	private HorizontalListView zhengshu_list;
	@CHInjectView(id = R.id.product_list)
	private ListView product_list;
	
	@CHInjectView(id = R.id.yi_point)
	private ImageView yi_point;
	@CHInjectView(id = R.id.er_point)
	private ImageView er_point;
	@CHInjectView(id = R.id.san_point)
	private ImageView san_point;
	@CHInjectView(id = R.id.si_point)
	private ImageView si_point;
	@CHInjectView(id = R.id.wu_point)
	private ImageView wu_point;
	
	@CHInjectView(id = R.id.pass)
	private Button pass;
	@CHInjectView(id = R.id.reject)
	private Button reject;
	
	private zuopinPhotoAdapter zuopinAdapter;
	private zuopinPhotoAdapter zhengshu_adapter;
	private ProductAdapter productAdapter;
	private ArrayList<TextView> prices = new ArrayList<TextView>();
	private CHBitmapCacheWork imageFetcher;
	private byte dengjiSelected = 0;
	private ArrayList<ImageView> dengjiList  = new ArrayList<ImageView>();
	private int callType = 0;
	private int barberID = 0;
	private SalonUser barber;
	
	private ISalonAdminService adminService = (ISalonAdminService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ADMIN_SERVER);
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		barberID = getIntent().getIntExtra("ID", 0);
		setTitile(R.string.admin_vbString2);
		
		showWaitDialog();
		accountService.getUser(barberID, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					barber = (SalonUser) response.getData();
					if(barber != null){
						initView();
					}
				}else{
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
		
		pass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dengjiSelected == 0){
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.admin_vbString15, Toast.LENGTH_SHORT).show();
				}else{
					final AppMainDialog dialog = new AppMainDialog(AdminVerifyBarberActivity.this, R.style.appdialog);
					dialog.withTitle(R.string.barber_orString16)
					.withMessage(R.string.admin_vbString16)
					.setOKClick(R.string.ok_queding, new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							doPass();
							dialog.dismiss();
						}
					})
					.setCancelClick(R.string.cancel_quxiao).show();
				}
			}
		});
		
		reject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final AppMainDialog dialog = new AppMainDialog(AdminVerifyBarberActivity.this, R.style.appdialog);
				dialog.withTitle(R.string.barber_orString16)
				.withMessage(R.string.admin_vbString17)
				.setOKClick(R.string.ok_queding, new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						doReject();
						dialog.dismiss();
					}
				})
				.setCancelClick(R.string.cancel_quxiao).show();
			}
		});
	}
	
	private void doPass(){
		showWaitDialog();
		adminService.check(barberID, true, dengjiSelected, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					hideAllDialog();
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.admin_vbString19, Toast.LENGTH_SHORT).show();
					setMyResult();
				}else{
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.admin_vbString18, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}

	private void doReject(){
		showWaitDialog();
		adminService.check(barberID, false, dengjiSelected, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					hideAllDialog();
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.admin_vbString19, Toast.LENGTH_SHORT).show();
					setMyResult();
				}else{
					Toast.makeText(AdminVerifyBarberActivity.this, R.string.admin_vbString18, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private void setMyResult(){
		Intent data = new Intent();
		data.putExtra("ID", barberID);
		setResult(RESULT_OK, data);
		finish();
	}
	
	private void initView() {
		String name = barber.getPerson_Name();
		String id_num = barber.getPerson_Id();
		String phone = barber.getTel();
		String nick = barber.getNick();
		String exp = String.valueOf(barber.getWorkYears());
		String district = SalonTools.getDisplayArea(this, barber.getAreas());
		String fencheng = SalonTools.getRatioString(this, barber.getRatio());
		byte dengji = barber.getLevel();
		
		String photo = barber.getPhoto();
		String health_photo = barber.getHealth();
		
		int[] prices = barber.getPrices();
		
		ArrayList<String> zhengshuPaths = barber.getCerts();
		ArrayList<String> zuopinPaths = barber.getWorks();
		ArrayList<Product> products = barber.getProducts();
		
		dengjiList.add(yi_point);
		dengjiList.add(er_point);
		dengjiList.add(san_point);
		dengjiList.add(si_point);
		dengjiList.add(wu_point);
		
		this.prices.add(tang_duan);
		this.prices.add(tang_zhong);
		this.prices.add(tang_chang);
		this.prices.add(ran_duan);
		this.prices.add(ran_zhong);
		this.prices.add(ran_chang);
		this.prices.add(jian_duan);
		this.prices.add(jian_zhong);
		
		this.name.setText(name);
		this.id_num.setText(id_num);
		this.phone.setText(phone);
		this.nick.setText(nick);
		this.exp.setText(exp);
		this.district.setText(district);
//		this.salon.setText(salon);
		this.fencheng.setText(fencheng);
		recommend.setText(barber.getDesc());
		
		if(barber.getAdept() == 0){
			adept.setText(R.string.admin_vbString20);
		}else{
			ArrayList<String> ss = new ArrayList<String>();
			if((barber.getAdept() | BarberRegisterActivity.MASK_JIAN) != 0){
				ss.add(getString(R.string.service1));
			}
			if((barber.getAdept() | BarberRegisterActivity.MASK_TANG) != 0){
				ss.add(getString(R.string.service2));
			}
			if((barber.getAdept() | BarberRegisterActivity.MASK_RAN) != 0){
				ss.add(getString(R.string.service3));
			}
			if((barber.getAdept() | BarberRegisterActivity.MASK_HULI) != 0){
				ss.add(getString(R.string.service4));
			}
			String s = "";
			for(int i = 0; i < ss.size(); i++){
				if(i == 0){
					s += ss.get(i);
				}else{
					s += "/" + ss.get(i);
				}
			}
			adept.setText(getString(R.string.admin_vbString21, s));
		}
		
		this.prices.add(jian_chang);
		for(int i = 0; i < 9; i++){
			this.prices.get(i).setText(String.valueOf(prices[i]));
		}
		
		dengji_txt.setText(R.string.admin_vbString9);
		SalonTools.setSelection(dengjiList.size(), dengjiList);
		
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
		
		if(products != null && products.size() > 0){
			productAdapter = new ProductAdapter(products, this);
			product_list.setAdapter(productAdapter);
		}
		
		findViewById(R.id.yi).setOnClickListener(clickSelect);
		findViewById(R.id.er).setOnClickListener(clickSelect);
		findViewById(R.id.san).setOnClickListener(clickSelect);
		findViewById(R.id.si).setOnClickListener(clickSelect);
		findViewById(R.id.wu).setOnClickListener(clickSelect);
		
		SalonTools.scrollToHead(this.name);
	}
	
	private OnClickListener clickSelect = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.yi:
				dengjiSelected = 1;
				SalonTools.setSelection(0, dengjiList);
				break;
				
			case R.id.er:
				dengjiSelected = 2;
				SalonTools.setSelection(1, dengjiList);
				break;
				
			case R.id.san:
				dengjiSelected = 3;
				SalonTools.setSelection(2, dengjiList);
				break;
				
			case R.id.si:
				dengjiSelected = 4;
				SalonTools.setSelection(3, dengjiList);
				break;
				
			case R.id.wu:
				dengjiSelected = 5;
				SalonTools.setSelection(4, dengjiList);
				break;
			}
		}
	};
	
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
	
	private class ProductAdapter extends BaseAdapter{

		private ArrayList<Product> list;
		private Context context;
		private int backcolor;
		
		public ProductAdapter(ArrayList<Product> list, Context context) {
			this.list = list;
			this.context = context;
			this.list.add(0, null);
			backcolor = context.getResources().getColor(R.color.activity_back);
			refreshHeight();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Product getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private void refreshHeight(){
			ViewGroup.LayoutParams params = product_list.getLayoutParams();
			int hdp = SalonTools.getProductHeight(list.size());
			params.height = DMUtil.getHeight(AdminVerifyBarberActivity.this, hdp);
			product_list.setLayoutParams(params);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final Product p = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_product_show_list, null);
				viewHolder.band = (TextView) convertView.findViewById(R.id.band);
				viewHolder.usage = (TextView) convertView.findViewById(R.id.usage);
				viewHolder.price = (TextView) convertView.findViewById(R.id.price);
				viewHolder.origin = (TextView) convertView.findViewById(R.id.origin);
				viewHolder.divider1 = (ImageView) convertView.findViewById(R.id.divider1);
				viewHolder.divider2 = (ImageView) convertView.findViewById(R.id.divider2);
				viewHolder.divider3 = (ImageView) convertView.findViewById(R.id.divider3);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			if(position == 0){
				viewHolder.band.setText(R.string.salon_reString20);
				viewHolder.usage.setText(R.string.salon_reString21);
				viewHolder.price.setText(R.string.salon_reString22);
				viewHolder.origin.setText(R.string.salon_reString42);
				viewHolder.band.setBackgroundColor(Color.TRANSPARENT);
				viewHolder.usage.setBackgroundColor(Color.TRANSPARENT);
				viewHolder.price.setBackgroundColor(Color.TRANSPARENT);
				viewHolder.origin.setBackgroundColor(Color.TRANSPARENT);
				viewHolder.divider1.setVisibility(View.INVISIBLE);
				viewHolder.divider2.setVisibility(View.INVISIBLE);
				viewHolder.divider3.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.band.setBackgroundColor(backcolor);
				viewHolder.usage.setBackgroundColor(backcolor);
				viewHolder.price.setBackgroundColor(backcolor);
				viewHolder.origin.setBackgroundColor(backcolor);
				viewHolder.band.setText(p.band);
				viewHolder.usage.setText(p.usage);
				viewHolder.price.setText(p.price);
				viewHolder.origin.setText(p.origin);
				viewHolder.divider1.setVisibility(View.VISIBLE);
				viewHolder.divider2.setVisibility(View.VISIBLE);
				viewHolder.divider3.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			private TextView band;
			private TextView usage;
			private TextView price;
			private TextView origin;
			private ImageView divider1;
			private ImageView divider2;
			private ImageView divider3;
		}
	}
}
