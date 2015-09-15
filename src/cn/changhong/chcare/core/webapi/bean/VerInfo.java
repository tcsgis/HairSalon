package cn.changhong.chcare.core.webapi.bean;

public class VerInfo extends BannerPic{

	private static final long serialVersionUID = 1L;

	private int VerCode;
	private String File;
	private String Desc;
	private String VerName;
	
	public void setVerCode(int i){
		VerCode = i;
	}
	
	public int getVerCode(){
		return VerCode;
	}
	
	public void setFile(String i){
		File = i;
	}
	
	public String getFile(){
		return File;
	}
	
	public void setDesc(String i){
		Desc = i;
	}
	
	public String getDesc(){
		return Desc;
	}
	
	public void setVerName(String i){
		VerName = i;
	}
	
	public String getVerName(){
		return VerName;
	}
}
