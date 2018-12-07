package textParse;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileWriter;


import java.io.FileInputStream;

import java.io.IOException;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;


import org.apache.tika.parser.ParseContext;
import org.xml.sax.SAXException;



public class getText {

	
	public static ArrayList<String> parseD(File myFile) throws FileNotFoundException, IOException, SAXException, TikaException
	{
	      
	      FileInputStream inputstream = new FileInputStream(myFile);

	      BodyContentHandler bodyH = new BodyContentHandler(-1);

	      ParseContext parsedata = new ParseContext();

	      HtmlParser htmlp = new HtmlParser();

	      Metadata mdata = new Metadata();

	      String temporaryString ;
	      htmlp.parse(inputstream, bodyH, mdata,parsedata);
	      temporaryString = bodyH.toString();
	      String dataStr;
	      dataStr = bodyH.toString();

	      ArrayList bigtextArrayList = new ArrayList(Arrays.asList(dataStr.split("[^a-zA-Z0-9]")));

	      return bigtextArrayList;
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException, SAXException, TikaException 
		{
			ArrayList<String> listdata = new ArrayList();
			String path= "C:\\Users\\Ikshita\\Downloads\\nypost";
			int i =0;
	        File dir = new File(path);
	        

	        File[] filesdata = dir.listFiles();
	        for(File x: filesdata)
	        	{listdata.addAll(parseD(x));}
	        
	        FileWriter fw = new FileWriter("C:\\Users\\Ikshita\\Desktop\\big\\bigdemo.txt");
	        BufferedWriter bw = new BufferedWriter(fw);
			for(String x: listdata) 
				{bw.write(x+"\n");}
			}
		
}