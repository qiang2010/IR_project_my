package com.qiang.manager;

import java.util.ArrayList;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import com.qiang.bean.Htmls;

public class HtmlDetailManager {

	final static  int  SOHU = 1;
	final static  int  SINA  = 2;
	final static  int  WanyYi  = 3;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration().configure();
		SessionFactory sf = conf.buildSessionFactory(new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry());
		Session sess = sf.openSession();
		// 开始事务
		Transaction tx = sess.beginTransaction();
		//String dir="D:\\git\\IR_Data\\sports.sohu.com";
		//String dir="D:\\git\\IR_Data\\体育新闻HTML-demo\\wangyi163";
		String dir="D:\\git\\IR_Data\\体育新闻HTML-demo\\sina";
		
		String fileName;
		ArrayList<String> allHtmlFileNames = GetAllHtmlFileNames.getAllHtmls(dir);
		GetInforFormHtml getInfor = new GetInforFormHtml();
		Htmls html;
		int type =2;
		// allHtmlFileNames.size()
		for(int i = 0;i<allHtmlFileNames.size(); i++){
			fileName = allHtmlFileNames.get(i);
			System.out.println(fileName);
			switch(type){
			   case SOHU:getInfor.getAllInfoSOHU(getInfor.getHtmlFileContent(fileName)); break;
			   case SINA:getInfor.getAllInfoSINA(getInfor.getHtmlFileContent(fileName)); break;
			   case WanyYi: getInfor.getAllInfoWangYi163(getInfor.getHtmlFileContent(fileName));break;
			} 		  
			html = new Htmls();	
			html.setType(type);
			if(getInfor.url == null || getInfor.url.length()< 5)
				html.setUrl(fileName);
			else
				html.setUrl(getInfor.url);
			html.setTitle( getInfor.title);
			
			html.setDescription(getInfor.description);
			html.setBody(getInfor.body);
			html.setTimestamp(getInfor.timeStamp);
			int comm  =getInfor.comments == -1	? randomNum(10000):-1;
			int pep = getInfor.jointPeople == -1? randomNum(10000):-1;
			
			html.setCommentsNum(comm);  /// 随机生成评论数 
			html.setPeople(pep);	
			html.setKeyWords(getInfor.keywords);	
			System.out.println("Qiang:***"+getInfor.url);	
			
			sess.save(html);
		}
	    // 这里开始对数据库的读写	
		//fileName = "D:\\git\\IR_Data\\sports.sohu.com\\20141110\\n405912634.shtml";
		tx.commit();
		sess.close();
		sf.close();
	} 
private static int randomNum(int max){
	return (int)(Math.random()*max);
	}
	
}
