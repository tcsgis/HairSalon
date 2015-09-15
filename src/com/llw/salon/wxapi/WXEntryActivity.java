package com.llw.salon.wxapi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.aaa.activity.login.WelcomeActivity;
import com.changhong.util.CHLogger;
import com.llw.salon.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	//00b98bc3afeb433aeeda2447ee3ceb3f 正式签名
	//7605ad692a3a85bce4fa79814b8a1a32    测试签名
	public static final String APP_ID = "wx197c2a487bd8e467";
	public static final String ACTION_INVITE_WX = "WXEntryActivity_send_by_wx";
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	private static ArrayList<onShareListener> listeners;
			
	private IWXAPI api;

	public interface onShareListener{
		public void onSuccess(String transaction);
	}
	
	public static void registerListener(onShareListener l){
		if(listeners == null){
			listeners = new ArrayList<onShareListener>();
		}
		
		if(l != null && ! listeners.contains(l)){
			listeners.add(l);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			api = WXAPIFactory.createWXAPI(this, APP_ID, true);
			api.handleIntent(getIntent(), this);
	//		Intent getIntent = getIntent();
	//		String timeline = getIntent.getStringExtra("timelineShare");
	//		String address = getIntent.getStringExtra("address");
	//		if("timelineShare".equals(timeline)){
	//			wechatShare(1,address);
	//			finish();
	//		}
		} catch (Exception ignored){}
		finish();
	}

	@Override
	public void onReq(BaseReq req) {
		CHLogger.d(this, "onReq");
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
//			Toast.makeText(this, "发送到朋友圈", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		String result = null;
		int result_stringID = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "send success";
			result_stringID = R.string.send_success;
			notifyObserver(resp.transaction);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "send cancelled";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "send denied";
			result_stringID = R.string.send_failed;
			break;
		default:
			result = "send unkown error";
			break;
		}
		if (result_stringID != 0) {
//			Toast.makeText(this, result_stringID, Toast.LENGTH_LONG).show();
		}
		overridePendingTransition(R.anim.change_in, R.anim.change_out);
		CHLogger.d(this, result);
		finish();
	}

/*	private void wechatShare(int flag, String location) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "dwz.cn/Arpea";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = getResources().getString(R.string.little_life);
		if (location != null)
			msg.description = getResources().getString(R.string.about_us_message_one)
					+ location;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		msg.setThumbImage(thumb);
		thumb.recycle();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = (flag == 0) ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}*/

	private void goToShowMsg(ShowMessageFromWX.Req req) {
		WXMediaMessage wxMsg = req.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;


		//NOTE: 这样Action消失了, 原因不明
		Intent intent = new Intent(WXEntryActivity.this, WelcomeActivity.class);
		intent.setAction(ACTION_INVITE_WX);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}
	
	protected void notifyObserver(String transaction) {
		if(listeners != null){
			for(onShareListener l : listeners){
				l.onSuccess(transaction);
			}
		}
	}
}
