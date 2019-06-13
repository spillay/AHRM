/**
 * 
 */
package com.dsleng.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.util.Iterator;



import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import java.util.Map;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dsleng.nlp.annotator.StopWord;
import com.google.common.collect.Lists;

/**
 * @author suresh
 *
 */
public class SimplePL {
	List<String> words = new ArrayList<>();
    List<String> posTags = new ArrayList<>();
    List<String> nerTags = new ArrayList<>();
    StanfordCoreNLP pipeline;
    Boolean onlyToken = false;
    
    public static String nltk_sw = "i,me,my,myself,we,our,ours,ourselves,you,you're,you've,you'll,you'd,your,yours,yourself,yourselves,he,him,his,himself,she,she's,her,hers,herself,it,it's,its,itself,they,them,their,theirs,themselves,what,which,who,whom,this,that,that'll,these,those,am,is,are,was,were,be,been,being,have,has,had,having,do,does,did,doing,a,an,the,and,but,if,or,because,as,until,while,of,at,by,for,with,about,against,between,into,through,during,before,after,above,below,to,from,up,down,in,out,on,off,over,under,again,further,then,once,here,there,when,where,why,how,all,any,both,each,few,more,most,other,some,such,no,nor,not,only,own,same,so,than,too,very,s,t,can,will,just,don,don't,'should',should've,now,d,ll,m,o,re,ve,y,ain,aren,aren't,couldn, couldn't,didn, didn't,doesn,doesn't,hadn, hadn't,hasn,hasn't,haven,haven't,isn,isn't,ma,mightn,mightn't,mustn,mustn't,needn,needn't,shan,shan't,shouldn,shouldn't,wasn,wasn't,weren,weren't,won, won't,wouldn,wouldn't"; 
    public SimplePL() {
		System.out.println("SimplePL");
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma,stopword, ner, parse, dcoref");
        props.setProperty("customAnnotatorClass.stopword", "com.dsleng.nlp.annotator.StopWord");
        pipeline = new StanfordCoreNLP(props);
	}
	public SimplePL(String stopWords,Boolean onlyToken) {
		System.out.println("SimplePL with Custom Stop Words");
		this.onlyToken = onlyToken;
		String customStopWordList = stopWords;
		Properties props = new Properties();
		if (onlyToken) {
			props.setProperty("annotators", "tokenize, ssplit,stopword");
		} else {
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma,stopword, ner, parse, dcoref");
		}
        props.setProperty("customAnnotatorClass.stopword", "com.dsleng.nlp.annotator.StopWord");
        props.setProperty(StopWord.STOPWORDS_LIST,customStopWordList);
        pipeline = new StanfordCoreNLP(props);
	}
	public List<String> getTokens(){ return words; }
	public List<String> getPOS(){ return posTags; }
	public List<String> getNER(){ return nerTags; }
	public boolean process(String text) {
		words = new ArrayList<>();
	    posTags = new ArrayList<>();
	    nerTags = new ArrayList<>();
	    
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

       
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                words.add(word);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                posTags.add(pos);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                nerTags.add(ne);
                
                Pair<Boolean, Boolean> stopword = token.get(StopWord.class);
                System.out.println("============================================================");
                //System.out.println(word + stopword.first() + "," + stopword.second());
                System.out.println(word + stopword);
            }
        }
//        System.out.println(words.toString());
//        System.out.println(posTags.toString());
//        System.out.println(nerTags.toString());
//
//        System.out.println( "End of Processing" );
        
        return true;
	}
	private JSONArray convertArray(List<String> strArray) {
		JSONArray jsArray = new JSONArray();
	    for (int i = 0; i < strArray.size(); i++) {
	         jsArray.put(strArray.get(i));
	     }
	    return jsArray;
	}
	public String processSentence(String text) {
		JSONObject obj = new JSONObject();
		
		List<String> words = new ArrayList<>();
	    List<String> posTags = new ArrayList<>();
	    List<String> nerTags = new ArrayList<>();
	    List<String> stopIndicator = new ArrayList<>();
	    List<String> tokensSW = new ArrayList<>();
	    
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

       
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                words.add(word);
                if (!this.onlyToken) {
	                // this is the POS tag of the token
	                String pos = token.get(PartOfSpeechAnnotation.class);
	                posTags.add(pos);
	                // this is the NER label of the token
	                String ne = token.get(NamedEntityTagAnnotation.class);
	                nerTags.add(ne);
                }
                Pair<Boolean, Boolean> stopword = token.get(StopWord.class);
                if (stopword.first()) {
                	stopIndicator.add("1");
                } else {
                	stopIndicator.add("0");
                }
            }
            
        }
        JSONArray jwArray = this.convertArray(words);
        obj.put("tokens", jwArray);
        
        if (!this.onlyToken) {
	        JSONArray jpArray = this.convertArray(posTags);
	        obj.put("pos", jpArray);
	        
	        JSONArray jnArray = this.convertArray(nerTags);
	        obj.put("ne", jnArray);
        }
        JSONArray swArray = this.convertArray(stopIndicator);
        obj.put("sw", swArray);
        
        int pos = 0;
        for(String tok: words) {
        	if (stopIndicator.get(pos).compareTo("0") == 0) {
        		tokensSW.add(tok);
        	}
        	pos++;
        }
        JSONArray tswArray = this.convertArray(tokensSW);
        obj.put("tokensSW", tswArray);
        
        return obj.toString();

	}
	public String processWithTree(String text) {    
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

       
        for (CoreMap sentence : sentences) {
           
            Tree tree = sentence.get(TreeAnnotation.class); 

//            System.out.println("Tree:\n"+ tree); 
//            System.out.println("Tree:\n"+ tree.pennString()); 
            
            
            List<CoreLabel> coreLabels = sentence.get(TokensAnnotation.class);

            tree.label();

            //System.out.println(tree);


            JSONObject json = toJSON(tree, coreLabels.iterator());
//            System.out.println(json.toString(2));
//            System.out.println(json);
            return json.toString();
            // This is the dependency graph of the sentence 

            //SemanticGraph dependencies = sentence.get(CollapsedDependenciesAnnotation.class); 

            //System.out.println("Dependencies\n:"+ dependencies);
        }
        

//        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class); 
//
//        System.out.println("Map of the chain:\n" + graph);
//        System.out.println( "End of Processing" );
        
        return "{}";
	}
	public static JSONObject toJSON(Tree tree, Iterator<CoreLabel> labels) throws JSONException {

	    List<JSONObject> children = Lists.newArrayList();
	    for (Tree child : tree.getChildrenAsList()) {
	      children.add(toJSON(child, labels));
	    }

	    JSONObject obj = new JSONObject();

	    if(tree.isLeaf()){
	      CoreLabel next = labels.next();

	      String word = next.get(TextAnnotation.class);
	      String pos = next.get(PartOfSpeechAnnotation.class);
	      String ne = next.get(NamedEntityTagAnnotation.class);

	      obj.put("word", word);
	      obj.put("pos", pos);
	      obj.put("ne", ne);

	    }else{
	      obj.put("type", tree.label());
	    }
	    if (children.size() > 0) {
		    return new JSONObject()
		        .put("data", obj)
		        .put("children", new JSONArray(children));
	    } else {
	    	return new JSONObject()
			        .put("data", obj);
	    }
	  }

}
