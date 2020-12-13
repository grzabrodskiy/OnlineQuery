/*************************************************************************
 *  Compilation:  javac Page.java
 *
 *  This models a "web page".  It is designed as part of the OnlineQuery
 *  program.
 *
 *************************************************************************/


import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.text.DecimalFormat;
 

public class Page implements Comparable {
    private String theURL;          // the URL of the page
    private Double theSimilarity;   // the similarity wrt the given query
    private boolean theResponse;    // whether or not the page can be viewed
    private String theContent;      // the HTML code of the entire page
    private ArrayList<String> theLinks; // the list of all outlinks

  
     public Page() {   // when we only want the content
		theURL = "";
		theSimilarity = null;
		theResponse = true;
		theContent = "";
		theLinks = null;
	}	

    public Page(String url) {
		theURL = url;
		theSimilarity = null;       // no query provided yet
		theResponse = true;   
		theContent = "";
		theLinks = null;
		In in = null;
		try{
			 //System.out.println(theURL);
			 in = new In(theURL);  // try to open the page
		}
		catch(Exception e){
			;
		}
		if (in==null || !in.exists()) {      // if no response, skip the rest
		    theResponse = false;
		    return;
		}
		
		theContent = in.readAll(); // get the content as one big string
	
		debug("Page: " + this.toString());
		
		if (theContent == null) {  // an empty page
		    theResponse = false;
		    return;
		}

		findLinks();            // find all outlinks
	
	
    }

    public boolean response()      { return theResponse;      }
    public String  getURL()        { return theURL;           }
    public String  getContent()    { return theContent;       }
    public double  getSimilarity() { return theSimilarity;    }
    public ArrayList<String> getLinks() { return theLinks;    }

    public void    setContent(String content) {
    	theContent = content;
    	findLinks();    // find all outlinks
    }

    public String toString() { 
		// print the similarity, the url, and a snippet of the content 
    	final String TITLE = "<title>"; 
    	final String TITLE_END = "</title>"; 
    	final String DESCRIPTION = "<meta name=\"description\"";
    	final String DESCRIPTION_END = ">";
    	final String CONTENT = "content=\"";
    	final String CONTENT_END = "\"";
    	
		String title = "";
		String desc = "";
		String content = "";
	
		if (theContent != null) {
			
			int from = theContent.toLowerCase().indexOf(TITLE);
			int to = 0;
			
			if (from > -1){
				
				to = theContent.toLowerCase().indexOf(TITLE_END, from+1);
				from += TITLE.length();
				if (to > -1)
					title = theContent.substring(from, to);
			}
			
			from = theContent.toLowerCase().indexOf(DESCRIPTION);
			if (from > -1){
				to = theContent.toLowerCase().indexOf(DESCRIPTION_END, from + 1);
				from += DESCRIPTION.length();
				//debug("t = " + from + "f = " + to);
						
				if (to > -1)
					desc = (theContent.substring(from, to));
			}
			if (desc.length() > 0){
				from = desc.toLowerCase().indexOf(CONTENT);
				if (from > -1){
					from += CONTENT.length();
					to = desc.toLowerCase().indexOf(CONTENT_END, from );
					//debug("f: " + from + " t: " + to + "  !" + desc);
					if (to > -1)
						desc = desc.substring(from, to);
				}
			}
			
			if (title.length() > 0){
				to = title.indexOf('\n');
				
				if(to > -1)
					title = title.substring(0, to);
				content = title;
			}
			if (desc.length() > 0){
				
				to = desc.indexOf('\n');
				
				if(to > -1)
					desc = desc.substring(0, to);
				
				
				content += ("\n>>>" + desc);
			}
			
			
			
			if (content.length() == 0){
				
				content = theURL;
			}
			
		    // ***
		    // What content do you want to display?
		    // the Title? A content snippet? ...
			
			
		}
		DecimalFormat df = new java.text.DecimalFormat("0.00");
		String sim = theSimilarity==null?"(n/a)":df.format(theSimilarity * 100.);
		
		return "- (" + sim + ") " + theURL + "\n" + content;
    }

    public void findSimilarity(Page other) {
	// ***
	// replace this method with a more meaningful one
    	theSimilarity = getCosineSimilarity(other);
    }

    public void findLinks() {
		// ***
		// implement this to find all http links in theContent
		// use 
		//    int indexOf(String substr, int fromIndex): 
		// Returns the index within this string of the first
		// occurrence of the specified substring, starting at the
		// specified index. If it does not occur, -1 is returned.
		// 
		// Look for the substring "http://
    	final String URL_TOKEN = "http://";
    	
    	
		
    	theLinks = new ArrayList<String> ();
		
		int from = 0;
		int to = 0;
		int to2 = 0;
		String link;
		do {
			
			from = theContent.indexOf(URL_TOKEN, from);
			
			//debug(theContent);
			
			//debug("Link from " + from);
			
			if (from > -1){
				to = theContent.indexOf( "\"", from);
				if (to == -1) to = Integer.MAX_VALUE;
				to2  =theContent.indexOf("?", from);
				if (to2 == -1) to2 = Integer.MAX_VALUE;
				to = Math.min(to, to2);
				to2  =theContent.indexOf("'", from);	
				if (to2 == -1) to2 = Integer.MAX_VALUE;
				to = Math.min(to, to2);
			
				//debug("Link to " + to);
							
				
				if (to != Integer.MAX_VALUE){
					link = theContent.substring(from, to);
					//debug("Link " + from +" found: " + link);
					
					if(isValidLink(link)){
						
						if (link.endsWith("/"))
							link = link.substring(0, link.length()-1);
						
						if (link.endsWith("/index.html"))
							link = link.substring(0, link.length()-"/index.html".length());
						
						if (link.endsWith("/home/index.html"))
							link = link.substring(0, link.length()-"/home/index.html".length());
						
						if (link.endsWith("/home"))
							link = link.substring(0, link.length()-"/home".length());
					
						
						if (!theLinks.contains(link) && !OnlineQuery.existsHistory(link)){						
						    debug("Link added: " + link);
							theLinks.add(link);
						}
						else{
							//debug("Link already there: " + link );
							;
						}
					}
				}
				else{
					//debug("Error: link does not have ending: " + theContent.substring(from, from + 20));
				}
				from = to;
			}
		} while (from > -1);
			
			
	}
		
		
		
    public int compareTo(Object other) {
	// ***
	// How do you want to rank?
	// Maybe some combination of similarity and links?
	// Remember: result should be -1, 0, or 1
    	Page otherPage = (Page) other;
    
    	//System.out.println("cmp");
    	
    	return - new Double(this.getSimilarity()).compareTo(new Double(otherPage.getSimilarity()));
    	
  
    }
    
    private boolean isValidLink(String link){
    	
    	final String INVALID_EXT[] = {".gif", ".png", ".css", ".jpg", ".js", ".jpeg", ".php", ".xml"};
    	
    	
    	
    	if (OnlineQuery.existsHistory(link))
    		return false;
    	
    	for (String exte: INVALID_EXT){
    		if (link.length()> exte.length()){
    			if (link.substring(link.length()-exte.length()).equalsIgnoreCase(exte))
    				return false;
    		}
    	}
    	return true;
    	
    	
    	
    }
    
    private static void debug(String s){
    	if (OnlineQuery.DEBUG)
    		System.out.println(s);
    }
    
    
   
    
    public double getCosineSimilarity(Page other){

    	debug("C***********************************************" + this.theURL);
       	debug(this.theContent);
       	debug("***********************************************");
           	
    	
    	String thisTokens[] =  this.theContent.toString().replaceAll("[\\W&&[^\\s]]", " ").split("\\W+"); 
       	String otherTokens[] =  other.theContent.toString().replaceAll("[\\W&&[^\\s]]", " ").split("\\W+"); 
         
       	debug("*this");
       	for (String s: thisTokens)
       		debug(s);
       	debug("*other");
    	for (String s: otherTokens)
       		debug(s);
    	debug("***********************************************");
    	        
       	
       	
    	double sim =  cosineTextSimilarity(thisTokens, otherTokens);
    	//System.out.println("sim = " + sim);
    	return sim;
    }
    
        
    public static double cosineTextSimilarity(String[] left, String[] right) {
        Map<String, Integer> leftWordCountMap = new HashMap<String, Integer>();
        Map<String, Integer> rightWordCountMap = new HashMap<String, Integer>();
        Set<String> uniqueSet = new HashSet<String>();
        Integer cnt = null;
        for (String leftWord : left) {
        	leftWord  =leftWord.toLowerCase();
        	//debug("before left: " + leftWord); 
        	if (isValidWord(leftWord)){
        		debug("left: " + leftWord); 
        		cnt = leftWordCountMap.get(leftWord);
        		if (cnt == null) {
        			leftWordCountMap.put(leftWord, 1);
        			uniqueSet.add(leftWord);
        		} else {
        			leftWordCountMap.put(leftWord, cnt + 1);
        		}
        	}
        }
        for (String rightWord : right) {
        	rightWord = rightWord.toLowerCase();
        	debug("right: " + rightWord);
            cnt = rightWordCountMap.get(rightWord);
            if (cnt == null) {
                rightWordCountMap.put(rightWord, 1);
                uniqueSet.add(rightWord);
            } else {
                rightWordCountMap.put(rightWord, cnt + 1);
            }
        }
        int[] leftVector = new int[uniqueSet.size()];
        int[] rightVector = new int[uniqueSet.size()];
        int idx = 0;
        
        cnt = 0;
        
        for (String uniqueWord : uniqueSet) {
            cnt = leftWordCountMap.get(uniqueWord);
            leftVector[idx] = cnt == null ? 0 : cnt;
            cnt = rightWordCountMap.get(uniqueWord);
            rightVector[idx] = cnt == null ? 0 : cnt;
            idx++;
        }
        return cosineVectorSimilarity(leftVector, rightVector);
    }

    
    private static double cosineVectorSimilarity(int[] left, int[] right) {
        if (left.length != right.length)
            return 1;
        
        double dotProduct = 0;
        double leftN = 0;
        double rightN = 0;
        for (int i = 0; i < left.length; i++) {
            dotProduct += left[i] * right[i];
            leftN += left[i] * left[i];
            rightN += right[i] * right[i];
        }
        
        leftN = Math.sqrt(leftN);
        rightN = Math.sqrt(rightN);
        
        if (leftN*rightN == 0)
        	return 0;
        
        return dotProduct/leftN/rightN;
    
    }
    
    private static boolean isValidWord(String word){

    	if (word.length() > 30)
    		return false;
    	if (word.length() < 3)
    		return false;
    	
    	if (word.matches(".*\\d+.*"))
    		return false;
    	
    	
    	return true;
    }
    
    
}
