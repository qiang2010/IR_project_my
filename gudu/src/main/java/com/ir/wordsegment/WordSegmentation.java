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

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 
 * @author chenxiaoxu
 * @version 2014/11/11
 *
 */
public class WordSegmentation {
	
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