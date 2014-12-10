package com.qiang.manager;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
 
 
public class mysql {
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        String sql;
        // MySQL��JDBC URL��д��ʽ��jdbc:mysql://�������ƣ����Ӷ˿�/���ݿ������?����=ֵ
        // ������������Ҫָ��useUnicode��characterEncoding
        // ִ�����ݿ����֮ǰҪ�����ݿ����ϵͳ�ϴ���һ�����ݿ⣬�����Լ�����
        // �������֮ǰ��Ҫ�ȴ���javademo���ݿ�
        String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
 
        
            // ֮����Ҫʹ������������䣬����ΪҪʹ��MySQL����������������Ҫ��������������
            // ����ͨ��Class.forName�������ؽ�ȥ��Ҳ����ͨ����ʼ������������������������ʽ������
            Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����
            // or:
            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // or��
            // new com.mysql.jdbc.Driver();
 
            System.out.println("�ɹ�����MySQL��������");
            // һ��Connection����һ�����ݿ�����
            conn = DriverManager.getConnection(url);
            System.out.println("���ӳɹ�");
            int id = 0;
            int r1 =49000, r2 = 50000;
            int tableSize = 60000;
            Statement stmt;
            ResultSet rs;
            String update_sql  = "update htmls set url = ? ,hot = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(update_sql);
            int newHot=-1;
            Random rand = new Random();
            while(r2<tableSize){
            	 sql = "select id,url,hot from htmls where id <=" + r2 +" && id >" + r1;
            	 stmt = conn.createStatement();
            	 rs = stmt.executeQuery(sql);
            	 String urlF="";
            	 int idd=-1 ;
            	 while(rs.next()){         		 
            		 String uu =  rs.getString(2);
            		 //if(uu.contains("http"))continue;
            		 urlF = UrlModify.modifyUrl(uu);
            		 idd = rs.getInt(1);
            		 newHot = (int)((rand.nextGaussian())*5000);
            		 if(newHot<0){
            			 newHot = (int)(Math.random()*100);
            		 }
            		 id++;
            		 System.out.println(id);
            		 //pstmt.setInt(1, id);
            		 pstmt.setString(1, urlF);
            		 pstmt.setInt(2, newHot);
            		 pstmt.setInt(3, idd);
            		 pstmt.execute();
            	 }
            	 r1 = r2;
            	 r2 = r1+1000;
            }
           
            //int dictionaryID =1;
            //String term = new String("njz");;
            //String insertTerm_sql = "insert into dictionary(dictionaryID,term) values(?,?)";
			//PreparedStatement pstmt = conn.prepareStatement(insertTerm_sql);
			//pstmt.setInt(1, dictionaryID);
			//pstmt.setString(2, term);
			//pstmt.executeUpdate(insertTerm_sql);
//			int insert_flag = pstmt.executeUpdate(insertTerm_sql);
//			if(insert_flag != 0){
//				System.out.println("�ɹ����뵹�ż�¼"+dictionaryID);
//				conn.commit();
//			}
            // Statement������кܶ෽��������executeUpdate����ʵ�ֲ��룬���º�ɾ����
//            Statement stmt = conn.createStatement();
//            
//            sql = "select id,`timestamp` from news0";
//          HashMap<Integer,String> map1 = new HashMap<Integer,String>();
//          ResultSet rs = stmt.executeQuery(sql);
//          while(rs.next()){
//        	int id = rs.getInt(1);
//        	String time = rs.getString(2);
//        	if(time == null || time.isEmpty()){
//        		map1.put(id, "00000000000000");
//        	}
//          }
//          	
//          	String timestamp = new String();
//          	int id = rs.getInt(1);
//          	String time = rs.getString(2);
//          	if(time == null || time.isEmpty()){
//          		System.out.println("timeΪ�գ�");
//          	}else{
            
//            sql = "select id,`timestamp` from news0";
//            HashMap<Integer,String> map1 = new HashMap<Integer,String>();
//            ResultSet rs = stmt.executeQuery(sql);
//            
//            while(rs.next()){
//            	
//            	String timestamp = new String();
//            	int id = rs.getInt(1);
//            	String time = rs.getString(2);
//            	if(time == null || time.isEmpty()){
//            		System.out.println("timeΪ�գ�");
//            	}else{
//            		//2014��10��11��14:59
//            		System.out.println(id+" "+time);
//                	String year = time.substring(0,4);
//                	System.out.println(year);
//                	String month = time.substring(5,7);
//                	System.out.println(month);
//                	String day = time.substring(8,10);
//                	String hour = time.substring(11,13);
//                	String minute = time.substring(14,16);
//                	timestamp = year+month+day+hour+minute+"00";
//                	System.out.println(timestamp);
//            	}
//            	map1.put(id,timestamp);            	
//            }//end-while
//            rs.close();stmt.close();
////            
//            stmt = conn.createStatement();
//            for(int i : map1.keySet()){           	 
//            	 String update_sql = "update news0 set timestamp='"+map1.get(i)+"' where id='"+i+"'";
//            	 System.out.println(update_sql);
//            	 stmt.executeUpdate(update_sql);
//            }
            
            System.out.println("���β����ɹ���");
//              sql = "insert into student(NO,name) values('2012001','��ΰ��')";
//              result = stmt.executeUpdate(sql);
//              sql = "insert into student(NO,name) values('2012002','��С��')";
//              result = stmt.executeUpdate(sql);
//              sql = "select * from student";
//              ResultSet rs = stmt.executeQuery(sql);// executeQuery�᷵�ؽ���ļ��ϣ����򷵻ؿ�ֵ
//              System.out.println("ѧ��\t����");
            
//            Date date;
//            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy��MM��dd��HH:mm");
//            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    		System.out.println(time.attr("content"));  // 2013-12-20T06:30:00+08:00
//    		String day = time.attr("content");
//    		String second ;
//    		String []hours =  day.split("T");
//    		day = hours[0].trim();
//    		 second = hours[1].trim();
//    		String last = day+" "+second.substring(0, 8);
//    		try {
//    			date = simpleTimeFormat.parse(last);
//    		} catch (ParseException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		}
//    		Timestamp timeStamp = new Timestamp(date.getTime());  // unix timestamp
//            sql = "create table student(NO char(20),name varchar(20),primary key(NO))";
//            int result = stmt.executeUpdate(sql);// executeUpdate���᷵��һ����Ӱ����������������-1��û�гɹ�
//            if (result != -1) {
//                System.out.println("�������ݱ�ɹ�");
//                sql = "insert into student(NO,name) values('2012001','��ΰ��')";
//                result = stmt.executeUpdate(sql);
//                sql = "insert into student(NO,name) values('2012002','��С��')";
//                result = stmt.executeUpdate(sql);
//                sql = "select * from student";
//                ResultSet rs = stmt.executeQuery(sql);// executeQuery�᷵�ؽ���ļ��ϣ����򷵻ؿ�ֵ
//                System.out.println("ѧ��\t����");
//                while (rs.next()) {
//                    System.out
//                            .println(rs.getString(1) + "\t" + rs.getString(2));// ��������ص���int���Ϳ�����getInt()
//                }
//            }
//            
     
            conn.close();
       
    }
}