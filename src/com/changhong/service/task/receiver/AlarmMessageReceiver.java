package com.changhong.service.task.receiver;

import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.changhong.chcare.core.webapi.bean.OfflineMessageBean;
import cn.changhong.chcare.core.webapi.bean.ResponseBeanWithRange;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.IOfflineMessageService;
import com.aaa.util.Role;
import com.changhong.CHApplication;
import com.changhong.service.msg.MessageType;
import com.changhong.service.task.AlarmBackRunTask;
import com.changhong.util.CHLogger;
import com.changhong.util.config.MyProperties;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;

public class AlarmMessageReceiver extends AlarmBackRunTask{

	private static final int GET_NEW_CUSTOM_BID = 10001;//有新的发型师参与竞标

	private static final int mBackRunInterval = 10 * 1000;//后台运行时触发事件间隔
	private static final int mForegroudInterval = 10 * 1000;//前台运行时触发事件间隔
	
	private static long mStartIndex = -1;
	private static int userId = -1;
	private static boolean haveNotification = true;


	private Handler mMainHandler;
	private MyHandler mMyHandler;
	private int mPreviousTime = 0;//最大为6

	private SharedPreferences mSP;

	private CHApplication application = CHApplication.getApplication();

	private Bundle retNotifyInfo;

	private IOfflineMessageService mMessageService = 
			(IOfflineMessageService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.CHCARE_OFFLINEMESSAGE_SERVER);

	private static AlarmMessageReceiver instance;
	private AlarmMessageReceiver(){

	}

	public static AlarmMessageReceiver getInstance(){

		if(instance == null){
			instance = new AlarmMessageReceiver();
			instance.initStart();
		}
		return instance;
	};

	public void setmMainHandler(Handler mMainHandler) {
		this.mMainHandler = mMainHandler;
	}

	public void setHaveNotification(boolean haveNotification) {
		AlarmMessageReceiver.haveNotification = haveNotification;
	}
	
	@Override
	protected Bundle doTask(Date date) {
		Bundle ret = null;
		try {
			requireNotify();
			if(retNotifyInfo != null && !isAppForegroud() && haveNotification )ret = retNotifyInfo;//如果是前台运行就不用通知提醒
			retNotifyInfo = null;
		} catch (Exception e) {
			CHLogger.e(this, "=doTask="+e.getLocalizedMessage());
		}
		return ret;
	}

	@Override
	protected int getRunInterval(boolean isAppForegroud) {
		//在前台并且没有异常情况时频率为10秒
		
//		if(isAppForegroud && mPreviousTime < 1){
			return mForegroudInterval;
//		}else{
//			return (mPreviousTime + 1) * mBackRunInterval;
//		}

	}
	
	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {


		@Override
		public void handleMessage(Message msg) {
			if (CacheManager.INSTANCE.getCurrentUser() == null) {
				return;
			}
			CHLogger.e(this, "msg.what====>"+msg.what);
			switch (msg.what) {
			
			case GET_NEW_CUSTOM_BID:
				doGetCustomBid();
				break;
			}
		}
	}

	private void initStart() {
		
		try {
			if(mMyHandler == null){
				mMyHandler = new MyHandler();
			}	

			if(application == null){
				application = CHApplication.getApplication();
			}
			
			String receiveCenterKey = MyProperties.getMyProperties().getReceiveNotifyKey();
			haveNotification = CHApplication.getApplication().getPreferenceConfig().getBoolean(receiveCenterKey, true);
		} catch (Exception e) {
			CHLogger.e(this, e);
		}
		
	}
	
	public void doGetCustomBid() {
		//todo
		sendMainEmptyMessage(MessageType.CUSTOM_NEW_BID);
		showToast(R.string.msg_string4);
	}

	@SuppressWarnings("unchecked")
	private void requireNotify() {
		if(CacheManager.INSTANCE.getCurrentUser() == null) return;

		int reState = 0;
		try {

			int currentUserId = CacheManager.INSTANCE.getCurrentUser().getId();
			initSharedPrefrences(currentUserId);
			CHLogger.e(this, "requireNotify, startIndex = " + mStartIndex);

			ResponseBeanWithRange<?> response = mMessageService.pollingMessage(mStartIndex, -1);

			if (response != null && response.getState() >= 0) {
				refreshStartIndex(response.getEndIndex());
				
				List<OfflineMessageBean<?>> msgList = (List<OfflineMessageBean<?>>) response.getData();
				if (msgList != null && msgList.size() > 0) {
					handleNotify(msgList);
				}
			} else if(response.getState() == -15){//cxp temp
				retNotifyInfo = new Bundle();
				retNotifyInfo.putInt("state", response.getState());
			} 
			
			reState = response.getState();
		} catch (Exception e) {
			CHLogger.e(this, e.getLocalizedMessage() + e.getMessage());
			reState = -400;
		}
		
		if(reState == -1 || reState == -400){
			if(mPreviousTime < 7)++mPreviousTime;
		}else{
			mPreviousTime = 0;
		}
	}

	private void handleNotify(List<OfflineMessageBean<?>> list) {
		
		byte role = CacheManager.INSTANCE.getCurrentUser().getRole();
		switch (role) {
		case Role.ADMIN:
			handleAdmin(list);
			break;

		case Role.CUSTOM:
			handleCustom(list);
			break;
			
		case Role.BARBER:
			handleBarber(list);
			break;
			
		case Role.SALON:
			handleSalon(list);
			break;
		}
		
		int notifyCount = list.size();
		boolean needSound = false;
		
		if(notifyCount > 0){
			needSound = application.getPreferenceConfig().getBoolean(MyProperties.getMyProperties().getSoundRemindKey(), true);
			boolean needNotify = application.getPreferenceConfig().getBoolean(MyProperties.getMyProperties().getReceiveNotifyKey(), true);

			if(needNotify){
				retNotifyInfo = notifySystem(notifyCount);
			}
		}
	
		if(needSound)
			soundRemind();
	}
	
	private void handleAdmin(List<OfflineMessageBean<?>> list){
		boolean newBarber = false;
		boolean newSalon = false;
		for(OfflineMessageBean<?> msg : list){
			if(msg.getType() == MessageType.ADMIN_NEW_BARBER){
				newBarber = true;
			}
			else if(msg.getType() == MessageType.ADMIN_NEW_SALON){
				newSalon = true;
			}
			if(newSalon && newBarber){
				break;
			}
				
		}
		
		if(newSalon && newBarber){
			sendMainEmptyMessage(MessageType.ADMIN_NEW_BARBER);
			sendMainEmptyMessage(MessageType.ADMIN_NEW_SALON);
			showToast(R.string.msg_string1);
		}
		else if(newSalon){
			sendMainEmptyMessage(MessageType.ADMIN_NEW_SALON);
			showToast(R.string.msg_string2);
		}
		else if(newBarber){
			sendMainEmptyMessage(MessageType.ADMIN_NEW_BARBER);
			showToast(R.string.msg_string3);
		}
	}
	
	private void handleCustom(List<OfflineMessageBean<?>> list){
		boolean newBid = false;
		boolean orderAccept = false;
		boolean orderDeny = false;
		boolean newCoupon = false;
		int msgTypeCount = 0;
		for(OfflineMessageBean<?> msg : list){
			switch (msg.getType()) {
			case MessageType.CUSTOM_NEW_BID:
				if(! newBid) msgTypeCount++;
				newBid = true;
				break;
				
			case MessageType.CUSTOM_ORDER_ACCEPT:
				if(! orderAccept) msgTypeCount++;
				orderAccept = true;
				break;
				
			case MessageType.CUSTOM_ORDER_DENY:
				if(! orderDeny) msgTypeCount++;
				orderDeny = true;
				break;
				
			case MessageType.CUSTOM_NEW_COUPON:
				if(! newCoupon) msgTypeCount++;
				newCoupon = true;
				break;
			}
		}
		
		if(msgTypeCount > 1){
			showToast(R.string.msg_string8);
		}
		
		if(newBid){
			mMyHandler.sendEmptyMessage(GET_NEW_CUSTOM_BID);
		}
		if(orderAccept){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string5);
			sendMainEmptyMessage(MessageType.CUSTOM_ORDER_ACCEPT);
		}
		if(orderDeny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string6);
			sendMainEmptyMessage(MessageType.CUSTOM_ORDER_DENY);
		}
		if(newCoupon){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string7);
			sendMainEmptyMessage(MessageType.CUSTOM_NEW_COUPON);
		}
	}
	
	private void handleBarber(List<OfflineMessageBean<?>> list){
		boolean barber_register_pass = false;
		boolean barber_register_deny = false;
		boolean barber_modify_pass = false;
		boolean barber_modify_deny = false;
		boolean barber_bid_success = false;
		boolean barber_bid_fail = false;
		boolean barber_order_accept = false;
		boolean barber_order_deny = false;
		boolean barber_order_cancel = false;
		boolean barber_order_done = false;
		boolean barber_order_share = false;
		boolean barber_salon_loss = false;
		boolean barber_new_order = false;
		int msgTypeCount = 0;
		for(OfflineMessageBean<?> msg : list){
			switch (msg.getType()) {
			case MessageType.BARBER_REGISTER_PASS:
				if(! barber_register_pass) msgTypeCount++;
				barber_register_pass = true;
				break;
				
			case MessageType.BARBER_REGISTER_DENY:
				if(! barber_register_deny) msgTypeCount++;
				barber_register_deny = true;
				break;
				
			case MessageType.BARBER_MODIFY_PASS:
				if(! barber_modify_pass) msgTypeCount++;
				barber_modify_pass = true;
				break;
				
			case MessageType.BARBER_MODIFY_DENY:
				if(! barber_modify_deny) msgTypeCount++;
				barber_modify_deny = true;
				break;
				
			case MessageType.BARBER_BID_SUCCESS:
				if(! barber_bid_success) msgTypeCount++;
				barber_bid_success = true;
				break;
				
			case MessageType.BARBER_BID_FAIL:
				if(! barber_bid_fail) msgTypeCount++;
				barber_bid_fail = true;
				break;
				
			case MessageType.BARBER_ORDER_ACCEPT:
				if(! barber_order_accept) msgTypeCount++;
				barber_order_accept = true;
				break;
				
			case MessageType.BARBER_ORDER_DENY:
				if(! barber_order_deny) msgTypeCount++;
				barber_order_deny = true;
				break;
				
			case MessageType.BARBER_ORDER_CANCEL:
				if(! barber_order_cancel) msgTypeCount++;
				barber_order_cancel = true;
				break;
				
			case MessageType.BARBER_ORDER_DONE:
				if(! barber_order_done) msgTypeCount++;
				barber_order_done = true;
				break;
				
			case MessageType.BARBER_ORDER_SHARE:
				if(! barber_order_share) msgTypeCount++;
				barber_order_share = true;
				break;
				
			case MessageType.BARBER_SALON_LOSS:
				if(! barber_salon_loss) msgTypeCount++;
				barber_salon_loss = true;
				break;
				
			case MessageType.BARBER_NEW_ORDER:
				if(! barber_new_order) msgTypeCount++;
				barber_new_order = true;
				break;
			}
		}
			
		if(msgTypeCount > 1)
			showToast(R.string.msg_string8);
		
		if(barber_register_pass){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string9);
			sendMainEmptyMessage(MessageType.BARBER_REGISTER_PASS);
		}
		if(barber_register_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string10);
			sendMainEmptyMessage(MessageType.BARBER_REGISTER_DENY);
		}
		if(barber_modify_pass){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string11);
			sendMainEmptyMessage(MessageType.BARBER_MODIFY_PASS);
		}
		if(barber_modify_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string12);
			sendMainEmptyMessage(MessageType.BARBER_MODIFY_DENY);
		}
		if(barber_bid_success){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string13);
			sendMainEmptyMessage(MessageType.BARBER_BID_SUCCESS);
		}
		if(barber_bid_fail){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string14);
			sendMainEmptyMessage(MessageType.BARBER_BID_FAIL);
		}
		if(barber_order_accept){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string15);
			sendMainEmptyMessage(MessageType.BARBER_ORDER_ACCEPT);
		}
		if(barber_order_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string16);
			sendMainEmptyMessage(MessageType.BARBER_ORDER_DENY);
		}
		if(barber_order_cancel){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string17);
			sendMainEmptyMessage(MessageType.BARBER_ORDER_CANCEL);
		}
		if(barber_order_done){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string18);
			sendMainEmptyMessage(MessageType.BARBER_ORDER_DONE);
		}
		if(barber_order_share){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string19);
			sendMainEmptyMessage(MessageType.BARBER_ORDER_SHARE);
		}
		if(barber_salon_loss){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string20);
			sendMainEmptyMessage(MessageType.BARBER_SALON_LOSS);
		}
		if(barber_new_order){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string32);
			sendMainEmptyMessage(MessageType.BARBER_NEW_ORDER);
		}
	}
	
	private void handleSalon(List<OfflineMessageBean<?>> list){
		boolean salon_register_pass = false;
		boolean salon_register_deny = false;
		boolean salon_modify_pass = false;
		boolean salon_modify_deny = false;
		boolean salon_order_accept = false;
		boolean salon_order_deny = false;
		boolean salon_order_cancel = false;
		boolean salon_order_done = false;
		boolean salon_order_share = false;
		boolean salon_barber_add = false;
		boolean salon_barber_loss = false;
		boolean salon_new_order = false;
		int msgTypeCount = 0;
		for(OfflineMessageBean<?> msg : list){
			switch (msg.getType()) {
			case MessageType.SALON_REGISTER_PASS:
				if(! salon_register_pass) msgTypeCount++;
				salon_register_pass = true;
				break;
				
			case MessageType.SALON_REGISTER_DENY:
				if(! salon_register_deny) msgTypeCount++;
				salon_register_deny = true;
				break;
				
			case MessageType.SALON_MODIFY_PASS:
				if(! salon_modify_pass) msgTypeCount++;
				salon_modify_pass = true;
				break;
				
			case MessageType.SALON_MODIFY_DENY:
				if(! salon_modify_deny) msgTypeCount++;
				salon_modify_deny = true;
				break;
				
			case MessageType.SALON_ORDER_ACCEPT:
				if(! salon_order_accept) msgTypeCount++;
				salon_order_accept = true;
				break;
				
			case MessageType.SALON_ORDER_DENY:
				if(! salon_order_deny) msgTypeCount++;
				salon_order_deny = true;
				break;
				
			case MessageType.SALON_ORDER_CANCEL:
				if(! salon_order_cancel) msgTypeCount++;
				salon_order_cancel = true;
				break;
				
			case MessageType.SALON_ORDER_DONE:
				if(! salon_order_done) msgTypeCount++;
				salon_order_done = true;
				break;
				
			case MessageType.SALON_ORDER_SHARE:
				if(! salon_order_share) msgTypeCount++;
				salon_order_share = true;
				break;
				
			case MessageType.SALON_BARBER_ADD:
				if(! salon_barber_add) msgTypeCount++;
				salon_barber_add = true;
				break;
				
			case MessageType.SALON_BARBER_LOSS:
				if(! salon_barber_loss) msgTypeCount++;
				salon_barber_loss = true;
				break;
				
			case MessageType.SALON_NEW_ORDER:
				if(! salon_new_order) msgTypeCount++;
				salon_new_order = true;
				break;
			}
		}
			
		if(msgTypeCount > 1)
			showToast(R.string.msg_string8);
		
		if(salon_register_pass){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string21);
			sendMainEmptyMessage(MessageType.SALON_REGISTER_PASS);
		}
		if(salon_register_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string22);
			sendMainEmptyMessage(MessageType.SALON_REGISTER_DENY);
		}
		if(salon_modify_pass){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string23);
			sendMainEmptyMessage(MessageType.SALON_MODIFY_PASS);
		}
		if(salon_modify_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string24);
			sendMainEmptyMessage(MessageType.SALON_MODIFY_DENY);
		}
		if(salon_order_accept){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string25);
			sendMainEmptyMessage(MessageType.SALON_ORDER_ACCEPT);
		}
		if(salon_order_deny){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string26);
			sendMainEmptyMessage(MessageType.SALON_ORDER_DENY);
		}
		if(salon_order_cancel){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string27);
			sendMainEmptyMessage(MessageType.SALON_ORDER_CANCEL);
		}
		if(salon_order_done){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string28);
			sendMainEmptyMessage(MessageType.SALON_ORDER_DONE);
		}
		if(salon_order_share){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string29);
			sendMainEmptyMessage(MessageType.SALON_ORDER_SHARE);
		}
		if(salon_barber_add){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string30);
			sendMainEmptyMessage(MessageType.SALON_BARBER_ADD);
		}
		if(salon_barber_loss){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string31);
			sendMainEmptyMessage(MessageType.SALON_BARBER_LOSS);
		}
		if(salon_new_order){
			if(msgTypeCount == 1) 
				showToast(R.string.msg_string32);
			sendMainEmptyMessage(MessageType.SALON_NEW_ORDER);
		}
	}

	private void initSharedPrefrences(int currentUserId){

		try {

			if(userId != currentUserId) {

				mSP = application.getSharedPreferences(String.valueOf(currentUserId), Activity.MODE_PRIVATE);
				if(mSP.contains("lastMsgId")){
					mStartIndex = mSP.getLong("lastMsgId", 0);
				}else{

					SharedPreferences.Editor mSPEditor = mSP.edit();
					mSPEditor.putLong("lastMsgId", 0);
					mSPEditor.apply();
					mStartIndex = 0;
				}
			}

		} catch (Exception e) {
			CHLogger.e(this, e.getMessage());
			mStartIndex = 0;
		}finally{
			userId = currentUserId;
		}
	}

	private void refreshStartIndex(int maxIndex) {

		if (maxIndex > mStartIndex) {
			try {

				SharedPreferences.Editor mSPEditor = mSP.edit();
				mSPEditor.putLong("lastMsgId", maxIndex).apply();
			} catch (Exception e) {
				CHLogger.e(this, e.getMessage());
			}finally{
				mStartIndex = maxIndex;
			}
		}
	}

	private void soundRemind() {
		Uri uri = RingtoneManager.getActualDefaultRingtoneUri(application, RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(application, uri);

		if(r != null){
			r.play();
		}
	}

	private Bundle notifySystem(int msgCount) {
		Bundle ret = new Bundle();
		ret.putInt("title", R.string.app_name);
		ret.putString("text", application.getResources().getString(R.string.there_is_new_msg, msgCount));
		ret.putInt("actionRes", R.string.MessageServiceNotify);
		
		return ret;
	}

	private void sendMainMessage(int action, int count){
		if(mMainHandler != null)
			mMainHandler.sendMessage(mMainHandler.obtainMessage(action, count));
	}
	
	private void sendMainEmptyMessage(int action){
		if(mMainHandler != null)
			mMainHandler.sendEmptyMessage(action);
	}

	private void saveOrUpateObj(Object obj , String where){
		try {
			List<?> objs= getSqlitdb().query(obj.getClass(), true, where, null, null, null, "1");

			if(objs == null || objs.isEmpty()){
				getSqlitdb().insert(obj);
			}else{
				getSqlitdb().update(obj);
			}
		} catch (Exception e) {
			CHLogger.e(this, e.getMessage());
		}

	}
	
	@Override
	public void doExit() {
		mMainHandler = null;
		mMyHandler = null;
//		mOtherHandler = null;
		mSP = null;
		instance = null;
	}
	
	private void showToast(int res){
		try {
			if(isAppForegroud()){
				Toast.makeText(CHApplication.getApplication(), res, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
