package com.aaa.activity.salon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ISalonBarberService;

import com.aaa.activity.custom.CustomSendOrderActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class SalonActivity extends LlwTitleActivity{
 
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.photo1)
	private ImageView photo1;
	@CHInjectView(id = R.id.photo2)
	private ImageView photo2;
	@CHInjectView(id = R.id.photo3)
	private ImageView photo3;
	@CHInjectView(id = R.id.photo4)
	private ImageView photo4;
	@CHInjectView(id = R.id.table1)
	private LinearLayout table1;
	@CHInjectView(id = R.id.table2)
	private LinearLayout table2;
	@CHInjectView(id = R.id.name)
	private TextView name;
	@CHInjectView(id = R.id.address)
	private TextView address;
	@CHInjectView(id = R.id.barber_count)
	private TextView barber_count;
	@CHInjectView(id = R.id.mianji)
	private TextView mianji;
	@CHInjectView(id = R.id.gongwei)
	private TextView gongwei;
	@CHInjectView(id = R.id.xitouwei)
	private TextView xitouwei;
	@CHInjectView(id = R.id.extra_service)
	private TextView extra_service;
	@CHInjectView(id = R.id.recommend)
	private TextView recommend;
	@CHInjectView(id = R.id.list)
	private ListView product_list;
	@CHInjectView(id = R.id.order)
	private Button order;
	
	public static final String SALON = "SalonActivity.salon";
	public static final String ID = "SalonActivity.id";
	public static final String NAME = "SalonActivity.NAME";
	public static final String CAN_ORDER = "SalonActivity.CAN_ORDER";
	
	private SalonUser salon;
	private boolean canOrder = true;
	private ArrayList<String> dianmianPaths = new ArrayList<String>();
	private ArrayList<Product> products = new ArrayList<Product>();
	private CHBitmapCacheWork imgFetcher;
	private ProductAdapter productAdapter;
	private int couponId = 0;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	private ISalonBarberService barberService = (ISalonBarberService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BARBER_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		couponId = getIntent().getIntExtra(CustomSendOrderActivity.COUPON_ID, 0);
		canOrder = getIntent().getBooleanExtra(CAN_ORDER, true);
		salon = (SalonUser) getIntent().getSerializableExtra(SALON);
		if(salon == null){
			int id = getIntent().getIntExtra(ID, 0);
			String name = getIntent().getStringExtra(NAME);
			if(name != null){
				setTitile(name);
				this.name.setText(name);
			}
			doGetData(id);
		}else{
			initView();
		}
	}

	private void doGetData(int id) {
		showWaitDialog();
		accountService.getUser(id, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					salon = (SalonUser) response.getData();
					initView();
				}
				
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void initView() {
		dianmianPaths = (ArrayList<String>) salon.getPhotos().clone();
		
		String title = SalonTools.getName(salon);
		if(salon.getLevel() > 0){
			title += getString(R.string.barber_acString12, salon.getLevel());
		}
		setTitile(title);
		
		this.name.setText(salon.getNick());
		this.address.setText(salon.getAddr());
		this.barber_count.setText(getString(R.string.salon_acString2, salon.getSalonBarberCount()));
		this.mianji.setText(getString(R.string.salon_acString3, salon.getSize()));
		this.gongwei.setText(getString(R.string.salon_acString4, salon.getHairCount()));
		this.xitouwei.setText(getString(R.string.salon_acString5, salon.getWashCount()));
		this.extra_service.setText(SalonTools.composeExtraService(this, salon.getAddinServices()));
		recommend.setText(salon.getDesc());
		
		try {
			imgFetcher.loadFormCache(dianmianPaths.get(0), photo);
			dianmianPaths .remove(0);
			
			switch (dianmianPaths.size()) {
			case 0:
				table1.setVisibility(View.GONE);
				table2.setVisibility(View.GONE);
				break;
				
			case 1:
				imgFetcher.loadFormCache(dianmianPaths.get(0), photo1);
				table2.setVisibility(View.GONE);
				break;
				
			case 2:
				imgFetcher.loadFormCache(dianmianPaths.get(0), photo1);
				imgFetcher.loadFormCache(dianmianPaths.get(1), photo2);
				table2.setVisibility(View.GONE);
				break;
				
			case 3:
				imgFetcher.loadFormCache(dianmianPaths.get(0), photo1);
				imgFetcher.loadFormCache(dianmianPaths.get(1), photo2);
				imgFetcher.loadFormCache(dianmianPaths.get(2), photo3);
				break;
				
			case 4:
				imgFetcher.loadFormCache(dianmianPaths.get(0), photo1);
				imgFetcher.loadFormCache(dianmianPaths.get(1), photo2);
				imgFetcher.loadFormCache(dianmianPaths.get(2), photo3);
				imgFetcher.loadFormCache(dianmianPaths.get(3), photo4);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		products = salon.getProducts();
		if(products != null && products.size() > 0){
			productAdapter = new ProductAdapter(products, this);
			product_list.setAdapter(productAdapter);
		}
		
		if(CacheManager.INSTANCE.getCurrentUser() != null 
				&& CacheManager.INSTANCE.getCurrentUser().getRole() == Role.CUSTOM && canOrder){
			order.setVisibility(View.VISIBLE);
			order.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SalonActivity.this, SalonOwnBarbersActivity.class);
					intent.putExtra("ID", salon.getId());
					intent.putExtra(CustomSendOrderActivity.COUPON_ID, couponId);
					startActivityForResult(intent, 2000);
				}
			});
		}
		else if(CacheManager.INSTANCE.getCurrentUser() != null 
				&& CacheManager.INSTANCE.getCurrentUser().getRole() == Role.BARBER && canOrder){
			order.setVisibility(View.VISIBLE);
			order.setText(R.string.salon_acString15);
			order.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					doBindSalon();
				}
			});
		}
//		else if(CacheManager.INSTANCE.getCurrentUser() != null 
//				&& CacheManager.INSTANCE.getCurrentUser().getRole() == Role.ADMIN){
//			order.setVisibility(View.VISIBLE);
//			order.setText(R.string.salon_acString18);
//			order.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					final AppMainDialog dialog = new AppMainDialog(SalonActivity.this, R.style.appdialog);
//					dialog.withTitle(R.string.barber_orString16)
//					.withMessage(R.string.barber_orString19)
//					.setOKClick(R.string.ok_queren, new View.OnClickListener() {
//						
//						@Override
//						public void onClick(View arg0) {
//							doRecommend();
//							dialog.dismiss();
//						}
//					})
//					.setCancelClick(R.string.cancel_quxiao).show();
//				}
//			});
//		}
		
		SalonTools.scrollToHead(photo);
	}
	
	private void doBindSalon(){
		showWaitDialog();
		barberService.bindSalon(salon.getId(), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					Toast.makeText(SalonActivity.this, R.string.salon_acString16, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SalonActivity.this, R.string.salon_acString17, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private void doRecommend(){
		
	}
	
	private class ProductAdapter extends BaseAdapter{

		private ArrayList<Product> list;
		private Context context;
		private int backcolor;
		
		public ProductAdapter(ArrayList<Product> list, Context context) {
			this.list = list;
			this.list.add(0, null);
			this.context = context;
			backcolor = context.getResources().getColor(R.color.activity_back);
			refreshHeight();
		}
		
		public ProductAdapter(Context context) {
			list = new ArrayList<Product>();
			this.context = context;
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
			params.height = DMUtil.getHeight(SalonActivity.this, hdp);
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
