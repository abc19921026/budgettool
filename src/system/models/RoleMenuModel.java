package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import system.dao.RoleMenuLink;
import system.dao.MenuLink;
import system.dao.Role;
import system.dao.RoleMenuLink;

public class RoleMenuModel extends RoleMenuLink {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1888221367640762173L;

	/**
	 * 查询并返回json字符串
	 * @return
	 * @throws Exception
	 */
	public static String toResult() throws Exception {
		String result = "";
		//查询所有角色
		String sql_role = "select * from system_role";
		List<Role> list_role =Role.dao.find(sql_role);
		//查询顶级菜单
		String sql_top = "select * from system_menu_link where plid=0 order by weight";
		List<MenuLink> list_top = MenuLink.dao.find(sql_top);
		//将所有下级菜单放在children属性
		for(MenuLink menu_top : list_top){
			menu_top = putChildren(menu_top, list_role, "");
		}
		//返回json字符串
		/*Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("roles", list_role);
		dataMap.put("menus", list_top);
		result = JsonKit.toJson(dataMap);*/
		result = JsonKit.toJson(list_top);
		return result;
	}
	
	
	/**
	 * 把下级菜单放到上级菜单的children属性，并查询role_menu表返回分配情况
	 * @param menu_link
	 * @return
	 * @throws Exception
	 */
	public static MenuLink putChildren(MenuLink menu_link, List<Role> list_role, String group_roles) throws Exception {
		int mlid = menu_link.getId();
		//根据has_children属性判断是否含有下级菜单
		int has_children = menu_link.getHasChildren();
		if(has_children == 1){
			//String sql_children = "select * from system_menu_links where menu_name='default' and plid=? order by weight";
			String sql_children = "select * from system_menu_link where plid=? order by weight";
			List<MenuLink> list_children = MenuLink.dao.find(sql_children, mlid);
			//循环处理下级菜单
			for(MenuLink menu_children : list_children){
				menu_children = putChildren(menu_children, list_role, group_roles);
			}
			menu_link.put("children", list_children);
		}
		
		List<RoleMenuLink> list_rm = new ArrayList<RoleMenuLink>();
		//不分组查询
		if(StrKit.isBlank(group_roles)){
			//查询role_menu表
			String sql_rm = "select * from system_role_menu_link where mlid=?";
			list_rm = dao.find(sql_rm, mlid);
			//menu_link.put("roleMenu", list_rm);
		//分组查询
		}else{
			//查询role_menu表，按分组筛选
			String sql_rm_grouped = "select * from system_role_menu_link where mlid=? and rid in (" + group_roles + ")";
			list_rm = dao.find(sql_rm_grouped, mlid);
		}
		
		Map<Integer, Integer> role_checked = new HashMap<Integer, Integer>();

		RoleMenuLink role_menu = null;
		for(Role role : list_role){
			menu_link.put(role.getName(), role.getRid());
			role_menu = new RoleMenuLink();
			role_menu.setMlid(mlid);
			role_menu.setRid(role.getRid());
			if(list_rm.contains(role_menu)){
				role_checked.put(role.getRid(), 1);
			}else{
				role_checked.put(role.getRid(), 0);
			}
		}
		menu_link.put("checkedRoles", role_checked);
		return menu_link;
	}

	public static String toGroupResult(int gid) throws Exception {
		String result = "";
		String sql_role_group = "select * from system_role where `group` = ?";
		List<Role> list_group_role = Role.dao.find(sql_role_group, gid);
		
		String group_roles = "";
		String s = "";
		for(Role role : list_group_role){
			int rid = role.getRid();
			group_roles += s + rid;
			s = ",";
		}

		//查询顶级菜单
		String sql_top = "select * from system_menu_link where plid=0 order by weight";
		List<MenuLink> list_top = MenuLink.dao.find(sql_top);
		//将所有下级菜单放在children属性
		for(MenuLink menu_top : list_top){
			menu_top = putChildren(menu_top, list_group_role, group_roles);
		}
		
		result = JsonKit.toJson(list_top);
		return result;
	}
	
	/**
	 * 清空systeem_role_menu_link表
	 * @throws Exception
	 */
	public static void clearRoleMenu() throws Exception {		
		String sql_truncate = "truncate table system_role_menu_link";
		Db.update(sql_truncate);
	}
	
	public static void clearRoleMenuByRid(String rids) throws Exception {
		String sql_delete = "delete from system_role_menu_link where rid in(" + rids + ")";
		Db.update(sql_delete);
	}
}
