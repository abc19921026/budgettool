package system.tools;

import javax.servlet.http.HttpServletRequest;

public class UtilTools {

        /**
	 * 获取上下文URL全路径
	 * @param request
	 * @author nbjgl
	 */
 public static String getContextPath(HttpServletRequest request) {
     StringBuilder sb = new StringBuilder();
     int port = request.getServerPort();
     if(80==port){
	 sb.append(request.getScheme()).append("://").append(request.getServerName()).append(request.getContextPath());
     }else{
	 sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(port).append(request.getContextPath());
     }
 
	 String path = sb.toString();
	 return path;
   }

}
