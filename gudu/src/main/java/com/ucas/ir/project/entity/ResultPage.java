package com.ucas.ir.project.entity;

import java.util.ArrayList;
import java.util.List;

import com.ucas.ir.project.cluster.ClusterDetail;

public class ResultPage {
	private int number;
	private int total;
	private int totalPages;
	private int clustersNum;
	
	private ArrayList<Html> content;
	private List<ClusterDetail> clusters;
	
	public ResultPage() {
		this.number = 0;
		this.total = 0;
		this.totalPages = 1;
		this.clustersNum = 0;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getClustersNum() {
		return clustersNum;
	}
	public void setClustersNum(int clustersNum) {
		this.clustersNum = clustersNum;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public ArrayList<Html> getContent() {
		return content;
	}
	public void setContent(ArrayList<Html> content) {
		this.content = content;
	}
	
	
	public List<ClusterDetail> getClusters() {
		return clusters;
	}
	public void setClusters(List<ClusterDetail> clusters) {
		this.clusters = clusters;
	}
	public boolean hasNextPage(){
		return number!=totalPages-1;
	}
	public boolean hasPreviousPage(){
		return number!=0;
	}
}
