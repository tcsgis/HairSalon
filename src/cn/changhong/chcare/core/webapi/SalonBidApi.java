package cn.changhong.chcare.core.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.changhong.chcare.core.webapi.bean.CouponView;
import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.server.ISalonBidService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public class SalonBidApi extends ISalonBidService{

	@Override
	public ResponseBean getBids(String area)
			throws HttpRequestException {

		String url = BASE_URL + "offer";
		if(area != null){
			url += "?areaCode=" + area;
		}
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<OfferView>> return_resultBean = new ResponseBeanWithRange<List<OfferView>>();
				ArrayList<OfferView> ovs = transToOfferViewList(response);
				
				return_resultBean.setData(ovs);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				
				result = return_resultBean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ResponseBean getCustomBid() throws HttpRequestException {
		
		String url = BASE_URL + "offer/me";
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<OfferView>> return_resultBean = new ResponseBeanWithRange<List<OfferView>>();
				ArrayList<OfferView> ovs = transToOfferViewList(response);
				
				return_resultBean.setData(ovs);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				
				result = return_resultBean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ResponseBean publishBid(String Pics, String Desc, String Area)
			throws HttpRequestException {
		
		String url = BASE_URL + "offer/me";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Pics", Pics);
		params.put("Desc", Desc);
		params.put("Area", Area);
//		params.put("UserId", CacheManager.INSTANCE.getCurrentUser().getId());
		
		String response = this.basePostRequestUtil(url, this.gson.toJson(params));
		ResponseBean<?> result = transToBean(response);
		
		try{
			if (result!=null && result.getState() >= 0) {
				ResponseBean<Integer> return_resultBean = new ResponseBean<Integer>();
				
				JsonParser parser = new JsonParser();
				JsonElement jsonEl = parser.parse(response);
				JsonObject jo = jsonEl.getAsJsonObject().getAsJsonObject("Data");
				
				OrderView ov = transToOrderView(jo);
				int id = ov.getId();
				
				return_resultBean.setData(id);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				result = return_resultBean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ResponseBean confirmBid(int offerID, int barberID) throws HttpRequestException {
		
		String url = BASE_URL + "offer/confirm?id=" + offerID;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BarberId", barberID);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}
	
	@Override
	public ResponseBean cancelBid(int offerID) throws HttpRequestException {

		String url = BASE_URL + "offer/confirm?id=" + offerID;
		return this.postRequestUtil(url, null);
	}

	@Override
	public ResponseBean doBid(int offerID, int price) throws HttpRequestException {
		
		String url = BASE_URL + "offer/biddings?offerid=" + offerID;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Price", price);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean getBid(int offerID) throws HttpRequestException {
		
		String url = BASE_URL + "offer/biddings?offerid=" + offerID;

		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = transToBean(response);
		
		try{
			if (result!=null && result.getState() >= 0) {
				ResponseBean<List<OfferBiddingView>> return_resultBean = new ResponseBean<List<OfferBiddingView>>();
				ArrayList<OfferBiddingView> ovs = transToOfferBiddingViewList(response);
				
				return_resultBean.setData(ovs);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				result = return_resultBean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ResponseBean sendCoupon(int CustomerId, float value)
			throws HttpRequestException {
		
		String url = BASE_URL + "coupons/";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CustomerId", CustomerId);
		params.put("Value", value);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean getCustomCoupon() throws HttpRequestException {
		
		String url = BASE_URL + "coupons/me";
		
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = transToBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<CouponView>> return_resultBean = new ResponseBeanWithRange<List<CouponView>>();
				ArrayList<CouponView> ovs = transToCouponViewList(response);
				
				return_resultBean.setData(ovs);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				
				result = return_resultBean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ResponseBean useCoupon(int couponId) throws HttpRequestException {
		
		String url = BASE_URL + "coupons/confirm?id=" + couponId;
		return this.getRequestUtil(url);
	}

	@Override
	public ResponseBean getBarberBid() throws HttpRequestException {
		
		String url = BASE_URL + "offer/me";
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<OfferView>> return_resultBean = new ResponseBeanWithRange<List<OfferView>>();
				ArrayList<OfferView> ovs = transToOfferViewList(response);
				
				return_resultBean.setData(ovs);
				return_resultBean.setState(result.getState());
				return_resultBean.setDesc(result.getDesc());
				
				result = return_resultBean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	

}