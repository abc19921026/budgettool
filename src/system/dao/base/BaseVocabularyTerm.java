package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseVocabularyTerm<M extends BaseVocabularyTerm<M>> extends Model<M> implements IBean {

	public void setTid(java.lang.Integer tid) {
		set("tid", tid);
	}

	public java.lang.Integer getTid() {
		return get("tid");
	}

	public void setVid(java.lang.Integer vid) {
		set("vid", vid);
	}

	public java.lang.Integer getVid() {
		return get("vid");
	}

	public void setPid(java.lang.Integer pid) {
		set("pid", pid);
	}

	public java.lang.Integer getPid() {
		return get("pid");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setWeight(java.lang.Integer weight) {
		set("weight", weight);
	}

	public java.lang.Integer getWeight() {
		return get("weight");
	}

	public void setDepth(java.lang.Integer depth) {
		set("depth", depth);
	}

	public java.lang.Integer getDepth() {
		return get("depth");
	}

}
