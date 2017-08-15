package system.models;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.jfinal.config.Constants;
import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.MultipartRequest;
import com.jfinal.upload.UploadFile;

import system.core.BaseModel;
import system.tools.DateTools;

public class FileModel extends system.dao.File{
	
	public static String web_root_path;
	public static String baseUploadPath;
	
	
/*	public FileModel(){
		web_root_path = PathKit.getWebRootPath();
		baseUploadPath = PropKit.use("config.properties").get("baseUploadPath", "../upload_files");
	}*/
	
	/**
	 * 构造函数不起作用，只能手动init参数
	 */
	private static void init(){
		web_root_path = PathKit.getWebRootPath();
		baseUploadPath = PropKit.use("config.properties").get("baseUploadPath", "../upload_files");
	}	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param uploadFile
	 * @param uid
	 * @param newName
	 * @return fid
	 */
	public static int save(UploadFile uploadFile, int uid, String newName) {
		system.dao.File system_file = new system.dao.File();
		String content_type = uploadFile.getContentType();

		String file_name = uploadFile.getFileName();
		String file_src_name = file_name;

		String fullPath = uploadFile.getUploadPath().replace("\\", "/");

		java.io.File io_file = uploadFile.getFile();
		
		int file_size = (int) io_file.length();//bytes
		String io_file_path = "";
		
		//默认上传路径
		//String baseUploadPath = PropKit.use("config.properties").get("baseUploadPath", "../upload_files");//默认为webroot同级的upload_files目录
		init();//改为在init中初始化
		//文件重命名
		if(StrKit.notBlank(newName)){
			String file_ext = uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf("."));//文件后缀
			file_name = newName + file_ext;
			
			if(!io_file.renameTo(new File(fullPath + "/" + newName + file_ext))){
				File oldFile = new File(fullPath + "/" + newName + file_ext);
				if(oldFile.delete()){
					io_file.renameTo(new File(fullPath + "/" + newName + file_ext));
				}
			}
			
		}
		

		/*Constants constants = new Constants();
		String rootName = constants.getBaseUploadPath();*/
		String rootName = PathKit.getWebRootPath();
		int rootIndex = fullPath.indexOf(rootName);
		String file_path = "";//相对upload_files的
		//String baseUploadPath = Constants.getBaseUploadPath();
		int baseUploadPath_index = fullPath.indexOf(baseUploadPath);
		String file_relative_path = fullPath.substring(baseUploadPath_index);// ../upload_files/custom_path
		String file_relative_final_path = fullPath.substring(baseUploadPath_index + baseUploadPath.length(), fullPath.length());//path
		
		if (file_relative_final_path.startsWith("/"))			// add prefix "/"
			file_relative_final_path = file_relative_final_path.substring(1, file_relative_final_path.length());//移除前面的/
		
		if(StrKit.notBlank(file_relative_final_path)){
			file_path = file_relative_final_path + "/" + file_name;
		}else{
			file_path = file_name;
		}
		
		//int file_size = 0;
		system_file.setFilePath(file_path);
		system_file.setFileName(file_name);
		system_file.setFileSrcName(file_src_name);
		system_file.setFileSize(file_size);
		system_file.setFileMime(content_type);
		system_file.setUid(uid);
		BaseModel.setCreateTime(system_file);
		boolean succeed = system_file.save();
		if(succeed){
			//jsonMap.put("fid", file.getFid());
			return system_file.getFid();
		}else{
			return 0;
		}
	}
	
	public static int save(UploadFile upload_file, int uid) {
		
		//如果没有设置文件名，随机生成
		int fid = 0;
		String file_name = DateTools.getDate() + "-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000000);
		//String file_name = System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
		//String file_name = String.valueOf(timestamp);
		fid = save(upload_file, uid, file_name);
		return fid;
	}
	
	public static boolean  exists(String file_path){
		//String web_root_path = PathKit.getWebRootPath();
		//String base_upload_path = PropKit.use("config.properties").get("baseUploadPath", "../upload_files");
		init();
		String file_full_path = web_root_path + "/" + baseUploadPath + "/" + file_path;
		//String file_full_path = baseUploadPath + "/" + file_path;
		java.io.File file = new java.io.File(file_full_path);
		return file.exists();
	}	
	//*************************************************************************************************
	public static system.dao.File changeAvatar(UploadFile uploadFile,String newName,Integer uid,Integer x,Integer y,Integer width,Integer height,Integer rotate) throws Exception{
		system.dao.File file = null;
		if(StrKit.isBlank(newName)){
			LogKit.info("newName is Blank!");
			return file;
		}
		String fullPath = uploadFile.getUploadPath().replace("\\", "/");
		File f = uploadFile.getFile();  //imageFile
		String fileType = uploadFile.getFileName().substring(uploadFile.getFileName().indexOf("."));
		String ratote_path = fullPath+File.separator+"ratote_"+newName+fileType;//图片临时地址
		String cutimage_path = fullPath+File.separator+"cut_"+newName+fileType;
		String thumimage_path = fullPath+File.separator+File.separator+newName+fileType;
		//旋转图片
		OutputStream output = new FileOutputStream(ratote_path);
		rotateImage(f, rotate, output);
		//剪切图片
		output = new FileOutputStream(cutimage_path);
		File raroteimage = new File(ratote_path);//旋转过后的文件
		cutImage(raroteimage, output, x, y, width, height);
		//缩略图
		File cutimage = new File(cutimage_path);//截取过后的文件
		output = new FileOutputStream(thumimage_path);
		if(!thumbImage(cutimage,output,150,150))return file;
		f.delete();//删除原来的图片
		cutimage.delete();
		raroteimage.delete();
		file = new system.dao.File();
		file.setFileName(newName + fileType);
		//String rootName = PathKit.getWebRootPath();
		//int rootIndex = fullPath.indexOf(rootName);
		file.setFilePath(fullPath.substring(fullPath.length() - 16));//../upload_files/userFiles/avatar改成userFiles/avatar		
		file.setUid(uid);
		DateTools.setTime(file);
		file.save();
		return file;
		
	}
    //按照比例剪切图片
	public static void cutImage(File srcImg,OutputStream output,int x,int y,int width,int height){
		cutImage(srcImg,output,new Rectangle(x, y, width, height));
	}
	public static void rotateImage(File srcImg, Integer rotate,OutputStream output) { 
	     String suffix = null;
		 // 获取图片后缀
        if(srcImg.getName().indexOf(".") > -1) {
           suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
        }
        if(suffix == null){
           LogKit.error("Sorry, the image suffix is illegal. the standard image suffix is {}.");
           return ;
        }
        try {
	        BufferedImage  src = ImageIO.read(srcImg);
	        int src_width = src.getWidth(null);  
	        int src_height = src.getHeight(null);  
	        // calculate the new image size  
	        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(  
	                src_width, src_height)), rotate);  
	  
	        BufferedImage res = null;  
	        res = new BufferedImage(rect_des.width, rect_des.height,  
	                BufferedImage.TYPE_INT_RGB);  
	        Graphics2D g2 = res.createGraphics(); 
	        AffineTransform affineTransform = new AffineTransform();
	        affineTransform.translate((rect_des.width - src_width) / 2,  
	                (rect_des.height - src_height) / 2);  
	        affineTransform.rotate(Math.toRadians(rotate), src_width / 2, src_height / 2);  
	        g2.setColor(Color.white);
	        g2.fillRect(0, 0, rect_des.width, rect_des.height);//填充白色
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//处理锯齿
	        g2.drawImage(src, affineTransform, null); 
	        g2.dispose();
		    ImageIO.write(res, suffix, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return ;  
    } 
	 public static Rectangle CalcRotatedSize(Rectangle src, int ratote) {  
        // if angel is greater than 90 degree, we need to do some conversion  
        if (ratote >= 90) {  
            if(ratote / 90 % 2 == 1){  
                int temp = src.height;  
                src.height = src.width;  
                src.width = temp;  
            }  
            ratote = ratote % 90;  
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;  
        double len = 2 * Math.sin(Math.toRadians(ratote) / 2) * r;  
        double angel_alpha = (Math.PI - Math.toRadians(ratote)) / 2;  
        double angel_dalta_width = Math.atan((double) src.height / src.width);  
        double angel_dalta_height = Math.atan((double) src.width / src.height);  
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha  
                - angel_dalta_width));  
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha  
                - angel_dalta_height));  
        int des_width = src.width + len_dalta_width * 2;  
        int des_height = src.height + len_dalta_height * 2;  
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
	 }
	//按比例缩放图片
	public static boolean thumbImage(File srcImg, OutputStream output,int width,int height){
		String suffix = null;
		 // 获取图片后缀
        if(srcImg.getName().indexOf(".") > -1) {
            suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
        }
        if(suffix == null){
            LogKit.error("Sorry, the image suffix is illegal. the standard image suffix is {}.");
            return false;
        }
		//按150*150格式
        Image img;
		try {
			img = ImageIO.read(srcImg);
			BufferedImage bis = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics g = bis.getGraphics();
	        g.drawImage(img, 0, 0, width, height, Color.LIGHT_GRAY, null);
	        g.dispose();
	        ImageIO.write(bis, suffix, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
         
	 }
	public static void cutImage(File srcImg,OutputStream output,Rectangle rect){
		FileInputStream fis = null;
		ImageInputStream iis = null;
		try {
			fis = new FileInputStream(srcImg);
			String suffix = null;
			 // 获取图片后缀
            if(srcImg.getName().indexOf(".") > -1) {
                suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
            }
            if(suffix == null){
                LogKit.error("Sorry, the image suffix is illegal. the standard image suffix is {}.");
                return ;
            }
            iis = ImageIO.createImageInputStream(fis);
            ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
            reader.setInput(iis,true);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, suffix, output);
            
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
		}finally{
			try {
				fis.close();
				iis.close();
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}
	//*************************************************************************************************
	/**
	 * 文件删除方法
	 * @param fid
	 * @return
	 * @throws Exception
	 */
	public static boolean fileDelete(int fid) throws Exception {
		init();
		File deleteFile = null;
		system.dao.File file = dao.findById(fid);
		boolean flag = false;
		if(file != null){
			String file_path=web_root_path+"/"+baseUploadPath+"/"+file.getFilePath();
			deleteFile = new File(file_path);
			if(exists(file.getFilePath())){
				flag = deleteFile.delete();
			}
		}
		return flag;
	}
		
}
