package app.routes;

import com.jfinal.config.Routes;

import app.controllers.crm.DepartmentController;
import app.controllers.crm.JobTitleController;
import app.controllers.crm.StaffController;
import system.controllers.MessageController;

public class AppRoutes extends Routes{

	@Override
	public void config() {
		//企业管理路由配置
		add("/crm/department",DepartmentController.class,"/app/crm/");
		add("/crm/staff",StaffController.class,"/app/crm/");
		add("/crm/job",JobTitleController.class,"/app/crm/");
		
		//系统消息配置
		add("/inbox",MessageController.class,"/system/inbox/");
	}
}
