package com.ucas.ir.project.entity;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;


@Entity
@Table(name = "docvector")
public class DocVector extends IdEntity {
	private int docLen;
	private Blob vector;
	
	
	public DocVector() {
		super();
	}
	public int getDocLen() {
		return docLen;
	}
	public void setDocLen(int docLen) {
		this.docLen = docLen;
	}
	public Blob getVector() {
		return vector;
	}
	public void setVector(Blob vector) {
		this.vector = vector;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
