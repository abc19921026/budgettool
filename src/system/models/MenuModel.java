package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;

import system.dao.MenuLink;
import system.dao.User;
import system.tools.StringTools;

public class MenuModel extends MenuLink{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String CACHE_NAME = "system_menu";//ehcache.xml配置文件中的缓存名称

	/**
	 * 
	 */


	/**
	 * 返回菜单html
	 * @param menu_name
	 * @param path
	 * @return
	 * @throws Exception
	 */
	/*public static String load_all(String current_path, String base_path){

		//先获取一级菜单
		List<MenuLink> list_menu_link = null;
		String sql = "select * from system_menu_link where depth = 1 order by weight asc";
		//使用缓存
		list_menu_link = dao.findByCache("menu", "system_menu_link", sql);
		if(list_menu_link == null){
			return "";
		}
		
		StringBuilder output = new StringBuilder();
		
		//当前路径的上级路径在菜单中需要展开
	    List<Object> open_links = menu_open_links(current_path);

		for(MenuLink menu_link : list_menu_link){
			output.append(menu_tree_output(menu_link, current_path, base_path, open_links));
		}
		return output.toString();
	}*/
	
/*	public static String load_by_user(String current_path, String base_path, int uid){
		//先获取用户角色，rid
		String role_ids = "0";//匿名用户
		
		if(uid > 1){
			//authenticated user
			//先获取用户角色，rid
			//List<String> user = UserRoleModel.findByUid(String.valueOf(uid));

			//role_ids = StringTools.implode(user);
			role_ids = UserRoleModel.find_rids(uid);
		}
		
		List<MenuLink> list_menu_link = null;

		list_menu_link = load_menu_links_by_roles(role_ids);
		if(list_menu_link == null){
			return "";
		}
		
		StringBuilder output = new StringBuilder();
		//当前路径的上级路径在菜单中需要展开
	    List<Object> open_links = menu_open_links(current_path);

		for(MenuLink menu_link : list_menu_link){
			output.append(menu_tree_output(menu_link, current_path, base_path, open_links));
		}
		return output.toString();
	}*/
	
	/**
	 * 
	 * @param current_path 不包含query string 如/admin/user?uid=123
	 * @param base_path
	 * @param uid
	 * @return
	 */
	public static String load_by_user_and_cache(String current_path, String base_path, int uid){
		//先获取用户角色，rid
		String role_ids = "0";//匿名用户
		String output = "";
		List<MenuLink> list_menu_link = null;
		
		if(uid <= 0){
			return output;
		}
		
		if(uid == 1){
			//root
			//一次性加载所有菜单到缓存对象
			list_menu_link = CacheKit.get(CACHE_NAME, "ALL");
			if (list_menu_link == null) {
				list_menu_link = load_all_menu_links(0);
				CacheKit.put(CACHE_NAME, "ALL", list_menu_link);
			}

		}else{		
			//authenticated user
			role_ids = UserRoleModel.find_rids(uid);
			//一次性加载所有菜单到缓存对象
			//list_menu_link = load_all_menu_links_by_cache(0, role_ids);
			
			list_menu_link = CacheKit.get(CACHE_NAME, role_ids);
			if (list_menu_link == null) {
				list_menu_link = load_all_menu_links(0, role_ids);
				CacheKit.put(CACHE_NAME, role_ids, list_menu_link);
			}
		
		}

		if(list_menu_link == null){
			return output;
		}
		
		//当前路径的上级路径在菜单中需要展开
	    List<Object> open_links = menu_open_links(current_path);

		/*for(MenuLink menu_link : list_menu_link){
			output.append(menu_tree_output(menu_link, current_path, base_path, open_links));
		}*/
	    output =  menu_tree_output_by_cache(list_menu_link, current_path, base_path, open_links);
		return output;
	}		
	
	/**
	 * 
	 * @param role_ids
	 * @return
	 */
	
/*	public static List<MenuLink> load_menu_links_by_roles(String role_ids){
		List<MenuLink> list_menu_link = null;
		String sql = "select * from system_menu_link ml left join system_role_menu_link rml on ml.id = rml.mlid where ml.depth = 1 and rml.rid IN(?) order by weight asc";
		
		list_menu_link = dao.find(sql, role_ids);
		
		return list_menu_link;
	}*/
	
	/**
	 * 
	 * @param role_ids
	 * @return
	 */
/*	public static List<MenuLink> load_all_menu_links_by_cache(String role_ids){
		List<MenuLink> list_menu_link = null;
		String sql = "select * from system_menu_link ml left join system_role_menu_link rml on ml.id = rml.mlid where ml.depth = 1 and rml.rid IN(?) order by weight asc";
		
		list_menu_link = dao.find(sql, role_ids);
		for(MenuLink menu_link : list_menu_link){
			int mlid = menu_link.getId();
			int has_children = menu_link.getHasChildren();
			if(has_children == 1 ){
				List<MenuLink> children = load_all_menu_links_by_cache(mlid, role_ids);
				menu_link.put("children", children);
			}
		}
		return list_menu_link;
	}*/
	
	/**
	 * 一次性按角色获取全部菜单数据，并根据plid循环
	 * @param role_ids
	 * @return
	 */
	public static List<MenuLink> load_all_menu_links(int plid, String role_ids){
		List<MenuLink> list_menu_link = null;
		String sql = "select distinct ml.* from system_menu_link ml left join system_role_menu_link rml on ml.id = rml.mlid where ml.plid = ? and rml.rid IN("+role_ids+") order by weight asc";
		
		list_menu_link = dao.find(sql, plid);
		
		for(MenuLink menu_link : list_menu_link){
			int mlid = menu_link.getId();
			int has_children = menu_link.getHasChildren();
			if(has_children == 1 ){
				//递归遍历
				List<MenuLink> children = load_all_menu_links(mlid, role_ids);
				menu_link.put("children", children);
			}
		}
		
		return list_menu_link;
	}
	
	/**
	 * 一次性获取全部菜单数据，并根据plid循环，用于为root用户显示全部菜单，以及新建菜单页的json数据
	 * @param role_ids
	 * @return
	 */
	public static List<MenuLink> load_all_menu_links(int plid){
		return load_all_menu_links(plid, false);
		/*List<MenuLink> list_menu_link = null;
		String sql = "select * from system_menu_link ml where ml.plid = ? order by weight asc";
		
		list_menu_link = dao.find(sql, plid);
		
		for(MenuLink menu_link : list_menu_link){
			int mlid = menu_link.getId();
			int has_children = menu_link.getHasChildren();
			if(has_children == 1 ){
				//递归遍历
				List<MenuLink> children = load_all_menu_links(mlid);
				menu_link.put("children", children);
			}
		}
		
		return list_menu_link;*/
	}
	
	/**
	 * 
	 * @param plid
	 * @param is_for_treegrid false默认全部数据，true则为显示id, text 的tree的数据
	 * @return
	 */
	public static List<MenuLink> load_all_menu_links(int plid, boolean is_for_treegrid){
		List<MenuLink> list_menu_link = null;
		String sql = "";
		if(is_for_treegrid){
			sql = "select id, has_children, link_title as text from system_menu_link ml where ml.plid = ? order by weight asc";			
		}else{
			sql = "select * from system_menu_link ml where ml.plid = ? order by weight asc";
		}
		
		list_menu_link = dao.find(sql, plid);
		
		for(MenuLink menu_link : list_menu_link){
			int mlid = menu_link.getId();
			int has_children = menu_link.getHasChildren();
			if(has_children == 1 ){
				//递归遍历
				List<MenuLink> children = load_all_menu_links(mlid, is_for_treegrid);
				menu_link.put("children", children);
			}
		}
		
		return list_menu_link;
	}	
	
	/**
	 * 不直接从数据库循环查，改为从缓存的对象中查询，提高运行效率
	 * @param list_menu_link
	 * @param path
	 * @param base_path
	 * @param open_links
	 * @return
	 */
	public static String menu_tree_output_by_cache(List<MenuLink> list_menu_link, String path, String base_path, List<Object> open_links)  {
		StringBuilder output = new StringBuilder();
		for(MenuLink menu_link : list_menu_link){
			int mlid = 0;
			String link_title = "";
			String link_path = "";
			int has_children = 0;
			int plid = -1;
			String icon = "";
			
			link_title = menu_link.getLinkTitle();
			plid = menu_link.getPlid();
			mlid = menu_link.getId();
			link_path = menu_link.getLinkPath();			
			has_children = menu_link.getHasChildren();
			icon = menu_link.getIcon();
			
			if(open_links != null && !open_links.isEmpty() && open_links.contains(mlid)){ //当前上级
				output.append("<li class=\"nav-item open active\">");
			}else if(StrKit.notBlank(link_path) && link_path.equals(path)){ //当前路径
				output.append("<li class=\"nav-item active\">");
			}else{
				output.append("<li class=\"nav-item\">");
			}
			
			if(has_children == 1){
				//有下级菜单
				output.append("\t<a href=\"javascript:;\"  class=\"nav-link nav-toggle\">");//有子菜单
			}else{
				//无下级菜单
				output.append("\t<a href=\""+ base_path + link_path + "\">");
			}
			
			if(StrKit.notBlank(icon)){
				output.append("<i class='"+ icon +"'></i> ");
			}
			
			if(plid == 0){
				//一级菜单
				output.append("<span class=\"title\">" + link_title + "</span>");
			}else{
				output.append(link_title);
			}
			
			if(has_children == 1){
				//生成子菜单
				
				if(open_links != null && open_links.contains(mlid)){
					//当前打开的项
					output.append("<span class=\"selected\"></span>");
					output.append("<span class=\"arrow open\"></span>");
				}else{
					//不是
					output.append("<span class=\"arrow\"></span>");
				}
				
				output.append("</a>\n");
				output.append("\t<ul class=\"sub-menu\">\n");
				//
				//String sql = "select * from system_menu_link where plid = ? order by weight asc";
				//List<MenuLink> list_menu_link = dao.find(sql, menu_link.getId());
				List<MenuLink> list_sub_menu_link = menu_link.get("children");
				//for(MenuLink sub_menu_link : list_sub_menu_link){
					//循环生成子菜单
					output.append(menu_tree_output_by_cache(list_sub_menu_link, path, base_path, open_links));
				//}
				output.append("\t</ul><!--end sub-menu-->\n");
			}else{
				//无下级菜单
				output.append("</a>");
			}

			output.append("</li>\n");
		}
		return output.toString();
		
	}	
	
	
/*	public static StringBuilder menu_tree_output(MenuLink menu_link, String path, String base_path, List<Object> open_links)  {
		StringBuilder output = new StringBuilder();
		
		//System.out.println(PathService.base_path());
		if(open_links == null)
		{
			System.out.println("got you!");
			return null;
		}
		if(open_links != null && !open_links.isEmpty() && open_links.contains(menu_link.getId())){ //当前上级
			output.append("<li class=\"nav-item open active\">");
		}else if(StrKit.notBlank(menu_link.getLinkPath()) && menu_link.getLinkPath().equals(path)){ //当前路径
			output.append("<li class=\"nav-item active\">");
		}else{
			output.append("<li class=\"nav-item\">");
		}

		if(menu_link.getHasChildren() != 1){
			output.append("\t<a href=\""+ base_path + menu_link.getLinkPath()+"\">");
		}else{
			output.append("\t<a href=\"javascript:;\"  class=\"nav-link nav-toggle\">");//有子菜单
		}
		if(menu_link.getIcon() != null){
			output.append("<i class='"+menu_link.getIcon()+"'></i> ");
		}
		if(menu_link.getDepth() !=null && menu_link.getDepth() == 1){
			output.append("<span class=\"title\">" + menu_link.getLinkTitle() + "</span>");
		}else{
			output.append(menu_link.getLinkTitle());
		}
		
		
		if(menu_link.getHasChildren() == 1){
			//生成子菜单
			
			if(open_links != null && open_links.contains(menu_link.getId())){
				//当前打开的项
				output.append("<span class=\"selected\"></span>");
				output.append("<span class=\"arrow open\"></span>");
			}else{
				//不是
				output.append("<span class=\"arrow\"></span>");
			}
			
			output.append("</a>\n");
			output.append("\t<ul class=\"sub-menu\">\n");
			//
			String sql = "select * from system_menu_link where plid = ? order by weight asc";
			List<MenuLink> list_menu_link = dao.find(sql, menu_link.getId());
			for(MenuLink sub_menu_link : list_menu_link){
				//循环生成子菜单
				output.append(menu_tree_output(sub_menu_link, path, base_path, open_links));
			}
			output.append("\t</ul><!--end sub-menu-->\n");
			
		}else{
			//无子菜单，结束
			output.append("</a>");
		}
		
		output.append("</li>\n");
		return output;
	}*/
	
	
	/**
	 * 根据当前路径，列出上级所有路径设置成打开状态，下一步改成从内存中的list_menu_links中获取数据而不是从数据库中查
	 * @param path
	 * @return
	 */
	private static List<Object> menu_open_links(String path) {
		if( StrKit.isBlank(path)){
			return null;
		}
		List<Object> menu_open_links = new ArrayList<Object>();

		String sql = "select * from system_menu_link where link_path = ?";
		List<MenuLink> list_menu_link = dao.find(sql, path);
		if(list_menu_link.size() <= 0){
			return null;
		}
		for(MenuLink menu_link : list_menu_link){
			//String sql_plid = "select * from system_menu_link where mlid = ?";
			//List<SystemMenuLinks> list_parent_menu_link = SystemMenuLinks.dao.find(sql_plid, menu_link.getMlid());
			//menu_open_links.add(menu_link.getMlid());
			//int depth = menu_link.getDepth();
			MenuLink system_menu_link = dao.findById(menu_link.getId());
			menu_open_links.add(system_menu_link.getPlid());
			MenuLink system_menu_link_3 = dao.findById(system_menu_link.getPlid());
			if(system_menu_link_3 != null){
				menu_open_links.add(system_menu_link_3.getPlid());
				//现在只考虑三级
			}
		}
		return menu_open_links;
	}
	
	public static List<MenuLink> toResult() throws Exception {
		/*Page<MenuLink> pages;
		String sql = "select m1.*, m2.link_title as p_title ";
		String sqlex = " from system_menu_link m1 left join system_menu_link m2 "
				+ "on m1.plid=m2.id ";
		pages = dao.paginate(page, rows, sql, sqlex);
		int total = pages.getTotalRow();
		List<MenuLink> list = pages.getList();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("total", total);
		jsonMap.put("rows", list);
		String result = JsonKit.toJson(jsonMap);
		return result;*/
		String sql_top = "select * from system_menu_link where plid=0 order by weight";
		List<MenuLink> list_top = MenuLink.dao.find(sql_top);
		for(MenuLink menu_link : list_top){
			menu_link = putChildren(menu_link);
		}
		
		return list_top;
	}

	public static MenuLink putChildren(MenuLink menu_link) throws Exception {
		//根据has_children属性判断是否含有下级菜单
		int has_children = menu_link.getHasChildren();
		if(has_children == 1){
			String sql_children = "select * from system_menu_link where plid=? order by weight";
			List<MenuLink> list_children = dao.find(sql_children, menu_link.getId());
			//循环处理下级菜单
			for(MenuLink menu_children : list_children){
				menu_children = putChildren(menu_children);
			}
			//下级菜单放入children属性下
			menu_link.put("children", list_children);
		}
		return menu_link;
	}
	
	public static boolean detailsDelete(String checkedIds) throws Exception {
		String[] mlids = checkedIds.split(",");
		for(String mlid : mlids){
			if(dao.deleteById(mlid)){
				
			}else{
				return false;
			}
		}
		return true;
	}
	
	public static List<MenuLink> loadParent() throws Exception {
		List<MenuLink> list_p = new ArrayList<MenuLink>();
		String sql = "select * from system_menu_link where has_children = 1 order by weight asc";
		list_p = dao.find(sql);
		return list_p;
	}	
	
	/**
	 * 重新设置菜单的weight排序 和 depth层级，避免直接在数据库中直接插入数据时不规范
	 */
	public static void reset_menu_field(){
		/*String sql = "select * from system_menu_link where plid = 0 order by weight asc";
		//List<MenuLink> list_menu_link = new ArrayList<MenuLink>();
		List<MenuLink> list_menu_link = dao.find(sql);
		int weight = 10;
		for(MenuLink ml: list_menu_link){
			int mlid = ml.getId();
			if(StrKit.isBlank(ml.getLinkPath())){
				ml.setLinkPath("");
			}
			ml.setWeight(weight); weight += 10;
			ml.setDepth(1);//层级
			ml.setMenuId(1);//系统菜单，现在只有系统菜单
			ml.update();
			
			sql = "select * from system_menu_link where plid = ? order by weight asc";
			List<MenuLink> list_menu_link_2 = dao.find(sql, mlid);

		}*/
		reset_menu_field(0, 0);
		CacheKit.removeAll(CACHE_NAME);
	}
	
	public static void reset_menu_field(int plid, int depth){
		String sql = "select * from system_menu_link where plid = ? order by weight asc";
		List<MenuLink> list_menu_link = dao.find(sql, plid);
		int weight = 10;
		depth++;
		int menu_id = 1;//系统菜单
		String default_link_path = "javascript:;";//默认的空白路径
		if(list_menu_link.size() > 0){
			//如果有下级，设为has_children = 1
			MenuLink parent_link = dao.findById(plid);
			if(parent_link != null){
				parent_link.setHasChildren(1);
				parent_link.update();
			}
			//下级菜单不为空
			for(MenuLink ml: list_menu_link){
				int mlid = ml.getId();
				if(StrKit.isBlank(ml.getLinkPath())){
					ml.setLinkPath(default_link_path);
				}
				ml.setWeight(weight); weight += 10;//排序以10为间隔
				ml.setDepth(depth);//层级
				ml.setMenuId(menu_id);//系统菜单，现在只有系统菜单
				ml.update();
				
				//循环下级 
				reset_menu_field(mlid, depth);
			}
		}else{
			//如果没有下级，设为has_children = 0
			MenuLink parent_link = dao.findById(plid);
			if(parent_link != null){
				parent_link.setHasChildren(0);
				parent_link.update();
			}
		}
	}
}
