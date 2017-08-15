1、升级了
slf4j-api-1.6.6.jar
修复bug:
SLF4J: Class path contains multiple SLF4J bindings.  

2、升级了log4j并加入了log4j-extras-1.2.17使用rolling.RollingFileAppender修复Windows下日志文件名重命名错误：
log4j:ERROR Failed to rename

3、修改c3p0连接池为druid连接池，解决ubuntu server jdk1.8+tomcat8下部署有可能出现的报错

4、注意logs目录不要加入svn版本库，即不要提交logs目录及目录下的日志文件

5、Jasper 导出 PDF包依赖
jasperreports-2.0.4.jar
commons-digester-1.7.jar
commons-collections-2.1.jar (commons-collections.jar)
commons-logging-1.0.2.jar
#commons-beanutils.jar
iText-2.0.7.jar (used infor PDF exporting)
spring core
spring beans
joda time
jfree chart
jcommon

移除了jasperreports-javaflow-6.3.0.jar解决了linux 下 tomcat 8 生成报表错误
http://stackoverflow.com/questions/38005004/jasperreport-6-2-java-lang-stackoverflowerror-when-using-subreport
Note that you do not need both jasperreports-x.y.x.jar and jasperreports-javaflow-x.y.z.jar on the classpath.
 The jasperreports-javaflow jar is a variant of the vanilla jar 
 that should only be used when the Javaflow based subreport runner is needed.

6、阿里云计算开发工具包依赖
aliyun-sdk-oss-2.6.0.jar
commons-codec-1.10.jar
commons-logging-1.2.jar
hamcrest-core-1.1.jar
httpclient-4.4.1.jar
httpcore-4.4.1.jar
jdom-1.1.jar