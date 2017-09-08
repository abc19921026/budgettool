package app.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBudgetPackage<M extends BaseBudgetPackage<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setComment(java.lang.String comment) {
		set("comment", comment);
	}

	public java.lang.String getComment() {
		return get("comment");
	}

	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}

	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public void setTotal(java.math.BigDecimal total) {
		set("total", total);
	}

	public java.math.BigDecimal getTotal() {
		return get("total");
	}

	public void setDeleted(java.lang.Integer deleted) {
		set("deleted", deleted);
	}

	public java.lang.Integer getDeleted() {
		return get("deleted");
	}

	public void setCreateUid(java.lang.Integer createUid) {
		set("create_uid", createUid);
	}

	public java.lang.Integer getCreateUid() {
		return get("create_uid");
	}

	public void setCreated(java.lang.Integer created) {
		set("created", created);
	}

	public java.lang.Integer getCreated() {
		return get("created");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}

	public java.lang.String getCreateTime() {
		return get("create_time");
	}

	public void setUpdateUid(java.lang.Integer updateUid) {
		set("update_uid", updateUid);
	}

	public java.lang.Integer getUpdateUid() {
		return get("update_uid");
	}

	public void setUpdated(java.lang.Integer updated) {
		set("updated", updated);
	}

	public java.lang.Integer getUpdated() {
		return get("updated");
	}

	public void setUpdateTime(java.lang.String updateTime) {
		set("update_time", updateTime);
	}

	public java.lang.String getUpdateTime() {
		return get("update_time");
	}

}
