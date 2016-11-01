package com.jsf.frontpage.dao;

import java.util.List;

import com.jsf.frontpage.entity.LessonBean;

public interface QueryLessonDao {
	public List<LessonBean> queryLessonByType(String type) throws Exception;
}
