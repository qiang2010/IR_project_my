/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ucas.ir.project.entity.Bodyterm;

public interface BodytermDao extends PagingAndSortingRepository<Bodyterm, Long>, JpaSpecificationExecutor<Bodyterm> {

	@Query("select distinct bodyterm.documentId  from Bodyterm bodyterm ")
	List<Integer> getDistinctTerm();
	
	@Query("select bodyterm  from Bodyterm bodyterm where bodyterm.documentId = (:docId)")
	List<Bodyterm> getDistinctTerm(@Param("docId")Integer docId);
}
