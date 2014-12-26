/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.repository;

import java.sql.Blob;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ucas.ir.project.entity.DocVector;
import com.ucas.ir.project.entity.SQuery;

public interface DocVectorDao extends PagingAndSortingRepository<DocVector, Long>, JpaSpecificationExecutor<SQuery> {

	@Query("select doc.vector from DocVector doc where doc.id = (:id)")
	Blob getVectorById(@Param("id")Long id);
	
	
}
