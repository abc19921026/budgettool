package system.models;

import java.util.Map;

import com.jfinal.kit.StrKit;

import plugins.sms.luosimao.LuosimaoApi;
import system.core.BaseModel;
import system.dao.SmsLog;

public class SmsModel {

	static public boolean send(String mobile, String message,int uid, String send_time,String type){
		if(StrKit.isBlank(message)||StrKit.isBlank(mobile)){
			return false;
		}
		String current_api = "LUOSIMAO";
		Map<String, Object> re = null;
		if(current_api.equals("LUOSIMAO")){
			//铁壳网络
			//获取api key;
			String api_key = "4ea66b9ea07273a78216493d29684918";
			LuosimaoApi luosimaoApi = new LuosimaoApi(api_key);
			if(type.equals("single")){
				//single send
				re = luosimaoApi.send(mobile, message,send_time);
			}else{
				//batch send
				re = luosimaoApi.send_batch(mobile, message,send_time);
			}
		}else if(current_api.equals("ALIDAYU")){
			//阿里大于
		}
		
		String response = "";
		String content = message;
		String token = "";//单条发送的标识，Luosimao为batch_id
		int status = 0;
		try{
			response = (String) re.get("response");
			content = (String) re.get("content");
			status = (Integer) re.get("status");
			token = (String) re.get("token");
		}catch(Exception e){
			e.printStackTrace();
		}
		save_log(mobile, content, current_api, status, response, token, uid,send_time);
		if(status == 1){
			return true;
		}else{
			return false;
		}
	}
	
	static public boolean send(String mobile, String message, int uid,String send_time){
		return send(mobile, message, uid,send_time,"single");
	}
	
	static public boolean send_batch(String mobile, String message, int uid,String send_time){
		return send(mobile, message, uid, send_time,"batch");
	}	
	
	static public void save_log(String mobile, String content, String api, int status, String response, String token, int uid, String send_time){

		SmsLog sms_log = new SmsLog();
		sms_log.setMobile(mobile);
		sms_log.setContent(content);
		sms_log.setApi(api);

		sms_log.setStatus(status);
		sms_log.setResponse(response);
		
		sms_log.setToken(token);
		
		sms_log.setCreateUid(uid);
		sms_log.setSendTime(send_time);
		BaseModel.setCreateTime(sms_log);
		sms_log.save();
	}
	
	/**
	 * 短信推送
	 */
	static public SmsLog receive(String token){
		String sql = "select * from  system_sms_log where token = ? limit 1";
		return SmsLog.dao.findFirst(sql, token);
	}
}
