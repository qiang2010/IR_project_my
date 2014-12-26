package com.njz.dictionary;

import java.sql.Timestamp;

/*
 * 带有得分的结果
 */
public class ResultScored {
	

	private int docID;
	private double tf_idf;
	private double hot;
	private long time;//记录时间差
	private double count;//文档中，词项出现次数
	private double doc_score;
	

	//得分机制计算公式
	public ResultScored(int docID, double tf_idf, double hot, long time1, double count) {
		
		this.docID = docID;
		this.tf_idf = tf_idf;
		
		this.hot = hot;
		
		Timestamp today = new Timestamp(System.currentTimeMillis());
		
//		System.out.println("时间:"+time1);
//		System.out.println("时间1:"+today.getTime());
		this.time = today.getTime() - time1;
		
		this.count = count;
//		this.doc_score = (double)this.time + this.tf_idf + (double)this.count + this.hot;
		doc_score = 0.5*(Math.log((double)this.hot)) + 3*(this.tf_idf) + 5*(this.count) + 2*(10.0/Math.log10(this.time));//计算tf-idf值
		Double doc_score1 = doc_score;
		if(doc_score1.isInfinite() || doc_score1.isNaN()){
			this.doc_score =0;
		}
	}
	
	public double getDoc_score() {
		return doc_score;
	}

	public long getTime() {
		return time;
	}


	public void setDoc_score(long doc_score) {
		this.doc_score = doc_score;
	}


	public void setTime(long time) {
		this.time = time;
	}
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public double getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getTf_idf() {
		return tf_idf;
	}
	public void setTf_idf(double tf_idf) {
		this.tf_idf = tf_idf;
	}
	public double getHot() {
		return hot;
	}
	public void setHot(double hot) {
		this.hot = hot;
	}
	
}
