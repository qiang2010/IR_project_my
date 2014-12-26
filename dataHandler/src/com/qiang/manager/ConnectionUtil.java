package com.qiang.manager;


import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtil {
	
	public static Connection getConnection(){
		 String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            con = DriverManager.getConnection(url);
//            System.out.println("成功连接！");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return con;
	}
}
