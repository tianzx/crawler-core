package org.crawler.core.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CrawlerController {

	private Logger log = Logger.getLogger(CrawlerController.class); 
	@RequestMapping("login")
	// 用来处理前台的login请求
	private	String loginController(ModelMap model, HttpServletRequest request) {
		
		model.addAttribute("test", "test");
		Cookie[] cookies = request.getCookies();
		for(Cookie c :cookies){
			System.err.println(c);
		}
		System.err.println("success3");
		log.info("success in logger");
		return "/success";
	}
}
