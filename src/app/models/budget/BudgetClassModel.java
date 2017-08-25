package app.models.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.javaflow.bytecode.transformation.bcel.BcelClassTransformer;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.swing.internal.plaf.metal.resources.metal;

import app.dao.BudgetClass;
import app.dao.BudgetItem;

public class BudgetClassModel extends BudgetClass{
	/**
	 * 根据预算id查询分类个数
	 * @param budget_id
	 * @return
	 */
	public static int get_budget_class_num_by_budget_id(int budget_id)throws Exception{
		String sql = "select count(*) as total from budget_class where budget_id = ?";
		Record re = Db.findFirst(sql, budget_id);
		
		int total = re.getLong("total").intValue();
		return total;
	}
	/**
	 * 删除某个预算的budgetclass
	 * @param budget_id
	 * @return
	 * @throws Exception
	 */
	public static int delete_budget_class_by_budget_id(int budget_id)throws Exception{
		int re = 0;
		String sql = "delete from budget_class where budget_id = ?";
		re = Db.update(sql, budget_id);
		return re;
	}
	
	public static List<BudgetClass> get_budget_class(int budget_id, int section) {
		try {
			String sql = "select b_c.*, b_c.name as text "
					+ "from budget_class b_c where parent_id = 0 "
					+ "and budget_id = ? and section = ? order by b_c.weight asc";
			List<BudgetClass> re = dao.find(sql, budget_id, section);

			return re;
		} catch (Exception e) {
			return null;
		}
	}	
	
	public static List<BudgetClass> get_budget_subclass(int budget_class_id) {
		try {
			String sql = "select b_c.*, b_c.name as text from budget_class b_c where parent_id = ? "
					+ "order by b_c.weight asc";
			List<BudgetClass> re = dao.find(sql, budget_class_id);

			return re;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static BudgetClass get_budget_class_with_parent(int id) {
		try {
			String sql = "select b_c.*, b_c_2.name as parent_name, b_c_2.sn as parent_sn "
					+ "from budget_class b_c "
					+ "left join budget_class b_c_2 on b_c.parent_id = b_c_2.id "
					+ "where b_c.id = ?";
			BudgetClass re = dao.findFirst(sql, id);

			return re;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 小计
	 * 
	 * @param budget_class_id
	 *            预算分类id
	 * @return
	 * @throws Exception
	 */
	public static boolean subtotal(int budget_class_id){
		if(budget_class_id <= 0){
			return false;
		}
		boolean result = false;
		BigDecimal total;
		int budget_id = 0;
		String sql = "SELECT SUM(amount) AS total FROM budget_item WHERE budget_class_id = ?";
		Record re = Db.findFirst(sql, budget_class_id);
		total = re.getBigDecimal("total");
		
		BudgetClass bc = BudgetClass.dao.findById(budget_class_id);
		budget_id = bc.getBudgetId();

		result = bc.set("subtotal", total).update();
		
		int budget_parent_id = bc.getParentId();
		if(budget_parent_id > 0){
			sql = "SELECT SUM(subtotal) AS total FROM budget_class WHERE parent_id = ?";
			re = Db.findFirst(sql, budget_parent_id);
			total = re.getBigDecimal("total");
			BudgetClass.dao.findById(budget_parent_id).set("subtotal", total).update();
		}
		BudgetModel.total(budget_id);	
		return result;
	}
	public static Integer get_max_weight_in_budget_class(Integer budget_id,Integer budget_class_id)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql =new StringBuffer("select max(weight) from budget_class bc where 1=1");
		if(budget_id!=null){
			sql.append(" and bc.budget_id = ?");
			param.add(budget_id);
		}
		if(budget_class_id!=null){
			sql.append(" and bc.parent_id = ?");
			param.add(budget_class_id);
		}
		Integer max = Db.queryInt(sql.toString(), param.toArray());
		if(max==null){
			return 0;
		}else{
			return max;
		}
	}
	
	public static Integer get_max_no_in_budget_class(Integer budget_id,Integer budget_class_id)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql =new StringBuffer("select max(no) from budget_class bc where 1=1");
		if(budget_id!=null){
			sql.append(" and bc.budget_id = ?");
			param.add(budget_id);
		}
		if(budget_class_id!=null){
			sql.append(" and bc.parent_id = ?");
			param.add(budget_class_id);
		}
		Integer max = Db.queryInt(sql.toString(), param.toArray());
		if(max==null){
			return 0;
		}else{
			return max;
		}
	}
	
	public static Long get_num_in_budget_class(Integer budget_id,Integer budget_class_id,Integer no)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql =new StringBuffer("select count(id) from budget_class bc where 1=1");
		if(budget_id!=null){
			sql.append(" and bc.budget_id = ? and bc.parent_id=0");
			param.add(budget_id);
		}
		if(budget_class_id!=null){
			sql.append(" and bc.parent_id = ?");
			param.add(budget_class_id);
		}
		if(no!=null){
			sql.append(" and bc.no < ?");
			param.add(no);
		}
		Long num = Db.queryLong(sql.toString(), param.toArray());
		if(num==null){
			return (long) 0;
		}else{
			return num;
		}
	}
	
	public static void change_budget_class_sn(Integer budget_id,Integer budget_class_id,Integer no)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql =new StringBuffer("select bc.* from budget_class bc where bc.no> ?");
		param.add(no);
		if(budget_id!=null){
			sql.append(" and bc.budget_id = ? and bc.parent_id = 0");
			param.add(budget_id);
		}
		if(budget_class_id!=null){
			sql.append(" and bc.parent_id = ?");
			param.add(budget_class_id);
		}
		sql.append(" order by bc.no asc");
		List<BudgetClass> list = BudgetClass.dao.find(sql.toString(), param.toArray());
		if(list.size()>0){
			if(budget_id!=null){
				Long num = get_num_in_budget_class(budget_id, null, no);
				for(BudgetClass bc : list){
					num += 1;
					bc.setSn(num.toString());
					bc.update();
					if(BudgetClassModel.get_budget_subclass(bc.getId()).size() > 0){
						change_budget_subclass_sn(bc.getId(), num);
					}else{
						change_budget_item_sn_by_budget_class(bc.getId());
					}					
				}
			}else if(budget_class_id!=null){
				Long num = get_num_in_budget_class(null, budget_class_id, no);
				String sn = BudgetClass.dao.findById(budget_class_id).getSn();
				for(BudgetClass bc : list){
					num += 1;
					bc.setSn(sn+"."+num);
					bc.update();
					if(BudgetItemModel.get_budget_item_num_by_class(bc.getId()) > 0){
						change_budget_item_sn_by_budget_class(bc.getId());
					}
				}
			}
		}
	}
	
	public static void change_budget_subclass_sn(Integer budget_class_id,Long sn)throws Exception{
		String sql = "select * from budget_class where parent_id = ?";
		List<BudgetClass> list = BudgetClass.dao.find(sql, budget_class_id);
		if(list.size()>0){
			for(BudgetClass bc : list){
				String bc_sn = bc.getSn();
				String[] split = bc_sn.split("\\.");
				bc.setSn(sn+"."+split[1]);
				bc.update();
				if(BudgetItemModel.get_budget_item_num_by_class(bc.getId()) > 0){
					change_budget_item_sn_by_budget_class(bc.getId());
				}
			}
		}
	}
	
	public static void change_budget_item_sn_by_budget_class(Integer budget_class_id)throws Exception{
		BudgetClass bc = BudgetClass.dao.findById(budget_class_id);
		String select = "select bi.* from budget_item bi where bi.budget_class_id = ? order by bi.no asc";
		List<BudgetItem> bi_list = BudgetItem.dao.find(select, budget_class_id);
		for(BudgetItem bi: bi_list){
			bi.setSn(bc.getSn()+"-"+bi.getSn().split("-")[1]);
			bi.update();
		}
	}
}
