package edu.csula.cs454.example;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Indexing 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("cs454-db");
		DBCollection collection = db.getCollection("dummyTable");
		
		DBCursor cursor = db.getCollection("dummyTable").find();
    	
		JSONObject djo;
		String text;
		String[] arr;
		
    	int tf = 0; //tf is the number of times that t occurs in the document
	
		int df = 0; //df: the number of documents that contain t
					//t is the "word" that we're indexing

		int N = cursor.count();	//Total number of documents
		double tfidfweight;
		
		while (cursor.hasNext()) 
		{	
			djo = (JSONObject) cursor.next();
			
			try 
			{
				text = (String) djo.get("Text");
				arr = text.split(" ");
						
				for(int i = 0; i < args.length; i++)
				{
					for(int j = 0; j < arr.length; j++)
					{
						if(args[i].equals(arr[j]))
						{
							tf++;
						}
					}
				}
				
			} 
			catch (JSONException e1) 
			{
				e1.printStackTrace();
			}
			
			if(tf > 0)
			{
				df++;
			}
			
			tfidfweight = Math.log(1 + tf) * Math.log10(N/df);
			
			try 
			{
				djo.put("Index", tfidfweight);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
		}

	}

}
