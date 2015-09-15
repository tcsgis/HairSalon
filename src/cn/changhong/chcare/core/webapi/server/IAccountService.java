package cn.changhong.chcare.core.webapi.server;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.User;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

/**
 * 与账号相关服务的接口定义
 * 
 * @ClassName: IAccountService
 * @Description: TODO
 * @author guoyang2011@gmail.com
 * @date 2014-9-24 下午7:05:23
 * 
 */
public abstract class IAccountService extends AbstractChCareWebApi implements
		IService {
	/**
	 * 注册账号第一步:验证手机号码 [1.5 获取短信验证码]
	 * 
	 * @Title: registerStage1
	 * @Description: TODO
	 * @param @param phoneNumber 电话号码
	 * @param @param type : 0 表示注册,1表示修改密码
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean registerStage1(String phoneNumber, int type)
			throws HttpRequestException;

	/**
	 * 注册账号第一步:验证手机号码 [1.5 获取短信验证码]
	 * 
	 * @Title: registerStage1
	 * @Description: TODO
	 * @param @param phoneNumber 电话号码
	 * @param @param type : 0 表示注册,1表示修改密码
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> registerStage1(final String phoneNumber,
			final int type, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(phoneNumber,type);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = registerStage1(phoneNumber, type);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_registerStage1_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_registerStage1_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 注册账号第二步:验证验证码 [1.6.验证短信验证码]
	 * 
	 * @Title: registerStage2
	 * @Description: TODO
	 * @param @param username 电话号码
	 * @param @param verifyCode : 收到的手机验证码
	 * @param @param type : 0 表示注册,1表示修改密码
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean registerStage2(String username,
			String verifyCode, int type) throws HttpRequestException;

	/**
	 * 注册账号第二步:验证验证码 [1.6.验证短信验证码]
	 * 
	 * @Title: registerStage2
	 * @Description: TODO
	 * @param @param username 电话号码
	 * @param @param verifyCode 收到的手机验证码
	 * @param @param type 0 表示注册,1表示修改密码
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> registerStage2(final String username,
			final String verifyCode, final int type,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username,verifyCode,type);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = registerStage2(username, verifyCode, type);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_registerStage2_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_registerStage2_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 注册账号第三步:输入密码 [1.7.新用户注册设置密码]
	 * 
	 * @Title: registerStage3
	 * @Description: TODO
	 * @param @param username 电话号码
	 * @param @param verifyCode 收到的手机验证码
	 * @param @param newPassword 新密码
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean registerStage3(String username,
			String verifyCode, String newPassword) throws HttpRequestException;

	/**
	 * 注册账号第三步:输入密码 [1.7.新用户注册设置密码]
	 * 
	 * @Title: registerStage3
	 * @Description: TODO
	 * @param @param username
	 * @param @param verifyCode : 收到的手机验证码
	 * @param @param newPassword : 新密码
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> registerStage3(final String username,
			final String verifyCode, final String newPassword,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username,verifyCode,newPassword);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = registerStage3(username, verifyCode,
									newPassword);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_registerStage3_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_registerStage3_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 登陆 [1.1 登陆]
	 * 
	 * @Title: login
	 * @Description: TODO
	 * @param @param username
	 * @param @param password
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean login(String username, String password)
			throws HttpRequestException;

	/**
	 * 登陆 [1.1 登陆]
	 * 
	 * @Title: login
	 * @Description: TODO
	 * @param @param username
	 * @param @param password
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> login(final String username,
			final String password,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username,password);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = login(username, password);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_login_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_login_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 登陆新接口(避免冲突，老接口也保留) [1.17登陆]
	 * 
	 * @Title: new_login
	 * @Description: TODO
	 * @param @param username
	 * @param @param password
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<SecureModel>}
	 * @throws
	 */
	public abstract ResponseBean new_login(String username, String password)
			throws HttpRequestException;

	/**
	 * 新登陆 [1.17 新登陆]
	 * 
	 * @Title: new_login
	 * @Description: TODO
	 * @param @param username
	 * @param @param password
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<SecureModel>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> new_login(final String username,
			final String password,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username,password);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = new_login(username, password);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_new_login_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_new_login_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 查询用户基本信息 [1.3 查询用户详情]
	 * 
	 * @Title: searchUserInfo
	 * @Description: TODO
	 * @param @param username
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<User>}
	 * @throws
	 */
	public abstract ResponseBean searchUserInfo(String username)
			throws HttpRequestException;

	/**
	 * 查询用户基本信息 [1.3 查询用户详情]
	 * 
	 * @Title: searchUserInfo
	 * @Description: TODO
	 * @param @param username
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<User>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> searchUserInfo(final String username,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = searchUserInfo(username);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_searchUserInfo_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_searchUserInfo_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 更新用户信息 [1.4 更新用户信息]
	 * 
	 * @Title: updateUserInfo
	 * @Description: TODO
	 * @param @param user
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean updateUserInfo(User user)
			throws HttpRequestException;

	/**
	 * 更新用户信息 [1.4 更新用户信息]
	 * 
	 * @Title: updateUserInfo
	 * @Description: TODO
	 * @param @param user
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> updateUserInfo(final User user,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(user);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateUserInfo(user);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_updateUserInfo_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_updateUserInfo_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 忘记密码-修改密码 [1.8.修改密码]
	 * 
	 * @Title: updateNewPassword
	 * @Description: TODO
	 * @param @param username
	 * @param @param newPassword 新密码
	 * @param @param verifyCode 收到的手机验证码
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean updateNewPassword(String username,
			String newPassword, String verifyCode) throws HttpRequestException;

	/**
	 * 忘记密码-修改密码 [1.8.修改密码]
	 * 
	 * @Title: updateNewPassword
	 * @Description: TODO
	 * @param @param username
	 * @param @param newPassword : 新密码
	 * @param @param verifyCode : 收到的手机验证码
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> updateNewPassword(final String username,
			final String newPassword, final String verifyCode,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(username,newPassword,verifyCode);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateNewPassword(username, newPassword,
									verifyCode);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_updateNewPassword_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_updateNewPassword_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 设置新密码 [1.14.用旧密码修改密码] 说明：修改密码后需要重新登录
	 * 
	 * @Title: changePassword
	 * @Description: TODO
	 * @param @param oldPwd
	 * @param @param newPwd
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean<?> setNewPassword(String username,String oldPwd, String newPwd)
			throws HttpRequestException;

	/**
	 * 设置新密码 [1.14.用旧密码修改密码] 说明：修改密码后需要重新登录
	 * 
	 * @Title: changePassword
	 * @Description: TODO
	 * @param @param oldPwd
	 * @param @param newPwd
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> setNewPassword(final String username,final String oldPwd,
			final String newPwd, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(oldPwd,newPwd);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean<?> call() {
						ResponseBean<?> bean = null;
						try {
							bean = setNewPassword(username,oldPwd, newPwd);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_setNewPassword_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_setNewPassword_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 上传头像 [1.9.上传或更新头像]
	 * 
	 * @Title: uploadUserPhoto
	 * @Description: TODO
	 * @param @param filepath : 文件的绝对路径
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean uploadUserPhoto(String filepath)
			throws HttpRequestException;

	/**
	 * 上传头像 [1.9.上传或更新头像]
	 * 
	 * @Title: uploadUserPhoto
	 * @Description: TODO
	 * @param @param instream : 文件输入流
	 * @param @param filename : 文件名(带扩展名)
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean uploadUserPhoto(InputStream instream,
			String filename) throws HttpRequestException;

	/**
	 * 上传头像 [1.9.上传或更新头像]
	 * 
	 * @Title: uploadUserPhoto
	 * @Description: TODO
	 * @param @param instream : 文件输入流
	 * @param @param filename : 文件名(带扩展名)
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> uploadUserPhoto(final InputStream instream,
			final String filename,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(instream,filename);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = uploadUserPhoto(instream, filename);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_uploadUserPhoto_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_uploadUserPhoto_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 上传头像 [1.9.上传或更新头像]
	 * 
	 * @Title: uploadUserPhoto
	 * @Description: TODO
	 * @param @param filepath : 文件的绝对路径
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> uploadUserPhoto(final String filepath,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(filepath);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = uploadUserPhoto(filepath);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_uploadUserPhoto_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_uploadUserPhoto_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 注销 [1.2 注销 ]
	 * 
	 * @Title: logout
	 * @Description: TODO
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean logout() throws HttpRequestException;

	/**
	 * 注销 1.2 注销
	 * 
	 * @Title: logout
	 * @Description: TODO
	 * @param @param handler
	 * @param @return
	 * @return Future<ResponseBean<NULL>>
	 * @throws
	 */
	public <T> Future<ResponseBean> logout(
			final AsyncResponseCompletedHandler<T> handler) {
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = logout();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_logout_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_logout_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 下载头像 [1.10.下载头像]
	 * 
	 * @Title: getUserIcon
	 * @Description: TODO
	 * @param @param iconUrl 获取的当前账号的User对象中的PhotoUrl值
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<CHCareFileInStream>}
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean getUserIcon(String iconUrl)
			throws HttpRequestException;

	/**
	 * 下载头像 [1.10.下载头像]
	 * 
	 * @Title: getUserIcon
	 * @Description: TODO
	 * @param @param iconUrl 获取的当前账号的User对象中的PhotoUrl值
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean>}
	 * @throws
	 */
	public <T> Future<ResponseBean> getUserIcon(final String iconUrl,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(iconUrl);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getUserIcon(iconUrl);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_getUserIcon_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_getUserIcon_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 领取金币 [1.11.签到领取财富]
	 * 
	 * @Title: updateFamilyWealth
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param type
	 * @param @param comment
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return (@code ResponseBean<NULL>} State为0表示领取成功,为1表示当天已经领取过
	 * @throws
	 */
	public abstract ResponseBean updateFamilyWealth(int familyID, int type,
			String comment) throws HttpRequestException;

	/**
	 * 领取金币 [1.11.签到领取财富]
	 * 
	 * @Title: updateFamilyWealth
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param type
	 * @param @param comment
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>} State为0表示领取成功,为1表示当天已经领取过
	 * @throws
	 */
	public <T> Future<ResponseBean> updateFamilyWealth(final int familyID,
			final int type, final String comment,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,type,comment);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateFamilyWealth(familyID, type, comment);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_updateFamilyWealth_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_updateFamilyWealth_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 金币捐赠(新|暂定) [17 金币捐赠]
	 * 
	 * @Title: donateGoldCoin
	 * @Description: TODO
	 * @param  fid(家庭ID)
	 * @param  quantity(成员捐献的金币数量)
	 * @return {@code Future<ResponseBean<NULL>>}
	 *         State为0表示领取成功,-1数据库错误，－5非法参数(金币不足或捐赠金币数为负)，－7没有权限(你不是该家庭成员或该家庭不存在
	 *         /已解散)
	 * @throws
	 */
	public abstract ResponseBean donateGoldCoin(int fid ,int quantity)
			throws HttpRequestException;

	/**
	 * 金币捐赠(新|暂定) [17 金币捐赠]
	 * 
	 * @Title: donateGoldCoin
	 * @Description: TODO
	 * @param  fid(家庭ID)
	 * @param quantity(成员捐献的金币数量)
	 * @param handler
	 * @return {@code Future<ResponseBean<NULL>>}
	 *         State为0表示领取成功,-1数据库错误，－5非法参数(金币不足或捐赠金币数为负)，－7没有权限(你不是该家庭成员或该家庭不存在
	 *         /已解散)
	 * @throws
	 */
	public <T> Future<ResponseBean> donateGoldCoin(final int fid,final int quantity, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(fid,quantity);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = donateGoldCoin(fid,quantity);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_donateGoldCoin_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_donateGoldCoin_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 签到(新|暂定) [0.4.0]
	 * 
	 * @Title: UserTreasure
	 * @Description: TODO
	 * @param @param Type (签到类型->填0就好了)
	 * @param @return
	 * @return {@code Future<ResponseBean<int>>} State为0表示领取成功,-1数据库错误,-5非法参数(类型不正确)
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean userTreasure(int type)
			throws HttpRequestException;

	/**
	 * 签到(新|暂定) [0.4.0]
	 * 
	 * @Title: UserTreasure
	 * @Description: TODO
	 * @param @param Type (签到类型->填0就好了)
	 * @param @return
	 * @return {@code Future<ResponseBean<int>>} State为0表示领取成功,-1数据库错误,-5非法参数(类型不正确)
	 * @throws
	 */
	public <T> Future<ResponseBean> UserTreasure(final int type,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(type);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() throws Exception {
						ResponseBean bean = null;
						try {
							bean = userTreasure(type);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_userTreasure_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_userTreasure_Service);
						}
						return null;
					}

				});
		return future;
	}

	/**
	 * 根据用户昵称或用户名模糊查询用户 [1.12.模糊查询用户]
	 * 
	 * @Title: searchUsers
	 * @Description: TODO
	 * @param @param keyword 用户名或者用户昵称
	 * @param @param startIndex 第一次查询为0，第N查询为N*count+1
	 * @param @param count 每页返回总的条数
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<List<User>>}
	 * @throws
	 */
	public abstract ResponseBean searchUsers(String keyword, int startIndex,
			int count) throws HttpRequestException;

	/**
	 * 根据用户昵称或用户名模糊查询用户 [1.12.模糊查询用户]
	 * 
	 * @Title: searchUsers
	 * @Description: TODO
	 * @param @param keyword 用户名或者用户昵称
	 * @param @param startIndex 第一次查询为0，第N查询为N*count+1
	 * @param @param count 每页返回总的条数
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<List<User>>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> searchUsers(final String keyword,
			final int startIndex, final int count,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(keyword,startIndex,count);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = searchUsers(keyword, startIndex, count);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_searchUsers_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_searchUsers_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 判断用户当天是否签到,签到状态 [1.13.获取签到状态]
	 * 
	 * @Title: isSignIn
	 * @Description: TODO
	 * @param type:{int?}签到类型 填0就好了, 对旧版兼容为可选参数, 默认0
	 * @param @return 返回状态 0 代表未签到，1代表已经签到 ,-1数据库错误,-5
	 *        非法参数(类型不正确),-7没有权限(你不是该家庭成员或该家庭不存在/已解散)
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean<?> isSignIn(int type)
			throws HttpRequestException;

	/**
	 * 判断用户当天是否签到 [1.13.获取签到状态]
	 * 
	 * @Title: isSignIn
	 * @Description: TODO
	 * @param type:{int?}签到类型 填0就好了, 对旧版兼容为可选参数, 默认0
	 * @param handler
	 * @param @return {@code Future<ResponseBean<NULL>>} 返回状态 0 代表未签到，1代表已经签到
	 *        ,-1数据库错误,-5 非法参数(类型不正确),-7没有权限(你不是该家庭成员或该家庭不存在/已解散)
	 * @return Future<ResponseBean<?>>
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> isSignIn(final int type,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(type);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean<?> call() {
						ResponseBean<?> bean = null;
						try {
							bean = isSignIn(type);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_isSignIn_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_isSignIn_Service);
						}
						return bean;
					}
				});
		return future;
	}

	public abstract String createUserPhotoAbsoluteUrl(String url);

	/**
	 * [1.16]
	 * 
	 * @Title: getUserInfoById
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return ResponseBean<?>
	 * @throws
	 */
	public abstract ResponseBean<?> getUserInfoById(int id)
			throws HttpRequestException;

	/**
	 * [1.16]
	 * 
	 * @Title: getUserInfoById
	 * @Description: TODO
	 * @param @param id
	 * @param @param handler
	 * @param @return
	 * @return Future<ResponseBean<?>>
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> getUserInfoById(final int id,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(id);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean<?> call() {
						ResponseBean<?> bean = null;
						try {
							bean = getUserInfoById(id);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Account_getUserInfoById_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Account_getUserInfoById_Service);
						}
						return bean;
					}
				});
		return future;
	}

}
