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
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ISalonAdminService;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.SalonTools;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;

public class AdminVerifySalonActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.name)
	private TextView name;
	@CHInjectView(id = R.id.contact)
	private TextView contact;
	@CHInjectView(id = R.id.phone)
	private TextView phone;
	@CHInjectView(id = R.id.district)
	private TextView district;
	@CHInjectView(id = R.id.address)
	private TextView address;
	@CHInjectView(id = R.id.mianji)
	private TextView mianji;
	@CHInjectView(id = R.id.jiantouwei)
	private TextView jiantouwei;
	@CHInjectView(id = R.id.xitouwei)
	private TextView xitouwei;
	@CHInjectView(id = R.id.extra_service)
	private TextView extra_service;
	@CHInjectView(id = R.id.dengji_txt)
	private TextView dengji_txt;
	@CHInjectView(id = R.id.accept_txt)
	private TextView accept_txt;
	@CHInjectView(id = R.id.accept_fencheng_txt)
	private TextView accept_fencheng_txt;
	@CHInjectView(id = R.id.accept_dengji_txt)
	private TextView accept_dengji_txt;
	@CHInjectView(id = R.id.recommend)
	private TextView recommend;
	
	@CHInjectView(id = R.id.barber_num)
	private TextView barber_num;
	@CHInjectView(id = R.id.look_barber)
	private Button look_barber;
	
	@CHInjectView(id = R.id.fengcheng)
	private LinearLayout fengcheng;
	@CHInjectView(id = R.id.dengji)
	private LinearLayout dengji;
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
	
	@CHInjectView(id = R.id.photo_list)
	private HorizontalListView photo_list;
	@CHInjectView(id = R.id.product_list)
	private ListView product_list;
	
	@CHInjectView(id = R.id.pass)
	private Button pass;
	@CHInjectView(id = R.id.reject)
	private Button reject;
	
	private ArrayList<ImageView> dengjiList  = new ArrayList<ImageView>();
	private PhotoAdapter photoAdapter;
	private ProductAdapter productAdapter;
	private int callType = 0;
	private int salonID = 0;
	private byte dengjiSelected = 0;
	private SalonUser salon;
	
	private ISalonAdminService adminService = (ISalonAdminService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ADMIN_SERVER);
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		salonID = getIntent().getIntExtra("ID", 0);
		setTitile(R.string.admin_saString13);
		
		showWaitDialog();
		accountService.getUser(salonID, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					salon = (SalonUser) response.getData();
					if(salon != null){
						initView();
					}
				}else{
					Toast.makeText(AdminVerifySalonActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
		
		pass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dengjiSelected == 0){
					Toast.makeText(AdminVerifySalonActivity.this, R.string.admin_vbString15, Toast.LENGTH_SHORT).show();
				}else{
					final AppMainDialog dialog = new AppMainDialog(AdminVerifySalonActivity.this, R.style.appdialog);
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
				final AppMainDialog dialog = new AppMainDialog(AdminVerifySalonActivity.this, R.style.appdialog);
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
		
		look_barber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminVerifySalonActivity.this, AdminSalonBarberActivity.class);
				intent.putExtra("ID", salonID);
				startActivity(intent);
			}
		});
	}
	
	private void doPass(){
		showWaitDialog();
		adminService.check(salonID, true, dengjiSelected, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					hideAllDialog();
					Toast.makeText(AdminVerifySalonActivity.this, R.string.admin_vbString19, Toast.LENGTH_SHORT).show();
					setMyResult();
				}else{
					Toast.makeText(AdminVerifySalonActivity.this, R.string.admin_vbString18, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}

	private void doReject(){
		showWaitDialog();
		adminService.check(salonID, false, dengjiSelected, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					hideAllDialog();
					Toast.makeText(AdminVerifySalonActivity.this, R.string.admin_vbString19, Toast.LENGTH_SHORT).show();
					setMyResult();
				}else{
					Toast.makeText(AdminVerifySalonActivity.this, R.string.admin_vbString18, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private void setMyResult(){
		Intent data = new Intent();
		data.putExtra("ID", salonID);
		setResult(RESULT_OK, data);
		finish();
	}
	
	private void initView(){
		String name = salon.getNick();
		String contact = salon.getPerson_Name();
		String phone = salon.getTel();
		String district = SalonTools.getDisplayArea(this, salon.getAreas());
		String address = salon.getAddr();
		String mianji = String.valueOf(salon.getSize());
		String jiantouwei = String.valueOf(salon.getHairCount());
		String xitouwei = String.valueOf(salon.getWashCount());
		String extra_service = SalonTools.composeExtraService(this, salon.getAddinServices());
		byte dengji = salon.getLevel();
		boolean accept_free_barber = salon.getAllowJoin();
		int accept_fengcheng = salon.getRatio();
		int accept_dengji = salon.getMinLevel();
		int barber_num = salon.getSalonBarberCount();
		
		ArrayList<String> salonPhoto = salon.getPhotos();
		ArrayList<Product> products = salon.getProducts();
		
		dengjiList.add(yi_point);
		dengjiList.add(er_point);
		dengjiList.add(san_point);
		dengjiList.add(si_point);
		dengjiList.add(wu_point);
		
		this.name.setText(name);
		this.contact.setText(contact);
		this.phone.setText(phone);
		this.district.setText(district);
		this.address.setText(address);
		this.mianji.setText(mianji);
		this.jiantouwei.setText(jiantouwei);
		this.xitouwei.setText(xitouwei);
		this.extra_service.setText(extra_service);
		this.barber_num.setText(String.valueOf(barber_num));
		recommend.setText(salon.getDesc());
		
		if(accept_free_barber){
			accept_txt.setText(R.string.admin_saString7);
			this.fengcheng.setVisibility(View.VISIBLE);
			this.dengji.setVisibility(View.VISIBLE);
			
			if(accept_fengcheng == 3){
				this.accept_fencheng_txt.setText(getString(R.string.admin_saString9, getString(R.string.salon_baString8)));
			}
			else if(accept_fengcheng == 4){
				this.accept_fencheng_txt.setText(getString(R.string.admin_saString9, getString(R.string.salon_baString9)));
			}
			else if(accept_fengcheng == 5){
				this.accept_fencheng_txt.setText(getString(R.string.admin_saString9, getString(R.string.salon_baString10)));
			}
			
			if(accept_dengji == 1){
				this.accept_dengji_txt.setText(getString(R.string.admin_saString10, getString(R.string.salon_baString3)));
			}
			else if(accept_dengji == 2){
				this.accept_dengji_txt.setText(getString(R.string.admin_saString10, getString(R.string.salon_baString4)));
			}
			else if(accept_dengji == 3){
				this.accept_dengji_txt.setText(getString(R.string.admin_saString10, getString(R.string.salon_baString5)));
			}
			else if(accept_dengji == 4){
				this.accept_dengji_txt.setText(getString(R.string.admin_saString10, getString(R.string.salon_baString6)));
			}
			else if(accept_dengji == 5){
				this.accept_dengji_txt.setText(getString(R.string.admin_saString10, getString(R.string.salon_baString7)));
			}
		}else{
			accept_txt.setText(R.string.admin_saString8);
			this.fengcheng.setVisibility(View.GONE);
			this.dengji.setVisibility(View.GONE);
		}
		
		SalonTools.setSelection(dengjiList.size(), dengjiList);
		
		if(products != null && products.size() > 0){
			productAdapter = new ProductAdapter(products, this);
			product_list.setAdapter(productAdapter);
		}
		
		if(salonPhoto != null && salonPhoto.size() > 0){
			photoAdapter = new PhotoAdapter(salonPhoto, this);
			photo_list.setAdapter(photoAdapter);
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
	
	private class PhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		private CHBitmapCacheWork imgFetcher;
		
		public PhotoAdapter(ArrayList<String> list, Context context) {
			this.list = list;
			this.context = context;
			imgFetcher = SalonTools.getImageFetcher(this.context, getCHApplication(), false, 0);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_show_list2, null);
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
			params.height = DMUtil.getHeight(AdminVerifySalonActivity.this, hdp);
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
