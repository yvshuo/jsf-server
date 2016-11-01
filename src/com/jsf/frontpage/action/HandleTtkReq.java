package com.jsf.frontpage.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.jsf.frontpage.dao.QueryLessonDao;
import com.jsf.frontpage.entity.LessonBean;

@Controller
//@RequestMapping("/ttk")
public class HandleTtkReq extends BaseController{
	
	private static Logger logger = Logger.getLogger(HandleTtkReq.class);
	
	@Autowired
	private QueryLessonDao queryLessonDao;
	
	/**
	 * TODO:获取课程列表
	 * @param request
	 * @param response
	 * return:void
	 * @throws Exception 
	 */
	@RequestMapping(value="/getLesson", method = RequestMethod.GET)
	public ModelAndView getLesson(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.error("cao");
		List<LessonBean> lessons = new ArrayList<>();
		String tag = request.getParameter("tag");
		if(StringUtils.isBlank(tag)){
			return null;
		}
		lessons=(List<LessonBean>)queryLessonDao.queryLessonByType(tag);
        return new ModelAndView("jsonView").addObject(lessons); 
	}
	
	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String test(){
		return "test";
	}
	
}
