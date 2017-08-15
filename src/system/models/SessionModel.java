package system.models;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;

import system.dao.Session;
import system.tools.DateTools;


public class SessionModel extends Session {

	/*public void save(){
		
	}*/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void update_user_tokens (int uid, String sid, String token, String hostname) throws Exception {
	
		//int created = DateTools.getTimeStamp();
		//String create_time = DateTools.getTime();

		//先清空
		clear_user_session(uid, sid);
		//再插入
		insert_user_sessions(uid, sid, token, hostname);
		
	}
	

	public static void insert_user_sessions (int uid, String sid, String token, String hostname) throws Exception {
		
		int created = DateTools.getTimeStamp();
		String create_time = DateTools.getTime();

			//写入session表

			new Session().set("uid", uid)
			.set("sid",sid)
			.set("token",token)
			.set("hostname",hostname)
			.set("created", created)
			.set("create_time", create_time).save();
		
	}	
	
	public boolean find_user_sessions(int uid, String sid, String token) throws  Exception{
		String sql = "select * from system_session where uid =? and token=?";
		List<Session> s = dao.find(sql, uid, token);
		if(s != null){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public static int find_user_by_token(String token){
		String sql = "select * from system_session where token=?";
		//TODO 下一步为增强系统安全性，服务器端加上过期时间判断，
		Session s = dao.findFirst(sql, token);
		if(s != null){
			return s.getUid();
		}else{
			return 0;
		}
	}	
	
	public static void clear_user_session(int uid, String sid){
		String sql = "delete from system_session where uid=? and sid=?";
		//Record record = new Record().set("uid", uid).set("sid", sid);
		Db.update(sql, uid, sid);

	}
	
	public static void delete_user_token(String token){
		String sql = "delete from system_session where token=?";
		//Record record = new Record().set("uid", uid).set("sid", sid);
		Db.update(sql, token);

	}	

}
