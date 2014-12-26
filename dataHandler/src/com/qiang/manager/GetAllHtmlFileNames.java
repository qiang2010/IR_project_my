package com.qiang.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
public class GetAllHtmlFileNames {



/*
 *  本类提供的接口功能是：给定一个文件夹，返回该文件夹下面的所有html htm shtml文件名
 *  html xhtml htm
 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getAllHtmls("D:\\git\\IR_Data\\sports.sohu.com").size());
	}
	public static ArrayList<String> getAllHtmls(String dir){
		File file = new File(dir);
		if(file.isDirectory() == false) return null;
		ArrayList<String> allHtmlNames = new ArrayList<String>();
		Queue<File> dirs = new LinkedList<File>();
		File []files;
		dirs.add(file);
		String name;
		while(dirs.isEmpty() ==false){
			file = dirs.poll();
			files = file.listFiles();
			for(File f:files){
				if(f.isDirectory())dirs.add(f);
				else{
					name = f.getAbsolutePath();
					if(name.endsWith("html")||name.endsWith("shtml")||name.endsWith("htm"))
						allHtmlNames.add(name);
				}
			}
		}
		return allHtmlNames;
	}   
}
