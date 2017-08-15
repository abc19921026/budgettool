package system.tools;

import javax.servlet.http.HttpSession;

import com.jfinal.kit.StrKit;

import system.models.PermissionModel;
import system.models.RolePermissionModel;
import system.models.UserRoleModel;

public class TemplateFuncs {
	private static int uid = 0;
	/*
	 * 初始化session uid
	 */
	public TemplateFuncs(int user_id){
		uid = user_id;
	}
	
	/**
	 * 检查当前用户是否有所给的权限
	 * @param perm
	 * @return
	 */
	public boolean has_perm(String perm) {
		/*boolean re = false;	
		if(uid <= 0){
			return false;
		}
		if(uid == 1){//root用户拥有所有权限
			return true;
		}

		String rids = UserRoleModel.find_rids(uid);
		if(RolePermissionModel.check_role_permission(rids, perm)){
			re = true;
		}

		return re;*/
		return PermissionModel.has_perm(perm, uid);
	}
	
	/**
	 * 检查当前用户是否有角色
	 * @param perm
	 * @return
	 */
	public boolean has_role(String role_name) {
		boolean re = false;	
		if(StrKit.isBlank(role_name) ||  uid <= 0){
			return false;
		}
		if(uid > 0){//root用户拥有所有权限
			re = UserRoleModel.has_role(role_name, uid);
		}

		return re;
	}	
}
