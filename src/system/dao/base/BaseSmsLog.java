package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSmsLog<M extends BaseSmsLog<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	public java.lang.String getMobile() {
		return get("mobile");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setApi(java.lang.String api) {
		set("api", api);
	}

	public java.lang.String getApi() {
		return get("api");
	}

	public void setToken(java.lang.String token) {
		set("token", token);
	}

	public java.lang.String getToken() {
		return get("token");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setResponse(java.lang.String response) {
		set("response", response);
	}

	public java.lang.String getResponse() {
		return get("response");
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

	public void setSendTime(java.lang.String sendTime) {
		set("send_time", sendTime);
	}

	public java.lang.String getSendTime() {
		return get("send_time");
	}

}
