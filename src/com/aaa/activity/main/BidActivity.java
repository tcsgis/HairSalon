package com.aaa.activity.main;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.util.SalonTools;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class BidActivity extends LlwTitleActivity{

	@CHInjectView(id = R.id.photo)
	private ImageView photo;
	@CHInjectView(id = R.id.price)
	private EditText price;
	@CHInjectView(id = R.id.district)
	private TextView district;
	@CHInjectView(id = R.id.detail)
	private TextView detail;
	@CHInjectView(id = R.id.done)
	private Button done;
	@CHInjectView(id = R.id.price_ll)
	private LinearLayout price_ll;
	
	private CHBitmapCacheWork imgFetcher;
	private OfferView offer;
	
	private ISalonBidService bidService = (ISalonBidService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_BID_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.bid_acString1);
		offer = (OfferView) getIntent().getSerializableExtra("offer");
		
		doGetBarbaerBids();
	}

	private void doGetBarbaerBids() {
		showWaitDialog();
		bidService.getBarberBid(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0 && response.getData() != null){
					boolean bidded = false;
					ArrayList<OfferView> offers = (ArrayList<OfferView>) response.getData();
					for(OfferView view : offers){
						if(view.getId() == offer.getId()){
							bidded = true;
							doGetBidPrice(view.getId());
							break;
						}
					}
					if(! bidded){
						hideAllDialog();
						initView(false, 0);
					}
				}else{
					hideAllDialog();
					initView(false, 0);
				}
				return null;
			}
		});
	}

	protected void doGetBidPrice(int offerId) {
		bidService.getBid(offerId, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				if(response.getState() >= 0 && response.getData() != null){
					int price = 0;
					ArrayList<OfferBiddingView> bidBarbers = (ArrayList<OfferBiddingView>) response.getData();
					for(OfferBiddingView bid : bidBarbers){
						if(bid.getBarberId() == CacheManager.INSTANCE.getCurrentUser().getId()){
							price = (int)bid.getPrice();
							break;
						}
					}
					if(price != 0){
						initView(true, price);								
					}else{
						initView(false, price);								
					}
				}else{
					initView(false, 0);								
				}
				hideAllDialog();
				return null;
			}
			
		});
	}

	private void initView(boolean bidded, int price) {
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		try {
			detail.setText(getString(R.string.bid_acString8, offer.getDesc()));
			district.setText(getString(R.string.bid_acString19, SalonTools.getDisplayArea(this, offer.getArea())));
			imgFetcher.loadFormCache(offer.getPics(), photo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(! bidded){
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(SalonTools.editNotNull(BidActivity.this.price)){
						int p = Integer.valueOf(BidActivity.this.price.getText().toString());
						if(p > 0){
							doBid(p);
						}else{
							Toast.makeText(BidActivity.this, R.string.bid_acString4, Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(BidActivity.this, R.string.bid_acString4, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}else{
			BidActivity.this.price.setText(String.valueOf(price));
			BidActivity.this.price.setEnabled(false);
			done.setBackgroundResource(R.drawable.salon_btn_invalid);
			done.setText(R.string.bid_acString21);
		}
		
	}

	protected void doBid(int price) {
		showWaitDialog();
		bidService.doBid(offer.getId(), price, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				
				if(response.getState() >= 0){
					Toast.makeText(BidActivity.this, R.string.bid_acString5, Toast.LENGTH_SHORT).show();
					hideAllDialog();
					finish();
				}else{
					hideAllDialog();
					Toast.makeText(BidActivity.this, R.string.bid_acString20, Toast.LENGTH_SHORT).show();
				}
				return null;
			}
		});
	}
	
}
