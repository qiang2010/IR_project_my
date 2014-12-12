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
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Clustering_GAAC build = new Clustering_GAAC();
		ArrayList<Integer> doclist = new ArrayList<Integer>();
		doclist.add(2);
		doclist.add(3);
		doclist.add(47);
		doclist.add(56);
		build.cluster(doclist);
	}
	
	// 聚类函数， 输入是需要聚类的文档列表
		// 返回是聚类后的类列表
		
		//PriorityQueue<E>
	    HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
		ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
		double [][] simValues;
		public  ArrayList<ClusterDetail> cluster(ArrayList<Integer> doclist) throws ClassNotFoundException, SQLException{
			boolean [] flag; // 用于标记那些文档已经被合并
			// 输入的是文档集合是随机的，但是我们映射一下，在0位置上的就是文档 0对应的实际文档
			if(result!=null) result.clear();
			
			getAllVectors(doclist);  // 获取给定文档集合的 向量
			
			int docSize = doclist.size();

			// 映射以后方便处理
			HashMap<Integer,Integer> mapDocId =new  HashMap<Integer, Integer>();
			for(int i =0;i<docSize;i++){
				mapDocId.put(doclist.get(i),i); 
			}		
			// 标记所有的文档都没有被加入到别的簇中
			
			simValues = new double[docSize][docSize];
			ClusterDetail  temp;
			// 初始化 docSize 个簇
			for(int k =0;k<docSize;k++){
				temp = new ClusterDetail();
				temp.clusterCenter = vectors.get(doclist.get(k));
				temp.title ="" ;
				temp.clusterSize =1;
				temp.docList.add(k);  // 现在就相当于处理 文档编号为 0 - (docsize-1) 的文档的聚类
				result.add(temp);
			}
			// 计算两两之间的相似度
			for( int i = 0;i<docSize;i++)
				for(int j = 0;j<docSize; j++){
					if(i==j)simValues[i][j]=1;
					else
						simValues[i][j] = sim(vectors.get(doclist.get(i)),vectors.get(doclist.get(j)));
				}
			
			double current_sim = 1.0;
			
			double sim_threshold = 0.2;
			int cluster_threshold = (int) Math.sqrt(docSize);
			int current_cluster_num = docSize;
			
			int it =0;
			// 开始聚类,
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
							//System.out.println("max:" +tem);
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
			System.out.println("cluster size:" +result.size());
			return result;
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
			tt1.clusterSize += tt2.clusterSize;
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

		// 计算 簇的质心，并且找到距离质心最近的文档
		public void calCluster_center(){
			int s = result.size();
			ArrayList<Integer> docList ;
			HashMap<Integer, Double> center;
			HashMap<Integer, Double> tempDocDetail;
			ClusterDetail clu;
			Set<Integer> keyset;
			Iterator<Integer> it;
			int docIdd;
			int termId;
			for(int j=0;j < s;j++){
				clu = result.get(j);
				docList = clu.docList;	
				center = clu.clusterCenter;
				for(int i=0;i<clu.clusterSize;i++){
				   	docIdd = docList.get(i);
				   	tempDocDetail = vectors.get(docIdd);
				   	keyset = tempDocDetail.keySet();
				   	it = keyset.iterator();
				   	while(it.hasNext()){
				   		termId = it.next();
				   		if(center.containsKey(termId)){  // 质心已经包含该词项
				   			
				   		}else{ // 质心不包括该词项
				   			
				   			
				   		}
				   	}
				}
			}

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
						vectors.put(docId, BuildDocVectorTable.normalize(vec)); // 归一化
						objinput.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
	        	}
	        }
	        conn.close(); 
			return true;
		}

}
