package com.aaa.activity.barber;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ISalonBarberService;

import com.aaa.activity.custom.CustomSendOrderActivity;
import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.activity.salon.SalonActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class BarberActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.order)
	private Button order;
	@CHInjectView(id = R.id.nick)
	private TextView nick;
	@CHInjectView(id = R.id.district)
	private TextView district;
	@CHInjectView(id = R.id.exp)
	private TextView exp;
	@CHInjectView(id = R.id.is_bid)
	private TextView is_bid;
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
	@CHInjectView(id = R.id.recommend)
	private TextView recommend;
	
	@CHInjectView(id = R.id.jian)
	private LinearLayout jian;
	@CHInjectView(id = R.id.tang)
	private LinearLayout tang;
	@CHInjectView(id = R.id.ran)
	private LinearLayout ran;
	@CHInjectView(id = R.id.hu_li)
	private LinearLayout hu_li;
	
	@CHInjectView(id = R.id.zuopin)
	private LinearLayout zuopin;
	@CHInjectView(id = R.id.product)
	private LinearLayout product;
	@CHInjectView(id = R.id.select_salon)
	private LinearLayout select_salon;
	
	@CHInjectView(id = R.id.zuopin_list)
	private HorizontalListView zuopin_list;
	@CHInjectView(id = R.id.salon_list)
	private GridView salon_list;
	@CHInjectView(id = R.id.product_list)
	private ListView product_list;
	
	public static final String BARBER = "BarberActivity.BARBER";
	public static final String SALON_BARBER = "BarberActivity.SALON_BARBER";
	public static final String ID = "BarberActivity.ID";
	public static final String NAME = "BarberActivity.NAME";
	public static final String FREE_BARBER = "BarberActivity.FREE_BARBER";
	public static final String IS_BID = "BarberActivity.IS_BID";
	public static final String SALON_VIEW = "BarberActivity.SALON_VIEW";
	public static final String OFFER_BID_VIEW = "BarberActivity.OFFER_BID_VIEW";
	
	private SalonUser barber;
	private boolean freeBarber = true;
	private boolean isBid = false;
	private boolean salonView = false;
	private zuopinPhotoAdapter zuopinAdapter;
	private SalonAdapter salonAdapter;
	private ProductAdapter productAdapter;
	private CHBitmapCacheWork imageFetcher;
	private ArrayList<String> zuopinPaths = new ArrayList<String>();
	private ArrayList<Product> products = new ArrayList<Product>();
	private ArrayList<SalonItem> salons = new ArrayList<SalonItem>();
	private ArrayList<TextView> prices = new ArrayList<TextView>();
	private ArrayList<SalonUser> salons2 = new ArrayList<SalonUser>();
	private SalonBarberInfoView salonBarber;
	private int couponId = 0;
	private OfferBiddingView offerBid = null;
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	private ISalonBarberService barberService = (ISalonBarberService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BARBER_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		prices.add(tang_duan);
		prices.add(tang_zhong);
		prices.add(tang_chang);
		prices.add(ran_duan);
		prices.add(ran_zhong);
		prices.add(ran_chang);
		prices.add(jian_duan);
		prices.add(jian_zhong);
		prices.add(jian_chang);
		
		offerBid = (OfferBiddingView) getIntent().getSerializableExtra(OFFER_BID_VIEW);
		couponId = getIntent().getIntExtra(CustomSendOrderActivity.COUPON_ID, 0);
		salonView = getIntent().getBooleanExtra(SALON_VIEW, false);
		
		isBid = getIntent().getBooleanExtra(IS_BID, false);
		
		freeBarber = getIntent().getBooleanExtra(FREE_BARBER, false);
		if(! freeBarber){
			product.setVisibility(View.GONE);
			select_salon.setVisibility(View.GONE);
			salonBarber = (SalonBarberInfoView) getIntent().getSerializableExtra(SALON_BARBER);
			
			if(salonBarber != null){
				barber = new SalonUser();
				barber.setId(salonBarber.getId());
				barber.setNick(salonBarber.getNick());
				barber.setPhoto(salonBarber.getPhoto());
				barber.setPrices(salonBarber.getPrices());
				barber.setHealth(salonBarber.getHealth());
				barber.setWorkYears(salonBarber.getWorkYears());
				barber.setCerts(salonBarber.getCerts());
				barber.setWorks(salonBarber.getWorks());
				initView();
			}
		}else{
			barber = (SalonUser) getIntent().getSerializableExtra(BARBER);
			if(barber == null){
				int id = getIntent().getIntExtra(ID, 0);
				String name = getIntent().getStringExtra(NAME);
				if(name != null){
					setTitile(name);
				}
				doGetData(id);
			}else{
				doGetSalons();
			}
		}
		
	}

	private void doGetData(int id) {
		showWaitDialog();
		accountService.getUser(id, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0 && response.getData() != null){
					barber = (SalonUser) response.getData();
					doGetSalons();
				}else{
					hideAllDialog();
				}
				
				return null;
			}
		});
	}

	protected void doGetSalons() {
		barberService.getBindSalons(barber.getId(), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					salons2 = (ArrayList<SalonUser>) response.getData();
					for(SalonUser user : salons2){
						SalonItem item = new SalonItem();
						item.facePhoto = user.getPhotos().get(0);
						item.ID = user.getId();
						item.name = user.getNick();
						salons.add(item);
					}
				}
				initView();
				hideAllDialog();
				return null;
			}
		});
	}

	private void initView() {
		if(offerBid != null){
			Toast.makeText(this, R.string.barber_acString10, Toast.LENGTH_SHORT).show();
		}
		
		setAdept(barber.getAdept());
		recommend.setText(barber.getDesc());
		
		String title = SalonTools.getName(barber);
		if(barber.getLevel() > 0){
			title += getString(R.string.barber_acString12, barber.getLevel());
		}
		setTitile(title);
		
		imageFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		try {
			imageFetcher.loadFormCache(barber.getPhoto(), photo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(isBid || salonView || CacheManager.INSTANCE.getCurrentUser() == null
					|| CacheManager.INSTANCE.getCurrentUser().getRole() != Role.CUSTOM){
			this.is_bid.setText(R.string.barber_acString9);
		}else{
			this.is_bid.setText(R.string.barber_acString8);
		}
		if(barber.getAreas() == null || barber.getAreas().equals("")){
			this.district.setVisibility(View.GONE);
		}else{
			this.district.setText(getString(R.string.barber_acString3, SalonTools.getDiaplayBarberArea(this, barber.getAreas())));
		}
		this.nick.setText(SalonTools.getName(barber));
		this.exp.setText(getString(R.string.barber_acString4, barber.getWorkYears()));

		for(int i = 0; i < 9; i++){
			this.prices.get(i).setText(String.valueOf(barber.getPrices()[i]));
		}
		
		zuopinPaths = barber.getWorks();
		
		if(zuopinPaths != null && zuopinPaths.size() > 0){
			zuopinAdapter = new zuopinPhotoAdapter(zuopinPaths, this);
			zuopin_list.setAdapter(zuopinAdapter);
		}else{
			zuopin.setVisibility(View.GONE);
		}
		
		if(freeBarber){
			if(salons != null && salons.size() > 0){
				salonAdapter = new SalonAdapter(salons, this);
				select_salon.setVisibility(View.VISIBLE);
				salon_list.setAdapter(salonAdapter);
			}else{
				select_salon.setVisibility(View.GONE);
			}
			
			products = barber.getProducts();
			if(products != null && products.size() > 0){
				product.setVisibility(View.VISIBLE);
				productAdapter = new ProductAdapter(products, this);
				productAdapter.refreshHeight();
				product_list.setAdapter(productAdapter);
			}else{
				product.setVisibility(View.GONE);
			}
		}
		
		if(isBid || salonView || CacheManager.INSTANCE.getCurrentUser() == null  
				|| CacheManager.INSTANCE.getCurrentUser().getRole() != Role.CUSTOM){
			order.setVisibility(View.GONE);
		}else{
			order.setVisibility(View.VISIBLE);
			order.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(freeBarber){
						if(salonAdapter != null && salonAdapter.getSelectedSalonId() != 0){
							Intent intent = new Intent(BarberActivity.this, CustomSendOrderActivity.class);
							intent.putExtra(CustomSendOrderActivity.CALL_TYPE, CustomSendOrderActivity.FREE_BARBER);
							intent.putExtra(CustomSendOrderActivity.BARBER_ID, barber.getId());
							intent.putExtra(CustomSendOrderActivity.SALON_ID, salonAdapter.getSelectedSalonId());
							intent.putExtra(CustomSendOrderActivity.COUPON_ID, couponId);
							intent.putExtra(OFFER_BID_VIEW, offerBid);
							startActivity(intent);
						}
						else if(salonAdapter == null){
							Toast.makeText(BarberActivity.this, getString(R.string.barber_acString7, nick), Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(BarberActivity.this, R.string.barber_acString6, Toast.LENGTH_SHORT).show();
						}
					}else{
						Intent intent = new Intent(BarberActivity.this, CustomSendOrderActivity.class);
						intent.putExtra(CustomSendOrderActivity.CALL_TYPE, CustomSendOrderActivity.SALON);
						intent.putExtra(CustomSendOrderActivity.BARBER_ID, salonBarber.getId());
						intent.putExtra(CustomSendOrderActivity.SALON_ID, salonBarber.getSalonInfoId());
						intent.putExtra(CustomSendOrderActivity.COUPON_ID, couponId);
						startActivity(intent);
					}
				}
			});
		}
		
		SalonTools.scrollToHead(photo);
	}
	
	private void setAdept(int adept) {
		if((adept & BarberRegisterActivity.MASK_JIAN) != 0){
			jian.setVisibility(View.VISIBLE);
		}
		if((adept & BarberRegisterActivity.MASK_TANG) != 0){
			tang.setVisibility(View.VISIBLE);
		}
		if((adept & BarberRegisterActivity.MASK_RAN) != 0){
			ran.setVisibility(View.VISIBLE);
		}
		if((adept & BarberRegisterActivity.MASK_HULI) != 0){
			hu_li.setVisibility(View.VISIBLE);
		}
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
		
		public zuopinPhotoAdapter(Context context) {
			list = new ArrayList<String>();
			this.context = context;
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
			params.height = DMUtil.getHeight(BarberActivity.this, hdp);
			product_list.setLayoutParams(params);
			CHLogger.d(this, "refreshHeight " + params.height);
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

	private class SalonItem{
		public int ID = 0;
		public String facePhoto = "";
		public String name = "";
		public boolean isSelected = false;
	}
	
	private OnItemClickListener salonListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			int id = salonAdapter.getItem(position).ID;
			if(id != 0){
				Intent intent = new Intent(BarberActivity.this, SalonActivity.class);
				intent.putExtra(SalonActivity.ID, id);
				startActivity(intent);
			}
		}
	};
	
	private class SalonAdapter extends BaseAdapter{

		private ArrayList<SalonItem> list;
		private Context context;
		private CHBitmapCacheWork imgFetcher;
		
		public SalonAdapter(ArrayList<SalonItem> list, Context context) {
			this.list = list;
			this.context = context;
			imgFetcher = SalonTools.getImageFetcher(this.context, getCHApplication(), false, R.drawable.default_barber_salon);
			refreshHeight();
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public SalonItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private void setSelected(int position){
			for(int i = 0; i < list.size(); i++){
				list.get(i).isSelected = i == position;
			}
		}
		
		private int getSelectedSalonId(){
			for(SalonItem item : list){
				if(item.isSelected){
					return item.ID;
				}
			}
			return 0;
		}
		
		private void refreshHeight(){
			ViewGroup.LayoutParams params = product_list.getLayoutParams();
			int layer = list.size() % 3 == 0 ? list.size() / 3 : list.size() / 3 + 1;
			int hdp = 90 * layer + 15;
			params.height = DMUtil.getHeight(BarberActivity.this, hdp);
			salon_list.setLayoutParams(params);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final SalonItem item = list.get(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_barber_activity_salon, null);
				vh.photo = (ImageView) convertView.findViewById(R.id.photo);
				vh.select = (Button) convertView.findViewById(R.id.select);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			if(isBid || salonView || CacheManager.INSTANCE.getCurrentUser() == null
					|| CacheManager.INSTANCE.getCurrentUser().getRole() != Role.CUSTOM){
				vh.select.setVisibility(View.GONE);
			}else{
				vh.select.setBackgroundResource(item.isSelected ? R.drawable.online : R.drawable.unonline);
				vh.select.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(item.isSelected){
							item.isSelected = false;
							notifyDataSetChanged();
						}else{
							setSelected(position);
							notifyDataSetChanged();
						}
					}
				});
			}
			
			if(! salonView){
				vh.photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						for(SalonUser user : salons2){
							if(user.getId() == item.ID){
								Intent intent = new Intent(context, SalonActivity.class);
								intent.putExtra(SalonActivity.SALON, user);
								intent.putExtra(SalonActivity.CAN_ORDER, false);
								context.startActivity(intent);
							}
						}
					}
				});
			}
			
			try {
				vh.name.setText(item.name);
				this.imgFetcher.loadFormCache(item.facePhoto, vh.photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			ImageView photo;
			Button select;
			TextView name;
		}
	}
}