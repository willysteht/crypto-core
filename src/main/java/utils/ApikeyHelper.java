package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ApikeyHelper {
	
	public static String getApikey(File f) {
		String out = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i == 0)
					out = line;
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Can't read API-Key");
		}
		return out;
	}
	
	/**
	 * 
	 * @param f
	 * @return String[] where [0] is key and [1] is secret
	 */
	public static String[] getApikeyAndSecret(File f) {
		String[] out = new String[2];
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i == 0)
					out[0] = line;
				else
					out[1] = line;
				++i;
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Can't read API-Key");
		}
		return out;
	}
}
