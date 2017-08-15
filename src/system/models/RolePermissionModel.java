package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import system.dao.Permission;
import system.dao.Role;
import system.dao.RolePermission;

public class RolePermissionModel extends RolePermission{

	/**
	 * 
	 */
	private static final long serialVersionUID = -29363746343492567L;

	public static boolean check_role_permission(String rids, String perm){
		boolean re = false;
		//int pid = 
		String sql = "select * from system_role_permission rp"
				+ " left join system_permission p on rp.pid = p.id"
				+ " WHERE FIND_IN_SET(rp.rid, ?) AND p.name= ?";
		RolePermission rp = dao.findFirst(sql, rids, perm);
		if(rp != null){
			re = true;
		}
		return re;
	}
	
	public static String toResult(String permissionTitle, Integer gid) throws Exception {
		String result = "";
		List<Role> list_role = null;
		if(gid == null)
			list_role = RoleModel.toList();
		else
			list_role = RoleModel.toListByGroup(gid);
		//List<Permission> list_p = PermissionModel.findPermissionByTitle(permissionTitle);
		List<Permission> list_p = PermissionModel.get_permission_tree(permissionTitle);
 		List<RolePermission> list_rp = toList();
		
		for(Permission p : list_p){
			List<Permission> list_children = p.get("children");
			if(list_children == null)
				continue;
			
			for(Permission children : list_children){
				Map<Integer, Integer> role_checked = new HashMap<Integer, Integer>();
				RolePermission rp = null;
				for(Role role : list_role){
					children.put(role.getName(), role.getRid());
					rp = new RolePermission();
					rp.setPid(children.getId());
					rp.setRid(role.getRid());
					if(list_rp.contains(rp)){
						role_checked.put(role.getRid(), 1);
					}else{
						role_checked.put(role.getRid(), 0);
					}
				}
				children.put("checkedRoles", role_checked);
			}
		}
		result = JsonKit.toJson(list_p);
		return result;
	}
	
	public static List<RolePermission> toList() {
		String sql = "select * from system_role_permission";
		return dao.find(sql);
	}
	
	public static void clearRolePermission() {
		String sql_truncate = "truncate table system_role_permission";
		Db.update(sql_truncate);
	}
	
	public static void deleteByTitle(String permissionTitle) {
		List<Permission> list_p = PermissionModel.findPermissionByTitle(permissionTitle);
		String pids = "";
		String s = "";
		for(Permission p : list_p){
			int pid = p.getId();
			pids += s + pid;
			s = ",";
		}
		String sql_delete = "delete from system_role_permission where find_in_set(pid, ?)";
		Db.update(sql_delete, pids);
	}
	
	public static void deleteByRid(String rids) {
		if(StrKit.isBlank(rids))
			return;
		
		String sql_delete = "delete from system_role_permission where rid in(" + rids + ")";
		Db.update(sql_delete);
	}
	
	public static void deleteByBoth(String permissionTitle, String rids) {
		if(StrKit.isBlank(permissionTitle) || StrKit.isBlank(rids))
			return;
		
		List<Permission> list_p = PermissionModel.findPermissionByTitle(permissionTitle);
		String pids = "";
		String s = "";
		for(Permission p : list_p){
			int pid = p.getId();
			pids += s + pid;
			s = ",";
		}
		
		String sql_delete = "delete from system_role_permission where find_in_set(pid, ?) and rid in (" + rids + ")";
		Db.update(sql_delete, pids);
	}
}
