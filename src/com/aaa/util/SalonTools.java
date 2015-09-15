package com.aaa.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.JetPlayer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.changhong.chcare.core.webapi.bean.SalonUser;

import com.aaa.activity.login.WelcomeActivity;
import com.aaa.db.Product;
import com.changhong.CHActivity;
import com.changhong.CHApplication;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.bitmap.CHBitmapCallBackHanlder;
import com.changhong.util.bitmap.CHDownloadBitmapHandler;
import com.changhong.util.config.CHIConfig;
import com.changhong.util.db.bean.CacheManager;
import com.google.gson.JsonElement;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

public class SalonTools {
	
	private static final String TAG = "SalonTools";
	private static final String PHOTO_DIVIDER = "\\|";
	private static final String DIVIDER_P1 = "&&";
	private static final String DIVIDER_P2 = "\\|";
	
	private static final String PHOTO_SPLIT = "|";
	private static final String SPLIT_P1 = "&&";
	private static final String SPLIT_P2 = "|";
	
	public static void setSelection(int position, ArrayList<ImageView> list) {
		for (int i = 0; i < list.size(); i++) {
			if (i == position) {
				list.get(i).setImageResource(R.drawable.online);
			} else {
				list.get(i).setImageResource(R.drawable.unonline);
			}
		}
	}
	
	public static boolean editNotNull(EditText edit){
		if(edit.getText() != null && edit.getText().toString() != null
				&& ! edit.getText().toString().trim().equals("")){
			return true;
		}
		return false;
	}
	
	public static void healthEffect(boolean hasHealth, ImageView view){
		view.setAlpha(hasHealth ? 1f : 0.5f);
		view.setClickable(hasHealth);
	}
	
	public synchronized static CHBitmapCacheWork getImageFetcher(Context contxt, CHApplication app, boolean isRound, int defaultImg){
		CHBitmapCacheWork imageFetcher = new CHBitmapCacheWork(contxt);
		
		CHBitmapCallBackHanlder taBitmapCallBackHanlder = new CHBitmapCallBackHanlder();
		if(defaultImg > 0){
			taBitmapCallBackHanlder.setLoadingImage(contxt, defaultImg);
		}
		taBitmapCallBackHanlder.setCircleParams(isRound);
		
		Bitmap loading = taBitmapCallBackHanlder.getmLoadingBitmap();
		if(loading != null){
			int width = taBitmapCallBackHanlder.getmLoadingBitmap().getWidth();
			int height = taBitmapCallBackHanlder.getmLoadingBitmap().getHeight();
			CHDownloadBitmapHandler downloadBitmapFetcher = new CHDownloadBitmapHandler(
					contxt, width, height);
			
			imageFetcher.setProcessDataHandler(downloadBitmapFetcher);
		}
		
		imageFetcher.setCallBackHandler(taBitmapCallBackHanlder);
		imageFetcher.setFileCache(app.getFileCache());		
		return imageFetcher;
	}
	
	public synchronized static void saveDistrict(CHActivity activity, String district){
		CHIConfig config = activity.getCHApplication().getPreferenceConfig();
		config.setString(WelcomeActivity.DISTRICT_SEARCH, district);
	}
	
	public synchronized static String getDistrict(CHActivity activity){
		CHIConfig config = activity.getCHApplication().getPreferenceConfig();
		return config.getString(WelcomeActivity.DISTRICT_SEARCH, "");
	}
	
	public synchronized static String getArea(CHActivity activity){
		String ret = "";
		CHIConfig config = activity.getCHApplication().getPreferenceConfig();
		String district = config.getString(WelcomeActivity.DISTRICT_SEARCH, "");
		
		if(district.equals("")){
			config.setString(WelcomeActivity.DISTRICT_SEARCH, activity.getString(R.string.default_district));
			ret = activity.getString(R.string.default_area);
		}else{
			String[] ss = district.split(DistrictActivity.DIVIDER);
			if(ss.length == 3){
				if(ss[2].equals(activity.getString(R.string.all_area))){
					ret = ss[1];
				}else{
					ret = ss[2];
				}
			}
			
			if(ss.length == 2){
				ret = ss[1];
			}
		}
		
		return ret;
	}
	
	public synchronized static String getSearchDistrict(CHActivity activity){
		CHIConfig config = activity.getCHApplication().getPreferenceConfig();
		String district = config.getString(WelcomeActivity.DISTRICT_SEARCH, "");
		String[] ss = district.split(DistrictActivity.DIVIDER);
		if(ss.length == 3 && ss[2].equals(activity.getString(R.string.all_area))){
			district = ss[0] + DistrictActivity.DIVIDER + ss[1] + DistrictActivity.DIVIDER;
		}
		return district;
	}
	
	public static String getSearchAreaString(CHActivity activity, String area){
		String ret = area;
		String[] ss = area.split(DistrictActivity.DIVIDER);
		if(ss.length == 3 && ss[2].equals(activity.getString(R.string.all_area))){
			ret = ss[0] + DistrictActivity.DIVIDER + ss[1] + DistrictActivity.DIVIDER;
		}
		return ret;
	}
	
	public static ArrayList<String> splitPhoto(JsonElement je){
		ArrayList<String> photos = new ArrayList<String>();
		if(je != null){
			String[] ss = je.getAsString().split(PHOTO_DIVIDER);
			for(int i = 0; i < ss.length; i++){
				photos.add(ss[i]);
			}
		}
		return photos;
	}
	
	public static ArrayList<String> splitPhoto(String s){
		ArrayList<String> photos = new ArrayList<String>();
		if(s != null){
			String[] ss = s.split(PHOTO_DIVIDER);
			for(int i = 0; i < ss.length; i++){
				photos.add(ss[i]);
			}
		}
		return photos;
	}
	
	public static ArrayList<Product> splitProduct(JsonElement je){
		ArrayList<Product> products = new ArrayList<Product>();
		if(je != null){
			String[] ss = je.getAsString().split(Product.DIVIDER1);
			for(int i = 0; i < ss.length; i++){
				String[] ss2 = ss[i].split(Product.DIVIDER2);
				if(ss2.length == 4){
					Product p = new Product(ss2[0], ss2[1], ss2[2], ss2[3]);
					products.add(p);
				}
			}
		}
		return products;
	}
	
	public static String getText(View v){
		String result = "";
		if(v != null){
			if(EditText.class.isInstance(v)){
				result = ((EditText)v).getText().toString().trim(); 
			}
			else if(TextView.class.isInstance(v)){
				result = ((TextView)v).getText().toString().trim();
			}
		}
		return result;
	}
	
//	public static int[] splitPrice(String s){
//		int[] prices = new int[9];
//		if(s != null){
//			String[] ss = s.split(PHOTO_DIVIDER);
//			if(ss.length == 9){
//				for(int i = 0; i < ss.length; i++){
//					prices[i] = Integer.valueOf(ss[i]);
//				}
//			}
//		}
//		return prices;
//	}
	
	public static int[] splitPrice(JsonElement je){
		int[] prices = new int[10];
		if(je != null){
			String[] ss = je.getAsString().split(PHOTO_DIVIDER);
			if(ss.length >= 9){
				for(int i = 0; i < ss.length; i++){
					prices[i] = Integer.valueOf(ss[i]);
				}
			}
		}
		CHLogger.d(TAG, "splitPrice " + prices.toString());
		return prices;
	}
	
//	public static int[] splitPrice(JsonElement je){
//		int[] prices = new int[9];
//		if(je != null){
//			String[] ss = je.getAsString().split(PHOTO_DIVIDER);
//			if(ss.length == 9){
//				for(int i = 0; i < ss.length; i++){
//					prices[i] = Integer.valueOf(ss[i]);
//				}
//			}
//		}
//		CHLogger.d(TAG, "splitPrice " + prices);
//		return prices;
//	}
	
	public static String composePhotos(ArrayList<String> photos){
		String s = "";
		if(photos != null){
			for(int i = 0; i < photos.size(); i++){
				if(i == 0){
					s += photos.get(i);
				}else{
					s += PHOTO_SPLIT + photos.get(i);
				}
			}
		}
		return s;
	}

	public static String composeProduct(ArrayList<Product> products) {
		String s = "";
		boolean first = true;
		if(products != null && products.size() > 0){
			for(int i = 0; i < products.size(); i++){
				if(products.get(i) != null){
					if(first){
						s += products.get(i).band + SPLIT_P2 + products.get(i).usage + SPLIT_P2 + products.get(i).price
								+ SPLIT_P2 + products.get(i).origin;
						first = false;
					}else{
						s += SPLIT_P1 + products.get(i).band + SPLIT_P2 + products.get(i).usage + SPLIT_P2 + products.get(i).price
								+ SPLIT_P2 + products.get(i).origin;
					}
				}
			}
		}
		return s;
	}
	
	public static String composePrice(int[] prices){
		String s = "";
		if(prices != null){
			for(int i = 0; i < prices.length; i++){
				if(i == 0){
					s += String.valueOf(prices[i]);
				}else{
					s += PHOTO_SPLIT + prices[i];
				}
			}
		}
		return s;
	}
	
	public static String composeExtraService(Context c, int extraService){
		String result = "";
		String s1 = c.getString(R.string.service5);
		String s2 = c.getString(R.string.service6);
		String s4 = c.getString(R.string.service7);
		String divide = " / ";
		switch (extraService) {
		case 0:
			result = c.getString(R.string.none);
			break;
			
		case 1:
			result = s1;
			break;

		case 2:
			result = s2;
			break;
			
		case 4:
			result = s4;
			break;
			
		case 1|2:
			result = s1 + divide + s2;
			break;
			
		case 1|4:
			result = s1 + divide + s4;
			break;
			
		case 2|4:
			break;
			
		case 1|2|4:
			result = s1 + divide + s2 + divide + s4;
			break;
		}
		return result;
	}
	
	public static String getDisplayArea(Context c, String area){
		if(area != null){
			String[] ss = area.split(DistrictActivity.DIVIDER);
			if(ss.length == 2){
				area += c.getString(R.string.all_area);
			}
		}
		return area;
	}
	
	public static String getDiaplayBarberArea(Context c, String area){
		String ret = getDisplayArea(c, area);
		if(ret != null){
			String[] ss = ret.split(DistrictActivity.DIVIDER);
			if(ss.length == 3){
				ret = ss[1] + DistrictActivity.DIVIDER + ss[2];
			}
		}
		return ret;
	}
	
	public static String getUploadArea(Context c, String area){
		if(area != null){
			String[] ss = area.split(DistrictActivity.DIVIDER);
			if(ss.length == 3 && ss[2].equals(c.getString(R.string.all_area))){
				area = ss[0] + DistrictActivity.DIVIDER + ss[1] + DistrictActivity.DIVIDER;
			}
		}
		return area;
	}
	
	public static String getName(){
		SalonUser user = CacheManager.INSTANCE.getCurrentUser();
		String name = "";
		if(user.getNick() != null && ! user.getNick().equals("")){
			name = user.getNick();
		}
		else if(user.getPerson_Name() != null && ! user.getPerson_Name().equals("")){
			name = user.getPerson_Name();
		}
		else if(user.getName() != null && ! user.getName().equals("")){
			name = user.getName();
		}
		return name;
	}
	
	public static String getName(SalonUser user){
		String name = "";
		if(user == null) return name;
		
		if(user.getNick() != null && ! user.getNick().equals("")){
			name = user.getNick();
		}
		else if(user.getPerson_Name() != null && ! user.getPerson_Name().equals("")){
			name = user.getPerson_Name();
		}
		else if(user.getName() != null && ! user.getName().equals("")){
			name = user.getName();
		}
		return name;
	}
	
	public static String getRatioString(Context c, byte ratio) {
		String ret = "";
		switch (ratio) {
		case 3:
			ret = c.getString(R.string.salon_reString16);
			break;
			
		case 4:
			ret = c.getString(R.string.salon_reString17);
			break;
			
		case 5:
			ret = c.getString(R.string.salon_reString18);
			break;
		}
		return ret;
	}
	
	public static int getProductHeight(int listSize){
		return 30 * (listSize + 2);
	}
	
	public static byte getActualRatio(byte salonRatio, byte customRatio){
		return customRatio;
	}
	
	public static void scrollToHead(View view){
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}
}
