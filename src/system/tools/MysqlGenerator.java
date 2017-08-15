package system.tools;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import system.tools.MyMetaBuilder;

public class MysqlGenerator {
	public static DataSource getDataSource() {
		Prop p = PropKit.use("config.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "system.dao.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/system/dao/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "system.dao";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		generator.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		//generator.addExcludedTable("system_menu_links");
		
		MyMetaBuilder myMetaBuilder = new MyMetaBuilder(getDataSource());
		myMetaBuilder.addIncludedTable("system_menu_link");
		myMetaBuilder.addIncludedTable("system_user");
		myMetaBuilder.addIncludedTable("system_session");
		myMetaBuilder.addIncludedTable("system_permission");		
		myMetaBuilder.addIncludedTable("system_role_permission");
		myMetaBuilder.addIncludedTable("system_role");
		myMetaBuilder.addIncludedTable("system_role_group");
		myMetaBuilder.addIncludedTable("system_role_menu_link");
		myMetaBuilder.addIncludedTable("system_user_role");
		myMetaBuilder.addIncludedTable("system_task");
		
		myMetaBuilder.addIncludedTable("system_file");
		myMetaBuilder.addIncludedTable("system_cache");
		myMetaBuilder.addIncludedTable("system_variable");
		
		myMetaBuilder.addIncludedTable("system_sms_api");
		myMetaBuilder.addIncludedTable("system_sms_log");
		myMetaBuilder.addIncludedTable("system_sms_template");
		myMetaBuilder.addIncludedTable("system_todo");
		
		//词汇相关表
		myMetaBuilder.addIncludedTable("system_vocabulary");
		myMetaBuilder.addIncludedTable("system_vocabulary_term");
		
		//消息相关表
		myMetaBuilder.addIncludedTable("system_message");
		generator.setMetaBuilder(myMetaBuilder);
		
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(true);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		generator.setRemovedTableNamePrefixes("system_");
		// 生成
		generator.generate();
	}
}
