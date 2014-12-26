package com.ucas.ir.project.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.ucas.ir.project.entity.Html;

public class ClusterDetail {
	 
	static final double termThres = 0.005;
	
	Html title;
	int clusterSize;
    double score = 0;
	@Override
	public String toString() {
		return "ClusterDetail [title=" + title + ", clusterSize=" + clusterSize
				+ ", score=" + score + ", clusterCenterSize=" + clusterCenter.size() +", centerDoc=" + centerDoc + ", simSum="
				+ simSum +  ", docList="
				+ docList + "]";  // ", clusterCenter=" + clusterCenter +
 	}
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
	public Html getTitle() {
		return title;
	}
	public void setTitle(Html title) {
		this.title = title;
	}
	public int getClusterSize() {
		return clusterSize;
	}
	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getCenterDoc() {
		return centerDoc;
	}
	public void setCenterDoc(int centerDoc) {
		this.centerDoc = centerDoc;
	}
	public double getSimSum() {
		return simSum;
	}
	public void setSimSum(double simSum) {
		this.simSum = simSum;
	}
	public HashMap<Integer, Double> getClusterCenter() {
		return clusterCenter;
	}
	public void setClusterCenter(HashMap<Integer, Double> clusterCenter) {
		this.clusterCenter = clusterCenter;
	}
	public ArrayList<Integer> getDocList() {
		return docList;
	}
	public void setDocList(ArrayList<Integer> docList) {
		this.docList = docList;
	}
	public static double getTermthres() {
		return termThres;
	}
	
}
