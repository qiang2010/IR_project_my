package com.qiang.manager;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UrlModify {

	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String dir = "http://sports.qq.com/a/20140115/016279.htm";
		dir = "http://sports.163.com/14/0101/11/9HGI0STQ00052UUC.htm";
		dir = "http://sports.sina.com.cn/l/2014-08-27/10527310705.shtml";
		//http://sports.sina.com.cn/c/2014-05-15/23507166970.shtm
		// http://sports.163.com/14/0711/08/A0S0Q3PO00051CA1.html
		//System.out.println(modifyUrl(dir));
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
	            conn.setAutoCommit(false);  // ���Զ��ύ
	            System.out.println("���ӳɹ�");
	            int id = 0;
	            int r1 =2000, r2 = 3000;
	            int tableSize = 103000;
	            Statement stmt;
	            ResultSet rs;
	            String update_sql  = "update htmls set time= ? where id = ?";
	            PreparedStatement pstmt = conn.prepareStatement(update_sql);
	            int newHot=-1;
	            Random rand = new Random();
	            while(r2<tableSize){
	            	 sql = "select id,url from htmls where id <=" + r2 +" && id >" + r1;
	            	 stmt = conn.createStatement();
	            	 rs = stmt.executeQuery(sql);
	            	 Timestamp urlF;
	            	 int idd=-1 ;
	            	 while(rs.next()){         		 
	            		 String uu =  rs.getString(2);
	            		 //if(uu.contains("http"))continue;
	            		 urlF = UrlModify.getTime(uu);
	            		 if(urlF == null) {
	            			 System.out.println( "wrong");
	            			 return;
	            		 }
	            		 idd = rs.getInt(1);
	            		 id = rs.getInt(1);
	            		 System.out.println(id);
	            		 pstmt.setInt(2, id);
	            		 pstmt.setTimestamp(1, urlF);
	            		 pstmt.addBatch();
	            		 //pstmt.execute();
	            		// break;
	            	 }
	            	 //break;
	            	r1 = r2;
	            	 r2 = r1+1000;
	            }
	            pstmt.executeBatch();
				conn.commit();
				System.out.println("ok");
	            conn.close(); 
	            System.out.println("ok");
	            conn.close();
		//System.out.println(getTime(dir));
	}
	public static Timestamp getTime(String url){
		String time ="";
		Date date ;
		String []ts = url.split("/{1,}");
		
		if(url.contains("sports.qq")){
			time = ts[3];
		}else{
			if(url.contains("sports.163")){
				time = "20"+ts[2]+ts[3];
			}else {
				if(url.contains("sports.sina")){
					for(int i = 0;i < ts.length;i++)
						if(ts[i].startsWith("2014-")){
							time = ts[i].replace("-", "");
							break;
						}	
				}
			}
		}
		if(time.startsWith("201")==false){
			System.out.println( url + " "+ time);
			return null;
		}
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyyMMdd");
		Timestamp timeStamp = null ;
		try {
			date = simpleTimeFormat.parse(time);
		   timeStamp = new Timestamp(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(url);
			return null;
		}
		return timeStamp;
	}
	public static String modifyUrl(String dir){
		String ans = "";
		int index = dir.indexOf("sports");
		dir = dir.substring(index);
		dir = dir.replace("\\", "/");  
		dir = dir.replace("_1", "");  
		dir = dir.replace("_2", "");  
		// http://sports.sina.com.cn/r/2014-02-28/10577044689.shtml
		ans = "http://"+dir;	
		if(ans.contains(".com")==false){
			ans = ans.replace("sports.qq", "sports.qq.com");
		}
		return ans;
	}

}
