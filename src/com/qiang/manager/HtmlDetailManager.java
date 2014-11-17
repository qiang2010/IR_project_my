package com.qiang.manager;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import com.qiang.bean.Htmls;

public class HtmlDetailManager {

	final static  int  SOHU = 1;
    
	final static  int SINA  = 2;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration().configure();
		SessionFactory sf = conf.buildSessionFactory(new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry());
		Session sess = sf.openSession();
		// 开始事务
		Transaction tx = sess.beginTransaction();
		String dir="D:\\git\\IR_Data\\sports.sohu.com";
		String fileName;
		ArrayList<String> allHtmlFileNames = GetAllHtmlFileNames.getAllHtmls(dir);
		GetInforFormHtml getInfor = new GetInforFormHtml();
		Htmls html;
		int type =2;
		for(int i = 0;i<allHtmlFileNames.size(); i++){
			fileName = allHtmlFileNames.get(i);
			System.out.println(fileName);
			switch(type){
			   case SOHU:getInfor.getAllInfoSOHU(getInfor.getHtmlFileContent(fileName)); break;
			   case SINA:getInfor.getAllInfoSINA(getInfor.getHtmlFileContent(fileName)); break;
			} 		  
			html = new Htmls();	
			html.setType(SOHU);
			html.setUrl(getInfor.url);
			html.setTitle( getInfor.title);
			
			html.setDescription(getInfor.description);
			html.setBody(getInfor.body);
			html.setTimestamp(getInfor.timeStamp);
			html.setCommentsNum(-1);
			html.setPeople(-1);
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

}
