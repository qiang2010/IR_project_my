package com.ucas.ir.project.cluster;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ucas.ir.project.repository.DocVectorDao;
import com.ucas.ir.project.repository.HtmlDao;

//Spring Bean的标识.
@Component
//类中所有public函数都纳入事务管理的标识.
@Transactional
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
	@Autowired
	private HtmlDao htmlDao;
	@Autowired
	private DocVectorDao docVectorDao;
	private  double sim_threshold = 0.02;
	
	private ArrayList<Double> docScoreList ;
	
	public void getClusterTitle() throws ClassNotFoundException, SQLException{
          int s = result.size();
          for(int i=0;i < s; i++){
        	  result.get(i).title = htmlDao.findOne((long)result.get(i).centerDoc);
        	 }
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
			return (int)(o2.score - o1.score);
		}
		
	}
	// 根据文档得分进行排序
	class com_doc implements Comparator<Integer>{

		@Override
		public int compare(Integer o1, Integer o2) {
			if (o1 == o2) {
				return 0;
			}
			if (docScoreList.get(o2) > docScoreList.get(o1)) {
				return 1;
			}else {
				return -1;
			}
		}	
	}
	//  给聚好的簇排序，同时将簇内的文档按照其得分排序, 将文档id再重新映射回来
	public void sortClusters(ArrayList<Integer> doclist){
		int s = result.size();
		Comparator<ClusterDetail> comparator = new Comparator<ClusterDetail>() {

			@Override
			public int compare(ClusterDetail o1, ClusterDetail o2) {
				//需要处理相等的情况,不然报生成规则异常
				if (null == o2||null == o1) {
					return 0;
				} else {
					if (o2.score > o1.score) {
						return 1;
					}else {
						return -1;
					}
					
				}
				
			}
		};
		
		Collections.sort(result, comparator);
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
	    private HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
	    private ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
		double [][] simValues;
		public  ArrayList<ClusterDetail> cluster
			(ArrayList<Integer> doclist,ArrayList<Double> docScoreList) 
					throws ClassNotFoundException, SQLException{
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
				temp.title =null ;
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
							t1 =i;t2=j;
							max = tem;
						}
					}
				}
				joinTwoClusters(t1, t2);
				current_cluster_num--;
				current_sim = max;
				
			}
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
							if(tempTFIDF < 0.1){
								//center.remove(docIdd);
							}else{
								//center.put(docIdd, tempTFIDF);
								tempCenter.put(docIdd, tempTFIDF);
							}
						}
						center = tempCenter;
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
			if (null == vector1 || null == vector2) {
				return 0.0;
			}
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
	        int size = doclist.size();
	        int docId;
	        HashMap<Integer, Double> vec;
	        for(int i = 0;i<size;i++){
	        	docId = doclist.get(i);
//	        		vec = new  HashMap<Integer, Double>();
	        		Blob blob =docVectorDao.getVectorById((long)docId);
					ObjectInputStream objinput = null;
					try {
						if (null == blob) {
							vec = new HashMap<Integer, Double>();
						}else{
						objinput = new ObjectInputStream(blob.getBinaryStream());
						vec= (HashMap<Integer, Double>) objinput.readObject();
						}
					//	System.out.println(vec);
						vectors.put(i, vec); // 归一化
						if(objinput != null)
						objinput.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
			return true;
		}
		// 给定向量进行归一化操作
		public static HashMap<Integer, Double> normalize(HashMap<Integer, Double> vector){
			if(vector == null) return null;
			double length=0;
			Set<Integer> keyS = vector.keySet();
			Iterator<Integer> it = keyS.iterator();
			int k;
			while(it.hasNext()){
				k = it.next();
				length += Math.pow(vector.get(k),2);
			}
			length = Math.sqrt(length);
			it = keyS.iterator();
			//double test=0;
			while(it.hasNext()){
				k = it.next();
				vector.put(k,vector.get(k)/length);
				//test += Math.pow(vector.get(k),2);
			}
			//System.out.println(test);
			return vector;
		}
}
