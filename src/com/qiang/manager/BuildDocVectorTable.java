package com.qiang.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

 class BuildDocVectorTable {

	 static double MIN_tfidf = 0.1;
	/*
	 *  根据 term表构建文档向量
	 *  
	 *   INSERT into termidTest(term,df) (SELECT term,count(document_id) FROM bodyterm GROUP BY term)
	 */
	public static void main(String[] args) throws Exception{
		
		BuildDocVectorTable build = new BuildDocVectorTable();
		long start = System.currentTimeMillis();
		build.getTerm_and_df();
		build.get_bodyTerm();
		build.writeVectorIntoDatabase();
		long end = System.currentTimeMillis();
		System.out.println("花费时间：" + (end-start)/60000.0 + " min");
		
	}

	// 遍历一遍bodyterm，map用文档id作为key，值是一个map，是其向量
	HashMap<Integer,HashMap<Integer,Double>> bigVectorMap = new HashMap<Integer,HashMap<Integer,Double>> ();
	public  void get_bodyTerm() throws SQLException{
		Connection con = ConnectionUtil.getConnection();
//		con.setAutoCommit(false); 
		Statement stmt = con.createStatement();
		
		//part1-------------将bodyterm读入内存
		
		System.out.println("开始将bodyterm读入内存！");
		int DocNum = 110000; // 文档总数目
		int termSum = 13848493; // 13848493
		for(long row=0; row<= termSum; row+=200000){
			System.out.println(row);
			String all_terms_sql = "select term,document_id,tf from bodyterm limit "+row+",200000";
//			select * from bodyterm limit 100000,100000
			ResultSet all_terms_rs = stmt.executeQuery(all_terms_sql);
			int df;
			int tf;
			int termId;
			int docId;
			termInfo termIn;
			HashMap<Integer,Double> docID_tfidf;
			while(all_terms_rs.next()){
				 String term = all_terms_rs.getString(1);
				 docId = all_terms_rs.getInt(2);
				 termIn = allTerm_and_df.get(term); // 将词项 转换成id
				 termId = termIn.id;
				 df = termIn.df;
				 tf = all_terms_rs.getInt(3);
				 docID_tfidf = bigVectorMap.get(docId);
				 double tfidf = (1+Math.log((double)tf))*(Math.log(DocNum/df)); 
				 if(tfidf < MIN_tfidf) continue;
				 if(docID_tfidf == null){
					 docID_tfidf =new  HashMap<Integer,Double>();
					 docID_tfidf.put(termId, tfidf);
					 bigVectorMap.put(docId, docID_tfidf);				
				}else{
					 docID_tfidf.put(termId, tfidf);
					 bigVectorMap.put(docId, docID_tfidf);
				}
			}
			all_terms_rs.close();	
		}//end-for
		System.out.println("向量空间大小 ："+bigVectorMap.size());
		//System.out.println("bodyterm成功读入内存！");
	}
	
	// 获取termid表中的信息
	class termInfo{
		public termInfo(int id,int df) {
			// TODO Auto-generated constructor stub
			this.id = id;
			this.df = df;
		}
		int id;
		int df;
	}
	public void writeVectorIntoDatabase() throws SQLException{
		  Connection conn = ConnectionUtil.getConnection();
	      String insertSql = "insert into docVector values(?,?,?)";
	      //PreparedStatement pstmt = conn.prepareStatement(docIdSql);
	      PreparedStatement pstmt = conn.prepareStatement(insertSql);
	      Set<Integer> keyset = bigVectorMap.keySet();
	      Iterator<Integer> it = keyset.iterator();
	      HashMap<Integer, Double>  temp;
	      conn.setAutoCommit(false);  // 不自动提交
	      int key;
	      int count=0;
	      while(it.hasNext()){
	    	  count++;
	    	  key = it.next();
	    	  System.out.println(key);
	    	  temp = bigVectorMap.get(key);
	    	  pstmt.setInt(1, key);
	    	  pstmt.setInt(2, temp.size());	    	  
	    	  pstmt.setObject(3, normalize(temp));
	    	  //pstmt.execute();
	    	  pstmt.addBatch();
	    	  if(count %1000 == 0){
	    		  pstmt.executeBatch();
	    		  conn.commit();
	    		  pstmt.clearBatch();
	    	  }
	    		  
	    	  //
	      }
	      pstmt.executeBatch();
		  conn.commit();
	      conn.close();
		
	}
	
	// 将词项表中的所有信息读入到内存中， term id df
	HashMap<String,termInfo> allTerm_and_df = new HashMap<String,termInfo>();
	public  void getTerm_and_df() throws SQLException{
		if(allTerm_and_df !=null) allTerm_and_df.clear();
		else {
			allTerm_and_df = new HashMap<String,termInfo>();
		}
		Connection conn = ConnectionUtil.getConnection();
		System.out.println("开始将term读入内存！");
		Statement stmt = conn.createStatement();
		String sql = "select termid,term,df from termid";
		ResultSet rs =  stmt.executeQuery(sql);
		
		while(rs.next()){
			allTerm_and_df.put(rs.getString(2),new termInfo(rs.getInt(1), rs.getInt(3)));
		}
		System.out.println("term读入内存 ok！");
		System.out.println("term num" + allTerm_and_df.size());
		
		
		conn.close();
	}
//	//  向向量空间表格中插入 文档 对应的 向量
//	public static void buildDocVector() throws ClassNotFoundException, SQLException{
//		Connection conn = null;
//        String sql;
//        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
//        // 避免中文乱码要指定useUnicode和characterEncoding
//        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
//        // 下面语句之前就要先创建javademo数据库
// 
//        
//            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
//            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
//            Class.forName("com.mysql.jdbc.Driver") ;// 动态加载mysql驱动
// 
//            System.out.println("成功加载MySQL驱动程序");
//            conn = DriverManager.getConnection(url);
//            conn.setAutoCommit(false);  // 不自动提交
//            System.out.println("连接成功");
//            Statement stmt = conn.createStatement();
//            Statement stmt2 = conn.createStatement();
//            ResultSet rs;
//            // 首先获取文档id的范围 
//            ArrayList<Integer> docId = new ArrayList<Integer>();
//            String docIdSql = "select distinct document_id  from bodyterm ";
//            rs = stmt.executeQuery(docIdSql);
//            while(rs.next()){
//            	docId.add(rs.getInt(1)); // 获取所有的文档id
//            }
//            int DocNum = docId.size();  // 这里就是文档数量 N (1+Math.log((double)tf))*(Math.log(N/df));
//            
//            // 这里 先直接使用term，不转换为相应的id
//            HashMap<Integer, Double> vector  = new HashMap<Integer, Double>();
//            int currentDocId;
//            String docIdSqlBase = "select term,tf from bodyterm where document_id = ";
//            String insertSql = "insert into docVector values(?,?,?)";
//            //PreparedStatement pstmt = conn.prepareStatement(docIdSql);
//            PreparedStatement pstmt = conn.prepareStatement(insertSql);
//            String term;
//            int df;
//            int tf;
//            double tfidf;
//            ResultSet rs2;
//            for(int i = 0;i< DocNum;i++){ // 对每个文档先查询出他的所有词汇
//            	currentDocId = docId.get(i); 
//            	vector.clear();	
//            	docIdSql = docIdSqlBase + currentDocId;
//            	System.out.println(docIdSql);
//            	rs = stmt.executeQuery(docIdSql);
//            	int termId;
//            	int countTerm =0;
//            	while(rs.next()){ // 对该文档的每个词项，查询得到其 df值
//            		countTerm ++;
//            		term = rs.getString(1);
//            		//docIdSql = "SELECT COUNT(distinct document_id) from bodyterm WHERE term = '" +term+"'";
//            		docIdSql = "SELECT id,df from termId WHERE term = '" +term+"'";
//            		//System.out.println(docIdSql);
//            		rs2 = stmt2.executeQuery(docIdSql);
//            		if(rs2.next()){
//            			termId = rs2.getInt(1);
//            			df = rs2.getInt(2);
//                		tf = rs.getInt(2);
//                		tfidf = (1+Math.log((double)tf))*(Math.log(DocNum/df));
//                		if(tfidf > MIN_tfidf) 
//                			vector.put(termId,tfidf);	
//            		}      		
//            	}
//            	pstmt.setInt(1, currentDocId);
//            	pstmt.setInt(2, countTerm);
//            	//System.out.println(vector);
//            	pstmt.setObject(3, vector);
//            	//System.out.println(currentDocId + " :" +vector.toString());
//            	pstmt.addBatch();
//            }
//            pstmt.executeBatch();
//			conn.commit();
//			System.out.println("ok");
//            conn.close(); 
//	}
	// 2014 12 12 构建词汇表改版
	// 给定向量进行归一化操作
		public static HashMap<Integer, Double> normalize(HashMap<Integer, Double> vector){
			if(vector == null) return null;
			double length=0;
			Set<Integer> keyS = vector.keySet();
			Iterator<Integer> it = keyS.iterator();
			int k;
			while(it.hasNext()){
				k = it.next();
				length += Math.pow(vector.get(k),2);
			}
			length = Math.sqrt(length);
			it = keyS.iterator();
			//double test=0;
			while(it.hasNext()){
				k = it.next();
				vector.put(k,vector.get(k)/length);
				//test += Math.pow(vector.get(k),2);
			}
			//System.out.println(test);
			return vector;
		}
	public static void insertTermID() throws ClassNotFoundException, SQLException{
		Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver") ;// 动态加载mysql驱动
 
            System.out.println("成功加载MySQL驱动程序");
            conn = DriverManager.getConnection(url);
            System.out.println("连接成功");

            String termSql ;
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            ResultSet rs,rs2;
            // 首先构建 id term 表
            ArrayList<String> allTerms = new ArrayList<String>();
            //HashMap<Integer, HashMap<String, V>>
            
            // 将所有的词 插入到termId表里面去，首先获取termId表里面的最大的id
            termSql = "SELECT MAX(id) from TermID";
            rs = stmt.executeQuery(termSql);
            int maxTermId = 0;
            if(rs.next()){
            	maxTermId = rs.getInt(1);
            } 
            maxTermId ++;
            // == 到此我们获得了 TermID表中的最大的id，插入就从id +1 开始
            
            // 接下来就是要获取 所有要插入的词项
            termSql  = "select distinct term  from bodyterm";
            rs = stmt.executeQuery(termSql); 
            while(rs.next()){
            	allTerms.add(rs.getString(1));
            }
            termSql = "insert into TermID_copy values(?,?,?)";           
            // SELECT COUNT(document_id) from bodyterm WHERE term = '冠军'
            PreparedStatement pstmt = conn.prepareStatement(termSql);
            //rs = stmt.executeQuery(docIdSql);
            int termNum = allTerms.size();
            boolean  insertFlag= false;
            String term;
            String docIdSql;
            int df=0;
            
            for(int j = 0; j< termNum ; j++){
            	System.out.println(maxTermId);
            	pstmt.setInt(1, maxTermId); 
            	term = allTerms.get(j);  
            	docIdSql = "SELECT (document_id) from bodyterm WHERE term = '" +term+"'";
            	rs2 = stmt2.executeQuery(docIdSql);
            	rs2.last();
            	df = rs2.getRow();
//        		if(rs2.next()){
//        			df = rs2.getInt(1);
//        		}
        		rs2.close();
            	pstmt.setString(2, term);
            	pstmt.setInt(3, df);
            	pstmt.addBatch();
            	//pstmt.execute();
            	//System.out.println(insertFlag);
            	//if(insertFlag == false) continue;
            	maxTermId++;
            }
            pstmt.executeBatch();
			conn.commit();
			System.out.println("ok");
            conn.close(); 
	}
	// 聚类函数， 输入是需要聚类的文档列表
	// 返回是聚类后的类列表
//	
//	//PriorityQueue<E>
//	static  HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
//	public  ArrayList<ClusterDetail> cluster(ArrayList<Integer> doclist) throws ClassNotFoundException, SQLException{
//		boolean [] flag; // 用于标记那些文档已经被合并
//		// 输入的是文档集合是随机的，但是我们映射一下，在0位置上的就是文档 0对应的实际文档
//		ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
//		getAllVectors(doclist);
//		int docSize = doclist.size();
//		flag = new boolean[docSize];
//		for(int i=0;i<flag.length;i++) flag[i]=false;
//		double [][] simValues = new double[docSize][docSize];
//		ClusterDetail  temp;
//		// 初始化 docSize 个簇
//		for(int k =0;k<docSize;k++){
//			temp = new ClusterDetail();
//			temp.clusterCenter = vectors.get(doclist.get(k));
//			temp.title ="" ;
//			temp.clusterSize =1;
//			temp.docList.add(doclist.get(k));
//			result.add(temp);
//		}
//		for( int i = 0;i<docSize;i++)
//			for(int j = 0;j<docSize; j++){
//				if(i==j)simValues[i][j]=1;
//				else
//					simValues[i][j] = sim(vectors.get(doclist.get(i)),vectors.get(doclist.get(j)));
//			}
//		System.out.println(vectors.size());
//		return result;
//	}
//	public  double sim(ClusterDetail clu1,ClusterDetail clu2){
//		double simValue =0;
//		
//		return simValue;
//	}
//	// 当簇的维度过高的时候进行维度筛选
//	// 筛选原则是： 选择那些词项权重比较大的
//	public  void featureSelect(){
//		
//	}
//	
//	
//	// 给定两个文档求相似度
//	public  double sim(HashMap<Integer, Double> vector1,HashMap<Integer, Double> vector2){
//		//if(doc1 == doc2 ) return 1;
//		double simValue=0;
//		
//		int a,b;
//		Set<Integer> vector1Keys = vector1.keySet();
//		int size = vector1Keys.size();
//		Iterator<Integer> it = vector1Keys.iterator();
//		while(it.hasNext()){
//			a = it.next();
//			if(vector2.containsKey(a)){
//				simValue += vector1.get(a) * vector2.get(a);
//			}
//		}
//		return simValue;
//	}	
}
