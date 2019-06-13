package com.dsleng.nlp;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		SimplePL o = new SimplePL(SimplePL.nltk_sw,true);
		String text = "She went to America last week.";
		text = "As you know, the Terrible SEC is conducting an informal inquiry into related party transactions Enron did with LJM.  We are cooperating fully and are providing the information the SEC has requested.  However, we have learned today that the SEC is extremely sensitive about us using their inquiry as a shield to not answer certain questions -- i.e. I can't answer that because of a pending SEC inquiry.  As a result, we have been advised that we should not make any comment about the SEC inquiry in any context.  This extends beyond media and includes any interactions with customers, regulators, etc.  Please forward this message on to anyone in your group who this might impact.  Call me if you have any questions.  Thank you.";
		//text = "What is the expected delivery date ?";
		//System.out.println(o.process(text));
		//System.out.println(o.processWithTree(text));
		for(int i=0;i <20;i++) {
			System.out.println(i);
			System.out.println(o.processSentence(text));
		}
	}

}
