package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonBarberInfoView;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonSalonService  extends AbstractChCareWebApi implements IService {

	public abstract ResponseBean uploadMyBarber(SalonBarberInfoView barber)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> uploadMyBarber(final SalonBarberInfoView barber,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(barber);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = uploadMyBarber(barber);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_uploadMyBarber);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_uploadMyBarber);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean deleteMyBarber(int barberID)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> deleteMyBarber(final int barberID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(barberID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = deleteMyBarber(barberID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_deleteMyBarber);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_deleteMyBarber);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean deleteFreeBarber(int barberID)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> deleteFreeBarber(final int barberID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(barberID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = deleteFreeBarber(barberID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_deleteFreeBarber);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_deleteFreeBarber);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getMyBarber(int salonId)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getMyBarber(final int salonId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(salonId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getMyBarber(salonId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_getMyBarber);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_getMyBarber);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getMyBarberCount(int salonId)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getMyBarberCount(final int salonId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(salonId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getMyBarberCount(salonId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_getMyBarberCount);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_getMyBarberCount);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getFreeBarber(int salonId)throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getFreeBarber(final int salonId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(salonId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getFreeBarber(salonId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Salon_getFreeBarber);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Salon_getFreeBarber);
						}
						return bean;
					}
				});
		return future;
	}
}
