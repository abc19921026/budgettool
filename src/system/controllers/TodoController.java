package system.controllers;


import system.core.BaseController;
import system.core.BaseModel;
import system.dao.Todo;
import system.models.TodoModel;

public class TodoController extends BaseController{
	
	/**
	 * 更新待办事宜的状态
	 * @throws Exception
	 */
	public void update_status() throws Exception{
		boolean flag=false;
		
		String message="";
		int id=getParaToInt("id");
		int status=getParaToInt("status");
		Todo todo=Todo.dao.findById(id);
		todo.setStatus(status);
		BaseModel.setUpdateTime(todo);
		int uid=current_user_id();
		todo.setUpdateUid(uid);
		flag=todo.update();
		
		if(status==TodoModel.status_hasdao){
			message="设为已办成功";
		}
		else if(status==TodoModel.status_delete){
			message="删除成功";
		}
		
		if(flag){
			render_success_message(message);
		}
		else{
			render_error_message("网络错误，请重新尝试");
		}
	}
}
