//package com.qiang.manager;
//
//
//import java.io.ObjectOutputStream;
//import java.sql.Blob;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//
//
//public class IndexConstruct_body {
//
//	public static void main(String[] args) {
//		System.out.println("��ʼ������");
//		long start = System.currentTimeMillis();
//		try {
//			index_constuct();
//		} catch (SQLException e) {		
//			e.printStackTrace();
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("����ʱ�䣺" + (end-start)/60000.0 + " min");
//		System.out.println("����������");
//	}
//	
//	static void index_constuct() throws SQLException{
//		
//			Connection con = ConnectionUtil.getConnection();
//			HashSet<String> terms_set = new HashSet<String>();
////			con.setAutoCommit(false); 
//			Statement stmt = con.createStatement();
//			
//			//part1-------------��bodyterm�����ڴ�
//			HashMap<String,HashMap<Integer,LinkedList<Integer>>> bigMap = new HashMap<String,HashMap<Integer,LinkedList<Integer>>>();
//			
//			System.out.println("��ʼ��bodyterm�����ڴ棡");
//			for(long row=0; row<=13848493; row+=200000){
//				String all_terms_sql = "select term,document_id,tf from bodyterm limit "+row+",200000";
////				select * from bodyterm limit 100000,100000
//				ResultSet all_terms_rs = stmt.executeQuery(all_terms_sql);
//				while(all_terms_rs.next()){
//					String term = all_terms_rs.getString(1);
//					if(bigMap.get(term) == null){
//						HashMap<Integer,LinkedList<Integer>> docID_tf_map = new HashMap<Integer,LinkedList<Integer>>();
//						LinkedList<Integer> docID_list = new LinkedList<Integer>();
//						LinkedList<Integer> tf_list = new LinkedList<Integer>();
//						docID_list.add(all_terms_rs.getInt(2));
//						tf_list.add(all_terms_rs.getInt(3));
//						docID_tf_map.put(1, docID_list);//1--docID
//						docID_tf_map.put(2, tf_list);//2--tf
//						bigMap.put(term, docID_tf_map);
//						
//					}else{
//						int docID = all_terms_rs.getInt(2);
//						int tf = all_terms_rs.getInt(3);
//						bigMap.get(term).get(1).add(docID);
//						bigMap.get(term).get(2).add(tf);
//					}
//				}
//				all_terms_rs.close();	
//			}//end-for
//			System.out.println("���ż�¼�� ��"+bigMap.size());
//			System.out.println("bodyterm�ɹ������ڴ棡");
//			
//			System.out.println("\n***********************************************\n");
//
//			
//			//part2-------------��htmls�����ڴ�
//			System.out.println("��ʼ��htmls�����ڴ棡");
//			HashMap<Integer,LinkedList<Long>> htmls_map = new HashMap<Integer,LinkedList<Long>>();
//
//			String all_htmlDatas_sql = "select id,time,hot from htmls";
//			ResultSet all_htmlDatas_rs = stmt.executeQuery(all_htmlDatas_sql);
//			
//			while(all_htmlDatas_rs.next()){			
//				int docID = all_htmlDatas_rs.getInt(1);
//				LinkedList<Long> time_hot_list = new LinkedList<Long>();
//				time_hot_list.add(all_htmlDatas_rs.getLong(2));//list[0]==hot
//				time_hot_list.add(all_htmlDatas_rs.getLong(3));//list[1]==time
//				htmls_map.put(docID, time_hot_list);
//			}
//			all_htmlDatas_rs.close();	
//			stmt.close();
//			System.out.println("htmls���� ��"+htmls_map.size());
//			System.out.println("htmls�ɹ������ڴ棡\n");
//			
//			System.out.println("\n***********************************************\n");
//
//			
//			int dictionaryID = 1;
//			
//			for(String term : bigMap.keySet()){//for1
//				
//				PreparedStatement pstmt = con.prepareStatement("insert into body_dictionary values(?,?,?,?)");
//
//				LinkedList <Index> term_list = new LinkedList<Index>();
//				int df = bigMap.get(term).get(1).size(); //docID_list�Ĵ�С
//				LinkedList<Integer> docID_list = new LinkedList<Integer>();
//				docID_list = bigMap.get(term).get(1);
//				LinkedList<Integer> tf_list = new LinkedList<Integer>();
//				tf_list = bigMap.get(term).get(2);
//				
//				for(int i=0; i<df; i++){//for2
//					int documentID = docID_list.get(i);
//					int tf = tf_list.get(i);
//					long time = htmls_map.get(documentID).get(0);//0---time
//					long hot = htmls_map.get(documentID).get(1);//1---hot
//					//��������
//					Index temp_index = new Index(documentID,tf,df,time,hot);
////					System.out.println("<"+term+">" + "-" + documentID + "-tfidf  :  " + temp_index.getTf_idf());
//					term_list.add(temp_index);	
//				}//end-for2
//				
//				//���뵹�ż�¼				
//				pstmt.setInt(1, dictionaryID);
//				pstmt.setString(2, term);
//				pstmt.setObject(3, term_list);
//				pstmt.setInt(4, df);
//				pstmt.executeUpdate();
//				pstmt.close();
//				
//				System.out.println("�ɹ����뵹�ż�¼ --- "+dictionaryID);
//				dictionaryID++;
//				
//			}//end-for1
//			
//			con.close();
//			
//	}//end-index_constuct()
//	
//}//end-class
