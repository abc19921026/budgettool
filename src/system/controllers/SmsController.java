package system.controllers;

import java.util.List;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import system.core.BaseController;
import system.core.LoginInterceptor;
import system.dao.SmsLog;
import system.dao.SmsTemplate;
import system.models.SmsLogModel;
import system.models.SmsModel;
import system.models.SmsTemplateModel;
import system.tools.DateTools;

public class SmsController extends BaseController{
	/**
	 * 跳转到发送短信的页面
	 * @throws Exception
	 */
	public void index() throws Exception{
		int uid=current_user_id();
		List<SmsTemplate> smsTemplates=SmsTemplateModel.get_mysms_template(uid);
		List<SmsTemplate> smsTemplatesAll=SmsTemplateModel.get_sms_template();
		setAttr("smsTemplates", smsTemplates);
		setAttr("smsTemplatesAll",smsTemplatesAll);
		setAttr("now",DateTools.getTime());
		
		set_head_title("短信发送");
		render("sms_send.html");
	}
	
	/**
	 * 发送短信
	 * @throws Exception
	 */
	public void short_message_send() throws Exception{
		int uid=current_user_id();
		String mobiles=getPara("mobile");
		String content=getPara("content");
		String send_time=getPara("send_time");
		boolean flag=false;
		
		//立即发送调用短信单个发送的接口
		if("send_now".equals(send_time)){
			String[] mobile=mobiles.split(",");
			for(String m:mobile){
				flag=SmsModel.send(m, content, uid,send_time);
				if(flag==false){
					break;
				}
			}
		}
		else{//定时发送
			flag=SmsModel.send_batch(mobiles, content, uid, send_time);
		}
		
		if(flag){
			render_message("SUCCESS","短信发送成功!");
		}
		else{
			render_message("ERROR","短信发送失败，请重新尝试!");
		}
	}
	
	/**
	 * 跳转到短信记录的页面
	 * @throws Exception
	 */
	public void sms_log() throws Exception{
		set_head_title("短信发送记录");
		render("sms_log_list.html");
	}
	
	/**
	 * 获取短信发送记录list数据
	 * @throws Exception
	 */
	public void json_sms_log() throws Exception{
		int uid = current_user_id();
		String mobile = getPara("mobile","").trim();
		String content = getPara("content","").trim();
		renderJson(JsonKit.toJson(SmsLogModel.json_sms_log(page, rows, uid ,mobile ,content)));
	}
	
	/**
	 * 删除短信记录
	 * @throws Exception
	 */
	public void sms_log_delete() throws Exception{
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS", message = "删除成功";
		if(StrKit.notBlank(checked_ids)){//
			String[] delete_ids = checked_ids.split(",");
			for(int i = 0; i < delete_ids.length; i++){
				String id = delete_ids[i];
				SmsLog.dao.deleteById(id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status,message);
	}
	
	/**
	 * 短信推送接口，Luosimao发送时需要采用单条发送的模式才能接收推送.
	 * https://luosimao.com/sms_rec?batch_id=09-B0966DD4-FD28-43C3-94A8-F762B2269B72&mobile=13761428267&status=DELIVRD
	 */
	
	@Clear(LoginInterceptor.class)
	public void sms_receive(){
		if(true){
			//api 为 luosimao
			String token = getPara("batch_id", "");
			String mobile = getPara("mobile", "");
			String status = getPara("status", "");//DELIVRD 接收成功 UNDELIV 接收失败(信号不良,关停机,黑名单等异常情况)
			
			if(StrKit.notBlank(token)){
				SmsLog smsLog = SmsModel.receive(token);
				if(smsLog == null){
					renderText("ERROR");
					return;
				}
				if("DELIVRD".equals(status)){//接收成功
					smsLog.setStatus(2);
					smsLog.update();
				}
				else if("UNDELIV".equals(status)){
					smsLog.setStatus(-2);
					smsLog.update();
				}
				else{
					//do nothing.
				}
				
			}
		}
		renderText("SUCCESS");
	}
	
	
}
