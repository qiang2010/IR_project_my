package com.qiang.manager;

public class UrlModify {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\git\\IR_Data\\三个新闻\\sports.163.com\\14_1\\0101\\22\\9HHOE5TK00052UUC.html";
		dir = "F:\\sports.qq\\a\\20141109\\020737.htm";
		System.out.println(modifyUrl(dir));
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
