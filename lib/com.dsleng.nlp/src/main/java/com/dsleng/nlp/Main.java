package com.dsleng.nlp;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		SimplePL o = new SimplePL(SimplePL.nltk_sw);
		String text = "She went to America last week.";
		//text = "What is the expected delivery date ?";
		//System.out.println(o.process(text));
		//System.out.println(o.processWithTree(text));
		System.out.println(o.processSentence(text));
	}

}
