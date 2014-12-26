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
	 *  ���� term�����ĵ�����
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
		System.out.println("����ʱ�䣺" + (end-start)/60000.0 + " min");
		
	}

	// ����һ��bodyterm��map���ĵ�id��Ϊkey��ֵ��һ��map����������
	HashMap<Integer,HashMap<Integer,Double>> bigVectorMap = new HashMap<Integer,HashMap<Integer,Double>> ();
	public  void get_bodyTerm() throws SQLException{
		Connection con = ConnectionUtil.getConnection();
//		con.setAutoCommit(false); 
		Statement stmt = con.createStatement();
		
		//part1-------------��bodyterm�����ڴ�
		
		System.out.println("��ʼ��bodyterm�����ڴ棡");
		int DocNum = 1000; // �ĵ�����Ŀ
		int termSum = 13848493; // 13848493
		for(long row=0; row<= termSum; row+=200000){
			System.out.println(row);
			String all_terms_sql = "select term,document_id,tf from bodyterm_1000 limit "+row+",200000";
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
				 termIn = allTerm_and_df.get(term); // ������ ת����id
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
		System.out.println("�����ռ��С ��"+bigVectorMap.size());
		//System.out.println("bodyterm�ɹ������ڴ棡");
	}
	
	// ��ȡtermid���е���Ϣ
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
	      String insertSql = "insert into docVector_1000 values(?,?,?)";
	      //PreparedStatement pstmt = conn.prepareStatement(docIdSql);
	      PreparedStatement pstmt = conn.prepareStatement(insertSql);
	      Set<Integer> keyset = bigVectorMap.keySet();
	      Iterator<Integer> it = keyset.iterator();
	      HashMap<Integer, Double>  temp;
	      conn.setAutoCommit(false);  // ���Զ��ύ
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
	
	// ��������е�������Ϣ���뵽�ڴ��У� term id df
	HashMap<String,termInfo> allTerm_and_df = new HashMap<String,termInfo>();
	public  void getTerm_and_df() throws SQLException{
		if(allTerm_and_df !=null) allTerm_and_df.clear();
		else {
			allTerm_and_df = new HashMap<String,termInfo>();
		}
		Connection conn = ConnectionUtil.getConnection();
		System.out.println("��ʼ��term�����ڴ棡");
		Statement stmt = conn.createStatement();
		String sql = "select termid,term,df from term_id_1000";
		ResultSet rs =  stmt.executeQuery(sql);
		
		while(rs.next()){
			allTerm_and_df.put(rs.getString(2),new termInfo(rs.getInt(1), rs.getInt(3)));
		}
		System.out.println("term�����ڴ� ok��");
		System.out.println("term num" + allTerm_and_df.size());
		
		
		conn.close();
	}
//	//  �������ռ����в��� �ĵ� ��Ӧ�� ����
//	public static void buildDocVector() throws ClassNotFoundException, SQLException{
//		Connection conn = null;
//        String sql;
//        // MySQL��JDBC URL��д��ʽ��jdbc:mysql://�������ƣ����Ӷ˿�/���ݿ������?����=ֵ
//        // ������������Ҫָ��useUnicode��characterEncoding
//        // ִ�����ݿ����֮ǰҪ�����ݿ����ϵͳ�ϴ���һ�����ݿ⣬�����Լ�����
//        // �������֮ǰ��Ҫ�ȴ���javademo���ݿ�
// 
//        
//            // ֮����Ҫʹ������������䣬����ΪҪʹ��MySQL����������������Ҫ��������������
//            // ����ͨ��Class.forName�������ؽ�ȥ��Ҳ����ͨ����ʼ������������������������ʽ������
//            Class.forName("com.mysql.jdbc.Driver") ;// ��̬����mysql����
// 
//            System.out.println("�ɹ�����MySQL��������");
//            conn = DriverManager.getConnection(url);
//            conn.setAutoCommit(false);  // ���Զ��ύ
//            System.out.println("���ӳɹ�");
//            Statement stmt = conn.createStatement();
//            Statement stmt2 = conn.createStatement();
//            ResultSet rs;
//            // ���Ȼ�ȡ�ĵ�id�ķ�Χ 
//            ArrayList<Integer> docId = new ArrayList<Integer>();
//            String docIdSql = "select distinct document_id  from bodyterm ";
//            rs = stmt.executeQuery(docIdSql);
//            while(rs.next()){
//            	docId.add(rs.getInt(1)); // ��ȡ���е��ĵ�id
//            }
//            int DocNum = docId.size();  // ��������ĵ����� N (1+Math.log((double)tf))*(Math.log(N/df));
//            
//            // ���� ��ֱ��ʹ��term����ת��Ϊ��Ӧ��id
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
//            for(int i = 0;i< DocNum;i++){ // ��ÿ���ĵ��Ȳ�ѯ���������дʻ�
//            	currentDocId = docId.get(i); 
//            	vector.clear();	
//            	docIdSql = docIdSqlBase + currentDocId;
//            	System.out.println(docIdSql);
//            	rs = stmt.executeQuery(docIdSql);
//            	int termId;
//            	int countTerm =0;
//            	while(rs.next()){ // �Ը��ĵ���ÿ�������ѯ�õ��� dfֵ
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
	// 2014 12 12 �����ʻ��İ�
	// �����������й�һ������
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
            // ֮����Ҫʹ������������䣬����ΪҪʹ��MySQL����������������Ҫ��������������
            // ����ͨ��Class.forName�������ؽ�ȥ��Ҳ����ͨ����ʼ������������������������ʽ������
            Class.forName("com.mysql.jdbc.Driver") ;// ��̬����mysql����
 
            System.out.println("�ɹ�����MySQL��������");
            conn = DriverManager.getConnection(url);
            System.out.println("���ӳɹ�");

            String termSql ;
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            ResultSet rs,rs2;
            // ���ȹ��� id term ��
            ArrayList<String> allTerms = new ArrayList<String>();
            //HashMap<Integer, HashMap<String, V>>
            
            // �����еĴ� ���뵽termId������ȥ�����Ȼ�ȡtermId�����������id
            termSql = "SELECT MAX(id) from TermID";
            rs = stmt.executeQuery(termSql);
            int maxTermId = 0;
            if(rs.next()){
            	maxTermId = rs.getInt(1);
            } 
            maxTermId ++;
            // == �������ǻ���� TermID���е�����id������ʹ�id +1 ��ʼ
            
            // ����������Ҫ��ȡ ����Ҫ����Ĵ���
            termSql  = "select distinct term  from bodyterm";
            rs = stmt.executeQuery(termSql); 
            while(rs.next()){
            	allTerms.add(rs.getString(1));
            }
            termSql = "insert into TermID_copy values(?,?,?)";           
            // SELECT COUNT(document_id) from bodyterm WHERE term = '�ھ�'
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
	// ���ຯ���� ��������Ҫ������ĵ��б�
	// �����Ǿ��������б�
//	
//	//PriorityQueue<E>
//	static  HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
//	public  ArrayList<ClusterDetail> cluster(ArrayList<Integer> doclist) throws ClassNotFoundException, SQLException{
//		boolean [] flag; // ���ڱ����Щ�ĵ��Ѿ����ϲ�
//		// ��������ĵ�����������ģ���������ӳ��һ�£���0λ���ϵľ����ĵ� 0��Ӧ��ʵ���ĵ�
//		ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
//		getAllVectors(doclist);
//		int docSize = doclist.size();
//		flag = new boolean[docSize];
//		for(int i=0;i<flag.length;i++) flag[i]=false;
//		double [][] simValues = new double[docSize][docSize];
//		ClusterDetail  temp;
//		// ��ʼ�� docSize ����
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
//	// ���ص�ά�ȹ��ߵ�ʱ�����ά��ɸѡ
//	// ɸѡԭ���ǣ� ѡ����Щ����Ȩ�رȽϴ��
//	public  void featureSelect(){
//		
//	}
//	
//	
//	// ���������ĵ������ƶ�
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
