package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRoleMenuLink<M extends BaseRoleMenuLink<M>> extends Model<M> implements IBean {

	public void setRid(java.lang.Integer rid) {
		set("rid", rid);
	}

	public java.lang.Integer getRid() {
		return get("rid");
	}

	public void setMlid(java.lang.Integer mlid) {
		set("mlid", mlid);
	}

	public java.lang.Integer getMlid() {
		return get("mlid");
	}

}
