
public class btindex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args[0].equals("-p") && args.length ==3)
		{
			BPlusTree tree = new BPlusTree();
			tree.run();
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
