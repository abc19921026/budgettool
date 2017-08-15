package system.controllers;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import system.core.BaseController;
import system.dao.Role;
import system.dao.RoleGroup;
import system.dao.User;
import system.models.RoleGroupModel;
import system.models.RoleModel;

public class RoleController extends BaseController {

	public void index() {
		//得到用户uid,让root权限用户可以新建、删除
		User user = current_user();
		int uid = user.getUid();
		
		set_head_title("角色管理");
		setAttr("uid", uid);
		
		render("role_list.html");
	}
	
	public void listAll() throws Exception {
		/*page = getParaToInt("page");
		rows = getParaToInt("rows");
		if (page == null) {
			page = 1;
		}
		if (rows == null) {
			rows = 50;
		}*/
		String result = RoleModel.toResult();
		renderJson(result);
	}
	
	public void listGroup() throws Exception {
		String result = RoleGroupModel.toResult();
		renderJson(result);
	}
	
	public void details() {
		Integer rid = getParaToInt("rid");
		Integer gid = getParaToInt("gid");
		
		Role role = null;
		if(rid != null){
			role = Role.dao.findById(rid);
		}else{
			role = new Role();
		}
		if(gid != null)
			role.setGroup(gid);
		
		setAttr("systemRole", role);
	}
	
	public void update(){
		String message = "保存成功";
		Role role = getModel(Role.class,"systemRole");
		if(role == null){
			renderError(404);
		}

		//未分组的角色group为null
		//if(role.getGroup() != null && role.getGroup() == 0)
			//role.setGroup(null);
		boolean succeed = false;
		if(role.getRid() != null){
			message = "更新成功";
			succeed = role.update();
		}else{
			succeed = role.save();
		}

		if(succeed){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
	public void group_edit(){
		int id = getParaToInt("id", 0);
		RoleGroup rg = RoleGroup.dao.findById(id);
		if(rg == null)
			rg = new RoleGroup();
		
		setAttr("roleGroup", rg);
	}
	
	public void group_update(){
		RoleGroup rg = getModel(RoleGroup.class);
		if(rg == null)
			renderError(404);
		
		String message = "保存成功";
		boolean succeed = false;
		if(rg.getId() != null){
			message = "更新成功";
			succeed = rg.update();
		}else
			succeed = rg.save();
		
		if(succeed)
			render_success_message(message);
		else
			render_error_message("操作失败，请重试");
	}
	
	public void group_json(){
		List<RoleGroup> list_rg = new ArrayList<RoleGroup>(); 
		RoleGroup default_rg = new RoleGroup();
		default_rg.put("value",0);
		default_rg.put("text", "无");
		list_rg.add(default_rg);
		list_rg.addAll(RoleGroupModel.toJsonList());
		
		renderJson(list_rg);
	}
	
	public void group_delete(){
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS",message = "删除成功";
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(String group_id : delete_ids){
				if(group_id.indexOf("group_") > -1)
					group_id = group_id.replace("group_", "");
				//删除分组内的角色的group字段（不删除角色）
				RoleGroupModel.ungroupRole(Integer.valueOf(group_id));
				//删除分组
				RoleGroup.dao.deleteById(group_id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status,message);
	}
	
	public void delete(){
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS",message = "删除成功";
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i =0; i < delete_ids.length; i++){
				String id = delete_ids[i];
				if(id.indexOf("role_") > -1)
					id = id.replace("role_", "");
				//删除role permission
				String sql = "delete from system_role_permission where rid = ?";
				Db.update(sql, id);
				
				//删除user role
				sql = "delete from system_user_role where rid = ?";
				Db.update(sql, id);
				
				//删除 role menu link
				sql = "delete from system_role_menu_link where rid = ?";
				Db.update(sql, id);
				
				//最后删除role
				Role.dao.deleteById(id);
			}
		}else {
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status,message);
	}
	
	
}
