package plugins.sms.luosimao;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class LuosimaoApi {

	
	//private static String API_KEY = "key-e4d54f27061d546a6adc2908876dbfc6";
	private static String API_KEY = "";
	private static String SIGNATURE = "铭鸿装饰";
	private final static String send_url = "http://sms-api.luosimao.com/v1/send.json";    //单独发送接口
	private final static String send_url_batch = "http://sms-api.luosimao.com/v1/send_batch.json";//群发接口
	private static Client client = Client.create();
	    
	public LuosimaoApi(String api_key){
		API_KEY = "key-" + api_key;
	}
	/*@SuppressWarnings("unused")
	private static Client getClient(){
		return client;
	} */
	/**
	 * 单条发送
	 * @param mobile
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> send(String mobile, String message,String send_time,String type){
		System.out.print(API_KEY);
        client.addFilter(new HTTPBasicAuthFilter("api", API_KEY));
        
        WebResource webResource = null;
        
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        
        if(type.equals("single")){
        	//单独发送接口
        	webResource = client.resource(send_url);
            formData.add("mobile", mobile);
        }else{
        	//批量发送接口
        	webResource = client.resource(send_url_batch);
            formData.add("mobile_list", mobile);
            if(!"send_now".equals(send_time)){
            	formData.add("time",send_time);
            }
        }
        

        //formData.add("mobile", mobile);
        //添加签名
        String content = message + "【"+ SIGNATURE + "】";
        formData.add("message", content);
        ClientResponse response =  webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).
        post(ClientResponse.class, formData);
        //返回的json
        String textEntity = response.getEntity(String.class);
        // int status = response.getStatus(); 发送状态 200ok，404没找到
        
        JSONObject jsonObj;
        int error_code = 0;
        String token = "";
		try {
			jsonObj = new JSONObject(textEntity);
	        error_code = jsonObj.getInt("error");
	        if("send_now".equals(send_time)){
	        	token = jsonObj.getString("batch_id");
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		int status = 0;
        if(error_code == 0){
        	//发送成功
        	status = 1;
        }else{
        	//发送失败
        	status = -1;
        	//return false;
        }
        
        Map<String, Object> re = new HashMap<String, Object>();
  		re.put("status", status);
  		re.put("content", content);
  		re.put("response", textEntity);
  		re.put("token", token);
  		return re;
	}
	
	public Map<String, Object> send(String mobile, String message,String send_time){
		return send(mobile, message,send_time,"single");
	}
	
	public Map<String, Object> send_batch(String mobile, String message,String send_time){
		return send(mobile, message,send_time,"batch");
	}
	
	//接收短信推送
	/*public static boolean receive(String batch_id, String mobile, String status){
		return false;
	}*/

}
