package system.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

public class BaseModel {
	/**
	 * 为对象set时间戳和创建时间，字段名分别为"created"和"create_time"，时间格式是"yyyy-MM-dd HH:mm:ss"
	 * @param t 要set时间的对象
	 * @throws Exception
	 */
	public static <T extends Model> void setCreateTime(T t) {	
		setTime(t, "created", "create_time", "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 为对象set时间戳和创建时间，字段名分别为"updated"和"update_time"，时间格式是"yyyy-MM-dd HH:mm:ss"
	 * @param t 要set时间的对象
	 * @throws Exception
	 */
	public static <T extends Model> void setUpdateTime(T t)  {
		setTime(t, "updated", "update_time", "yyyy-MM-dd HH:mm:ss");
	}	
	
	/**
	 * 为对象set时间戳和创建时间，字段名由用户指定
	 * @param t 要set时间的对象
	 * @param field_timestamp 指定时间戳字段的名称
	 * @param field_time 指定时间字段的名称
	 * @throws Exception
	 */
	public static <T extends Model> void setTime(T t, String field_timestamp, String field_time) {
		setTime(t, field_timestamp, field_time, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 为对象set时间戳和创建时间，字段名和时间格式都由用户指定
	 * @param t 要set时间的对象
	 * @param field_timestamp 指定时间戳字段的名称
	 * @param field_time 指定时间字段的名称
	 * @param format 指定时间格式
	 * @throws Exception
	 */
	public static <T extends Model> void setTime(T t, String field_timestamp, String field_time, String format) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		t.set(field_time, sdf.format(now));
		t.set(field_timestamp, (int)(now.getTime()/1000));
	}
}
