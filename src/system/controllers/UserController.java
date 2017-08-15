package system.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import app.dao.CrmStaff;
import app.models.crm.StaffModel;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import system.core.BaseController;
import system.core.LoginInterceptor;
import system.dao.Role;
import system.dao.User;
import system.models.RoleModel;
import system.models.SessionModel;
import system.models.UserModel;
import system.models.UserRoleModel;
import system.tools.DateTools;


public class UserController extends BaseController{
	
	/**
	 * 跳转到用户列表页
	 * @throws Exception
	 */
	public void admin() throws Exception{

		int uid = check_user_login(true);
		//Console.log(uid);

		if(uid <= 0){
			//user_login(2);// test mode only
		}//用户未登录
		
		//init_page();//初始化页面
		set_head_title("用户管理");
		render("user_list.html");
		
	}
	public void listAll() throws Exception{
		//显示分页
		//String keyword = getPara("keyword","0");
		//String field = getPara("field");
		//查询条件
		String name = getPara("username", "").trim();
		Map<String, Object> list_user_obj = UserModel.find_users(page, rows, name);
		
		List<User> list_user = (List<User>) list_user_obj.get("rows");
		int total = (int) list_user_obj.get("total");
		
		List<Role> list_role = RoleModel.toList();
		
		//分别查询每个用户的角色名称，todo重构
		for(User user : list_user){
			int uid = user.getUid();
			List<Integer> list_rid = UserRoleModel.findByUid(uid);
			String roleNames = "";
			for(int i = 0; i < list_rid.size(); i++){
				for(Role role : list_role){
					if(list_rid.get(i).equals(role.getRid())){
						roleNames += role.getDescription();
						if(i < (list_rid.size()-1))
							roleNames += ",";
						break;
					}
				}				
			}
			user.put("roleName", roleNames);
		}

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("total", total);
		jsonMap.put("rows", list_user);
		String result = JsonKit.toJson(jsonMap);
		renderJson(result);
	}
	
	public void details() throws Exception {
		Integer uid = getParaToInt("uid");
		User user = new User();
		List<Integer> list_rid = new ArrayList<Integer>();
		CrmStaff cs = new CrmStaff();
		if(uid != null){
			user = UserModel.detailsLoad(uid);
			list_rid = UserRoleModel.findByUid(uid);
			cs = StaffModel.get_staff_by_uid(uid);
		}
		setAttr("systemUser", user);
		setAttr("list_rid", list_rid);
		setAttr("staff", cs);
		//加载角色列表
		List<Role> list_role = RoleModel.toList();
		setAttr("list_role", list_role);
	}
	
	/**
	 * 用户修改
	 */
	public void update() throws Exception {
		//通过反射得到对象
		User user = getModel(User.class, "systemUser");
		/*if(ml == null)
		{
			renderError(404);
		}
		if(StrKit.notBlank(ml.getUid().toString())){
			ml.setPass(StringTools.mpMD5(ml.getPass()));
			if(UserModel.detailsUpdate(ml)){
				renderText("保存成功！");
			}else{
				renderText("保存失败！");
			}
		}*/
		String message = "保存成功！";
		boolean success = false;
		
		//检查用户名是否重复
		boolean duplicated = UserModel.check_username_duplicated(user.getName());
		
		if(duplicated){
			message = "用户名已存在，请检查后重新输入.";
			render_error_message(message);
			return;
		}
		
		//处理密码		
		String pass = user.getPass();//用户输入的密码
		if(StrKit.notBlank(pass)){
			String stored_hash = DigestUtils.md5Hex(pass);
			user.setPass(stored_hash);
		}
		
		if(user.getUid() != null){
			message = "更新成功！";
			user.setUpdateTime(DateTools.getTime());
			success = user.update();
		}else{
			user.setCreated(DateTools.getTime());
			success = user.save();
		}
		
		//处理角色
		String[] roleIds = getParaValues("systemUsersRoles.rid");
		if(user.getUid() != null){
			UserRoleModel.rolesUpdate(user.getUid(), roleIds);			
		}
		if(success){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	/**
	 * 用户删除
	 */
	public void delete() throws Exception{
		//获取用户id
		/*String mlid = getPara("uid");
	    String[] str= mlid.split(",");
		System.out.println(mlid);
		if(StrKit.notBlank(mlid)){
			boolean delete=false;
			for(int i=0;i<str.length;i++)
			{
			   delete = UserModel.deleteUser(str[i]);
			}
			if(true==delete)
			{
				renderText("删除成功！");
			}else
			{
				renderText("删除失败！");
			}
		}else{
			renderError(404);
		}*/
		String checked_ids = getPara("checked_ids");
		String message = "删除成功！";
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(String id : delete_ids){
				UserModel.dao.deleteById(id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;
		}
		render_success_message(message);
	}
	
	public void pass_edit() throws Exception {
		int uid = getParaToInt("uid");
		//User user = UserModel.dao.findById(uid);
		setAttr("uid", uid);
	}
	
	public void pass_update() throws Exception {
		String message = "修改成功！";
		int uid = getParaToInt("uid", 0);
		String pass = getPara("pass");
		String pass_new = getPara("passNew");
		String pass_repeat = getPara("passRepeat");
		
		if(/*StrKit.isBlank(pass) || */StrKit.isBlank(pass_new) || StrKit.isBlank(pass_repeat)){
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
		
		/*String user_pass = user.getPass();
		String pass_hash = DigestUtils.md5Hex(pass);
		if(!user_pass.equals(pass_hash)){
			message = "修改失败，密码输入错误";
			render_error_message(message);
			return;
		}*/
		
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
	
	/**
	 * autor:zzm
	 * 删除头像
	 * @throws Exception
	 */
	public void deletePictrue() throws Exception{
		User users = current_user();
		if(users != null){
		  String path =  PathKit.getWebRootPath()+users.getPicture();
		  File file =new File(path);
		  if(file.exists()){
			  if(file.delete()){
				  users.setPicture("");
				  users.update();
				  renderJson(Arrays.asList("成功"));
			  } 
		  }else{
			  renderJson("删除失败");
		  }
		  //users.setPicture("");
		}else{
		 renderError(404);
		}
	}
	//重置密码
	/**
	 * autor:钟志苗
	 * @throws Exception
	 */
	//@Clear(LoginInterceptor.class)
	@Before(POST.class)
	public void resetPw() throws Exception{
		String luotest = getPara("luotest_response");
		String phone = getPara("Phone");
		if(StrKit.isBlank(luotest)){
		   renderText("非法请求");
		   return;
		}
		Record record = Db.findFirst("select uid from person where mobile =?",phone);
		if(record == null){
			renderText("暂无此用户");
		}else{
			//随机生成6位密码
			StringBuffer sb = new StringBuffer();
			Random rd =new Random();
			for(int i=0;i<6;i++){
				sb.append(rd.nextInt(10));
			}
			//String stored_hash = StringTools.mpMD5(sb.toString());
			String stored_hash = UserModel.generate_pass(sb.toString());
			int insertNum = Db.update("update system_user set pass =? where uid =?",stored_hash,record.getInt("uid"));
			if(insertNum>0){
			  /**
			   * 短信接口
			   */
/*				if(Sms.sendSms(phone, "新密码为"+sb.toString()+",登陆后，前往个人中心修改密码。"))
			        renderText("success");
				else
					renderText("发送失败");*/
			}else{
				renderText("系统错误");	
			}
		}
		//System.out.println(phone);
	}
	@Clear(LoginInterceptor.class)
	public void login() throws Exception{
		//user_login(1);
		if(current_user_id() > 0){
			//已经登录的用户来到当前页面
			my_redirect("me");
			
			return;
		}
		
		String username =  getPara("username", "");
		String password =  getPara("password", "");
		
		String remember_me = getPara("remember_me", "0");
		
		String redirect_url =  getPara("redirect_url");
		
		String error = "";

		if(StrKit.notBlank(username) && StrKit.notBlank(password)){

			int uid = UserModel.user_check_password(username, password);
			
			//todo check 验证码
			//validateCaptcha("captcha");
			if(uid > 0){
				//密码验证成功，验证用户status
				if(UserModel.check_user_status(uid)){				
					//写入session
					user_login(uid);
					if(remember_me.equals("1")){
						//下次自动登录
						String sid =  getSession().getId();
						String token = DigestUtils.md5Hex(uid + "-" + System.currentTimeMillis());
						String ip = get_real_ip();
						setCookie("token", token,  60*60*24*7);
						SessionModel.update_user_tokens(uid, sid, token, ip);
					}
					//页面跳转
					if(StrKit.notBlank(redirect_url)){
						my_redirect(redirect_url);
					}else{
						my_redirect("dashboard");
					}
					return;
				}else{
					error = "此账户已禁用，请联系管理员";
				
				}
			}else{
				error = "用户名或密码不正确";
			}
		}else{
			//error = "用户名或密码不能为空";
		}
		
		setAttr("redirect_url", redirect_url);
		setAttr("error", error);
		
	}
	
	@Clear(LoginInterceptor.class)
	public void logout(){
		user_logout();
		my_redirect("user/login");
	}
	
	public void show(){
		//redirect("/user/login");
		int uid = check_user_login(true);
		//Console.log(uid);
		User user = UserModel.user_load_by_uid(uid);
		
		setAttr("user", user);
		if(uid > 0){
			render("show.jsp");
		}
	}
	
	public void captcha(){
		renderCaptcha();
	}
    
	
	/*public void headshotUpdate() throws Exception {
		String headshot = getPara("headshot");
		if(StrKit.notBlank(headshot)){
			SystemUsers user = current_user();
			user.setPicture(headshot);
			boolean succeed = user.update();
			if(succeed){
				renderText("保存成功！");
			}else{
				renderText("保存失败！");
			}
		}else{
			renderError(404);
		}
	}*/
	
	public void changePassword() throws Exception {
		String currentPass = getPara("currentPassword");
		String newPass = getPara("newPassword");
		String repeatPass = getPara("repeatPassword");
		if(StrKit.notBlank(currentPass) && StrKit.notBlank(newPass) && StrKit.notBlank(repeatPass)){
			User user = current_user();
			boolean succeed = false;
			if(!currentPass.equals(user.getPass())){
				//setFlashdata("密码输入错误，请重试！");
			}else if(!newPass.equals(repeatPass)){
				//setFlashdata("新密码输入不一致，请重新确认！");
			}else{
				//user.setPass(StringTools.mpMD5(newPass)); 加密后暂时不用
				user.setPass(newPass);
				succeed = user.update();
				if(succeed){
					//setFlashdata("密码修改成功！");
				}else{
					//setFlashdata("密码修改失败！");
				}
			}
			
			my_redirect("me/profile#tab_1_3");
			return;
		}else{
			renderError(404);
		}
		//render("profile.html");
	}
}
