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
	int    comments = -1;    // 评论数目
	int    jointPeople = -1; //参与人数
	int    hot = -1;	
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
		//  sports.163.com\11\1215\10\7LAC9H8800052DT2.html 新闻body变化
		//   <div class="d_info clearfix">

			
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
	
	public void getAllInfoWangYi163(String content) {	
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		 //Elements metas = document.getElementsByTag("meta");  // 元数据
		 //description = metas.get(3).attr("content");  // 元数据中的描述
		 Elements keywordEs =document.getElementsByAttributeValue("name", "keywords");
		 keywords = " ";  // 由于有的网页有多个keywords 和description ，这里选择最长的那个keywords 和description
		 String temp;
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(keywords.length() < temp.length())
				 keywords = temp;
		 }
		 keywordEs = document.getElementsByAttributeValue("name", "description");
		 description = " ";  // 由于有的网页有多个keywords 和description ，这里选择最长的那个keywords 和description
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(description.length() < temp.length())
				 description = temp;
		 }
		 System.out.println("description: "+description);
		 print(keywords);
		 //print(url);
		//  新闻正文在id为endText的div中，所以先获取该div在获取div中的p
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
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		//print(body);
		// time 
		
		Elements times = document.getElementsByAttributeValue("class", "ep-time-soure cDGray");
		if(times== null ||times.size() == 0 || times.get(0).text().contains("-")==false) times = document.getElementsByAttributeValue("class", "date");
		if(times==null || times.size() == 0 || times.get(0).text().contains("-")==false) times = document.getElementsByAttributeValue("class", "left");

		// 网易07年的时间 是   <div class="text">2007-06-11 20:09:33　来源:
		// 09 年的时候变成  <span class="info">2009-03-28 13:17:48　来源: 
		// 注意 09 年的时候 info 和text 的标签都是存在的
//		if(times.size() == 0) 
//			times = document.getElementsByAttributeValue("class", "text");
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByAttributeValue("class", "info");
//		// 10 年的时候  <cite>2010-01-19 23:53:37　来源: 
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
	
	// 人民网  
	public void getAllInfoPeople(String content) {	
		document =Jsoup.parse(content);
		title = document.title();		// title
		print(title);
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		 //Elements metas = document.getElementsByTag("meta");  // 元数据
		 //description = metas.get(3).attr("content");  // 元数据中的描述
		 Elements keywordEs =document.getElementsByAttributeValue("name", "keywords");
		 keywords = " ";  // 由于有的网页有多个keywords 和description ，这里选择最长的那个keywords 和description
		 String temp;
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(keywords.length() < temp.length())
				 keywords = temp;
		 }
		 keywordEs = document.getElementsByAttributeValue("name", "description");
		 description = " ";  // 由于有的网页有多个keywords 和description ，这里选择最长的那个keywords 和description
		 for(int i = 0;i< keywordEs.size();i++){
			 temp = keywordEs.get(i).attr("content");
			 if(description.length() < temp.length())
				 description = temp;
		 }
		 System.out.println("description: "+description);
		 print(keywords);
		 //print(url);
		//  新闻正文在id为p_content的div中，所以先获取该div在获取div中的p
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
			 body+= p.text().trim(); // 去除其他空白字符
	    }
		//print(body);
		// time 
		
		//Elements times = document.getElementsByAttributeValue("class", "left");
		//if(times==null || times.size() == 0) times = document.getElementsByAttributeValue("class", "date");
		// 网易07年的时间 是   <div class="text">2007-06-11 20:09:33　来源:
		// 09 年的时候变成  <span class="info">2009-03-28 13:17:48　来源: 
		// 注意 09 年的时候 info 和text 的标签都是存在的
//		if(times.size() == 0) 
//			times = document.getElementsByAttributeValue("class", "text");
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByAttributeValue("class", "info");
//		// 10 年的时候  <cite>2010-01-19 23:53:37　来源: 
//		if(times.size() == 0 || times.get(0).text().contains("-")== false) 
//			times = document.getElementsByTag("cite");
		//System.out.println( times.size());
		//Element time = times.get(0);
		Element time = document.getElementById("p_publishtime");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
	
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
	
	// 分析腾讯体育的数据
	public void getAllInfoTencent(String content) {
		printFlag = true;
		document =Jsoup.parse(content);
		title = document.title();		// title
		if(title.startsWith("404")){
			hot = -3; return;
		}
		// ||
		if(title.startsWith( "高清")||title.contains("(图)") ||title.startsWith( "组图")){
			//hot = -3; return;
		}
		print(title);
		
//		Elements links = document.getElementsByTag("link");
//		System.out.println(links.size());
//		for(Element link: links){
//			System.out.println(link.attr("href"));
//		}
//		 url   = links.get(0).attr("href");	//url html中所有连接的第一个应该是本网页自己的链接
		
		 // 元数据中的描述
		 // 元数据中的keywords
		 keywords = document.getElementsByAttributeValue("name", "keywords").get(0).attr("content");
		 description = document.getElementsByAttributeValue("name", "description").get(0).attr("content");
		 //url     = metas.get(7).attr("content");
		 System.out.println("keywords: "+keywords);
		 System.out.println("description: "+description);
		 //print(url);
		//  新闻正文在id为contextText的div中，所以先获取该div在获取div中的p
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
	    		body+= p.text().trim(); // 去除其他空白字符
	    	}
	    	print(body);
	    	// time 
	    }
	  
	
		// 14年 腾讯 <span class="article-time">2014-01-01 06:00</span>
		Elements times = document.getElementsByAttributeValue("class","article-time");
		if(times == null || times.size()==0) times = document.getElementsByAttributeValue("class","pubTime");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		System.out.println(times.size());
		String tt="";
		// 如果还为0，那么时间就使用文件名称
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
				simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
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
