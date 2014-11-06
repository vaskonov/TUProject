// Entailment relative threshold

package eu.excitementproject.eop.biutee.text_understanding_project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.cyberneko.html.filters.Writer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.aliasi.util.Files;
import com.google.common.collect.Lists;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;

import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import gate.creole.annic.apache.lucene.store.InputStream;

public class TextUnderstandingProject {
	public final static int EntailmentsThreshold = 4;	// first form	
	public final static double EntailmentPortion = 0.15;	// second form
	public final static double EntailmentConfidenceThreshold = -0.3; // third form
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		try {
			/* load lap and biutee */
			//BiuTestUtils.assumeBiuEnvironment();
			LAPAccess lap = new BIUFullLAP(
					"../third-party/stanford-postagger-full-2008-09-28/models/left3words-wsj-0-18.tagger",
					"../third-party/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz",
					"localhost",
					8080);
			BiuteeEDA biutee = new BiuteeEDA();
			biutee.initialize(new ImplCommonConfig(new File("biutee.xml")));
			
			/* load jsons */
			
			JSONArray toTagJson = new JSONArray(Files.readFromFile(new File("../TUProject/test2.json")));
	        JSONObject possibleTagsJson = new JSONObject(Files.readFromFile(new File("../TUProject/train3.json")));
			List<String> tags = Lists.newArrayList(possibleTagsJson.keys());
			
			/* run biutee */
			
			for (int toTagIndex =0;toTagIndex < toTagJson.length();toTagIndex++){
				JSONObject curEntry = toTagJson.getJSONObject(toTagIndex);
				String t = curEntry.getString("input");
				JSONArray curTags = new JSONArray();
				
				
		        for(String tag : tags){
		            JSONArray arr = possibleTagsJson.getJSONArray(tag);
		            int entailments = 0;	// first & second form
		            //double confidenceSum = 0.0;	// third form
		            for(int exampleNumber=0;exampleNumber < arr.length();exampleNumber++){
		            	String h = (String) arr.get(exampleNumber);
		            	JCas jcas = lap.generateSingleTHPairCAS(t,h);
		    			TEDecision decision = biutee.process(jcas);
		            	if (decision.getDecision() == DecisionLabel.Entailment){	// first & second form
		            		entailments++;
		            		if (entailments / (double)arr.length() > EntailmentPortion) {	// second form
			            		curTags.put(tag);	
			            		break; 
		            		}
		            	}
		            }
		        }
		        
		        curEntry.put("tags", curTags);
			}
			
			/* write answers to file */
			PrintWriter out = new PrintWriter( "../TUProject/ans2.json");
			String s = toTagJson.toString();
			out.println(s);
			out.close();
			
			/* THE END */
			System.out.println("-- THE END --");
			
		} catch (IOException | ConfigurationException | EDAException | ComponentException | JSONException e) {
			
			e.printStackTrace();
		}
	}

}
