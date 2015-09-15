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

public class AdminSalonsActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.list)
	private ListView list;

//	public static final int MODIFY = 101;
	public static final int REGISTER = 102;
	
	private ArrayList<SalonItem> datas = new ArrayList<SalonItem>();
	private MyAdapter adapter;
	
	private ISalonAdminService adminService = (ISalonAdminService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ADMIN_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.admin_saString1);
		doGetDatas();
	}

	private void doGetDatas() {
		showWaitDialog();
		adminService.getUnchecked(Role.SALON, null, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0){
					ArrayList<SalonUser> users = (ArrayList<SalonUser>) response.getData();
					if(users != null){
						for(SalonUser user : users){
							SalonItem item = new SalonItem();
//						item.area = SalonTools.getDisplayArea(AdminSalonsActivity.this, user.getAreas());
							item.name = user.getNick();
							item.salonID = user.getId();
							datas.add(item);
						}
					}
					adapter = new MyAdapter(AdminSalonsActivity.this, datas, getCHApplication());
					list.setAdapter(adapter);
				}else{
					Toast.makeText(AdminSalonsActivity.this, R.string.get_failed, Toast.LENGTH_SHORT).show();
				}
				hideAllDialog();
				return null;
			}
		});
	}
	
	private class SalonItem{
		public int salonID;
		public String name;
		public String area;
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
		private ArrayList<SalonItem> list;
		private Context context;
		
		public MyAdapter(Context context, ArrayList<SalonItem> list, CHApplication app){
			this.context = context;
			this.list = list;
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
		
		private void remove(int salonID){
			for(SalonItem item : list){
				if(item.salonID == salonID){
					list.remove(item);
					notifyDataSetChanged();
					break;
				}
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final SalonItem item = list.get(position);
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
				viewHolder.name.setText(getString(R.string.admin_saString11, item.name));
//				viewHolder.area.setText(getString(R.string.admin_saString12, item.area));
				
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(AdminSalonsActivity.this, AdminVerifySalonActivity.class);
						intent.putExtra("ID", item.salonID);
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
