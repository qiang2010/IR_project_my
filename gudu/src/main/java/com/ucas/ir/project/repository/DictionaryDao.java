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

import com.ucas.ir.project.entity.Dictionary;

public interface DictionaryDao extends PagingAndSortingRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {

	@Query("select dict.tIndex from Dictionary dict where dict.term= (:term)")
	Blob getBlob(@Param("term")String term);
}
