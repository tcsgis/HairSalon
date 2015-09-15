package cn.changhong.chcare.core.webapi.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.aaa.util.SalonTools;

public class BannerPic implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String Photo;
	private String Url;
	
	public void setPhotos(String s){
		Photo = s;
	}
	
	public ArrayList<String> getPhotos(){
		return SalonTools.splitPhoto(Photo);
	}
	
	public void setUrls(String s){
		Url = s;
	}
	
	public ArrayList<String> getUrls(){
		return SalonTools.splitPhoto(Url);
	}
}
