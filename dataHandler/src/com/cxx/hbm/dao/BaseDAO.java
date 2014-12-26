package com.ir.hbm.dao;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.ir.hbm.util.HibernateSessionFactory;

/**
 * @author chenxiaoxu
 *
 * @param <T>
 */
public class BaseDAO<T> {

	public void create(T object) {
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();

			session.persist(object);

			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public void update(T object) {
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			session.update(object);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public void delete(T object) {
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();

		try {
			session.beginTransaction();
			session.delete(object);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public T find(Class<? extends T> clazz, Serializable id) {
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			return (T) session.get(clazz, id);
		} finally {
			session.getTransaction().commit();
			session.close();
		}
	}

	public Query execHql(String hql){
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			return  session.createQuery(hql);
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
	}
	
	public SQLQuery execSql(String sql){
		Session session = HibernateSessionFactory.getSessionFactory()
				.openSession();
		try {
			session.beginTransaction();
			return  session.createSQLQuery(sql);
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
	}
	
	
}
