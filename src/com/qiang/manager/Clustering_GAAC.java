package com.qiang.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Clustering_GAAC {

	
	/*
	 *  组平均聚类算法  大部分应用中的最佳选择
	 *  聚为K类，给定相似度为t，当小于t的时候停止聚类，截断
	 *  1. 获取查询返回的文档的向量
	 *  2. 对向量做特征选择，  如何降维？？  不再降维
	 *  3. 在降维后的向量上做聚类
	 *  4. 给出每个类别一个标签： 基于簇内信息的标签生成，只是根据簇本身的信息计算标签，一种方法是最接近质心的文档的标题
	 *  
	 *  具体： N篇文档，计算两两相似度，放在一个二维数组中，簇中记录包含的文档的id就行了
	 *  
	 */
	double sim_threshold = 0.08;
	double TFIDF_THRESHOLD = 0.01;
	ArrayList<Integer> doclist = new ArrayList<Integer>();
	ArrayList<Double> docScoreList ;
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Clustering_GAAC build = new Clustering_GAAC();
		ArrayList<Integer> doclist = new ArrayList<Integer>();
		ArrayList<Double> docScoreList = new ArrayList<Double>();
		for(int j =2;j<1000;j++){
			doclist.add(j*2);
			docScoreList.add(Math.random()*10);
		}
			
		build.cluster(doclist,docScoreList);
//		build.calCluster_center_and_score(docScoreList);
//		build.sortClusters();
		build.print();
	}
	
	public void getClusterTitle() throws ClassNotFoundException, SQLException{
		 String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
		  Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动

          System.out.println("成功加载MySQL驱动程序");
          // 一个Connection代表一个数据库连接
          Connection conn = DriverManager.getConnection(url);
          System.out.println("连接成功");
          
          String sqlBase =" select title from htmls where id =";
          String sql;
          Statement stmt ;
          ResultSet rs; 
          int s = result.size();
          for(int i=0;i < s; i++){
        	  sql = sqlBase + result.get(i).centerDoc;
        	  stmt = conn.createStatement();
        	  rs = stmt.executeQuery(sql);
        	  if(rs.next()){
        		  result.get(i).title = rs.getString(1);
        	  }
          }
          conn.close();
	}
	
	// 打印详细信息
	public void print(){
		int s = result.size();		
		for ( int j =0; j < s; j++){
			System.out.println(result.get(j) );
		}
	}
	
	// 实现比较器，用于簇的排序
	class com implements Comparator<ClusterDetail>{

		@Override
		public int compare(ClusterDetail o1, ClusterDetail o2) {
			// TODO Auto-generated method stub
			if(o2.score == o1.score){
				return 0;
			}else if(o2.score > o1.score){
				return 1;
			}else return -1;
		}
		
	}
	// 根据文档得分进行排序
	class com_doc implements Comparator<Integer>{

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
//			System.out.println( o1 + "  " + o2);
			if(docScoreList.get(o2) == docScoreList.get(o1)){
				return 0;
			}else if(docScoreList.get(o2) > docScoreList.get(o1)){
				return 1;
			}else return -1;
		}	
	}
	//  给聚好的簇排序，同时将簇内的文档按照其得分排序, 将文档id再重新映射回来
	public void sortClusters(ArrayList<Integer> doclist){
		int s = result.size();
		Collections.sort(result, new com());
		ArrayList<Integer> newDoclist ;
		ArrayList<Integer> cluList;
		
		for ( int j =0; j < s; j++){
			Collections.sort(result.get(j).docList,new com_doc());
			cluList =result.get(j).docList; 
			int ss = cluList.size();
			newDoclist = new ArrayList<Integer>();
			for( int i = 0;i< ss;i++){
				newDoclist.add(doclist.get(cluList.get(i)));
			}
			result.get(j).docList = newDoclist; 
			result.get(j).centerDoc = doclist.get(result.get(j).centerDoc);
		}
	}
	
	// 聚类函数， 输入是需要聚类的文档列表
		// 返回是聚类后的类列表
		
		//PriorityQueue<E>
	    HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
		ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
		double [][] simValues;
		public  ArrayList<ClusterDetail> cluster
			(ArrayList<Integer> doclist,ArrayList<Double> docScoreList) 
					throws ClassNotFoundException, SQLException{
			System.out.println();
			this.docScoreList = docScoreList;
			// 输入的是文档集合是随机的，但是我们映射一下，在0位置上的就是文档 0对应的实际文档
			if(result!=null) result.clear();
			
			getAllVectors(doclist);  // 获取给定文档集合的 向量
			
			int docSize = doclist.size();
			// 标记所有的文档都没有被加入到别的簇中
			simValues = new double[docSize][docSize];
			ClusterDetail  temp;
			// 初始化 docSize 个簇
			for(int k =0;k<docSize;k++){
				temp = new ClusterDetail();
				temp.clusterCenter = vectors.get(k);
				temp.title ="" ;
				temp.clusterSize =1;
				temp.docList.add(k);  // 现在就相当于处理 文档编号为 0 - (docsize-1) 的文档的聚类
				result.add(temp);
			}
			// 计算两两之间的相似度
			for( int i = 0;i<docSize;i++)
				for(int j = 0;j<docSize; j++){
					if(i==j)	simValues[i][j]=1;
					else
						simValues[i][j] = sim(vectors.get(i),vectors.get(j));
				}
			
			double current_sim = 1.0;
			
			int cluster_threshold = (int) Math.sqrt(docSize);
			int current_cluster_num = docSize;
			
			int it =0;
			//  开始聚类
			//  聚类结束条件 一个是簇的数目, 一个是相似度，任一不满足就停止聚类
			while(cluster_threshold < current_cluster_num && current_sim > sim_threshold){
				it++;
				int t1=-1,t2=-1;
				double max = -1;
				double tem;
				for(int i=0;i<current_cluster_num;i++){
					for(int j=i+1;j<current_cluster_num;j++){
						tem = simValueBetweenClusters(i,j);
						if(max < tem){
							System.out.println("max:" +tem);
							t1 =i;t2=j;
							max = tem;
						}
					}
				}
				joinTwoClusters(t1, t2);
				current_cluster_num--;
				current_sim = max;
				
				System.out.println("it:" +it + "  current_sim: "+current_sim);
			}
			System.out.println("doc num	: " + doclist.size());
			System.out.println("cluster size:" +result.size());

			calCluster_center_and_score(docScoreList); // 计算质心
			sortClusters(doclist);   //排序
			getClusterTitle();
			return result;
		}
		// 计算 簇的质心，并且找到距离质心最近的文档
				public void calCluster_center_and_score(ArrayList<Double> docScores){
					int s = result.size();
					ArrayList<Integer> docList ;
					HashMap<Integer, Double> center;
					HashMap<Integer, Double> tempDocDetail;
					ClusterDetail clu;
					Set<Integer> keyset;
					Iterator<Integer> it;
					int docIdd;
					int termId;
					double tempTFIDF;
					for(int j=0;j < s;j++){
						clu = result.get(j);
						docList = clu.docList;	
						center = clu.clusterCenter;
						clu.score =0;
						for(int i=0;i<clu.clusterSize;i++){						
						   	docIdd = docList.get(i);
							clu.score += docScores.get(docIdd); // 计算得分之和
						   //	System.out.println(docIdd);
						   	tempDocDetail = vectors.get(docIdd);
						   	keyset = tempDocDetail.keySet();
						   	it = keyset.iterator();
						   	while(it.hasNext()){
						   		termId = it.next();
						   		//System.out.println(center);
						   		if(center.containsKey(termId)){  // 质心已经包含该词项
						   			center.put(termId, center.get(termId) + tempDocDetail.get(termId));
						   		}else{ // 质心不包括该词项
						   			center.put(termId,tempDocDetail.get(termId));
						   		}
						   	}
						}
						// 处理完所有文档后，求簇的质心，并且将那些权重很小的词项去掉
						keyset = center.keySet();
						it = keyset.iterator();
						//System.out.println(it);
						HashMap<Integer, Double> tempCenter = new HashMap<Integer, Double>();
						while(it.hasNext()){
							docIdd = it.next();
							tempTFIDF = center.get(docIdd)/clu.clusterSize;
							if(tempTFIDF < TFIDF_THRESHOLD){
								//center.remove(docIdd);
							}else{
								//center.put(docIdd, tempTFIDF);
								tempCenter.put(docIdd, tempTFIDF);
							}
						}
						clu.clusterCenter = center = tempCenter;
						// 找到距离最近的文档
						HashMap<Integer, Double> docCen;
						int maxId = -1;
						double max = -1;
						double simVV;
						for(int kk = 0;kk<clu.clusterSize;kk++){
							docCen = vectors.get(docList.get(kk));
							simVV = sim(docCen,center);
							if(simVV > max){
								max = simVV; maxId = docList.get(kk);
							}
						}
						clu.centerDoc = maxId;  
						//System.out.println("j: " + center.size());
					}

					
				}
				
		
		// 合并两个簇
		public boolean joinTwoClusters(int c1,int c2){
			if(c1 <0||c2<0) return false;
			ClusterDetail tt1 = result.get(c1);
			ClusterDetail tt2 = result.get(c2);
			ArrayList<Integer> tt2List = tt2.docList;
			int s = tt2List.size();
			for(int i =0;i<s;i++){
				tt1.docList.add(tt2List.get(i));
			}
			tt1.clusterSize += tt2.clusterSize;  // 合并以后，簇大小为两者之和
			result.remove(c2); // 合并以后删除一个
			return true;
		}
		
		// 求 两个簇的相似度
		public  double simValueBetweenClusters(int ClusterNum1,int ClusterNum2){
			double simV =0;
			if(result == null) return -1;
			ArrayList<Integer> clu1= result.get(ClusterNum1).docList;
			ArrayList<Integer> clu2= result.get(ClusterNum2).docList;
			ArrayList<Integer> clu = new ArrayList<Integer>();			
			int s1 = clu1.size();
			int s2 = clu2.size();
			double aveSim = 0;
			for(int i=0;i<s1;i++)
				clu.add(clu1.get(i));
			for(int i=0;i<s2;i++)
				clu.add(clu2.get(i));
			int s = s1+s2;
			for( int i =0;i<s;i++)
				for(int j=i+1;j<s;j++){
					simV += simValues[i][j];
				}
			simV = simV/(s*(s-1)); // 这里本来要乘以2，省略不影响排序
			return simV;
		}

		
		
		// 给定两个文档求相似度
		public  double sim(HashMap<Integer, Double> vector1,HashMap<Integer, Double> vector2){
			//if(doc1 == doc2 ) return 1;
			double simValue=0;
			
			int a,b;
			Set<Integer> vector1Keys = vector1.keySet();
			int size = vector1Keys.size();
			Iterator<Integer> it = vector1Keys.iterator();
			while(it.hasNext()){
				a = it.next();
				if(vector2.containsKey(a)){
					simValue += vector1.get(a) * vector2.get(a);
				}
			}
			return simValue;
		}	
		// 从数据库中读取所有的 需要文档的 向量
		public  boolean getAllVectors(ArrayList<Integer> doclist) throws ClassNotFoundException, SQLException{
			if(vectors !=null) vectors.clear();
			Connection conn = null;
	        String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
	        Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
	        System.out.println("成功加载MySQL驱动程序");
	        conn = DriverManager.getConnection(url);
	        System.out.println("连接成功");
	        String sqlBase = "select vector from docVector where docid =";
	        String sql ;
	        Statement stmt = conn.createStatement();
	        ResultSet rs;
	        int size = doclist.size();
	        int docId;
	        HashMap<Integer, Double> vec;
	        for(int i = 0;i<size;i++){
	        	docId = doclist.get(i);
	        	sql = sqlBase + docId;
	        	rs = stmt.executeQuery(sql);
	        	if(rs.next()){
//	        		vec = new  HashMap<Integer, Double>();
	        		Blob blob = (Blob) rs.getBlob("Vector");
					ObjectInputStream objinput;
					try {
						objinput = new ObjectInputStream(blob.getBinaryStream());
						vec= (HashMap<Integer, Double>) objinput.readObject();	
					//	System.out.println(vec);
						vectors.put(i, BuildDocVectorTable.normalize(vec)); // 归一化
						objinput.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
	        	}else{  // 没有改文档
	        		vectors.put(i,new HashMap<Integer, Double>());
	        	}
	        }
	        System.out.println("Vectors size " + vectors.size());
	        conn.close(); 
			return true;
		}

}
