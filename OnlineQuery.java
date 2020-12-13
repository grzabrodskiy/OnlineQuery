/*************************************************************************
 *  Compilation:  javac OnlineQuery.java 
 *  Execution:    java OnlineQuery -uURL -qqueryString [-mM]
 *  Dependencies: stdlib.jar Queue.java Page.java MergeSort.java
 *  
 *  Downloads web pages, starting from the URL, and subsequently through 
 *  hyperlinks found in the pages.  Computes the similarity of each page 
 *  with the queryString and prints a list of ranked result.
 *
 *************************************************************************/
import java.util.ArrayList;
import java.util.List;

public class OnlineQuery { 
	
	
    final static int MAX_LINKS = 100;
    
	final static  boolean DEBUG = false;
    private static List<String> theHistory = null; // URL history
    
    
    public static void clearHistory(){
    	
    	theHistory = new ArrayList<String>();
    }
    
    public static boolean existsHistory(String url){
    	
    	if (theHistory == null)
    		return false;
    	return theHistory.contains(url);
    	
    }
    
    
    public static void insertHistory(String url){
    	
    	if (theHistory == null)
    		clearHistory();
    	
    	if (!existsHistory(url))
    		theHistory.add(url);
    	
    }

	
	

    public static void main(String[] args) { 

	

	if (args.length < 3) {
	    System.out.println("Usage: java OnlineQuery -uURL -qqueryString [-mM] (Maximum)");
	    System.exit(0);
	}

        // timeout connection after 1000 miliseconds
        System.setProperty("sun.net.client.defaultConnectTimeout", "1000");
        System.setProperty("sun.net.client.defaultReadTimeout",    "1000");

        // parsing the commandline arguments                                                                                                                  
        int maxPages = 25;         // maximum number of pages to collect
        Page query = new Page();   // the query page
        Page start = null;         // the start page
        int cnt = 0;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].substring(0,2).equals("-u"))
                start = new Page(args[i].substring(2));
            else if (args[i].substring(0,2).equals("-q"))
                query.setContent(args[i].substring(2));
            else if (args[i].substring(0,2).equals("-m"))
                maxPages = Integer.parseInt(args[i].substring(2));
            else {
                System.out.println("invalid flag:" + args[i].substring(0,2));
                System.exit(0);
            }
        }

        if (start == null || query.getContent().equals("")) 
	    // no seed page or query provided
        	System.exit(0);

        int pageCount = 0; // the number of pages that we visit
        int pageOff = 0; // the number of pages that we visit

        // resulting pages
        Page[] results = new Page[maxPages]; 

        // a data structure for the list of pages to be examined;
        // could be either FIFO, or LIFO

        Queue<Page> frontier = new CircularBufferQueue<Page>();

        // initialize it to the start 
        frontier.enqueue(start);

        //System.out.println(start.getURL());
        
        
        
        // do a crawl of web
        System.out.println("....... Searching (this may take several minutes...)");
        System.out.println("....... Search keyword(s): "+ query.getContent());
		
        clearHistory();
        int cntlinks = 0;
		while (! frontier.isEmpty()) {
			
			//System.out.println("F has " + frontier.size() + " pages");

            Page v = frontier.dequeue();  // the next page in line

            //System.out.println(v.getURL());
            
            
            if (existsHistory(v.getURL()))
            	continue;
            
            insertHistory(v.getURL());
            
            if (! v.response()) 
            	continue; // no luck with this page 

            
            
            v.findSimilarity(query);      // compute similarity 

            //System.out.println(v.getSimilarity() + ":" + v.getURL());
            
            
            if (v.getSimilarity() <= 0 && frontier.size() != 0) 
            	continue;

            if (v.getSimilarity() == 0)
            	pageOff++;
            
            results[pageCount++] = v;     
            System.out.print("*");
            cnt++;

            if (pageCount >= maxPages) // limit reached
            	break;

            // find all outgoing links
            ArrayList<String> outLinks = v.getLinks();

            //int cntlinks = 0;
            for (String url : outLinks){
            	
            	if(cntlinks++ < MAX_LINKS){
            		if (!existsHistory(url)){
            			//System.out.println(url);
            		
            			frontier.enqueue(new Page(url));
            			if (cnt++%50 == 49)
            				System.out.println(".");
            			else
            				System.out.print(".");
            		}
            	}
            	
            }
            //System.out.print("+");
            
	    // *** loop to add all previously unseen links to the frontier;
	    // *** maintain a history list of URLs in a link list

        }

	// sort the result and print

	MergeSort.sort(0,pageCount,results);

	System.out.println("\n....... Search Results:");
	//System.out.println("# of results: " + (pageCount - pageOff));

	for (int i = 0; i < pageCount; i++) {
		if(results[i].getSimilarity()> 0)
			System.out.println((i+1) + ". " +results[i]);
	}
   }
}