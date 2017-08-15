package system.controllers;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import system.core.BaseController;
import system.core.BaseModel;
import system.dao.SmsTemplate;
import system.models.SmsTemplateModel;

public class SmsTemplateController extends BaseController{
	/**
	 * 跳转到模版列表页面
	 * @throws Exception
	 */
	public void index() throws Exception{
		set_head_title("短信模版");
		render("sms_template_list.html");
	}
	
	/**
	 * 获取短信模版列表的数据
	 * @throws Exception
	 */
	public void json_template_list() throws Exception{
		String content=getPara("content");
		Integer staff_id=getParaToInt("staff_id");
		Integer type=getParaToInt("type",-1);//默认是全部
		renderJson(JsonKit.toJson(SmsTemplateModel.json_template_list(page, rows,content,staff_id,type)));
	}
	
	/**
	 * 短信模版编辑页面
	 * @throws Exception
	 */
	public void sms_template_edit() throws Exception{
		int id=getParaToInt(0,0);
		SmsTemplate st=null;
		if(id>0){
			st=SmsTemplate.dao.findById(id);
		}
		setAttr("st", st);
	}
	
	/**
	 * 新增或者编辑
	 * @throws Exception
	 */
	public void sms_template_update() throws Exception{
		SmsTemplate st=getModel(SmsTemplate.class,"st");
		String message="";
		boolean flag=false;
		int uid=current_user_id();
		st.setUid(uid);
		if(st.getId()!=null&&!"".equals(st.getId())){//编辑
			flag=st.update();
			message="更新成功!";
		}
		else{
			st.setCreateUid(uid);
			BaseModel.setCreateTime(st);
			flag=st.save();
			message="添加成功!";
		}
		
		if(flag){
			render_success_message(message);
		}
		else{
			render_error_message("网络错误，请重新操作!");
		}
	}
	
	/**
	 * 删除模版
	 * @throws Exception
	 */
	public void sms_template_delete() throws Exception{
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS",message = "删除成功";
		if(StrKit.notBlank(checked_ids)){//
			String[] delete_ids = checked_ids.split(",");
			for(int i =0; i < delete_ids.length; i++){
				String id = delete_ids[i];
				SmsTemplate.dao.deleteById(id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status,message);
	}
}
