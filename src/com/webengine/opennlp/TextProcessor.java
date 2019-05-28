package com.webengine.opennlp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import opennlp.tools.stemmer.PorterStemmer;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.model.BaseModel;


public class TextProcessor{
	
	private final static Logger LOGGER = Logger.getLogger(TextProcessor.class.getName());
	
	

	/** constant for german language */
	private final static String de_lang = "deu";
	/** constant for english language */
	private final static String en_lang = "eng";
	/** constant for tokenizer model */
	private final static String token = "token";
	
	/** constant for pos maxent model */
	private final static String posMaxEnt = "pos-maxent";
	
	/** constant for sentence extraction model */
	private final static String sentence = "sent";
	
	/** constant for lemmatization model */
	private final static String lemma = "lemma";
	
	
	
	/** the models used. */
	Map<String, Map<String, BaseModel>> models = new HashMap<String, Map<String, BaseModel>>();
	
	/** the models used. */
	Map<String, Collection<String>> stopwords = new HashMap<String, Collection<String>>();
	/** the categorizer to detect language */
	private LanguageDetector myCategorizer;

	/** the constructor */
	public TextProcessor() {
		super();

	}

	/** loads the models */
	
	public void init() throws IOException {
		// load the models
		
		ClassLoader classLoader = this.getClass().getClassLoader();
		//resources folder must be added to build path!!!
		
		stopwords.put(de_lang, getWords(classLoader.getResource("models/"+de_lang+"/stopp.txt")));
		stopwords.put(en_lang, getWords(classLoader.getResource("models/"+en_lang+"/stopp.txt")));
		
		myCategorizer = new LanguageDetectorME(new LanguageDetectorModel(
				new FileInputStream(classLoader.getResource("models/langdetect-183.bin").getFile())   ));
		HashMap<String, BaseModel> deModels = new HashMap<String, BaseModel>();
		deModels.put(token, new TokenizerModel(
				new FileInputStream(classLoader.getResource("models/" + de_lang + "/token.bin").getFile()) ));
		deModels.put(sentence, new SentenceModel(
				new FileInputStream(classLoader.getResource("models/" + de_lang + "/sent.bin").getFile()) ));
		deModels.put(posMaxEnt, new POSModel(
				new FileInputStream(classLoader.getResource("models/" + de_lang + "/"+posMaxEnt+".bin").getFile()) ));

		
		
		
		
		HashMap<String, BaseModel> enModels = new HashMap<String, BaseModel>();
		enModels.put(token, new TokenizerModel(
				new FileInputStream(classLoader.getResource("models/" + en_lang + "/token.bin").getFile()) ));
		enModels.put(sentence, new SentenceModel(
				new FileInputStream(classLoader.getResource("models/" + en_lang + "/sent.bin").getFile()) ));
		enModels.put(posMaxEnt, new POSModel(
				new FileInputStream(classLoader.getResource("models/" + en_lang + "/"+posMaxEnt+".bin").getFile()) ));
		
		
		
		
		models.put(en_lang, enModels);
		models.put(de_lang, deModels);
		// load en models
	}

	private Collection<String> getWords(URL resource) {
		Collection<String> result = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(resource.getFile()), "UTF-8"))) {

			//br returns as stream and convert it into a List
			result =  br.lines().collect(Collectors.toList());

		} catch (IOException e) {
			LOGGER.error("Error reading stopwqords");
		}
		return result;
		
	}

	/**
	 * process the incoming content.
	 * 
	 * @param content
	 *            a textfile
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public TextProcessingResult process(String content) throws FileNotFoundException, IOException {
		TextProcessingResult result = new TextProcessingResult();
		String language = detectLanguage(content).getLang();
		result.setLanguage(language);
		
		
		
		if (language.equals("deu")) {
			
			String[] sentences = extractSentences(language, content);
			
			Stemmer stemmer = new SnowballStemmer(ALGORITHM.GERMAN);
			
			
			for (String sentence : sentences) {
				Sentence s = new Sentence();
				Collection<String> words = extractWords(language, sentence);
			//words = words.stream().filter(word -> !stopwords.get(language).contains(word)).collect(Collectors.toList());;
				
				String[] tags = tag(language, words);
				String[] toks = tok(words);
				
				for (int i= 0; i<toks.length; i++) {
					
					if (!stopwords.get(language).contains(toks[i]) && !stopwords.get(language).contains(stemmer.stem(toks[i]).toString())) {
						Word w = new Word();				
						w.setTag(tags[i]);
						
						w.setStemmedWord(stemmer.stem(toks[i]).toString());
						
						s.getWords().add(w);
					}
					

					
				}
				
				
				result.getSentences().add(s);
			}
		
		} else 
			if (language.equals("eng")) {
				
				String[] sentences = extractSentences(language, content);
				
				ClassLoader classLoader = this.getClass().getClassLoader();
				InputStream dictLemmatizer = new FileInputStream(classLoader.getResource("models/" + en_lang + "/"+lemma+".bin").getFile()); 
				
				DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
				//Stemmer stemmer = new PorterStemmer();
				Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
				
				for (String sentence : sentences) {
					Sentence s = new Sentence();
					Collection<String> words = extractWords(language, sentence);
					//words = words.stream().filter(word -> !stopwords.get(language).contains(word.toLowerCase())).collect(Collectors.toList());;
					

					String[] tags = tag(language, words);					
					String[] toks = tok(words);

					String[] lemma = lemmatizer.lemmatize(toks, tags); 
										
					
					for (int i= 0; i<toks.length; i++) {
								
						System.out.println("Token: "+toks[i] +  " Lemma: " +lemma[i] + " Stem: " +stemmer.stem(toks[i]).toString());

						if (!stopwords.get(language).contains(toks[i].toLowerCase())  && !stopwords.get(language).contains(lemma[i])    ) {
							Word w = new Word();				
							w.setTag(tags[i]);
							if (lemma[i].equals("O")) {
								w.setStemmedWord(toks[i]); 
							} else
							w.setStemmedWord(lemma[i]);
							
							//w.setStemmedWord(stemmer.stem(toks[i]).toString());
							
							s.getWords().add(w);
						}
						

						
					}
					
					
					result.getSentences().add(s);
				}
				
				
			}
		
		
		return result;
	}

	public String[] tag(String language, Collection<String> words) {
		POSModel model = (POSModel) models.get(language).get(posMaxEnt);
		POSTaggerME tagger = new POSTaggerME(model);
		String[] wordsArray = words.stream().toArray(String[]::new);
		return tagger.tag(words.toArray(wordsArray));
	}

	
	public String[] tok(Collection<String> words) {
		
		String[] wordsArray = words.stream().toArray(String[]::new);
		return words.toArray(wordsArray);
	}
	
	
	/**
	 * 
	 * @param language
	 *            language of the sentence
	 * @param sentence
	 *            the text of the sentence
	 * @return the words of the sentence
	 * 
	 */
	private Collection<String> extractWords(String language, String sentence) {

		TokenizerModel model = (TokenizerModel) models.get(language).get(token);
		TokenizerME sentenceDetector = new TokenizerME(model);
		return Arrays.asList(sentenceDetector.tokenize(sentence));

	}

	/**
	 * detects the language of the given string.
	 * 
	 * @param content
	 *            the text
	 * @return the Language of the text
	 * 
	 */
	public Language detectLanguage(String content) {

		// Get the most probable language
		return myCategorizer.predictLanguage(content);
	}

	public String[] extractSentences(String language, String content) throws IOException {
		SentenceModel model = (SentenceModel) models.get(language).get(sentence);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		return sentenceDetector.sentDetect(content);
	}

	

}
