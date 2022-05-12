import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BPlusTree {
	private BufferedReader reader;
	private long startTime;
	private long endTime;
	public BPlusTree()
	{
		
	}
	
	public void run(int pageSize, String file) throws FileNotFoundException {
		// Open the heap file for reading
		reader = new BufferedReader(new FileReader(file));
		// Set the start time
		startTime = System.currentTimeMillis();
		String line = "";
		try {
			// Iterate through the lines in the file
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Set the end time
		endTime = System.currentTimeMillis();
		// Print info to the console
		
		System.out.println("Records Loaded: " );
		System.out.println("Page Count: " );
		System.out.println("Time Taken (milliseconds): " + (endTime - startTime));
	}
}
