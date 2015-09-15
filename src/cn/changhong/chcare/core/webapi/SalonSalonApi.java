package cn.changhong.chcare.core.webapi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aaa.activity.barber.BarberSalonsActivity;
import com.aaa.util.SalonTools;
import com.changhong.util.CHLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import cn.changhong.chcare.core.webapi.bean.Family;
import cn.changhong.chcare.core.webapi.bean.FamilyMemberInfo;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public class SalonSalonApi extends ISalonSalonService{

	@Override
	public ResponseBean uploadMyBarber(SalonBarberInfoView barber)
			throws HttpRequestException {
		
//		salon/salonbarber?salonBarberId={salonBarberId}
		String url = BASE_URL;
		if(barber.getId() == 0){
			url += "salon/salonbarber";
		}else{
			url += "salon/salonbarber?salonBarberId=" + barber.getId();
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Id", barber.getId());
		params.put("Nick", barber.getNick());
		params.put("Photo", barber.getPhoto());
		params.put("SalonInfoId", barber.getSalonInfoId());
		params.put("Prices", SalonTools.composePrice(barber.getPrices()));
		params.put("Health", barber.getHealth());
		params.put("WorkYears", barber.getWorkYears());
		params.put("Certs", SalonTools.composePhotos(barber.getCerts()));
		params.put("Works", SalonTools.composePhotos(barber.getWorks()));
		
		String response = this.basePostRequestUtil(url, this.gson.toJson(params));
		ResponseBean<?> result = this.transToBean(response);
		
		if (result != null && result.getState() >= 0) {
			ResponseBean<Integer> return_resultBean = new ResponseBean<Integer>();
			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(response);
			JsonObject jsonObj = jsonEl.getAsJsonObject().getAsJsonObject("Data");
			
			Integer id = jsonObj.get("Id") == null ? 0 :jsonObj.get("Id").getAsInt();
			return_resultBean.setData(id);
			return_resultBean.setState(result.getState());
			
			result = return_resultBean;
		}
		return result;
	}

	@Override
	public ResponseBean deleteMyBarber(int barberID)
			throws HttpRequestException {
		
		String url = BASE_URL + "salon/salonbarber?salonBarberId=" + barberID;
		return this.deleteRequestUtil(url);
	}

	@Override
	public ResponseBean getMyBarber(int salonId) throws HttpRequestException {
		
		String url = BASE_URL + "salon/salonbarber?id=" + salonId;
			
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = this.transToBean(response);
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBean<List<SalonBarberInfoView>> return_resultBean = new ResponseBean<List<SalonBarberInfoView>>();
				ArrayList<SalonBarberInfoView> barbers = new ArrayList<SalonBarberInfoView>();
				
				JsonParser parser = new JsonParser();
				JsonElement jsonEl = parser.parse(response);
				JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
				
				for(JsonElement je : array){
					JsonObject jo = je.getAsJsonObject();
					SalonBarberInfoView barber = new SalonBarberInfoView();
					barber.setId(jo.get("Id") == null ? 0 : jo.get("Id").getAsInt());
					barber.setNick(jo.get("Nick") == null ? null : jo.get("Nick").getAsString());
					barber.setPhoto(jo.get("Photo") == null ? null : jo.get("Photo").getAsString());
					barber.setSalonInfoId(jo.get("SalonInfoId") == null ? 0 : jo.get("SalonInfoId").getAsInt());
					barber.setPrices(SalonTools.splitPrice(jo.get("Prices")));
					barber.setAdept(barber.getPrices()[9]);//llw 2015.09.14
					barber.setHealth(jo.get("Health") == null ? null : jo.get("Health").getAsString());
					barber.setWorkYears(jo.get("WorkYears") == null ? null : jo.get("WorkYears").getAsByte());
					barber.setCerts(SalonTools.splitPhoto(jo.get("Certs")));
					barber.setWorks(SalonTools.splitPhoto(jo.get("Works")));
					barbers.add(barber);
				}
				
				return_resultBean.setData(barbers);
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
	public ResponseBean getFreeBarber(int salonId) throws HttpRequestException {
		
		String url = BASE_URL + "salon/freebarber?id=" + salonId;
		
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = transToBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBean<List<SalonUser>> return_resultBean = new ResponseBean<List<SalonUser>>();
				ArrayList<SalonUser> users = transToSalonUserList(response);
				
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
	public ResponseBean deleteFreeBarber(int barberID)
			throws HttpRequestException {
		
		String url = BASE_URL + "salon/freebarber?freeBarberId=" + barberID;
		return this.deleteRequestUtil(url);
	}

	@Override
	public ResponseBean getMyBarberCount(int salonId) throws HttpRequestException {

		String url = BASE_URL + "salon/salonbarber?id=" + salonId;
		
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = this.transToBean(response);
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBean<Integer> return_resultBean = new ResponseBean<Integer>();
				
				JsonParser parser = new JsonParser();
				JsonElement jsonEl = parser.parse(response);
				JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
				int count = array.size();
				
				return_resultBean.setData(count);
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
