package system.tools;

import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class StringTools {

	public  static String implode(String[] split){
		//String foo = "This,that,other";
		//String[] split = foo.split(",");
		try{
			if(split == null || split.length <=0 ){
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < split.length; i++) {
			    sb.append(split[i]);
			    if (i != split.length - 1) {
			        sb.append(",");
			    }
			}
			String joined = sb.toString();
			
			return joined;
		}catch (Exception e){
			return "";
		}
	}
	
	public  static String implode(List<String> split_list){
		String[] split = 	split_list.toArray(new String[0]);//supported by jdk 1.8 and above
		return implode(split);
	}
		
	
	
	/**
	 * 根据特定字符串进行分割
	 * @param split
	 * @return string
	 */
	public  static String split(String text,String split){
		 String[] arr = text.split(split);
		 return implode(arr);
	}
	
	/**
	 * MD5加密
	 * @param text
	 * @return
	 */
	public static String mpMD5(String text) {
		String md5_str = "";
		if (text == null)
			text = "";
		if (text.length() > 0) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(text.getBytes());
				byte[] array = md5.digest();
				for (int i = 0; i < array.length; i++)
					md5_str += array[i];
				md5_str = md5_str.replaceAll("-", "").substring(0, 16);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return md5_str;
	}
	
	public static String generate_md5hx(String pass){
		String hash = "";
		hash = DigestUtils.md5Hex(pass);
		return hash;
	}
}
