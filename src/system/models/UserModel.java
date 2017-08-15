package system.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;

import system.dao.User;
import system.tools.StringTools;

public class UserModel extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8850723181645329484L;
	private static HashMap<String, Integer> hsmap=new HashMap<String, Integer>();
    static{
	   Collections.synchronizedMap(hsmap);
   }
	/**
	 * @author 钟志苗
	 * user分页返回json
	 * @param field 
	 * @param keyword 
	 */
	/*public static List<User> toResult(int page, int rows) throws Exception {
		Page<User> pages;
		//String sql = "select * ";
		//String sqlex = " from system_user where ("+field+" like \"%"+keyword+"%\" or '0' in ('"+keyword+"')) order by uid";
		String sql = "select * ";
		String sqlex = "from system_user order by uid";
		pages = dao.paginate(page, rows, sql, sqlex);
		//int total = pages.getTotalRow();
		List<User> list = pages.getList();
		return list;
	}*/
	
	/**
	 * @author 钟志苗
	 * user分页返回json
	 * @param field 
	 * @param keyword 
	 */
	public static Map<String, Object> find_users(int page, int rows, String name) throws Exception {
		Map<String, Object> re = new HashMap<String, Object>();
		Page<User> pages;
		String sql = "select * ";
		String sqlex = "from system_user where name like ? or '0' = ? order by uid";
		pages = dao.paginate(page, rows, sql, sqlex, "%"+name+"%", "%"+name+"%");
		//int total = pages.getTotalRow();
		List<User> list = pages.getList();
		int total = pages.getTotalRow();	
		re.put("total", total);
		re.put("rows", list);
		return re;

	}
	
	/**
	 * user详细显示
	 * 
	 */
	public static User detailsLoad(int uid) throws Exception {
		User ml = dao.findById(uid);
		return ml;
	}
	/**
	 * 获取新建users的uid
	 */
	public static Object getSysUid() throws Exception{
		return hsmap.get("uid");
	}
	/**
	 * 清空map
	 * @throws Exception
	 */
	public static void deleteMap() throws Exception{
		hsmap.clear();
	}
	/**
	 * 用户删除
	 * @param ml
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteUser(String uid)throws Exception{
		boolean delete=false;
		User ml_1 = dao.findById(uid);
		if(ml_1 != null){
			//批量删除
			delete =dao.deleteById(uid);
		}
		return delete;
	}
	
	/**
	 * 用户更新或插入
	 * @param ml
	 * @return 
	 * @throws Exception
	 */
	public static boolean detailsUpdate(User ml) throws Exception {
		boolean updated = false;
		User ml_1 = dao.findById(ml.getUid());
		if(ml_1 != null){
			updated = ml.update();
		}else{
			ml.setUid(null);
			updated = ml.save();
			String sql="select max(uid) as uid from system_user";
			User users=dao.findFirst(sql);
			hsmap.put("uid",users.getUid());
		}
		return updated;
	}

	public static HashMap<String, Integer> getHsmap() {
		return hsmap;
	}
	public static void setHsmap(HashMap<String, Integer> hsmap) {
		UserModel.hsmap = hsmap;
	}
	/**
	 * 教师档案更新email
	 * @param email
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static boolean detailsEmail(String email,int id) throws Exception {
		boolean updated = false;
		User ml_1 = dao.findById(id);
		if(ml_1 != null){
			ml_1.setMail(email);
			updated =ml_1.update() ;
		}
		return updated;
	}
	
	public static boolean check_user_status(int uid){
		boolean re = false;
		User user = dao.findById(uid);
		if(user != null){
			int status = user.getStatus();
			if(status >= 0){
				re = true;
			}
		}
		return re;
	}
	/**
	 * 
	 * @param username
	 * @param pass
	 * @return
	 */
	public static int user_check_password(String username, String pass){
		int uid = 0;//-1账号不存在，0密码不正确，1用户名密码正确
		
		User user = user_load_by_name(username);
		
		if(user == null){
			return -1;
		}
		
		String password = DigestUtils.md5Hex(pass);
		//String password = StringTools.mpMD5(pass);
		//String password = pass;
		String stored_hash = user.getPass();
		/*Devel.print(pass);
		Devel.print(password);
		Devel.print(stored_hash);*/
		
		if(password.equals(stored_hash)){
			uid = user.getUid();
		}else{
			uid = 0;
		}
		return uid;

		
	}
	
	/**
	 * 生成密码
	 * @param pass
	 * @return
	 */
	public static String generate_pass(String pass){
		return StringTools.generate_md5hx(pass);
	}
	
	/**
	 * 
	 */
	public static boolean check_username_duplicated(String name){
		boolean re = false;
		String sql = "select * from system_user where name = ? limit 1";
		User user = dao.findFirst(sql, name);
		if(user != null){
			re = true;
		}
		return re;
	}
	
	/**
	 * 
	 * @param headshot
	 * @throws Exception
	 */
	public static boolean headshotUpdate(String headshot,int uid) throws Exception {
		
			User user = user_load_by_uid(uid);
			user.setPicture(headshot);
			return  user.update();
		
	}
	/**
	 * Try to validate the user's login credentials locally.
	 *
	 * @param $name
	 *   User name to authenticate.
	 * @param $password
	 *   A plain-text password, such as trimmed text from form values.
	 * @return
	 *   The user's uid on success, or FALSE on failure to authenticate.
	 */
	public String user_authenticate(String name, String password) {
	  String uid = "0";

	  return uid;
	}
	
	
	/**
	 * Fetch a user object by account name.
	 *
	 * @param $name
	 *   String with the account's user name.
	 * @return
	 *   A fully-loaded $user object upon successful user load or FALSE if user
	 *   cannot be loaded.
	 *
	 * @see user_load_multiple()
	 */
	public static User user_load_by_name(String name){
		String sql = "select * from system_user where name = ?";
		List<User> users = dao.find(sql, name);
		if(users.size() >= 1 ){
			return users.get(0);
		}else{
			return null;
		}
		
	}
	
	public static User user_load_by_uid(int uid){
		String sql = "select * from system_user where uid = ?";
		User user = dao.findFirst(sql, uid);
		return user;
		
	}
}
