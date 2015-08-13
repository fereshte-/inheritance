package generateTest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SpreadSheetMaker {


	public Spreadsheet makeSpreadSheet(String address) {

		String csvFile = address;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		ArrayList<String> literals = new ArrayList<String>(), relations = new ArrayList<String>();

		try {

			br = new BufferedReader(new FileReader(csvFile));
			int counter = 0 ;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				if(counter == 0)
					Collections.addAll(relations, line.split(cvsSplitBy));
				else{
					Collections.addAll(literals, line.split(cvsSplitBy));
				}
				counter ++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new Spreadsheet(literals, relations);
	}

}