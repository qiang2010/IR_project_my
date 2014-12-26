package com.ucas.ir.project.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import com.njz.dictionary.Index;
import com.njz.dictionary.ResultScored;


public class ScoreProcess {

	//得分考虑因素 ： tf_idf；热度hot；出现次数count；时间time；文档长度；
		public static LinkedList<ResultScored> scoreProcess(LinkedList<Index> countedIndexList, 
															HashMap<Integer, LinkedList<Double>> docID_count_map){
			
			LinkedList<ResultScored> SordedIndex = new LinkedList<ResultScored>();
			
			for(Index ind : countedIndexList){
				
				int doc_id = ind.getDocumentID();
				long time = ind.time;
				
				LinkedList<Double> for_list = new LinkedList<Double>();
				for_list = docID_count_map.get(doc_id);
				double count  = for_list.get(0);
				double hot = for_list.get(1);
				double tf_idf = for_list.get(2);
				
				//生成一个带有得分的文档序列
				ResultScored score = new ResultScored(doc_id,  tf_idf,  hot,  time,  count);
				SordedIndex.add(score);
			}
			Comparator<ResultScored> comparator = new Comparator<ResultScored>() {

				@Override
				public int compare(ResultScored result_0, ResultScored result_1) {
					if (null == result_0 || null == result_1) {
						return 0;
					} 
					if (result_0.equals(result_1)) {
						return 0;
					} 
					Double flag = result_0.getDoc_score() - result_1.getDoc_score();
					if (flag.isNaN()) {
						return 0;
					}
					if(result_0.getDoc_score() > result_1.getDoc_score()){
						 return -1;
					 }else if(result_0.getDoc_score() < result_1.getDoc_score()){
						 return 1;
					 }
					 return 0;
				}
			};
			Collections.sort(SordedIndex, comparator);
			
			LinkedList<ResultScored> topK_SordedIndex = new LinkedList<ResultScored>();
			if(SordedIndex.size()>100){
				for(int i=0; i<100; i++){
					topK_SordedIndex.add(SordedIndex.get(i));
				}
//				for(ResultScored top_result : topK_SordedIndex){
//					System.out.println(top_result.getDocID() + " : " + top_result.getDoc_score());
//					System.out.println(top_result.getDocID()+" : (出现次数"+top_result.getCount()+") : (热度"+top_result.getHot()+") : (tf-idf"
//							+top_result.getTf_idf()+") : (时间"+top_result.getTime()+")");
//				}
				return topK_SordedIndex;
			}
			
//			for(ResultScored res : SordedIndex){
//				System.out.println(res.getDocID() + " : " + res.getDoc_score());
//				System.out.println(res.getDocID()+" : (出现次数"+res.getCount()+") : (热度"+res.getHot()+") : (tf-idf"
//						+res.getTf_idf()+") : (时间"+res.getTime()+")");
//			}
			
			
			return SordedIndex;
		}
	
}//end-class
