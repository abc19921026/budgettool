package app.dao;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("b_t_addr_city", "id", BTAddrCity.class);
		arp.addMapping("b_t_addr_district", "id", BTAddrDistrict.class);
		arp.addMapping("b_t_addr_province", "id", BTAddrProvince.class);
		arp.addMapping("b_t_scale", "id", BTScale.class);
		arp.addMapping("company_info", "id", CompanyInfo.class);
		arp.addMapping("contact_form", "id", ContactForm.class);
		arp.addMapping("contact_info", "id", ContactInfo.class);
		arp.addMapping("crm_department", "id", CrmDepartment.class);
		arp.addMapping("crm_job_title", "id", CrmJobTitle.class);
		arp.addMapping("crm_staff", "id", CrmStaff.class);
		arp.addMapping("crm_staff_extra", "id", CrmStaffExtra.class);
		arp.addMapping("marketing_record", "id", MarketingRecord.class);
		arp.addMapping("marketing_record_follow", "id", MarketingRecordFollow.class);
		arp.addMapping("marketing_sale_source", "id", MarketingSaleSource.class);
		arp.addMapping("marketing_sale_source_type", "id", MarketingSaleSourceType.class);
	}
}

