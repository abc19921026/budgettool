package app.models.budget;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import app.dao.BudgetLineItem;

public class BudgetLineItemModel extends BudgetLineItem{
	private static String field_project = "project"; // 基础工程field key
	private static String field_material = "material";//主材工程 field key
	private static String field_management = "management";//
	private static String field_design = "design";//
	private static String field_draw = "draw";//
	private static String field_tax = "tax";//
	private static String field_remote = "remote";//
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static boolean subtotal_by_section(int budget_id, int section){
		String sql = "SELECT SUM(amount) AS total FROM budget_item  WHERE budget_id = ? and section = ?";
		Record record = Db.findFirst(sql, budget_id, section);
		BigDecimal subtotal = record.getBigDecimal("total");
		
		sql = "select * from budget_line_item where budget_id = ? and section = ?";
		BudgetLineItem bli = BudgetLineItem.dao.findFirst(sql, budget_id, section);
		if(bli == null){
			//没有这一项
			LogKit.warn("budget_id=" + budget_id + "未找到line item 数据, section=" + section);
			return false;
		}
		bli.setLineItemAmount(subtotal);	
		boolean re = bli.update();
		return re;
	}
	/**
	 * 计算管理费
	 * @param budget_id
	 * @param section
	 * @return
	 */
	public static boolean subtotal_management(int budget_id){
		String sql = "select SUM(line_item_amount) AS total from budget_line_item where budget_id = ? and section in(0,1)";
		Record record = Db.findFirst(sql, budget_id);
		BigDecimal subtotal = record.getBigDecimal("total");
		if(subtotal == null){//修复预算基础工程 + 主材均无记录bug
			subtotal = new BigDecimal(0);
		}
		//管理费 = （基础工程 + 主材工程） x 10%
		BigDecimal fee_rate = new BigDecimal(0.1);
		
		subtotal = subtotal.multiply(fee_rate);
		
		sql = "select * from budget_line_item where budget_id = ? and line_item_field = ?";
		BudgetLineItem bli = BudgetLineItem.dao.findFirst(sql, budget_id, field_management);
		if(bli == null){
			//没有这一项
			LogKit.warn("budget_id=" + budget_id + "未找到line item 数据");
			return false;
		}
		bli.setLineItemAmount(subtotal);	
		boolean re = bli.update();
		return re;
	}
	public static Map<String, Object> get_budget_line_item(int budget_id, int rows, int page){
		Map<String, Object> re = new HashMap<String, Object>();
		List<BudgetLineItem> list = null;
		//List<Record> re =  ;
		try{
			String select = "select bli.* ";
			//re = Db.find(sql, budget_class_id);
			String sqlExceptSelect = " from budget_line_item bli where bli.budget_id = ? order by bli.weight asc";
			Page<BudgetLineItem> pages = dao.paginate(page, rows, select, sqlExceptSelect, budget_id);
			list = pages.getList();
			int total = pages.getTotalRow();			

			re.put("total", total);
			re.put("rows", list);
			return re;
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		return re;
	}
}
