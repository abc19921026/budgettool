package system.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import system.dao.Role;

public class RoleModel extends Role{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5458143855471370757L;

	/**
	 * 根据角色名称查询角色
	 * @param role_name
	 * @return
	 * @throws Exception
	 */
	public static Role findByName(String role_name) {
		String sql = "select * from system_role where name = ? limit 1";
		Role role = dao.findFirst(sql, role_name);
		return role;
	}
	
	public static String toResult() throws Exception {
		String result = "";
		List<Role> list_r = toList();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("total", list_r.size());
		jsonMap.put("rows", list_r);
		result = JsonKit.toJson(jsonMap);
		return result;
	}
	
	public static List<Role> toList() throws Exception {
		String sql = "select * from system_role";
		return dao.find(sql);
	}
	
	public static List<Role> toListByGroup(int gid) {
		return dao.find("select * from system_role where `group` = ?", gid);
	}
	
	/**
	 * 根据分组查询角色
	 * @param gid
	 * @return
	 */
	public static List<Role> toTreeByGroup(int gid) {
		return dao.find("select concat('role_',rid) as id, description as name, concat('group_',`group`) as `group` from system_role where `group` = ? ", gid);
	}
	
	/**
	 * 查询未分组的角色
	 */
	public static List<Role> toTreeNoGroup() {
		return dao.find("select concat('role_',rid) as id, description as name, concat('group_',`group`) as `group` from system_role WHERE NOT (`group` > 0) ");
	}
}
