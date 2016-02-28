package edu.csula.cs454.example;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class wordData 
{
	@Id
	ObjectId Id;
	String word;
	
	public wordData() 
	{
		
	}
//-----------------------------------------------------------------------------------------------------------
	public wordData(String word)
	{
		
	}
//-----------------------------------------------------------------------------------------------------------	
	public String getWord()
	{		
		return word;
	}
//-----------------------------------------------------------------------------------------------------------	
	public void setWord(String word)
	{		
		this.word = word;
	}
//-----------------------------------------------------------------------------------------------------------
}
