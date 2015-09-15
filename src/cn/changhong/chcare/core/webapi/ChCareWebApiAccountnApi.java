package cn.changhong.chcare.core.webapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.changhong.chcare.core.webapi.bean.CHCareFileInStream;
import cn.changhong.chcare.core.webapi.bean.Family;
import cn.changhong.chcare.core.webapi.bean.FamilyMemberInfo;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.User;
import cn.changhong.chcare.core.webapi.server.ChCareWebApiRequestErrorType;
import cn.changhong.chcare.core.webapi.server.IAccountService;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;
import cn.changhong.chcare.core.webapi.util.TokenManager;

import com.changhong.activity.util.Tools;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ChCareWebApiAccountnApi extends IAccountService {
	@Override
	public ResponseBean<?> registerStage1(String phoneNumber, int type)
			throws HttpRequestException {
		String url = BASE_URL + "VerifyCode?Username=" + phoneNumber
				+ "&type=" + type;
		return this.getRequestUtil(url);
	}

	@Override
	public ResponseBean<?> registerStage2(String username, String verifyCode,
			int type) throws HttpRequestException {

		String url = BASE_URL + "VerifyCode";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Username", username);
		params.put("VerifyCode", verifyCode);
		params.put("Type", type);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean<?> registerStage3(String username, String verifyCode,
			String newPassword) throws HttpRequestException {
		String url = BASE_URL + "SetPassword";

		System.out.println(url);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Username", username);
		String password_encryed = Tools.des_Encypt(username, newPassword);
		params.put("Password", password_encryed);
		params.put("VerifyCode", verifyCode);
		params.put("PwdMode", 1);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean<?> login(String username, String password)
			throws HttpRequestException {
		String url = BASE_URL + "Token";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Usr", username);
		String password_encryed = Tools.des_Encypt(username, password);
		params.put("PwdMode", 1);
		params.put("Pwd", password_encryed);
		params.put("ClientId", "1");

		ResponseBean<?> loginBean = this.postRequestUtil(url,
				this.gson.toJson(params));
		if (loginBean.getState() == 0) {
			TokenManager.setToken(loginBean.getData().toString());
		}
		return loginBean;
	}

	@Override
	public ResponseBean<?> searchUserInfo(String username)
			throws HttpRequestException {

		String url = BASE_URL + "UserInfo";
		if (username == null) {
			url += "?Username=";
		} else {
			try {
				url += "?Username=" + URLEncoder.encode(username, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(url);
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = this.transToBean(response);
		if (result != null && result.getState() >= 0) {
			Type type = new TypeToken<ResponseBean<User>>() {
			}.getType();
			result = this.transToBean(response, type);
		}
		return result;
	}

	@Override
	public ResponseBean<?> updateUserInfo(User user)
			throws HttpRequestException {

		String url = BASE_URL + "UserInfo";
		return this.postRequestUtil(url, this.gson.toJson(user));
	}

	@Override
	public ResponseBean<?> updateNewPassword(String username,
			String newPassword, String verifyCode) throws HttpRequestException {
		String url = BASE_URL + "ChangePassword";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("UserName", username);
		String password_encryed = Tools.des_Encypt(username, newPassword);
		params.put("PwdMode", 1);
		params.put("NewPassword", password_encryed);
		params.put("VerifyCode", verifyCode);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	@Override
	public ResponseBean<?> uploadUserPhoto(String filepath)
			throws HttpRequestException {
		if (filepath == null) {
			throw new HttpRequestException("Enter File Path Is Null!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		File file = new File(filepath);
		if (!file.isFile()) {
			throw new HttpRequestException("Enter path=[" + filepath
					+ "] Is Not File!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		InputStream instream = null;
		try {
			instream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new HttpRequestException("Read File Stream Error!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		return this.uploadUserPhoto(instream, file.getName());
	}

	/**
	 * 
	 * 
	 * @param instream
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#uploadUserPhoto(java.io.InputStream)
	 */

	@Override
	public ResponseBean<?> uploadUserPhoto(InputStream instream, String filename)
			throws HttpRequestException {
		String url = BASE_URL + "UserAvatar";
		if (filename == null || filename.trim().length() == 0) {
			throw new HttpRequestException(
					"Illegal Input Args,FileName Is NUll!",
					ChCareWebApiRequestErrorType.CHCAREWEBAPI_REQUEST_ERROR);
		}
		return this.transToBean(this.doPostSingleFileUsedFormType(url, instream,
				new HashMap<String, String>(), filename));
	}

	@Override
	public ResponseBean<?> logout() throws HttpRequestException {
		String url = BASE_URL + "Token";
		Map<String, Object> params = new HashMap<String, Object>();
		return this.deleteRequestUtil(url, this.gson.toJson(params));
	}

	/**
	 * 
	 * 
	 * @param iconUrl
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#getUserIcon(java.lang.String)
	 */

	@Override
	public ResponseBean<CHCareFileInStream> getUserIcon(String iconUrl)
			throws HttpRequestException {
		String url = BASE_URL + "UserAvatar?photoUrl=" + iconUrl;
		System.out.println(url);
		// this.getRequestUtil(url);

		CHCareFileInStream data = this.httpRequestHandler.getPhotoFile(url);
		ResponseBean<CHCareFileInStream> result = new ResponseBean<CHCareFileInStream>();
		result.setState(0);
		result.setData(data);
		result.setDesc("操作成功");
		return result;
	}

	/**
	 * 
	 * 
	 * @param userID
	 * @param familyID
	 * @param amount
	 * @param comment
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#updateFamilyWealth(java.lang.String,
	 *      java.lang.String, int, java.lang.String)
	 */

	@Override
	public ResponseBean<?> updateFamilyWealth(int familyID, int type,
			String comment) throws HttpRequestException {
		String url = BASE_URL + "UserBalance";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("FamilyID", familyID);
		params.put("Type", type);
		params.put("Comment", comment);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	/**
	 * 
	 * 
	 * @param keyword
	 * @param startIndex
	 * @param count
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#searchUsers(java.lang.String,
	 *      int, int)
	 */

	@Override
	public ResponseBean<?> searchUsers(String keyword, int startIndex, int count)
			throws HttpRequestException {
		String url = BASE_URL + "UserSearch?index=" + startIndex + "&count="
				+ count;
		if (keyword != null && keyword.trim().length() > 0) {
			try {
				url += "&keyWord=" + URLEncoder.encode(keyword, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(url);
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = this.transToBean(response);
		if (result != null && result.getState() >= 0) {
			Type type = new TypeToken<ResponseBean<List<User>>>() {
			}.getType();
			result = this.transToBean(response, type);
		}
		return result;
	}

	/**
	 * 
	 * 
	 * @param url
	 * @return
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#createUserPhotoAbstractUrl(java.lang.String)
	 */

	@Override
	public String createUserPhotoAbsoluteUrl(String url) {
		return BASE_URL + "UserAvatar?photoUrl=" + url;
	}

	/**
	 * 
	 * 
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#isSignIn()
	 */

	@Override
	public ResponseBean<?> isSignIn(int type) throws HttpRequestException {
		String url = BASE_URL + "SignState?type=" + type;

		return this.getRequestUtil(url);
	}

	/**
	 * 
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#setNewPassword(java.lang.String,
	 *      java.lang.String)
	 */

	@Override
	public ResponseBean<?> setNewPassword(String username,String oldPwd, String newPwd)
			throws HttpRequestException {
		String url = BASE_URL + "SetNewPassword";
		Map<String, Object> params = new HashMap<String, Object>();
		String password_encryed = Tools.des_Encypt(username, newPwd);
		params.put("PwdMode", 1);
		String old_password_encryed = Tools.des_Encypt(username, oldPwd);
		params.put("OPassword", old_password_encryed);
		params.put("NPassword", password_encryed);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * @throws HttpRequestException
	 * @see cn.changhong.chcare.core.webapi.server.IAccountService#getUserInfoById(int)
	 */

	@Override
	public ResponseBean<?> getUserInfoById(int id) throws HttpRequestException {
		String url = BASE_URL + "UserInfoByID?id=" + id;
		System.out.println(url);
		String response = this.baseGetRequestUtil(url);
		ResponseBean<?> result = this.transToBean(response);
		if (result != null && result.getState() >= 0) {
			Type type = new TypeToken<ResponseBean<User>>() {
			}.getType();
			result = this.transToBean(response, type);
		}
		return result;
	}

	@Override
	public ResponseBean<?> new_login(String username, String password)
			throws HttpRequestException {
		String url = BASE_URL + "Token?v=2&misc=UserInfo,GetFamilyInfoList,GetFamilyMember";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Usr", username);
		String password_encryed = Tools.des_Encypt(username, password);
		params.put("Pwd", password_encryed);
		params.put("ClientId", "1");
		params.put("PwdMode", 1);
		

		String response = this.basePostRequestUtil(url, this.gson.toJson(params));

		ResponseBean<?> result = this.transToBean(response);
		
		if (result != null && result.getState() >= 0) {

			ResponseBean<Map<String, Object>> return_resultBean = new ResponseBean<Map<String, Object>>();
			
			Map<String, Object> date_map = new HashMap<String, Object>();

			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(response);

			JsonObject jsonObj = jsonEl.getAsJsonObject().getAsJsonObject("Data");// 转换成Json对象

			TokenManager.setToken(jsonObj.getAsJsonPrimitive("Token").getAsString());
			TokenManager.setFiletoken(jsonObj.getAsJsonPrimitive("FileToken").getAsString());
			jsonObj = jsonObj.getAsJsonObject("Extra");// 转换成Json对象

			JsonObject userJs = jsonObj.getAsJsonObject("UserInfo");
			JsonArray familyJs = jsonObj.getAsJsonArray("GetFamilyInfoList");

			User user = gson.fromJson(userJs, User.class);
			date_map.put("UserInfo", user);

			if(familyJs.size() > 0){

				Type beanType = new TypeToken<List<Family>>() {
				}.getType();
				List<Family> familys = gson.fromJson(familyJs, beanType);
				date_map.put("GetFamilyInfoList", familys);


				if(!(familys == null  || familys.isEmpty())){

					Family ams = familys.get(0);
					JsonArray memberJs = jsonObj.getAsJsonObject("GetFamilyMember").getAsJsonArray(String.valueOf(ams.getID()));
					beanType = new TypeToken<List<FamilyMemberInfo>>() {
					}.getType();
					List<FamilyMemberInfo> members = gson.fromJson(memberJs, beanType);
					date_map.put("GetFamilyMember", members);
				}
			}

			return_resultBean.setData(date_map);
			return_resultBean.setState(result.getState());
			return_resultBean.setDesc(result.getDesc());
			
			result = return_resultBean;
			
		}
		return result;
	}

	@Override
	public ResponseBean donateGoldCoin(int fid,int quantity)
			throws HttpRequestException {
		String url = BASE_URL + "DonateGoldCoin";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Quantity", quantity);
		params.put("FamilyID", fid);
		return this.postRequestUtil(url, this.gson.toJson(params));
		
	}

	@Override
	public ResponseBean userTreasure(int type) throws HttpRequestException {
		String url = BASE_URL + "UserTreasure";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Type", type);
		return this.postRequestUtil(url, this.gson.toJson(params));
	}

}
