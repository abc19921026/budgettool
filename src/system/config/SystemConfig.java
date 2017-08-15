package system.config;
import system.controllers.DevelController;
import system.controllers.FileController;
import system.controllers.ImageController;
import system.controllers.IndexController;
import system.controllers.MenuController;
import system.controllers.PermissionController;
import system.controllers.RoleController;
import system.controllers.RoleMenuController;
import system.controllers.RolePermissionController;
import system.controllers.SmsController;
import system.controllers.SmsTemplateController;
import system.controllers.TodoController;
import system.controllers.UserController;
import system.controllers.UserProfileController;
import system.core.AuthInterceptor;
import system.core.BaseInterceptor;
import system.core.LoginInterceptor;
import system.dao._MappingKit;
import system.handler.ContextPathHandler;
import system.models.VariableModel;
import app.routes.AppRoutes;
import freemarker.template.TemplateModelException;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import java.util.Locale;

public class SystemConfig extends JFinalConfig{

	@Override
	public void configConstant(Constants me) {
		// TODO Auto-generated method stub
		PropKit.use("config.properties");
		me.setDevMode(PropKit.getBoolean("devMode"));
		//me.setViewType(ViewType.JSP);
		//设置页面路径
		me.setBaseViewPath("/views");
		//me.setUrlParaSeparator(urlParaSeparator);
		
		//FreeMarkerRender.setProperty("classic_compatible", "true");////如果为null则转为空值,不需要再用!处理
		
		//me.setJsonFactory(new JacksonFactory());
		
		FreeMarkerRender.setProperty("template_update_delay", "0");//模板缓存时间

		String error404View = "/views/system/error/404.html";
		me.setError404View(error404View);
		
		String error500View = "/views/system/error/500.html";
		me.setError500View(error500View);
		
		String error403View = "/views/system/error/403.html";
		me.setError403View(error403View);
		
		//设置上传路径，tomcat部署环境下，上传文件目录默认为项目同级的upload_files文件夹
		//String baseUploadPath = "../upload_files";
		String baseUploadPath = PropKit.get("baseUploadPath", "../upload_files");
		me.setBaseUploadPath(baseUploadPath);
		me.setBaseDownloadPath(baseUploadPath);
		
	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub

		//me.add(new AppRoutes());
		
		me.add("/", IndexController.class, "/system/");

		me.add("/profile", UserProfileController.class, "/system/profile/");
		me.add("/file", FileController.class, "/system/file/");
		me.add("/image", ImageController.class);
		
		me.add("/user", UserController.class, "/system/user/");
		
		me.add("/admin/menu", MenuController.class, "/system/menu/");
		me.add("/admin/role", RoleController.class, "/system/role/");
		me.add("/admin/role_menu", RoleMenuController.class, "/system/role/");
		me.add("/admin/role_permission", RolePermissionController.class, "/system/role/");
		me.add("/admin/permission",PermissionController.class, "/system/role/");
		me.add("/sms", SmsController.class,"/system/sms/");
		me.add("/sms/template",SmsTemplateController.class, "/system/sms/");
		me.add("/devel", DevelController.class);
		me.add("/todo", TodoController.class);
		me.add(new AppRoutes());
	}
	
	@Override
	public void configPlugin(Plugins me) {
		// TODO Auto-generated method stub
		Prop p = PropKit.use("config.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		
		me.add(c3p0Plugin);
		
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		
		/*DruidPlugin dp = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		dp.addFilter(new StatFilter());
		WallFilter wall = new WallFilter();
		wall.setDbType("mysql");
		dp.addFilter(wall);
		me.add(dp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		  
		  
		arp.setCache(new EhCache());*/
		
		_MappingKit.mapping(arp);
		app.dao._MappingKit.mapping(arp);

		arp.setShowSql(p.getBoolean("devMode"));
		me.add(arp);
		//配置缓存插件
		me.add(new EhCachePlugin());
		
		//me.add(new Cron4jPlugin(PropKit.use("task.properties")));
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
		//用户登录
		me.addGlobalActionInterceptor(new LoginInterceptor());
		//处理用户权限，用户信息，flash data等
		me.addGlobalActionInterceptor(new AuthInterceptor());		
		//处理菜单，分页变量等
		me.addGlobalActionInterceptor(new BaseInterceptor());

	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub
		me.add(new ContextPathHandler("BASE_PATH"));
		//me.add(new BaseHandler());
	}
	
	public void afterJFinalStart() {
	    //FreeMarkerRender.getConfiguration().setSharedVariable("title, "标题值在此");
		String site_name = VariableModel.find_site_name();
		//System.out.println(site_name);
		try {
			FreeMarkerRender.getConfiguration().setSharedVariable("site_name", site_name);
			FreeMarkerRender.getConfiguration().setLocale(Locale.CHINA);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
	}	
}
