package com.changhong.activity.widget.activity;
/**  
 * @ClassName:TextInputActivity
 * @Description: TODO
 * @author quxy  
 *
 */
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.llw.salon.R;
import com.changhong.activity.BaseActivity;
import com.changhong.activity.widget.TextInputBean;
import com.changhong.annotation.CHInjectView;

public class TextInputActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "TextInputActivity";
	private final static String TextType = "TextBox"; //大文本框
	
	public static final int UPDATE_USER_INFO_SUCCESS = 105;
	public static final int UPDATE_USER_INFO_FAILED = 106;

	@CHInjectView(id = R.id.page_setting_edt)
	private EditText mPageSettingExt;
	@CHInjectView(id = R.id.word_count)
	private TextView mWordCountTxt;
	@CHInjectView(id = R.id.backBtn)
	private ImageButton mBackBtn;
	@CHInjectView(id = R.id.clearBtn)
	private ImageButton mClearBtn;
	@CHInjectView(id = R.id.sign_setting_edt)
	private EditText mPublishExt;
	@CHInjectView(id = R.id.title)
	private TextView mTitleTxt;
	@CHInjectView(id = R.id.unit)
	private TextView mUnitTxt;
	@CHInjectView(id = R.id.finish)
	private Button mFinishBtn;
	@CHInjectView(id = R.id.publish)
	private Button mPublishBtn;
	@CHInjectView(id = R.id.page_setting_linearlayout)
	private LinearLayout mPageSettingLinearLayout;
	@CHInjectView(id = R.id.sign_setting_linearlayout)
	private LinearLayout mSignSettingLinearLayout;

	private TextInputBean mTextBean;
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		initData();
		initView();
		initUI();
	}

	private void initView() {
		Log.i(TAG,"initView");
		mBackBtn.setOnClickListener(this);
		mClearBtn.setOnClickListener(this);
		mFinishBtn.setOnClickListener(this);
		mPublishBtn.setOnClickListener(this);
		mPublishExt.addTextChangedListener(getTextChangeListener());
	}
	
	private void initData() {
		
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra("TextInputBean");
		
		if(obj == null){
			mTextBean = new TextInputBean();
		}else{
			mTextBean = (TextInputBean)obj;
		}
		
		if(mTextBean.getMaxLen() == -1){
			mTextBean.setMaxLen(1000);
		}
		
		if(mTextBean.getMaxLenMsg() == null || "".equals(mTextBean.getMaxLenMsg())){
			mTextBean.setMaxLenMsg(getResources().getString(R.string.input_max_len, mTextBean.getMaxLen()));
		}
		
		if(mTextBean.getMinLenMsg() == null || "".equals(mTextBean.getMinLenMsg())){
			mTextBean.setMinLenMsg(getResources().getString(R.string.input_min_len, mTextBean.getMinLen()));
		}
	}

	private void initUI() {

		if(mTextBean.getTitle() != -1){
			mTitleTxt.setText(mTextBean.getTitle());	
		}

		if (TextType.equals(mTextBean.getTextType())) {
			mPageSettingLinearLayout.setVisibility(View.GONE);
			mSignSettingLinearLayout.setVisibility(View.VISIBLE);

			mTextBean.setMaxLen(mTextBean.getMaxLen() == -1?1000:mTextBean.getMaxLen());
			mPublishExt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextBean.getMaxLen())});

			if (mTextBean.getContent() != null) {
				mPublishExt.setText(mTextBean.getContent());
				mWordCountTxt.setText(String.valueOf(mTextBean.getMaxLen() - mTextBean.getContent().length()));
			}else{
				mWordCountTxt.setText(String.valueOf(mTextBean.getMaxLen()));
			}

			return;
		} 

		if(mTextBean.getUnit() != -1){
			mUnitTxt.setText(mTextBean.getUnit());
		}

		mPageSettingExt.setInputType(mTextBean.getInputType() == -1 ? InputType.TYPE_CLASS_TEXT : mTextBean.getInputType());
		mPageSettingExt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextBean.getMaxLen())});
		
		if(mTextBean.getHintMsg() != null){
			mPageSettingExt.setHint(mTextBean.getHintMsg());
		}

		if(mTextBean.getContent() != null){
			mPageSettingExt.setText(mTextBean.getContent());
		}

	}

	private TextWatcher getTextChangeListener() {
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int count) {
				//llw start 一次性删除1个以上的文字时，countInt会比实际值小
				
				int length = arg0.length();
				Integer countInt = mTextBean.getMaxLen() - length;
				
				if (countInt < 1) {
					String message = getResources().getString(R.string.input_count_reach_max);
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
					mWordCountTxt.setText("0");
				}else{
					mWordCountTxt.setText(countInt.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn :
			this.finish();
			break;
		case R.id.finish :
		case R.id.publish :
			doFinishData();
			break;
		case R.id.clearBtn :
			if (mPageSettingExt.getText().length() != 0) {
				mPageSettingExt.setText("");
			}
			break;

		default :
			break;
		}
	}

	private void doFinishData() {

		switch (mTextBean.getInputType()) {
		case InputType.TYPE_CLASS_NUMBER :
		case InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL :
			inputNumberText();
			break;
		case InputType.TYPE_CLASS_TEXT :
		case -1 :
			inputStringText();
			break;
		default :
			break;
		}

	}
	
	/**
	 * 输入的是数字
	 */
	private void inputNumberText(){
		
		String content = mPageSettingExt.getText() == null ? "": mPageSettingExt.getText().toString().trim();
		if (content.length() > 0 || mTextBean.getMinLen() == -1) {
			
			double value = Double.parseDouble(content);
			if(isValidInput(value)){
				putUserInfo();
			}
			
		} else {
			popInfo(mTextBean.getMinLenMsg());
		}
	}
	
	/**
	 * 无限定的输入
	 */
	private void inputStringText(){
		
		if (TextType.equals(mTextBean.getTextType())) {
			putUserInfo();
		}else{
			
			String content = mPageSettingExt.getText() == null ? "": mPageSettingExt.getText().toString().trim();
			int byteSize = content.getBytes().length;
			Log.i(TAG,"byteSize =" + byteSize + "nickStr.length()" + content.length());
			if (content.length() > 0 || mTextBean.getMinLen() == -1) {
				
				if(isValidInput(byteSize)){
					putUserInfo();
				}
			} else {
				
				popInfo(getResources().getString(R.string.input_count_null));
			}
		}
	}
	
	private boolean isValidInput(double len) {
		
		boolean isOK = false;
		if(len < mTextBean.getMinLen()){
			
			popInfo(mTextBean.getMinLenMsg());
		}else if(len > mTextBean.getMaxLen()){
			
			popInfo(mTextBean.getMaxLenMsg());
		}else{
			
			isOK = true;
		}
		return isOK;
	}

	private void putUserInfo() {
		
		Intent intent = new Intent();
		if (TextType.equals(mTextBean.getTextType())) {
			intent.putExtra("content", mPublishExt.getText());
		} else {
			intent.putExtra("content", mPageSettingExt.getText());
		}
		
		setResult(RESULT_OK, intent);
		this.finish();
	}

	private void popInfo(String msg) {
		Toast.makeText(TextInputActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
}
