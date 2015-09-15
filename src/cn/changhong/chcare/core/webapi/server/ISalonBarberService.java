package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonBarberService extends AbstractChCareWebApi implements IService  {
	
	public abstract ResponseBean bindSalon(int salonId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> bindSalon(final int salonId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(salonId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = bindSalon(salonId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Barber_bindSalon);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Barber_bindSalon);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean unbindSalon(int salonId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> unbindSalon(final int salonId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(salonId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = unbindSalon(salonId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Barber_unbindSalon);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Barber_unbindSalon);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getBindSalons(int barberId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getBindSalons(final int barberId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(barberId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBindSalons(barberId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Barber_getBindSalons);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Barber_getBindSalons);
						}
						return bean;
					}
				});
		return future;
	}
}
