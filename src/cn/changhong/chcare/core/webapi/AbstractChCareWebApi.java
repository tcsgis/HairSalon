package cn.changhong.chcare.core.webapi;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.changhong.chcare.core.webapi.bean.CHCareFileInStream;
import cn.changhong.chcare.core.webapi.bean.CouponView;
import cn.changhong.chcare.core.webapi.bean.OfferBiddingView;
import cn.changhong.chcare.core.webapi.bean.OfferView;
import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.server.ChCareWebApiRequestErrorType;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;
import cn.changhong.chcare.core.webapi.util.HttpsConnectionManager;
import cn.changhong.chcare.core.webapi.util.IHttpRestApi;
import cn.changhong.chcare.core.webapi.util.MultipartUtility;
import cn.changhong.chcare.core.webapi.util.TokenManager;
import cn.changhong.chcare.core.webapi.util.WebApiExecutorProvider;

import com.aaa.util.Role;
import com.aaa.util.SalonTools;
import com.changhong.BuildConfig;
import com.changhong.util.CHLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class AbstractChCareWebApi {
	protected final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	protected final static String charset = "utf-8";
	public static String BASE_URL = "http://182.92.165.152:9081/api/";
	public void setServerUrl(URL url){
		BASE_URL=url.toString();
	}
	public URL getServerUrl(){
		URL result=null;
		try {
			result= new URL(BASE_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return result;
	}
	protected IHttpRestApi httpRequestHandler = HttpsConnectionManager.Self
			.defaultInstance();
	protected WebApiExecutorProvider executorProvider = WebApiExecutorProvider.Self
			.defaultInstance();

	protected ResponseBean<?> transToBean(String jsonStr) throws HttpRequestException {
		return transToBean(jsonStr, ResponseBean.class);
	}

	protected ResponseBeanWithRange<?> transToRangeBean(String jsonStr) throws HttpRequestException {
		return transToRangeBean(jsonStr, ResponseBeanWithRange.class);
	}

	protected ResponseBean<?> transToBean(String jsonStr, Type type)
			throws HttpRequestException {
		ResponseBean<?> result = (ResponseBean<?>)transToRaw(jsonStr, type);
		return result;
	}

	protected ResponseBeanWithRange<?> transToRangeBean(String jsonStr, Type type)
			throws HttpRequestException {
		ResponseBeanWithRange<?> result = (ResponseBeanWithRange<?>)transToRaw(jsonStr, type);
		return result;
	}

	protected Object transToRaw(String jsonStr, Type type)
			throws HttpRequestException {
		if (jsonStr != null) {
			try {
				return gson.fromJson(jsonStr, type);
			} catch (Exception e) {
				String msg = "Transform JsonString Value[" + safeWrapResponse(jsonStr)
						+ "] To Type [" + type.toString() + "] Error!";
				throw new HttpRequestException(
						msg,
						ChCareWebApiRequestErrorType.CHCAREWEBAPI_TRANSFORM_DATA_ERROR);
			}/* finally {
				try {
					jsonStr.close();
				} catch (IOException ignored) {

				}
			}*/
		}else{
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}

	}

	protected ResponseBean<?> postRequestUtil(String url,
			String requestBodyparams) throws HttpRequestException {
		return transToBean(basePostRequestUtil(url, requestBodyparams));
	}

	protected String basePostRequestUtil(String url, String requestBodyparams)
			throws HttpRequestException {
		String response = this.httpRequestHandler.post(url, requestBodyparams);
		if (response == null) {
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}
		safeLogResponse(response);
		return response;
	}

	protected ResponseBean<?> putRequestUtil(String url,
			String requestBodyparams) throws HttpRequestException {
		return transToBean(basePutRequestUtil(url, requestBodyparams));
	}
	protected String basePutRequestUtil(String url, String requestBodyparams)
			throws HttpRequestException {
		String response = this.httpRequestHandler.put(url, requestBodyparams);
		if (response == null) {
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}
		safeLogResponse(response);
		return response;
	}
	protected ResponseBean<?> getRequestUtil(String url)
			throws HttpRequestException {
		return transToBean(baseGetRequestUtil(url));
	}

	protected String baseGetRequestUtil(String url) throws HttpRequestException {
		String response = this.httpRequestHandler.get(url);
		if (response == null) {
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}
		safeLogResponse(response);
		return response;
	}

	/**
	 * 关于delete是否可以存在正文, http标准及各服务器的实现并不明确, 建议不传
	 * @param url
	 * @param requestBodyparams
	 * @return
	 * @throws HttpRequestException
	 */
	@Deprecated
	protected ResponseBean<?> deleteRequestUtil(String url,
			String requestBodyparams) throws HttpRequestException {
		return transToBean(baseDeleteRequestUtil(url));
	}

	protected ResponseBean<?> deleteRequestUtil(String url) throws HttpRequestException {
		return transToBean(baseDeleteRequestUtil(url));
	}

	/**
	 * 关于delete是否可以存在正文, http标准及各服务器的实现并不明确, 建议不传
	 * @param url
	 * @param requestBodyparams
	 * @return
	 * @throws HttpRequestException
	 */
	@Deprecated
	protected String baseDeleteRequestUtil(String url, String requestBodyparams)
			throws HttpRequestException {
		return baseDeleteRequestUtil(url);
	}

	protected String baseDeleteRequestUtil(String url)
			throws HttpRequestException {
		String response = this.httpRequestHandler
				.delete(url, null);
		if (response == null) {
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}
		safeLogResponse(response);
		return response;
	}

	@Deprecated
	protected String baseUsedFormUploadPhoto(String url, InputStream instream,
			String params) throws HttpRequestException {
		if (instream == null) {
			throw new HttpRequestException(
					"Read InputStream Error,InputStream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		System.out.println(url + "," + params);
		String response = null;
		try {
			response = this.httpRequestHandler.postFile(url, instream, params);
		} finally {
			this.httpRequestHandler.closeStream(instream);
		}
		if (response == null) {
			throw new HttpRequestException("Response Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		safeLogResponse(response);
		return response;
	}

	public CHCareFileInStream downloadFile(String url)
			throws HttpRequestException {
		return this.httpRequestHandler.getPhotoFile(url);
	}
	public boolean downloadFile(String url,OutputStream out) throws HttpRequestException{
		String downurl = BASE_URL;
		try
		{
			downurl = new StringBuilder(BASE_URL).append("File/?id=").append(url).
					append("&key=").append(TokenManager.getFiletoken()).toString();
//			if(url.contains("?"))
//				url = (new StringBuilder(String.valueOf(url))).append("&key=").append(TokenManager.getFiletoken()).toString();
		}
		catch(Exception ignored) { 

		}
		CHLogger.d(this, "downurl " + downurl );
		return this.httpRequestHandler.getPhotoFile(downurl, out);
	}
	protected String doPostSingleFileUsedFormType(String url,
			InputStream instream, Map<String, String> formBodys, String filename)
			throws HttpRequestException {
		System.out.println(url+this.gson.toJson(formBodys));
		String response = null;
		MultipartUtility multipart = new MultipartUtility(url, charset);
		for (Map.Entry<String, String> body : formBodys.entrySet()) {
			multipart.addFormField(body.getKey(), body.getValue());
		}
		multipart.addFilePart(filename, instream);
		List<String> responses = multipart.finish();
		if (responses != null && responses.size() > 0) {
			response = responses.get(0);
		} else {
			throw new HttpRequestException("Http Response Stream Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_RESPONSE_ERROR);
		}
		safeLogResponse(response);
		return response;
	}

	private void safeLogResponse(String response){
		if (BuildConfig.DEBUG)
			System.out.println(safeWrapResponse(response));
	}

	private String safeWrapResponse(String response){
		if (response == null)
			return "{EMPTY RESPONSE}";
		else if (response.length() > 65536)
			return response.substring(0, 1000) + "...(too long data)";
		else
			return response;
	}
	
	protected SalonUser transToSalonUser(JsonObject userJs){
		try {
			SalonUser user = new SalonUser();
			user.setId(userJs.get("Id") == null ? 0 :userJs.get("Id").getAsInt());
			user.setName(userJs.get("Name") == null ? null : userJs.get("Name").getAsString());
			user.setStatus(userJs.get("Status") == null ? 0 : userJs.get("Status").getAsByte());
			user.setRole(userJs.get("Role") == null ? Role.UNDIFINED : userJs.get("Role").getAsByte());
			user.setVersion(userJs.get("Version") == null ? 0 : userJs.get("Version").getAsByte());
			user.setNick(userJs.get("Nick") == null ? null : userJs.get("Nick").getAsString());
			user.setPhoto(userJs.get("Photo") == null ? null : userJs.get("Photo").getAsString());
			
			if(user.getRole() == Role.BARBER || user.getRole() == Role.SALON){
				user.setAreas(userJs.get("Areas") == null ? null : userJs.get("Areas").getAsString());
				user.setPerson_Name(userJs.get("Person_Name") == null ? null : userJs.get("Person_Name").getAsString());
				user.setLevel(userJs.get("Level") == null ? 0 : userJs.get("Level").getAsByte());
				user.setRatio(userJs.get("Ratio") == null ? 0 : userJs.get("Ratio").getAsByte());
				user.setProducts(SalonTools.splitProduct(userJs.get("Products")));
				user.setTel(userJs.get("Tel") == null ? null : userJs.get("Tel").getAsString());
				user.setAvgScore(userJs.get("AvgScore") == null ? 0 : userJs.get("AvgScore").getAsFloat());
				user.setDesc(userJs.get("Desc") == null ? null : userJs.get("Desc").getAsString());
			}
			if(user.getRole() == Role.SALON){
				user.setAddr(userJs.get("Addr") == null ? null : userJs.get("Addr").getAsString());
				user.setAllowJoin(userJs.get("AllowJoin") == null ? false : userJs.get("AllowJoin").getAsBoolean());
				user.setMinLevel(userJs.get("MinLevel") == null ? 0 : userJs.get("MinLevel").getAsByte());
				user.setSize(userJs.get("Size") == null ? 0 :userJs.get("Size").getAsInt());
				user.setHairCount(userJs.get("HairCount") == null ? 0 :userJs.get("HairCount").getAsInt());
				user.setWashCount(userJs.get("WashCount") == null ? 0 :userJs.get("WashCount").getAsInt());
				user.setAddinServices(userJs.get("AddinServices") == null ? 0 :userJs.get("AddinServices").getAsInt());
				user.setPhotos(SalonTools.splitPhoto(userJs.get("Photos")));
				user.setSalonBarberCount(userJs.get("SalonBarberCount") == null ? 0 : userJs.get("SalonBarberCount").getAsInt());
			}
			if(user.getRole() == Role.BARBER){
				user.setSalonInfoId(userJs.get("SalonInfoId") == null ? 0 :userJs.get("SalonInfoId").getAsInt());
				user.setPerson_Id(userJs.get("Person_Id") == null ? null : userJs.get("Person_Id").getAsString());
				user.setPrices(SalonTools.splitPrice(userJs.get("Prices")));
				user.setAdept(user.getPrices()[9]);//llw 2015.09.14 非正式添加“擅长”
				user.setHealth(userJs.get("Health") == null ? null : userJs.get("Health").getAsString());
				user.setWorkYears(userJs.get("WorkYears") == null ? 0 : userJs.get("WorkYears").getAsByte());
				user.setSalonName(userJs.get("SalonName") == null ? null : userJs.get("SalonName").getAsString());
				user.setCerts(SalonTools.splitPhoto(userJs.get("Certs")));
				user.setWorks(SalonTools.splitPhoto(userJs.get("Works")));
			}
			
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected ArrayList<SalonUser> transToSalonUserList(String response){
		ArrayList<SalonUser> users = new ArrayList<SalonUser>();
		JsonParser parser = new JsonParser();
		JsonElement jsonEl = parser.parse(response);
		JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
		
		for(JsonElement je : array){
			SalonUser user = new SalonUser();
			user = transToSalonUser(je.getAsJsonObject());
			if(user != null){
				users.add(user);
			}
		}
		return users;
	}
	
	protected OfferView transToOfferView(JsonObject jo){
		try {
			OfferView ov = new OfferView();
			ov.setId(jo.get("Id") == null ? 0 :jo.get("Id").getAsInt());
			ov.setPics(jo.get("Pics") == null ? null :jo.get("Pics").getAsString());
			ov.setUserId(jo.get("UserId") == null ? 0 :jo.get("UserId").getAsInt());
			ov.setDesc(jo.get("Desc") == null ? null :jo.get("Desc").getAsString());
			ov.setArea(jo.get("Area") == null ? null :jo.get("Area").getAsString());
			ov.setOfferStatus(jo.get("OfferStatus") == null ? 0 :jo.get("OfferStatus").getAsByte());
			ov.setBiddingCount(jo.get("BiddingCount") == null ? 0 :jo.get("BiddingCount").getAsByte());
			
			ov.setBarberId(jo.get("BarberId") == null ? 0 :jo.get("BarberId").getAsInt());
			
			if(jo.get("User") != null){
				JsonObject userJo = jo.get("User").getAsJsonObject();
				ov.setuserNick(userJo.get("Nick") == null ? null :userJo.get("Nick").getAsString());
			}
			return ov;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected ArrayList<OfferView> transToOfferViewList(String response) {
		ArrayList<OfferView> ovs = new ArrayList<OfferView>();
		JsonParser parser = new JsonParser();
		JsonElement jsonEl = parser.parse(response);
		JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
		
		for(JsonElement je : array){
			OfferView ov = new OfferView();
			ov = transToOfferView(je.getAsJsonObject());
			if(ov != null){
				ovs.add(ov);
			}
		}
		return ovs;
	}
	
	protected OfferBiddingView transToOfferBiddingView(JsonObject jo){
		try {
			OfferBiddingView ov = new OfferBiddingView();
			ov.setId(jo.get("Id") == null ? 0 :jo.get("Id").getAsInt());
			ov.setOfferId(jo.get("OfferId") == null ? 0 :jo.get("OfferId").getAsInt());
			ov.setBarberId(jo.get("BarberId") == null ? null :jo.get("BarberId").getAsInt());
			ov.setPrice(jo.get("Price") == null ? null :jo.get("Price").getAsFloat());
			
			if(jo.get("Barber") != null){
				JsonObject barberJo = jo.get("Barber").getAsJsonObject();
				ov.setName(barberJo.get("Nick") == null ? null :barberJo.get("Nick").getAsString());
				ov.setPhoto(barberJo.get("Photo") == null ? null :barberJo.get("Photo").getAsString());
			}
			return ov;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected ArrayList<OfferBiddingView> transToOfferBiddingViewList(String response) {
		ArrayList<OfferBiddingView> ovs = new ArrayList<OfferBiddingView>();
		JsonParser parser = new JsonParser();
		JsonElement jsonEl = parser.parse(response);
		JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
		
		for(JsonElement je : array){
			OfferBiddingView ov = new OfferBiddingView();
			ov = transToOfferBiddingView(je.getAsJsonObject());
			if(ov != null){
				ovs.add(ov);
			}
		}
		return ovs;
	}
	
	protected CouponView transToCouponView(JsonObject jo){
		try {
			CouponView ov = new CouponView();
			ov.setId(jo.get("Id") == null ? 0 :jo.get("Id").getAsInt());
			ov.setCustomerId(jo.get("CustomerId") == null ? 0 :jo.get("CustomerId").getAsInt());
			ov.setSalesId(jo.get("SalesId") == null ? 0 :jo.get("SalesId").getAsInt());
			ov.setValue(jo.get("Value") == null ? 0 :jo.get("Value").getAsFloat());
			ov.setUsed(jo.get("Used") == null ? true :jo.get("Used").getAsBoolean());
			ov.setSalesRole(jo.get("Role") == null ? 0 :jo.get("Role").getAsByte());
			
			if(jo.get("Sales") != null){
				JsonObject couponJo = jo.get("Sales").getAsJsonObject();
				ov.setSalesName(couponJo.get("Nick") == null ? null :couponJo.get("Nick").getAsString());
			}
			return ov;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected ArrayList<CouponView> transToCouponViewList(String response) {
		ArrayList<CouponView> ovs = new ArrayList<CouponView>();
		JsonParser parser = new JsonParser();
		JsonElement jsonEl = parser.parse(response);
		JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
		
		for(JsonElement je : array){
			CouponView ov = new CouponView();
			ov = transToCouponView(je.getAsJsonObject());
			if(ov != null){
				ovs.add(ov);
			}
		}
		return ovs;
	}
	
	protected OrderView transToOrderView(JsonObject jo){
		try {
			OrderView ov = new OrderView();
			ov.setId(jo.get("Id") == null ? 0 :jo.get("Id").getAsInt());
			ov.setOrderDate(jo.get("OrderDate") == null ? null :jo.get("OrderDate").getAsString());
			ov.setOrderTime(jo.get("OrderTime") == null ? null :jo.get("OrderTime").getAsString());
			ov.setDesc(jo.get("Desc") == null ? null :jo.get("Desc").getAsString());
			ov.setOrderStatus(jo.get("OrderStatus") == null ? 0 :jo.get("OrderStatus").getAsByte());
			ov.setUserId(jo.get("UserId") == null ? 0 :jo.get("UserId").getAsInt());
			ov.setSalonId(jo.get("SalonId") == null ? 0 :jo.get("SalonId").getAsInt());
			ov.setSalonBarberId(jo.get("SalonBarberId") == null ? 0 :jo.get("SalonBarberId").getAsInt());
			ov.setFreeBarberId(jo.get("FreeBarberId") == null ? 0 :jo.get("FreeBarberId").getAsInt());
			ov.setCouponId(jo.get("CouponId") == null ? 0 :jo.get("CouponId").getAsInt());
			ov.setScore(jo.get("Score") == null ? 0 :jo.get("Score").getAsFloat());
			ov.setRatio(jo.get("Ratio") == null ? 0 :jo.get("Ratio").getAsByte());
			
			if(jo.get("Coupon") != null){
				JsonObject couponJo = jo.get("Coupon").getAsJsonObject();
				ov.setValue(couponJo.get("Value") == null ? 0 :couponJo.get("Value").getAsFloat());
			}
			
			if(jo.get("User") != null){
				JsonObject userJo = jo.get("User").getAsJsonObject();
				ov.setCustomName(userJo.get("Nick") == null ? null :userJo.get("Nick").getAsString());
				ov.setCustomTel(userJo.get("Tel") == null ? null :userJo.get("Tel").getAsString());
			}
			
			if(jo.get("Salon") != null){
				JsonObject salonJo = jo.get("Salon").getAsJsonObject();
				ov.setSalonName(salonJo.get("Nick") == null ? null :salonJo.get("Nick").getAsString());
				ov.setSalonTel(salonJo.get("Tel") == null ? null :salonJo.get("Tel").getAsString());
			}
			
			if(jo.get("SalonBarber") != null){
				JsonObject salonBarberJo = jo.get("SalonBarber").getAsJsonObject();
				ov.setBarberName(salonBarberJo.get("Nick") == null ? null :salonBarberJo.get("Nick").getAsString());
				ov.setBarberTel(salonBarberJo.get("Tel") == null ? null :salonBarberJo.get("Tel").getAsString());
			}
			else if(jo.get("FreeBarber") != null){
				JsonObject freeBarberJo = jo.get("FreeBarber").getAsJsonObject();
				ov.setBarberName(freeBarberJo.get("Nick") == null ? null :freeBarberJo.get("Nick").getAsString());
				ov.setBarberTel(freeBarberJo.get("Tel") == null ? null :freeBarberJo.get("Tel").getAsString());
			}
			return ov;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected ArrayList<OrderView> transToOrderViewList(String response) {
		ArrayList<OrderView> ovs = new ArrayList<OrderView>();
		JsonParser parser = new JsonParser();
		JsonElement jsonEl = parser.parse(response);
		JsonArray array = jsonEl.getAsJsonObject().getAsJsonArray("Data");
		
		for(JsonElement je : array){
			OrderView ov = new OrderView();
			ov = transToOrderView(je.getAsJsonObject());
			if(ov != null){
				ovs.add(ov);
			}
		}
		return ovs;
	}
}
