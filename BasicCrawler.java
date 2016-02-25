package edu.csula.cs454.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


/* 
 * We used http://www.mkyong.com/java/jsoup-html-parser-hello-world-examples/ as reference for our extractor 
 * We used http://jsoup.org/cookbook/extracting-data/example-list-links to get the http links
 * We used http://www.mkyong.com/java/java-sha-hashing-example/
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
		
		DBCollection collection = db.createCollection("dummyTable", new BasicDBObject());	//.getCollection("dummyTable");	
		
//		DB worddb = mongo.getDB("wordDB");
		DBCollection word = db.createCollection("UniqueWords", new BasicDBObject());	
		
		
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
			crawlUrl(word, collection, cUrl, args[3], count, avu, ex);
		}
		else if(args.length == 5)
		{
			String extract = args[4];		

			if( extract.equals("-e") )
			{
				ex = true;			
				crawlUrl(word, collection, cUrl, args[3], count, avu, ex);
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
	static void crawlUrl(DBCollection word, DBCollection collection, ArrayList<String> currentList, String depth, int count, 
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
							tester.put("Title", doc.title());	//gets the name of official website
							tester.put("URL", currentList.get(i));
								
							String text = doc.select("body").text();							
							tester.put("Text", text);
							
							checkWord(word, text);
							
							System.out.println("All Text on site : " + text);
														
							try 
					        {			
								String hash = hashFunction(doc.title());
								tester.put("hashTitle", hash);
								String path = "C:/Users/Allen/Desktop/cs454 assignments/TESTER/" + hash + ".html";
						        Writer output = null;
						        
						        File file = new File(path);				        
								output = new BufferedWriter(new FileWriter(file));									        
						        String docString = doc.toString();
						        output.write(docString);
								output.close();
								
								tester.put("Path", path);
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
							
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
						String text = "No text for this page";
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
			
			crawlUrl(word, collection, list , newDep, tracker, p, x);
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
	public static void checkWord(DBCollection word, String texts)
	{
		String[] array = texts.split(" ");
		
		String[] check = {"a", "able", "about", "above", "abst", "accordance", "accordingly", "across", "act", 
							"actually", "added", "adj", "affected", "affecting", "affects", "after", "afterwards", "again", "against", 
							"ah", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst",
							"an", "and", "announce", "another", "any", "anybody", "anyhow", "anymore", "anyone", "anything", "anyway", "anyways",
							"anywhere", "apparently", "approximately", "aren", "arent", "aren't", "arise", "around", "as", "aside", "ask", 
							"asking", "at", "auth", "available", "away", "awfully", "back", "be", "became", "because", "become", "becomes", 
							"becoming", "been", "before", "beforehand", "begin", "beginning", "beginnings", "begins", "behind", "being", "believe",
							"below", "beside", "besides", "between", "beyond", "biol", "both", "brief", "briefly", "but", "by", "came", "can",
							"cannot", "can't", "cause", "causes", "certain", "certainly", "come", "comes", "contain", "containing", "contains", 
							"could", "couldn't", "date", "did", "didn't", "different", "do", "does", "doesn't", "doing", "done", "don't", "down",
							"downwards", "due", "during", "each", "ed", "edu", "effect", "eg", "eight", "eighty", "either", "else", "elsewhere", 
							"end", "ending", "enough", "especially", "et", "et-al", "etc", "even", "ever", "every", "everybody", "everyone", 
							"everything", "everywhere", "ex", "except", "far", "few", "fifth", "first", "five", "fix", "followed", "following",
							"follows", "for", "former", "formerly", "forth", "found", "four", "from", "further", "furthermore", "gave", "get", 
							"gets", "getting", "give", "given", "gives", "giving", "go", "goes", "gone", "got", "gotten", "had", "happens", 
							"hardly", "has", "hasn't", "have", "haven't", "having", "he", "hed", "hence", "her", "here", "hereafter", "hereby", 
							"herein", "heres", "hereupon", "hers", "herself", "hes", "hi", "hid", "him", "himself", "his", "hither", "home", "how", 
							"howbeit", "however", "hundred", "i", "id", "ie", "if", "i'll", "im", "immediate", "immediately", "importance", 
							"important", "in", "inc", "indeed", "index", "information", "instead", "into", "invention", "inward", "is", "isn't", 
							"it", "it'd", "it'll", "its", "itself", "i've", "just", "keep", "keeps", "kept", "kg", "km", "know", "known", 
							"largely", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked",
							"likely", "line", "little", "'ll", "look", "looking", "looks", "ltd", "made", "mainly", "make", "makes", "many", 
							"may", "maybe", "me", "mean", "means", "meantime", "meanwhile", "merely", "mg", "might", "million", "miss", "ml", 
							"more", "moreover", "most", "mostly", "mr", "mrs", "much", "mug", "must", "my", "myself", "name", "namely", "nay", 
							"near", "nearly", "necessarily", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", 
							"nine", "ninety", "no", "nobody", "non", "none", "nonetheless", "noone", "nor", "normally", "nos", "not", "noted", 
							"nothing", "now", "nowhere", "obtain", "obtained", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", 
							"omitted", "on", "once", "one", "ones", "only", "onto", "or", "ord", "other", "others", "otherwise", "ought", "our",
							"ourselves", "out", "outside", "over", "overall", "owing", "own", "page", "pages", "part", "particular", "particularly",
							"past", "per", "perhaps", "placed", "please", "plus", "poorly", "possible", "possibly", "potentially", "pp", 
							"predominantly", "present", "previously", "primarily", "probably", "promptly", "proud", "provides", "put", "que", 
							"quickly", "quite", "ran", "rather", "re", "readily", "really", "recent", "recently", "ref", "refs", "regarding", 
							"regardless", "regards", "related", "relatively", "research", "respectively", "resulted", "resulting", "results", 
							"right", "run", "said", "same", "saw", "say", "saying", "says", "sec", "section", "see", "seeing", "seem", "seemed", 
							"seeming", "seems", "seen", "self", "selves", "sent", "seven", "several", "shall", "she", "shed", "she'll", "shes", 
							"should", "shouldn't", "show", "showed", "shown", "showns", "shows", "significant", "significantly", "similar", 
							"simiarly", "since", "six", "slightly", "so", "some", "somebody", "somehow", "someone", "somethan", "something", 
							"sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specifically", "specified", "specify", "specifying",
							"still", "stop", "strongly", "sub", "substantially", "successfully", "such", "sufficiently", "suggest", "sup", "sure",
							"take", "taken", "taken", "taking", "tell", "tends", "than", "thank", "thanks", "thanx", "that", "that'll", "thats", 
							"that've", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "thered", 
							"therefore", "therein", "there'll", "thereof", "thereto", "thereupon", "there've", "these", "they", "they'll", "they're",
							"they've", "think", "this", "those", "thou", "though", "thousand", "through", "throughout", "thru", "thus", "til", "tip",
							"to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", 
							"under", "unfortunately", "unless", "unlike", "unlikely", "until", "unto", "up", "upon", "ups", "us", "use", "used", 
							"useful", "usefully", "usefulness", "uses", "using", "usually", "value", "various", "'ve", "very", "via", "viz", "vol", 
							"vols", "want", "wants", "was", "wasn't", "way", "we", "we'd", "welcome", "we'll", "went", "were", "weren't", "we've", 
							"what", "whatever", "what'll", "what's", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", 
							"wherein", "wheres", "whereupon", "wherever", "whether", "which", "while", "whim", "whither", "who", "who'd", "whoever",
							"whole", "who'll", "whom", "whomever", "whos", "whose", "why", "widely", "willing", "wish", "with", "within", "without",
							"won't", "words", "world", "would", "wouldn't", "www", "y", "yes", "yet", "you", "you'd", "you'll", "your", "you're", 
							"yours", "yourself", "yourselves", "you've", "zero"
						};
		
		int count = 0;
		
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < check.length; j++)
			{
				if( !(array[i].equals(check[j])) )
				{
					count++;
				}
			}
			
			if( count == check.length )
			{
				addWord(word, array[i]);			
			}
			
			count = 0;
		}
		
		
	}
//-----------------------------------------------------------------------------------------------------------------
	public static void addWord(DBCollection word, String uniqueWord)
	{
		JSONObject uWord = new JSONObject();
		DBCursor cursor = word.getCollection("UniqueWords").find();
		DBObject dbObject;
		
		if(word.count() == 0)
		{
			try 
			{
				uWord.put("Word", uniqueWord);
				dbObject = (DBObject) JSON.parse(uWord.toString());
				word.insert(dbObject);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			BasicDBObject wjo;
			int count = 0;
			
			while(cursor.hasNext())
			{
				wjo = (BasicDBObject) cursor.next();
				
				if( !(uniqueWord.equals(wjo.getString("Word"))) )
				{
					count++;
				}
				
			}
			
			if( count == word.count())
			{
				try 
				{
					uWord.put("Word", uniqueWord);
					dbObject = (DBObject) JSON.parse(uWord.toString());
					word.insert(dbObject);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			
		}
	}
//-----------------------------------------------------------------------------------------------------------------
}