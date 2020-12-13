import java.util.Iterator;


public interface Queue <Item > extends Iterable<Item>{
    
    // return the size of the queue
    public int size();
     
    public boolean isEmpty();
     
    public boolean isFull();
     
    // insert an element into the queue 
    public void enqueue(Item i) ;
     
    // removes an element from the queue 
    public Item dequeue() ; 
    
    public Iterator<Item> iterator();
 
}
