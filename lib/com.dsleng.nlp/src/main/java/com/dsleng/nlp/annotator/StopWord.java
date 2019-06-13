package com.dsleng.nlp.annotator;


import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.core.StopAnalyzer;
import java.util.*;


public class StopWord implements Annotator,CoreAnnotation<Pair<Boolean, Boolean>> {
	
	public static final String ANNOTATOR_CLASS = "stopword";
	
	public static final String CHECK_LEMMA = "check-lemma";
	public static final String STOPWORDS_LIST = "stopword-list";
	public static final String IGNORE_STOPWORD_CASE = "ignore-stopword-case";
	
	private static Class<? extends Pair> boolPair = Pair.makePair(true, true).getClass();
	private Properties props;
    private CharArraySet stopwords;
    private boolean checkLemma;
    
	public StopWord(String annotatorClass, Properties props) {
        this.props = props;

        this.checkLemma = Boolean.parseBoolean(props.getProperty(CHECK_LEMMA, "false"));

        if (this.props.containsKey(STOPWORDS_LIST)) {
            String stopwordList = props.getProperty(STOPWORDS_LIST);
            boolean ignoreCase = Boolean.parseBoolean(props.getProperty(IGNORE_STOPWORD_CASE, "false"));
            this.stopwords = getStopWordList(stopwordList, ignoreCase);
        } else {
            this.stopwords = (CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET;
        }
    }
    
	@Override
	public void annotate(Annotation annotation) {
		if (stopwords != null && stopwords.size() > 0 && annotation.containsKey(TokensAnnotation.class)) {
            List<CoreLabel> tokens = annotation.get(TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                boolean isWordStopword = stopwords.contains(token.word().toLowerCase());
                boolean isLemmaStopword = checkLemma ? stopwords.contains(token.word().toLowerCase()) : false;
                Pair<Boolean, Boolean> pair = Pair.makePair(isWordStopword, isLemmaStopword);
                token.set(StopWord.class, pair);
            }
        }
		
	}


	@Override
	public Set<Class<? extends CoreAnnotation>> requires() {
	    return Collections.unmodifiableSet(new ArraySet<>(Arrays.asList(
	        CoreAnnotations.TextAnnotation.class,
	        CoreAnnotations.TokensAnnotation.class,
	        CoreAnnotations.SentencesAnnotation.class
	        //CoreAnnotations.PartOfSpeechAnnotation.class
	    )));
	  }

	  @Override
	  public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
	    return Collections.singleton(CoreAnnotations.LemmaAnnotation.class);
	  }
	  
	  @Override
	    @SuppressWarnings("unchecked")
	    public Class<Pair<Boolean, Boolean>> getType() {
	        return (Class<Pair<Boolean, Boolean>>) boolPair;
	    }

	  public static CharArraySet getStopWordList(String stopwordList, boolean ignoreCase) {
	        String[] terms = stopwordList.split(",");
	        System.out.println(terms);
	        CharArraySet stopwordSet = new CharArraySet(Arrays.asList(terms), ignoreCase);
//	        for (String term : terms) {
//	            stopwordSet.add(term);
//	        }
	        return CharArraySet.unmodifiableSet(stopwordSet);
	  }
   
}
