package com.qiang.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ClusterDetail {
	 
	static final double termThres = 0.005;
	
	String title;
	int clusterSize;
	int centerDoc ; // ��������������ĵ�
	double simSum = 0; // ���ڴ�Ŵ������ƶ�֮�ͣ� ���Խ�ʡ����
	HashMap<Integer, Double> clusterCenter = new HashMap<Integer, Double>();
	ArrayList<Integer> docList = new ArrayList<Integer>();	
	// ���ص�ά�ȹ��ߵ�ʱ�����ά��ɸѡ
	// ɸѡԭ���ǣ� ѡ����Щ����Ȩ�رȽϴ��
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
