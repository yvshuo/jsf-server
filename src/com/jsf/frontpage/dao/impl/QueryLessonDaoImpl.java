package com.jsf.frontpage.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsf.dao.GeneralDao;
import com.jsf.exception.DaoException;
import com.jsf.frontpage.dao.QueryLessonDao;
import com.jsf.frontpage.entity.LessonBean;
import com.jsf.wxpay.dao.impl.OrderQueryDaoImpl;

@Repository
public class QueryLessonDaoImpl implements QueryLessonDao {
	private static Logger logger = Logger.getLogger(QueryLessonDaoImpl.class);
	
	@Autowired
	private GeneralDao generalDao;
	
	@Override
	public List<LessonBean> queryLessonByType(String type) throws Exception {
		return	generalDao.getEntityList(LessonBean.class, 
				"select * from lesson where type=?",
				new Object[]{type});
	}

}
