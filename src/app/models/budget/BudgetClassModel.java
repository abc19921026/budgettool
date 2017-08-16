package app.models.budget;

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
}
