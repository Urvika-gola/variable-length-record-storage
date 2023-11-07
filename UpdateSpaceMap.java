import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class UpdateSpaceMap {	
	
	public static void printBlockDetails(String strBlock, int numOfBlocks) {
	    int numOfRecord = 0;
		Map<Integer, List<Integer>> mapOffsetLengthRecords = new HashMap<Integer, List<Integer>>();
		int recordNum = 1;
		for(int i = 6; strBlock.charAt(i)!='.';i=i+6) {
			List<Integer> recordCoordinates = new LinkedList<Integer>();
			int recordOffset = Integer.parseInt(new StringBuilder().append(strBlock.charAt(i)).append(strBlock.charAt(i+1)).append(strBlock.charAt(i+2)).toString());
			recordCoordinates.add(recordOffset);
			int recordLength = Integer.parseInt(new StringBuilder().append(strBlock.charAt(i+3)).append(strBlock.charAt(i+4)).append(strBlock.charAt(i+5)).toString());
			recordCoordinates.add(recordLength);
			mapOffsetLengthRecords.put(recordNum, recordCoordinates);
			recordNum++;
		}
		System.out.printf("%-30.30s  %-30.30s  %-30.30s  %-30.30s   %-30.30s%n", "No.", "Type", "Date", "Product", "Transistors");
		for(Entry<Integer, List<Integer>> eachRecord : mapOffsetLengthRecords.entrySet()) {
			int rOffset = eachRecord.getValue().get(0);
			int rLength = eachRecord.getValue().get(1);
			
			String record = strBlock.substring(rOffset, rOffset+rLength-1);
			
			int firstVariableOffset = Integer.parseInt(new StringBuilder().append(record.charAt(0)).append(record.charAt(1)).toString());
			int firstVariableLength = Integer.parseInt(new StringBuilder().append(record.charAt(2)).append(record.charAt(3)).toString());
			
			int secondVariableOffset = Integer.parseInt(new StringBuilder().append(record.charAt(4)).append(record.charAt(5)).toString());
			int secondVariableLength = Integer.parseInt(new StringBuilder().append(record.charAt(6)).append(record.charAt(7)).toString());
			
			String type = record.substring(12, 15);
			String date = record.substring(15, 25);
			String product = "";
			
			//Checking null Bitmap to see if product is empty or not
			if(record.charAt(10)!='1')
				product = record.substring(firstVariableOffset, firstVariableOffset + firstVariableLength-1);
			
			//Checking null Bitmap to see if transistors is empty or not
			String transistors = "";
			if(record.charAt(11)!='1')
				transistors = record.substring(secondVariableOffset, secondVariableOffset + secondVariableLength-1);
			if (transistors == "")
				transistors = "null"; // entering null string to avoid empty spaces in the result, which could lead to confusion if the data is available and not seen, or data is not there.
							
			System.out.printf("%-30.30s  %-30.30s  %-30.30s  %-30.30s   %-30.30s%n", numOfRecord, type, date, product, transistors);

			numOfRecord++;
		}
	}
	public static void ExtraOperations(String dbFileOutputArray[], int numOfBlocks) {
		Scanner sc= new Scanner(System.in);   
		System.out.println("\n\nEnter the value of M");
		int m = sc.nextInt();  
		System.out.println("Enter the value of N");
		int n = sc.nextInt();  
		
		
		int blockNum = 1;

		System.out.println("\n\n==========================================================================================================================================");
		System.out.println("Number of chips that have atleast " +m+ ", but nomore than " +n+" are:\n------------------------------------------------------------------------------------------------------------------------------------------");
		
		System.out.printf("%-30.30s  %-30.30s  %-30.30s   %-30.30s%n", "Type", "Date", "Product", "Transistors");

	    while (m>0 && n>0) {

			for(String strBlock : dbFileOutputArray) 
			{
				Map<Integer, List<Integer>> mapOffsetLengthRecords = new HashMap<Integer, List<Integer>>();
				int recordNum = 1;
				for(int i = 6; strBlock.charAt(i)!='.';i=i+6) 
				{
					List<Integer> recordCoordinates = new LinkedList<Integer>();
					int recordOffset = Integer.parseInt(new StringBuilder().append(strBlock.charAt(i)).append(strBlock.charAt(i+1)).append(strBlock.charAt(i+2)).toString());
					recordCoordinates.add(recordOffset);
					int recordLength = Integer.parseInt(new StringBuilder().append(strBlock.charAt(i+3)).append(strBlock.charAt(i+4)).append(strBlock.charAt(i+5)).toString());
					recordCoordinates.add(recordLength);
					mapOffsetLengthRecords.put(recordNum, recordCoordinates);
					recordNum++;
				}
				for(Entry<Integer, List<Integer>> eachRecord : mapOffsetLengthRecords.entrySet()) {
					int rOffset = eachRecord.getValue().get(0);
					int rLength = eachRecord.getValue().get(1);
					
					String record = strBlock.substring(rOffset, rOffset+rLength-1);
					
					int firstVariableOffset = Integer.parseInt(new StringBuilder().append(record.charAt(0)).append(record.charAt(1)).toString());
					int firstVariableLength = Integer.parseInt(new StringBuilder().append(record.charAt(2)).append(record.charAt(3)).toString());
					
					int secondVariableOffset = Integer.parseInt(new StringBuilder().append(record.charAt(4)).append(record.charAt(5)).toString());
					int secondVariableLength = Integer.parseInt(new StringBuilder().append(record.charAt(6)).append(record.charAt(7)).toString());
					
					String type = record.substring(12, 15);
					String date = record.substring(15, 25);
					String product = "";
					
					//Checking null Bitmap to see if product is empty or not
					if(record.charAt(10)!='1')
						product = record.substring(firstVariableOffset, firstVariableOffset + firstVariableLength-1);
					
					//Checking null Bitmap to see if transistors is empty or not
					String transistors = "";
					if(record.charAt(11)!='1')
						transistors = record.substring(secondVariableOffset, secondVariableOffset + secondVariableLength-1);
					if (transistors == "")
						transistors = "null"; // entering null string to avoid empty spaces in the result, which could lead to confusion if the data is available and not seen, or data is not there.
					
					if ((transistors != "null") && (Integer.parseInt(transistors) >=m) &&  (Integer.parseInt(transistors)<n))
						System.out.printf("%-30.30s  %-30.30s  %-30.30s   %-30.30s%n", type, date, product, transistors);
				}
			}
			System.out.println("\n\nEnter the value of M");
			m = sc.nextInt();  
			System.out.println("Enter the value of N");
			n = sc.nextInt();
		}
	}
	public static void main(String[] args) {
		String dbFileData = new String();
		// read the dbfile.txt until we encounter it's end i.e the reader is -1
		File file = new File("dbfile.txt");
        try (FileReader fr = new FileReader(file))
        {
            int content;
            while ((content = fr.read()) != -1) {
            	dbFileData = dbFileData + (char) content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		String dbFileOutputArray[] = dbFileData.split("\n");
		
		// added \n for readability but for counting the total number of blocks in the dbfile.txt, we get rid of the \n and calculate the number of blocks.
		int numOfBlocks = dbFileData.replace("\n", "").length()/1000;  // to find the total number of blocks in the text file.

		String strFirstBlock = new String();
		String strLastBlock = new String();

		int blockNum = 1;
		for(String each : dbFileOutputArray) {
			int countOfFreeSpace = 0;
			for(int i=0; i<each.length(); i++)
				if(each.charAt(i) == '.')
					countOfFreeSpace+=1;
			System.out.println("Block Number: " + String.format("%02d", blockNum) + "\t\t" + 
					"Number Of Records: " + Integer.parseInt(new StringBuilder().append(each.charAt(0)).append(each.charAt(1)).append(each.charAt(2)).toString()) + "\t\t" + 
					"Size of Freespace: " + countOfFreeSpace + " bytes");
			if(blockNum ==1 || blockNum == numOfBlocks)
				if(blockNum == 1)
					strFirstBlock = each;
				else
					strLastBlock = each;
			blockNum++;
		}
		System.out.println("-----------------------------------------------------------------------------------------------");

		System.out.println("\n\n==========================================================================================================================================");
		System.out.println("First Block Information\n------------------------------------------------------------------------------------------------------------------------------------------");
		printBlockDetails(strFirstBlock, numOfBlocks);
		
		System.out.println("\n\n==========================================================================================================================================");
		System.out.println("Last Block Information\n-------------------------------------------------------------------------------------------------------------------------------------------");
		printBlockDetails(strLastBlock, numOfBlocks);	
		
		ExtraOperations(dbFileOutputArray, numOfBlocks);

	}
}