package plugins.attendance;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSON;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 阿里钉钉考勤接口
 * @author 陈炳坤
 *
 */
public class AttendanceAPI {
	private static final String CorpID="ding47bfbb997204ee6f35c2f4657eb6378f";
	private static final String CorpSecret="sQ9sIRJ6oLS76hRmSs-aDC2_Lnpe01LKZsAyl9v_Jad6Nrx2ORDzLDa1qEh85aGR";
	private static final String url_accesstoken="https://oapi.dingtalk.com/gettoken?";
	private static final String url_get_attendance="https://oapi.dingtalk.com/attendance/list?";
	
	/**
	 * 获得token
	 * @return
	 */
	public static String get_token(){
		Client client = Client.create();
		String url=url_accesstoken+"corpid="+CorpID+"&corpsecret="+CorpSecret;
		URI uri=null;
		try {
			uri=new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        WebResource webResource = client.resource(uri);
        ClientResponse response =  webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).get(ClientResponse.class);
        String textEntity = response.getEntity(String.class);
        return textEntity;
	}
	
	/**
	 * 获得考勤的数据
	 * @param token
	 * @return
	 */
	public static String get_attendance(String token,Map<String,Object> map){
		Client client = Client.create();
		String url=url_get_attendance+"access_token="+token;
		URI uri=null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        WebResource webResource = client.resource(uri);
        String params=JSON.toJSONString(map);
        ClientResponse response =  webResource.header("Content-Type",MediaType.APPLICATION_JSON).post(ClientResponse.class,params);
        String textEntity = response.getEntity(String.class);
        return textEntity;
	}
}
