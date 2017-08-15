package app.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBudget<M extends BaseBudget<M>> extends Model<M> implements IBean {

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

	public void setSn(java.lang.String sn) {
		set("sn", sn);
	}

	public java.lang.String getSn() {
		return get("sn");
	}

	public void setOrderId(java.lang.Integer orderId) {
		set("order_id", orderId);
	}

	public java.lang.Integer getOrderId() {
		return get("order_id");
	}

	public void setVersion(java.lang.String version) {
		set("version", version);
	}

	public java.lang.String getVersion() {
		return get("version");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setPrimary(java.lang.Integer primary) {
		set("primary", primary);
	}

	public java.lang.Integer getPrimary() {
		return get("primary");
	}

	public void setTotal(java.math.BigDecimal total) {
		set("total", total);
	}

	public java.math.BigDecimal getTotal() {
		return get("total");
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
