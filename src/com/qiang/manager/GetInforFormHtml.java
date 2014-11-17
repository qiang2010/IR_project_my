package com.qiang.manager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
 *  读取一个html文件，抽取相应信息，放在HtmlInforDetail的对象中
 *  sohu sports
 */
public class GetInforFormHtml {

	File file;
	//FileReader fileReader ;
	BufferedReader bufferedReader;	
	Document document;
	int type;            // 标记该HTML信息来源是哪个网站
	String title;  		// 标题
	Timestamp   timeStamp; 	// 发布时间
	int    comments;    // 评论数目
	int    jointPeople; //参与人数
	String body;      	// 新闻内容主题
	String body_40Words;
	String description; // 元数据中的
	String keywords;    // 元数据中的
	String url;      	// 当前网页的URL
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		String fileName = "D:\\git\\IR_Data\\sports.sina.com\\1.htm";
		fileName = "D:\\git\\IR_Data\\爬虫数据\\sports.qq.com\\a\\20141111\\100000.htm";
		//fileName = "D:\\git\\IR_Data\\sports.sohu.com\\20141110\\n405912634.shtml";
    	GetInforFormHtml getInforFormHtml = new GetInforFormHtml();
		 getInforFormHtml.getAllInfoTencent(getInforFormHtml.getHtmlFileContent(fileName));
		//System.out.println(detail.toString());
	}
	Date date;
	// 获取一个html中的信息
	
	public void getAllInfoSOHU(String content) {
		document =Jsoup.parse(content);
		title = document.title();		// title 
		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		Elements metas = document.getElementsByTag("meta");  // 元数据
		 description = metas.get(2).attr("content");  // 元数据中的描述
		 keywords =    metas.get(1).attr("content");   // 元数据中的keywords
			
		//  新闻正文在id为contextText的div中，所以先获取该div在获取div中的p
		Element contentText = document.getElementById("contentText");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		// time 
		Element time = document.getElementById("pubtime_baidu");
		Elements times;
		if (time == null){ 
		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
		  //System.out.println(times.size());
		  time = times.get(0);
		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(time.attr("content"));  // 2013-12-20T06:30:00+08:00
		String day = time.attr("content");
		String second ;
		String []hours =  day.split("T");
		day = hours[0].trim();
		 second = hours[1].trim();
		String last = day+" "+second.substring(0, 8);
		try {
			date = simpleTimeFormat.parse(last);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    timeStamp = new Timestamp(date.getTime());  // unix timestamp
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}

	boolean printFlag = true;
	public void getAllInfoSINA(String content) {	
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		Elements metas = document.getElementsByTag("meta");  // 元数据
		 description = metas.get(3).attr("content");  // 元数据中的描述
		 keywords =    metas.get(2).attr("content");   // 元数据中的keywords
		 url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 print(url);
		//  新闻正文在id为contextText的div中，所以先获取该div在获取div中的p
		Element contentText = document.getElementById("artibody");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		System.out.println(time.text());  // 2013-12-20T06:30:00+08:00
//		String day = time.attr("content");
//		String second ;
//		String []hours =  day.split("T");
//		day = hours[0].trim();
//		 second = hours[1].trim();
//		String last = day+" "+second.substring(0, 8);
		try {
			date = simpleTimeFormat.parse(time.text());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    timeStamp = new Timestamp(date.getTime());  // unix timestamp
	    print(timeStamp.toString());
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}
	
	
	// 分析腾讯体育的数据
	public void getAllInfoTencent(String content){
		printFlag = true;
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		Elements metas = document.getElementsByTag("meta");  // 元数据
		 description = metas.get(3).attr("content");  // 元数据中的描述
		 keywords =    metas.get(2).attr("content");   // 元数据中的keywords
		 //url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 //print(url);
		//  新闻正文在id为contextText的div中，所以先获取该div在获取div中的p
		 //  content id  1. Cnt-Main-Article-QQ
		Element contentText = document.getElementById("Cnt-Main-Article-QQ");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		System.out.println(time.text());  // 2013-12-20T06:30:00+08:00
//		String day = time.attr("content");
//		String second ;
//		String []hours =  day.split("T");
//		day = hours[0].trim();
//		 second = hours[1].trim();
//		String last = day+" "+second.substring(0, 8);
		try {
			date = simpleTimeFormat.parse(time.text());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    timeStamp = new Timestamp(date.getTime());  // unix timestamp
	    print(timeStamp.toString());
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}
	// 分析凤凰体育的数据
	public void getAllInfoIfeng(String content){
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		Elements metas = document.getElementsByTag("meta");  // 元数据
		 description = metas.get(3).attr("content");  // 元数据中的描述
		 keywords =    metas.get(2).attr("content");   // 元数据中的keywords
		 url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 print(url);
		//  新闻正文在id为contextText的div中，所以先获取该div在获取div中的p
		Element contentText = document.getElementById("artibody");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		System.out.println(time.text());  // 2013-12-20T06:30:00+08:00
//		String day = time.attr("content");
//		String second ;
//		String []hours =  day.split("T");
//		day = hours[0].trim();
//		 second = hours[1].trim();
//		String last = day+" "+second.substring(0, 8);
		try {
			date = simpleTimeFormat.parse(time.text());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    timeStamp = new Timestamp(date.getTime());  // unix timestamp
	    print(timeStamp.toString());
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}
	// 对数据类型以及长度的限制 
	//  url  varchar 128
	//  title varchar 64
	//  desc  varchar 128
	//	body longtext	
	//  keywords varchar 64
	// 检查下面属性是否满足长度要求，否则截取前面部分
	private void checkIfValid(){
		if(title!=null && title.length()>64){
			title = title.substring(0,63);
		}
		if(description!=null && description.length()>128) 
			description = description.substring(0,127);
		if(keywords!=null && keywords.length()> 64)
			keywords = keywords.substring(0,63);
	}
	
	// 读取一个html文件
	public String getHtmlFileContent(String name){
		StringBuilder content= new StringBuilder();
		String line;
		try {
			file = new File(name);
			bufferedReader = new BufferedReader(new FileReader(file));
			while(true){
				line =bufferedReader.readLine();
				if(line == null ) break;
				content.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			// TODO: handle exception
			if(bufferedReader!=null){
				try {
					bufferedReader.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}	
		return content.toString();
	}
	private void print(String tar){
		if(printFlag) System.out.println(tar);
	}
}
