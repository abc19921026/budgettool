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
	/**
	 * 新建预算时 加载总计和其他费用
	 * @param budget_id
	 * @throws Exception
	 */
	public static void load_line_item(int budget_id)throws Exception{
		BudgetLineItem project = new BudgetLineItem();
		project.setSection(0);
		project.setWeight(0);
		project.setLineItemField("project");
		project.setLineItemName("基础工程总计");
		project.setLineItemComment("详见小计");
		project.setBudgetId(budget_id);
		project.save();
		BudgetLineItem material = new BudgetLineItem();
		material.setSection(1);
		material.setWeight(10);
		material.setLineItemField("material");
		material.setLineItemName("主材工程总计");
		material.setLineItemComment("详见小计");
		material.setBudgetId(budget_id);
		material.save();
		BudgetLineItem management = new BudgetLineItem();
		management.setSection(2);
		management.setWeight(20);
		management.setLineItemField("management");
		management.setLineItemName("管理费");
		management.setLineItemComment("（基础工程总计+主材工程总计）*10%");
		management.setBudgetId(budget_id);
		management.save();
		BudgetLineItem design = new BudgetLineItem();
		design.setSection(3);
		design.setWeight(30);
		design.setLineItemField("design");
		design.setLineItemName("设计费");
		design.setLineItemComment("80-120元/平方");
		design.setBudgetId(budget_id);
		design.save();
		BudgetLineItem draw = new BudgetLineItem();
		draw.setSection(4);
		draw.setWeight(40);
		draw.setLineItemField("draw");
		draw.setLineItemName("制图费");
		draw.setLineItemComment("依设计师、装修风格等情况而定，1000-3000元/套");
		draw.setBudgetId(budget_id);
		draw.save();
		BudgetLineItem tax = new BudgetLineItem();
		tax.setSection(5);
		tax.setWeight(50);
		tax.setLineItemField("tax");
		tax.setLineItemName("税金");
		tax.setLineItemComment("甲方如需开发票，税金自理，税金按直接价和管理费的总和的6%计算。");
		tax.setBudgetId(budget_id);
		tax.save();
		BudgetLineItem remote = new BudgetLineItem();
		remote.setSection(6);
		remote.setWeight(60);
		remote.setLineItemField("remote");
		remote.setLineItemName("远程管理费");
		remote.setLineItemComment("杭州地区（不含老余杭、萧山南郊、富阳）之外，另收3-12%的远程管理费。");
		remote.setBudgetId(budget_id);
		remote.save();
	}
}
