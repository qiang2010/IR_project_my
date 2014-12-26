/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springside.modules.web.Servlets;

import com.ir.wordsegment.WordSegmentation;
import com.ucas.ir.project.entity.ResultPage;
import com.ucas.ir.project.entity.SQuery;
import com.ucas.ir.project.service.HtmlService;
import com.ucas.ir.project.service.SQueryService;

/**
 * Html管理的Controller, 使用Restful风格的Urls:
 * 
 * List page : POST /html/
 * @author xpx
 */
@Controller
@RequestMapping(value = "/html")
public class HtmlController {

	private static final String PAGE_SIZE = "8";

	@Autowired
	private HtmlService htmlService;
	@Autowired
	private SQueryService squeryService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String listGET(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			Model model,ServletRequest request) throws UnsupportedEncodingException {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		String keyword = (String)searchParams.get("LIKE_title");
		if (null == keyword || keyword.trim().equals("")) {
			model.addAttribute("message", "请输入关键字");
			return "index";
		}
		keyword=new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
		searchParams.put("LIKE_title", keyword);
		List<String> terms = null;
		try {
			terms = WordSegmentation.getWordsList(keyword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(terms);
		ResultPage htmls = htmlService.getHtmlPageByTerm(terms, pageNumber, pageSize);//htmlService.getHtml(searchParams, pageNumber, pageSize);
		//readindex.searchHtmlByKey(terms, pageNumber, pageSize);
		
		
		model.addAttribute("htmls", htmls);
		
		squeryService.autoIncrease(keyword);
		List<SQuery> others = squeryService.getOthers(terms); 
		String split = squeryService.getSplit(terms);
		model.addAttribute("others", others);
		model.addAttribute("htmls", htmls);
		model.addAttribute("keyword", keyword);
		model.addAttribute("splits", split);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "htmlList";
	}
	
	
	@RequestMapping(value="/cluster", method = RequestMethod.GET)
	public String listClusterGET(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			Model model,ServletRequest request) throws UnsupportedEncodingException {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		String keyword = (String)searchParams.get("LIKE_title");
		if (null == keyword || keyword.trim().equals("")) {
			model.addAttribute("message", "请输入关键字");
			return "index";
		}
		keyword=new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
		searchParams.put("LIKE_title", keyword);
		List<String> terms = null;
		try {
			terms = WordSegmentation.getWordsList(keyword);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println(terms);
		ResultPage htmls = htmlService.getHtmlPageByTerm(terms, pageNumber, pageSize);//htmlService.getHtml(searchParams, pageNumber, pageSize);
		//readindex.searchHtmlByKey(terms, pageNumber, pageSize);
		squeryService.autoIncrease(keyword);
		List<SQuery> others = squeryService.getOthers(terms); 
		String split = squeryService.getSplit(terms);
		model.addAttribute("message", "请输入关键字");
		model.addAttribute("others", others);
		model.addAttribute("htmls", htmls);
		model.addAttribute("keyword", keyword);
		model.addAttribute("splits", split);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "htmlList";
	}/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Html对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getHtml(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("html", htmlService.getHtml(id));
		}
	}

}
