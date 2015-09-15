package cn.changhong.chcare.core.webapi.bean;

import java.io.Serializable;
import com.changhong.util.db.annotation.CHPrimaryKey;
import com.changhong.util.db.annotation.CHTransient;

public class OrderView implements Serializable{

	@CHTransient
	private static final long serialVersionUID = 1L;
	
	//所有用户
	@CHPrimaryKey()
    private int Id;//订单Id
	private String OrderDate;//预约日期
	private String OrderTime; //预约时间
	private String Desc; //备注
	private byte OrderStatus; //备注//订单状态
	private int UserId;//顾客Id
	private int SalonId;//发廊Id
	private int SalonBarberId;//店内发型师Id(二选一)
	private int FreeBarberId;//自由发型师Id(二选一)
	private int CouponId;// //优惠券Id
	private float Score;//评分
	private float Value; //优惠券面值
	private String SalonName;
	private String CustomName;
	private String BarberName;
	private String CustomTel;
	private String SalonTel;
	private String BarberTel;
	private Byte Ratio;
	
	public void setId(int i){
		Id = i;
	}
	
	public int getId(){
		return Id;
	}
	
	public void setOrderDate(String i){
		OrderDate = i;
	}
	
	public String getOrderDate(){
		return OrderDate;
	}
	
	public void setOrderTime(String i){
		OrderTime = i;
	}
	
	public String getOrderTime(){
		return OrderTime;
	}
	
	public void setDesc(String i){
		Desc = i;
	}
	
	public String getDesc(){
		return Desc;
	}
	
	public void setOrderStatus(byte i){
		OrderStatus = i;
	}
	
	public byte getOrderStatus(){
		return OrderStatus;
	}
	
	public void setRatio(byte i){
		Ratio = i;
	}
	
	public byte getRatio(){
		return Ratio;
	}
	
	public void setUserId(int i){
		UserId = i;
	}
	
	public int getUserId(){
		return UserId;
	}
	
	public void setSalonId(int i){
		SalonId = i;
	}
	
	public int getSalonId(){
		return SalonId;
	}
	
	public void setSalonBarberId(int i){
		SalonBarberId = i;
	}
	
	public int getSalonBarberId(){
		return SalonBarberId;
	}
	
	public void setFreeBarberId(int i){
		FreeBarberId = i;
	}
	
	public int getFreeBarberId(){
		return FreeBarberId;
	}
	
	public void setCouponId(int i){
		CouponId = i;
	}
	
	public int getCouponId(){
		return CouponId;
	}
	
	public void setScore(float i){
		Score = i;
	}
	
	public float getScore(){
		return Score;
	}
	
	public void setValue(float i){
		Value = i;
	}
	
	public float getValue(){
		return Value;
	}
	
	public void setSalonName(String i){
		SalonName = i;
	}
	
	public String getSalonName(){
		return SalonName;
	}
	
	public void setCustomName(String i){
		CustomName = i;
	}
	
	public String getCustomName(){
		return CustomName;
	}
	
	public void setBarberName(String i){
		BarberName = i;
	}
	
	public String getBarberName(){
		return BarberName;
	}
	
	public void setCustomTel(String i){
		CustomTel = i;
	}
	
	public String getCustomTel(){
		return CustomTel;
	}
	
	public void setSalonTel(String i){
		SalonTel = i;
	}
	
	public String getSalonTel(){
		return SalonTel;
	}
	
	public void setBarberTel(String i){
		BarberTel = i;
	}
	
	public String getBarberTel(){
		return BarberTel;
	}
}
