package app.models.budget;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import app.dao.BudgetClass;

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
}
