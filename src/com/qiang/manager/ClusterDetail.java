package com.qiang.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ClusterDetail {
	 
	static final double termThres = 0.005;
	
	String title;
	int clusterSize;
	int centerDoc ; // 距离质心最近的文档
	double simSum = 0; // 用于存放簇内相似度之和， 可以节省计算
	HashMap<Integer, Double> clusterCenter = new HashMap<Integer, Double>();
	ArrayList<Integer> docList = new ArrayList<Integer>();	
	// 当簇的维度过高的时候进行维度筛选
	// 筛选原则是： 选择那些词项权重比较大的
	public  void featureSelect(){
		Set<Integer> keyset = clusterCenter.keySet();
		Iterator<Integer> it = keyset.iterator();
		int k;
		while(it.hasNext()){
			k = it.next();
			if(clusterCenter.get(k)< termThres){
				clusterCenter.remove(k);
			}
		}
	}
}
