import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class BPlusTree {
	private BufferedReader reader;
	private long startTime;
	private long endTime;
	private int pageNo;

	public BPlusTree() {

	}

	public void run(int pageSize, String file) throws FileNotFoundException {
		// Open the heap file for reading
		int numBytesInOneRecord = constants.TOTAL_SIZE;
		int numRecordsPerPage = pageSize / numBytesInOneRecord;
		byte[] page = new byte[pageSize];
		FileInputStream inStream = new FileInputStream(file);
		int numBytesRead = 0;
		byte[] personNameBytes = new byte[constants.PERSON_NAME_SIZE];
		byte[] birthDateBytes = new byte[constants.BIRTH_DATE_SIZE];
		// Set the start time
		startTime = System.currentTimeMillis();
		pageNo = 0;
		String line = "";
		try {
			// Iterate through the lines in the file
			while ((numBytesRead = inStream.read(page)) != -1) {
				// Process each record in page
				for (int i = 0; i < numRecordsPerPage; i++) {

					// Copy record's person name and birth date
					System.arraycopy(page, ((i * numBytesInOneRecord) + constants.PERSON_NAME_OFFSET), personNameBytes,
							0, constants.PERSON_NAME_SIZE);
					System.arraycopy(page, ((i * numBytesInOneRecord) + constants.BIRTH_DATE_OFFSET), birthDateBytes, 0,
							constants.BIRTH_DATE_SIZE);

					// Check if person name field is empty; if so, end of all records found (packed
					// organisation)
					if (personNameBytes[0] == 0) {
						// can stop checking records
						break;
					}

					// Check for match
					long birthDateLong = ByteBuffer.wrap(birthDateBytes).getLong();
					if (0 == birthDateLong) {
						// skip NULL birth dates
						continue;
					}
					Date birthDate = new Date(ByteBuffer.wrap(birthDateBytes).getLong());
					System.out.println(birthDate);
					++pageNo;
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Set the end time
		endTime = System.currentTimeMillis();
		// Print info to the console
<<<<<<< HEAD
		System.out.println("Records Indexed: ");
		System.out.println("Page Count: " + pageNo);
		System.out.println("Tree Height: ");
=======
		
		System.out.println("Records Loaded: " );
		System.out.println("Page Count: " );
>>>>>>> 2f7abee8f5742f4932c108b60f7fdef028489054
		System.out.println("Time Taken (milliseconds): " + (endTime - startTime));
	}
}
