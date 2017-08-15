package system.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
public class BaseCache {

	public static void clear(String cacheName){
		CacheManager manager = CacheManager.getInstance();
		//String cacheName = "menu";
		Cache cache = manager.getCache(cacheName);
		cache.removeAll();;
	}
	
	public static void clearAll(){
		CacheManager manager = CacheManager.getInstance();
		manager.clearAll();
	}
}
