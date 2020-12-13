import java.util.Stack;

public class Test {
    // a small test program to illustrate the use of In and Page
    public static void main(String args[]) {
	Stack<Page> a = new Stack<Page>();

	for (int i = 0; i < args.length; i++) 
	    a.push(new Page(args[i]));

	while (!a.empty()) {
	    Page p = a.pop();
	    if (p.response()) 
		System.out.println(p.getContent());
	}

    }

}