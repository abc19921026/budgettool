package app.models.budget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import app.dao.Budget;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

public class BudgetModel extends Budget{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Map<String, Object> get_json_budget_list(int rows, int page,String sn, String startdate, String enddate) throws Exception{
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
}
