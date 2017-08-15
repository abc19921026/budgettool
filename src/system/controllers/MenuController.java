package system.controllers;

import java.util.List;

import system.aop.Permission;
import system.core.BaseController;
import system.dao.MenuLink;
import system.models.MenuModel;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

@Permission("access menu")
public class MenuController extends BaseController {


	Integer page;
	Integer rows;
	
	public void index() throws Exception{
		int uid = check_user_login(true);
		//Console.log(uid);

		//if(uid <= 0){
			//user_login(2);// test mode only
		//}//用户未登录
		
		//init_page();//初始化页面
		set_head_title("菜单管理");
		render("menu_list.html");
	}
	
	public void listAll() throws Exception {
		/*page = getParaToInt("page");
		rows = getParaToInt("rows");
		if(page == null){
			page = 1;
		}
		if(rows == null){
			rows = 10;
		}
		String menus = MenuModel.toResult(page, rows);
		renderJson(menus);*/
		List<MenuLink> menus = MenuModel.toResult();
		renderJson(menus);
	}
	
	public void delete() throws Exception {
		String checked_ids = getPara("checked_ids");
		if(StrKit.notBlank(checked_ids)){
			/*if(MenuModel.detailsDelete(checked_ids)){
				renderText("删除成功");
			}else{
				renderText("删除失败");
			}*/
			String[] delete_ids = checked_ids.split(",");
			for(String id : delete_ids){
				MenuModel.dao.deleteById(id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;
		}/*else{
		}
			renderText("请选中数据");
		}*/
		//清除缓存
		CacheKit.removeAll(MenuModel.CACHE_NAME);
		render_success_message("删除成功！");
	}
	
	public void update() throws Exception {
		MenuLink ml = getModel(MenuLink.class, "systemMenuLinks");
		if(ml == null){
			renderError(404);
		}
		if(ml.getPlid() == null){
			ml.setPlid(0);
		}
		
		boolean succeed = false;
		if(ml.getId() != null){
			/*if(MenuModel.detailsUpdate(ml)){
				renderText("保存成功");
			}else{
				renderText("error");
			}*/
			succeed = ml.update();
		}else{
			//renderText("请输入链接ID");
			succeed = ml.save();
		}
		
		if(succeed){
			//清除缓存
			CacheKit.removeAll(MenuModel.CACHE_NAME);
			//重置weight, depth
			MenuModel.reset_menu_field();
			render_success_message("保存成功", 1);
		}else{
			render_error_message("保存失败，请重试.");
		}
	}
	
	public void details() throws Exception {
		Integer mlid = getParaToInt("mlid");
		MenuLink ml = new MenuLink();
		if(mlid != null){
			ml = MenuModel.dao.findById(mlid);
		}/*else{
			mlid =  new GUID().toString();
		}
		setAttr("mlid", mlid);*/
		setAttr("systemMenuLinks", ml);
		
		List<MenuLink> list_p = MenuModel.loadParent();
		setAttr("list_p", list_p);
	}
	
	public void json_load_all(){
		List<MenuLink> list_menu_link = MenuModel.load_all_menu_links(0, true);
		MenuLink default_select = new MenuLink();
		default_select.setId(0);
		default_select.put("text", "无");
		list_menu_link.add(0, default_select);//加到List最前边
		renderJson(list_menu_link);
	}
	
	public void reset() {
		MenuModel.reset_menu_field();
		render_success_message("SUCCESS");
	}
}
