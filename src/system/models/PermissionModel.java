package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import system.dao.Permission;

public class PermissionModel extends Permission {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4184394260631452451L;
	
	/**
	 * 列表加载
	 * @param rows
	 * @param page
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> get_permission_list(int rows, int page, String title) throws Exception{
		// TODO Auto-generated method stub
		String select = "SELECT * ";
		StringBuffer sqlExceptSelect = new StringBuffer(" FROM system_permission WHERE 1=1 ");
		List<Object> param = new ArrayList<Object>();
		if(StrKit.notBlank(title)){
			sqlExceptSelect.append(" AND title LIKE ? ");
			param.add("%"+title+"%");
		}
		sqlExceptSelect.append(" ORDER BY weight ");
		
		Page<Record> pages = Db.paginate(page, rows, select, sqlExceptSelect.toString(),param.toArray());
		int total = pages.getTotalRow();
		List<Record> list = pages.getList();
		Map<String,Object> jsonMap = new HashMap<String,Object>();
		jsonMap.put("total", total);
		jsonMap.put("rows", list);
		return jsonMap;//此处最好返回jsonMap方便以后继续处理数据
	}
	
	public static List<Permission> get_permission_tree(String title) throws Exception {
		List<Permission> result = new ArrayList<Permission>();
		String sql_module = "select module from system_permission group by module";
		List<String> list_module = Db.query(sql_module);
		
		List<Permission> list_p = null;
		String sql = "select * from system_permission";
		if(StrKit.notBlank(title)){
			sql += " where title like ?";
			list_p = Permission.dao.find(sql, "%"+title+"%");
		}else
			list_p = Permission.dao.find(sql);
		
		int temp_id = -1;
		for(String module : list_module){
			Permission permission_module = new Permission();
			List<Permission> list_children = new ArrayList<Permission>();
			
			for(Permission permission : list_p){
				if(module == null && permission.getModule() == null){
					permission_module.setName("系统默认");
					list_children.add(permission);
				}else if(module != null && module.equals(permission.getModule())){
					permission_module.setName(module);
					list_children.add(permission);
				}
			}
			if(list_children.size() > 0){
				permission_module.setId(temp_id);
				permission_module.put("children", list_children);
				result.add(permission_module);
				temp_id--;
			}
		}
				
		return result;
	}
	
	/**
	 * 数据详情
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static Permission get_permission_detail(int id) throws Exception{
		// TODO Auto-generated method stub
		Permission permission = Permission.dao.findById(id);
		return permission;
	}
	
	/**
	 * 更新排序号
	 * @return
	 * @throws Exception
	 */
	public static boolean update_permission_weight() throws Exception{
		String sql = "select * from system_permission  order by weight asc";
		List<Permission> re = dao.find(sql);
		Boolean result = false;
		for (int i = 0; i < re.size(); i++) {
			int id = re.get(i).getId();
			int weight = 10 + 10 * i;

			result = Permission.dao.findById(id).set("weight", weight).update();

		}
		return result;
	}


	public static List<Permission> toList() {
		String sql = "select * from system_permission";
		return dao.find(sql);
	}

	public static List<Permission> findPermissionByTitle(String permissionTitle) {
		String sql = "select * from system_permission where title like ? or '0' = ?";
		return dao.find(sql, "%" + permissionTitle + "%", "%" + permissionTitle + "%");
	}
	
	/**
	 * 检查所给用户是否有所给的权限
	 * @param perm
	 * @param uid
	 * @return
	 */
	public static boolean has_perm(String perm, int uid) {
		boolean re = false;
		if(uid <= 0){
			return false;
		}
		if(uid == 1){//root用户拥有所有权限
			return true;
		}
		if(StrKit.isBlank(perm)){//如果perm为空，返回真
			return true;
		}

		String rids = UserRoleModel.find_rids(uid);
		if(RolePermissionModel.check_role_permission(rids, perm)){
			re = true;
		}

		return re;
	}

	
}
