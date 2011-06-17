package razorclaw.evaluation.kea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import razorclaw.evaluation.storage.StorageHandler;

public class FormatResult {

	private static final String BASE_PATH = "data/full-list/kea-results/kea-cn/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> result = new ArrayList<String>();

		File f = new File(BASE_PATH);
		String[] keyFiles = f.list();
		String wrapper = "";

		for (String file : keyFiles) {
			if (file.contains(".key")) {
				wrapper = file.substring(0, file.indexOf(".")) + "\t";

				try {
					BufferedReader in = new BufferedReader(new FileReader(
							BASE_PATH + file));

					String line = "";
					int counter = 0;
					while ((line = in.readLine()) != null && counter++ < 3) {
						wrapper += line + ", ";
					}

					wrapper = wrapper.substring(0, wrapper.length() - 2);

					result.add(wrapper);
				} catch (IOException e) {
					e.printStackTrace();

					continue;
				}
			}
		}

		StorageHandler.saveResult(result, "data/full-list/kea_cn.txt");
	}
}
