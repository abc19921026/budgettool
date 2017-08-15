package system.core;

import javax.servlet.http.HttpServletRequest;

import system.models.MenuModel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class BaseInterceptor  implements Interceptor{

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub

		
		Controller c = inv.getController();
		

		//加载菜单
		HttpServletRequest request = c.getRequest();
		
		//// Will point to http://www.example.com/subDirectory
		String base_url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath();
		String BASE_PATH = request.getContextPath() + "/"; // /subDirectory/ or /
		
		String request_uri = request.getRequestURI();// /subDirectory/admin/menu
		String current_path = request_uri.substring(BASE_PATH.length()); // admin/menu 不包含 query string

		//如果是root，加载全部菜单项
		String system_menu_links = "";
		Integer uid = c.getSessionAttr("uid");
		if(uid == null){
			uid = 0;
		}
		//if(uid == 1){
			//root
			//system_menu_links = MenuModel.load_all(current_path, BASE_PATH);
			//system_menu_links = MenuModel.load_by_user_and_cache(current_path, BASE_PATH, uid);
		//}else{
			//anonymous user or any other authenticated user
			//system_menu_links = MenuModel.load_by_user(current_path, BASE_PATH, uid);
			system_menu_links = MenuModel.load_by_user_and_cache(current_path, BASE_PATH, uid);
		//}
		c.setAttr("system_menu_links", system_menu_links);
/*		int rows = c.getParaToInt("rows", 10);
		c.setAttr("rows", rows);*/
		
		//BaseController controller = (BaseController) ai.getController();
		  
		Class<?> controllerClass = c.getClass();
		Class<?> superControllerClass = controllerClass.getSuperclass();
		
		//Class<?> basecontrollerClass;
		try{
			//预处理分页变量
			Method method = superControllerClass.getMethod("init_pager");  
			method.invoke(c);
		}catch(Exception e){
			e.printStackTrace();
		}

		//Field[] fields = controllerClass.getDeclaredFields();
		//String name = controllerClass.getName();

		inv.invoke();
	}

}
