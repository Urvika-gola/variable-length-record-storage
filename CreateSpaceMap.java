import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The CreateSpaceMap class is designed to manage the allocation and tracking of space blocks,
 * which could be used in file systems, memory management, or other systems that require
 * space allocation.
 */
public class CreateSpaceMap  {  
	private Map<Integer, char[]> blockMap = new HashMap<Integer, char[]>();
	private Map<Integer, Integer> blockRecordStructureLength = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> blockBlockStructureLength = new HashMap<Integer, Integer>();
	private List<Double> freeSpaceMapList = new ArrayList<Double>();
	
	public static void main(String args[]) {
		
		CreateSpaceMap mainClassObject = new CreateSpaceMap();
		GetContentFromExcelSheets getContent = new GetContentFromExcelSheets();
		List<Chips> chipList = getContent.getChipsList();
		
		char[] blockArray = new char[1000];  // blocks will be saved in a char array of size 1000
		blockArray = initializeBlock(blockArray);
		
		mainClassObject.blockMap.put(1, blockArray); //maintain a map which contains block number as key, and the contents of the block as value
		
		// storing the length of the record structure of the block i.e the space occupied by the data after the free space ends
		// initially when there is no data in the block, it means that block 1 has 0 length i.e there is no data after free space. 
		mainClassObject.blockRecordStructureLength.put(1, 0);
		
		// storing the length of the block header, it is Initialized to 6 as, 3 bytes for total number of records and 3 bytes for free space ending
		// initially when there is no data in the block, it means that block 1 has 6 length header.
		mainClassObject.blockBlockStructureLength.put(1, 6);
		
		
		// maintain a free space map list, and initially since there is no data in block, the space in the block is at it's max i.e 15/16
		mainClassObject.freeSpaceMapList.add((Double) 15.0); 
		
		for(Chips chip : chipList) {
			
			// Includes total length of record + bitmap (4) + offset and length for two variable length strings(4+4 i.e 8)
			int lengthOfChipInRecordStructure = (chip.getType() == null ? 3 : chip.getType().length()) +
												(chip.getReleaseDate() == null ? 10: chip.getReleaseDate().length()) +
												(chip.getProduct() == null ? 0 : chip.getProduct().length()) +
												(chip.getTransistors() == null ? 0 : chip.getTransistors().length()) +
												4 + 8; 
					
			int lengthOfChipInBlockStructure = 6; // For every record inserted, 6 bytes are added to the block structure
			
			// to determine in which block number will the incoming recored be stored in 
			int availableBlockNumber = getFreeAvailableBlockNumber(lengthOfChipInRecordStructure, lengthOfChipInBlockStructure,  mainClassObject);
			if (availableBlockNumber > 0) {
				
				addDataToTheBlock(availableBlockNumber, mainClassObject, chip);
			}
			// -1 is returned if space is not found in existing blocks and new block needs to be created
			else {
				mainClassObject.freeSpaceMapList.add((Double) 15.0);
				char[] blockArrayInsertion = new char[1000];  
				blockArray = initializeBlock(blockArrayInsertion);
				int newBlockNumberCreated = mainClassObject.freeSpaceMapList.size();
				
				mainClassObject.blockMap.put(newBlockNumberCreated, blockArrayInsertion);
				mainClassObject.blockRecordStructureLength.put(newBlockNumberCreated, 0); // In the beginning since there is no record it is set to 0
				
				//Initialized to 6 - three bytes for total number of records and 3 bytes for free space ending
				mainClassObject.blockBlockStructureLength.put(newBlockNumberCreated, 6);
				
				addDataToTheBlock(newBlockNumberCreated, mainClassObject, chip);
			}
		}		
		String finalCharString = new String();
		
		for(Entry<Integer, char[]> each : mainClassObject.blockMap.entrySet()) {
			System.out.println("Block Number: " + each.getKey() + "\t\t\t" + "FreeSpaceMapValue: " + mainClassObject.freeSpaceMapList.get(each.getKey()-1).intValue());
			finalCharString = finalCharString + new String(each.getValue()) + "\n";
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		    writer.write(finalCharString);
		    writer.close();
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	private static void addDataToTheBlock(int blockNumber, CreateSpaceMap mainClassObject, Chips chip) {
		
		char[] blockStructure = mainClassObject.blockMap.get(blockNumber);
		int numOfRecordsInBlock = getTotalRecordsInBlock(blockStructure);
		int freeSpaceEndingPosition = getFreespaceEndingPosition(blockStructure);
		
		char strBitMap[] = {'0', '0', '0', '0'};
		
		// type is a fixed length data, if it's null, we pad it by xxx and set it's corresponding null bitmap to 1
		String strType = chip.getType();
		if(strType == null) {
			strType="xxx";
			strBitMap[0] = '1';
		}
		// store the length of the type 
		int typeLength = strType.length();
		
		String strReleaseDate = chip.getReleaseDate();
		if(strReleaseDate.compareTo("NaT")==0 || strReleaseDate==null) {
			strReleaseDate = "not-a-date"; // this contains 10 characters, same as a valid date string
			strBitMap[1] = '1';
		}
		int releaseDateLength = strReleaseDate.length();
		
		
		String strProduct = chip.getProduct();
		int productLength;
		if(strProduct == null) {
			strBitMap[2] = '1';
			productLength=0;
		} else 
			productLength = strProduct.length();

		String strTransistors = chip.getTransistors();
		int iTransistorsLength;
		if(strTransistors == null) {
			strBitMap[3] = '1';
			iTransistorsLength = 0;
		} else
			iTransistorsLength = strTransistors.length();
		
		// concatenate the variable length strings 
		String concatenatedVariableLengthString = ((strProduct==null) ? "" :strProduct) + ((strTransistors==null) ? "" :strTransistors);
		
		// concatenate the fixed length strings 
		String concatenatedFixedLengthString = strType + strReleaseDate;
		
		// concatenate the bitmap, fixed length string and variable length string
		String concatenateBitMapAndFixedAndVariable = new String(strBitMap) + concatenatedFixedLengthString + concatenatedVariableLengthString;
		
		char[] recordVariableLengthPositioning = new char[8];
		
		// Constant because only two variable length fields, each consume (4 chars so 2*4 = 8) + 4 (bitmap) + 3 (Type) + 10 (date) = 25
		// therefore we know that the variable length data will start at 25th char
		recordVariableLengthPositioning[0] = '2';
		recordVariableLengthPositioning[1] = '5';
		
		// if product length is 7, we make it 007, or if it's 70, we make it 070 (eg) so that it occupies 3 char as expected
		String strProductLength = String.format("%02d", productLength);
		
		recordVariableLengthPositioning[2] = strProductLength.charAt(0);
		recordVariableLengthPositioning[3] = strProductLength.charAt(1);
		
		int secondsVariableStartPosition = 25 + productLength;
		
		recordVariableLengthPositioning[4] = String.valueOf(secondsVariableStartPosition).charAt(0);
		recordVariableLengthPositioning[5] = String.valueOf(secondsVariableStartPosition).charAt(1);

		String strTransistorsLength = String.format("%02d", iTransistorsLength);

		recordVariableLengthPositioning[6] = strTransistorsLength.charAt(0);
		recordVariableLengthPositioning[7] = strTransistorsLength.charAt(1);
		
		//Final Record Structure
		String strRecordStructure = new String(recordVariableLengthPositioning) + concatenateBitMapAndFixedAndVariable;
		int iLenRecordStructure = strRecordStructure.length();
		
		
		int newNumOfRecordsInBlock = numOfRecordsInBlock + 1;
		//Prefix with 0's if required = three digit
		String strNumOfRecordsInBlock = String.format("%03d", newNumOfRecordsInBlock);
		
		//Updating the new number of records in block structure(header) in block array
		for(int i = 0; i < strNumOfRecordsInBlock.length(); i++)
			blockStructure[i] = strNumOfRecordsInBlock.charAt(i);
		
		freeSpaceEndingPosition = freeSpaceEndingPosition - iLenRecordStructure;
		
		String strfreeSpaceEndingPosition = String.format("%03d", freeSpaceEndingPosition);
		
		//Updating the free space ending value in block structure(header) in block array
		for(int i = 0; i < strfreeSpaceEndingPosition.length(); i++)
			blockStructure[i+3] = strfreeSpaceEndingPosition.charAt(i);
		
		String strBlockStructure = strNumOfRecordsInBlock + freeSpaceEndingPosition;
		
		int nextAvailPosBlockStructure = 6 + (6 * numOfRecordsInBlock);
		
		int incomingDataBlockStartPos = freeSpaceEndingPosition + 1;
		String strIncomingDataBlockStartPos = String.format("%03d", incomingDataBlockStartPos);
		
		String strIncomingDataBlockLength = String.format("%03d", iLenRecordStructure);
		
		String incomingDataInBlock = strIncomingDataBlockStartPos + strIncomingDataBlockLength;
		
		
		//Updating the block structure
		for(int i = 0; i< incomingDataInBlock.length(); i++)
			blockStructure[i+nextAvailPosBlockStructure] = incomingDataInBlock.charAt(i);
		
		//Updating the record Structure
		int recordStructureStartPosition = incomingDataBlockStartPos;
		for(int i = 0; i<iLenRecordStructure; i++)
			blockStructure[recordStructureStartPosition + i] = strRecordStructure.charAt(i);
		
		//Updating the block array for the corresponding block number after adding new data
		mainClassObject.blockMap.put(blockNumber, blockStructure);
		
		int currentBlockStructureLength;
		int currentRecordStructureLength;
		
		if(mainClassObject.blockBlockStructureLength.get(blockNumber)!=null) {
			currentBlockStructureLength = mainClassObject.blockBlockStructureLength.get(blockNumber);
			mainClassObject.blockBlockStructureLength.put(blockNumber, currentBlockStructureLength + incomingDataInBlock.length());
		} else {
			currentBlockStructureLength = mainClassObject.blockBlockStructureLength.put(blockNumber, incomingDataInBlock.length());
		}
		
		if(mainClassObject.blockRecordStructureLength.get(blockNumber)!=null) {
			currentRecordStructureLength = mainClassObject.blockRecordStructureLength.get(blockNumber);
			mainClassObject.blockRecordStructureLength.put(blockNumber, currentRecordStructureLength + iLenRecordStructure);
		} else {
			currentRecordStructureLength = mainClassObject.blockRecordStructureLength.put(blockNumber, iLenRecordStructure);
		}
		
		int newAvailableFreeSpaceInBytes = 1000 - (currentBlockStructureLength + incomingDataInBlock.length()) 
				                         - (currentRecordStructureLength + iLenRecordStructure);
		
		newAvailableFreeSpaceInBytes = (newAvailableFreeSpaceInBytes<=0)?0 : newAvailableFreeSpaceInBytes;
		
		Double freeSpaceFraction = (newAvailableFreeSpaceInBytes * 16.0)/994.0;
		
		// update the free space map list whenever a record is inserted into the block by calculating the free space remaining after adding the record
		mainClassObject.freeSpaceMapList.set(blockNumber - 1, Math.floor(freeSpaceFraction));
				
	}

	private static int getFreeAvailableBlockNumber(int lengthOfChipInRecordStructure, int lengthOfChipInBlockStructure, CreateSpaceMap mainClassObject) {
		
		int blockNumber = 1;
		List<Double> temp = mainClassObject.freeSpaceMapList;
		for(Double eachFreeMapFraction : mainClassObject.freeSpaceMapList) {			
			// calculate the free space/bytes available using the free space map
			double freeBytesAvailable =  ((eachFreeMapFraction / 16.0) *  994.0);
			
			// if the space available if more than required, then we can insert the record to the block
			if(freeBytesAvailable >= (lengthOfChipInBlockStructure + lengthOfChipInRecordStructure))
				return blockNumber;
			blockNumber++;
		}
		// if no free space block is found then we return -1
		return -1;
	}

	// getting the integer form from the number of total record fields of the block i.e 0th, 1st and 2nd index field combined.
	private static int getTotalRecordsInBlock(char[] blockStructure) {
		String concatenatedNumberOfRecords = new StringBuilder().append(blockStructure[0]).append(blockStructure[1]).append(blockStructure[2]).toString();
		return Integer.parseInt(concatenatedNumberOfRecords);
	}
	
	// getting the integer form from the number of total record fields of the block i.e 3rd, 4th and 5th index field combined.
	private static int getFreespaceEndingPosition (char[] blockStructure) {
		String concatenatedNumberOfRecords = new StringBuilder().append(blockStructure[3]).append(blockStructure[4]).append(blockStructure[5]).toString();
		return Integer.parseInt(concatenatedNumberOfRecords);
	}

	private static char[] initializeBlock(char[] blockArray) {
		// Setting 000997 for each block structure of the newly created block
		// signifying that there are 0 records and currently there is 997 free space available.
		blockArray[0] = '0';
		blockArray[1] = '0';
		blockArray[2] = '0';
		blockArray[3] = '9';
		blockArray[4] = '9';
		blockArray[5] = '7';
		// fill the remaining array with dots which represent free space
		for(int i = 6; i< blockArray.length;i++)
			blockArray[i] = '.';	
		return blockArray;
	}
}