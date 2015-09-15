package com.aaa.activity.main;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.llw.salon.R;

public class SendCouponActivity extends LlwTitleActivity{
	
	@CHInjectView(id = R.id.done)
	private Button done;
	@CHInjectView(id = R.id.coupon)
	private EditText coupon;
	
	private int customId;
	
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.cus_reString8);
		customId = getIntent().getIntExtra("customId", 0);
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(SalonTools.editNotNull(coupon)){
					int value = Integer.valueOf(coupon.getText().toString());
					if(value > 0){
						doCoupon(value);
					}else{
						Toast.makeText(SendCouponActivity.this, R.string.cus_reString9, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	protected void doCoupon(int value) {
		showWaitDialog();
		bidService.sendCoupon(customId, value, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					Toast.makeText(SendCouponActivity.this, R.string.send_success, Toast.LENGTH_SHORT).show();
					hideAllDialog();
					finish();
				}else{
					Toast.makeText(SendCouponActivity.this, R.string.send_failed, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
}
