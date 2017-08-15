package system.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

public class DateTools {
	/**
	 * 获取当前系统时间戳，单位为秒
	 * @return
	 * @throws Exception
	 */
	public static int getTimeStamp() {
		//int now = (int)(System.currentTimeMillis()/1000);
		//Console.log(now);
		return (int)(System.currentTimeMillis()/1000);
	}
	
	/**
	 * 获取当前系统时间戳，单位为秒
	 * @return
	 * @throws Exception
	 */
	public static long getTimeStampMillis() {
		//int now = (int)(System.currentTimeMillis()/1000);
		//Console.log(now);
		return System.currentTimeMillis();
	}	
	
	/**
	 * 根据字符串获取时间戳
	 * @param date 时间字符串的格式应当为"yyyy-MM-dd HH:mm:ss",
	 * 若为其他时间格式请调用getTime(String date, String format)方法
	 * @return
	 * @throws Exception
	 */
	public static int getTimeStamp(String date) {
		return getTimeStamp(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 根据字符串和时间格式获取时间戳
	 * @param date 时间字符串
	 * @param format 时间格式，如"yyyy-MM-dd HH:mm:ss"等
	 * @return
	 * @throws Exception
	 */
	public static int getTimeStamp(String date, String format) {
		if(StrKit.isBlank(date)){
			return getTimeStamp();
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date time = null;
		try {
			time = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Console.log((int)(time.getTime()/1000));
		return (int)(time.getTime()/1000);
	}
	
	
	/**
	 * 获取当前时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 * @throws Exception
	 */
	public static String getTime() throws Exception {
		return getTime("yyyy-MM-dd HH:mm:ss");
	}	
	
	/**
	 * 以指定的时间格式获取当前时间
	 * @param format 时间格式
	 * @return
	 * @throws Exception
	 */
	public static String getTime(String format) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(now);
	}
	
	/**
	 * 根据时间戳获取时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @param timestamp 时间戳，单位为秒
	 * @return
	 * @throws Exception
	 */
	public static String getTime(int timestamp) throws Exception {
		return getTime(timestamp, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 以指定的时间格式，根据时间戳，获取时间
	 * @param timestamp 时间戳，单位为秒
	 * @param format 指定的时间格式
	 * @return
	 * @throws Exception
	 */
	public static String getTime(int timestamp, String format) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date(timestamp*1000l);
		return sdf.format(date);
	}
	
	/**
	 * 获取当前日期，格式为"yyyy-MM-dd"
	 * @return
	 * @throws Exception
	 */
	public static String getDate(){
		return getTime("yyyy-MM-dd");
	}
	
	/**
	 * 为对象set时间戳和创建时间，字段名分别为"created"和"create_time"，时间格式是"yyyy-MM-dd HH:mm:ss"
	 * @param t 要set时间的对象
	 * @throws Exception
	 */
	public static <T extends Model> void setTime(T t) throws Exception {
		setTime(t, "created", "create_time", "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 为对象set时间戳和创建时间，字段名由用户指定
	 * @param t 要set时间的对象
	 * @param created 指定时间戳字段的名称
	 * @param create_time 指定时间字段的名称
	 * @throws Exception
	 */
	public static <T extends Model> void setTime(T t, String created, String create_time) throws Exception {
		setTime(t, created, create_time, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 为对象set时间戳和创建时间，字段名和时间格式都由用户指定
	 * @param t 要set时间的对象
	 * @param created 指定时间戳字段的名称
	 * @param create_time 指定时间字段的名称
	 * @param format 指定时间格式
	 * @throws Exception
	 */
	public static <T extends Model> void setTime(T t, String created, String create_time, String format) throws Exception {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		t.set(create_time, sdf.format(now));
		t.set(created, (int)(now.getTime()/1000));
	}
	
	
		
	/**
	 * 计算指定天数前的时间
	 * @param date 指定的时间，时间格式为"yyyy-MM-dd"
	 * @param days 经过的天数
	 * @return
	 * @throws Exception
	 */
	public static String daysAgo(String date, int days) throws Exception {
		int timePassed = days *24 *60 *60;
		//Console.log(timePassed);
		int timestamp = getTimeStamp(date, "yyyy-MM-dd");
		//Console.log(timestamp);
		return timeCalculation(timestamp, timePassed, false, "yyyy-MM-dd");
	}
	
	/**
	 * 计算指定天数后的时间
	 * @param date 指定的时间，时间格式为"yyyy-MM-dd"
	 * @param days 经过的天数
	 * @return
	 * @throws Exception
	 */
	public static String daysLater(String date, int days) throws Exception {
		int timePassed = days *24 *60 *60;
		int timestamp = getTimeStamp(date, "yyyy-MM-dd");
		return timeCalculation(timestamp, timePassed, true, "yyyy-MM-dd");
	}
	
	/**
	 * 以指定的时间计算时间，格式为"yyyy-MM-dd HH:mm:ss"
	 * @param timestamp 当前时间， 单位为秒
	 * @param timePassed 经过的时间， 单位为秒
	 * @param forward 时间前进还是后退，true：前进；false：后退
	 * @return
	 * @throws Exception
	 */
	public static String timeCalculation(int timestamp, int timePassed, boolean forward) throws Exception {
		return timeCalculation(timestamp, timePassed, forward, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 以指定的时间和格式计算时间
	 * @param timestamp 当前时间， 单位为秒
	 * @param timePassed 经过的时间， 单位为秒
	 * @param forward 时间前进还是后退，true：前进；false：后退
	 * @param format 指定的时间格式
	 * @return
	 * @throws Exception
	 */
	public static String timeCalculation(int timestamp, int timePassed, boolean forward, String format) throws Exception {
		int time = 0;
		if(forward){
			time = timestamp + timePassed;
		}else{
			time = timestamp - timePassed;
		}
		return getTime(time, format);
	}
}
