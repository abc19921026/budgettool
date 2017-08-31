package app.models.budget;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import system.core.BaseModel;
import app.dao.Budget;
import app.dao.BudgetClass;
import app.dao.BudgetItem;
import app.dao.BudgetItemAmount;
import app.dao.BudgetItemCost;
import app.dao.BudgetLineItem;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class BudgetModel extends Budget{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Map<String, Object> get_json_budget_list(int rows, int page,String sn, String startdate, String enddate,String name) throws Exception{
		// TODO Auto-generated method stub
		List<Object> params=new ArrayList<Object>();
		String select = "SELECT b.*";
		StringBuffer sqlExceptSelect = new StringBuffer( " FROM budget b");
				sqlExceptSelect.append(" WHERE 1=1 ");
		if(StrKit.notBlank(sn)){
			sqlExceptSelect.append(" and b.sn like ?");
			params.add("%"+sn+"%");
		}
		if(StrKit.notBlank(startdate)&&StrKit.notBlank(enddate)){
			sqlExceptSelect.append(" AND DATE_FORMAT(b.update_time,'%Y-%m-%d') >= ? AND DATE_FORMAT(b.update_time,'%Y-%m-%d') <= ? ");
			params.add(startdate);
			params.add(enddate);
		}
		if(StrKit.notBlank(name)){
			sqlExceptSelect.append(" and b.name like ?");
			params.add("%"+name+"%");
		}
		sqlExceptSelect.append(" order by b.update_time desc");
		Page<Budget> pages;
		
		pages = dao.paginate(page, rows, select, sqlExceptSelect.toString(),params.toArray());
		List<Budget> list = pages.getList();
		int total = pages.getTotalRow();
		Map<String,Object> re = new HashMap<String,Object>();
		re.put("total", total);
		re.put("rows", list);
		return re;
	}
	
	/**
	 * 生成预算编号
	 * @param budget_id
	 * @return
	 */
	public static String generate_budget_sn()throws Exception{
		String budget_sn_token = "Y";
		String sn = "";
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyMMddHH");
		//String s = f.format(date).substring(2,8);
		String s = f.format(date);//161011
		//int rand_4 = Math.random()
		int min = 1000, max = 9999; //生成四位数的随机数
        Random random = new Random();

        int random_4 = random.nextInt(max)%(max-min+1) + min;
		sn = budget_sn_token + s + "-" + random_4;
		return sn;
	}
	
	public static Budget get_budget(int id)throws Exception {
		// TODO Auto-generated method stub
		try {
			//Budget re = Budget.dao.findById(id);
			String sql = "SELECT b.* FROM budget b WHERE b.id = ? ";
			Budget re = Budget.dao.findFirst(sql, id);
			return re;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 预算总计
	 * 
	 * @param budget_id
	 *            预算id
	 * @return
	 * @throws Exception
	 */
	public static boolean total(int budget_id) {
		if(budget_id <=0 ){
			return false;
		}
		boolean result = false;
		BigDecimal total;
		//先计算基础工程总计
		BudgetLineItemModel.subtotal_by_section(budget_id, 0);
		//计算主材工程总计
		BudgetLineItemModel.subtotal_by_section(budget_id, 1);
		//计算管理费
		BudgetLineItemModel.subtotal_management(budget_id);
		String sql = "SELECT SUM(line_item_amount) AS total FROM budget_line_item  WHERE budget_id = ?";
		Record re = Db.findFirst(sql, budget_id);
		total = re.getBigDecimal("total");
		if(total != null){
			result = Budget.dao.findById(budget_id).set("total", total).update();
		}
		return result;
	}
	/**
	 * 搜索历史预算表
	 * @param q
	 * @throws Exception
	 */
	public static List<Record> get_history_budget(String q)throws Exception{
		List<Object> params=new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select b.*,b.name as text from budget b where 1=1");
		if(StrKit.notBlank(q)){
			sql.append(" and b.name like ?");
			params.add("%"+q+"%");
		}
		List<Record> list = Db.find(sql.toString(), params.toArray());
		return list;
	}
	
	/**
	 * 
	 * @param budget_id
	 * @param new_budget_id
	 * @return
	 */
	public static boolean budget_copy_all(int budget_id, int new_budget_id){

		boolean succeed = false;

		// 复制一级 budget_class
		String sql = "select * from budget_class where budget_id = ? and parent_id = 0";
		List<BudgetClass> re = BudgetClass.dao.find(sql, budget_id);
		for (int i = 0; i < re.size(); i++) {
			BudgetClass bc = re.get(i);
			int budget_class_id = bc.getId();
			// int budget_parent_id = bc.getParentId();
			bc.setId(null);
			bc.setBudgetId(new_budget_id);
			bc.setParentId(0);
			BaseModel.setCreateTime(bc);
			BaseModel.setUpdateTime(bc);
			succeed = bc.save();
			//新的预算分类id
			int new_budget_class_id = bc.getId();
			copy_budget_item(budget_class_id, new_budget_class_id,new_budget_id);
			
			//检查该分类下是否有下级分类，如果有继续复制
			String sql_has_children = "select * from budget_class where parent_id = ?";
			List<BudgetClass> re_children = BudgetClass.dao.find(sql_has_children, budget_class_id);
			for( int j = 0; j < re_children.size(); j++){
				//System.out.println(budget_class_id);
				BudgetClass bc_child = re_children.get(j);
				int budget_class_child_id = bc_child.getId();
				bc_child.setId(null);//重置对象，设为新对象
				bc_child.setBudgetId(new_budget_id);
				bc_child.setParentId(new_budget_class_id);
				BaseModel.setCreateTime(bc_child);
				BaseModel.setUpdateTime(bc_child);
				succeed = bc_child.save();				
				int new_budget_class_child_id = bc_child.getId();
				copy_budget_item(budget_class_child_id, new_budget_class_child_id, new_budget_id);				
			}
		}
		copy_budget_line_item(budget_id,new_budget_id);
		return succeed;	
	}
	private static void copy_budget_line_item(int budget_id, int new_budget_id) {
		String sql = "select bli.* from budget_line_item bli where bli.budget_id = ?";
		List<BudgetLineItem> list = BudgetLineItem.dao.find(sql, budget_id);
		for(BudgetLineItem bli:list){
			bli.setId(null);
			bli.setBudgetId(new_budget_id);
			bli.save();
		}
		
	}

	/**
	 * 根据分类id复制预算条目，同时复制budget_item_amount 和budget_item_cost
	 * @param budget_class_id
	 * @param new_budget_class_id
	 * @param new_budget_id
	 * @return
	 */
	public static boolean copy_budget_item(int budget_class_id,
			int new_budget_class_id, int new_budget_id) {
		String sql2 = "select bi.*, bia.* from budget_item bi "
				+ "left join budget_item_amount bia on bi.id = bia.id "
				+ "where bi.budget_class_id = ? ";
		List<BudgetItem> re2 = BudgetItem.dao.find(sql2, budget_class_id);

		boolean re = false;
		for (int j = 0; j < re2.size(); j++) {

			BudgetItem bi = re2.get(j);
			Integer id = bi.getId();
			bi.setId(null);
			bi.setBudgetId(new_budget_id);
			bi.setBudgetClassId(new_budget_class_id);

			BaseModel.setCreateTime(bi);
			BaseModel.setUpdateTime(bi);
			re = bi.save();
			
			int budget_item_id = bi.getId();
			//从模板复制budget item amount 
			BudgetItemAmount bia = BudgetItemAmount.dao.findById(id);
			bia.setId(budget_item_id);
			//金额均为默认值0
			bia.save();
		}
		return re;
	}
	/**
	 * 获得封面信息
	 * @return
	 * @param budget_id
	 * @throws Exception
	 */
	public static Record get_cover_info(int budget_id) throws Exception{
//		String sql=	" select b.version,bp.name as package_name,o.order_name,cs1.name as designer_name,cs1.mobile as designer_mobile "
//				  + " ,cc.name as customer_name,cc.mobile as customer_mobile,ocs.structure,oi.box_area,b.sn as budget_sn "
//				  + " from budget b "
//				  + " left join `order` o on b.order_id=o.id "
//				  + " left join order_info oi on o.id=oi.id "
//				  + " left join budget_package bp on oi.contract_package_id=bp.id "
//				  + " left join crm_staff cs1 on cs1.id=o.designer_id "
//				  + " left join crm_customer cc on o.customer_id=cc.id "
//				  + " left join order_construction_site ocs on ocs.id = o.construction_site_id "
//				  + " where b.id=? ";
		String sql = "select b.*"
				+" from budget b"
				+" where b.id=?";
		return Db.findFirst(sql, budget_id);
	}
}
