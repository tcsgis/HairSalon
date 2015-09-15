package cn.changhong.chcare.core.webapi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.aaa.util.Status;
import com.changhong.activity.util.Tools;
import com.changhong.util.CHLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.bean.VerInfo;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;
import cn.changhong.chcare.core.webapi.util.TokenManager;

public class SalonAccountApi extends ISalonAccountService{

	@Override
	public ResponseBean<?> getVetifyCode(String phoneNumber, int type, byte role)
			throws HttpRequestException {
		
		String url = BASE_URL + "Account/Vfy?username=" + phoneNumber + "&type=" + type + "&role=" + role;
		return this.getRequestUtil(url);
	}

	@Override
	public ResponseBean<?> verifyCode(String username, String code, int type)
			throws HttpRequestException {
		
		String url = BASE_URL + "Account/Vfy";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("UserName", username);
		params.put("VerifyCode", code);
		params.put("Type", type);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean<?> register(String username, String verifyCode,
			String newPassword, byte role) throws HttpRequestException {
		
		String url = BASE_URL + "Account/Regist";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Name", username);
		String password_encryed = Tools.des_Encypt(username, newPassword);
		params.put("NewPwd", password_encryed);
		params.put("Role", role);
		params.put("PwdMode", 1);
		params.put("VerifyCode", verifyCode);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean<?> resetSecret(String username, String newPassword,
			String verifyCode) throws HttpRequestException {
		
		String url = BASE_URL + "Account/Pwd/Rst";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Name", username);
		String password_encryed = Tools.des_Encypt(username, newPassword);
		params.put("NewPwd", password_encryed);
		params.put("Role", Role.UNDIFINED);
		params.put("PwdMode", 1);
		params.put("VerifyCode", verifyCode);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean verifyOldSecret(String username, String oldPassword)
			throws HttpRequestException {
		
		String url = BASE_URL + "Account/Pwd/Vfy";
		Map<String, Object> params = new HashMap<String, Object>();
		String password_encryed = Tools.des_Encypt(username, oldPassword);
		params.put("CurPwd", password_encryed);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean changeSecret(String username, String oldPassword)
			throws HttpRequestException {
		
		String url = BASE_URL + "Account/Pwd/Mod";
		Map<String, Object> params = new HashMap<String, Object>();
		String password_encryed = Tools.des_Encypt(username, oldPassword);
		params.put("CurPwd", password_encryed);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}
	
	@Override
	public ResponseBean<?> login(String username, String password)
			throws HttpRequestException {
		
		String url = BASE_URL + "Token?v=2&misc=UserInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Name", username);
		String password_encryed = Tools.des_Encypt(username, password);
		params.put("CurPwd", password_encryed);
		params.put("ClientId", "1");
		params.put("PwdMode", 1);
		params.put("Role", Role.UNDIFINED);
		
		String response = this.basePostRequestUtil(url, this.gson.toJson(params));
		ResponseBean<?> result = this.transToBean(response);
		
		if (result != null && result.getState() >= 0) {

			ResponseBean<SalonUser> return_resultBean = new ResponseBean<SalonUser>();
			
			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(response);

			JsonObject jsonObj = jsonEl.getAsJsonObject().getAsJsonObject("Data");// 转换成Json对象

			TokenManager.setToken(jsonObj.getAsJsonPrimitive("Token").getAsString());
			TokenManager.setFiletoken(jsonObj.getAsJsonPrimitive("FileToken").getAsString());
			jsonObj = jsonObj.getAsJsonObject("Extra");// 转换成Json对象
			JsonObject userJs = jsonObj.getAsJsonObject("UserInfo");
			
			SalonUser user = new SalonUser();
			user = transToSalonUser(userJs);
			
			return_resultBean.setData(user);
			return_resultBean.setState(result.getState());
			return_resultBean.setDesc(result.getDesc());
			
			result = return_resultBean;
		}
		return result;
	}

	@Override
	public ResponseBean<?> logout() throws HttpRequestException {
		String url = BASE_URL + "Token";
		return this.deleteRequestUtil(url);
	}

	@Override
	public ResponseBean getUser(int userId) throws HttpRequestException {
		String url = BASE_URL + "User?id=" + userId;
		
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = transToBean(response);
		
		if (result != null && result.getState() >= 0) {
			ResponseBean<SalonUser> return_resultBean = new ResponseBean<SalonUser>();
			
			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(response);

			JsonObject jsonObj = jsonEl.getAsJsonObject().getAsJsonObject("Data");// 转换成Json对象
			SalonUser user = transToSalonUser(jsonObj);
			
			return_resultBean.setData(user);
			return_resultBean.setState(result.getState());
			return_resultBean.setDesc(result.getDesc());
			
			result = return_resultBean;
		}
		
		return result;
	}

	@Override
	public ResponseBean updateSelfMg(SalonUser user)
			throws HttpRequestException {
			
		String url = BASE_URL + "User";
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(user.getRole() == Role.SALON){
			params.put("Nick", user.getNick());
			params.put("Areas", user.getAreas());
			params.put("Addr", user.getAddr());
			params.put("Size", user.getSize());
			params.put("HairCount", user.getHairCount());
			params.put("WashCount", user.getWashCount());
			params.put("Version", user.getVersion());
			params.put("Person_Name", user.getPerson_Name());
			params.put("Tel", user.getTel());
			params.put("AddinServices", user.getAddinServices());
			params.put("Photos", SalonTools.composePhotos(user.getPhotos()));
			params.put("AllowJoin", user.getAllowJoin());
			params.put("Ratio", user.getRatio());
			params.put("MinLevel", user.getMinLevel());
			params.put("Products", SalonTools.composeProduct(user.getProducts()));
			params.put("Desc", user.getDesc());
		}
		else if(user.getRole() == Role.BARBER){
			params.put("Person_Name", user.getPerson_Name());
			params.put("Person_Id", user.getPerson_Id());
			params.put("WorkYears", user.getWorkYears());
			params.put("Areas", user.getAreas());
			params.put("Version", user.getVersion());
			params.put("Nick", user.getNick());
			params.put("Tel", user.getTel());
			params.put("Ratio", user.getRatio());
			params.put("Photo", user.getPhoto());
			params.put("Certs", SalonTools.composePhotos(user.getCerts()));
			params.put("Works", SalonTools.composePhotos(user.getWorks()));
			params.put("Products", SalonTools.composeProduct(user.getProducts()));
			params.put("Prices", SalonTools.composePrice(user.getPrices()));
			params.put("Health", user.getHealth());
			params.put("Desc", user.getDesc());
		}
		else if(user.getRole() == Role.CUSTOM){
			params.put("Nick", user.getNick());
			params.put("Photo", user.getPhoto());
		}
		
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean searchUser(byte role, String key, String area)
			throws HttpRequestException {
		
		String url = BASE_URL + "user/search?";
		if(area != null){
			try {
				url += "areacode=" +  URLEncoder.encode(area, "utf-8") ;
				if(key != null){
					url += "&keyword=" + key + "&role=" + role;
				}else{
					url += "&role=" + role;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			if(key != null){
				url += "keyword=" + key + "&role=" + role;
			}else{
				url += "role=" + role;
			}
		}
		
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
	public ResponseBean getBannerUsers(String area) throws HttpRequestException {
		String url;
		if(area != null){
			url = BASE_URL + "user/banner?areaCode=" + area;
		}else{
			url = BASE_URL + "user/banner";
		}
		
		String response = this.baseGetRequestUtil(url);
		ResponseBeanWithRange<?> result = transToRangeBean(response);
		
		try {
			if (result!=null && result.getState() >= 0) {
				ResponseBeanWithRange<List<SalonUser>> return_resultBean = new ResponseBeanWithRange<List<SalonUser>>();
				
				ArrayList<SalonUser> users = new ArrayList<SalonUser>();
				JsonParser parser = new JsonParser();
				JsonElement jsonEl = parser.parse(response);
				JsonObject data = jsonEl.getAsJsonObject().getAsJsonObject("Data");
				
				JsonArray jo = data.getAsJsonArray("Barbers");
				for(JsonElement je : jo){
					SalonUser user = new SalonUser();
					user = transToSalonUser(je.getAsJsonObject());
					if(user != null){
						users.add(user);
					}
				}
				
				jo = data.getAsJsonArray("Salons");
				for(JsonElement je : jo){
					SalonUser user = new SalonUser();
					user = transToSalonUser(je.getAsJsonObject());
					if(user != null){
						users.add(user);
					}
				}
				
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
	public ResponseBean getBanners() throws HttpRequestException {
		
		String url = BASE_URL + "cfg";
		
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = transToRangeBean(response);
		
		try {
			ResponseBean<List<BannerPic>> return_resultBean = new ResponseBean<List<BannerPic>>();
			
			ArrayList<BannerPic> banners = new ArrayList<BannerPic>();
			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(response);
			JsonObject data = jsonEl.getAsJsonObject().getAsJsonObject("Data");
			
			BannerPic ad = new BannerPic();
			if(data.get("AdPics") != null){
				JsonObject adJo = data.get("AdPics").getAsJsonObject();
				if(adJo != null){
					ad.setPhotos(adJo.get("Photo") == null ? null : adJo.get("Photo").getAsString());
					ad.setUrls(adJo.get("Url") == null ? null : adJo.get("Url").getAsString());
				}
			}
			
			BannerPic mall = new BannerPic();
			if(data.get("StorePics") != null){
				JsonObject mallJo = data.get("StorePics").getAsJsonObject();
				if(mallJo != null){
					mall.setPhotos(mallJo.get("Photo") == null ? null : mallJo.get("Photo").getAsString());
					mall.setUrls(mallJo.get("Url") == null ? null : mallJo.get("Url").getAsString());
				}
			}
			
			VerInfo ver = new VerInfo();
			if(data.get("VerInfo") != null){
				JsonObject mallJo = data.get("VerInfo").getAsJsonObject();
				if(mallJo != null){
					ver.setVerCode(mallJo.get("VerCode") == null ? 0 : mallJo.get("VerCode").getAsInt());
					ver.setFile(mallJo.get("File") == null ? null : mallJo.get("File").getAsString());
					ver.setDesc(mallJo.get("Desc") == null ? null : mallJo.get("Desc").getAsString());
					ver.setVerName(mallJo.get("VerName") == null ? null : mallJo.get("VerName").getAsString());
				}
			}
			
			banners.add(ad);
			banners.add(mall);
			banners.add(ver);
			
			return_resultBean.setData(banners);
			return_resultBean.setState(result.getState());
			return_resultBean.setDesc(result.getDesc());
			
			result = return_resultBean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
