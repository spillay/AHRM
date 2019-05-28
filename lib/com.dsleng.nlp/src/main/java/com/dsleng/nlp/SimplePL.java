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
	public SimplePL() {
		System.out.println("SimplePL");
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
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
            }
            JSONArray jwArray = this.convertArray(words);
            obj.put("tokens", jwArray);
            
            JSONArray jpArray = this.convertArray(posTags);
            obj.put("pos", jpArray);
            
            JSONArray jnArray = this.convertArray(nerTags);
            obj.put("ne", jnArray);
            
            return obj.toString();
        }

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
