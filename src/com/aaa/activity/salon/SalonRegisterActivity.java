package com.aaa.activity.salon;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.bean.SalonUser;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider;
import cn.changhong.chcare.core.webapi.server.ChCareWepApiServiceType;
import cn.changhong.chcare.core.webapi.server.ISalonAccountService;
import cn.changhong.chcare.core.webapi.server.ISalonFileService;
import cn.changhong.chcare.core.webapi.server.ISalonSalonService;
import cn.changhong.chcare.core.webapi.server.CHCareWebApiProvider.WebApiServerType;

import com.aaa.activity.main.LlwTitleActivity;
import com.aaa.db.Product;
import com.aaa.util.DMUtil;
import com.aaa.util.PhotoType;
import com.aaa.util.SalonTools;
import com.aaa.util.Status;
import com.changhong.activity.util.PictureUtil;
import com.changhong.activity.widget.AppMainDialog;
import com.changhong.activity.widget.HorizontalListView;
import com.changhong.activity.widget.PhotoSelectPopupView;
import com.changhong.annotation.CHInjectView;
import com.changhong.util.CHLogger;
import com.changhong.util.bitmap.CHBitmapCacheWork;
import com.changhong.util.db.bean.CacheManager;
import com.llw.salon.R;
import com.mrwujay.cascade.activity.DistrictActivity;

public class SalonRegisterActivity extends LlwTitleActivity implements OnClickListener{
	
	@CHInjectView(id = R.id.name)
	private EditText name;
	@CHInjectView(id = R.id.contact)
	private EditText contact;
	@CHInjectView(id = R.id.phone)
	private EditText phone;
	@CHInjectView(id = R.id.district)
	private Button district;
	@CHInjectView(id = R.id.address)
	private EditText address;
	@CHInjectView(id = R.id.mianji)
	private EditText mianji;
	@CHInjectView(id = R.id.jiantouwei)
	private EditText jiantouwei;
	@CHInjectView(id = R.id.xitouwei)
	private EditText xitouwei;
	@CHInjectView(id = R.id.recommend)
	private EditText recommend;
	
	@CHInjectView(id = R.id.service5)
	private LinearLayout service5;
	@CHInjectView(id = R.id.service6)
	private LinearLayout service6;
	@CHInjectView(id = R.id.service7)
	private LinearLayout service7;
	@CHInjectView(id = R.id.service5_point)
	private ImageView service5_point;
	@CHInjectView(id = R.id.service6_point)
	private ImageView service6_point;
	@CHInjectView(id = R.id.service7_point)
	private ImageView service7_point;
	
	@CHInjectView(id = R.id.barber_num)
	private TextView barber_num;
	@CHInjectView(id = R.id.salon_re_title)
	private TextView salon_re_title;
	@CHInjectView(id = R.id.modify_barber)
	private Button modify_barber;
	@CHInjectView(id = R.id.done)
	private Button done;
	
	@CHInjectView(id = R.id.fengcheng)
	private LinearLayout fengcheng;
	@CHInjectView(id = R.id.dengji)
	private LinearLayout dengji;
	@CHInjectView(id = R.id.fengcheng37_point)
	private ImageView fengcheng37_point;
	@CHInjectView(id = R.id.fengcheng46_point)
	private ImageView fengcheng46_point;
	@CHInjectView(id = R.id.fengcheng55_point)
	private ImageView fengcheng55_point;
	@CHInjectView(id = R.id.yes_point)
	private ImageView yes_point;
	@CHInjectView(id = R.id.no_point)
	private ImageView no_point;
	@CHInjectView(id = R.id.yi_point)
	private ImageView yi_point;
	@CHInjectView(id = R.id.er_point)
	private ImageView er_point;
	@CHInjectView(id = R.id.san_point)
	private ImageView san_point;
	@CHInjectView(id = R.id.si_point)
	private ImageView si_point;
	@CHInjectView(id = R.id.wu_point)
	private ImageView wu_point;
	
	@CHInjectView(id = R.id.photo_list)
	private HorizontalListView photo_list;
	@CHInjectView(id = R.id.product_list)
	private ListView product_list;
	
	private final int PHOTO_MAX = 5;
	private final int PRODUCT_MAX = 20 + 1;
	
	private final int EDIT_BARBER = 11220;
	private final int SELECT_DISTRICT = 11221;
	private final int SERVICE5 = 1;
	private final int SERVICE6 = 2;
	private final int SERVICE7 = 4;
	
	private int extraService = 0;
	private boolean service5Selected = false;
	private boolean service6Selected = false;
	private boolean service7Selected = false;
	private boolean acceptBarber = true;
	private byte fengchengSelected = 3;//37分=3,46分=4...
	private byte dengjiSelected = 1;
	
	private ArrayList<Product> products = new ArrayList<Product>();
	private ArrayList<ImageView> acceptList  = new ArrayList<ImageView>();
	private ArrayList<ImageView> fenchengList  = new ArrayList<ImageView>();
	private ArrayList<ImageView> dengjiList  = new ArrayList<ImageView>();
	private PhotoSelectPopupView mPopupAltView;
	private PhotoAdapter photoAdapter;
	private ProductAdapter productAdapter;
	private Uri mPhotoUri;
	private CHBitmapCacheWork imgFetcher;
	private static int barberCount = 0;
	
	private ISalonFileService fileService = (ISalonFileService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_FILE_SERVER);
	private ISalonAccountService accountService = (ISalonAccountService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_ACCOUNT_SERVER);
	private ISalonSalonService salonService = (ISalonSalonService) CHCareWebApiProvider.Self
			.defaultInstance().getDefaultWebApiService(
					WebApiServerType.SALON_SALON_SERVER);
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		setTitile(R.string.salon_reString1);
		imgFetcher = SalonTools.getImageFetcher(this, getCHApplication(), false, 0);
		
		service5.setOnClickListener(this);
		service6.setOnClickListener(this);
		service7.setOnClickListener(this);
		modify_barber.setOnClickListener(this);
		done.setOnClickListener(this);
		district.setOnClickListener(this);
		
		acceptList.add(yes_point);
		acceptList.add(no_point);
		
		fenchengList.add(fengcheng37_point);
		fenchengList.add(fengcheng46_point);
		fenchengList.add(fengcheng55_point);
		
		dengjiList.add(yi_point);
		dengjiList.add(er_point);
		dengjiList.add(san_point);
		dengjiList.add(si_point);
		dengjiList.add(wu_point);
		
		findViewById(R.id.yes).setOnClickListener(clickSelect);
		findViewById(R.id.no).setOnClickListener(clickSelect);
		findViewById(R.id.fengcheng37).setOnClickListener(clickSelect);
		findViewById(R.id.fengcheng46).setOnClickListener(clickSelect);
		findViewById(R.id.fengcheng55).setOnClickListener(clickSelect);
		findViewById(R.id.yi).setOnClickListener(clickSelect);
		findViewById(R.id.er).setOnClickListener(clickSelect);
		findViewById(R.id.san).setOnClickListener(clickSelect);
		findViewById(R.id.si).setOnClickListener(clickSelect);
		findViewById(R.id.wu).setOnClickListener(clickSelect);
		
		mPopupAltView = new PhotoSelectPopupView(this);
		
		initView();
		
		showWaitDialog();
		salonService.getMyBarberCount(CacheManager.INSTANCE.getCurrentUser().getId(),
				new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response != null && response.getState() >= 0){
					if(response.getData() != null){
						barberCount = (Integer) response.getData();
						initView();
					}
				}
				hideAllDialog();
				return null;
			}
		});
	}

	private void initView() {
		SalonUser user = CacheManager.INSTANCE.getCurrentUser();
		//text
		if(user.getStatus() == Status.NORMAL){
			ArrayList<EditText> notModify = new ArrayList<EditText>();
			notModify.add(name);//llwtest
			notModify.add(address);
			notModify.add(mianji);
			notModify.add(jiantouwei);
			notModify.add(xitouwei);
			
			salon_re_title.setText(R.string.salon_reString40);
			
			int color = getResources().getColor(R.color.gray_txt);
			for(EditText edit : notModify){
				edit.setEnabled(false);
				edit.setTextColor(color);
			}
			district.setEnabled(false);
			district.setTextColor(color);
		}

		recommend.setText(user.getDesc());
		name.setText(user.getNick());
		contact.setText(user.getPerson_Name());
		phone.setText(user.getTel());
		district.setText(SalonTools.getDisplayArea(this, user.getAreas()));
		address.setText(user.getAddr());
		mianji.setText(user.getSize() == 0 ? null : String.valueOf(user.getSize()));
		jiantouwei.setText(user.getHairCount() == 0 ? null : String.valueOf(user.getHairCount()));
		xitouwei.setText(user.getWashCount() == 0 ? null : String.valueOf(user.getWashCount()));
		
		//extra service
		setExtraService(user.getAddinServices());
		
		//photo
		photoAdapter = new PhotoAdapter(user.getPhotos(), this);
		photo_list.setAdapter(photoAdapter);
		
		//accept fress barber
		acceptBarber = user.getAllowJoin();
		fengchengSelected = user.getRatio();
		
		SalonTools.setSelection(acceptBarber ? 0 : 1, acceptList);
		setAcceptBarber(acceptBarber);
		if(acceptBarber){
			if(fengchengSelected == 3) SalonTools.setSelection(0, fenchengList);
			if(fengchengSelected == 4) SalonTools.setSelection(1, fenchengList);
			if(fengchengSelected == 5) SalonTools.setSelection(2, fenchengList);
			SalonTools.setSelection(dengjiSelected - 1, dengjiList);
		}
		
		//products
		products = user.getProducts();
		productAdapter = new ProductAdapter(products, this);
		productAdapter.refreshHeight();
		product_list.setAdapter(productAdapter);
	}
	
	private void setExtraService(int i){
		switch (i) {
		case 1:
			service5Selected = true;
			service5_point.setImageResource(R.drawable.online);
			break;
			
		case 2:
			service6Selected = true;
			service6_point.setImageResource(R.drawable.online);
			break;
			
		case 3:
			service5Selected = true;
			service5_point.setImageResource(R.drawable.online);
			service6Selected = true;
			service6_point.setImageResource(R.drawable.online);
			break;
			
		case 4:
			service7Selected = true;
			service7_point.setImageResource(R.drawable.online);
			break;
			
		case 5:
			service5Selected = true;
			service5_point.setImageResource(R.drawable.online);
			service7Selected = true;
			service7_point.setImageResource(R.drawable.online);
			break;
			
		case 6:
			service6Selected = true;
			service6_point.setImageResource(R.drawable.online);
			service7Selected = true;
			service7_point.setImageResource(R.drawable.online);
			break;
			
		case 7:
			service5Selected = true;
			service5_point.setImageResource(R.drawable.online);
			service6Selected = true;
			service6_point.setImageResource(R.drawable.online);
			service7Selected = true;
			service7_point.setImageResource(R.drawable.online);
			break;
		}
	}
	
	private int getExtraService(){
		int result = 0;
		if(service5Selected)
			result += 1;
		if(service6Selected)
			result += 2;
		if(service7Selected)
			result += 4;
		
		return result;
	}
	
	private OnClickListener clickSelect = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.yes:
				acceptBarber = true;
				SalonTools.setSelection(0, acceptList);
				setAcceptBarber(true);
				break;
				
			case R.id.no:
				acceptBarber = false;
				SalonTools.setSelection(1, acceptList);
				setAcceptBarber(false);
				break;
				
			case R.id.fengcheng37:
				fengchengSelected = 3;
				SalonTools.setSelection(0, fenchengList);
				break;
				
			case R.id.fengcheng46:
				fengchengSelected = 4;
				SalonTools.setSelection(1, fenchengList);
				break;
				
			case R.id.fengcheng55:
				fengchengSelected = 5;
				SalonTools.setSelection(2, fenchengList);
				break;
				
			case R.id.yi:
				dengjiSelected = 1;
				SalonTools.setSelection(0, dengjiList);
				break;
				
			case R.id.er:
				dengjiSelected = 2;
				SalonTools.setSelection(1, dengjiList);
				break;
				
			case R.id.san:
				dengjiSelected = 3;
				SalonTools.setSelection(2, dengjiList);
				break;
				
			case R.id.si:
				dengjiSelected = 4;
				SalonTools.setSelection(3, dengjiList);
				break;
				
			case R.id.wu:
				dengjiSelected = 5;
				SalonTools.setSelection(4, dengjiList);
				break;
			}
		}
	};
	
	private void setAcceptBarber(boolean accept){
		dengji.setAlpha(accept ? 1f : 0.5f);
		fengcheng.setAlpha(accept ? 1f : 0.5f);
		findViewById(R.id.fengcheng37).setClickable(accept);
		findViewById(R.id.fengcheng46).setClickable(accept);
		findViewById(R.id.fengcheng55).setClickable(accept);
		findViewById(R.id.yi).setClickable(accept);
		findViewById(R.id.er).setClickable(accept);
		findViewById(R.id.san).setClickable(accept);
		findViewById(R.id.si).setClickable(accept);
		findViewById(R.id.wu).setClickable(accept);
		fengchengSelected = (byte) (accept ? 3 : 0);
		dengjiSelected = (byte) (accept ? 1 : 0);
		SalonTools.setSelection(accept ? 0 : fenchengList.size(), fenchengList);
		SalonTools.setSelection(accept ? 0 : dengjiList.size(), dengjiList);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.service5:
			if(service5Selected){
				extraService -= SERVICE5;
				service5Selected = false;
				service5_point.setImageResource(R.drawable.unonline);
			}else{
				extraService += SERVICE5;
				service5Selected = true;
				service5_point.setImageResource(R.drawable.online);
			}
			break;
			
		case R.id.service6:
			if(service6Selected){
				extraService -= SERVICE6;
				service6Selected = false;
				service6_point.setImageResource(R.drawable.unonline);
			}else{
				extraService += SERVICE6;
				service6Selected = true;
				service6_point.setImageResource(R.drawable.online);
			}
			break;
			
		case R.id.service7:
			if(service7Selected){
				extraService -= SERVICE7;
				service7Selected = false;
				service7_point.setImageResource(R.drawable.unonline);
			}else{
				extraService += SERVICE7;
				service7Selected = true;
				service7_point.setImageResource(R.drawable.online);
			}
			break;
			
		case R.id.modify_barber:
			Intent intent = new Intent(SalonRegisterActivity.this, SalonAddBarberActivity.class);
			startActivityForResult(intent, EDIT_BARBER);
			break;
			
		case R.id.district:
			Intent intent2 = new Intent(SalonRegisterActivity.this, DistrictActivity.class);
			intent2.putExtra("address", district.getText().toString());//不传参数也可以，就不会有默认选中某个值
			startActivityForResult(intent2, SELECT_DISTRICT);
			break;
			
		case R.id.done:
			if(validDone()){
				doUpload();
			}
			break;
		}
	}
	
	private boolean validDone(){
		if(SalonTools.editNotNull(name) && phone.getEditableText().toString().length() == 11
				&& SalonTools.editNotNull(contact) && SalonTools.editNotNull(address)
				&& SalonTools.editNotNull(mianji) && SalonTools.editNotNull(jiantouwei)
				&& SalonTools.editNotNull(xitouwei) && district.getText() != null && district.getText().length() > 0
				&& photoAdapter.getPhotoPaths().size() > 0 && barberCount > 0){
			return true;
		}
		Toast.makeText(this, R.string.salon_reString37, Toast.LENGTH_SHORT).show();
		return false;
	}
	
	private void doUpload(){
		CHLogger.d(this, "doUpload photos");
		final ArrayList<String> newPhotos = photoAdapter.getPhotoPaths();
		ArrayList<String> uploadPhotos = new ArrayList<String>(); 
		if(newPhotos.size() > 0){
			for(int i = 0; i < newPhotos.size(); i++){
				if(! CacheManager.INSTANCE.getCurrentUser().getPhotos().contains(newPhotos.get(i))){
					uploadPhotos.add(newPhotos.get(i));
					newPhotos.remove(i);
					i--;
				}
			}
		}
		
		if(uploadPhotos.size() == 0){
			uploadUser(newPhotos);
		}else{
			showWaitDialog();
			fileService.uploadFiles(uploadPhotos, DMUtil.getFacePhotoWidth(this), DMUtil.getFacePhotoHeight(this), 
					new AsyncResponseCompletedHandler<String>() {

						@Override
						public String doCompleted(ResponseBean<?> response,
								ChCareWepApiServiceType servieType) {
							if(response.getState() >= 0){
								try {
									ArrayList<String> photos = SalonTools.splitPhoto((String)response.getData());
									newPhotos.addAll(photos);
									uploadUser(newPhotos);
								} catch (Exception e) {
									e.printStackTrace();
									showToast(R.string.upload_fail);
									hideAllDialog();
								}
							}else{
								showToast(R.string.upload_fail);
								hideAllDialog();
							}
							return null;
						}
					});
		}
	}
	
	private void uploadUser(ArrayList<String> photos){
		final SalonUser user = CacheManager.INSTANCE.getCurrentUser().clone();
		user.setNick(SalonTools.getText(name));
		user.setPerson_Name(SalonTools.getText(contact));
//		user.setPhoto(SalonTools.getText(phone));
		user.setAreas(SalonTools.getUploadArea(SalonRegisterActivity.this, SalonTools.getText(district)));
		user.setAddr(SalonTools.getText(address));
		user.setSize(SalonTools.getText(mianji).equals("") ? 0 : Integer.valueOf(SalonTools.getText(mianji)));
		user.setHairCount(SalonTools.getText(jiantouwei).equals("") ? 0 : Integer.valueOf(SalonTools.getText(jiantouwei)));
		user.setWashCount(SalonTools.getText(xitouwei).equals("") ? 0 : Integer.valueOf(SalonTools.getText(xitouwei)));
		user.setAddinServices(getExtraService());
		user.setPhotos(photos);
		user.setTel(SalonTools.getText(phone));
		user.setAllowJoin(acceptBarber);
		user.setRatio(fengchengSelected);
		user.setMinLevel(dengjiSelected);
		user.setProducts(productAdapter.getProducts());
		user.setVersion((byte) 1);
		try {
			user.setDesc(recommend.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showWaitDialog();
		accountService.updateSelfMg(user, new AsyncResponseCompletedHandler<String>() {

			@Override
			public String doCompleted(ResponseBean<?> response,
					ChCareWepApiServiceType servieType) {
				if(response != null && response.getState() >= 0){
					if(CacheManager.INSTANCE.getCurrentUser().getStatus() != Status.NORMAL){
						Toast.makeText(SalonRegisterActivity.this, R.string.upload_success, Toast.LENGTH_LONG).show();
						user.setStatus(Status.VERIFYING);
					}else{
						Toast.makeText(SalonRegisterActivity.this, R.string.upload_success2, Toast.LENGTH_LONG).show();
					}
					CacheManager.INSTANCE.setCurrentUser(user.clone());
					hideAllDialog();
					finish();
				}else{
					Toast.makeText(SalonRegisterActivity.this, R.string.upload_fail, Toast.LENGTH_SHORT).show();
					hideAllDialog();
				}
				return null;
			}
		});
	}
	
	private class PhotoAdapter extends BaseAdapter{

		private ArrayList<String> list;
		private Context context;
		
		public PhotoAdapter(ArrayList<String> list, Context context) {
			this.list = (ArrayList<String>) list.clone();
			this.context = context;
			if(this.list.size() < PHOTO_MAX){
				this.list.add(this.list.size(), null);
			}
		}
		
		public PhotoAdapter(Context context) {
			list = new ArrayList<String>();
			list.add(null);
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private ArrayList<String> getPhotoPaths(){
			ArrayList<String> ret = (ArrayList<String>) list.clone();
			ret.remove(null);
			return ret;
		}

		private void addsalonPhoto(String path){
			if(list.size() == PHOTO_MAX){
				list.remove(PHOTO_MAX - 1);
				list.add(path);
			}else if(list.size() < PHOTO_MAX){
				list.add(list.size() - 1, path);
			}
			
			photoAdapter.notifyDataSetChanged();
			photo_list.setSelection(list.size() - 1);
		}
		
		private void removesalonPhoto(String path){
			if(list.size() == PHOTO_MAX){
				list.remove(path);
				if(list.get(list.size() - 1) != null){
					list.add(null);
				}
			}else if(list.size() < PHOTO_MAX){
				list.remove(path);
			}
			
			photoAdapter.notifyDataSetChanged();
			photo_list.setSelection(list.size() - 1);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final String path = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_list, null);
				viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
				viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
				
			viewHolder.photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(path == null)
						mPopupAltView.show();
				}
			});
			
			viewHolder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
					dialog.withTitle(R.string.dialog_title)
							.withMessage(R.string.salon_reString24)
									.setOKClick(R.string.ok_queren, new View.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											removesalonPhoto(path);
											dialog.dismiss();
										}
									})
									.setCancelClick(R.string.cancel_quxiao).show();
				}
			});
			
			if(path == null){
				viewHolder.delete.setVisibility(View.INVISIBLE);
				viewHolder.photo.setImageResource(R.drawable.add_salon_photo);
			}else{
				viewHolder.delete.setVisibility(View.VISIBLE);
				try {
					File file = new File(path);
					if(file.exists()){
						viewHolder.photo.setImageBitmap(PictureUtil.
								decodeSampledBitmapFromFile(path, 
										viewHolder.photo.getWidth(), 
										viewHolder.photo.getHeight()));
					}else{
						imgFetcher.loadFormCache(path, viewHolder.photo);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			
			return convertView;
		}
		
		final class ViewHolder{
			public ImageView photo;
			public Button delete;
		}
	}
	
	private class ProductAdapter extends BaseAdapter{

		private ArrayList<Product> list;
		private Context context;
		
		public ProductAdapter(ArrayList<Product> list, Context context) {
			if(list == null){
				this.list = new ArrayList<Product>();
			}else{
				this.list = (ArrayList<Product>) list.clone();
				try {
					for(int i = 0; i < this.list.size(); i++){
						Product p = this.list.get(i);
						if(p == null){
							list.remove(i);
							i--;
						}
					}
				} catch (Exception e) {
				}
			}
			
			this.context = context;
			this.list.add(0, null);
			if(this.list.size() < PRODUCT_MAX){
				this.list.add(null);
			}
		}
		
		public ProductAdapter(Context context) {
			list = new ArrayList<Product>();
			list.add(null);
			list.add(null);
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Product getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private ArrayList<Product> getProducts(){
			ArrayList<Product> ret = (ArrayList<Product>) list.clone();
			if(ret.size() > 0){
				for(int i = 0; i < ret.size(); i++){
					if(ret.get(i) == null){
						ret.remove(i);
						i--;
					}
				}
			}
			return ret;
		}
		
		private void addProduct(Product p){
			if(list.size() == PRODUCT_MAX){
				list.remove(PRODUCT_MAX - 1);
				list.add(p);
			}else if(list.size() < PRODUCT_MAX){
				list.add(list.size() - 1, p);
			}
			
			refreshHeight();
			productAdapter.notifyDataSetChanged();
			product_list.setSelection(list.size() - 1);
		}
		
		private void removeProduct(Product p){
			if(list.size() == PRODUCT_MAX){
				list.remove(p);
				if(list.get(list.size() - 1) != null){
					list.add(null);
				}
			}else if(list.size() < PRODUCT_MAX){
				list.remove(p);
			}
			
			refreshHeight();
			productAdapter.notifyDataSetChanged();
			product_list.setSelection(list.size() - 1);
		}
		
		private void refreshHeight(){
			ViewGroup.LayoutParams params = product_list.getLayoutParams();
			int hdp = SalonTools.getProductHeight(list.size());
			params.height = DMUtil.getHeight(SalonRegisterActivity.this, hdp);
			product_list.setLayoutParams(params);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final Product p = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_product_list, null);
				viewHolder.band = (EditText) convertView.findViewById(R.id.band);
				viewHolder.usage = (EditText) convertView.findViewById(R.id.usage);
				viewHolder.price = (EditText) convertView.findViewById(R.id.price);
				viewHolder.origin = (EditText) convertView.findViewById(R.id.origin);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
				viewHolder.button = (RelativeLayout) convertView.findViewById(R.id.button);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			final ViewHolder vh2 = viewHolder;
			
			if(position == 0){
				vh2.button.setVisibility(View.INVISIBLE);
				vh2.band.setText(R.string.salon_reString20);
				vh2.usage.setText(R.string.salon_reString21);
				vh2.price.setText(R.string.salon_reString22);
				vh2.origin.setText(R.string.salon_reString42);
				vh2.band.setEnabled(false);
				vh2.usage.setEnabled(false);
				vh2.price.setEnabled(false);
				vh2.origin.setEnabled(false);
				vh2.band.setBackgroundColor(Color.TRANSPARENT);
				vh2.usage.setBackgroundColor(Color.TRANSPARENT);
				vh2.price.setBackgroundColor(Color.TRANSPARENT);
				vh2.origin.setBackgroundColor(Color.TRANSPARENT);
			}else{
				vh2.button.setVisibility(View.VISIBLE);
				vh2.band.setBackgroundColor(Color.WHITE);
				vh2.usage.setBackgroundColor(Color.WHITE);
				vh2.price.setBackgroundColor(Color.WHITE);
				vh2.origin.setBackgroundColor(Color.WHITE);
				vh2.band.setText(p == null ? null : p.band);
				vh2.usage.setText(p == null ? null : p.usage);
				vh2.price.setText(p == null ? null : p.price);
				vh2.origin.setText(p == null ? null : p.origin);
			}
			
			if(p == null && position != 0){
				vh2.band.setEnabled(true);
				vh2.usage.setEnabled(true);
				vh2.price.setEnabled(true);
				vh2.origin.setEnabled(true);
				vh2.img.setImageResource(R.drawable.add_product);
			}else{
				vh2.band.setEnabled(false);
				vh2.usage.setEnabled(false);
				vh2.price.setEnabled(false);
				vh2.origin.setEnabled(false);
				vh2.img.setImageResource(R.drawable.delete_product);
			}
			
			vh2.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(p == null){
						String band = vh2.band.getText().toString();
						String usage = vh2.usage.getText().toString();
						String price = vh2.price.getText().toString();
						String origin = vh2.origin.getText().toString();
						
						if(band != null && ! band.equals("") && usage != null && ! usage.equals("") 
								&& price != null && ! price.equals("") && origin != null && ! origin.equals("")){
							Product product = new Product(band, usage, price, origin);
							addProduct(product);
						}else{
							Toast.makeText(context, R.string.salon_reString25, Toast.LENGTH_SHORT).show();
						}
					}else{
						final AppMainDialog dialog = new AppMainDialog(context, R.style.appdialog);
						dialog.withTitle(R.string.dialog_title)
						.withMessage(R.string.salon_reString26)
						.setOKClick(R.string.ok_queren, new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								removeProduct(p);
								dialog.dismiss();
							}
						})
						.setCancelClick(R.string.cancel_quxiao).show();
					}
				}
			});
			
			return convertView;
		}
		
		final class ViewHolder{
			private EditText band;
			private EditText usage;
			private EditText price;
			private EditText origin;
			private ImageView img;
			private RelativeLayout button;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CHLogger.d(this, "requestCode " + requestCode + ",  resultCode " + resultCode);
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_LOCAL){
					if (data != null) {
						String filepath = data.getStringExtra("filepath");
						Uri uri = Uri.fromFile(new File(filepath));
						mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_FACE);
					}
				}
				else if(requestCode == PhotoSelectPopupView.TAKE_PHOTO_FROM_CAMERA) {
					Uri uri = mPopupAltView.getPhotoUri();
					mPopupAltView.cutPhoto(uri, PhotoType.PHOTO_FACE);
				}
				else if(requestCode == PhotoSelectPopupView.CUT_PHOTO){
					mPhotoUri = mPopupAltView.getPhotoUri();
					if(mPhotoUri != null && mPhotoUri.getPath() != null){
						File file = new File(mPhotoUri.getPath());
						if(file.exists() && file.isFile()){
							photoAdapter.addsalonPhoto(file.getPath());
						}
					}
				}
				
				if(requestCode == SELECT_DISTRICT){
					district.setText(data.getStringExtra("address"));
				}
			}
			
			if(requestCode == EDIT_BARBER){
				barberCount = data.getIntExtra("barberCount", 0);
				CHLogger.d(this, "barberCount " + barberCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
