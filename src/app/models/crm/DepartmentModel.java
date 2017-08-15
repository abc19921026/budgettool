package app.models.crm;

import java.util.List;

import app.dao.CrmDepartment;
import app.dao.CrmStaff;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class DepartmentModel extends CrmDepartment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6441263719689317498L;

	/**
	 * 一级部门
	 * @return
	 */
	public static List<CrmDepartment> get_department_class() {
		// TODO Auto-generated method stub
		try {
			String sql = "select * from crm_department where pid = 0  order by id asc";
			List<CrmDepartment> re = dao.find(sql);
			return re;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 一级部门下的子部门pid
	 * @param id
	 * @return
	 */
	public static List<CrmDepartment> get_department_subclass(Integer id) {
		// TODO Auto-generated method stub
		try {
			String sql = "select * from crm_department where pid = ? order by id asc";
			List<CrmDepartment> re = dao.find(sql, id);
			return re;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 递归判断是否有下级部门
	 * @param re
	 * @return
	 */
	public static List<CrmDepartment> get_department_recursive(List<CrmDepartment> re) {
		// TODO Auto-generated method stub
		for(int i = 0; i < re.size(); i++){
			List<CrmDepartment> departmentSubClass = DepartmentModel.get_department_subclass(re.get(i).getId());//下级部门
			if(departmentSubClass.size() > 0){
				re.get(i).put("children", get_department_recursive(departmentSubClass));
			}
		}
		return re;
	}
	
	/**
	 * 获取部门详情
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static CrmDepartment get_department(int id) throws Exception{
		// TODO Auto-generated method stub
		String sql = "select d1.*, d2.name as parent_name from crm_department d1 left join crm_department d2 on d1.pid = d2.id where d1.id = ? ";
		CrmDepartment cd = CrmDepartment.dao.findFirst(sql,id);
		return cd;
	}
	
	/**
	 * select2员工匹配部门
	 * @param q
	 * @return
	 */
	public static List<Record> get_staff_department_name(String q) {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "";
			
			if(StrKit.notBlank(q)){
				sql = "select d.id as id, d.name as text "
						+ "FROM crm_department d where d.pid = 0 "
						+ " AND d.name like ? " + " order by d.id asc";
				re = Db.find(sql, "%"+ q + "%");
			}else{
			sql = "select d.id as id, d.name as text "
					+ "FROM crm_department d  where d.pid = 0 order by d.id asc";
			re = Db.find(sql);
			}
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<Record> get_staff_department_name(int pid, String q) {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "";
			if(StrKit.notBlank(q)){
				sql = "select d.id as id, d.name as text "
						+ "FROM crm_department d where d.pid = ? "
						+ " AND d.name like ? " + " order by d.id asc";
				re = Db.find(sql, pid, "%"+ q + "%");
			}else{
				sql = "select d.id as id, d.name as text "
						+ "FROM crm_department d where d.pid = ? " + "order by d.id asc";
				re = Db.find(sql, pid);
			}
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<Record> get_staff_name(String q, int id) {
		// TODO Auto-generated method stub
		List<Record> re = null;
		String sql = "";
		if(StrKit.notBlank(q)){
			sql = "select cs.id as id, cs.name as text from crm_staff cs "
					+ " where cs.name like ? and cs.departments = ? and status <> -1 " + " order by cs.id asc";
			re = Db.find(sql,"%"+q+"%",id);
		}else{
			sql = "select cs.id as id, cs.name as text from crm_staff cs where cs.departments = ? and status <> -1 order by cs.id asc ";
			re = Db.find(sql,id);
		}
		return re;
	}
	
	//*************************************************************************
	public static String get_departments_name(int staff_id){		
		String department_name = "";
		List<CrmDepartment> cds = get_departments_info(staff_id);
		if(cds==null){
			return "";
		}else{
			for(int i=0;i<cds.size();i++){
				if(i == cds.size()-1){
					department_name += cds.get(i).getName();
				}else{
					department_name += cds.get(i).getName()+",";
				}
			}
			return department_name;
		}
		
	}
	
	public static List<CrmDepartment> get_departments_info(int staff_id){
		CrmStaff cs = CrmStaff.dao.findById(staff_id);
		String departments = cs.getDepartments();
		if(departments==null||departments.equals("")){
			return null;
		}else{
			StringBuffer sql = new StringBuffer("select cd.id, cd.name from crm_department cd where cd.id in (");
			sql.append(departments);
			sql.append(")");
			System.out.println(sql);
			List<CrmDepartment> crmDepartments = CrmDepartment.dao.find(sql.toString());
			return crmDepartments;
		}
	}
	
	/**
	 * 根据token获得部门集合
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static List<CrmDepartment> get_department_list(String token) throws Exception{
		String sql=" select * from crm_department where find_in_set(query_token,?) ";
		return CrmDepartment.dao.find(sql, token);
	}
}
