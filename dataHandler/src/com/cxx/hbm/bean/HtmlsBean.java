package com.ir.hbm.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

public class HtmlsBean  implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5801590247341793893L;
	
	public HtmlsBean(){}
	

	public HtmlsBean(int id, int type, String title, String url,
			Timestamp timestamp, String description, String body,
			Integer commentsNum, Integer people, String keyWords) {
		super();
		this.id = id;
		this.type = type;
		this.title = title;
		this.url = url;
		this.timestamp = timestamp;
		this.description = description;
		this.body = body;
		this.commentsNum = commentsNum;
		this.people = people;
		this.keyWords = keyWords;
	}


	private int id;	
	private int type;
	private String title;
	private String url;
	private Timestamp timestamp;
    private String description;
    private String body;
    private Integer commentsNum;
    private Integer people;
    private String keyWords;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Integer getCommentsNum() {
		return commentsNum;
	}
	public void setCommentsNum(Integer commentsNum) {
		this.commentsNum = commentsNum;
	}
	public Integer getPeople() {
		return people;
	}
	public void setPeople(Integer people) {
		this.people = people;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result
				+ ((commentsNum == null) ? 0 : commentsNum.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((keyWords == null) ? 0 : keyWords.hashCode());
		result = prime * result + ((people == null) ? 0 : people.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + type;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		HtmlsBean other = (HtmlsBean) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (commentsNum == null) {
			if (other.commentsNum != null)
				return false;
		} else if (!commentsNum.equals(other.commentsNum))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (keyWords == null) {
			if (other.keyWords != null)
				return false;
		} else if (!keyWords.equals(other.keyWords))
			return false;
		if (people == null) {
			if (other.people != null)
				return false;
		} else if (!people.equals(other.people))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}


	

	

}
