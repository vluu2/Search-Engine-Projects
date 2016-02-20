package edu.csula.cs454.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

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
        

    	DBCursor cursor = db.getCollection("dummyTable").find();
    	BasicDBObject djo;
    	
    	ObjectMapper mapper = new ObjectMapper();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	mapper.setDateFormat(df);    	
    	
		while (cursor.hasNext()) 
		{
			try 
			{
//				System.out.println("IN WHILE LOOP");
				djo = (BasicDBObject) cursor.next();
				
//				output.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(djo));
//				
//				((BufferedWriter) output).newLine();				
//				((BufferedWriter) output).newLine();
				
				output.write("{");
				((BufferedWriter) output).newLine();
				output.write("\t\"id\": " + djo.getString("_id"));
				((BufferedWriter) output).newLine();
				output.write("\t\"Path\": " + "\"" + djo.getString("Path") + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Text\": " + "\"" + djo.get("Text") + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Url\": " + "\"" + djo.getString("URL") + "\"");
				((BufferedWriter) output).newLine();
				output.write("\t\"Name\": " + "\"" + djo.getString("Name") + "\"");
				((BufferedWriter) output).newLine();
				output.write("}");
				
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