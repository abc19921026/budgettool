package system.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSmsApi<M extends BaseSmsApi<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setApiName(java.lang.String apiName) {
		set("api_name", apiName);
	}

	public java.lang.String getApiName() {
		return get("api_name");
	}

	public void setApiTitle(java.lang.String apiTitle) {
		set("api_title", apiTitle);
	}

	public java.lang.String getApiTitle() {
		return get("api_title");
	}

	public void setApiDesc(java.lang.String apiDesc) {
		set("api_desc", apiDesc);
	}

	public java.lang.String getApiDesc() {
		return get("api_desc");
	}

	public void setApiUser(java.lang.String apiUser) {
		set("api_user", apiUser);
	}

	public java.lang.String getApiUser() {
		return get("api_user");
	}

	public void setApiKey(java.lang.String apiKey) {
		set("api_key", apiKey);
	}

	public java.lang.String getApiKey() {
		return get("api_key");
	}

	public void setApiDocs(java.lang.String apiDocs) {
		set("api_docs", apiDocs);
	}

	public java.lang.String getApiDocs() {
		return get("api_docs");
	}

}
