package app.dao.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBudgetItemAmount<M extends BaseBudgetItemAmount<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setAmountMaterial(java.math.BigDecimal amountMaterial) {
		set("amount_material", amountMaterial);
	}

	public java.math.BigDecimal getAmountMaterial() {
		return get("amount_material");
	}

	public void setAmountAssist(java.math.BigDecimal amountAssist) {
		set("amount_assist", amountAssist);
	}

	public java.math.BigDecimal getAmountAssist() {
		return get("amount_assist");
	}

	public void setAmountLabor(java.math.BigDecimal amountLabor) {
		set("amount_labor", amountLabor);
	}

	public java.math.BigDecimal getAmountLabor() {
		return get("amount_labor");
	}

	public void setAmountMachinery(java.math.BigDecimal amountMachinery) {
		set("amount_machinery", amountMachinery);
	}

	public java.math.BigDecimal getAmountMachinery() {
		return get("amount_machinery");
	}

	public void setAmountLoss(java.math.BigDecimal amountLoss) {
		set("amount_loss", amountLoss);
	}

	public java.math.BigDecimal getAmountLoss() {
		return get("amount_loss");
	}

	public void setPriceMaterial(java.math.BigDecimal priceMaterial) {
		set("price_material", priceMaterial);
	}

	public java.math.BigDecimal getPriceMaterial() {
		return get("price_material");
	}

	public void setPriceAssist(java.math.BigDecimal priceAssist) {
		set("price_assist", priceAssist);
	}

	public java.math.BigDecimal getPriceAssist() {
		return get("price_assist");
	}

	public void setPriceLabor(java.math.BigDecimal priceLabor) {
		set("price_labor", priceLabor);
	}

	public java.math.BigDecimal getPriceLabor() {
		return get("price_labor");
	}

	public void setPriceMachinery(java.math.BigDecimal priceMachinery) {
		set("price_machinery", priceMachinery);
	}

	public java.math.BigDecimal getPriceMachinery() {
		return get("price_machinery");
	}

	public void setPriceLoss(java.math.BigDecimal priceLoss) {
		set("price_loss", priceLoss);
	}

	public java.math.BigDecimal getPriceLoss() {
		return get("price_loss");
	}

	public void setUpdated(java.lang.Integer updated) {
		set("updated", updated);
	}

	public java.lang.Integer getUpdated() {
		return get("updated");
	}

}
