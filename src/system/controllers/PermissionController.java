package system.controllers;

import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import system.core.BaseController;
import system.dao.Permission;
import system.models.PermissionModel;
/**
 * 权限管理
 * @author 
 *
 */
@system.aop.Permission("system admin")
public class PermissionController extends BaseController {
	
	/**
	 * 权限管理索引
	 * @throws Exception
	 */
	public void index() throws Exception{
		set_head_title("权限管理");
		render("permission_list.html");
	}
	
	/**
	 * 权限管理列表加载
	 * @throws Exception
	 */
	public void json_permission_list() throws Exception{
		String title = getPara("title","").trim();
		/*Map<String, Object> re = PermissionModel.get_permission_list(rows,page,title);
		String jsonList = JsonKit.toJson(re);*/
		List<Permission> list_p = PermissionModel.get_permission_tree(title);
		
		renderJson(JsonKit.toJson(list_p));
		
	}
	
	/**
	 * 权限管理编辑
	 * @throws Exception
	 */
	public void persission_edit() throws Exception{
		int id = getParaToInt(0,0);
		Permission permission = null;
		if (id > 0) {
			permission = PermissionModel.get_permission_detail(id);
		}else {
			permission = new Permission();
		}
		setAttr("permission", permission);
	}
	
	/**
	 * 权限管理(保存)更新
	 * @throws Exception
	 */
	public void permission_update() throws Exception{
		boolean flag = false;
		String message = "保存成功";
		
		Permission re = getModel(Permission.class);
		
		if (re.getId() != null) {
			message = "更新成功";
			flag = re.update();
		}else {
			flag = re.save();
		}
		PermissionModel.update_permission_weight();//更新排序号
		if (flag) {
			render_success_message(message);
		}else {
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
	/**
	 * 权限管理删除
	 * @throws Exception
	 */
	public void permission_delete() throws Exception{
		String checkedIds = getPara("checked_ids");
		if (StrKit.notBlank(checkedIds)) {
			String[] ids = checkedIds.split(",");
			boolean succeed = false;
			for (String id : ids) {
				succeed = Permission.dao.deleteById(id);
			}
			PermissionModel.update_permission_weight();//更新排序号
			if(succeed) {
				// renderText("删除成功");
				render_success_message("删除成功");
			}else {
				render_success_message("删除失败");
			}
		} else {
			renderError(404);
		}
	}
	
}
