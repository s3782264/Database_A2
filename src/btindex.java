import java.io.FileNotFoundException;
import java.text.ParseException;

public class btindex {

	public static void main(String[] args) throws NumberFormatException, FileNotFoundException, ParseException {
		// TODO Auto-generated method stub
		if(args[0].equals("-p") && args.length ==3)
		{
			BPlusTree tree = new BPlusTree();
			tree.run(Integer.parseInt(args[1]), args[2]);
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
