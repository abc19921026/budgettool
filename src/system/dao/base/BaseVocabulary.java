package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseVocabulary<M extends BaseVocabulary<M>> extends Model<M> implements IBean {

	public void setVid(java.lang.Integer vid) {
		set("vid", vid);
	}

	public java.lang.Integer getVid() {
		return get("vid");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

}
