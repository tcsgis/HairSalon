package cn.changhong.chcare.core.webapi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aaa.util.OrderStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.server.ISalonOrderService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public class SalonOrderApi extends ISalonOrderService{

	@Override
	public ResponseBean createOrder(OrderView order)
			throws HttpRequestException {
		
		String url = BASE_URL + "orders/me";
		Map<String, Object> params = new HashMap<String, Object>();
		if(order.getDesc() != null && order.getDesc().length() > 0){
			params.put("Desc", order.getDesc());
		}
		params.put("OrderDate", order.getOrderDate());
		params.put("OrderTime", order.getOrderTime());
		params.put("SalonId", order.getSalonId());
		
		if(order.getFreeBarberId() != 0){
			params.put("FreeBarberId", order.getFreeBarberId());
		}
		
		if(order.getSalonBarberId() != 0){
			params.put("SalonBarberId", order.getSalonBarberId());
		}
		
		if(order.getCouponId() != 0){
			params.put("CouponId", order.getCouponId());
		}
		
		String response = this.basePostRequestUtil(url, this.gson.toJson(params));
		ResponseBean<?> result = transToBean(response);
		
		try{
			if (result!=null && result.getState() >= 0) {
				ResponseBean<Integer> return_resultBean = new ResponseBean<Integer>();
				
				JsonParser parser = new JsonParser();
				JsonElement jsonEl = parser.parse(response);
				JsonObject jo = jsonEl.getAsJsonObject().getAsJsonObject("Data");
				
				OfferView ov = transToOfferView(jo);
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
	public ResponseBean getMyOrders() throws HttpRequestException {
		
		String url = BASE_URL + "orders/me";
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<OrderView>> return_resultBean = new ResponseBeanWithRange<List<OrderView>>();
				ArrayList<OrderView> users = transToOrderViewList(response);
				
				return_resultBean.setData(users);
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
	public ResponseBean cancelOrder(int orderId) throws HttpRequestException {
		
		String url = BASE_URL + "orders/me?id=" + orderId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrderStatus", OrderStatus.Cancled);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean acceptOrder(int orderId) throws HttpRequestException {
		
		String url = BASE_URL + "orders/accept?id=" + orderId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrderStatus", OrderStatus.Doing);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean rejectOrder(int orderId) throws HttpRequestException {
		
		String url = BASE_URL + "orders/accept?id=" + orderId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrderStatus", OrderStatus.Reject);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean finishOrder(int orderId, float score)
			throws HttpRequestException {
		
		String url = BASE_URL + "orders/me?id=" + orderId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrderStatus", OrderStatus.Done);
		params.put("Score", score);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean shareOrder(int orderId) throws HttpRequestException {
		
		String url = BASE_URL + "orders/me?id=" + orderId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrderStatus", OrderStatus.Shared);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

}
