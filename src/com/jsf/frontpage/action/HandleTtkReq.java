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
import org.springframework.web.bind.annotation.ResponseBody;
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
	 * TODO:根据tag获取课程列表
	 * @param request
	 * @param response
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/getLessonsByType", method = RequestMethod.GET)
	public Map<String, Object> getLessonsByType(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<LessonBean> lessons = new ArrayList<>();
		String type = request.getParameter("type");
		if(StringUtils.isBlank(type)){
			logger.error("请求参数为空");
			map.put("ret_code", 0);
			map.put("ret_msg","请求参数异常");
			return map;
		}
		lessons=(List<LessonBean>)queryLessonDao.queryLessonByType(type);
		map.put("ret_code", 1);
		map.put("ret_data", lessons);
        return map; 
	}
	
	/**
	 * TODO:测试运行环境
	 * @return
	 */
	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String test(){
		return "test";
	}
	
}
