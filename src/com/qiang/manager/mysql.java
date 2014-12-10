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
        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
        String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
 
        
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            // or:
            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // or：
            // new com.mysql.jdbc.Driver();
 
            System.out.println("成功加载MySQL驱动程序");
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            System.out.println("连接成功");
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
//				System.out.println("成功插入倒排记录"+dictionaryID);
//				conn.commit();
//			}
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
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
//          		System.out.println("time为空！");
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
//            		System.out.println("time为空！");
//            	}else{
//            		//2014年10月11日14:59
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
            
            System.out.println("本次操作成功！");
//              sql = "insert into student(NO,name) values('2012001','陶伟基')";
//              result = stmt.executeUpdate(sql);
//              sql = "insert into student(NO,name) values('2012002','周小俊')";
//              result = stmt.executeUpdate(sql);
//              sql = "select * from student";
//              ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
//              System.out.println("学号\t姓名");
            
//            Date date;
//            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
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
//            int result = stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
//            if (result != -1) {
//                System.out.println("创建数据表成功");
//                sql = "insert into student(NO,name) values('2012001','陶伟基')";
//                result = stmt.executeUpdate(sql);
//                sql = "insert into student(NO,name) values('2012002','周小俊')";
//                result = stmt.executeUpdate(sql);
//                sql = "select * from student";
//                ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
//                System.out.println("学号\t姓名");
//                while (rs.next()) {
//                    System.out
//                            .println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
//                }
//            }
//            
     
            conn.close();
       
    }
}