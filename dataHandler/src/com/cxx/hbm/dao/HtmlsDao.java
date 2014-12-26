package com.ir.hbm.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ir.hbm.bean.HtmlsBean;
import com.ir.hbm.util.HibernateSessionFactory;

public class HtmlsDao extends BaseDAO<HtmlsBean> {

	//分页查询
	@SuppressWarnings("unchecked")
	public List<HtmlsBean> getPageRecord(int pageBegin, int pageSize){
		final String hql = "from HtmlsBean";  
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			Query query= session.createQuery(hql);
			query.setMaxResults(pageSize);
			query.setFirstResult(pageBegin);
			return query.list();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
	}
	
	//统计记录数
	public long countRecord(){
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			Long count = null;
			count = (Long)session.createQuery("select count(id) from HtmlsBean" ).uniqueResult();
			if(count!=null)return count;
			else return 0;
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
	}
}
