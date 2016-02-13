package edu.csula.cs454.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


/* 
 * We used http://www.mkyong.com/java/jsoup-html-parser-hello-world-examples/ as reference for our extractor 
 * We used http://jsoup.org/cookbook/extracting-data/example-list-links to get the http links
 * 
 * 
 * 
 * 
 * */

public class BasicCrawler 
{
	public static void main(String[] args) 
	{			
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("cs454-db");
		DBCollection collection = db.getCollection("dummyTable");	
		
		int count = 1;
		String original = args[1];
		
		String url = args[0];
		String depth = args[2];
		String lvl = args[3];
				
		boolean ex = false;
		
		
		
		
		if( !(url.equals("-u")) || (!(depth.equals("-d"))) )  
		{
			System.out.println("url = " + url + "\n" + "depth = " + depth + "\n" + "lvl = " + lvl + "\n" +"INCORRECT PARAMETERS!!!!");
			System.exit(0);
		}
		
		ArrayList<String> avu = new ArrayList<String>();
		ArrayList<String> cUrl = new ArrayList<String>();
		cUrl.add(original);
		
		if(args.length == 4)
		{
			crawlUrl(collection, cUrl, args[3], count, avu, ex);
		}
		else if(args.length == 5)
		{
			String extract = args[4];		

			if( extract.equals("-e") )
			{
				ex = true;			
				crawlUrl(collection, cUrl, args[3], count, avu, ex);
			}
			else
			{
				System.exit(0);
			}		
		}
		else
		{
			System.out.println("INCORRECT # of PARAMETERS!!!!");
			System.exit(0);
		}		
	}
 //-------------------------------------------------------------------------------------------
	static void crawlUrl(DBCollection collection, ArrayList<String> currentList, String depth, int count, 
			ArrayList<String> p, boolean x)	
	{
		int tracker = count;
		
		ArrayList<String> list = new ArrayList<String>();
		
		int dep = Integer.parseInt(depth);
		Document doc;
		Elements links;
		String addUrl;
		
		if(dep != 0)
		{
			for(int i = 0; i < tracker; i++)		//possible reason for duplicates
			{
				try 
		        {
					doc = Jsoup.connect(currentList.get(i)).get();
					System.out.println(currentList.get(i));//doc
					
					p.add(currentList.get(i));
					
					try 
					{
						links = doc.select("a[href]");
						
						if(x == true)
						{	
							JSONObject tester = new JSONObject();
							tester.put("Name", doc.title());	//gets the name of official website
							tester.put("URL", currentList.get(i));
								
							String text = doc.select("body").text();
							tester.put("Text", text);
							
							System.out.println("All Text on site : " + text);
							
							DBObject dbObject = (DBObject) JSON.parse(tester.toString());
							collection.insert(dbObject);
						}					
						
						
						for (Element link : links) 
						{	
							addUrl = "" + link.absUrl("href");
							
							boolean same = false;
							for(int j = 0; j < p.size(); j++)
							{
								if(p.get(j).equals(addUrl))
								{
									same = true;
								}
							}
							
							if(same != true)
							{;
								list.add(addUrl);
							}					
						}
						
						System.out.println("Depth level: " + dep);
					
					} 
					catch (JSONException e) 
					{
						System.out.println("Catch: 1");
						e.printStackTrace();
					}						
				} 
				catch (IOException e) 
				{
					System.out.println("Catch: 2");
					e.printStackTrace();
				}
			} //end of for loop
			
			tracker = list.size();
			dep--;
			System.out.println("Does dep decrease? " + dep);	
			
			String newDep = "" + dep;
			System.out.println("Newdep contains: " + newDep);
			System.out.println("Tracker contains: " + tracker);
			
			crawlUrl(collection, list , newDep, tracker, p, x);
		}
		else
		{
			System.out.println("DONE");
			System.exit(0);
		}
		
	}
}