package plugins.sms.luosimao;

public class LuosimaoApiTest {

	public static void main(String[] args) {
		String mobile = "15267041596";
		String message = "您的验证码为171126";
		
		String api_key = "4ea66b9ea07273a78216493d29684918";
		LuosimaoApi luosimaoApi = new LuosimaoApi(api_key);
		luosimaoApi.send(mobile,message,"");
	}
}
