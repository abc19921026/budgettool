package system.models;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class VariableModel {
	private static final String CACHE_NAME = "system_variable";//ehcache.xml配置文件中的缓存名称
	public static String find_site_name (){
		/*String site_name = "";
		String sql = "select * from system_variable where name = ?";
		Record re = Db.findFirst(sql, "site_name");
		if(re != null){
			site_name = re.getStr("value");
		}
		return site_name;*/
		return find("site_name");
	}
	
	public static String find (String variable_name){
		String value = "";
		value = CacheKit.get(CACHE_NAME, variable_name);
		if(value != null){
			return value;
		}
		String sql = "select * from system_variable where name = ?";
		Record re = Db.findFirst(sql, variable_name);
		if(re != null){
			value = re.getStr("value");
			CacheKit.put(CACHE_NAME, variable_name, value);
		}
		return value;
	}	
	
}
