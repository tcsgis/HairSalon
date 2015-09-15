package com.aaa.activity.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.util.DMUtil;
import com.aaa.util.OfferStatus;
import com.aaa.util.PhotoType;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.aaa.util.Status;
import com.changhong.CHApplication;
import com.changhong.activity.BaseActivity;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshListView;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.Mode;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.OnRefreshListener2;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.OnSmoothScrollFinishedListener;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BidView  extends LinearLayout implements OnRefreshListener2<ListView>, OnSmoothScrollFinishedListener{

	@CHInjectView(id = R.id.btn_left)
	public Button mBtnLeft;
	@CHInjectView(id = R.id.txt_title)
	public TextView mTitle;
	
	@CHInjectView(id = R.id.custom)
	private View custom;
	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	
	@CHInjectView(id = R.id.has_no_bid)
	private RelativeLayout has_no_bid;
	@CHInjectView(id = R.id.fabu)
	public Button fabu;
	@CHInjectView(id = R.id.district)
	public TextView district;
	@CHInjectView(id = R.id.detail)
	public EditText detail;
	
	@CHInjectView(id = R.id.has_bid)
	private RelativeLayout has_bid;
	@CHInjectView(id = R.id.list)
	private HorizontalListView barberList;
	@CHInjectView(id = R.id.cancel)
	public Button cancel;
	@CHInjectView(id = R.id.done)
	public Button done;
	
	@CHInjectView(id = R.id.grid)
	private PullToRefreshListView grid;
	
	private final int BID_PER_PAGE = 12;
	
	private final int ORDER_BID = 19001;
	
	private Context mContext = null;
	private BaseActivity activity;
	private CHApplication app;
	private PhotoSelectPopupView mPopupAltView;
	private Uri mPhotoUri;
	
	private String photoPath;
	private BidAdapter bidAdapter;
	private byte role = Role.UNDIFINED;
	private CHBitmapCacheWork imgFetcher;
	private BarberAdapter barberAdapter;
	private OfferView offerView;
	private ArrayList<OfferBiddingView> bidBarbers;
	private ArrayList<OfferView> offers;
	private ArrayList<BarberItem> datas = new ArrayList<BarberItem>();
	
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	private ISalonFileService fileService = (ISalonFileService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_FILE_SERVER);
	
	public BidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.bid_view, this, true);
	}
	
	public void initView(BaseActivity activity, CHApplication app){
		this.activity = activity;
		this.app = app;
		mBtnLeft.setVisibility(View.GONE);
	}
	
	public void refresh(){
		offerView = null;
		
		if(CacheManager.INSTANCE.getCurrentUser() != null
				&& CacheManager.INSTANCE.getCurrentUser().getRole() == Role.CUSTOM){
			
			activity.showWaitDialog();
			bidService.getCustomBid(new AsyncResponseCompletedHandler<String>() {

				@Override
				public String doCompleted(ResponseBean<?> response,
						ChCareWepApiServiceType servieType) {
					
					if(response.getState() >= 0 && response.getData() != null){
						List<OfferView> list = (List<OfferView>) response.getData();
						if(list != null && list.size() > 0){
							for(OfferView offer : list){
								if(offer.getOfferStatus() == OfferStatus.Pending || offer.getOfferStatus() == OfferStatus.Full){
									offerView = offer;
									break;
								}
							}
						}
						if(offerView != null){
							getBidBarbers();
						}else{
							refresh2();
							activity.hideAllDialog();
						}
					}else{
						refresh2();
						activity.hideAllDialog();
					}
					return null;
				}
			});
		}else{
			refresh2();
		}
	}
	
	private void refresh2(){
		if(CacheManager.INSTANCE.getCurrentUser() == null || CacheManager.INSTANCE.getCurrentUser().getRole() != Role.CUSTOM){
			mTitle.setText(R.string.bid_string1);
			custom.setVisibility(View.GONE);
			grid.setVisibility(View.VISIBLE);
			enablePull();
			doGetCommenData();
		}else{
			mTitle.setText(R.string.bid_string8);
			custom.setVisibility(View.VISIBLE);
			grid.setVisibility(View.GONE);
			doGetCustomData();
		}
	}
	
	private void getBidBarbers(){
		bidService.getBid(offerView.getId(), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0 && response.getData() != null){
					bidBarbers = (ArrayList<OfferBiddingView>) response.getData();
					datas.clear();
					for(OfferBiddingView bid : bidBarbers){
						BarberItem item = new BarberItem();
						item.barberID = bid.getBarberId();
						item.name = bid.getName();
						item.photo = bid.getPhoto();
						item.price = (int) bid.getPrice();
						datas.add(item);
					}
				}
				refresh2();
				activity.hideAllDialog();
				return null;
			}
			
		});
	}
	
	private void doGetCustomData(){
		//check whether has bid
		if(offerView != null){
			grid.setVisibility(View.GONE);
			has_no_bid.setVisibility(View.GONE);
			cancel.setVisibility(View.VISIBLE);
			has_bid.setVisibility(View.VISIBLE);
			imgFetcher = SalonTools.getImageFetcher(mContext, app, false, 0);
			
			try {
				imgFetcher.loadFormCache(offerView.getPics(), photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			ArrayList<BarberItem> barbers = new ArrayList<BidView.BarberItem>();
			barberAdapter = new BarberAdapter(datas, mContext, app);
			barberList.setAdapter(barberAdapter);
			
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final AppMainDialog dialog = new AppMainDialog(mContext, R.style.appdialog);
					dialog.withTitle(R.string.barber_orString16)
					.withMessage(R.string.bid_acString14)
					.setOKClick(R.string.ok_queren, new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							doCancel();
							dialog.dismiss();
						}
					})
					.setCancelClick(R.string.cancel_quxiao).show(); 
				}
			});
			
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BarberItem item = barberAdapter.getSelected();
					if(item != null){
						for(OfferBiddingView barber : bidBarbers){
							if(barber.getBarberId() == item.barberID){
								Intent intent = new Intent(mContext, BarberActivity.class);
								intent.putExtra(BarberActivity.ID, item.barberID);
								intent.putExtra(BarberActivity.FREE_BARBER, true);
								intent.putExtra(BarberActivity.OFFER_BID_VIEW, barber);
								activity.startActivityForResult(intent, ORDER_BID);
								break;
							}
						}
					}else{
						toast(R.string.bid_acString15);
					}
				}
			});
		}else{
			grid.setVisibility(View.GONE);
			has_no_bid.setVisibility(View.VISIBLE);
			has_bid.setVisibility(View.GONE);
			cancel.setVisibility(View.GONE);
			
			mPopupAltView = new PhotoSelectPopupView(activity);
			photo.setImageResource(R.drawable.add_salon_photo);
			photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPopupAltView.show();
				}
			});
			
			detail.setText("");
			district.setText("");
			district.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent2 = new Intent(activity, DistrictActivity.class);
					intent2.putExtra("address", district.getText().toString());//不传参数也可以，就不会有默认选中某个值
					activity.startActivityForResult(intent2, 1111);
				}
			});
			
			fabu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(photoPath == null){
						Toast.makeText(mContext, R.string.bid_acString13, Toast.LENGTH_SHORT).show();
					}
					else if(district.getText() == null || district.getText().toString().trim().length() == 0){
						Toast.makeText(mContext, R.string.bid_acString12, Toast.LENGTH_SHORT).show();
					}else{
						doFabu();
					}
				}
			});
		}
	}
	
	private void doGetCommenData(){
		activity.showWaitDialog();
		bidService.getBids(null, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				ArrayList<BidItem> datas = new ArrayList<BidView.BidItem>();
				if(response.getState() >= 0 && response.getData() != null){
					offers = (ArrayList<OfferView>) response.getData();
					
					for(int i = 0; i < offers.size(); i++){
						BidItem item = new BidItem();
						if(offers.size() > i && offers.get(i) != null){
							item.ID1 = offers.get(i).getId();
							item.nick1 = offers.get(i).getuserNick();
							item.photo1 = offers.get(i).getPics();
						}else{
							break;
						}
						
						i++;
						if(offers.size() > i && offers.get(i) != null){
							item.ID2 = offers.get(i).getId();
							item.nick2 = offers.get(i).getuserNick();
							item.photo2 = offers.get(i).getPics();
						}else{
							datas.add(item);
							break;
						}
						
						i++;
						if(offers.size() > i && offers.get(i) != null){
							item.ID3 = offers.get(i).getId();
							item.nick3 = offers.get(i).getuserNick();
							item.photo3 = offers.get(i).getPics();
							datas.add(item);
						}else{
							datas.add(item);
							break;
						}
					}
				}
				bidAdapter = new BidAdapter(datas, mContext, app);
				grid.setAdapter(bidAdapter);
				activity.hideAllDialog();
				return null;
			}
		});
	}
	
	private void doFabu(){
		activity.showWaitDialog();
		ArrayList<String> ss = new ArrayList<String>();
		ss.add(photoPath);
		fileService.uploadFiles(ss, DMUtil.getBidPhotoWidth(activity), DMUtil.getBidPhotoHeight(activity), 
				new AsyncResponseCompletedHandler<String>() {
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					try {
						ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
						fabu(photos.get(0));
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(mContext, R.string.publish_fail, Toast.LENGTH_SHORT).show();
						activity.hideAllDialog();
					}
				}else{
					Toast.makeText(mContext, R.string.publish_fail, Toast.LENGTH_SHORT).show();;
					activity.hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private void fabu(final String photo){
		bidService.publishBid(photo, SalonTools.getText(detail), SalonTools.getSearchAreaString(activity, district.getText().toString()),
				new AsyncResponseCompletedHandler<String>() {
			
			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					int id = (Integer) response.getData();
					offerView = new OfferView();
					offerView.setId(id);
					offerView.setArea(district.getText().toString());
					offerView.setDesc(SalonTools.getText(detail));
					offerView.setPics(photo);
					refresh();
					Toast.makeText(mContext, R.string.publish_success, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(mContext, R.string.publish_fail, Toast.LENGTH_SHORT).show();
				}
				activity.hideAllDialog();
				return null;
			}
		});
	}
	
	private void doCancel(){
		activity.showWaitDialog();
		bidService.cancelBid(offerView.getId(), new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					Toast.makeText(mContext, R.string.cancel_success, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(mContext, R.string.cancel_fail, Toast.LENGTH_SHORT).show();
				}
				offerView = null;
				refresh2();
				activity.hideAllDialog();
				return null;
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_LOCAL){
					if (data != null) {
						String filepath = data.getStringExtra("filepath");
						Uri uri = Uri.fromFile(new File(filepath));
						mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_BID);
					}
				}
				else if(requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_CAMERA) {
					Uri uri = mPopupAltView.getPhotoUri();
					mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_BID);
				}
				else if(requestCode == PhotoSelectPopupView.CUT_PHOTO){
					mPhotoUri = mPopupAltView.getPhotoUri();
					if(mPhotoUri != null && mPhotoUri.getPath() != null){
						File file = new File(mPhotoUri.getPath());
						if(file.exists() && file.isFile()){
							photoPath = file.getPath();
							photo.setImageBitmap(PictureUtil.
									decodeSampledBitmapFromFile(file.getPath(), 
											photo.getWidth(), 
											photo.getHeight()));
						}
						
					}
				}
				
				if(requestCode == 1111){
					district.setText(data.getStringExtra("address"));
				}
				
				if(requestCode == MainActivity.SELECT_DISTRICT){
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class BidItem{
		public int ID1;
		public int ID2;
		public int ID3;
		public String nick1 = null;
		public String nick2 = null;
		public String nick3 = null;
		public String photo1;
		public String photo2;
		public String photo3;
	}
	
	private class BidAdapter extends BaseAdapter{

		private ArrayList<BidItem> list;
		private Context context;
		private CHBitmapCacheWork imageFetcher;
		
		public BidAdapter(ArrayList<BidItem> list, Context context, CHApplication app) {
			this.list = list;
			this.context = context;
			imageFetcher = SalonTools.getImageFetcher(context, app, false, 0);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public BidItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final BidItem item = list.get(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_bid_grid, null);
				vh.photo1 = (ImageView) convertView.findViewById(R.id.photo1);
				vh.photo2 = (ImageView) convertView.findViewById(R.id.photo2);
				vh.photo3 = (ImageView) convertView.findViewById(R.id.photo3);
				vh.nick1 = (TextView) convertView.findViewById(R.id.nick1);
				vh.nick2 = (TextView) convertView.findViewById(R.id.nick2);
				vh.nick3 = (TextView) convertView.findViewById(R.id.nick3);
				vh.item1 = (LinearLayout) convertView.findViewById(R.id.item1);
				vh.item2 = (LinearLayout) convertView.findViewById(R.id.item2);
				vh.item3 = (LinearLayout) convertView.findViewById(R.id.item3);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			try {
				if(item.nick1 == null){
					vh.item1.setVisibility(View.GONE);
				}else{
					vh.item1.setVisibility(View.VISIBLE);
					vh.nick1.setText(item.nick1);
					imageFetcher.loadFormCache(item.photo1, vh.photo1);
					vh.item1.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							click(item.ID1);
						}
					});
				}
				
				if(item.nick2 == null){
					vh.item2.setVisibility(View.GONE);
				}else{
					vh.item2.setVisibility(View.VISIBLE);
					vh.nick2.setText(item.nick2);
					imageFetcher.loadFormCache(item.photo2, vh.photo2);
					vh.item2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							click(item.ID2);
						}
					});
				}
				
				if(item.nick3 == null){
					vh.item3.setVisibility(View.GONE);
				}else{
					vh.item3.setVisibility(View.VISIBLE);
					vh.nick3.setText(item.nick3);
					imageFetcher.loadFormCache(item.photo3, vh.photo3);
					vh.item3.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							click(item.ID3);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		private void click(int bidID){
			if(CacheManager.INSTANCE.getCurrentUser() == null
					|| CacheManager.INSTANCE.getCurrentUser().getRole() != Role.BARBER){
				toast(R.string.bid_string6);
			}
			else if(CacheManager.INSTANCE.getCurrentUser().getStatus() != Status.NORMAL){
				toast(R.string.bid_string7);
			}
			else{
				for(OfferView offer : offers){
					if(offer.getId() == bidID){
						Intent intent = new Intent(context, BidActivity.class);
						intent.putExtra("offer", offer);
						context.startActivity(intent);
					}
				}
			}
		}
		
		final class ViewHolder{
			public TextView nick1;
			public TextView nick2;
			public TextView nick3;
			public ImageView photo1;
			public ImageView photo2;
			public ImageView photo3;
			public LinearLayout item1;
			public LinearLayout item2;
			public LinearLayout item3;
		}
	}
	
	private class BarberItem{
		public String photo = null;
		public int price = 0;
		public String name = "";
		public boolean selected = false;
		public int barberID = 0;
	}
	
	private class BarberAdapter extends BaseAdapter{

		private ArrayList<BarberItem> list;
		private Context context;
		private CHBitmapCacheWork imgFetcher;
		
		public BarberAdapter(ArrayList<BarberItem> list, Context c, CHApplication app){
			this.list = list;
			this.context = c;
			imgFetcher = SalonTools.getImageFetcher(this.context, app, false, 0);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public BarberItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private BarberItem getSelected(){
			BarberItem result = null;
			for(BarberItem item : list){
				if(item.selected){
					result = item;
					break;
				}
			}
			return result;
		}
		
		private void setDatas(ArrayList<BarberItem> l){
			list = l;
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			final BarberItem item = list.get(position);
			if(convertView == null){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_bid_barber, null);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				vh.price = (TextView) convertView.findViewById(R.id.price);
				vh.photo = (ImageView) convertView.findViewById(R.id.photo);
				vh.select = (Button) convertView.findViewById(R.id.select);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			
			try {
				vh.name.setText(item.name);
				vh.price.setText(context.getString(R.string.bid_acString17, item.price));
				vh.select.setBackgroundResource(item.selected ? R.drawable.online : R.drawable.unonline);
				if(item.photo != null){
					imgFetcher.loadFormCache(item.photo, vh.photo);
				}
				vh.select.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(item.selected){
							item.selected = false;
						}else{
							for(int i = 0; i < list.size(); i++){
								if(i == position){
									list.get(i).selected = true;
								}else{
									list.get(i).selected = false;
								}
							}
						}
						notifyDataSetChanged();
					}
				});
				vh.photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, BarberActivity.class);
						intent.putExtra(BarberActivity.ID, item.barberID);
						intent.putExtra(BarberActivity.FREE_BARBER, true);
						intent.putExtra(BarberActivity.IS_BID, true);
						context.startActivity(intent);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public TextView price;
			public TextView name;
			public ImageView photo;
			public Button select;
		}
	}
	
	@Override
	public void onSmoothScrollFinished() {
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//		doSearch(true);
	}
	
	private void enablePull(){
		grid.setMode(Mode.PULL_FROM_END);
	}
	
	private void disablePull(){
		grid.setMode(Mode.MANUAL_REFRESH_ONLY);
	}
	
	private void toast(int msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
}
