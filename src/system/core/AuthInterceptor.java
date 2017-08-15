package system.core;


import org.apache.commons.codec.digest.DigestUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.render.FreeMarkerRender;

import app.dao.CrmStaff;
import app.models.crm.StaffModel;
import system.dao.User;
import system.models.PermissionModel;
import system.models.SessionModel;
import system.models.UserModel;
import system.tools.TemplateFuncs;
import system.aop.Permission;

public class AuthInterceptor implements Interceptor {
	public String base_url; // Will point to http://www.example.com/subDirectory
	public String base_path; // / or /subDirectory/
	public String current_path; // admin/menu
	

	public Controller c;
	
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		Controller c = inv.getController();
		this.c = c;
		//Console.log(c.getSessionAttr("uid"));
		//if(c.getSessionAttr("uid") != null){
		handle_user();

		handle_flashdata();
		handle_head_title();
		handle_template_func();
		
		handle_account();//账号信息：员工信息、部门等
		//自定义函数

		
		String action_key = inv.getActionKey();

		
		Permission action_permission = inv.getMethod().getAnnotation(Permission.class);
		if(action_permission != null){
			//先判断action permission
			String action_permission_name = action_permission.value();
			handle_permission(action_permission_name);
			
		}else{
			
			//如果action permission为空再检查controller permission
			Permission controller_permission =  c.getClass().getAnnotation(Permission.class);
			if(controller_permission != null){
				String controller_permission_name = controller_permission.value();
				handle_permission(controller_permission_name);
			}
		}
		
		//System.out.println(action_key);

		inv.invoke();
	}
	
	//自定义模板函数
	public void handle_template_func(){
		//c.setAttr("has_perm", new GetUname());
		/*c.setAttr("getStatus",new GetStatus());
		c.setAttr("getPrize", new GetPrize());
		*/
		Integer uid = c.getSessionAttr("uid");
		try{
			FreeMarkerRender.getConfiguration().setSharedVariable("TF", new TemplateFuncs(uid));
		}catch (Exception e){
			
		}
    }

	
	//用户
	public void handle_user() {
		Integer uid = c.getSessionAttr("uid");

		if(uid != null && uid > 0) {
			User user = UserModel.user_load_by_uid(uid);
			//.setUser(user);
			c.setAttr("user", user);
		}else{
			//验证自动登录
			String token =  c.getCookie("token");
			if(token != null){
				//验证token合法性
				uid = SessionModel.find_user_by_token(token);
				if(uid > 0){
					//更新user session sid
					try{
						//删除原来的token
						SessionModel.delete_user_token(token);
						
						//重新生成token
						String sid =  c.getSession().getId();
						String new_token = DigestUtils.md5Hex(uid + "-" + System.currentTimeMillis());
						//String ip = c.getRequest().getRemoteAddr();
						//设置新的token
						c.setCookie("token", new_token,  60*60*24*7);//7天过期
						String hostname = c.getRequest().getRemoteAddr();
						SessionModel.insert_user_sessions(uid, sid, new_token, hostname);
						User user = UserModel.user_load_by_uid(uid);
						//.setUser(user);
						c.setAttr("user", user);
						c.setSessionAttr("uid", uid);
					}catch(Exception e){
						
					}
				}
			}
		}
				
	}
	
	public void handle_permission(String perm_name){
		//判断当前用户是否有权限访问当前action 或controller
		Integer uid = c.getSessionAttr("uid");
		Boolean access = false;
		if(uid != null){
			access = PermissionModel.has_perm(perm_name, uid);
			if(!access){
				c.renderError(403);
				return;
			}
		}else{
			//用户尚未登录
			//c.redirect("/user/login");
			c.renderError(403);
			return;
		}
		//System.out.println(access);
	}
	
	public void handle_account(){
		Integer uid = c.getSessionAttr("uid");
		try {
			if(uid!=null){
				CrmStaff staff = StaffModel.get_staff_by_uid(uid);
				c.setAttr("staff", staff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	public void handle_flashdata(){
		//String message = c.getSessionAttr("FLASH_DATA");
		Object flash_data = c.getSessionAttr("FLASH_DATA");
		c.setAttr("FLASH_DATA", flash_data);
		//c.getSession().removeAttribute(flash_data);
		c.getSession().removeAttribute("FLASH_DATA");
	}
	
	public void handle_head_title(){
		//String message = c.getSessionAttr("FLASH_DATA");
		String default_head_title = "河豚Hsms";
		//page_title += " - JIAKI";
		c.setAttr("head_title", default_head_title);

	}	

}
