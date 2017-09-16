package system.report;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;


public class ReportGenerator {
	
	
	public Connection getConnection(){
		
		 //String url = "jdbc:mysql://121.41.224.228:3306/jiaki_erp?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";  
		  //String name = "com.mysql.jdbc.Driver";  
		 //String user = "jiaki_erp";  
		   //String password = "Jiakierp123456";  
		  
		   Prop p = PropKit.use("config.properties");
		   String url = p.get("jdbcUrl");
		   String user = p.get("user");
		   String password = p.get("password");
		   String name = "com.mysql.jdbc.Driver";
		   
		     Connection conn = null;  
		     PreparedStatement pst = null;  		  
		    
		        try {  
		            Class.forName(name);//指定连接类型  
		            conn = DriverManager.getConnection(url, user, password);//获取连接  
		           
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		return conn;
		
	}
	
	
	/*public boolean _Compilereport(String reportName){
		
		  String reportModelFile = "E:/Workspaces/MyEclipse 2015/lbframworkjob/"+reportName+".jrxml";
		  String reportTarFile = getFileTemplatePath() + "/" + "report/"+reportName+".jasper";
		
		try {
			
			
			
			JasperCompileManager.compileReportToFile(reportModelFile,reportTarFile);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}*/
	
	
	
    //读取jasper模版文件
    public ByteArrayOutputStream  generateJasperReport(Map<String, Object> parameters,String reportName){
    	ByteArrayOutputStream outPut = new ByteArrayOutputStream();
    	
        String str = "";
        try {
            Connection conn = this.getConnection(); 
            String reportModelFile = getFileTemplatePath() + "/" + "report/"+reportName+".jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportModelFile,parameters,conn);           
            JasperExportManager.exportReportToPdfStream(jasperPrint, outPut);
        }catch(JRException e){
            e.printStackTrace();
        }catch (Exception es) {
             es.printStackTrace();
        }
        return outPut;
    }
    
    
    //生成pdf
    public String generatePDFReport(ByteArrayOutputStream outPut ,String reportName){
    	
    	
    	FileOutputStream fos;
    	String file_full_path = "";
		try {
			file_full_path = "report/files/" + reportName + ".pdf";
			fos = new FileOutputStream(new File(getFileTemplatePath() + "/" + file_full_path));
         // Put data in your baos
         ObjectOutputStream objectOutputStream = new ObjectOutputStream(outPut);       
         objectOutputStream.flush();
         objectOutputStream.close();
         outPut.writeTo(fos);
         outPut.flush();
         outPut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
	         try {
				outPut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
         return file_full_path;
    	
    }

    private String getFileTemplatePath() {
        String result = "";
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File file = new File(path);
        result = file.getParent();
        result = result.replace("%20", " ");
        //if (result.toLowerCase().indexOf("bin") >= 0)
            return result;
        //return result + "/bin";
    }
	
	

}
