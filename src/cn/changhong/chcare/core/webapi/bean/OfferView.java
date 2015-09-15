package cn.changhong.chcare.core.webapi.bean;

import java.io.Serializable;

import com.changhong.util.db.annotation.CHPrimaryKey;
import com.changhong.util.db.annotation.CHTransient;

public class OfferView implements Serializable{
	
	@CHTransient
	private static final long serialVersionUID = 1L;
	
	//所有用户
	@CHPrimaryKey()
	private int Id;//竞价Offer Id  
	private String Pics;//照片文件Id
	private String Desc;//描述
	private String Area;//区域
	private int UserId; //发布顾客Id
	private byte OfferStatus;//订单状态{0: 默认, 1: 已满额, 2:已完成,3: 已关闭}
	private byte BiddingCount;//竞价数
	private String userNick;//顾客昵称
	
	private int BarberId;//中标barberID，仅对BarerBidActivity有效
	private int Price = 0;//仅对BarerBidActivity有效
	
	public void setId(int i){
		Id = i;
	}
	
	public int getId(){
		return Id;
	}
	
	public void setPrice(int i){
		Price = i;
	}
	
	public int getPrice(){
		return Price;
	}
	
	public void setUserId(int i){
		UserId = i;
	}
	
	public int getUserId(){
		return UserId;
	}
	
	public void setBarberId(int i){
		BarberId = i;
	}
	
	public int getBarberId(){
		return BarberId;
	}
	
	public void setPics(String i){
		Pics = i;
	}
	
	public String getPics(){
		return Pics;
	}
	
	public void setuserNick(String i){
		userNick = i;
	}
	
	public String getuserNick(){
		return userNick;
	}
	
	public void setDesc(String i){
		Desc = i;
	}
	
	public String getDesc(){
		return Desc;
	}
	
	public void setArea(String i){
		Area = i;
	}
	
	public String getArea(){
		return Area;
	}
	
	public void setOfferStatus(byte i){
		OfferStatus = i;
	}
	
	public byte getOfferStatus(){
		return OfferStatus;
	}
	
	public void setBiddingCount(byte i){
		BiddingCount = i;
	}
	
	public byte getBiddingCount(){
		return BiddingCount;
	}
}
