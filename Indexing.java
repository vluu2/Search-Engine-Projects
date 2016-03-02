package edu.csula.cs454.example;

import java.io.IOException;
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

//		DB db = mongo.getDB("cs454-db");
//		DBCollection word = db.getCollection("UniqueWords");	
//		DBCollection connection = db.getCollection("dummyTable");
		
		Morphia morphia = new Morphia();
		morphia.map(metaData.class);
		morphia.map(wordData.class);
		mdb = morphia.createDatastore(mongo, "cs454");
		
//		JSONObject djo;
//		DBCursor cursor = connection.find();	
//		BasicDBObject idk;			
//		DBCursor wordCursor = word.find();
//		BasicDBObject wordObj;
		
		String before;
		String[] text;
		String currentWord;
		
		boolean decider = false;
		List<wordData> currentWordList = mdb.find(wordData.class).asList();
		List<metaData> currentUrlList = mdb.find(metaData.class).asList();
		
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		
		
		for(int i = 0; i < currentWordList.size(); i++)
		{
//			wordObj = (BasicDBObject) wordCursor.next();
//			currentWord = wordObj.getString("Word");
			
			currentWord = currentWordList.get(i).getWord();
			ArrayList<String> pageContainWord = new ArrayList<String>();
			
			for(int j = 0; j < currentUrlList.size(); j++)	//while(cursor.hasNext())
			{
//				idk = (BasicDBObject) cursor.next();
				
				before = currentUrlList.get(j).getText();	//idk.getString("Text");
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
//					JSONObject add = (JSONObject) idk;
					
					pageContainWord.add( currentUrlList.get(j).getHash() );
					break;
				}
				
			}
			
			hm.put(currentWord, pageContainWord);
			
			currentWordList.get(i).setHashMap(hm);
		}

	}
//-----------------------------------------------------------------------------------------------------------------------		
	public double tfidf(int docCount, int totalDoc, HashMap<String, ArrayList<String>> hm)	//String word, 
	{		
		double value = 0;
		
		HashMap<String, ArrayList<String>> filterHm = hm;
	
		int tf = 0; //tf is the number of times that t occurs in the document
		
		int df = 0; //df: the number of documents that contain t
					//t is the "word" that we're indexing
	
		int N = totalDoc; //cursor.count();	//Total number of documents
		
		double tfidfweight;
		
		List<metaData> currentUrlList = mdb.find(metaData.class).asList();
				
		String text;
		
		//String currentWord = word;
		
		String[] arr; 
		
		Map.Entry<String, ArrayList<String>> currentDoc;
		Set<Entry<String, ArrayList<String>>> set = filterHm.entrySet();
		Iterator<Entry<String, ArrayList<String>>> i = set.iterator();
		
		while(i.hasNext()) //for(int i = 0; i < filterHm.size(); i++)	//while (cursor.hasNext()) 
		{	
//			djo = (JSONObject) cursor.next();
			
//			try 
//			{
//				text = (String) djo.get("Text");
			
			currentDoc = (Map.Entry<String, ArrayList<String>>) i.next();
			text = currentDoc.getKey();	//currentUrlList.get(i).getText();
			arr = text.split(" ");			

//------------------------------------------------------------------------------------------------------------			
			for(int k = 0; k < currentDoc.getValue().size(); k++)				//THIS PART IS STILL UNFINISHED
			{
				ArrayList<HashMap> allTfidf = new ArrayList<HashMap>();
				
				for(int j = 0; j < arr.length; j++)
				{
					if(text.equals(arr[j]))
					{
						tf++;
					}
				}
								
				df = currentDoc.getValue().size();
				
				tfidfweight = Math.log(1 + tf) * Math.log10(N/df);
				
				HashMap index = new HashMap();
				index.put(text,tfidfweight);
				
				allTfidf.add(index);
			}
//------------------------------------------------------------------------------------------------------------			
						
//				for(int o = 0; o < args.length; o++)
//				{
//					for(int j = 0; j < arr.length; j++)
//					{
//						if(args[i].equals(arr[j]))
//						{
//							tf++;
//						}
//					}
//				}			
//			} 
//			catch (JSONException e1) 
//			{
//				e1.printStackTrace();
//			}		
//			if(tf > 0)
//			{
//				df++;
//			}
		
//			try 
//			{
//				djo.put("Index", tfidfweight);
//			} 
//			catch (JSONException e) 
//			{
//				e.printStackTrace();
//			}
			
		}
		
		
		return value;
	}
	
//--------------------------------------------------------------------------------------------------------------
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