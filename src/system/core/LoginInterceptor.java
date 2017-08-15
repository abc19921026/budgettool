package system.core;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class LoginInterceptor  implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		Controller c = inv.getController();
		if(c.getSessionAttr("uid") == null || (Integer) c.getSessionAttr("uid") == 0 )
			c.redirect("/user/login");	
		else
		    inv.invoke();
	}

}
