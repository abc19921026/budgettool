package system.controllers;


import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import system.core.BaseController;
import system.dao.Role;
import system.dao.RoleGroup;
import system.dao.RoleMenuLink;
import system.models.MenuModel;
import system.models.RoleMenuModel;
import system.models.RoleModel;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
public class RoleMenuController extends BaseController {

	public void index() throws Exception {
		//验证登录
		//int uid = check_user_login(true);
		List<Role> list_role = RoleModel.toList();
		setAttr("list_role", list_role);
		set_head_title("角色菜单");
		render("role_menu.html");
	}
	
	public void listAll() throws Exception {
		//查询数据并返回
		String result = RoleMenuModel.toResult();
		renderJson(result);
	}
	
	public void update() throws Exception {
		String message = "保存成功！";
		//获取参数
		String rm_json = getPara("roleMenu");
		String group_roles = getPara("group_roles", "");
		boolean succeed = false;
		if(StrKit.notBlank(rm_json)){
			//先清空systeem_role_menu_link表
			if(StrKit.notBlank(group_roles))
				RoleMenuModel.clearRoleMenuByRid(group_roles);
			else
				RoleMenuModel.clearRoleMenu();
			
			//将字符串转换成json数组
			JSONArray rm_array = new JSONArray(rm_json);
			int array_length = rm_array.length();
			int success = 0;
			RoleMenuLink rm = new RoleMenuLink();
			
			//循环数组保存数据
			for(int i = 0; i < rm_array.length(); i++){
				JSONObject rm_object = rm_array.getJSONObject(i);

				rm.setRid(rm_object.getInt("rid"));
				rm.setMlid(rm_object.getInt("id"));
				//插入数据库
				if(rm.save())
					success++;
			}
			//保存结果
			succeed = array_length == success;	

		}
		if(succeed){
			//清除缓存
			CacheKit.removeAll(MenuModel.CACHE_NAME);
			render_success_message(message);
		}else{
			message = "保存失败！";
			render_success_message(message);
		}		
	}
	
	public void group_menu() throws Exception {
		int gid = getParaToInt("gid", 0);
		
		RoleGroup rg = RoleGroup.dao.findById(gid);
		if(rg == null){
			rg = new RoleGroup();
			rg.setId(0);
			rg.setName("系统默认");
		}
		setAttr("roleGroup", rg);
		//分组内的角色ID
		String group_roles = "";
		List<Role> list_group_role = RoleModel.toListByGroup(gid);
		setAttr("list_role", list_group_role);
		
		String s = "";
		for(Role role : list_group_role){
			int rid = role.getRid();
			group_roles += s + rid;
			s = ",";
		}			
		setAttr("group_roles", group_roles);
		
		set_head_title("分组菜单");
		render("group_menu.html");
	}
	
	public void listGroup() throws Exception {
		int gid = getParaToInt("gid", 0);
		String result = RoleMenuModel.toGroupResult(gid);
		renderJson(result);
	}
}
