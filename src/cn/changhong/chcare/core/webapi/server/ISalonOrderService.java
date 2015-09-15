package cn.changhong.chcare.core.webapi.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.OrderView;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

public abstract class ISalonOrderService extends AbstractChCareWebApi implements IService{

	public abstract ResponseBean createOrder(OrderView order) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> createOrder(final OrderView order,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(order);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = createOrder(order);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_createOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_createOrder);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean getMyOrders() throws HttpRequestException;
	public <T> Future<ResponseBean<?>> getMyOrders(
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart();
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getMyOrders();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_getMyOrders);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_getMyOrders);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean cancelOrder(int orderId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> cancelOrder(final int orderId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(orderId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = cancelOrder(orderId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_cancelOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_cancelOrder);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean acceptOrder(int orderId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> acceptOrder(final int orderId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(orderId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = acceptOrder(orderId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_acceptOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_acceptOrder);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean rejectOrder(int orderId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> rejectOrder(final int orderId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(orderId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = rejectOrder(orderId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_rejectOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_rejectOrder);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean finishOrder(int orderId, float score) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> finishOrder(final int orderId, final float score,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(orderId, score);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = finishOrder(orderId, score);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_finishOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_finishOrder);
						}
						return bean;
					}
				});
		return future;
	}
	
	public abstract ResponseBean shareOrder(int orderId) throws HttpRequestException;
	public <T> Future<ResponseBean<?>> shareOrder(final int orderId,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(orderId);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = shareOrder(orderId);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.Salon_Order_shareOrder);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.Salon_Order_shareOrder);
						}
						return bean;
					}
				});
		return future;
	}
}
