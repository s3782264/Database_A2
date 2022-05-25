import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.*;

public class BPlusTree {
	private BufferedReader reader;
	private long startTime;
	private long endTime;
	private int pageNo, recordNo, height;
	private LeafNode firstLeaf;
	private int m;
	private InternalNode root;
	private int hieght;
	private int index;
	long temp;

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
		byte[] birthPlaceBytes = new byte[constants.BIRTH_PLACE_SIZE];
		byte[] deathDateBytes = new byte[constants.DEATH_DATE_SIZE];
		byte[] fieldBytes = new byte[constants.FIELD_SIZE];
		byte[] genreBytes = new byte[constants.GENRE_SIZE];
		byte[] instrumentBytes = new byte[constants.INSTRUMENT_SIZE];
		byte[] nationalityBytes = new byte[constants.NATIONALITY_SIZE];
		byte[] thumbnailBytes = new byte[constants.THUMBNAIL_SIZE];
		byte[] wikipageIdBytes = new byte[constants.WIKIPAGE_ID_SIZE];
		byte[] descriptionBytes = new byte[constants.DESCRIPTION_SIZE];
		// Set the start time
		startTime = System.currentTimeMillis();
		pageNo = 0;
		index = 0;
		this.m = 3;
		this.root = null;
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
					Date birthDate = new Date(birthDateLong);
		
					String name = new String(personNameBytes).trim();
					
					int indexKey = (int) birthDateLong;
					Date date1 = new Date(indexKey);
					insert(birthDateLong, name);
					
					temp = birthDateLong;
					++index;
					++recordNo;
				}
				
				++pageNo;
			}
			System.out.println(search(temp));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Set the end time
		endTime = System.currentTimeMillis();
		// Print info to the console
		System.out.println("Records Indexed: " + recordNo);
		System.out.println("Page Count: " + pageNo);
		System.out.println("Tree Height: ");
		System.out.println("Time Taken (milliseconds): " + (endTime - startTime));
	}

	private int binarySearch(DictionaryPair[] dps, int numPairs, long birthDateLong) {
		Comparator<DictionaryPair> c = new Comparator<DictionaryPair>() {
			@Override
			public int compare(DictionaryPair o1, DictionaryPair o2) {
				long a = o1.getKey();
				long b = o1.getKey();
				if(a < b)
					return -1;
				else if(a == b)
					return 0;
				else if(a > b)
					return 1;
				return 0;
			}
		};
		return Arrays.binarySearch(dps, 0, numPairs, new DictionaryPair(birthDateLong, null), c);
	}

	public String search(long birthDateLong) {

		if (isEmpty()) {
			return null;
		}

		LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(birthDateLong);

		DictionaryPair[] dps = ln.dictionary;
		int index = binarySearch(dps, ln.numPairs, birthDateLong);

		if (index < 0) {
			return null;
		} else {
			return dps[index].value;
		}
	}

	private boolean isEmpty() {
		return firstLeaf == null;
	}

	public class DictionaryPair implements Comparable<DictionaryPair> {
		long key;
		String value;

		public DictionaryPair(long birthDateLong, String value2) {
			this.key = birthDateLong;
			this.value = value2;
		}

		public int compareTo(DictionaryPair o) {
			if (key == o.key) {
				return 0;
			} else if (key > o.key) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public long getKey()
		{
			return this.key;
		}
	}

	private int linearNullSearch(DictionaryPair[] dps) {
		for (int i = 0; i < dps.length; i++) {
			if (dps[i] == null) {
				return i;
			}
		}
		return -1;
	}

	private int linearNullSearch(Node[] pointers) {
		for (int i = 0; i < pointers.length; i++) {
			if (pointers[i] == null) {
				return i;
			}
		}
		return -1;
	}

	private class InternalNode extends Node {
		int maxDegree;
		int minDegree;
		int degree;
		InternalNode leftSibling;
		InternalNode rightSibling;
		Integer[] keys;
		Node[] childPointers;

		private void appendChildPointer(Node pointer) {
			this.childPointers[degree] = pointer;
			this.degree++;
		}

		private int findIndexOfPointer(Node pointer) {
			for (int i = 0; i < childPointers.length; i++) {
				if (childPointers[i] == pointer) {
					return i;
				}
			}
			return -1;
		}

		private void insertChildPointer(Node pointer, int index) {
			for (int i = degree - 1; i >= index; i--) {
				childPointers[i + 1] = childPointers[i];
			}
			this.childPointers[index] = pointer;
			this.degree++;
		}

		private boolean isOverfull() {
			return this.degree == maxDegree + 1;
		}

		private void removePointer(int index) {
			this.childPointers[index] = null;
			this.degree--;
		}

		private InternalNode(int m, Integer[] keys) {
			this.maxDegree = m;
			this.minDegree = (int) Math.ceil(m / 2.0);
			this.degree = 0;
			this.keys = keys;
			this.childPointers = new Node[this.maxDegree + 1];
		}

		private InternalNode(int m, Integer[] keys, Node[] pointers) {
			this.maxDegree = m;
			this.minDegree = (int) Math.ceil(m / 2.0);
			this.degree = linearNullSearch(pointers);
			this.keys = keys;
			this.childPointers = pointers;
		}
	}

	public class Node {
		InternalNode parent;
	}

	public class LeafNode extends Node {
		int maxNumPairs;
		int minNumPairs;
		int numPairs;
		LeafNode leftSibling;
		LeafNode rightSibling;
		DictionaryPair[] dictionary;

		public void delete(int index) {
			this.dictionary[index] = null;
			numPairs--;
		}

		public boolean insert(DictionaryPair dp) {
			if (this.isFull()) {
				return false;
			} else {
				this.dictionary[numPairs] = dp;
				numPairs++;
				Arrays.sort(this.dictionary, 0, numPairs);

				return true;
			}
		}

		public boolean isDeficient() {
			return numPairs < minNumPairs;
		}

		public boolean isFull() {
			return numPairs == maxNumPairs;
		}

		public boolean isLendable() {
			return numPairs > minNumPairs;
		}

		public boolean isMergeable() {
			return numPairs == minNumPairs;
		}

		public LeafNode(int m, DictionaryPair dp) {
			this.maxNumPairs = m - 1;
			this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
			this.dictionary = new DictionaryPair[m];
			this.numPairs = 0;
			this.insert(dp);
		}

		public LeafNode(int m, DictionaryPair[] dps, InternalNode parent) {
			this.maxNumPairs = m - 1;
			this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
			this.dictionary = dps;
			this.numPairs = linearNullSearch(dps);
			this.parent = parent;
		}
	}

	private LeafNode findLeafNode(InternalNode node, long birthDateLong) {

		Integer[] keys = node.keys;
		int i;

		for (i = 0; i < node.degree - 1; i++) {
			if (birthDateLong < keys[i]) {
				break;
			}
		}
		Node childNode = node.childPointers[i];
		if (childNode instanceof LeafNode) {
			return (LeafNode) childNode;
		} else {
			return findLeafNode((InternalNode) node.childPointers[i], birthDateLong);
		}
	}

	private LeafNode findLeafNode(long birthDateLong) {

		Integer[] keys = this.root.keys;
		int i;

		for (i = 0; i < this.root.degree - 1; i++) {
			if (birthDateLong < keys[i]) {
				break;
			}
		}

		Node child = this.root.childPointers[i];
		if (child instanceof LeafNode) {
			return (LeafNode) child;
		} else {
			return findLeafNode((InternalNode) child, birthDateLong);
		}
	}

	private void sortDictionary(DictionaryPair[] dictionary) {
		Arrays.sort(dictionary, new Comparator<DictionaryPair>() {
			@Override
			public int compare(DictionaryPair o1, DictionaryPair o2) {
				if (o1 == null && o2 == null) {
					return 0;
				}
				if (o1 == null) {
					return 1;
				}
				if (o2 == null) {
					return -1;
				}
				return o1.compareTo(o2);
			}
		});
	}

	private int getMidpoint() {
		return (int) Math.ceil((this.m + 1) / 2.0) - 1;
	}

	private DictionaryPair[] splitDictionary(LeafNode ln, int split) {

		DictionaryPair[] dictionary = ln.dictionary;

		DictionaryPair[] halfDict = new DictionaryPair[this.m];

		for (int i = split; i < dictionary.length; i++) {
			halfDict[i - split] = dictionary[i];
			ln.delete(i);
		}

		return halfDict;
	}

	private Integer[] splitKeys(Integer[] keys, int split) {

		Integer[] halfKeys = new Integer[this.m];

		keys[split] = null;

		for (int i = split + 1; i < keys.length; i++) {
			halfKeys[i - split - 1] = keys[i];
			keys[i] = null;
		}

		return halfKeys;
	}

	private Node[] splitChildPointers(InternalNode in, int split) {

		Node[] pointers = in.childPointers;
		Node[] halfPointers = new Node[this.m + 1];

		for (int i = split + 1; i < pointers.length; i++) {
			halfPointers[i - split - 1] = pointers[i];
			in.removePointer(i);
		}

		return halfPointers;
	}

	private void splitInternalNode(InternalNode in) {

		InternalNode parent = in.parent;

		int midpoint = getMidpoint();
		int newParentKey = in.keys[midpoint];
		Integer[] halfKeys = splitKeys(in.keys, midpoint);
		Node[] halfPointers = splitChildPointers(in, midpoint);

		in.degree = linearNullSearch(in.childPointers);

		InternalNode sibling = new InternalNode(this.m, halfKeys, halfPointers);
		for (Node pointer : halfPointers) {
			if (pointer != null) {
				pointer.parent = sibling;
			}
		}

		sibling.rightSibling = in.rightSibling;
		if (sibling.rightSibling != null) {
			sibling.rightSibling.leftSibling = sibling;
		}
		in.rightSibling = sibling;
		sibling.leftSibling = in;

		if (parent == null) {

			Integer[] keys = new Integer[this.m];
			keys[0] = newParentKey;
			InternalNode newRoot = new InternalNode(this.m, keys);
			newRoot.appendChildPointer(in);
			newRoot.appendChildPointer(sibling);
			this.root = newRoot;

			in.parent = newRoot;
			sibling.parent = newRoot;

		} else {

			parent.keys[parent.degree - 1] = newParentKey;
			Arrays.sort(parent.keys, 0, parent.degree);

			int pointerIndex = parent.findIndexOfPointer(in) + 1;
			parent.insertChildPointer(sibling, pointerIndex);
			sibling.parent = parent;
		}
	}

	public void insert(long birthDateLong, String value) {
		if (isEmpty()) {

			LeafNode ln = new LeafNode(this.m, new DictionaryPair(birthDateLong, value));

			this.firstLeaf = ln;

		} else {
			LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(birthDateLong);

			if (!ln.insert(new DictionaryPair(birthDateLong, value))) {

				ln.dictionary[ln.numPairs] = new DictionaryPair(birthDateLong, value);
				ln.numPairs++;
				sortDictionary(ln.dictionary);

				int midpoint = getMidpoint();
				DictionaryPair[] halfDict = splitDictionary(ln, midpoint);

				if (ln.parent == null) {

					Integer[] parent_keys = new Integer[this.m];
					parent_keys[0] = (int) halfDict[0].key;
					InternalNode parent = new InternalNode(this.m, parent_keys);
					ln.parent = parent;
					parent.appendChildPointer(ln);

				} else {
					int newParentKey = (int) halfDict[0].key;
					ln.parent.keys[ln.parent.degree - 1] = newParentKey;
					Arrays.sort(ln.parent.keys, 0, ln.parent.degree);
				}

				LeafNode newLeafNode = new LeafNode(this.m, halfDict, ln.parent);

				int pointerIndex = ln.parent.findIndexOfPointer(ln) + 1;
				ln.parent.insertChildPointer(newLeafNode, pointerIndex);

				newLeafNode.rightSibling = ln.rightSibling;
				if (newLeafNode.rightSibling != null) {
					newLeafNode.rightSibling.leftSibling = newLeafNode;
				}
				ln.rightSibling = newLeafNode;
				newLeafNode.leftSibling = ln;

				if (this.root == null) {

					this.root = ln.parent;

				} else {
					InternalNode in = ln.parent;
					while (in != null) {
						if (in.isOverfull()) {
							splitInternalNode(in);
						} else {
							break;
						}
						in = in.parent;
					}
				}
			}
		}
	}
}
