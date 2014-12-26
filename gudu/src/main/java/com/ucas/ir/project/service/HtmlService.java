/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ucas.ir.project.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.njz.dictionary.Index;
import com.njz.dictionary.ResultScored;
import com.ucas.ir.project.cluster.ClusterDetail;
import com.ucas.ir.project.cluster.Clustering_GAAC;
import com.ucas.ir.project.entity.Html;
import com.ucas.ir.project.entity.ResultPage;
import com.ucas.ir.project.repository.DictionaryDao;
import com.ucas.ir.project.repository.HtmlDao;
import com.ucas.ir.project.repository.OtherDictionaryDao;

// Spring Bean的标识.
@Component
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class HtmlService {

	@Autowired
	private HtmlDao htmlDao;
	@Autowired
	private DictionaryDao dictionaryDao;
	@Autowired
	private OtherDictionaryDao otherDictionaryDao;
	@Autowired
	private Clustering_GAAC build;
	
	public Html getHtml(Long id) {
		return htmlDao.findOne(id);
	}

	//动态补齐查询相关name
	public List<String> getAutoName(String keyword) {
		// TODO Auto-generated method stub
		return htmlDao.getNameLike("%"+keyword+"%");
	}

	public Page<Html> getHtml(Map<String, Object> searchParams, int pageNumber, int pageSize) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
		Specification<Html> spec = buildSpecification(searchParams);

		return htmlDao.findAll(spec, pageRequest);
	}

	/**
	 * 创建分页请求.
	 */
	private PageRequest buildPageRequest(int pageNumber, int pagzSize) {

		return new PageRequest(pageNumber - 1, pagzSize);
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Html> buildSpecification(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Html> spec = DynamicSpecifications.bySearchFilter(filters.values(), Html.class);
		return spec;
	}

	
	/*public LinkedList<Index> searchIndexByTerm(String term){
		LinkedList<Index> result = new LinkedList<Index>();
		Blob blob = dictionaryDao.getBlob(term);
		if (null == blob) {
			return null;
		}
		ObjectInputStream in = null;
		 try {
			in = new ObjectInputStream(blob.getBinaryStream());
			result = (LinkedList<Index>)in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return result;
	}*/
	public ResultPage getHtmlPageByTerm(List<String> terms,int pageNumber,int pageSize){
		HashMap<Integer,LinkedList<Double>> result_map = new HashMap<Integer,LinkedList<Double>>(); 
		result_map = getResultsDocByTerms(terms);
		LinkedList<Double> idList = result_map.get(0);
		LinkedList<Double> scoreList = result_map.get(1);
		LinkedList<Double> sizeList = result_map.get(2);
		if (null == idList || null == scoreList || null == sizeList) {
			return new ResultPage();
		}
		ArrayList<Html> resultHtml = new ArrayList<Html>(); 
		if (null == idList || idList.isEmpty()) {
			idList = new LinkedList<Double>();
			idList.add(0.0);
		}else {
			int start = (pageNumber-1)*pageSize;
			int end = pageNumber*pageSize;
			for (int i = start; i < end && i<idList.size(); i++) {
				resultHtml.add(htmlDao.findOne(Math.round(idList.get(i))));
			}
		}
		
		ArrayList<ClusterDetail> clusterList = new ArrayList<ClusterDetail>();
		ArrayList<Double> docScoreList = new ArrayList<Double>(scoreList);
		ArrayList<Integer> docIdList = new ArrayList<Integer>();
		for (Double id : idList) {
			docIdList.add((int) Math.round(id));
		}
		
		try {
			clusterList = build.cluster(docIdList,docScoreList);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResultPage resultPage = new ResultPage();
		resultPage.setClusters(clusterList.subList(0, 8));
		resultPage.setContent(resultHtml);
		resultPage.setClustersNum(clusterList.size());
		resultPage.setTotal((int)Math.round(sizeList.get(0)));
		resultPage.setNumber(pageNumber-1);
		resultPage.setTotalPages((idList.size()+4)/pageSize);
		return resultPage;
	}
	
	
	/*
	 * 根据词项查询倒排记录，得到最后docID
	 */
	/*public List<Long> getResults_docID(List<String> terms) {
		
		if (terms==null || terms.isEmpty()){
			System.out.println("输入查询内容不正确");
			return null;
		}else{
			System.out.println("查询词项数量："+terms.size());
		}
			
		//词项在list中，正常保存
		int i;
		LinkedList<Index> results = new LinkedList<Index>();
		ArrayList<Long> docID_list = new ArrayList<Long>();
		//遍历词项，找到第一个能查到的结果集合
		for (i = 0; i < terms.size(); i++) {
			results = searchIndexByTerm(terms.get(i));
			if (null != results)
				break;
			
		}
		//处理查询结果集result，不断叠加
				for (i=i+1; i < terms.size(); i++) {//for1			
					if (results == null)
						return null;
					
					LinkedList<Index> tmp = searchIndexByTerm(terms.get(i));
					if (tmp == null)
						continue;
					
					for (int j = 0; j < results.size(); j++) {
						boolean have = false;
						for (int k = 0; k < tmp.size(); k++) {
							
							if (tmp.get(k).equals(results.get(j))) {
								have = true;
								break;
							}
						}
						if (have == false) {
							results.remove(j);
							j--;
						}				
					}//end-for
				
				}//end-for1
				
				if(results == null || results.isEmpty()){
					System.out.println(terms.toString()+"没有对应结果集合");
				}else{
					for(Index e : results){
						docID_list.add(Long.valueOf(e.getDocumentID()));
					}
				}
				
				return docID_list;
	}*/
	/*
	 * 根据词项查询倒排记录，得到最后-List<Index>
	 */
	public LinkedList<Index> getResults_index(LinkedList<String> terms) {
		
		if (terms == null){
			System.out.println("输入查询内容不正确");
			return null;
		}else{
			System.out.println("查询词项数量："+terms.size());
		}
			
		//词项在list中，正常保存
		int i;
		LinkedList<Index> results = new LinkedList<Index>();
//		LinkedList<Integer> docID_list = new LinkedList<Integer>();
		//遍历词项，找到第一个能查到的结果集合
		for (i = 0; i < terms.size(); i++) {
			results = searchedByTerm(terms.get(i));
			if (results != null)
				break;
		}
		
		//处理查询结果集result，不断叠加
		for (i=i+1; i < terms.size(); i++) {//for1			
			if (results == null)
				return null;
			
			LinkedList<Index> tmp = searchedByTerm(terms.get(i));
			if (tmp == null)
				continue;
			
			for (int j = 0; j < results.size(); j++) {
				boolean have = false;
				for (int k = 0; k < tmp.size(); k++) {
					
					if (tmp.get(k).equals(results.get(j))) {
						have = true;
						break;
					}
				}
				if (have == false) {
					results.remove(j);
					j--;
				}				
			}//end-for
		
		}//end-for1
		return results;//返回--list<index>
	}
	
	/*
	 * 根据Index对象得到索引中的docID值
	 */
	public ArrayList<Integer> getDoc_idByIndex(LinkedList<Index> index_list){
		ArrayList<Integer> docID_list = new ArrayList<Integer>();
		if(index_list == null){
			return null;
		}
		
		for(Index e : index_list){
			docID_list.add(e.getDocumentID());
		}
		return docID_list;//返回--文档ID
	}
	
	
	public LinkedList<Index> searchedByTerm(String term) {
			
		LinkedList<Index> result = new LinkedList<Index>();
		Blob blob = otherDictionaryDao.getBlob(term);
		if (null == blob) {
			return null;
		}
		ObjectInputStream in = null;
		 try {
			in = new ObjectInputStream(blob.getBinaryStream());
			result = (LinkedList<Index>)in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return result;
	
	}//end-searchByTerm
	public LinkedList<Index> searchByTermbody(String term) {
		
		LinkedList<Index> result = new LinkedList<Index>();
		Blob blob = dictionaryDao.getBlob(term);
		if (null == blob) {
			return null;
		}
		ObjectInputStream in = null;
		 try {
			in = new ObjectInputStream(blob.getBinaryStream());
			result = (LinkedList<Index>)in.readObject();
			in.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

}//end-searchByTermbody

	
	
	/*
	 * 根据词项查询倒排记录，得到最后-List<Index>(不带有得分)---查询body
	 */
	public LinkedList<ResultScored> getResultsDocByBodyTerms(List<String> terms) {
		
		if (terms == null){
			System.out.println("输入查询内容不正确");
			return null;
		}else{
			System.out.println("查询词项数量："+terms.size());
		}
			
		int i;
		//文档id-词项出现次数,hot和,tf-idf
		HashMap<Integer,LinkedList<Double>> docID_count_map = new HashMap<Integer,LinkedList<Double>>();
		LinkedList<Index> results = new LinkedList<Index>();
//		LinkedList<Integer> docID = new LinkedList<Integer>();
		LinkedList<ResultScored> resultsScored = new LinkedList<ResultScored>();	
		
		//遍历词项--找到第一个不为空的词项
		for (i = 0; i < terms.size(); i++) {			
			results = searchByTermbody(terms.get(i));
			if (results != null){
//				docID = getDoc_idByIndex(results);
				for(Index index : results){
					LinkedList<Double> count_tfidf_hot = new LinkedList<Double>();
					count_tfidf_hot.add((double) 1);//次数
					count_tfidf_hot.add((double) index.hot);//hot和
//					System.out.println("hot:"+index.hot);
					count_tfidf_hot.add(index.tf_idf);//tfidf和
//					System.out.println("tf_idf:"+index.tf_idf);
					docID_count_map.put(index.documentID, count_tfidf_hot);
				}
				break;
			}			
		}		
		
		//处理查询结果集result，不断叠加;里面的count可能不准，近似代替
		for (i=i+1; i < terms.size(); i++) {//for0
			if (results == null)
				return null;
			
			//tmp存在情况下
			LinkedList<Index> tmp = new LinkedList<Index>();
			tmp = searchByTermbody(terms.get(i));
			if (tmp == null)
				continue;
//			docID = getDoc_idByIndex(tmp);
			for(Index ind : tmp){
				int doc = ind.getDocumentID();
				if(docID_count_map.get(doc) == null){
					LinkedList<Double> count_tfidf_hot = new LinkedList<Double>();
					count_tfidf_hot.add((double) 1);//次数---0
					count_tfidf_hot.add((double) ind.hot);//hot和---1
					count_tfidf_hot.add(ind.tf_idf);//tfidf和---2
					docID_count_map.put(ind.documentID, count_tfidf_hot);
				}else{
					double for_hot = docID_count_map.get(doc).get(1);
					docID_count_map.get(doc).set(1, (ind.hot+for_hot));
					double for_tfidf = docID_count_map.get(doc).get(2);
					docID_count_map.get(doc).set(2, (ind.tf_idf+for_tfidf));
				}
					
			}
			LinkedList<Index> temp_addIndex = new LinkedList<Index>();
			//tmp内部存在result里面没有的，补充到result中
			for (int j=0; j<results.size(); j++){//for1
				for (int k=0; k<tmp.size(); k++){//for2
					if (tmp.get(k).equals(results.get(j))) {//出现交集
						int now_docID = results.get(j).getDocumentID();
						LinkedList<Double> for_list = docID_count_map.get(now_docID);
						double for_count = for_list.get(0);//原来的count值
						docID_count_map.get(now_docID).set(0, (for_count+1));
						temp_addIndex.add(tmp.get(k));
					}			
				}//end-for2				
			}//end-for1
			
			tmp.removeAll(temp_addIndex);
			results.addAll(tmp);
//			System.out.println("temp_addIndex:"+temp_addIndex.size());
		
		}//end-for0
		
		if(results == null || results.isEmpty()){
			System.out.println(terms.toString()+"没有对应结果集合");
		}else{
			System.out.println("结果数量" + results.size());
		}
		
//		for(int key : docID_count_map.keySet()){
//			System.out.println(key+" : " + docID_count_map.get(key));
//		}
		
		resultsScored = ScoreProcess.scoreProcess(results, docID_count_map);
		
		return resultsScored;//返回--得分对象list(针对body)	
	}//end-getResults_index
	
	/*
	 * 根据词项查询倒排记录，得到最后-List<Index>(不带有得分)---查询title，如果不够进行查询body
	 */
	public HashMap<Integer,LinkedList<Double>> getResultsDocByTerms(List<String> terms) {
		
		if (terms == null){
			System.out.println("输入查询内容不正确");
			return null;
		}else{
			System.out.println("查询词项数量："+terms.size());
		}
			
		int i;
		//文档id-词项出现次数,hot和,tf-idf
		HashMap<Integer,LinkedList<Double>> docID_count_map = new HashMap<Integer,LinkedList<Double>>();
		LinkedList<Index> results = new LinkedList<Index>();
//		LinkedList<Integer> docID = new LinkedList<Integer>();
		
		//遍历词项--找到第一个不为空的词项
		for (i = 0; i < terms.size(); i++) {			
			results = searchedByTerm(terms.get(i));
			if (results != null){
//				docID = getDoc_idByIndex(results);
				for(Index index : results){
					LinkedList<Double> count_tfidf_hot = new LinkedList<Double>();
					count_tfidf_hot.add((double) 1);//次数
					count_tfidf_hot.add((double) index.hot);//hot和
//					System.out.println("hot:"+index.hot);
					count_tfidf_hot.add(index.tf_idf);//tfidf和
//					System.out.println("tf_idf:"+index.tf_idf);
					docID_count_map.put(index.documentID, count_tfidf_hot);
				}
				break;
			}			
		}		
		
		//处理查询结果集result，不断叠加;里面的count可能不准，近似代替
		for (i=i+1; i < terms.size(); i++) {//for0
			if (results == null)
				return null;
			
			//tmp存在情况下
			LinkedList<Index> tmp = new LinkedList<Index>();
			tmp = searchedByTerm(terms.get(i));
			if (tmp == null)
				continue;
//			docID = getDoc_idByIndex(tmp);
			for(Index ind : tmp){
				int doc = ind.getDocumentID();
				if(docID_count_map.get(doc) == null){
					LinkedList<Double> count_tfidf_hot = new LinkedList<Double>();
					count_tfidf_hot.add((double) 1);//次数---0
					count_tfidf_hot.add((double) ind.hot);//hot和---1
					count_tfidf_hot.add(ind.tf_idf);//tfidf和---2
					docID_count_map.put(ind.documentID, count_tfidf_hot);
				}else{
					double for_hot = docID_count_map.get(doc).get(1);
					docID_count_map.get(doc).set(1, (ind.hot+for_hot));
					double for_tfidf = docID_count_map.get(doc).get(2);
					docID_count_map.get(doc).set(2, (ind.tf_idf+for_tfidf));
				}
					
			}
			LinkedList<Index> temp_addIndex = new LinkedList<Index>();
			//tmp内部存在result里面没有的，补充到result中
			for (int j=0; j<results.size(); j++){//for1
				for (int k=0; k<tmp.size(); k++){//for2
					if (tmp.get(k).equals(results.get(j))) {//出现交集
						int now_docID = results.get(j).getDocumentID();
						LinkedList<Double> for_list = docID_count_map.get(now_docID);
						double for_count = for_list.get(0);//原来的count值
						docID_count_map.get(now_docID).set(0, (for_count+1));
						temp_addIndex.add(tmp.get(k));
					}			
				}//end-for2				
			}//end-for1
			
			tmp.removeAll(temp_addIndex);
			results.addAll(tmp);
//			System.out.println("temp_addIndex:"+temp_addIndex.size());
		
		}//end-for0
		
		if(results == null || results.isEmpty()){
			System.out.println(terms.toString()+"没有对应结果集合");
		}else{
			System.out.println("结果数量" + results.size());
		}
		
		LinkedList<ResultScored> resultsScored_all = new LinkedList<ResultScored>();	
		resultsScored_all = ScoreProcess.scoreProcess(results, docID_count_map);
		
		//title、description搜索结果不够，需要进一步搜索body
		if(resultsScored_all.size() < 100){
			LinkedList<ResultScored> resultsScored_body = new LinkedList<ResultScored>();	
			resultsScored_body = getResultsDocByBodyTerms(terms);
			resultsScored_all.addAll(resultsScored_body);
		}
		
		HashMap<Integer,LinkedList<Double>> finalMap = new HashMap<Integer,LinkedList<Double>>();
		LinkedList<Double> docID_list =  new LinkedList<Double>();
		LinkedList<Double> score_list =  new LinkedList<Double>();
		LinkedList<Double> size_list =  new LinkedList<Double>();

		if(results == null || results.isEmpty()){
			System.out.println(terms.toString()+"没有对应结果集合");
		}else{
			for(ResultScored res_score : resultsScored_all){
				docID_list.add((double)res_score.getDocID());
				score_list.add(res_score.getDoc_score());
			}
			
			double size;
			if(results.size() >= 100){
				size = (double)results.size();
			}else{
//				double random = (Math.random()*100)*(Math.random()*10)+90;
				double random = 100.0;
				size = random + (double)results.size();
			}
			size_list.add(size);//所有的查询结果数量

			finalMap.put(0, docID_list);
			finalMap.put(1, score_list);
			finalMap.put(2, size_list);
		}
		
		return finalMap;//返回--文档ID		
	}//end-getResults_index
	
	public List<String> getTerms(String input) {
		String[] terms= input.split(" ");
		List<String> termList = new ArrayList<String>();
		termList = (List<String>) Arrays.asList(terms);
		return termList;
	}
}
