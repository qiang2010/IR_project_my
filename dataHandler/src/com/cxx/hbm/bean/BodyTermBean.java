package com.ir.hbm.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class BodyTermBean  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6410093359000718035L;
	
	public BodyTermBean(){}
	

	public BodyTermBean(long term_id, String term, long document_id, int tf) {
		super();
		this.term_id = term_id;
		this.term = term;
		this.document_id = document_id;
		this.tf = tf;
	}


	private long term_id;	
	private String term;
	private long document_id;
	private int tf;

	
	public long getTerm_id() {
		return term_id;
	}

	public void setTerm_id(long term_id) {
		this.term_id = term_id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public long getDocument_id() {
		return document_id;
	}

	public void setDocument_id(long document_id) {
		this.document_id = document_id;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (document_id ^ (document_id >>> 32));
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + (int) (term_id ^ (term_id >>> 32));
		result = prime * result + tf;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BodyTermBean other = (BodyTermBean) obj;
		if (document_id != other.document_id)
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (term_id != other.term_id)
			return false;
		if (tf != other.tf)
			return false;
		return true;
	}


}
