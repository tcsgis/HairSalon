package com.changhong.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.aaa.activity.main.MainActivity;
import com.changhong.CHApplication;
import com.changhong.activity.util.PollingUtils;
import com.changhong.service.task.AlarmOnObserver;
import com.changhong.service.task.receiver.AlarmMessageReceiver;
import com.changhong.util.CHLogger;
import com.llw.salon.R;

public class PollingService extends Service {

	private static final int DELAY_MILLES = 10 * 1000;
	
	private Notification mNotification;
	private NotificationManager mManager;
	private Map<Integer, AlarmOnObserver> tasks = new ConcurrentHashMap<Integer, AlarmOnObserver>();
	private ExecutorService pool;
	private HandlerThread mHandlerThread;
	private Handler mMyHandler;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
		pool = Executors.newSingleThreadExecutor();
	}

	@Override
	public void onStart(Intent intent, int startId) {
//		if(intent != null && intent.getAction().equals(getResources().getString(R.string.AlarmAnniServiceNotify))){
//			System.out.println("Polling..." + intent.getAction());
//			try {
//				pool.execute(new PollingThread());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else{
//			
//			stopSelf();
//		}
		if (intent != null
				&& intent.getAction().equals(getResources().getString(
								R.string.AlarmAnniServiceNotify))) {
			initNotifiManager();
		} else {

			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
		try {
			pool.shutdown();
			notifyObserverExit();
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	//初始化通知栏配置
	private void initNotifiManager() {
		
		if(mHandlerThread == null){
			tasks.put(R.string.MessageServiceNotify, AlarmMessageReceiver.getInstance());
			
			mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int icon = R.drawable.logo_salon;
			mNotification = new Notification();
			mNotification.icon = icon;
			mNotification.tickerText = "梳妆台";
			mNotification.defaults |= Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			
			mHandlerThread = new HandlerThread("PollingService");
			mHandlerThread.start();
			mMyHandler = new Handler(mHandlerThread.getLooper());
			CHLogger.d(this, "mMyHandler.postDelayed(mTasks, 1000)");
			mMyHandler.postDelayed(mTasks, 1000);
		}
	}

	//弹出Notification
	private void showNotification(Bundle bundle) {
		mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		System.out.println("showNotification==>" + bundle);
		
		if(bundle != null){
			
			int netstate = bundle.getInt("state");
			
			if(netstate == -15){
				PollingUtils.stopPollingService(this, PollingService.class, getString(R.string.AlarmAnniServiceNotify));
				return;
			}
			
			int titleRes = bundle.getInt("title");
			String text = bundle.getString("text");
			int actionRes = bundle.getInt("actionRes");
			
			Intent intent = new Intent(this, MainActivity.class);
			intent.setAction(getString(actionRes));
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			mNotification.setLatestEventInfo(this,getResources().getString(titleRes), text, pendingIntent);
			mManager.notify(actionRes, mNotification);
		}
		
		/*NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		Builder builder = new Builder(this);
		Intent intent = null;
		PendingIntent pIntent = null;
		
		String title = getString(R.string.app_name);
		String text = getString(R.string.there_is_anni, count);
		
		String actiontext = getString(R.string.AlarmAnniServiceNotify);
		
		
		intent = new Intent(this, MainActivity.class);
		intent.setAction(actiontext);
		pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder.setContentIntent(pIntent)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(title)
		.setContentText(text)
		.setAutoCancel(true);
		notification = builder.build();
		
		nm.notify(R.string.AlarmAnniServiceNotify, notification);*/
	}
	
	private void notifyObserver()
	{
		
		if(CHApplication.getApplication() == null) return;
		
		Date date = Calendar.getInstance().getTime();
		for (AlarmOnObserver observer : tasks.values())
		{
			if (observer != null)
			{
				Bundle ret = observer.onTimeOut(date);
				showNotification(ret);
			}
		}
	}
	
	
	private void notifyObserverExit()
	{
		for (AlarmOnObserver observer : tasks.values())
		{
			if (observer != null)
			{
				observer.doExit();
			}
		}
	}
	
	public void putTaskInfo(int actionRes, AlarmOnObserver observer)
	{
		if(!tasks.containsKey(actionRes)){
			tasks.put(actionRes, observer);
		}
		/*AlarmAnniReceiver task = new AlarmAnniReceiver();
		Bundle ret = task.onTimeOut(Calendar.getInstance().getTime());
		showNotification(ret);*/
	}
	
	/**
	 * Polling thread
	 * 模拟向Server轮询的异步线程
	 * @Author Ryan
	 * @Create 2013-7-13 上午10:18:34
	 */
//	class PollingThread extends Thread {
//		@Override
//		public void run() {
//			
//			System.out.println("Polling...dostar");
//			notifyObserver();
//			
//			System.out.println("Polling...doend");
//		}
//	}
	
	private Runnable mTasks = new Runnable()
	{

		public void run()
		{
			notifyObserver();
			mMyHandler.postDelayed(mTasks, DELAY_MILLES);
		}
	};
}
