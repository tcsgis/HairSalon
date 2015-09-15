/**     
 * @Title: CacheManager.java  
 * @Package com.changhong.crazycat.cache  
 * @Description: TODO  
 * @author guoyang2011@gmail.com    
 * @date 2014-10-9 下午4:12:22  
 * @version V1.0     
*/  
package com.changhong.util.db.bean;  

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aaa.util.Role;

import cn.changhong.chcare.core.webapi.bean.Family;
import cn.changhong.chcare.core.webapi.bean.FamilyMemberInfo;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.bean.User;
import cn.changhong.chcare.core.webapi.bean.UserAppConfig;

/**  
 * 
 * @ClassName: CacheManager  
 * @Description: TODO  
 * @author cxp  
 * @date 2014-10-9 下午4:12:22  
 *     
 */
public class CacheManager {
	//账号相关的基本信息
	private volatile SalonUser currentUser=null;
	private volatile Family currentFamily=null;
	private Map<String,FamilyMemberInfo> familyMembers=new ConcurrentHashMap<String,FamilyMemberInfo>();
	private volatile UserAppConfig currentUConfig = new UserAppConfig();
	
	
	public SalonUser getCurrentUser() {
		return currentUser;
	}

	public synchronized void setCurrentUser(SalonUser currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * 当前用户的设置信息,如果没有返回为默认参数值
	 * @return UserAppConfig
	 */
//	public UserAppConfig getCurrentUConfig() {
//		return currentUConfig;
//	}

//	public synchronized void setCurrentUConfig(UserAppConfig currentUConfig) {
//		this.currentUConfig = currentUConfig;
//	}
	
	public synchronized void clearAllData() {
		currentUser = null;
		currentUConfig = new UserAppConfig();
	}
	
	public static final CacheManager INSTANCE=new CacheManager();
	private CacheManager(){}
	
}
