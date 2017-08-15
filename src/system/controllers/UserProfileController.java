package system.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import app.dao.CrmStaff;
import app.models.crm.StaffModel;
import system.aop.Permission;
import system.core.BaseController;
import system.core.LoginInterceptor;
import system.dao.Role;
import system.dao.User;
import system.dao.UserRole;
import system.models.FileModel;
import system.models.PermissionModel;
import system.models.RoleModel;
import system.models.UserModel;
import system.models.UserRoleModel;
import system.tools.DateTools;

/**
 * 个人中心操作类
 * @author 疏离
 *
 */
public class UserProfileController extends BaseController {
	/**
	 * 个人中心索引页
	 * @throws Exception
	 */
	public void index() throws Exception{
		List<Role> list_role = RoleModel.toList();
		int account_id = current_user_id();
		List<UserRole> list_account_roles = UserRoleModel.findUserRoles(account_id);
		setAttr("list_account_roles", list_account_roles);
		
		CrmStaff account_staff = StaffModel.get_staff_by_uid(account_id);
		setAttr("account_staff", account_staff);
		
		User account = UserModel.dao.findById(account_id);
		setAttr("account", account);
		
		render("profile.html");
	}
	
	/**
	 * 查看某个用户profile
	 * @throws Exception
	 */
	@Permission("system_admin_user")
	public void view() throws Exception{
		int uid = current_user_id();
		/*if(uid != 1){//仅有root有权限
			renderError(403);
			return;
		}*/
		int account_id = getParaToInt(0);
		if(account_id <= 0){
			renderError(404);
			//account_id = uid;
		}
		List<Role> list_role = RoleModel.toList();
		
		List<UserRole> list_account_roles = UserRoleModel.findUserRoles(account_id);
		setAttr("list_account_roles", list_account_roles);
		
		User account = UserModel.dao.findById(account_id);
		setAttr("account", account);
		
		CrmStaff account_staff = StaffModel.get_staff_by_uid(account_id);
		setAttr("account_staff", account_staff);
		
		render("profile.html");
	}	
	
	/**
	 * 个人中心详情页
	 * @throws Exception
	 */
	public void details() throws Exception {
		Integer uid = getParaToInt("uid");
		User user = new User();
		List<Integer> list_rid = new ArrayList<Integer>();
		if(uid != null){
			user = UserModel.detailsLoad(uid);
			list_rid = UserRoleModel.findByUid(uid);
		}
		setAttr("user", user);
		setAttr("list_rid", list_rid);
		//加载角色列表
		List<Role> list_role = RoleModel.toList();
		setAttr("list_role", list_role);
	}
	
	/**
	 * 用户修改
	 *  @throws Exception
	 */
	public void update() throws Exception {
			User account = new User();
			Integer account_uid = getParaToInt("uid");
			int uid = current_user_id();
			if(uid != account_uid){
				//当前用户与 修改账号不一致，判断权限
				boolean access = PermissionModel.has_perm("system_admin_user", uid);
				if(!access){
					renderError(403);
					return;
				}
			}
			String mail = getPara("mail");
			String name = getPara("name");
			
			int status = getParaToInt("status", 0);
			
			account.setUid(account_uid);
			account.setMail(mail);
			//account.setName(name);//用户名不让用户修改
			//检查用户名是否重复
			boolean duplicated = UserModel.check_username_duplicated(name);
			
			account.setStatus(status);
			
			String message = "更新成功.";
			boolean success = false;
			
			//if(account.getUid() != null){
				account.setUpdateTime(DateTools.getTime());
				success = account.update();
			//}
			
			//处理角色
			/*String[] roleIds = getParaValues("systemUserRole.rid");
			if(account.getUid() != null){
				UserRoleModel.rolesUpdate(account.getUid(), roleIds);			
			}*/
			if(success){
				render_success_message(message);
			}else{
				message = "操作失败，请重试";
				render_error_message(message);
			}

	}
	
	//更新头像
		public void changeAvatar() throws Exception{
			String path = getPara("path");
			String newName = current_user_id()+"_"+System.currentTimeMillis();
			List<UploadFile> uploadFile = getFiles(path);
			if(uploadFile == null || uploadFile.size()==0){
				render_error_message( "上传失败");
			}else{
				String avatar_data = getPara("avatar_data");
				Map<String, Object> map = JSON.parseObject(avatar_data, Map.class);
				Object ox = map.get("x");
				Object oy = map.get("y");
				Object ow = map.get("width");
				Object oh = map.get("height");
				Integer x = null ;
				Integer y = null ;
				Integer width = null ;
				Integer height = null ;
				Integer rotate = (Integer)map.get("rotate");
				if(ox instanceof Double){
					double sum = Double.valueOf(ox.toString());
					x = (int)sum;
				}else if(ox instanceof Integer){
					x = (int) ox;
				}else if(ox instanceof BigDecimal){
					x = ((BigDecimal) ox).intValue();
				}
				if(oy instanceof Double){
					double sum = Double.valueOf(oy.toString());
					y = (int)sum;
				}else if(oy instanceof Integer){
					y = (int) oy;
				}else if(oy instanceof BigDecimal){
					y = ((BigDecimal) oy).intValue();
				}
				if(ow instanceof Double){
					double sum = Double.valueOf(ow.toString());
					width = (int)sum;
				}else if(ow instanceof Integer){
					width = (int)ow;
				}else if(ow instanceof BigDecimal){
					width = ((BigDecimal) ow).intValue();
				}
				if(oh instanceof Double){
					double sum = Double.valueOf(oh.toString());
					height = (int)sum;
				}else if(oh instanceof Integer){
					height =  (Integer) oh;
				}else if(oh instanceof BigDecimal){
					height = ((BigDecimal) oh).intValue();
				}
				if(x<0){
					width +=x;
					if(width<=0){
						LogKit.info("the image width is zreo or smaller");
						render_error_message( "上传失败,请截取有效的图片！");
						return;
					}
					x = 0;
				}
				if(y<0){
					height +=y;
					if(height<=0){
						LogKit.info("the image height is zreo or smaller");
						render_error_message( "上传失败,请截取有效的图片！");
						return;
					}
					y = 0;
				}
				try{
					system.dao.File file = FileModel.changeAvatar(uploadFile.get(0), newName,current_user_id(),x,y,width,height,rotate);
					if(file == null){render_error_message( "上传失败");return;}
					//更新头像
					String image_path = file.getFilePath() + File.separator + file.getFileName();
					UserModel.headshotUpdate(image_path, current_user_id());
					Map<String, Object> jsonMap = new HashMap<String, Object>();
					jsonMap.put("status", 200);
					jsonMap.put("message", "保存成功");
					jsonMap.put("result", image_path);
					renderJson(JsonKit.toJson(jsonMap));
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					render_error_message( "上传失败！");
				}
			}
		}
	
	/**
	 * 密码修改
	 * @throws Exception
	 */
	public void changePassword() throws Exception {
		String message = "修改成功！";
		//User user = current_user();
		int uid = getParaToInt("uid", 0);
		String pass = getPara("currentPassword");
		String pass_new = getPara("newPassword");
		String pass_repeat = getPara("repeatPassword");
		
		if(StrKit.isBlank(pass) || StrKit.isBlank(pass_new) || StrKit.isBlank(pass_repeat)){
			message = "修改失败，密码不能为空";
			render_error_message(message);
			return;
		}
		
		User user = UserModel.dao.findById(uid);
		if(user == null){
			message = "修改失败，找不到用户";
			render_error_message(message);
			return;
		}
		
		String user_pass = user.getPass();
		String pass_hash = DigestUtils.md5Hex(pass);
		if(!user_pass.equals(pass_hash)){
			message = "修改失败，密码输入错误";
			render_error_message(message);
			return;
		}
		
		if(!pass_new.equals(pass_repeat)){
			message = "修改失败，新密码必须一致";
			render_error_message(message);
			return;
		}
		
		String stored_hash = DigestUtils.md5Hex(pass_new);
		user.setPass(stored_hash);
		user.setUpdateTime(DateTools.getTime());
		boolean success = user.update();
		
		if(success){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
}
