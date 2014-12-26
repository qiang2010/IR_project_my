package com.ucas.ir.project.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "squery")
public class SQuery extends IdEntity {
	private String squery;
	private int count;
	public String getSquery() {
		return squery;
	}
	public void setSquery(String squery) {
		this.squery = squery;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public SQuery() {
		super();
	}
	public SQuery(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
