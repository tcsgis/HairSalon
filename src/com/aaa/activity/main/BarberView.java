package com.aaa.activity.main;

import java.util.ArrayList;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.barber.BarberActivity;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.activity.BaseActivity;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshListView;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.Mode;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.OnRefreshListener2;
import com.changhong.activity.widget.other.pull2refresh.PullToRefreshBase.OnSmoothScrollFinishedListener;
import com.changhong.annotation.CHInjectView;
import com.changhong.common.CHStringUtils;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BarberView extends LinearLayout implements	OnRefreshListener2<ListView>, OnSmoothScrollFinishedListener{

	@CHInjectView(id = R.id.list)
	private PullToRefreshListView mListView;
	@CHInjectView(id = R.id.list_search_null)
	private TextView mNullText;
	@CHInjectView(id = R.id.txt_area)
	private TextView txt_area;
	@CHInjectView(id = R.id.btn_search)
	private ImageView btn_search;
	@CHInjectView(id = R.id.edit_search)
	private EditText edit_search;
	
	private final int COUNT_PER_PAGE = 1000;
	
	private Context mContext = null;
	private BaseActivity mActivity = null;
	private CHApplication mApp = null;
	private MyAdapter mAdapter;
	private ArrayList<SalonUser> mListData;
	private ArrayList<SalonUser> mListSalon;
	private String mSearch;
	private String mLastSearch;
	private int mSearchCount = 0;
	private String lastDistrict;
	private MyHandler mHandler = new MyHandler();
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);

	public BarberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.barber_view, this, true);
	}
	
	public void initView(BaseActivity activity, CHApplication app){
		mActivity = activity;
		mApp = app;
		enablePull();
		edit_search.setHint(R.string.barber_string1);
		
		mListView.setOnRefreshListener(this);
		mListView.getLoadingLayoutProxy(false, true).setLoadingDrawable(null);
		
		txt_area.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DistrictActivity.class);
				intent.putExtra("address", SalonTools.getDistrict(mActivity));
				mActivity.startActivityForResult(intent, MainActivity.SELECT_DISTRICT);
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doSearch(false, false, true, false);
			}
		});
	}
	
	public void refresh(boolean b){
		txt_area.setText(SalonTools.getArea(mActivity));
		String district = SalonTools.getDistrict(mActivity);
		if(district != null && ! district.equals(lastDistrict)){
			lastDistrict = district;
			doSearch(false, false, false, true);
		}
	}
	
	private class MyHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 100){
				ResponseBean<?> response = (ResponseBean<?>) msg.obj;
				boolean isPullUp = msg.arg1 > 0 ? true : false;
				boolean isPullDown = msg.arg2 > 0 ? true : false;
				if(response != null && response.getState() >= 0){
					try {
						mListSalon = (ArrayList<SalonUser>) response.getData();
						
						if(isPullDown){
							if(mListData != null){
								initList(false);
							}
						}else{
							if(isPullUp && mListSalon != null && mListSalon.size() == 0){
								Toast.makeText(mContext, R.string.pull_refresh_no_more_info, Toast.LENGTH_SHORT).show();
								disablePull();
							}
							else if(mListSalon != null && mListSalon.size() < COUNT_PER_PAGE){
								disablePull();
								if(isPullUp){
									mListView.customResetRefresh(BarberView.this);
								}else{
									initList(false);
								}
							}
							else{
								if(isPullUp){
									mListView.customResetRefresh(BarberView.this);
								}else{
									initList(false);
								}
							}
							mLastSearch = mSearch;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mListView.onRefreshComplete();
				mActivity.hideAllDialog();
			}
		}
	}
	
	private void initList(boolean isPullUp) {
		if(mListData == null){
			mListData = new ArrayList<SalonUser>();
		}
		
		if(! isPullUp){
			mListView.setAdapter(null);
			mNullText.setText(R.string.search_null);
			mListData.clear();
			
			if(!(mListSalon == null ||mListSalon.isEmpty())){
				for(SalonUser salon : mListSalon){
					mListData.add(salon);
				}
				
				mAdapter = new MyAdapter(mContext, mListData, mApp);
				mListView.setAdapter(mAdapter);
			}
		}else{
			if(!(mListSalon == null || mListSalon.isEmpty())){
				mListData.clear();
				for(SalonUser salon : mListSalon){
					mListData.add(salon);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void doSearch(final boolean isPullUp, final boolean isPullDown, boolean click, boolean areaChange) {
		mSearch = CHStringUtils.handleInputSpace(edit_search.getText().toString());
		
		if(! validModify(mSearch, mLastSearch, isPullUp, isPullDown, click, areaChange)) return;
		if(! isPullUp && ! isPullDown) mActivity.showWaitDialog();
			
		accountService.searchUser(Role.BARBER, null, SalonTools.getSearchDistrict(mActivity), 
				new AsyncResponseCompletedHandler<String>() {

					@Override
					public String doCompleted(ResponseBean<?> response,
							ChCareWepApiServiceType servieType) {
						
						Message msg = mHandler.obtainMessage(100, response);
						msg.arg1 = isPullUp ? 1 : -1;
						msg.arg2 = isPullDown ? 1 : -1;
						mHandler.sendMessage(msg);
						lastDistrict = SalonTools.getDistrict(mActivity);
						return null;
					}
		});
	}
	

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == MainActivity.SELECT_DISTRICT){
				SalonTools.saveDistrict(mActivity, data.getStringExtra("address"));
				txt_area.setText(SalonTools.getArea(mActivity));
				String district = SalonTools.getDistrict(mActivity);
				edit_search.setText(null);
				if(district != null && ! district.equals(lastDistrict)){
					lastDistrict = district;
					doSearch(false, false, false, true);
				}
			}
		}
	}
	
	private class MyAdapter extends BaseAdapter{
		private ArrayList<SalonUser> list;
		private Context context;
		private CHBitmapCacheWork imageFetcher;
		
		public MyAdapter(Context context, ArrayList<SalonUser> list, CHApplication app){
			this.context = context;
			this.list = list;
			imageFetcher = SalonTools.getImageFetcher(context, app, false, 0);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_barber_view, null);
				viewHolder.portrait = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.rate = (RatingBar) convertView.findViewById(R.id.rate);
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.area = (TextView) convertView.findViewById(R.id.area);
				viewHolder.exp = (TextView) convertView.findViewById(R.id.exp);
				viewHolder.level = (TextView) convertView.findViewById(R.id.level);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			try {
				if(item.getLevel() > 0){
					viewHolder.level.setText(context.getString(R.string.barber_string3, item.getLevel()));
				}else{
					viewHolder.level.setVisibility(View.INVISIBLE);
				}
				
				viewHolder.name.setText(SalonTools.getName(item));
				viewHolder.area.setText(SalonTools.getDiaplayBarberArea(context, item.getAreas()));
				viewHolder.exp.setText(mContext.getResources().getString(R.string.barber_string2, item.getWorkYears()));
				viewHolder.rate.setRating(item.getAvgScore());
				imageFetcher.loadFormCache(item.getPhoto(), viewHolder.portrait);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BarberActivity.class);
					intent.putExtra(BarberActivity.BARBER, item);
					intent.putExtra(BarberActivity.FREE_BARBER, true);
					mActivity.startActivity(intent);
				}
			});
			
			return convertView;
		}
		
		final class ViewHolder{
			ImageView portrait;
			TextView name;
			TextView area;
			TextView exp;
			TextView level;
			RatingBar rate;
		}
	}

	@Override
	public void onSmoothScrollFinished() {
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		doSearch(false, true, false, false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		doSearch(true, false, false, false);
	}

	private void enablePull(){
		mListView.setMode(Mode.BOTH);
	}
	
	private void disablePull(){
		mListView.setMode(Mode.PULL_FROM_START);
	}
	
	private boolean validModify(String search, String lastSearch, boolean isPullUp, 
			boolean isPullDown, boolean click, boolean areaChange) {
		
		boolean result = true;
		
		if(isPullDown) return true;
		
		if (search.equals("") && click) {
			mSearchCount = 0;
			Toast.makeText(mContext, R.string.salon_string4, Toast.LENGTH_SHORT).show();
			result = false;
		} 
		else if (search.equals(lastSearch) && ! areaChange) {
			if(isPullUp){
				mSearchCount++;
			}else{
				mListView.onRefreshComplete();
				result = false;
			}
		}else{
			if(! isPullUp){
				mSearchCount = 0;
			}else{
				mSearchCount++;
			}
		}
		return result;
	} 
}