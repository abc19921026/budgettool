package system.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.render.JsonRender;

import app.dao.CrmStaff;
import app.models.crm.StaffModel;
import system.dao.User;
import system.dao.UserRole;
import system.models.SessionModel;
import system.models.UserModel;
import system.models.UserRoleModel;
import system.render.FileSrcRender;
import system.tools.UtilTools;


public class BaseController extends Controller{
	public String base_url; // Will point to http://www.example.com/subDirectory
	public String base_path; // / or /subDirectory/
	public String current_path; // admin/menu
	
	//public SystemUsers user;
	
	public int page;
	public int rows;
	
	//添加默认页数和每页条数，供BaseInterceptor调用
	public void init_pager(){
		try{
			this.page = getParaToInt("page", 1);
			this.rows = getParaToInt("rows", 10);
			
			setAttr("page", this.page);
			setAttr("rows", this.rows);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void access_controle_allow_origin(){
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
	}
	
	public void set_no_cache(){
		getResponse().addHeader("Pragma", "No-cache"); 
		getResponse().addHeader("Cache-Control", "no-cache");
		getResponse().addHeader("Expires", "0");
	}
	
	public void goto_previous_page(){
		String referer = getRequest().getHeader("referer");
		String uri = getRequest().getRequestURI();  //获取请求的uri
		String ctx = UtilTools.getContextPath(getRequest()); //获取网站的根路径
		//System.out.println(referer);
		if (StrKit.notBlank(referer) && referer.contains(ctx)) {
			//System.out.println(referer);
			redirect(referer);
		}
	}
	

	
/*	public SystemUsers get_user(){
		if(this.user != null){
			return user;
		}
		Integer uid = getSessionAttr("uid");
		if(uid != null && uid > 0){
			SystemUsers user = UserModel.user_load_by_uid(uid);
			
			this.user = user;

		}
		return user;
		
	}*/
	
	
	public void user_login(int uid){
		setSessionAttr("uid", uid);
		//Console.log(uid);
		User user = UserModel.user_load_by_uid(uid);
		//Console.log(user.getName());
		//this.user = user;
		setAttr("user", user);
		setAttr("uid", uid);
		
		//SessionKit.getSession()
		//todo 其他事项
	}
	
	
	public void user_logout(){

		
		String sid =  getSession().getId();
		Integer uid = getSessionAttr("uid");
		if(uid != null){
		   SessionModel.clear_user_session(uid, sid);
		}	
		setSessionAttr("uid", 0);
		//setSessionAttr("uid", 0);
		//SessionKit.getSession()
	}
	
/*	public int check_user_login(){
		return check_user_login(false);
	}*/
	
	/**
	 * 是否在登录后跳转到当前url
	 * @param is_redirect
	 * @return
	 */
	public int check_user_login(boolean is_redirect){
		HttpServletRequest request = getRequest();
		String redirect_url = "";
		String path = request.getServletPath();
		String query_string = request.getQueryString();

		if(path != null){
			//Console.log(path);
			redirect_url = path;
		}
		if(query_string != null){
			redirect_url +="?" + query_string;
		}
		Integer uid = getSessionAttr("uid");
		//Console.log(uid);
		if(uid != null && uid > 0){
			//Debug.print(uid);
		}else{
			
			if(is_redirect && !redirect_url.equals("")){
				my_redirect("user/login" + "?redirect_url=" + redirect_url);
				//return 0;
			}else{
				my_redirect("user/login");
			}
			return 0;
		}
		return uid;

	}
	
/*	public String base_path(){
		HttpServletRequest request = getRequest();

		
		String path = request.getContextPath();
		//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		return path + "/";

	}*/
	
	public User current_user(){
		int uid = getSessionAttr("uid");
		return UserModel.user_load_by_uid(uid);
	}
	
	public CrmStaff current_staff() throws Exception{
		int uid=getSessionAttr("uid");
		return StaffModel.get_staff_by_uid(uid);
	}
	
	public int current_user_id(){
		Integer uid = getSessionAttr("uid");
		if(uid == null){
			uid = 0;
		}
		return uid;
	}
	
	public List<UserRole> current_role() throws Exception{
		int uid=getSessionAttr("uid");
		return UserRoleModel.findUserRoles(uid);
	}
	
	/**
	 * Redirect to url
	 */
	public void my_redirect(String url) {
/*		HttpServletRequest request = getRequest();
		
		String basePath = request.getContextPath();
		Console.log(basePath);*/
		//Console.log(url);
		if( !url.startsWith("/")){
			//basePath += "/";
			url = "/" + url;
		}
		//Console.log(url);
		redirect(url);
	} 
	
	public void render_message(String status, String message){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", status);
		re.put("message", message);
		//String result = JsonKit.toJson(jsonMap);
		renderJson(re);
	}
	
	public void render_message(int code,String message,Map<String,Object> re){
		re.put("code", code);
		re.put("message", message);
		renderJson(re);
	}
	
	public void render_message(int code,String message){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("code", code);
		re.put("message", message);
		renderJson(re);
	}
	
	public void render_success_message(String message){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "SUCCESS");
		re.put("message", message);
		renderJson(re);
	}
	
	/**
	 * 
	 * @param message
	 * @param contentType
	 */
	public void render_success_message(String message, int content_type){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "SUCCESS");
		re.put("message", message);
		if(content_type == 1){
			// contentType text/html
			render(new JsonRender(re).forIE());
		}else{
			// contentType  application/json
			render_success_message(message);
		}
	}
	
	public void render_success_message(String message, Map<String, Object> re){
		//Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "SUCCESS");
		re.put("message", message);
		renderJson(re);
	}
	
	public void render_success_message(String message, Map<String, Object> re, int content_type){
		//Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "SUCCESS");
		re.put("message", message);
		
		if(content_type == 1){
			// contentType text/html
			render(new JsonRender(re).forIE());
		}else{
			// contentType  application/json
			renderJson(re);
		}
		
		//renderJson(re);
	}	
	
	public void render_error_message(String message, Map<String, Object> re){
		re.put("status", "ERROR");
		re.put("message", message);
		renderJson(re);
	}
	
	public void render_error_message(String message){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "ERROR");
		re.put("message", message);
		renderJson(re);
	}
	
	public void render_warning_message(String message){
		Map<String, Object> re = new HashMap<String, Object>();
		re.put("status", "WARNING");
		re.put("message", message);
		renderJson(re);
	}
	
	public void set_flash_data(String message){
		String type = "info";// 设置默认值，可选值为 info/warning/danger/success
		set_flash_data(message, type);
	}
	
	public void set_flash_data(String message, String type){
		//Map flash_data = new HashMap<String, String>();
		if(StrKit.isBlank(type)){
			type = "info";
		}
		Map<String, Object> flash_data = new HashMap<String, Object>();
		flash_data.put("message", message);
		flash_data.put("type", type);
		getSession().setAttribute("FLASH_DATA", flash_data);
	}	
	
	public void set_head_title(String head_title){
		//String system_title = getAttr("head_title");
		//System.out.println(system_title);
		//page_title += system_title;
		setAttr("head_title", head_title);
	}
	
	public String get_real_ip() {
		HttpServletRequest request = getRequest();
		//String accessIP = request.getHeader("x-forwarded-for");
        //if (null == accessIP)
            return request.getRemoteAddr();
        //return accessIP;
	}
	
	public void setFlashdata(String message){
		getSession().setAttribute("flashdata", message);
	}
	
	public void renderSrcFile(File file, String sourceName){
		render(new FileSrcRender(file, sourceName));
	}

	public void renderSrcFile(String fileName, String sourceName){
		render(new FileSrcRender(fileName, sourceName));
	}
}
