package com.jsf.wxpay.constants;

public class Configuration {

	//微信公众平台appid
	public static String appid = "wxa724f52bb5f2adba";
	//微信商户平台商户ID
	public static String mchId = "1229355602";
	//微信商户平台api密钥
	public static String wechatWxpayKey = "abcdefghijklmnopqrstuvwxyzqwerty";
	//微信商户平台支付结果通知URL
	public static String notifyUrl = "http://test.zishu15.com/web/order/wechatWxPayGetPayResult.htm";
	//统一下单URL
	public static String wechatUnifiedOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	//查询订单URL
	public static String wechatOrderQueryURL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
}
