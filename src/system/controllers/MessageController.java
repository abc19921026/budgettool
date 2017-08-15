package system.controllers;
import java.util.ArrayList;
import java.util.List;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import system.core.BaseController;
import system.dao.Message;
import system.tools.PageHandler;

/**
 * 关于系统消息的操作
 * 
 * @author libo
 * 
 */
public class MessageController extends BaseController {
	// 跳转到主页
	public void index() {
		// 从前端获取当前页数，如果获取不到就默认为第一页
		int page = getParaToInt("page", 1);
		// 查看未读信息
		int status = getParaToInt("status", -1);
		// 获取活动的类型
		int type = getParaToInt("type", -1);
		if (type > -1) {
			setAttr("type", type);
		} else {
			setAttr("status", status);
		}
		 String info=getPara("info","");
		// 获取数据的总数
		List<Message> list1 = Message.dao.find("select * from system_message");
		int inbox_length = list1.size();

		// 获取活动消息的总条数
		List<Message> list2 = Message.dao.find(
				"select * from system_message where type = ? ", 0);
		int inbox_activity = list2.size();

		// 获得通知消息的总条数
		List<Message> list3 = Message.dao.find(
				"select * from system_message where type = ? ", 1);
		int inbox_info = list3.size();

		// 获得未读消息的总条数
		List<Message> list4 = Message.dao.find(
				"select * from system_message where status = ? ", 0);
		int unRead_length = list4.size();
		// 首先默认刚开始是展示所有的消息

		String select = "select sm.* ";
		StringBuffer sqlExceptSelect = new StringBuffer(
				" from system_message sm  ");
		sqlExceptSelect.append(" where 1 = 1");
		List<Object> param = new ArrayList<Object>();
		if (status > -1) {
			sqlExceptSelect.append(" AND sm.status = ? ");
			param.add(status);
			setAttr("title", "未读消息");
		}else{
			setAttr("title", "收件箱");
		}
		
		if ((type > -1) && (type == 0)) {
			sqlExceptSelect.append(" AND sm.type = ? ");
			param.add(type);
			setAttr("title", "促销活动");
		} else if ((type > -1) && (type == 1)) {
			sqlExceptSelect.append(" AND sm.type = ? ");
			param.add(type);
			setAttr("title", "通知");
		}
		 if(StrKit.notBlank(info)){
		 sqlExceptSelect.append(" AND sm.title =  ? "); 
		 param.add(info);
		 setAttr("info", info);
		 }
		 sqlExceptSelect.append(" ORDER BY created DESC ");
		int pageSize = 5;
		Page<Message> system_message = Message.dao.paginate(page, pageSize,
				select, sqlExceptSelect.toString(), param.toArray());
		// 将原生分页类返回值做为参数传入封装的分页类，得到封装好的分页信息
		List<Message> list = system_message.getList();
		int from = (page - 1) * pageSize + 1;
		int to = page * pageSize;
		PageHandler pageHandler = new PageHandler(system_message);
		pageHandler.setBasePathUrl("/inbox");
		setAttr("from", from);
		setAttr("to", to);
		setAttr("totalpage", system_message.getTotalPage());
		setAttr("totalrow", system_message.getTotalRow());
		setAttr("list", list);
		setAttr("pageHandler", pageHandler);
		setAttr("inbox_length", inbox_length);
		setAttr("inbox_activity", inbox_activity);
		setAttr("inbox_info", inbox_info);
		setAttr("inbox_unRead", unRead_length);
		setAttr("page", page);
		render("inbox.html");
	}

	// 删除消息
	public void deleteInbox() {
		String ids = getPara("ids");
		if (StrKit.notBlank(ids)) {
			String[] deletedId = ids.split(",");
			boolean succeed = false;
			for (String id : deletedId) {
				succeed = Message.dao.deleteById(id);
			}
			if (succeed) {
				render_success_message("删除成功");
			} else {
				render_success_message("删除失败");
			}
		} else {
			renderError(404);
		}
	}

	// 把消息标记为已读
	public void isReadInbox() {
		String ids = getPara("ids");
		if (StrKit.notBlank(ids)) {
			String[] isReadId = ids.split(",");
			for (String id : isReadId) {
				Message message = Message.dao.findById(id);
				message.set("status", 1).update();
			}
		}
		render_success_message("标记成功");
	}

	// 跳转到每条消息的详情页
	public void app_inbox_view() {
		// 首先我们要获取当前信息的id
		int message_id = getParaToInt("message_id", 1);
		Message message = Message.dao.findById(message_id);
		setAttr("message", message);
		render("app_inbox_view.html");
	}
	//跳转到新建页面
	public void app_inbox_compose(){
		render("app_inbox_compose.html");
	}
	//跳转到回复页面
	public void app_inbox_reply(){
		render("app_inbox_reply.html");
	}
}
