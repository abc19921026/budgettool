package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends Model<M> implements IBean {

	public void setUid(java.lang.Integer uid) {
		set("uid", uid);
	}

	public java.lang.Integer getUid() {
		return get("uid");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setPass(java.lang.String pass) {
		set("pass", pass);
	}

	public java.lang.String getPass() {
		return get("pass");
	}

	public void setMail(java.lang.String mail) {
		set("mail", mail);
	}

	public java.lang.String getMail() {
		return get("mail");
	}

	public void setTheme(java.lang.String theme) {
		set("theme", theme);
	}

	public java.lang.String getTheme() {
		return get("theme");
	}

	public void setCreated(java.lang.String created) {
		set("created", created);
	}

	public java.lang.String getCreated() {
		return get("created");
	}

	public void setAccess(java.lang.Integer access) {
		set("access", access);
	}

	public java.lang.Integer getAccess() {
		return get("access");
	}

	public void setLogin(java.lang.Integer login) {
		set("login", login);
	}

	public java.lang.Integer getLogin() {
		return get("login");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setPicture(java.lang.String picture) {
		set("picture", picture);
	}

	public java.lang.String getPicture() {
		return get("picture");
	}

	public void setData(java.lang.String data) {
		set("data", data);
	}

	public java.lang.String getData() {
		return get("data");
	}

	public void setUpdateTime(java.lang.String updateTime) {
		set("update_time", updateTime);
	}

	public java.lang.String getUpdateTime() {
		return get("update_time");
	}

}
