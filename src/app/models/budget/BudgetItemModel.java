package app.models.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

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
	
	/**
	 * 按不同类别加载，0基础工程1主材2优惠
	 * @param q
	 * @param section
	 * @return
	 */
	public static List<Record> get_class_catalog(String q, int section,int type,int parent_id)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select bc.name as id, bc.name as text ");
			sql.append(" FROM budget_class bc");
			sql.append(" where bc.section = ?");
			param.add(section);
		if(type==0){
			sql.append(" and bc.parent_id = 0");
		}	
		if(StrKit.notBlank(q)){
			sql.append(" and bc.name like ?");
			param.add("%"+q+"%");
		}
		if(parent_id>0){
			sql.append(" and bc.parent_id = ?");
			param.add(parent_id);
		}
			sql.append(" group by bc.name order by bc.weight asc");
		List<Record> re = Db.find(sql.toString(), param.toArray());
		return re;
	}	
	
	public static List<Record> get_same_class_catalog(String name)throws Exception{
		String sql = "select * from budget_class "
				+" where name = ?";
		List<Record> list = Db.find(sql, name);
		return list;
	}	
	
	/**
	 * 根据预算分类id查询分类下的工程项目个数
	 * @param budget_class_id
	 * @return
	 */
	public static int get_budget_item_num_by_class(int budget_class_id)throws Exception{
		String sql = "select count(*) as total from budget_item where budget_class_id = ?";
		Record re = Db.findFirst(sql, budget_class_id);
		int total = re.getLong("total").intValue();
		return total;
	}
}
