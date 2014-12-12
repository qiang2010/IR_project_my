//package com.qiang.manager;
//
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
//import java.util.HashSet;
//import java.util.LinkedList;
//
//
//public class indexConstruct {
//
//	public static void main(String[] args) {
//		System.out.println("开始构建！");
//		long start = System.currentTimeMillis();
//		try {
//			index_constuct();
//		} catch (SQLException e) {		
//			e.printStackTrace();
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("花费时间：" + (end-start)/60000.0 + " min");
//		System.out.println("构建结束！");
//	}
//	
//	static void index_constuct() throws SQLException{
//		
//			Connection con = ConnectionUtil.getConnection();
//			HashSet<String> terms_set = new HashSet<String>();
//			con.setAutoCommit(false); 
//			Statement stmt = con.createStatement();					
//			String terms_sql = "select distinct term from bodyterm";
//			ResultSet terms_rs = stmt.executeQuery(terms_sql);
//			while(terms_rs.next()){
//				String temp_term = terms_rs.getString(1);
////				if(temp_term.contains())
//				terms_set.add(temp_term);
//			}
//			terms_rs.close();
//			System.out.println("倒排记录数 ： " + terms_set.size());
//			
//			int dictionaryID = 1;		
//			PreparedStatement pstmt = con.prepareStatement("insert into dictionary values(?,?,?,?)");
//			for(String term : terms_set){
//				LinkedList <Index> term_list = new LinkedList<Index>();
//				String singleTerm_sql = "select document_id,tf from bodyterm where term ='"+term+"'";
//				ResultSet singleTerm_rs = stmt.executeQuery(singleTerm_sql);
//				singleTerm_rs.last();
//				int df = singleTerm_rs.getRow();
//				singleTerm_rs.beforeFirst();
//				Statement stmt1 = con.createStatement();
//				while(singleTerm_rs.next()){
//					int documentID = singleTerm_rs.getInt(1);
//					int tf = singleTerm_rs.getInt(2);
////					//加入hot和time
//					String time_hot_sql = "select timestamp,hot from htmls where id ='"+documentID+"'";
//					ResultSet time_hot_rs = stmt1 .executeQuery(time_hot_sql);
//					Timestamp time = null;
//					int hot = 0;
//					while(time_hot_rs.next()){
//						time = time_hot_rs.getTimestamp(1);
//						hot = time_hot_rs.getInt(2);
//					}
//					
//					Index temp_index = new Index(documentID,tf,df);//构建对象
//					System.out.println("<"+term+">" + "-" + documentID + "-tfidf  :  " + temp_index.getTf_idf());
//					term_list.add(temp_index);	
//					time_hot_rs.close();
//				}//end-while				
//				
//				//插入倒排记录				
//				pstmt.setInt(1, dictionaryID);
//				pstmt.setString(2, term);
//				pstmt.setObject(3, term_list);
//				pstmt.setInt(4, df);
//				pstmt.addBatch();
//				System.out.println("成功插入倒排记录 --- "+dictionaryID);
//				dictionaryID++;
//				singleTerm_rs.close();
//			}//end-for
//			
//			pstmt.executeBatch();
//			con.commit();
//			stmt.close();con.close();    
//	}
//	
//}
