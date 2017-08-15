package system.controllers;

import system.core.BaseController;

public class DevelController extends BaseController{
	public void switch_user() throws Exception{

		int account_id = getParaToInt(0);
		int uid = check_user_login(true);
		//Console.log(uid);

		if(uid != 1){
			//user_login(2);// test mode only
			render_error_message("You dont have enough permission to switch user!");
			return;
		}//用户未登录
		
		//init_page();//初始化页面
		if(account_id > 0){
			user_logout();
			user_login(account_id);
		}
		//返回前一页
		//goto_previous_page();
		redirect("/");
		//render_success_message("ok");
		return;
		
	}
}
