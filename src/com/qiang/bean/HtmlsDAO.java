package com.qiang.bean;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for Htmls
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see com.qiang.bean.Htmls
 * @author MyEclipse Persistence Tools
 */
public class HtmlsDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory.getLogger(HtmlsDAO.class);
	// property constants
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String DESCRIPTION = "description";
	public static final String BODY = "body";
	public static final String COMMENTS_NUM = "commentsNum";
	public static final String PEOPLE = "people";
	public static final String HOT = "hot";
	public static final String KEY_WORDS = "keyWords";

	public void save(Htmls transientInstance) {
		log.debug("saving Htmls instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Htmls persistentInstance) {
		log.debug("deleting Htmls instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Htmls findById(java.lang.Integer id) {
		log.debug("getting Htmls instance with id: " + id);
		try {
			Htmls instance = (Htmls) getSession().get("com.qiang.bean.Htmls",
					id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Htmls instance) {
		log.debug("finding Htmls instance by example");
		try {
			List results = getSession().createCriteria("com.qiang.bean.Htmls")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Htmls instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Htmls as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	public List findByTitle(Object title) {
		return findByProperty(TITLE, title);
	}

	public List findByUrl(Object url) {
		return findByProperty(URL, url);
	}

	public List findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findByBody(Object body) {
		return findByProperty(BODY, body);
	}

	public List findByCommentsNum(Object commentsNum) {
		return findByProperty(COMMENTS_NUM, commentsNum);
	}

	public List findByPeople(Object people) {
		return findByProperty(PEOPLE, people);
	}

	public List findByHot(Object hot) {
		return findByProperty(HOT, hot);
	}

	public List findByKeyWords(Object keyWords) {
		return findByProperty(KEY_WORDS, keyWords);
	}

	public List findAll() {
		log.debug("finding all Htmls instances");
		try {
			String queryString = "from Htmls";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Htmls merge(Htmls detachedInstance) {
		log.debug("merging Htmls instance");
		try {
			Htmls result = (Htmls) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Htmls instance) {
		log.debug("attaching dirty Htmls instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Htmls instance) {
		log.debug("attaching clean Htmls instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}