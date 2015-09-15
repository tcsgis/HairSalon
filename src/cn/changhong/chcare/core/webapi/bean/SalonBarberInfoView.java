package cn.changhong.chcare.core.webapi.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.changhong.util.CHLogger;
import com.changhong.util.db.annotation.CHPrimaryKey;
import com.changhong.util.db.annotation.CHTransient;


public class SalonBarberInfoView implements Serializable, Cloneable{

	@CHTransient
	private static final long serialVersionUID = 1L;
	
	@CHPrimaryKey
	private int Id;
	private String Nick;
	private String Photo;
	private int SalonInfoId;
	private int[] Prices;
	private String Health;
	private byte WorkYears;//从业年限
	private ArrayList<String> Certs ;//培训证书文件Id
	private ArrayList<String> Works ;//优秀作品文件Id
	private int Adept;
	
	public void setId(int id){
		Id = id;
	}
	
	public int getId(){
		return Id;
	}
	
	public void setNick(String v){
		Nick = v;
	}
	
	public String getNick(){
		return Nick;
	}
	
	public void setPhoto(String v){
		Photo = v;
	}
	
	public String getPhoto(){
		return Photo;
	}
	
	public void setSalonInfoId(int v){
		SalonInfoId = v;
	}
	
	public int getSalonInfoId(){
		return SalonInfoId;
	}
	
	public void setPrices(int[] v){
		Prices = v;
	}
	
	public int[] getPrices(){
		return Prices;
	}
	
	public void setHealth(String v){
		Health = v;
	}
	
	public String getHealth(){
		return Health;
	}
	
	public void setWorkYears(byte v){
		WorkYears = v;
	}
	
	public byte getWorkYears(){
		return WorkYears;
	}
	
	public void setWorks(ArrayList<String> v){
		Works = v;
	}
	
	public ArrayList<String> getWorks(){
		return Works;
	}
	
	public void setCerts(ArrayList<String> v){
		Certs = v;
	}
	
	public ArrayList<String> getCerts(){
		return Certs;
	}
	
	public void setAdept(int v){
		Adept = v;
	}
	
	public int getAdept(){
		return Adept;
	}
	
	public SalonBarberInfoView clone() {
		SalonBarberInfoView o = null;
		try {
			o = (SalonBarberInfoView) super.clone();
		} catch (CloneNotSupportedException e) {
			CHLogger.e(this, e);
		}
		return o;
	}
}
