package com.aaa.util;

public class OrderStatus {
	public static final byte Pending = 0; // 预约中
	public static final byte Wating1 = 1; // (自由发型师)等待发廊同意
	public static final byte Wating2 = 2; // (发廊)等待发型师同意
	public static final byte Doing = 3; // 预约成功
	public static final byte Reject = 4; // 预约被拒绝
	public static final byte Done = 5; // 订单已完成
	public static final byte Shared = 6; // 订单已分享
	public static final byte Cancled = 7; // 已取消
}
