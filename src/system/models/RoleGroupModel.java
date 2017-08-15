package system.models;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import system.dao.Role;
import system.dao.RoleGroup;

public class RoleGroupModel extends RoleGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 357225145550596674L;

	public static String toResult() {
		String result = "";
		List<Model> list = new ArrayList<Model>();
		//已分组的角色
		List<RoleGroup> list_rg = toTree();
		if(list_rg.size() > 0){
			for(RoleGroup rg : list_rg){
				int gid = rg.get("gid");
				List<Role> list_role_grouped = RoleModel.toTreeByGroup(gid);
				if(list_role_grouped.size() > 0)
					rg.put("children", list_role_grouped);
			}
		}		
		list.addAll(list_rg);

		//未分组的角色
		List<Role> list_role_no_group = RoleModel.toTreeNoGroup();
		if(list_role_no_group.size() > 0){
			Role role = new Role();
			role.put("id", "ungrouped");
			role.setName("系统默认");
			role.put("children", list_role_no_group);
			list.add(role);
		}
		
		result = JsonKit.toJson(list);
		return result;
	}
	
	public static List<RoleGroup> toTree() {
		return dao.find("select id as gid, concat('group_',id) as id, name, weight from system_role_group order by weight,id");
	}
	
	public static List<RoleGroup> toJsonList() {
		return dao.find("select id as value, name as text from system_role_group order by weight,id");
	}
	
	public static int ungroupRole(int gid) {
		return Db.update("update system_role set `group` = null where `group` = ?", gid);
	}
}
