package com.qiang.manager;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import com.qiang.bean.Htmls;

public class HtmlDetailManager {

	final static  int SOHU = 1;
	final static  int  SINA  = 2;
	final static  int  WanyYi  = 3;
	final static  int  Tencent  = 4;
	final static  int  People  = 5;
	
	static SimpleDateFormat simpleTimeFormat;
	static Date date;
	public static void main(String[] args) {

			   
		// TODO Auto-generated method stub
		//String dir="D:\\git\\IR_Data\\sports.sohu.com";
		//String dir="D:\\git\\IR_Data\\体育新闻HTML-demo\\wangyi163";
		//String dir="D:\\git\\IR_Data\\体育新闻HTML-demo\\sina";
		String dir="D:\\git\\IR_Data\\三个新闻\\sports.163.com";
		dir = "D:\\git\\IR_Data\\三个新闻\\sports.163.com\\11\\1215";
		dir = "D:\\git\\IR_Data\\三个新闻\\sports.163.com\\14";
		dir = "D:\\git\\IR_Data\\三个新闻\\sports.qq.com\\2014";
		String basedir = "D:\\git\\IR_Data\\三个新闻\\sports.163.com\\14_2\\";
		basedir = "D:\\git\\IR_Data\\sports.people\\n\\2014\\";
		basedir = "D:\\git\\IR_Data\\sports.qq\\a\\";
		basedir = "F:\\sports.qq\\a\\";
	    int ddd = 20140701; //String zero = "";
	 for( int jj = ddd;jj<=20141109;jj++){
		 
		 //if(jj<1000) zero ="0"; else zero = "";
		 dir = basedir +jj;		
		String fileName;
		   Configuration conf = new Configuration().configure();
					SessionFactory sf = conf.buildSessionFactory(new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry());
					Session sess = sf.openSession();
					// 开始事务
					Transaction tx = sess.beginTransaction();
		ArrayList<String> allHtmlFileNames = GetAllHtmlFileNames.getAllHtmls(dir);
		if (allHtmlFileNames == null) continue;
		System.out.println(allHtmlFileNames.size());
		Htmls html;
		int type =4;
		// allHtmlFileNames.size()
		for(int i = 0;i<allHtmlFileNames.size(); i++){
			GetInforFormHtml getInfor = new GetInforFormHtml();
			fileName = allHtmlFileNames.get(i);
			System.out.println(fileName);
			//try {
				switch(type){
				   case SOHU:getInfor.getAllInfoSOHU(getInfor.getHtmlFileContent(fileName)); break;
				   case SINA:getInfor.getAllInfoSINA(getInfor.getHtmlFileContent(fileName)); break;
				   case WanyYi: getInfor.getAllInfoWangYi163(getInfor.getHtmlFileContent(fileName));break;
				   case Tencent: getInfor.getAllInfoTencent(getInfor.getHtmlFileContent(fileName));break;
				   case People: getInfor.getAllInfoPeople(getInfor.getHtmlFileContent(fileName));break;
				} 
			//} catch (Exception e) {
				// TODO: handle exception
				//continue;
			//	System.out.println("++++++++++++++");
			//}
		  if(getInfor.hot == -3){ 
			  System.out.println("continue");
			  continue;
		  
		  }
			System.out.println("i:  " + i +"  jj: "+jj);
			html = new Htmls();	
			html.setType(type);
			if(getInfor.url == null || getInfor.url.length()< 5)
				html.setUrl(fileName);
			else
				html.setUrl(getInfor.url);
			html.setTitle( getInfor.title);
			
			html.setDescription(getInfor.description);
			html.setBody(getInfor.body);
			if(getInfor.hot == -4){
				String tt = jj+" 00:00";
				Timestamp   timeStamp; 	// 发布时间
			   date=null;
			   simpleTimeFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
				try {
					date = simpleTimeFormat.parse(tt);
				    timeStamp = new Timestamp(date.getTime());  // unix timestamp
					html.setTimestamp(timeStamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
				html.setTimestamp(getInfor.timeStamp);
			int comm  = getInfor.comments == -1	? randomNum(10000):getInfor.comments ;
			int pep   = getInfor.jointPeople == -1? randomNum(10000):getInfor.jointPeople;
			int hot   = getInfor.hot == -1?  comm + pep :getInfor.hot;
			
			html.setHot(hot);   
			html.setCommentsNum(comm);  /// 随机生成评论数 
			html.setPeople(pep);	
			html.setKeyWords(getInfor.keywords);	
			//System.out.println("Qiang:***"+getInfor.url);	
			
			sess.save(html);
		}	
		tx.commit();
		sess.close();
		sf.close(); }
	    // 这里开始对数据库的读写	
		//fileName = "D:\\git\\IR_Data\\sports.sohu.com\\20141110\\n405912634.shtml";
		
	
	}
private static int randomNum(int max){
	return (int)(Math.random()*max);
	}
	
}
