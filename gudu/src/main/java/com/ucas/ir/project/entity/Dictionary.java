package com.ucas.ir.project.entity;


import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;



@Entity
@Table(name = "body_dictionary")
public class Dictionary extends IdEntity {
	private String term;
	private Blob tIndex;
	private int df;
	
	
	public Dictionary() {
		super();
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
	public Blob gettIndex() {
		return tIndex;
	}
	public void settIndex(Blob tIndex) {
		this.tIndex = tIndex;
	}
	public int getDf() {
		return df;
	}
	public void setDf(int df) {
		this.df = df;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
