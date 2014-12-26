/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ucas.ir.project.entity.SQuery;

public interface SQueryDao extends PagingAndSortingRepository<SQuery, Long>, JpaSpecificationExecutor<SQuery> {

	@Query("select squery.squery from SQuery squery where squery.squery like (:keyword) order by squery.count desc")
	List<String> getSQueryLike(@Param("keyword")String keyword,Pageable top5);
	
	@Query("select squery from SQuery squery where squery.squery like (:keyword) order by squery.count desc")
	List<SQuery> getSQueryObjectLike(@Param("keyword")String keyword,Pageable top5);
	
	@Modifying
	@Query("update SQuery squery set squery.count = squery.count+1 where squery.squery like (:keyword)")
	int autoIncrease(@Param("keyword")String keyword);
}
