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
 *  ��ȡһ��html�ļ�����ȡ��Ӧ��Ϣ������HtmlInforDetail�Ķ�����
 *  sohu sports
 */
public class GetInforFormHtml {

	File file;
	//FileReader fileReader ;
	BufferedReader bufferedReader;	
	Document document;
	int type;            // ��Ǹ�HTML��Ϣ��Դ���ĸ���վ
	String title;  		// ����
	Timestamp   timeStamp; 	// ����ʱ��
	int    comments;    // ������Ŀ
	int    jointPeople; //��������
	String body;      	// ������������
	String body_40Words;
	String description; // Ԫ�����е�
	String keywords;    // Ԫ�����е�
	String url;      	// ��ǰ��ҳ��URL
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		String fileName = "D:\\git\\IR_Data\\sports.sina.com\\1.htm";
		fileName = "D:\\git\\IR_Data\\��������\\sports.qq.com\\a\\20141111\\100000.htm";
		//fileName = "D:\\git\\IR_Data\\sports.sohu.com\\20141110\\n405912634.shtml";
    	GetInforFormHtml getInforFormHtml = new GetInforFormHtml();
		 getInforFormHtml.getAllInfoTencent(getInforFormHtml.getHtmlFileContent(fileName));
		//System.out.println(detail.toString());
	}
	Date date;
	// ��ȡһ��html�е���Ϣ
	
	public void getAllInfoSOHU(String content) {
		document =Jsoup.parse(content);
		title = document.title();		// title 
		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 description = metas.get(2).attr("content");  // Ԫ�����е�����
		 keywords =    metas.get(1).attr("content");   // Ԫ�����е�keywords
			
		//  ����������idΪcontextText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		Element contentText = document.getElementById("contentText");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
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
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 description = metas.get(3).attr("content");  // Ԫ�����е�����
		 keywords =    metas.get(2).attr("content");   // Ԫ�����е�keywords
		 url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 print(url);
		//  ����������idΪcontextText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		Element contentText = document.getElementById("artibody");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
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
	
	
	// ������Ѷ����������
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
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 description = metas.get(3).attr("content");  // Ԫ�����е�����
		 keywords =    metas.get(2).attr("content");   // Ԫ�����е�keywords
		 //url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 //print(url);
		//  ����������idΪcontextText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		 //  content id  1. Cnt-Main-Article-QQ
		Element contentText = document.getElementById("Cnt-Main-Article-QQ");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
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
	// �����������������
	public void getAllInfoIfeng(String content){
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 description = metas.get(3).attr("content");  // Ԫ�����е�����
		 keywords =    metas.get(2).attr("content");   // Ԫ�����е�keywords
		 url     = metas.get(7).attr("content");
		 print(description);
		 print(keywords);
		 print(url);
		//  ����������idΪcontextText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		Element contentText = document.getElementById("artibody");	
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
	    }
		print(body);
		// time 
		Element time = document.getElementById("pub_date");
//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
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
	// �����������Լ����ȵ����� 
	//  url  varchar 128
	//  title varchar 64
	//  desc  varchar 128
	//	body longtext	
	//  keywords varchar 64
	// ������������Ƿ����㳤��Ҫ�󣬷����ȡǰ�沿��
	private void checkIfValid(){
		if(title!=null && title.length()>64){
			title = title.substring(0,63);
		}
		if(description!=null && description.length()>128) 
			description = description.substring(0,127);
		if(keywords!=null && keywords.length()> 64)
			keywords = keywords.substring(0,63);
	}
	
	// ��ȡһ��html�ļ�
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
