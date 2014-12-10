package com.qiang.manager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
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
	int    comments = -1;    // ������Ŀ
	int    jointPeople = -1; //��������
	int    hot = -1;	
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
	
	//  china news 
	public void getAllInfoChinaNews(String content) {	
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
		//  sports.163.com\11\1215\10\7LAC9H8800052DT2.html ����body�仯
		//   <div class="d_info clearfix">

			
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
	
	public void getAllInfoWangYi163(String content) {	
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		 //Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 //description = metas.get(3).attr("content");  // Ԫ�����е�����
		 Elements keywordEs =document.getElementsByAttributeValue("name", "keywords");
		 keywords = " ";  // �����е���ҳ�ж��keywords ��description ������ѡ������Ǹ�keywords ��description
		 String temp;
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(keywords.length() < temp.length())
				 keywords = temp;
		 }
		 keywordEs = document.getElementsByAttributeValue("name", "description");
		 description = " ";  // �����е���ҳ�ж��keywords ��description ������ѡ������Ǹ�keywords ��description
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(description.length() < temp.length())
				 description = temp;
		 }
		 System.out.println("description: "+description);
		 print(keywords);
		 //print(url);
		//  ����������idΪendText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		Element contentText = document.getElementById("endText");	
		Elements conts;
		if(contentText == null ){
			conts = document.getElementsByAttributeValue("class", "d_info clearfix");
			if (conts == null || conts.size()==0) conts = document.getElementsByAttributeValue("class", "bd");
			System.out.println(conts.size());
			contentText = conts.get(0);
		}
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
	    }
		//print(body);
		// time 
		
		Elements times = document.getElementsByAttributeValue("class", "ep-time-soure cDGray");
		if(times== null ||times.size() == 0 || times.get(0).text().contains("-")==false) times = document.getElementsByAttributeValue("class", "date");
		if(times==null || times.size() == 0 || times.get(0).text().contains("-")==false) times = document.getElementsByAttributeValue("class", "left");

		// ����07���ʱ�� ��   <div class="text">2007-06-11 20:09:33����Դ:
		// 09 ���ʱ����  <span class="info">2009-03-28 13:17:48����Դ: 
		// ע�� 09 ���ʱ�� info ��text �ı�ǩ���Ǵ��ڵ�
//		if(times.size() == 0) 
//			times = document.getElementsByAttributeValue("class", "text");
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByAttributeValue("class", "info");
//		// 10 ���ʱ��  <cite>2010-01-19 23:53:37����Դ: 
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByTag("cite");
		System.out.println( times.size());
		Element time = times.get(0);
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tt = time.text().trim();
		System.out.println(tt+"-=========");
		String []fields = tt.split("\\s{1,}");
		//System.out.println(tt);  // 2013-12-20T06:30:00+08:00
//		String day = time.attr("content");
//		String second ;
//		String []hours =  day.split("T");
//		day = hours[0].trim();
//		 second = hours[1].trim();
//		String last = day+" "+second.substring(0, 8);
		try {
			date = simpleTimeFormat.parse(fields[0].trim()+" "+fields[1].trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("===========%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%======");
			return ;
		}
	    timeStamp = new Timestamp(date.getTime());  // unix timestamp
	    //print(timeStamp.toString());
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}
	
	// ������  
	public void getAllInfoPeople(String content) {	
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		 //Elements metas = document.getElementsByTag("meta");  // Ԫ����
		 //description = metas.get(3).attr("content");  // Ԫ�����е�����
		 Elements keywordEs =document.getElementsByAttributeValue("name", "keywords");
		 keywords = " ";  // �����е���ҳ�ж��keywords ��description ������ѡ������Ǹ�keywords ��description
		 String temp;
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(keywords.length() < temp.length())
				 keywords = temp;
		 }
		 keywordEs = document.getElementsByAttributeValue("name", "description");
		 description = " ";  // �����е���ҳ�ж��keywords ��description ������ѡ������Ǹ�keywords ��description
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(description.length() < temp.length())
				 description = temp;
		 }
		 System.out.println("description: "+description);
		 print(keywords);
		 //print(url);
		//  ����������idΪp_content��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		Element contentText = document.getElementById("p_content");	
		Elements conts;
		if(contentText == null ){
			conts = document.getElementsByAttributeValue("class", "d_info clearfix");
			if (conts == null || conts.size()==0) conts = document.getElementsByAttributeValue("class", "bd");
			System.out.println(conts.size());
			contentText = conts.get(0);
		}
	    Elements ps = contentText.getElementsByTag("p");
	    body = "";  //  body
		for(Element p:ps){
			 body+= p.text().trim(); // ȥ�������հ��ַ�
	    }
		//print(body);
		// time 
		
		//Elements times = document.getElementsByAttributeValue("class", "left");
		//if(times==null || times.size() == 0) times = document.getElementsByAttributeValue("class", "date");
		// ����07���ʱ�� ��   <div class="text">2007-06-11 20:09:33����Դ:
		// 09 ���ʱ����  <span class="info">2009-03-28 13:17:48����Դ: 
		// ע�� 09 ���ʱ�� info ��text �ı�ǩ���Ǵ��ڵ�
//		if(times.size() == 0) 
//			times = document.getElementsByAttributeValue("class", "text");
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByAttributeValue("class", "info");
//		// 10 ���ʱ��  <cite>2010-01-19 23:53:37����Դ: 
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByTag("cite");
		//System.out.println( times.size());
		//Element time = times.get(0);
		Element time = document.getElementById("p_publishtime");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
	
		//System.out.println(tt);  // 2013-12-20T06:30:00+08:00
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
	    //print(timeStamp.toString());
		//Data d = new DataTime(time.attr("content"));
		//System.out.println(document.title());
		 
		checkIfValid();
	}
	
	// ������Ѷ����������
	public void getAllInfoTencent(String content) {
		printFlag = true;
		document =Jsoup.parse(content);
		title = document.title();		// title
		if(title.startsWith("404")){
			hot = -3; return;
		}
		// ||
		if(title.startsWith( "����")||title.contains("(ͼ)") ||title.startsWith( "��ͼ")){
			//hot = -3; return;
		}
		print(title);
		
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html���������ӵĵ�һ��Ӧ���Ǳ���ҳ�Լ�������
		
		 // Ԫ�����е�����
		 // Ԫ�����е�keywords
		 keywords = document.getElementsByAttributeValue("name", "keywords").get(0).attr("content");
		 description = document.getElementsByAttributeValue("name", "description").get(0).attr("content");
		 //url     = metas.get(7).attr("content");
		 System.out.println("keywords: "+keywords);
		 System.out.println("description: "+description);
		 //print(url);
		//  ����������idΪcontextText��div�У������Ȼ�ȡ��div�ڻ�ȡdiv�е�p
		 //  content id  1. Cnt-Main-Article-QQ
		Element contentText = document.getElementById("Cnt-Main-Article-QQ"); //Cnt-Main-Article-QQ
		if(contentText == null) {
		//	
			System.out.println( contentText);
			contentText = document.getElementById("C-Main-Article-QQ"); //C-Main-Article-QQ
			if(contentText == null)
				contentText = document.getElementById("infoTxt"); //C-Main-Article-QQ
		}
		Elements ps=null;
		if(contentText != null){
			ps = contentText.getElementsByTag("p");
			if(ps == null || ps.size()==0) ps = contentText.getElementsByTag("P");
		}
	    
	    if(ps != null && ps.size()!=0){
	    	body = "";  //  body
	    	for(Element p:ps){
	    		body+= p.text().trim(); // ȥ�������հ��ַ�
	    	}
	    	print(body);
	    	// time 
	    }
	  
	
		// 14�� ��Ѷ <span class="article-time">2014-01-01 06:00</span>
		Elements times = document.getElementsByAttributeValue("class","article-time");
		if(times == null || times.size()==0) times = document.getElementsByAttributeValue("class","pubTime");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		System.out.println(times.size());
		String tt="";
		// �����Ϊ0����ôʱ���ʹ���ļ�����
		if(times == null || times.size()==0) {
			hot = -4;
		}
		else {
			Element time = times.get(0);	
			//System.out.println(time.text());  // 2013-12-20T06:30:00+08:00
			tt =	time.text();
			try {
				date = simpleTimeFormat.parse(tt);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
				try {
					date = simpleTimeFormat.parse(tt);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		    timeStamp = new Timestamp(date.getTime());  // unix timestamp
		    //print(timeStamp.toString());
		}

//		if (time == null){ 
//		  times = document.getElementsByAttributeValue("itemprop", "datePublished");//
//		  //System.out.println(times.size());
//		  time = times.get(0);
//		}
	
		
//		String day = time.attr("content");
//		String second ;
//		String []hours =  day.split("T");
//		day = hours[0].trim();
//		 second = hours[1].trim();
//		String last = day+" "+second.substring(0, 8);


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
//		try {
//			String code = getEncoding(description);
//			description =  new String(description.getBytes(code),"UTF8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(title!=null && title.length()>128){
			title = title.substring(0,127);
		}
		if(description!=null && description.length()>128) 
			description = description.substring(0,127);
		if(keywords!=null && keywords.length()> 64)
			keywords = keywords.substring(0,63);
	}
	 public static String getEncoding(String str) {    
         String encode = "GB2312";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s = encode;    
                return s;    
             }    
         } catch (Exception exception) {    
         }    
         encode = "ISO-8859-1";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s1 = encode;    
                return s1;    
             }    
         } catch (Exception exception1) {    
         }    
         encode = "UTF-8";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s2 = encode;    
                return s2;    
             }    
         } catch (Exception exception2) {    
         }    
         encode = "GBK";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s3 = encode;    
                return s3;    
             }    
         } catch (Exception exception3) {    
         }    
        return "";    
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
