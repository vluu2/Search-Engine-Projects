package edu.csula.cs454.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Indexing 
{
	static Datastore mdb;
	
	public static void main(String[] args) 
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		
		Morphia morphia = new Morphia();
		morphia.map(metaData.class);
		morphia.map(wordData.class);
		mdb = morphia.createDatastore(mongo, "cs454");
		
		String before;
		String[] text;
		String currentWord;
		
		boolean decider = false;
		List<wordData> currentWordList = mdb.find(wordData.class).asList();
		List<metaData> currentUrlList = mdb.find(metaData.class).asList();
		
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		
		
		for(int i = 0; i < currentWordList.size(); i++)
		{	
			currentWord = currentWordList.get(i).getWord();
			ArrayList<String> pageContainWord = new ArrayList<String>();
			
			for(int j = 0; j < currentUrlList.size(); j++)
			{				
				before = currentUrlList.get(j).getText();
				text = before.split(" ");
						
				for(int k = 0; k < text.length; k++)
				{
					if(currentWord.equals(text[k]))
					{
						decider = true;
					}
				}
				
				if(decider == true)
				{				
					pageContainWord.add( currentUrlList.get(j).getHash() );
					break;
				}
				
			}
			
			hm.put(currentWord, pageContainWord);
			
			currentWordList.get(i).setHashMap(hm);	
			
			tfidf(currentUrlList.size(), hm);
				
		}
		
		printAll(currentUrlList);

	}
//-----------------------------------------------------------------------------------------------------------------------		
	public static void tfidf(int totalDoc, HashMap<String, ArrayList<String>> hm)
	{				
		System.out.println("In TFIDF");
		
		double value = 0;
		
		HashMap<String, ArrayList<String>> filterHm = hm;
	
		int tf = 0; //tf is the number of times that t occurs in the document
		
		int df = 0; //df: the number of documents that contain t
					//t is the "word" that we're indexing
	
		int N = totalDoc; //cursor.count();	//Total number of documents
		
		double tfidfweight;
		
		List<metaData> currentUrlList = mdb.find(metaData.class).asList();
				
		String word;
		
		String[] text; 
		
		Map.Entry<String, ArrayList<String>> currentDoc;
		Set<Entry<String, ArrayList<String>>> set = filterHm.entrySet();
		Iterator<Entry<String, ArrayList<String>>> i = set.iterator();
		
		ArrayList<String> urlsWithWord;
		
		metaData findObj;
		ArrayList<HashMap> wordAndTfidf;
		
		
		while(i.hasNext())
		{	
			System.out.println("In TFIDF While Loop");
			
			currentDoc = (Map.Entry<String, ArrayList<String>>) i.next();
			word = currentDoc.getKey();		
			
			urlsWithWord = currentDoc.getValue();
//------------------------------------------------------------------------------------------------------------			
			for(int k = 0; k < urlsWithWord.size(); k++)				//THIS PART IS STILL UNFINISHED
			{
//				System.out.println("In TFIDF While Loop For Loop 1");
				ArrayList<HashMap> allTfidf = new ArrayList<HashMap>();
									
				findObj = mdb.find(metaData.class, "hashTitle", urlsWithWord.get(k)).get();
				text = findObj.getText().split(" ");
				
				for(int j = 0; j < text.length; j++)
				{
//					System.out.println("In TFIDF While Loop For Loop 2");
					if(word.equals(text[j]))
					{
						tf++;
					}
				}
								
				df = currentDoc.getValue().size();
				
				tfidfweight = Math.log(1 + tf) * Math.log10(N/df);
				
				HashMap index = new HashMap();
				index.put(word, tfidfweight);
				
				wordAndTfidf = findObj.getTfidfArrList();
				
				System.out.println("In TFIDF While Loop right before add index");
				
				wordAndTfidf.add(index);
				findObj.setTfidfArrList( wordAndTfidf );
//				mdb.save(findObj);
				allTfidf.add(index);
			}
			
		}
	}	
//--------------------------------------------------------------------------------------------------------------
	public static void printAll(List<metaData> currentUrlList)
	{
		String path = "C:/Users/Allen/Desktop/cs454 assignments/allUrlObjects.txt";
        Writer output = null;
        File file = new File(path);
        
        try 
        {
			output = new BufferedWriter(new FileWriter(file));
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
        
       
        for(int p = 0; p < currentUrlList.size(); p++)
        {
        	try 
        	{		
	        	output.write("{");
				((BufferedWriter) output).newLine();
				output.write("\t\"id\": " + currentUrlList.get(p).Id);
				((BufferedWriter) output).newLine();
				output.write("\t\"Path\": " + "\"" + currentUrlList.get(p).getPath() + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Text\": " + "\"" + currentUrlList.get(p).getText() + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Url\": " + "\"" + currentUrlList.get(p).getUrl() + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Title\": " + "\"" + currentUrlList.get(p).getTitle() + "\"");
				((BufferedWriter) output).newLine();
				
				output.write("\t\"hashTitle\": " + "\"" + currentUrlList.get(p).getHash() + "\"");
				
//				for(int g = 0; g < currentUrlList.get(p).getTfidfArrList().size(); g++)
//				{
//					output.write("TFIDF: " + currentUrlList.get(p).getTfidfArrList());//.get(g)
//					((BufferedWriter) output).newLine();
//				}
			
				((BufferedWriter) output).newLine();
				output.write("}");
				((BufferedWriter) output).newLine();
			} 
        	catch (IOException e) 
        	{
				e.printStackTrace();
			}
        }
        
        try 
		{
			output.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}

//-------------------------------------------------------------------------
/*
1 get list of all metaData objects

2 each doc, split text into array

3 go through each term in the array and append to hashmap (check if it exist before appending)
	key is word and value is arraylist (arraylist of doc that has that word)

4 do tfidf then store that value

*/
//-------------------------------------------------------------------------