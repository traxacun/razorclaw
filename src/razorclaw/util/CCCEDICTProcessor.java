package razorclaw.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class convert cc-cedict file to two resource files containing simplified
 * and traditional words one per line.
 * 
 * @author Shuai Yuan
 * 
 */
public class CCCEDICTProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			return;
		}
		String inFile = args[0], simplifiedFile = args[1], traditionalFile = args[2];

		try {
			BufferedReader in = new BufferedReader(new FileReader(inFile));
			BufferedWriter simplifiedWriter = new BufferedWriter(
					new FileWriter(simplifiedFile));
			BufferedWriter traditionalWriter = new BufferedWriter(
					new FileWriter(traditionalFile));

			for (String s = in.readLine(); s != null; s = in.readLine()) {
				String[] array = s.split(" ");

				traditionalWriter.write(array[1]);
				traditionalWriter.newLine();

				simplifiedWriter.write(array[0]);
				simplifiedWriter.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
