package system.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import system.aop.Permission;
import system.core.BaseController;
import system.dao.Todo;
import system.models.TodoModel;

@Permission("")
public class IndexController extends BaseController{

	public void index() throws Exception{
		//my_redirect("/coming_soon/index.html");
		//TODO 获得待办事宜list
		int dev = getParaToInt("dev", 0);
		setAttr("dev", dev);
		
		int uid=current_user_id();
		List<Todo> todos=TodoModel.get_todo_list(uid,TodoModel.status_todo);
		setAttr("todos", todos);
		
		List<Todo> hastodos=TodoModel.get_todo_list(uid,TodoModel.status_hasdao);
		setAttr("hastodos", hastodos);
		//统计一段时间内到店数，协议数，合同数，合同金额
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date before = calendar.getTime();
		String yesterday = sdf.format(before);
		setAttr("yesterday", yesterday);
		
		String start_date = getPara("start_date", yesterday.substring(0, 4)+"-01-01");//@TODO 改成当年第一天
		String end_date = getPara("end_date",yesterday);
		setAttr("start_date", start_date);
		setAttr("end_date", end_date);
		
		
		/*Long total_arrival = MarketingRecordArrivalModel.get_arrival_sum(start_date, end_date);
		setAttr("total_arrival", total_arrival);
		
		Record agreement = OrderContractModel.get_contract_sum(start_date, end_date, 2);
		setAttr("agreement_sum", agreement.get("sum"));
		
		Record  contract = OrderContractModel.get_contract_sum(start_date, end_date, 0);
		setAttr("contract_sum", contract.get("sum"));
		
		BigDecimal contract_amount_sum = contract.getBigDecimal("amount_sum");		
		double cas =(double) Math.round(contract_amount_sum.doubleValue()/1000.0);
		setAttr("contract_amount_sum",cas/10);
		//到店数，协议数，合同数，合同金额报表
		
		
		List<Record> day_arrival = MarketingRecordArrivalModel.get_day_arrival();
		List<Record> month_arrival = MarketingRecordArrivalModel.get_month_arrival(yesterday);
		setAttr("day_arrival", day_arrival);
		setAttr("month_arrival", month_arrival);
		Long month_arrival_sum = MarketingRecordArrivalModel.get_one_month_arrival(sdf.format(date).substring(0, 7),yesterday);
		Long year_arrival_sum = MarketingRecordArrivalModel.get_one_year_arrival(sdf.format(date).substring(0, 4),yesterday);
		setAttr("month_arrival_sum", month_arrival_sum);
		setAttr("year_arrival_sum", year_arrival_sum);
		
		List<Record> day_agreement = OrderContractModel.get_day_contract(2);
		List<Record> month_agreement = OrderContractModel.get_month_contract(2,yesterday);
		setAttr("day_agreement",day_agreement);
		setAttr("month_agreement", month_agreement);
		OrderContract oc_agreement_month = OrderContractModel.get_one_month_contract(sdf.format(date).substring(0, 7), 2,yesterday);
		OrderContract oc_agreement_year = OrderContractModel.get_one_year_contract(sdf.format(date).substring(0, 4), 2,yesterday);
		setAttr("month_agreement_sum", oc_agreement_month.get("sum"));
		setAttr("year_agreement_sum", oc_agreement_year.get("sum"));
		
		List<Record> day_contract = OrderContractModel.get_day_contract(0);
		List<Record> month_contract = OrderContractModel.get_month_contract(0,yesterday);
		setAttr("day_contract",day_contract);
		setAttr("month_contract", month_contract);
		OrderContract oc_contract_month = OrderContractModel.get_one_month_contract(sdf.format(date).substring(0, 7), 0,yesterday);
		OrderContract oc_contract_year = OrderContractModel.get_one_year_contract(sdf.format(date).substring(0, 4), 0,yesterday );
		setAttr("month_contract_sum", oc_contract_month.get("sum"));
		setAttr("year_contract_sum", oc_contract_year.get("sum"));
		setAttr("month_contract_amount_sum", Math.round(( oc_contract_month.getBigDecimal("amount_sum")).doubleValue()/100.0)/100.0);
		setAttr("year_contract_amount_sum",  Math.round(( oc_contract_year.getBigDecimal("amount_sum")).doubleValue()/100.0)/100.0);
		//积分显示
		CrmStaff staff = StaffModel.get_staff_by_uid(uid);

		if(staff!=null){
			setAttr("staff_id", staff.getId());
			if(RewardPointsInfoModel.get_RewardPoints_ByStaffId(staff.getId())!=null){
				setAttr("totalpoints", RewardPointsInfoModel.get_RewardPoints_ByStaffId(staff.getId()).getPoints());
				setAttr("addpoints", RewardPointsTransactionModel.points_total(staff.getId(), sdf.format(date).substring(0, 7)+"-01", sdf.format(date), "", ""));
			}
		}*/
		set_head_title("首页");
		render("index.html");
	}
	public void dashboard(){
		//renderText("dashboard");
		my_redirect("/");
	}
}
