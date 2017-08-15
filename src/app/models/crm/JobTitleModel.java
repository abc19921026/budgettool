package app.models.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dao.CrmDepartment;
import app.dao.CrmJobTitle;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class JobTitleModel extends CrmJobTitle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1343699122086386576L;

	/*public static Object get_job_list(int rows, int page, int department,String name) throws Exception{
		// TODO Auto-generated method stub
		String select = "SELECT cjt.* ,cd.name as department_name ";
		String sqlExceptSelect = "FROM crm_job_title cjt LEFT JOIN crm_department cd ON cd.id = cjt.department "
				+ "WHERE (cjt.name LIKE ? OR '0' = ?) AND (cjt.department = ? OR 0 in (?)) ORDER BY cjt.id ";
		Page<CrmJobTitle> pages;
		
		pages = CrmJobTitle.dao.paginate(page, rows, select, sqlExceptSelect,"%"+name+"%","%"+name+"%",department,department);
		
		int total = pages.getTotalRow();
		List<CrmJobTitle> list = pages.getList();
		Map<String,Object> jsonMap = new HashMap<String,Object>();
		jsonMap.put("total", total);
		jsonMap.put("rows", list);
		return jsonMap;//此处最好返回jsonMap方便以后继续处理数据
	}*/
	
	/**
	 * 获取一级部门
	 * @return
	 * @throws Exception
	 */
	public static List<CrmDepartment> get_department() throws Exception{
		// TODO Auto-generated method stub
		String sql = "SELECT concat('cd_', id) as id, id as real_id, name, 'catalog' as type "
				+ " FROM crm_department WHERE pid = 0 ORDER BY id ";//id冲突处理
		List<CrmDepartment> re = CrmDepartment.dao.find(sql);
		return re;
	}
	/**
	 * 部门对应的职位
	 * @param real_id
	 * @param department
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static List<CrmJobTitle> get_job_title_list(int real_id,int department, String name) throws Exception{
		// TODO Auto-generated method stub
		String sql = "SELECT cjt.* from crm_job_title cjt WHERE cjt.department = ? "
				+ " AND (cjt.name LIKE ? OR '0' = ?) AND (cjt.department = ? OR 0 in (?)) ORDER BY cjt.id ";
		List<CrmJobTitle> re = CrmJobTitle.dao.find(sql,real_id,"%"+name+"%","%"+name+"%",department,department);
		return re;
	}

	public static CrmJobTitle get_job_title(int id) throws Exception{
		// TODO Auto-generated method stub
		//CrmJobTitle cjt = CrmJobTitle.dao.findById(id);
		String sql = "SELECT cjt.*, cd.name as department_name FROM crm_job_title cjt "
				+ "LEFT JOIN crm_department cd ON cd.id = cjt.department where cjt.id = ? ";
		CrmJobTitle cjt = CrmJobTitle.dao.findFirst(sql, id);
		return cjt;
	}
	
	public static List<Record> get_department(String q) {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "";
			if(StrKit.notBlank(q)){
				sql = "SELECT cd.id as id,cd.name as text "
						+ "FROM crm_department cd "
						+ "WHERE name LIKE ? AND pid = 0 " + "ORDER BY cd.id ASC ";
				re = Db.find(sql, q + "%");
			}else{
				sql = "SELECT cd.id as id,cd.name as text "
						+ "FROM crm_department cd WHERE pid = 0 " + "ORDER BY cd.id ASC";
				re = Db.find(sql);
			}
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据职位名称查询职位
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static CrmJobTitle get_job_title(String name) throws Exception{
		String sql=" select * from crm_job_title where name=? limit 1";
		return CrmJobTitle.dao.findFirst(sql, name);
	}
}
