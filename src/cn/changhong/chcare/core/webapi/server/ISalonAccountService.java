package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonAccountService extends AbstractChCareWebApi implements IService {

	/*username:{string} 用户名(要求电话号码)
	type:{int} 0:注册, 1:重置密码 role:{byte} 用户身份 {0: 未定义, 1: 顾客, 2: 理发店, 4: 发型师, 0xff(255): 系统管理员}*/
	public abstract ResponseBean getVetifyCode(String phoneNumber, int type, byte role)throws HttpRequestException;
	
	public <T> Future<ResponseBean> getVetifyCode(final String phoneNumber,
			final int type, final byte role, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(phoneNumber,type);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getVetifyCode(phoneNumber, type, role);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_getVetufyCode);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_getVetufyCode);
						}
						return bean;
					}
				});
		return future;
	}
	
//	从获取到流程结束有效时间为5分钟
//	调试时可用1234开头电话, 密码123456
//	SUCCESS 否 0 成功 
//	INV_ARGS 是 -5 非法参数 
//	VFY_ERR 否 -8 验证码错误 
//	DATA_OVERDUE 否 -16 验证码过期 
	
//	 UserName:{string}                           //用户
//    VerifyCode:{string}                         //验证码
//    Type:{VerifyCodeType}                       //验证码类型, VerifyCodeType->{0:注册, 1:找回密码}

	public abstract ResponseBean verifyCode(String username, String code, int type)throws HttpRequestException;
	
	public <T> Future<ResponseBean> verifyCode(final String username,
			final String code, final int type,
			final AsyncResponseCompletedHandler<T> handler){
		handler.onStart(username,type);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = verifyCode(username, code, type);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_VetufyCode);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_VetufyCode);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean register(String username,
			String verifyCode, String newPassword, byte role)throws HttpRequestException;
	
	public <T> Future<ResponseBean> register(final String username,
			final String verifyCode, final String newPassword, final byte role,
			final AsyncResponseCompletedHandler<T> handler){
		handler.onStart(username,verifyCode, newPassword, role);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = register(username, verifyCode, newPassword, role);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_register);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_register);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean resetSecret(String username, String newPassword,
			String verifyCode)throws HttpRequestException;
	
	public <T> Future<ResponseBean> resetSecret(final String username,
			final String verifyCode, final String newPassword,
			final AsyncResponseCompletedHandler<T> handler){
		handler.onStart(username,verifyCode, newPassword);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = resetSecret(username, verifyCode, newPassword);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_resetSecret);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_resetSecret);
						}
						return bean;
					}
				});
		return future;
	}
	
	
	
	public abstract ResponseBean verifyOldSecret(String username, String oldPassword)throws HttpRequestException;
	
	public <T> Future<ResponseBean> verifyOldSecret(final String username, final String oldPassword, 
			final AsyncResponseCompletedHandler<T> handler){
		handler.onStart(username, oldPassword);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = verifyOldSecret(username, oldPassword);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_verifyOldSecret);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_verifyOldSecret);
						}
						return bean;
					}
				});
		return future;
	}
	
	
	
	public abstract ResponseBean changeSecret(String username, String oldPassword)throws HttpRequestException;
	
	public <T> Future<ResponseBean> changeSecret(final String username, final String newPassword, 
			final AsyncResponseCompletedHandler<T> handler){
		handler.onStart(username, newPassword);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = changeSecret(username, newPassword);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_changeSecret);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_changeSecret);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean login(String username, String password)
			throws HttpRequestException;
	
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
									ChCareWepApiServiceType.Salon_Account_new_login);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_new_login);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean logout() throws HttpRequestException;
	
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
									ChCareWepApiServiceType.Salon_Account_logout);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_logout);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getUser(int userId) throws HttpRequestException;
	public <T> Future<ResponseBean> getUser(final int userId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(userId);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getUser(userId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_getUser);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_getUser);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean updateSelfMg(SalonUser user) throws HttpRequestException;
	public <T> Future<ResponseBean> updateSelfMg(final SalonUser user,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(user);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateSelfMg(user);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_updateSelfMg);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_updateSelfMg);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean searchUser(byte role, String key, String area) throws HttpRequestException;
	public <T> Future<ResponseBean> searchUser(final byte role, final String key, final String area,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(role, key, area);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = searchUser(role, key, area);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_searchUser);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_searchUser);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getBannerUsers(String area) throws HttpRequestException;
	public <T> Future<ResponseBean> getBannerUsers(final String area,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(area);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBannerUsers(area);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_getBannerUsers);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_getBannerUsers);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getBanners() throws HttpRequestException;
	public <T> Future<ResponseBean> getBanners(
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart();
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBanners();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Account_getBanners);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Account_getBanners);
						}
						return bean;
					}
				});
		return future;
	}
}
