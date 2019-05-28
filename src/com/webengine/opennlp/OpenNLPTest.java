package com.webengine.opennlp;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OpenNLPTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Hello!");
		
		try {
			TextProcessor tp = new TextProcessor();
			tp.init();
			
			String originalcontent = "Are you coming from Krung Thep? My own flat is very large and has many windows.";
			//originalcontent = "You load a chair in the car and I better read the best book I find. I am busy with booking a flight, too.";
			
			//originalcontent = "Hast du vorhin die Katzen gesehen? Nein, das waren Hunde.";
			
			System.out.println("Original Content: " +originalcontent);
			
			System.out.println(" ");
			TextProcessingResult result = tp.process(originalcontent);
			
			
			//System.out.println("Result: " +result.toString());
			System.out.println(" ");
			System.out.println("Language: " +result.getLanguage());
			System.out.println(" ");
			System.out.println("Sentences: ");
			
			for (int i =0; i<result.getSentences().size(); i++) {
				System.out.println(" ");
				
			//	System.out.println(result.getSentences().get(i).getWords());
				
					Collection<Word> words =  result.getSentences().get(i).getWords();
					
					for (Iterator iterator = words.iterator(); iterator.hasNext();) {
						  Word word = (Word) iterator.next();   
						  
						  System.out.print(word.getStemmedWord()+"|"+word.getTag()+" ");;
						  
						}
				
				
				
			}
		
		
		
		} catch (IOException iox) {
			
			System.out.println("Exception: " +iox.getMessage());
			
		}
		
	}

}
