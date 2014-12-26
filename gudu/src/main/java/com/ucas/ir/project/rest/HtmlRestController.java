/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.rest;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.web.MediaTypes;

import com.ucas.ir.project.entity.Html;
import com.ucas.ir.project.service.HtmlService;

/**
 * Html的Restful API的Controller.
 * 
 * @author calvin
 */
@RestController
@RequestMapping(value = "/api/v1/html")
public class HtmlRestController {

	private static Logger logger = LoggerFactory.getLogger(HtmlRestController.class);

	@Autowired
	private HtmlService htmlService;

	@Autowired
	private Validator validator;


	@RequestMapping(value = "/auto/{keyword}",method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
	public List<String> autoNameList(@PathVariable("keyword") String keyword) throws UnsupportedEncodingException {
		keyword=new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
		return htmlService.getAutoName(keyword);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public Html get(@PathVariable("id") Long id) {
		Html html = htmlService.getHtml(id);
		if (html == null) {
			String message = "网页不存在(id:" + id + ")";
			logger.warn(message);
			throw new RestException(HttpStatus.NOT_FOUND, message);
		}
		return html;
	}

}
