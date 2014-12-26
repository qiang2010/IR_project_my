/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ucas.ir.project.entity.Html;

public interface HtmlDao extends PagingAndSortingRepository<Html, Long>, JpaSpecificationExecutor<Html> {

	@Query("select title from Html html where html.title like (:keyword)")
	List<String> getNameLike(@Param("keyword")String keyword);
	
	@Query("select title from Html html where html.id = (:id)")
	String getTitleById(@Param("id")Long id);
	
	@Query("select html from Html html where (html.id) in (:list)")
	Page<Html> getHtmlByList(@Param("list")List<Long> result_docID_list, Pageable page);
}
