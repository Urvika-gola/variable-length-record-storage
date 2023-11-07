import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GetContentFromExcelSheets {
	
	public static boolean isValidIndex(String[] arr, int index) {
        return index >= 0 && index < arr.length;
    }
	
	public List<Chips> getChipsList()   
	{  
		String line = "";  
		String splitBy = ",";  
		List<Chips> chipsList = new ArrayList<Chips>();
		try {  
		//parsing the chips.csv file into BufferedReader class constructor  
			BufferedReader br = new BufferedReader(new FileReader("/home/cs560/fall2022/chips.csv"));  
			//Skipping metadata line
			br.readLine();
			while ((line = br.readLine()) != null) {   //returns a Boolean value  
				Chips chip = new Chips();
				String[] chips = line.split(splitBy);    // use comma as separator  
				if (isValidIndex(chips, 0)) 
					chip.setProduct(chips[0]);
				else 
					chip.setProduct(null);
				
				if (isValidIndex(chips, 1)) 
					chip.setType(chips[1]);
				else 
					chip.setType(null);
				
				if (isValidIndex(chips, 2))
					chip.setReleaseDate(chips[2]);
				else
					chip.setReleaseDate(null);
				
				if (isValidIndex(chips, 3))
					chip.setTransistors(chips[3]);
				else 
					chip.setTransistors(null);		
				chipsList.add(chip);
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		}
		// chipsList is a list, that will contain all the rows from the CSV file
		return chipsList;
	}  
}
