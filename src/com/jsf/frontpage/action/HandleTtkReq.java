package com.jsf.frontpage.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
//@RequestMapping("/ttk")
public class HandleTtkReq extends BaseController{
	
	private static Logger logger = Logger.getLogger(HandleTtkReq.class);

	/**
	 * TODO:JS下订单接口
	 * @param request
	 * @param response
	 * return:void
	 */
	@RequestMapping("/getLesson")
	public ModelAndView getLesson(HttpServletRequest request,HttpServletResponse response){
		logger.error("调用ttk");
		Map<String,Object> map = new HashMap<String,Object>(); 
        map.put("name","fengxiang"); 
        map.put("age",23); 
        return new ModelAndView("jsonView").addObject(map); 
	}
	
	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String test(){
		
		return "test";
	}
	
}
