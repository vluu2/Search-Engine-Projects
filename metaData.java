package edu.csula.cs454.example;

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
}
