package com.njz.dictionary;

import java.io.Serializable;
import java.sql.Timestamp;

public class Index implements Serializable{
	
//  private static final long serialVersionUID = 4558876142427402513L;

	public int documentID;
	public int tf;
	public double tf_idf;
	public long time;
	public long hot;
	
	public static double N = 100000;

	public Index(int documentID, int tf, int df) {
		this.documentID = documentID;
		this.tf = tf;
		this.tf_idf = (1+Math.log((double)tf))*(Math.log(N/df));//计算tf-idf值
	}
	
	public Index(int documentID, int tf, int df, long time, long hot) {
		this.documentID = documentID;
		this.tf = tf;
		this.tf_idf = (1+Math.log((double)tf))*(Math.log(N/df));//计算tf-idf值
		this.time = time;
		this.hot = hot;
	}

	public double getTf_idf() {
		return tf_idf;
	}

	public static double getN() {
		return N;
	}

	public static void setN(double n) {
		N = n;
	}

	public void setTf_idf(double tf_idf) {
		this.tf_idf = tf_idf;
	}

	public int getDocumentID() {
		return documentID;
	}
	
	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}
	
	public int getTf() {
		return tf;
	}
	
	public void setTf(int tf) {
		this.tf = tf;
	}	
	
	
	public boolean equals(Object obj) {
		
		Index other = (Index) obj;
		if (documentID != other.documentID){
			return false;
		}else{
			return true;
		}		
	}

}
