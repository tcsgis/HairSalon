package com.aaa.db;

import java.io.Serializable;

public class Product implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DIVIDER1 = "&&";//product之间
	public static final String DIVIDER2 = "\\|";//band/usage/price之间
	
	public String band;
	public String usage;
	public String price;
	public String origin;//产地
	
	public Product(String band, String usage, String price, String origin){
		this.band = band;
		this.usage = usage;
		this.price = price;
		this.origin = origin;
	}
}
