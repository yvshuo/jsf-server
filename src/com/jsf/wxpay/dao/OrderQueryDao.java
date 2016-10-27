package com.jsf.wxpay.dao;

import com.jsf.wxpay.entity.OrderQueryRequestData;

public interface OrderQueryDao {
	public boolean saveOrderQueryReq(OrderQueryRequestData orederQueryReq) throws Exception;
}
