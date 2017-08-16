package app.models.budget;

import com.jfinal.plugin.activerecord.Db;

import app.dao.BudgetItem;

public class BudgetItemModel extends BudgetItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int delete_budget_item_by_budget_id(int budget_id){
		int re = 0;
		
		String sql = "";
		sql = "delete bi, bia, bic from budget_item bi"
				+ " INNER JOIN budget_item_amount bia on bi.id = bia.id"
				+ " INNER JOIN budget_item_cost bic on bi.id = bic.id"
				+ " WHERE bi.budget_id = ?";
		re = Db.update(sql, budget_id);
		return re;
	}
}
