package plugins.sms.luosimao;

import com.alibaba.fastjson.JSON;
import com.jfinal.ext.interceptor.POST;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Luosimao短信接口测试代码
 * @author Administrator
 *
 */

public class Api {
    public static void main(String[] args) {
        Api api = new Api();
        /*String httpResponse =  api.testSend();
         try {
            JSONObject jsonObj = new JSONObject( httpResponse );
            int error_code = jsonObj.getInt("error");
            String error_msg = jsonObj.getString("msg");
            if(error_code==0){
                System.out.println("Send message success.");
            }else{
                System.out.println("Send message failed,code is "+error_code+",msg is "+error_msg);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }

        httpResponse =  api.testStatus();
        try {
            JSONObject jsonObj = new JSONObject( httpResponse );
            int error_code = jsonObj.getInt("error");
            if( error_code == 0 ){
                int deposit = jsonObj.getInt("deposit");
                System.out.println("Fetch deposit success :"+deposit);
            }else{
                String error_msg = jsonObj.getString("msg");
                System.out.println("Fetch deposit failed,code is "+error_code+",msg is "+error_msg);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        String httpResponse=api.testToken();
        System.out.println(httpResponse);
        JSONObject jsonObj;
		try {
			jsonObj = new JSONObject( httpResponse );
			String token=jsonObj.getString("access_token");
			System.out.println(token);
			
			String attendance=api.testAttendance(token);
			System.out.println(attendance);
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
    }

    private String testToken(){
        // just replace key here
        Client client = Client.create();
        /*client.addFilter(new HTTPBasicAuthFilter(
            "api","4ea66b9ea07273a78216493d29684918"));*/
        WebResource webResource = client.resource(
            "https://oapi.dingtalk.com/gettoken?corpid=ding47bfbb997204ee6f35c2f4657eb6378f&corpsecret=sQ9sIRJ6oLS76hRmSs-aDC2_Lnpe01LKZsAyl9v_Jad6Nrx2ORDzLDa1qEh85aGR");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
//        formData.add("mobile", "15267041596");
//        formData.add("message", "您正在登录验证，验证码111710，切勿将验证码泄露于他人。【铭鸿装饰】");
        ClientResponse response =  webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).get(ClientResponse.class);
//        post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);
//        int status = response.getStatus();
        //System.out.print(textEntity);
        //System.out.print(status);
        return textEntity;
    }
    
    private String testAttendance(String token) throws ParseException{
        // just replace key here
        Client client = Client.create();
        /*client.addFilter(new HTTPBasicAuthFilter(
            "api","4ea66b9ea07273a78216493d29684918"));*/
        long start_time=stringToLong("2016-12-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        long end_time=stringToLong("2016-12-31 00:00:00", "yyyy-MM-dd HH:mm:ss");
        WebResource webResource = client.resource("https://oapi.dingtalk.com/checkin/record?access_token="+token+"&department_id=1&start_time="+start_time+"&end_time="+end_time+"&offset=0&size=100&order=asc");
//        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        /*Map<String,Object> map=new HashMap<String,Object>();
        map.put("workDateFrom", "2016-12-01 00:00:00");
        map.put("workDateTo", "2016-12-06 00:00:00");
        String params=JSON.toJSONString(map);*/
        /*formData.add("access_token",token);
        formData.add("department_id","1");
        
        long start_time=stringToLong("2016-12-01", "yyyy-MM-dd HH:mm:ss");
        long end_time=stringToLong("2016-12-31", "yyyy-MM-dd HH:mm:ss");
        formData.add("start_time", start_time);
        formData.add("end_time", end_time);
        formData.add("offset", 0);
        formData.add("size", 100);
        formData.add("order", "asc");*/
//        ClientResponse response =  webResource.header("Content-Type",MediaType.APPLICATION_JSON).post(ClientResponse.class,params);
//        post(ClientResponse.class, formData);
        ClientResponse response=webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).get(ClientResponse.class);
        String textEntity = response.getEntity(String.class);
//        int status = response.getStatus();
        //System.out.print(textEntity);
        //System.out.print(status);
        return textEntity;
    }
    
    public static Date stringToDate(String strTime, String formatType)
 			throws ParseException {
 		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
 		Date date = null;
 		date = formatter.parse(strTime);
 		return date;
 	}
    
    public static long stringToLong(String strTime, String formatType)
 			throws ParseException {
 		Date date = stringToDate(strTime, formatType); // String类型转成date类型
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = dateToLong(date); // date类型转成long类型
 			return currentTime;
 		}
 	}
    
    public static long dateToLong(Date date) {
 		return date.getTime();
 	}
    
    private String testSend(){
        // just replace key here
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
            "api","4ea66b9ea07273a78216493d29684918"));
        WebResource webResource = client.resource(
            "http://sms-api.luosimao.com/v1/send.json");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("mobile", "15267041596");
        formData.add("message", "您正在登录验证，验证码111710，切勿将验证码泄露于他人。【铭鸿装饰】");
        ClientResponse response =  webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).
        post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        //System.out.print(textEntity);
        //System.out.print(status);
        return textEntity;
    }

    private String testStatus(){
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
            "api","key-4ea66b9ea07273a78216493d29684918"));
        WebResource webResource = client.resource( "http://sms-api.luosimao.com/v1/status.json" );
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        ClientResponse response =  webResource.get( ClientResponse.class );
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        //System.out.print(status);
        //System.out.print(textEntity);
        return textEntity;
    }
}
