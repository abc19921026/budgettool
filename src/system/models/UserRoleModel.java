package system.models;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;

import system.dao.Role;
import system.dao.UserRole;
import system.tools.StringTools;

public class UserRoleModel extends UserRole {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8039496388519616350L;

	/**
	 * 根据用户ID获取角色ID列表
	 * @param uid 用户ID
	 * @return
	 * @throws Exception
	 */
	public static List<Integer> findByUid(int uid) {
		String sql = "select * from system_user_role where uid= ? ";
		List<UserRole> list_ur = dao.find(sql, uid);
		List<Integer> list_rid = new ArrayList<Integer>();
		for(UserRole sur : list_ur){
			Integer rid = sur.getRid();
			list_rid.add(rid);
		}
		return list_rid;
	}
	
	public static String find_rids(int uid) {
		String sql = "select * from system_user_role where uid= ? ";
		List<UserRole> list_ur = dao.find(sql, uid);
		List<String> list_rid = new ArrayList<String>();
		for(UserRole sur : list_ur){
			Integer rid = sur.getRid();
			list_rid.add(rid.toString());
		}
		return StringTools.implode(list_rid);
	}	
	
	/**
	 * 根据用户ID获取角色ID列表
	 * @param uid 用户ID
	 * @return
	 * @throws Exception
	 */
	/*public static List<String> findByUid(String uid) {
		String sql = "select * from system_user_role where uid= ? ";
		List<UserRole> list_ur = dao.find(sql, uid);
		List<String> list_rid = new ArrayList<String>();
		for(UserRole sur : list_ur){
			Integer rid = sur.getRid();
			list_rid.add(rid.toString());
		}
		return list_rid;
	}*/

	
	
	/**
	 * 根据用户ID获取角色ID列表
	 * @param uid 用户ID
	 * @return
	 * @throws Exception
	 */
	public static List<UserRole> findUserRoles(int uid) throws Exception {
		String sql = "SELECT u_r.*, r.name, r.description FROM system_user_role u_r LEFT JOIN system_role r ON u_r.rid = r.rid where u_r.uid= ? order by rid";
		List<UserRole> list_user_roles = dao.find(sql, uid);
		return list_user_roles;
	}	
	
	/**
	 * 根据角色ID获取用户ID列表
	 * @param rid 角色ID
	 * @return
	 * @throws Exception
	 */
	public static List<Integer> findByRid(int rid) throws Exception {
		String sql = "select * from system_user_role where rid=? order by uid";
		List<UserRole> list_ur = dao.find(sql, rid);
		List<Integer> list_uid = new ArrayList<Integer>();
		for(UserRole sur : list_ur){
			Integer uid = sur.getUid();
			list_uid.add(uid);
		}
		return list_uid;
	}
	
	/**
	 * 根据用户ID和角色ID判断该用户是否具有该角色
	 * @param rid 角色ID
	 * @param uid 用户ID
	 * @return
	 * @throws Exception
	 */
	public static boolean has_role(int rid, int uid){
		UserRole userRole = dao.findById(rid, uid);
		if(userRole != null){
			return true;
		}else{
			return false;
		}
	} 

	/**
	 * 根据用户ID和角色名称判断该用户是否具有该角色
	 * @param role_name 角色名称
	 * @param uid 用户ID
	 * @return
	 * @throws Exception
	 */
	public static boolean has_role(String role_name, int uid){
		//first find role id
		/*String sql = "select * from system_role where name=?";
		List<SystemRole> list_r = dao.find(sql, role_name);
		if(list_r.size() > 0){*/
		Role role = RoleModel.findByName(role_name);
		if(role != null && role.getRid() != null){
			int rid = role.getRid();
			return has_role(rid, uid);
		}
		//then find if the people has the role 
		return false;
	}
	
	
	/**
	 * 根据用户ID和角色ID数组更新该用户的角色
	 * @param uid
	 * @param roleIds
	 * @throws Exception
	 */
	public static void rolesUpdate(int uid, String[] roleIds) throws Exception {
		//删除原有角色
		String sql_del = "delete from system_user_role where uid = ?";
		Db.update(sql_del, uid);
		/*List<Integer> list_rid = findByUid(uid);
		if(list_rid.size() > 0){
			for(int rid : list_rid){
				dao.deleteById(rid, uid);
			}
		}*/
		
		//更新新角色
		UserRole userRole;
		if(roleIds != null && roleIds.length > 0){
			for(String roleId : roleIds){
				userRole = new UserRole();
				int rid = Integer.valueOf(roleId);
				userRole.setUid(uid);
				userRole.setRid(rid);
				userRole.save();
			}
		}
	}
}
