/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ucas.ir.project.entity.SQuery;
import com.ucas.ir.project.repository.SQueryDao;

//import com.ucas.ir.project.repository.SQueryDao;

// Spring Bean的标识.
@Component
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class SQueryService {
	private SQueryDao queryDao;

	//动态补齐查询相关name
	public List<String> getAutoQuery(String keyword) {
		// 自动补齐的候选查询 前5条
		Pageable top5 = new PageRequest(0, 5);
		return queryDao.getSQueryLike("%"+keyword+"%",top5);
	}
		
	//搜索历史增长
	public void autoIncrease(String keyword){
		if (0 == queryDao.autoIncrease(keyword)) {
			SQuery query = new SQuery();
			query.setSquery(keyword);
			query.setCount(1);
			queryDao.save(query);
		}
	}
	@Autowired
	public void setSQueryDao(SQueryDao queryDao) {
		this.queryDao = queryDao;
	}
	//查询相关查询语句的查询次数最高前三个排名
	public List<SQuery> getOthers(List<String> terms) {
		if (null == terms || terms.isEmpty()) {
			return new ArrayList<SQuery>();
		}
		Comparator<SQuery> comparator = new Comparator<SQuery>(){
			@Override
			public int compare(SQuery o1, SQuery o2) {
				if (o2.getCount() == o1.getCount()) {
					return 0;
				} else {
					return o2.getCount() - o1.getCount();
				}
				
			}
			  
		};
		Set<SQuery> otherSet = new HashSet<SQuery>();
		Pageable top3 = new PageRequest(0, 3);
		for (String keyword : terms) {
			otherSet.addAll(queryDao.getSQueryObjectLike("%"+keyword+"%",top3));
		}
		List<SQuery> otherList = new ArrayList<SQuery>();
		for (SQuery sQuery : otherSet) {
			otherList.add(sQuery);
		}
		Collections.sort(otherList,comparator);
		return otherList;
		
	}
	public String getSplit(List<String> terms) {
		StringBuffer result = new StringBuffer();
		for (int i = 0;i<terms.size();i++) {
			result.append(' '+terms.get(i));
		}
		return result.toString();
	}

	
}
