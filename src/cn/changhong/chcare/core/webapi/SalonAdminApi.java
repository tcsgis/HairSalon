package cn.changhong.chcare.core.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.server.ISalonAdminService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public class SalonAdminApi extends ISalonAdminService{

	@Override
	public ResponseBeanWithRange<?> getUnchecked(byte role, String area)
			throws HttpRequestException {
		
		String url = BASE_URL + "admin/unchecked?";
		if(area != null)
			url += "areaCode=" + area + "&";
		url += "role=" + role;
		
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

	@Override
	public ResponseBean check(int userId, boolean pass, byte level)
			throws HttpRequestException {
		
		String url = BASE_URL + "admin/unchecked?id=" + userId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Allowed", pass);
		params.put("Score", level);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean updateAdPics(BannerPic bp)
			throws HttpRequestException {
		
		String url = BASE_URL + "cfg";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("AdPics", bp);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean updateMallPics(BannerPic bp)
			throws HttpRequestException {
		
		String url = BASE_URL + "cfg";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("StorePics", bp);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

}
