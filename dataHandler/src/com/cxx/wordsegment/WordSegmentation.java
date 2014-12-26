package com.ir.wordsegment;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.ir.hbm.bean.BodyTermBean;
import com.ir.hbm.bean.HtmlsBean;
import com.ir.hbm.dao.BodyTermDao;
import com.ir.hbm.dao.HtmlsDao;
import com.ir.hbm.util.HibernateSessionFactory;

/**
 * 
 * @author chenxiaoxu
 * @version 2014/11/11
 *
 */
public class WordSegmentation {
	
	public static void main(String[] args) throws IOException {
		
		/*****分词API使用示例******/
		
        String text = "新浪体育讯	C罗与梅西相互为敌，又相互激励。C罗的竞技雄心让梅西变得更为出色，而梅西在足球层面的饥渴感，也让C罗成为更强的球员。"
        		+ "自从葡萄牙巨星C罗降临西班牙首都后，西甲就极大地受益于两大巨星之间的健康竞争。刚刚结束的西甲第14轮比赛就是一个最好的例子"
        		+ "对塞尔塔的比赛，C罗上演了帽子戏法，其中包括一个点球，而第二天，梅西也用一个帽子戏法对C罗做出回应。"
        		+ "梅西在西甲已经完成21次帽子戏法，C罗则上演了23次帽子戏法，葡萄牙人是西甲历史上上演帽子戏法次数最多的球员。";
        
        //得到词项-词频MAP
//        Map<String, Integer> wordsFrenMaps = getWordsMap(text);
//        for ( Entry<String, Integer> entry:wordsFrenMaps.entrySet() )
//        {
//        	System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
//        }
        
        //以"|"号隔开的分词结果
        StringBuffer words = getWordsString(text);
        System.out.println(words);
        //sortSegmentResultByFren(wordsFrenMaps,topWordsCount);
        
        //以列表的形式返回分词结果
//        List<String> wordList = getWordsList(text);
//        for(String word:wordList){
//        	System.out.print("word ");
//        }
	}
	
	/**
	 * @FunName:getWordsMap
	 * @Description: Segment document into word term and its frequency
	 * @param text: Input document
	 * @return Map<String, Integer>: word and its frequency
	 * @throws IOException
	 */
	public static Map<String, Integer> getWordsMap(String text) throws IOException {
        Map<String, Integer> wordsFren=new HashMap<String, Integer>();
        IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
        Lexeme lexeme;
        while ((lexeme = ikSegmenter.next()) != null) {
            if(lexeme.getLexemeText().length()>1){
                if(wordsFren.containsKey(lexeme.getLexemeText())){
                    wordsFren.put(lexeme.getLexemeText(),wordsFren.get(lexeme.getLexemeText())+1);
                }else {
                    wordsFren.put(lexeme.getLexemeText(),1);
                }
            }
        }
        return wordsFren;
    }
	/**
	 * @FunName:getWordsList
	 * @Description: Segment document into word term
	 * @param text: Input document
	 * @return List<String>: word List
	 * @throws IOException
	 */
	public static List<String> getWordsList(String text) throws IOException{
        IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
        Lexeme lexeme;
		List<String> wordsList = new ArrayList<String>();
        while ((lexeme = ikSegmenter.next()) != null) {
            if(lexeme.getLexemeText().length()>1){
            	wordsList.add(lexeme.getLexemeText());
            }
        }
		for (int i = 0; i <wordsList.size() - 1; i++) { 
			for (int j = wordsList.size() - 1; j > i; j--) { 
				if (wordsList.get(j).equals(wordsList.get(i))) { 
					wordsList.remove(j); 
				} 
			} 
		} 
		return wordsList;
	}
	/**
	 * @FunName:getWord
	 * @Description: Segment document into word and store it in String, it is easy to print
	 * @param text: Input document
	 * @return StringBuffer: wors string
	 * @throws IOException
	 */
	public static StringBuffer getWordsString(String text) throws IOException{
        IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
        Lexeme lexeme;
		StringBuffer words = new StringBuffer();
        while ((lexeme = ikSegmenter.next()) != null) {
            if(lexeme.getLexemeText().length()>1){
            	words.append(lexeme.getLexemeText()+"|");
            }
        }
		return words;
	}
	/**
	 * @FunName:sortSegmentResultByFren
	 * @Description: Sort the result by the term frequency
	 * @param wordsFrenMaps
	 * @param topWrodsCount
	 * @return void
	 * @throws IOException
	 */
	public static void sortSegmentResultByFren(Map<String,Integer> wordsFrenMaps,int topWordsCount){
        System.out.println("排序前:================");
        Iterator<Map.Entry<String,Integer>> wordsFrenMapsIterator=wordsFrenMaps.entrySet().iterator();
        while (wordsFrenMapsIterator.hasNext()){
            Map.Entry<String,Integer> wordsFrenEntry=wordsFrenMapsIterator.next();
            System.out.println(wordsFrenEntry.getKey()+"             的次数为"+wordsFrenEntry.getValue());
        }
 
        List<Map.Entry<String, Integer>> wordFrenList = new ArrayList<Map.Entry<String, Integer>>(wordsFrenMaps.entrySet());
        Collections.sort(wordFrenList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        System.out.println("排序后:================");
        for(int i=0;i<topWordsCount&&i<wordFrenList.size();i++){
            Map.Entry<String,Integer> wordFrenEntry=wordFrenList.get(i);
            if(wordFrenEntry.getValue()>1){
                System.out.println(wordFrenEntry.getKey()+"             的次数为"+wordFrenEntry.getValue());
            }
        }
    }
}