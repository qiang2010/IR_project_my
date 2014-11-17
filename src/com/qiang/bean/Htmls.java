package com.qiang.bean;

import java.sql.Timestamp;


/**
 * Htmls entity. @author MyEclipse Persistence Tools
 */

public class Htmls  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private Integer type;
     private String title;
     private String url;
     private Timestamp timestamp;
     private String description;
     private String body;
     private Integer commentsNum;
     private Integer people;
     private String keyWords;


    // Constructors

    /** default constructor */
    public Htmls() {
    }

    
    /** full constructor */
    public Htmls(Integer type, String title, String url, Timestamp timestamp, String description, String body, Integer commentsNum, Integer people, String keyWords) {
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

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return this.body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public Integer getCommentsNum() {
        return this.commentsNum;
    }
    
    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Integer getPeople() {
        return this.people;
    }
    
    public void setPeople(Integer people) {
        this.people = people;
    }

    public String getKeyWords() {
        return this.keyWords;
    }
    
    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }
   








}