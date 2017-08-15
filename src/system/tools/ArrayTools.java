package system.tools;

import java.util.Arrays;
import java.util.HashSet;

public class ArrayTools {

	/**
	 * 去重 [1,1,2,3,3]返回[1,2,3]
	 * @param arr
	 * @return
	 */
	public static String[] unique(String[] arr){
		String[] unique = new HashSet<String>(Arrays.asList(arr)).toArray(new String[0]);
		
		return unique;
	}
}
