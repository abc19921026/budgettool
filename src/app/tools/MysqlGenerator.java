package app.tools;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import system.tools.MyMetaBuilder;
//下面定义的是一个发生器，它的目的是自动生成需要的model包结构，然后自己在里面定义一些业务逻辑
public class MysqlGenerator {
	public static DataSource getDataSource(){
		Prop p = PropKit.use("config.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "app.dao.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/app/dao/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "app.dao";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		generator.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		//generator.addExcludedTable("system_menu_links");
		
		MyMetaBuilder myMetaBuilder = new MyMetaBuilder(getDataSource());
		
		//基础信息维护
		myMetaBuilder.addIncludedTable("b_t_addr_province");
		myMetaBuilder.addIncludedTable("b_t_addr_city");
		myMetaBuilder.addIncludedTable("b_t_addr_district");
		myMetaBuilder.addIncludedTable("marketing_sale_source");
		myMetaBuilder.addIncludedTable("marketing_sale_source_type");
		myMetaBuilder.addIncludedTable("b_t_scale");
		
		
		//企业管理
		myMetaBuilder.addIncludedTable("crm_department");
		myMetaBuilder.addIncludedTable("crm_staff");
		myMetaBuilder.addIncludedTable("crm_staff_extra");
		myMetaBuilder.addIncludedTable("crm_job_title");
		
		//营销中心对应表
		myMetaBuilder.addIncludedTable("marketing_record");
		myMetaBuilder.addIncludedTable("marketing_record_follow");
		myMetaBuilder.addIncludedTable("company_info");
		myMetaBuilder.addIncludedTable("contact_info");
		myMetaBuilder.addIncludedTable("contact_form");
		generator.setMetaBuilder(myMetaBuilder);
		
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(true);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		//gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		generator.generate();
	}
}
