package cn.changhong.chcare.core.webapi.server;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import cn.changhong.chcare.core.webapi.CHCareWebApiFamilyApi;
import cn.changhong.chcare.core.webapi.ChCareAppManagerApi;
import cn.changhong.chcare.core.webapi.ChCareWebApiAccountnApi;
import cn.changhong.chcare.core.webapi.ChCareWebApiOfflineMessageApi;
import cn.changhong.chcare.core.webapi.SalonAccountApi;
import cn.changhong.chcare.core.webapi.SalonAdminApi;
import cn.changhong.chcare.core.webapi.SalonBarberApi;
import cn.changhong.chcare.core.webapi.SalonBidApi;
import cn.changhong.chcare.core.webapi.SalonFileApi;
import cn.changhong.chcare.core.webapi.SalonOrderApi;
import cn.changhong.chcare.core.webapi.SalonSalonApi;

public class CHCareWebApiProvider {

	private static final Map<WebApiServerType, IService> currentServerMap = new WeakHashMap<WebApiServerType, IService>();
	private static final Map<WebApiServerType, Class<? extends IService>> defaultServiceMap = new HashMap<WebApiServerType, Class<? extends IService>>();

	private CHCareWebApiProvider() {
	}

	public static class Self {
		private static CHCareWebApiProvider instance;

		public static CHCareWebApiProvider defaultInstance() {
			if (instance == null) {
				instance = new CHCareWebApiProvider();
			}
			return instance;
		}
	}

	static {
		defaultServiceMap.put(WebApiServerType.CHCARE_ACCOUNT_SERVER,
				ChCareWebApiAccountnApi.class);
		defaultServiceMap.put(WebApiServerType.CHCARE_OFFLINEMESSAGE_SERVER,
				ChCareWebApiOfflineMessageApi.class);
		defaultServiceMap.put(WebApiServerType.CHCARE_FAMILY_SERVER,
				CHCareWebApiFamilyApi.class);
		defaultServiceMap.put(WebApiServerType.CHCARE_CHCAREAPPMANAGERAPI_SERVER,
				ChCareAppManagerApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_ACCOUNT_SERVER,
				SalonAccountApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_FILE_SERVER,
				SalonFileApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_ADMIN_SERVER,
				SalonAdminApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_SALON_SERVER,
				SalonSalonApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_BARBER_SERVER,
				SalonBarberApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_BID_SERVER,
				SalonBidApi.class);
		defaultServiceMap.put(WebApiServerType.SALON_ORDER_SERVER,
				SalonOrderApi.class);
	}

	public static CHCareWebApiProvider newInstance() {
		return Self.defaultInstance();
	}

	public static enum WebApiServerType {
		CHCARE_ACCOUNT_SERVER("accountservice"), CHCARE_HEALTH_SERVER(
				"healthservice"), CHCARE_LOCATION_SERVER("locationservice"), CHCARE_FAMILY_SERVER(
				"familyservice"), CHCARE_PHOTOWALL_SERVER("photowallservice"), CHCARE_OFFLINEMESSAGE_SERVER(
				"offlinemessageservice"), CHCARE_FUNCPHONE_SERVER(
				"funcphoneservice"), CHCARE_CHCAREAPPMANAGERAPI_SERVER(
				"appmanagerserver"), CHCARE_FAMILYDIARY_SERVER(
				"familydiaryservice"), CHCARE_LIFERANGE_SERVER(
				"liferangeservice"), 
				SALON_ACCOUNT_SERVER ("salonaccountservice"),
				SALON_ADMIN_SERVER ("salonAdminservice"),
				SALON_SALON_SERVER ("salonSalonservice"),
				SALON_BARBER_SERVER ("salonBarberservice"),
				SALON_BID_SERVER ("salonBidservice"),
				SALON_ORDER_SERVER ("salonOderservice"),
				SALON_FILE_SERVER ("salonfileservice");
		private String value;

		private WebApiServerType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public IService getDefaultWebApiService(WebApiServerType type) {
		IService result = currentServerMap.get(type);
		if (result == null) {
			Class<?> serviceClass = defaultServiceMap.get(type);
			if (serviceClass != null) {
				try {
					result = (IService) serviceClass.getConstructors()[0]
							.newInstance((Object[]) null);
					synchronized (currentServerMap) {
						currentServerMap.put(type, result);
					}
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
