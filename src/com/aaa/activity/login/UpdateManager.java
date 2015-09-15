package com.aaa.activity.login;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import cn.changhong.chcare.core.webapi.bean.BannerPic;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.VerInfo;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.util.ConnFactory;

import com.changhong.activity.widget.AppMainDialog;
import com.changhong.common.AsyncTask;
import com.changhong.util.CHLogger;
import com.llw.salon.R;

public class UpdateManager {
	private Context mContext;
	private boolean mIsLoading = false;
	private String mApkUrl;/*"http://182.92.165.152/appdownload/CHFamilyNew.apk"*/
	private int localVersion = -1;
	private Handler handler = new Handler();
	private final static int NETWORK_ERROR = 30001;
	private final static String TAG = "UpdateManager";
	private ProgressDialog mProgressDialog; // 进度条对话框
	private String mDataPath;// apk保存路径
	
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);

	public UpdateManager(Context context) {
		super();
		this.mContext = context;
	}

	public void showUpdateDialog(VerInfo appVersion, boolean fromMainView){
		
		if(appVersion != null){
			localVersion = getLocalVersion();
			mApkUrl = appVersion.getFile();
			if(appVersion.getVerCode() > localVersion){
				Message message = Message.obtain();
				message.what = 2;
				message.obj = appVersion.getVerName();
				mhandler.sendMessage(message);
//				updateVerifyDialog(R.string.has_version_update,appVersion.getDevVer());
			}
			else if(! fromMainView){
				mhandler.sendEmptyMessage(1);
//				popMsg(mContext.getResources().getString(R.string.no_version_update));
			}
		}
		
	}
	
	public void doCheckUpdate() {
		if (isConnected()) {
			checkUpdate();
		} else {
			popMsg(mContext.getResources().getString(R.string.err_net_connected));
		}
	}

	private void checkUpdate() {
		
		localVersion = getLocalVersion();
		accountService.getBanners(new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response, ChCareWepApiServiceType servieType) {
				try {
					if(response.getState() >= 0 && response.getData() != null){
						ArrayList<BannerPic> banners = (ArrayList<BannerPic>) response.getData();
						VerInfo ver = (VerInfo) banners.get(2);
						showUpdateDialog(ver, false);
					}else{
						mhandler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}


	private void updateVerifyDialog(String serverVersion) {
		final AppMainDialog dialog = new AppMainDialog(mContext, R.style.appdialog);
		dialog.withTitle(R.string.update_version)
				.withMessage(mContext.getString(R.string.has_version_update, serverVersion))
				.setOKClick(R.string.ok_queren, new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (isConnected()) {
							downLoadApk();
						} else {
							popMsg(mContext.getResources().getString(R.string.err_net_connected));
						}
						dialog.dismiss();
					}
				}).setCancelClick(R.string.cancel_quxiao).show();
	}

	//下载apk
	public void downLoadApk() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			mProgressDialog = new ProgressDialog(mContext);
		} else {
			mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
		}
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading_update));
		mProgressDialog.show();
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
					CHLogger.i(TAG,"KEYCODE_BACK");
					remindPopDialog();
				}
				return false;
			}
		});

		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(mApkUrl, mProgressDialog);
					Thread.sleep(3000);
					if (null != file) {
						mProgressDialog.dismiss(); // 结束掉进度条对话框
						installApk(file);
					}
				} catch (Exception e) {//下载新版本失败
					popMsg(mContext.getResources().getString(R.string.download_apk_failed));
					e.printStackTrace();
				}
			}
		});
	}

	//安装apk
	protected void installApk(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//防止安装中退出
		mContext.startActivity(intent);
	}

	// 判断是否存在SD卡
	private boolean checkSoftStage() {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public File getFileFromServer(String path, final ProgressDialog progressDialog) {
		InputStream inputstream = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		try {
			if (checkSoftStage()) {
				mDataPath = Environment.getExternalStorageDirectory().getPath();
				CHLogger.i(TAG, "dataPath(有卡):" + mDataPath);
			} else {
				mDataPath = mContext.getFilesDir().getPath();
				CHLogger.i(TAG, "dataPath(无卡):" + mDataPath);
			}

			URL url = new URL(path);
			mIsLoading = true;
			HttpURLConnection conn = (HttpURLConnection) ConnFactory.getInstance().open(url);
			conn.setConnectTimeout(5000);
			progressDialog.setMax(conn.getContentLength());// 获取到文件的大小
			inputstream = conn.getInputStream();

			File file = new File(mDataPath, "updata.apk");
			if (file.exists()) {
				file.delete();
			}

			CHLogger.i(TAG, "file1.getPath():" + file.getPath());
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bis = new BufferedInputStream(inputstream);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			int i = 16;
			while (((len = bis.read(buffer)) != -1) && mIsLoading) {
//				CHLogger.i(TAG, "is downloading.....");
				fos.write(buffer, 0, len);
				total += len;
				if (--i == 0) {
					i = 16;
					progressDialog.setProgress(total);// 获取当前下载量
				}
			}
			fos.close();
			bos.close();
			bis.close();
			inputstream.close();
			if (!mIsLoading) {
				file.delete();
				return null;
			} else {
				mIsLoading = false;
			}
			return file;
		} catch (Exception e) {
			CHLogger.e(TAG, "e.getMessage()=[" + e.getMessage() + "]");
			e.printStackTrace();
		}
		mIsLoading = false;
		CHLogger.e(TAG,"网络异常处理");
		try {
			if (fos != null) {
				fos.close();
			}
			if (inputstream != null) {
				inputstream.close();
			}
			if(bis!= null){
				bis.close();
			}

			handler.post(new Runnable() {
				public void run() {
					progressDialog.dismiss();
					Message message = Message.obtain();
					message.what = NETWORK_ERROR;
					mhandler.sendMessage(message);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			progressDialog.dismiss();
			Message message = Message.obtain();
			message.what = NETWORK_ERROR;
			mhandler.sendMessage(message);
		}
		return null;
	}

	Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NETWORK_ERROR:
				errorPopDialog();
				break;
			case 1:
				popMsg(mContext.getResources().getString(R.string.no_version_update));
				break;
			case 2:
				String devVer = (String)msg.obj;
				updateVerifyDialog(devVer);
				break;
			default:
				break;
			}
		}
	};

	private void remindPopDialog() {
		final AppMainDialog dialog = new AppMainDialog(mContext, R.style.appdialog);
		dialog.withTitle(R.string.prompt).withMessage(R.string.sure_to_cancel)
				.setOKClick(R.string.ok_queren, new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mIsLoading = false;
						mProgressDialog.cancel();
						dialog.dismiss();
					}
				})
				.setCancelClick(R.string.cancel_quxiao, new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								mIsLoading = true;
								dialog.dismiss();
							}
						}).show();
	}

	private void errorPopDialog() {
		popMsg(mContext.getResources().getString(R.string.network_problem_download_apk_failed));

		final AppMainDialog dialog = new AppMainDialog(mContext, R.style.appdialog);
		dialog.withTitle(R.string.prompt).withMessage(R.string.load_failed)
				.setOKClick(R.string.ok_queren, new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						downLoadApk();
						dialog.dismiss();
					}
				}).setCancelClick(R.string.cancel_quxiao).show();
	}

	private void popMsg(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	// 返回当前版本号
	private int getLocalVersion() {
		try {
			return mContext
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(
							mContext.getApplicationContext().getPackageName(),
							0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	//判断网络是否连接
	private boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			boolean available = networkInfo.isAvailable();
			if (available) {
				CHLogger.i(TAG, "当前的网络连接可用");
				return true;
			} else {
				CHLogger.i(TAG, "当前的网络连接不可用");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
