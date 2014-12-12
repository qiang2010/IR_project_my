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
	 *  ��ƽ�������㷨  �󲿷�Ӧ���е����ѡ��
	 *  ��ΪK�࣬�������ƶ�Ϊt����С��t��ʱ��ֹͣ���࣬�ض�
	 *  1. ��ȡ��ѯ���ص��ĵ�������
	 *  2. ������������ѡ��  ��ν�ά����  ���ٽ�ά
	 *  3. �ڽ�ά���������������
	 *  4. ����ÿ�����һ����ǩ�� ���ڴ�����Ϣ�ı�ǩ���ɣ�ֻ�Ǹ��ݴر������Ϣ�����ǩ��һ�ַ�������ӽ����ĵ��ĵ��ı���
	 *  
	 *  ���壺 Nƪ�ĵ��������������ƶȣ�����һ����ά�����У����м�¼�������ĵ���id������
	 *  
	 */
	double sim_threshold = 0.05;
	double TFIDF_THRESHOLD = 0.01;
	ArrayList<Integer> doclist = new ArrayList<Integer>();
	ArrayList<Double> docScoreList ;
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Clustering_GAAC build = new Clustering_GAAC();
		ArrayList<Integer> doclist = new ArrayList<Integer>();
		ArrayList<Double> docScoreList = new ArrayList<Double>();
		for(int j =2;j<100;j++){
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
		  Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����

          System.out.println("�ɹ�����MySQL��������");
          // һ��Connection����һ�����ݿ�����
          Connection conn = DriverManager.getConnection(url);
          System.out.println("���ӳɹ�");
          
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
	
	// ��ӡ��ϸ��Ϣ
	public void print(){
		int s = result.size();		
		for ( int j =0; j < s; j++){
			System.out.println(result.get(j) );
		}
	}
	
	// ʵ�ֱȽ��������ڴص�����
	class com implements Comparator<ClusterDetail>{

		@Override
		public int compare(ClusterDetail o1, ClusterDetail o2) {
			// TODO Auto-generated method stub
			return (int)(o2.score - o1.score);
		}
		
	}
	// �����ĵ��÷ֽ�������
	class com_doc implements Comparator<Integer>{

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
//			System.out.println( o1 + "  " + o2);
			return (int)(docScoreList.get(o2) - docScoreList.get(o1));
		}	
	}
	//  ���ۺõĴ�����ͬʱ�����ڵ��ĵ�������÷�����, ���ĵ�id������ӳ�����
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
	
	// ���ຯ���� ��������Ҫ������ĵ��б�
		// �����Ǿ��������б�
		
		//PriorityQueue<E>
	    HashMap<Integer, HashMap<Integer, Double>> vectors = new HashMap<Integer, HashMap<Integer, Double>>();
		ArrayList<ClusterDetail> result = new ArrayList<ClusterDetail>();
		double [][] simValues;
		public  ArrayList<ClusterDetail> cluster
			(ArrayList<Integer> doclist,ArrayList<Double> docScoreList) 
					throws ClassNotFoundException, SQLException{
			this.docScoreList = docScoreList;
			// ��������ĵ�����������ģ���������ӳ��һ�£���0λ���ϵľ����ĵ� 0��Ӧ��ʵ���ĵ�
			if(result!=null) result.clear();
			
			getAllVectors(doclist);  // ��ȡ�����ĵ����ϵ� ����
			
			int docSize = doclist.size();
			// ������е��ĵ���û�б����뵽��Ĵ���
			simValues = new double[docSize][docSize];
			ClusterDetail  temp;
			// ��ʼ�� docSize ����
			for(int k =0;k<docSize;k++){
				temp = new ClusterDetail();
				temp.clusterCenter = vectors.get(k);
				temp.title ="" ;
				temp.clusterSize =1;
				temp.docList.add(k);  // ���ھ��൱�ڴ��� �ĵ����Ϊ 0 - (docsize-1) ���ĵ��ľ���
				result.add(temp);
			}
			// ��������֮������ƶ�
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
			//  ��ʼ����
			//  ����������� һ���Ǵص���Ŀ, һ�������ƶȣ���һ�������ֹͣ����
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

			calCluster_center_and_score(docScoreList); // ��������
			sortClusters(doclist);   //����
			getClusterTitle();
			return result;
		}
		// ���� �ص����ģ������ҵ���������������ĵ�
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
							clu.score += docScores.get(docIdd); // ����÷�֮��
						   //	System.out.println(docIdd);
						   	tempDocDetail = vectors.get(docIdd);
						   	keyset = tempDocDetail.keySet();
						   	it = keyset.iterator();
						   	while(it.hasNext()){
						   		termId = it.next();
						   		//System.out.println(center);
						   		if(center.containsKey(termId)){  // �����Ѿ������ô���
						   			center.put(termId, center.get(termId) + tempDocDetail.get(termId));
						   		}else{ // ���Ĳ������ô���
						   			center.put(termId,tempDocDetail.get(termId));
						   		}
						   	}
						}
						// �����������ĵ�����ص����ģ����ҽ���ЩȨ�غ�С�Ĵ���ȥ��
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
						// �ҵ�����������ĵ�
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
				
		
		// �ϲ�������
		public boolean joinTwoClusters(int c1,int c2){
			if(c1 <0||c2<0) return false;
			ClusterDetail tt1 = result.get(c1);
			ClusterDetail tt2 = result.get(c2);
			ArrayList<Integer> tt2List = tt2.docList;
			int s = tt2List.size();
			for(int i =0;i<s;i++){
				tt1.docList.add(tt2List.get(i));
			}
			tt1.clusterSize += tt2.clusterSize;  // �ϲ��Ժ󣬴ش�СΪ����֮��
			result.remove(c2); // �ϲ��Ժ�ɾ��һ��
			return true;
		}
		
		// �� �����ص����ƶ�
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
			simV = simV/(s*(s-1)); // ���ﱾ��Ҫ����2��ʡ�Բ�Ӱ������
			return simV;
		}

		
		
		// ���������ĵ������ƶ�
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
		// �����ݿ��ж�ȡ���е� ��Ҫ�ĵ��� ����
		public  boolean getAllVectors(ArrayList<Integer> doclist) throws ClassNotFoundException, SQLException{
			if(vectors !=null) vectors.clear();
			Connection conn = null;
	        String url = "jdbc:mysql://localhost:3306/ir_project?"+"user=root&password=qiang&useUnicode=true&characterEncoding=UTF8";
	        Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����
	        System.out.println("�ɹ�����MySQL��������");
	        conn = DriverManager.getConnection(url);
	        System.out.println("���ӳɹ�");
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
						vectors.put(i, BuildDocVectorTable.normalize(vec)); // ��һ��
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
