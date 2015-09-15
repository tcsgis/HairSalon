package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonBidService extends AbstractChCareWebApi implements IService{

	/**
	 * 搜索竞价
	 * @param area
	 * @return
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean getBids(String area) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getBids(final String area,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(area);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBids(area);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_getBids);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_getBids);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getCustomBid() throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getCustomBid(
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart();
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getCustomBid();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_getCustomBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_getCustomBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean publishBid(String Pics, String Desc, String Area) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> publishBid(final String Pics, final String Desc, final String Area,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(Pics, Desc, Area);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = publishBid(Pics, Desc, Area);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_publishBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_publishBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean confirmBid(int offerID, int barberId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> confirmBid(final int offerID, final int barberId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(offerID, barberId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = confirmBid(offerID, barberId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_confirmBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_confirmBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean cancelBid(int offerID) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> cancelBid(final int offerID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(offerID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = cancelBid(offerID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_cancelBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_cancelBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean doBid(int offerID, int price) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> doBid(final int offerID, final int price,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(offerID, price);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = doBid(offerID, price);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_doBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_doBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	/**
	 * 获取参与竞价的自由发型师
	 * @param offerID
	 * @param price
	 * @return
	 * @throws HttpRequestException
	 */
	public abstract ResponseBean getBid(int offerID) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getBid(final int offerID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(offerID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBid(offerID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_getBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_getBid);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean sendCoupon(int CustomerId, float value) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> sendCoupon(final int CustomerId, final float value,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(CustomerId, value);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = sendCoupon(CustomerId, value);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_sendCoupon);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_sendCoupon);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getCustomCoupon() throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getCustomCoupon(
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart();
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getCustomCoupon();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_getCustomCoupon);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_getCustomCoupon);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean useCoupon(int couponId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> useCoupon(final int couponId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(couponId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = useCoupon(couponId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_useCoupon);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_useCoupon);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getBarberBid() throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getBarberBid(
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart();
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getBarberBid();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Bid_getBarberBid);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Bid_getBarberBid);
						}
						return bean;
					}
				});
		return future;
	}
}
