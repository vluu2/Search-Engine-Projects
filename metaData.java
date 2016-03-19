package edu.csula.cs454.example;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class metaData 
{
	@Id
	ObjectId Id;
	String path;
	String text;
	String url;
	String title;
	String hashTitle;
	ArrayList<HashMap<String, Double>> tfidf = new ArrayList<HashMap<String, Double>>();;
	
	public metaData() 
	{
		
	}
//-----------------------------------------------------------------------------------------------------------
	public metaData(String path, String text, String url, String title, String hashTitle) 
	{
		
	}
//-----------------------------------------------------------------------------------------------------------
	public String getPath()
	{		
		return path;
	}
//-----------------------------------------------------------------------------------------------------------	
	public String getText()
	{		
		return text;
	}
//-----------------------------------------------------------------------------------------------------------	
	public String getUrl()
	{		
		return url;
	}
//-----------------------------------------------------------------------------------------------------------	
	public String getTitle()
	{		
		return title;
	}
//-----------------------------------------------------------------------------------------------------------	
	public String getHash()
	{		
		return hashTitle;
	}
//-----------------------------------------------------------------------------------------------------------	
	public ArrayList<HashMap<String, Double>> getTfidfArrList()
	{		
		return tfidf;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setPath(String path)
	{		
		this.path = path;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setText(String text)
	{		
		this.text = text;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setUrl(String url)
	{		
		this.url = url;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setTitle(String title)
	{		
		this.title = title;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setHash(String hash)
	{		
		this.hashTitle = hash;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setTfidfArrList(ArrayList<HashMap<String, Double>> tfidf)
	{		
		this.tfidf = tfidf;
	}
//-----------------------------------------------------------------------------------------------------------	
}
