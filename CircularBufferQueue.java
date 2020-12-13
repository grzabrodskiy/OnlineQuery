import java.util.Iterator;

public class CircularBufferQueue<Item> implements Queue<Item> {

	private final int DEFAULT_CAPACITY = 100;
	
	private int capacity;
	private int front =0;
	private int cnt =0; //rear = 0; 
	private Item[] arr;
	private boolean autoresize = true; 
	
	CircularBufferQueue(){
		this.capacity = DEFAULT_CAPACITY;
		arr = (Item[]) new Object[this.capacity];
	}
	
	CircularBufferQueue(boolean autoresize){
		this.capacity = DEFAULT_CAPACITY;
		this.autoresize = autoresize;
		arr = (Item[]) new Object[this.capacity];
	}

	
	CircularBufferQueue(int capacity){
		this.capacity = capacity;
		arr = (Item[]) new Object[this.capacity];
	}
	
	CircularBufferQueue(int capacity, boolean autoresize){
		this.capacity = capacity;
		this.autoresize = autoresize;
		arr = (Item[]) new Object[this.capacity];
	}

	
	
	public void autoresize(boolean flag){
		autoresize = flag;
	}
	
	
	public void resize(int size){
		if (size <= capacity)
			return;
		
		Item[] temp = (Item[])new Object[this.capacity];
		
		for (int i = 0; i < capacity; i++){
			temp[i] = arr[(front+i)%capacity];
		}
		
		
		arr = (Item[]) new Object[size];
		
		for (int i = 0; i < capacity; i++){
			arr[i] = temp[i];
		}
		front = 0;
		capacity = size;
		
	}
	
	


	@Override
	public int size() {
		return cnt;
	}

	@Override
	public boolean isEmpty() {
		return cnt == 0;
	}

	@Override
	public boolean isFull() {
		return cnt==capacity;
	}

	@Override
	public void enqueue(Item item) {
		if (cnt < capacity){
			cnt++;
		}
		else{
			
			if (autoresize){
				resize(capacity*2);
				cnt++;
			}
			else {
				front++;
				front %= capacity;
			}
		}
		arr[(front + cnt-1) %capacity] = item;
	}

	@Override
	public Item dequeue() {
		if (isEmpty())
			return null;
		Item item = arr[front];
		cnt--;
		arr[front] = null;
		front++;
		front %= capacity;
		
		return item;
	}
	
	@Override
	public String toString(){
		String s = "Queue size (capacity) " + this.size() + "(" + capacity + ")\n" ; 
		System.out.println("f:" + front + " c:" + cnt);
		

		
		for (int i = front; i < front + cnt; i++){

			s += ("{" + arr[(i%capacity)].toString()+ "}");
		}
		return s;
		
	}
	
	 public static final void main (String[] args){
	    	
		 Queue<String> q = new CircularBufferQueue<String>(5, true);
		 //System.out.println(q);
		 q.enqueue("A");
		 q.enqueue("B");
		 q.enqueue("a");
		 q.enqueue("b");
		 System.out.println(q);
		 
		 System.out.println(q.dequeue());
		 
		 
		 q.enqueue("c");
		 q.enqueue("d");
		 q.enqueue("e");
		 
		 System.out.println(q);
		 
		 q.enqueue("f");
		
		 System.out.println(q);
	 
		 System.out.println(q.dequeue());
		 
		 System.out.println(q);
		 
		 q.enqueue("g");
		 System.out.println(q);
		 q.enqueue("h");
		 System.out.println(q);
		 
		 System.out.println(q.dequeue());
		 System.out.println(q.dequeue());
		 System.out.println(q);
		 for(String s: q)
			 System.out.println(s + "}");
		 
	 }
	
	@Override
	public Iterator<Item> iterator() { return new QueueIterator();}

    // an iterator, doesn't implement remove() since it's optional
    private class QueueIterator implements Iterator<Item> {
        private int idx = 0;
        public boolean hasNext()  { return idx < cnt;}
        public void remove()      { return;  }

        public Item next() {
        	
        	if (!hasNext())
        		return null;
        	
            Item item = arr[((idx++) + front) % capacity];
      
            return item;
        }
    }
	
   

}
