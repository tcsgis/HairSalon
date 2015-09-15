package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonAdminService  extends AbstractChCareWebApi implements IService {
	
	public abstract ResponseBean getUnchecked(byte role, String area)
			throws HttpRequestException;
	
	public <T> Future<ResponseBean> getUnchecked(final byte role, final String area, 
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(role, area);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getUnchecked(role, area);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Admin_getUnchecked);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Admin_getUnchecked);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean check(int userId, boolean pass, byte level)
			throws HttpRequestException;
	
	public <T> Future<ResponseBean> check(final int userId, final boolean pass, final byte level, 
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(userId, pass, level);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = check(userId, pass, level);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Admin_check);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Admin_check);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean updateAdPics(BannerPic bp) throws HttpRequestException;
	public <T> Future<ResponseBean> updateAdPics(final BannerPic bp, 
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(bp);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateAdPics(bp);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Admin_updateAdPics);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Admin_updateAdPics);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean updateMallPics(BannerPic bp) throws HttpRequestException;
	public <T> Future<ResponseBean> updateMallPics(final BannerPic bp, 
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(bp);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateMallPics(bp);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Admin_updateMallPics);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Admin_updateMallPics);
						}
						return bean;
					}
				});
		return future;
	}
}
