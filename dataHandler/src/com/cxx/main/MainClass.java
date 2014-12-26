package com.ir.main;
/**
 * @author chenxiaoxu
 * @version 2014/12/9
 */
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;

import com.ir.hbm.bean.BodyTermBean;
import com.ir.hbm.bean.HtmlsBean;
import com.ir.hbm.dao.BodyTermDao;
import com.ir.hbm.dao.HtmlsDao;
import com.ir.hbm.util.HibernateSessionFactory;
import com.ir.wordsegment.WordSegmentation;

public class MainClass {
	
	public static final int pageSize = 1000;
	
	public static void main(String[] args) throws IOException {
		HtmlsDao htmlsdao = new HtmlsDao();
		BodyTermDao termdao = new BodyTermDao();
		long countID = termdao.getMax();
		long recordNum = htmlsdao.countRecord();	
		int pageBegin = 1;
		List<BodyTermBean> termlist = new ArrayList<BodyTermBean>();
		do{
			if(!termlist.isEmpty()) termlist.clear();
			List<HtmlsBean> htmllist = htmlsdao.getPageRecord(pageBegin, pageSize);
			for(HtmlsBean htmlsbean: htmllist){
				long document_id = htmlsbean.getId();
				String body = htmlsbean.getBody();
				Map<String, Integer> bodymap = WordSegmentation.getWordsMap(body);
				for (Entry<String, Integer> entry : bodymap.entrySet()) {
					BodyTermBean bodyterm = new BodyTermBean(); 
					countID++;
			        //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			        bodyterm.setTerm(entry.getKey());
			        bodyterm.setTf(entry.getValue());
			        bodyterm.setDocument_id(document_id);
			        bodyterm.setTerm_id(countID);
			        termlist.add(bodyterm);
			    }
			}
			System.out.println(termlist.size());
			termdao.insertVolumn(termlist);
			recordNum -= pageSize;
			pageBegin += pageSize;
		}while(recordNum>0);
		
		
	}
}
