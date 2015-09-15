package com.aaa.activity.salon;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.SalonTools;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class SalonAddBarberActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.grid)
	private GridView grid;
	@CHInjectView(id = R.id.btn_right)
	private Button btn_right;
	
	public static final int ADD = 1000;
	public static final int MODIFY = 1001;
	
	private final int ADD_BARBER = 100;
	private PhotoAdapter photoAdapter;
	private ArrayList<SalonBarberInfoView> barbers = new ArrayList<SalonBarberInfoView>();
	private CHBitmapCacheWork imgFetcher;
	
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_abString1);
		setRightBtn(true, 0);
		btn_right.setText(R.string.salon_abString4);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		int id = getIntent().getIntExtra("ID", 0);
		if(id != 0){
			
		}
		
		showWaitDialog();
		salonService.getMyBarber(CacheManager.INSTANCE.getCurrentUser().getId(),
				new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response != null && response.getState() >= 0){
					if(response.getData() != null){
						barbers = (ArrayList<SalonBarberInfoView>) response.getData();
					}
				}
				initView();
				hideAllDialog();
				return null;
			}
		});
	}
	
	@Override
	protected void clickRight() {
		Intent intent = new Intent(SalonAddBarberActivity.this, SalonRegisterBarberActivity.class);
		startActivityForResult(intent, ADD_BARBER);
	}
	
	private void initView(){
		photoAdapter = new PhotoAdapter(barbers, SalonAddBarberActivity.this);
		grid.setAdapter(photoAdapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(SalonAddBarberActivity.this, SalonRegisterBarberActivity.class);
				intent.putExtra("barber", photoAdapter.getItem(position));
				startActivityForResult(intent, ADD_BARBER);
			}
		});
		
		grid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> list, View arg1, int position, long arg3) {
				final SalonBarberInfoView s = photoAdapter.getItem(position);
				if(s != null){
					final AppMainDialog dialog = new AppMainDialog(SalonAddBarberActivity.this, R.style.appdialog);
					dialog.withTitle(R.string.dialog_title)
							.withMessage(R.string.salon_abString2)
									.setOKClick(R.string.ok_queren, new View.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											doDelete(s.getId());
											dialog.dismiss();
										}
									})
									.setCancelClick(R.string.cancel_quxiao).show();
				}
				return true;
			}
		});
	}
	
	private void doDelete(final int barberId){
		showWaitDialog();
		salonService.deleteMyBarber(barberId, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					photoAdapter.removeBarber(barberId);
				}else{
					Toast.makeText(SalonAddBarberActivity.this, R.string.salon_abString3, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
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
		
		public PhotoAdapter(Context context) {
			list = new ArrayList<SalonBarberInfoView>();
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
		
		private int getBarberCount(){
			if(list != null && list.size() > 0){
				return list.size(); 
			}
			return 0;
		}

		private void addBarber(SalonBarberInfoView barber){
			if(list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					if(list.get(i) != null && list.get(i).getId() == barber.getId()){
						list.remove(i);
						list.add(i, barber);
						notifyDataSetChanged();
						return;
					}
				}
			}
			list.add(barber);
			notifyDataSetChanged();
		}
		
		private void removeBarber(SalonBarberInfoView barber){
			list.remove(barber);
			photoAdapter.notifyDataSetChanged();
		}
		
		private void removeBarber(int barberId){
			for(SalonBarberInfoView barber : list){
				if(barber != null && barber.getId() == barberId){
					list.remove(barber);
					break;
				}
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final SalonBarberInfoView barber = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_salon_add_barber, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.nick = (TextView) convertView.findViewById(R.id.nick);
				viewHolder.delete = (ImageView) convertView.findViewById(R.id.button);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			
			try {
				imgFetcher.loadFormCache(barber.getPhoto(), viewHolder.photo);
				viewHolder.nick.setText(barber.getNick());
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public ImageView delete;
			public TextView nick;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ADD_BARBER){
			if(resultCode == ADD){
				SalonBarberInfoView barber = (SalonBarberInfoView) data.getSerializableExtra("barber");
				if(barber != null){
					photoAdapter.addBarber(barber);
				}
			}
			else if(resultCode == MODIFY){
				SalonBarberInfoView barber = (SalonBarberInfoView) data.getSerializableExtra("barber");
				if(barber != null){
					photoAdapter.addBarber(barber);
				}
			}
		}
	}
	
	@Override
	protected void clickLeft() {
		Intent data = new Intent();
		data.putExtra("barberCount", photoAdapter.getBarberCount());
		setResult(RESULT_OK, data);
		super.clickLeft();
	}
	
	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("barberCount", photoAdapter.getBarberCount());
		setResult(RESULT_OK, data);
		super.onBackPressed();
	}
}
