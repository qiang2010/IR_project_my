package com.ir.hbm.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ir.hbm.bean.BodyTermBean;
import com.ir.hbm.util.HibernateSessionFactory;

public class BodyTermDao extends BaseDAO<BodyTermBean> {
	
	//批量插入
	public void insertVolumn(List<BodyTermBean> termlist) {
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			session.clear();
			for(BodyTermBean term:termlist){
				session.save(term);
				//session.flush();
			}
			session.getTransaction().commit();
			System.out.println("succeed");
		} catch (Exception e) {
			System.out.println(e);
			session.getTransaction().rollback();
			System.out.println("Failed!");
		} finally {
			session.close();
		}
	}
	
	//得到ID最大值
	public long getMax(){
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			Long maxid = null;
			maxid = (Long)session.createQuery("select max(term_id) from BodyTermBean" ).uniqueResult();
			if(maxid!=null)return maxid;
			else return 1;
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
	}
	


}
