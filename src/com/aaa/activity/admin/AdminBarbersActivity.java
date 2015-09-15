package com.aaa.activity.admin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAdminService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.CHApplication;
import com.changhong.annotation.CHInjectView;
import com.llw.salon.R;

public class AdminBarbersActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.list)
	private ListView list;
	
	private ArrayList<BarberItem> datas = new ArrayList<BarberItem>();
	private MyAdapter adapter;
	
	private ISalonAdminService adminService = (ISalonAdminService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ADMIN_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_baString1);
		doGetDatas();
	}

	private void doGetDatas() {
		showWaitDialog();
		adminService.getUnchecked(Role.BARBER, null, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					ArrayList<SalonUser> users = (ArrayList<SalonUser>) response.getData();
					if(users != null){
						for(SalonUser user : users){
							BarberItem item = new BarberItem();
//						item.area = SalonTools.getDisplayArea(AdminBarbersActivity.this, user.getAreas());
							item.name = user.getPerson_Name();
							item.barberID = user.getId();
							datas.add(item);
						}
					}
					adapter = new MyAdapter(AdminBarbersActivity.this, datas, getCHApplication());
					list.setAdapter(adapter);
				}else{
					Toast.makeText(AdminBarbersActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private class BarberItem{
		public String area;
		public String name;
		public int barberID;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == 1001){
				int barberID = data.getIntExtra("ID", 0);
				adapter.remove(barberID);
			}
		}
	}
	
	private class MyAdapter extends BaseAdapter{
		private ArrayList<BarberItem> list;
		private Context context;
		
		public MyAdapter(Context context, ArrayList<BarberItem> list, CHApplication app){
			this.context = context;
			this.list = list;
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
		
		private void remove(int barberID){
			for(BarberItem item : list){
				if(item.barberID == barberID){
					list.remove(item);
					notifyDataSetChanged();
					break;
				}
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final BarberItem item = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_admin_barber, null);
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
//				viewHolder.area = (TextView) convertView.findViewById(R.id.area);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			try {
				viewHolder.name.setText(getString(R.string.admin_baString2, item.name));
//				viewHolder.area.setText(getString(R.string.admin_saString12, item.area));
				
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(AdminBarbersActivity.this, AdminVerifyBarberActivity.class);
						intent.putExtra("ID", item.barberID);
						startActivityForResult(intent, 1001);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			TextView name;
//			TextView area;
		}
	}
}
