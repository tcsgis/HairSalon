package cn.changhong.chcare.core.webapi;

import java.util.ArrayList;
import java.util.List;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.server.ISalonBarberService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public class SalonBarberApi extends ISalonBarberService{

	@Override
	public ResponseBean bindSalon(int salonId) throws HttpRequestException {
		
		String url = BASE_URL + "barber/salon?salonId=" + salonId;
		return this.postRequestUtil(url, null);
	}

	@Override
	public ResponseBean unbindSalon(int salonId) throws HttpRequestException {
		
		String url = BASE_URL + "barber/salon?salonId=" + salonId;
		return this.deleteRequestUtil(url);
	}

	@Override
	public ResponseBean getBindSalons(int barberId) throws HttpRequestException {
		
		String url = BASE_URL + "barber/salon?id=" + barberId;
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<SalonUser>> return_resultBean = new ResponseBeanWithRange<List<SalonUser>>();
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

}
