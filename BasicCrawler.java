package edu.csula.cs454.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;


/* 
 * We used http://www.mkyong.com/java/jsoup-html-parser-hello-world-examples/ as reference for our extractor 
 * We used http://jsoup.org/cookbook/extracting-data/example-list-links to get the http links
 * We used http://www.mkyong.com/java/java-sha-hashing-example/
 */

public class BasicCrawler 
{
	static Datastore mdb = null;
	
	public static void main(String[] args) 
	{			 
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		
		Morphia morphia = new Morphia();
		morphia.map(metaData.class);
		morphia.map(wordData.class);
		mdb = morphia.createDatastore(mongo, "cs454");
		
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
			crawlUrl(cUrl, args[3], count, avu, ex);//word, collection, 
		}
		else if(args.length == 5)
		{
			String extract = args[4];		

			if( extract.equals("-e") )
			{
				ex = true;			
				crawlUrl(cUrl, args[3], count, avu, ex);//word, collection, 
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
	static void crawlUrl(ArrayList<String> currentList, String depth, int count, ArrayList<String> p, boolean x)
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
					
					links = doc.select("a[href]");
					
					if(x == true)
					{	
						metaData tester = new metaData();
						tester.setTitle(doc.title());	//gets the name of official website
						tester.setUrl(currentList.get(i));
							
						String text = doc.select("body").text();							
						tester.setText(text);
						
						checkWord(text);	//word, 
						
						System.out.println("All Text on site : " + text);
						
						try 
				        {			
							String hash = hashFunction(doc.title());
							tester.setHash(hash);
							String path = "C:/Users/Vincent/Desktop/CS454_SE/CrawledInfo/" + hash + ".html";
					        Writer output = null;
					        
					        File file = new File(path);				        
							output = new BufferedWriter(new FileWriter(file));									        
					        String docString = doc.toString();
					        output.write(docString);
							output.close();
							
							tester.setPath(path);
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
		
						mdb.save(tester);							
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
			
			crawlUrl(list , newDep, tracker, p, x); 
		}
		else
		{
			System.out.println("DONE");
			System.exit(0);
		}
		
	}
//-----------------------------------------------------------------------------------------------------------------
	public static String hashFunction(String name)
	{
		String toHash = name;
		MessageDigest digest;
		byte[] hash;
		String hashName = "";
		StringBuffer sb = new StringBuffer();
		try 
		{
			digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
						
	        for (int i = 0; i < hash.length; i++) 
	        {
	        	sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
	        }
				
			hashName = sb.toString();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		
		return hashName;
	}
//-----------------------------------------------------------------------------------------------------------------
	private static String readFile(String fileName) throws IOException
	{	
		//File file = new File(fileName);
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		String line = null;
		StringBuilder sb = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		try
		{
			while((line = reader.readLine()) != null)
			{
				sb.append(line);
				sb.append(ls);
			}
			return sb.toString();
		} 
		finally
		{
			reader.close();
		}
	}
//-----------------------------------------------------------------------------------------------------------------	
	public static void checkWord(String texts)
	{
		String[] array = texts.split(" ");
		
		try 
		{
			String file = "C:/Users/Vincent/Desktop/CS454_SE/stopwords.txt"; // C:/Users/Vincent/Desktop/CS454_SE/stopwords.txt
			String check = readFile(file);
		
			for(int i = 0; i < array.length; i++)
			{
				if(array[i].matches(check))
				{
					addWord(array[i]);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}			
//-----------------------------------------------------------------------------------------------------------------
	public static void addWord(String uniqueWord)
	{
		wordData mWord = new wordData();
		
		List<wordData> currentList = mdb.find(wordData.class).asList();
	
		if(currentList.size() == 0)
		{
			mWord.setWord(uniqueWord);
			mdb.save(mWord);
		}
		else
		{
			int count = 0;
			
			for(int i = 0; i < currentList.size(); i++)
			{
				if( !(uniqueWord.equals(currentList.get(i).getWord())) )
				{
					count++;
				}
			}
			if(count == currentList.size())
			{
				mWord.setWord(uniqueWord);

				mdb.save(mWord);
			}
			
		}
	}
//-----------------------------------------------------------------------------------------------------------------
}