package com.jsf.wxpay.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsf.dao.GeneralDao;
import com.jsf.wxpay.dao.OrderQueryDao;
import com.jsf.wxpay.entity.OrderQueryRequestData;

@Repository
public class OrderQueryDaoImpl implements OrderQueryDao {
	private static Logger logger = Logger.getLogger(OrderQueryDaoImpl.class);
	
	@Autowired
	private GeneralDao generalDao;
	
	@Override
	public boolean saveOrderQueryReq(OrderQueryRequestData orederQueryReq) throws Exception {
		return generalDao.saveEntity(orederQueryReq);
	}
	
	
}
