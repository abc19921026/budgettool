package test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import system.tools.StringTools;
public class StringTest {

   public static void main(String args[]) {
		/*String sn = "";
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyMMddHH");
		//String s = f.format(date).substring(2,8);
		String s = f.format(date);//161011
		//int rand_4 = Math.random()
		int min = 1000, max = 9999; //生成四位数的随机数
       Random random = new Random();

       int random_4 = random.nextInt(max)%(max-min+1) + min;
		sn = "B" + s + "-" + random_4;
		
		System.out.println(sn);*/
	   
	   //String sn = "271828";
	  // sn = StringTools.generate_md5hx(sn);
	   String foo = "1,2";
	   int sn = Integer.parseInt(foo);
	   System.out.println(sn);

   }
}