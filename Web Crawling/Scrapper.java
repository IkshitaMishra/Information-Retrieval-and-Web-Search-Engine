import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.james.mime4j.dom.Header;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Scrapper extends WebCrawler {
	
	 	int fetches_attemp = 0;
	    	int urlsOutUniqueWithin = 0;
	    	int urlsOutUniqueOutside = 0;
	 	int fetches_Succ = 0;
	 	int fetches_Fail=0;
	 	int fetches_Abort=0;
	 	int urlsOutTotal = 0;
	 	int sc200 = 0;
	 	int sc301 = 0;
	 	int sc401 = 0;
	 	int sc403 = 0;
	 	int sc404 = 0;
	 	int size1k = 0; 
	    	int size10k = 0; 
	    	int size100k = 0; 
		int size1m = 0;
		int sizeg1m = 0; 
	 	int urlsOutUnique;
	 	int html = 0;
	 	int gif=0;
	 	int jpeg=0;
	 	int png=0;
	 	int pdf=0;
	 	int fetches_FailOrAbort = 0;
		String name = "Ikshita Mishra";
		String id = "";


		HashMap<String, Integer> fetchmap = new HashMap<String, Integer>();
		List<String> val = new ArrayList<String>();
		HashMap<String, String> uniqueout = new HashMap<String, String>();
		HashMap<String, String> discovered = new HashMap<String, String>();


		private final static Pattern MATCHES = Pattern.compile(".*(\\.(css|js|xml|json|rss" + "|html|doc|pdf|gif|jpg|jpeg|png|zip|gz))$");
	
		@Override
		public boolean shouldVisit(Page referringPage, WebURL url) {
			String href = url.getURL().toLowerCase();
			 if (href.startsWith("https://nypost.com/"))
		        {
		            if (!(discovered.containsKey(href) && discovered.get(href).equals("OK")))
		            	discovered.put(href, "OK");
		        } else {
		            if (!(discovered.containsKey(href) && discovered.get(href).equals("N_OK")))
		            	discovered.put(href, "N_OK");
			}
			return !MATCHES.matcher(href).matches() && (href.startsWith("https://nypost.com/"));
		}
	
		@Override 
		//Parsing WebPage
	    	public void visit(Page page) {

	        	String url = page.getWebURL().getURL();
	        	String contentype = page.getContentType();
	        	int content_size = page.getContentData().length;

			//Check size of downloaded webpages
	        	if (content_size < 1024)   
				{size1k++;}
			else if (content_size < 10 * 1024)
				{size10k++;}
			else if (content_size < 100 * 1024)
				{size100k++;}
			else if (content_size < 1000 * 1024)
				{size1m++;}
			else
				{sizeg1m++;}
			
			//Check content type of downloaded pages
			if (contentype.contains("text/html"))
			{html++;}
			else if (contentype.contains("image/png"))
			{png++;}
			else if (contentype.contains("image/jpeg"))
			{jpeg++;}
			else if (contentype.contains("application/pdf"))
			{pdf++;}
			else if (contentype.contains("image/gif"))
			{gif++;}
			
			
	        	logger.info("URL: {}", url);
	        
	     		// Check if fetch succeeded
			int statusCode = page.getStatusCode();
			int outgoing_no = 0;
	       		if (page.getParseData() instanceof HtmlParseData) {
	            		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	            		Set<WebURL> links = htmlParseData.getOutgoingUrls();
	            		outgoing_no = links.size();
	            		urlsOutTotal += outgoing_no;
	            		for (WebURL w : links) {
					String outUrl = w.getURL();
					if (outUrl.startsWith("https://nypost.com/")) {
						if (!uniqueout.containsKey(url)) {
							uniqueout.put(url, "OK");
							urlsOutUniqueWithin++;
							}
					}
					else {
						if (!uniqueout.containsKey(url)) {
							uniqueout.put(url, "N_OK");
							urlsOutUniqueOutside++;
						}
					}
				}
	            	}
	        	val.add(url);
	        	val.add(Integer.toString(content_size));
	        	val.add(contentype);
			val.add(Integer.toString(outgoing_no));
			}
		
		
	    	@Override
	   	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription)
	 	{
	    		fetches_attemp++;
	    		String url = String.valueOf(webUrl).replaceAll(",", "_");
	        	logger.info("URL: {} status code: {}: ",url, statusCode);
	        	fetchmap.put(url, statusCode);
	        
	        	if (statusCode >= 200 && statusCode < 300) {
	        		fetches_Succ++;                      //Fetches Succeeded
	        	} else {
	        		fetches_Fail++;			     //Fetched Failed (Status Code: 300-500)
			}
	        					     //fetches failed/aborted = (# fetches failed + # fetches aborted)
		
			//Count Webpages per status code

	        	if (statusCode == 200) {		     
	        		sc200++;
	        	}
	        	if (statusCode == 301) {
        			sc301++;
	        	}
	        	if (statusCode == 401) {
        			sc401++;
	        	}
	        	if (statusCode == 403) {
        			sc403++;
	        	}
	        	if (statusCode == 404) {
        			sc404++;
	        	}

	    	}
	    
	    	public void onBeforeExit() {  
		    	File fetchpath = new File("C:\\Users\\Ikshita\\Desktop\\fetch_Latimes.csv");
	        	FileWriter fetchwriter = null;
			try {
				fetchwriter = new FileWriter(fetchpath);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		     	for(String link: fetchmap.keySet()) {
		        	StringBuilder sb = new StringBuilder();
		            	sb.append(link);
		            	sb.append(',');
		            	sb.append(fetchmap.get(link));
		            	sb.append('\n');
		            	try {
					fetchwriter.write(sb.toString());			
				} catch (IOException e) {
					e.printStackTrace();
				}
		        }
		     
		     	try {
				fetchwriter.flush();
				fetchwriter.close();
			       
			} catch (IOException e) {
				e.printStackTrace();
			}
		     
		     
		     
	         	File stlpath = new File("C:\\Users\\Ikshita\\Desktop\\visits_Latimes.csv");
	         	FileWriter stlwriter = null;
			try {
				stlwriter = new FileWriter(stlpath);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		     	for(int i = 0; i< val.size();i=i+4)
		     	{
		    		StringBuilder sb1 = new StringBuilder();
		    	 	sb1.append(val.get(i));
		         	sb1.append(',');
		         	sb1.append(val.get(i+1));
		         	sb1.append(',');
		         	sb1.append(val.get(i+2));
		         	sb1.append(',');
		         	sb1.append(val.get(i+3));
		         	sb1.append('\n');
		        	try {
		        		stlwriter.write(sb1.toString());			
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	 }
		     	try {
		    		stlwriter.flush();
		    	 	stlwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		     	File oknokpath = new File("C:\\Users\\Ikshita\\Desktop\\urls_Latimes.csv");
		     	FileWriter oknokwriter = null;
			try {
				oknokwriter = new FileWriter(oknokpath);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		  	for(String url: discovered.keySet()) {
		     		StringBuilder sb2 = new StringBuilder();
		     		sb2.append(url);
		     		sb2.append(',');
		     		sb2.append(discovered.get(url));
		     		sb2.append('\n');
		        	try {
		        		oknokwriter.write(sb2.toString());			
				} catch (IOException e) {
					e.printStackTrace();
				}
		     	}
		  	try {
				oknokwriter.flush();
			  	oknokwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  
			urlsOutUnique =urlsOutUniqueWithin + urlsOutUniqueOutside;
		   	fetches_Abort = fetches_Succ - sc200;
		   	fetches_FailOrAbort = fetches_Fail + fetches_Abort;
		  	PrintWriter writer;
			try {
				  writer = new PrintWriter("C:\\Users\\Ikshita\\Desktop\\report.txt", "UTF-8");
				  writer.printf("Name: %s", name);writer.println();
				  writer.printf("USC ID: %s", id);writer.println();
				  writer.printf("New Sites Crawled : https://nypost.com/");writer.println();
				  writer.println();
				  writer.printf("Fetch Statistics: ");writer.println();
				  writer.printf("================");writer.println();
				  writer.printf("# fetches attempted: %d", fetches_attemp);writer.println();
				  writer.printf("# fetches succeeded: %d", fetches_Succ);writer.println();
				  writer.printf("# fetches failed or aborted: %d", fetches_FailOrAbort);writer.println();
				  writer.println();
				  writer.printf("Outgoing URLs: ");writer.println();
				  writer.printf("=============");writer.println();
				  writer.printf("Total URLs extracted: %d", urlsOutTotal);writer.println();
				  writer.printf("# unique URLs extracted: %d", urlsOutUnique);writer.println();
				  writer.printf("# unique URLs within News Site: %d", urlsOutUniqueWithin);writer.println();
				  writer.printf("# unique URLs outside News Site: %d", urlsOutUniqueOutside);writer.println();
				  writer.println();
				  writer.printf("Status Codes: ");writer.println();
				  writer.printf("============");writer.println();
				  writer.printf("200 OK: %d" , sc200);writer.println();
				  writer.printf("301 Moved Permanently: %d" , sc301);writer.println();
				  writer.printf("401 Unauthorized: %d" , sc401);writer.println();
				  writer.printf("403 Forbidden: %d" , sc403);writer.println();
				  writer.printf("404 Not Found: %d" , sc404);writer.println();
				  writer.println();
				  writer.printf("File Sizes: ");writer.println();
				  writer.printf("============");writer.println();
				  writer.printf("< 1 KB: %d", size1k);writer.println();
				  writer.printf("1 KB ~ <10 KB: %d", size10k);writer.println();
				  writer.printf("10 KB ~ <100 KB: %d", size100k);writer.println();
				  writer.printf("100 KB ~ < 1MB: %d", size1m);writer.println();
				  writer.printf(">= 1MB: %d", sizeg1m);writer.println();
				  writer.println();
				  writer.printf("Content Types: ");writer.println();
				  writer.printf("============");writer.println();
				  writer.printf("text/html: %d", html);writer.println();
				  writer.printf("image/gif: %d", gif);writer.println();
				  writer.printf("image/jpeg:%d", jpeg);writer.println();
				  writer.printf("image/png: %d", png);writer.println();
				  writer.printf("application/pdf: %d", pdf);writer.println();
				  writer.printf("\n");
				  writer.flush();
				  writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		         
		 }
	    
	   
	    
	}


