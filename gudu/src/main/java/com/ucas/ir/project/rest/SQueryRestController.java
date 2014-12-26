/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.rest;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.web.MediaTypes;

import com.ucas.ir.project.service.SQueryService;

/**
 * SQuery的Restful API的Controller.
 * 
 * @author calvin
 */
@RestController
@RequestMapping(value = "/api/v1/query")
public class SQueryRestController {

	@Autowired
	private SQueryService queryService;

	@Autowired
	private Validator validator;


	@RequestMapping(value = "/auto/{keyword}",method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
	public List<String> autoQueryList(@PathVariable("keyword") String keyword) throws UnsupportedEncodingException {
		keyword=new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
		return queryService.getAutoQuery(keyword);
	}
	

}
