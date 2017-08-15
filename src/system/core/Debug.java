package system.core;

import com.jfinal.core.JFinal;

public class Debug {

	public static void main(String[] args) {
		JFinal.start("WebRoot",8080, "/", 5);
	}
	
	public static void print(Object var){
		System.out.println(var);
	}
}
