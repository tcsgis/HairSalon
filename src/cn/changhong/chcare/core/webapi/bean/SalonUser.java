package cn.changhong.chcare.core.webapi.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.aaa.db.Product;
import com.changhong.util.CHLogger;
import com.changhong.util.db.annotation.CHPrimaryKey;
import com.changhong.util.db.annotation.CHTransient;

public class SalonUser implements Serializable, Cloneable{
	
	@CHTransient
	private static final long serialVersionUID = 1L;
	
	//所有用户
	@CHPrimaryKey()
    private int Id;//用户Id
	private String Name;//用户名
    private byte Status ;//正常0, 待审核1, 已驳回/未补全2
    private byte Role;
    private byte Version;//当前补全要求版本号, 默认0, 补全后为1
    private String Nick;//昵称
    private String Photo;//头像文件Id

//理发店和发型师
    private float AvgScore;//顾客给的评分
    private String Areas;//所在地区
    private String Person_Name ;//(联系人)姓名
    private byte Level;//评级
    private byte Ratio;//分成比例(37分=3, 依次类推)
    private ArrayList<Product> Products;//产品信息
    private String Desc;//自我推荐
//理发店
    private String Addr;//地址
    private boolean AllowJoin;//是否允许自由发型师加入
    private byte MinLevel;//最低可加入的自由发型师评级
    private int Size;//面积
    private int HairCount;//剪头工位
    private int WashCount ;//洗头工位
    private int AddinServices;//额外服务(0: 无, 1:盘发, 2:美甲, 4:化妆, (1|4)=5:盘发+化妆)
    private ArrayList<String> Photos;//门店照片
    private String Tel;//电话
    private int SalonBarberCount;
//发型师
    private int SalonInfoId;//理发店Id,自由发型师的为空
    private String Person_Id;//身份证号
    private int[] Prices;//服务费(应该不会有按服务费筛选的功能吧)//在最后一位加上“擅长”
    private String Health;//健康证文件Id(没有传null)
    private byte WorkYears;//从业年限
    private String SalonName;//所在发廊名称
    private ArrayList<String> Certs ;//培训证书文件Id
    private ArrayList<String> Works ;//优秀作品文件Id
    private int Adept;//擅长
    
  //所有用户
	public void setId(int id){
		Id = id;
	}
	
	public int getId(){
		return Id;
	}
	
	public void setName(String b){
		Name = b;
	}
	
	public String getName(){
		return Name;
	}
	    
	public void setStatus(byte b){
		Status = b;
	}
	
	public byte getStatus(){
		return Status;
	}
	
	public void setRole(byte r){
		Role = r;
	}
	
	public byte getRole(){
		return Role;
	}
	
	public void setVersion(byte v){
		Version = v;
	}
	
	public byte getVersion(){
		return Version;
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
	
  //理发店和发型师
    public void setAreas(String v){
    	Areas = v;
	}
	
	public String getAreas(){
		return Areas;
	}
	
	public void setPerson_Name(String v){
		Person_Name = v;
	}
	
	public String getPerson_Name(){
		return Person_Name;
	}
	
	public void setDesc(String v){
		Desc = v;
	}
	
	public String getDesc(){
		return Desc;
	}
	
	public void setLevel(byte v){
		Level = v;
	}
	
	public byte getLevel(){
		return Level;
	}
	
	public void setRatio(byte v){
		Ratio = v;
	}
	
	public byte getRatio(){
		return Ratio;
	}
	
	public void setAvgScore(float v){
		AvgScore = v;
	}
	
	public float getAvgScore(){
		return AvgScore;
	}
	
	public void setProducts(ArrayList<Product> v){
		Products = v;
	}
	
	public ArrayList<Product> getProducts(){
		return Products;
	}
	
	//理发店
    public void setAddr(String v){
    	Addr = v;
	}
	
	public String getAddr(){
		return Addr;
	}
	
	public void setAllowJoin(boolean v){
		AllowJoin = v;
	}
	
	public boolean getAllowJoin(){
		return AllowJoin;
	}
	
	public void setMinLevel(byte v){
		MinLevel = v;
	}
	
	public byte getMinLevel(){
		return MinLevel;
	}
	
	public void setSize(int v){
		Size = v;
	}
	
	public int getSize(){
		return Size;
	}
	
	public void setHairCount(int v){
		HairCount = v;
	}
	
	public int getHairCount(){
		return HairCount;
	}
	
	public void setWashCount(int v){
		WashCount = v;
	}
	
	public int getWashCount(){
		return WashCount;
	}
	
	public void setAddinServices(int v){
		AddinServices = v;
	}
	
	public int getAddinServices(){
		return AddinServices;
	}
	
	public void setPhotos(ArrayList<String> v){
		Photos = v;
	}
	
	public ArrayList<String> getPhotos(){
		return Photos;
	}
	
	public void setTel(String v){
		Tel = v;
	}
	
	public String getTel(){
		return Tel;
	}
	
	//发型师
    public void setSalonInfoId(int v){
    	SalonInfoId = v;
	}
	
	public int getSalonInfoId(){
		return SalonInfoId;
	}
	
	public void setSalonBarberCount(int v){
		SalonBarberCount = v;
	}
	
	public int getSalonBarberCount(){
		return SalonBarberCount;
	}
	
	public void setPerson_Id(String v){
		Person_Id = v;
	}
	
	public String getPerson_Id(){
		return Person_Id;
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
	
	public void setSalonName(String v){
		SalonName = v;
	}
	
	public String getSalonName(){
		return SalonName;
	}
	
	public void setCerts(ArrayList<String> v){
		Certs = v;
	}
	
	public ArrayList<String> getCerts(){
		return Certs;
	}
	
	public void setWorks(ArrayList<String> v){
		Works = v;
	}
	
	public ArrayList<String> getWorks(){
		return Works;
	}
	
	public void setWorkYears(byte v){
		WorkYears = v;
	}
	
	public byte getWorkYears(){
		return WorkYears;
	}
	
	public void setAdept(int v){
		Adept = v;
	}
	
	public int getAdept(){
		return Adept;
	}
	
	public SalonUser clone() {
		SalonUser o = null;
		try {
			o = (SalonUser) super.clone();
		} catch (CloneNotSupportedException e) {
			CHLogger.e(this, e);
		}
		return o;
	}
}
