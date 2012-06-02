package eu.prismsw.tools;

import java.util.ArrayList;
import java.util.List;

public class ListFunctions {
	
	/** Converts a List of objects to one String, consisting of each object.toString(), separated by the separator **/
	public static <T> String listToString(List<T> list, String separator) {
		String str = "";
		for(int i = 0; i < list.size(); i++) {
			if(i > 0) {
				str += separator;
			}
			
			str += list.get(i).toString();
		}
		return str;
	}
	
	/** Converts a List of objects to a List of the object.toString() **/
	public static <T> List<String> listToStringList(List<T> list) {
		List<String> strList = new ArrayList<String>();
		
		for(Object o : list) {
			strList.add(o.toString());
		}
		
		return strList;
	}
}
