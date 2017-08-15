package system.controllers;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jfinal.kit.StrKit;

import system.core.BaseController;
import system.dao.Role;
import system.dao.RoleGroup;
import system.dao.RolePermission;
import system.models.RoleModel;
import system.models.RolePermissionModel;

public class RolePermissionController extends BaseController {

	public void index() throws Exception {
		List<Role> list_role = RoleModel.toList();
		setAttr("list_role", list_role);
		
		set_head_title("角色权限");
		render("role_permission.html");
	}
	
	public void listAll() throws Exception {
		String permissionTitle = getPara("permissionTitle", "").trim();
		Integer gid = getParaToInt("gid");
		String result = RolePermissionModel.toResult(permissionTitle, gid);
		renderJson(result);
	}

	public void update() throws Exception {
		String message = "保存成功！";
		//获取参数
		String rm_json = getPara("rolePermission");
		String permissionTitle = getPara("permissionTitle").trim();
		String group_roles = getPara("group_roles");
		
		boolean succeed = false;
		if(StrKit.notBlank(rm_json)){
			if(StrKit.isBlank(permissionTitle) && StrKit.isBlank(group_roles))
				RolePermissionModel.clearRolePermission();
			else if(StrKit.notBlank(permissionTitle) && StrKit.notBlank(group_roles))
				RolePermissionModel.deleteByBoth(permissionTitle, group_roles);
			else if(StrKit.notBlank(permissionTitle))
				RolePermissionModel.deleteByTitle(permissionTitle);
			else if(StrKit.notBlank(group_roles))
				RolePermissionModel.deleteByRid(group_roles);
			
			/*//先清空systeem_role_permission表
			if(StrKit.isBlank(permissionTitle))
				RolePermissionModel.clearRolePermission();
			//如果有查询条件，按照查询条件删除数据
			else
				RolePermissionModel.deleteByTitle(permissionTitle);*/
			
			//将字符串转换成json数组
			JSONArray rp_array = new JSONArray(rm_json);
			int array_length = rp_array.length();
			int success = 0;
			RolePermission rm = new RolePermission();
			
			//循环数组保存数据
			for(int i = 0; i < rp_array.length(); i++){
				JSONObject rm_object = rp_array.getJSONObject(i);

				rm.setRid(rm_object.getInt("rid"));
				rm.setPid(rm_object.getInt("id"));
				//插入数据库
				if(rm.save())
					success++;
			}
			//保存结果
			succeed = array_length == success;	

		}
		if(succeed){
			render_success_message(message);
		}else{
			message = "保存失败！";
			render_success_message(message);
		}		
	}
	
	public void group_permission(){
		Integer gid = getParaToInt("gid");
		
		if(gid != null){
			RoleGroup roleGroup = RoleGroup.dao.findById(gid);
			setAttr("roleGroup", roleGroup);
			
			List<Role> list_role = RoleModel.toListByGroup(gid);
			setAttr("list_role", list_role);
			String group_roles = "";
			String s = "";
			for(Role role : list_role){
				int rid = role.getRid();
				group_roles += s + rid;
				s = ",";
			}
			setAttr("group_roles", group_roles);
		}
				
		set_head_title("分组权限");
		render("group_permission.html");
	}
	
	
}
