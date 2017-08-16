package app.models.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

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
	
	public static Map<String, Object> get_budget_item_by_class(int budget_class_id, int rows, int page){
		Map<String, Object> re = new HashMap<String, Object>();
		List<BudgetItem> list = null;
		//List<Record> re =  ;
		try{
			String select = "select b_i.*";
			//re = Db.find(sql, budget_class_id);
			String sqlExceptSelect = " from budget_item b_i "
					+ "where b_i.budget_class_id = ? order by b_i.weight asc";
			Page<BudgetItem> pages = dao.paginate(page, rows, select, sqlExceptSelect, budget_class_id);
			list = pages.getList();
			int total = pages.getTotalRow();			
			
			List<BudgetItem> footlist=new ArrayList<BudgetItem>();
			BudgetItem budgetItem=new BudgetItem();
			budgetItem.setName("总计:");
			BigDecimal totalamount=Db.queryBigDecimal(" select sum(amount) from budget_item where budget_class_id = ? ", budget_class_id);
			if(totalamount==null){
				totalamount=new BigDecimal(0.00);
			}
			budgetItem.setAmount(totalamount);
			budgetItem.setMaterialId(0);
			budgetItem.setMaterialAttached(-1);
			footlist.add(budgetItem);
			re.put("total", total);
			re.put("rows", list);
			re.put("footer", footlist);
			return re;
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		return re;
	}
}
