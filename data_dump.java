package edu.csula.cs454.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/*
 * Used http://stackoverflow.com/questions/7417401/writing-jsonobject-into-a-file for reference
 */
public class data_dump 
{

	public static void main(String[] args) 
	{
		
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("cs454-db");
		DBCollection collection = db.getCollection("dummyTable");
		
		String path = "C:/Users/Allen/Desktop/cs454 assignments/db1.json";
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
        
    	System.out.println(collection.toString());
        
       // String jsonString = tester.toString();
        output.write(jsonString);
        ((BufferedWriter) output).newLine();	
        
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
