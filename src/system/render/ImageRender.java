package system.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import com.jfinal.core.JFinal;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;

public class ImageRender extends Render{
	private String file_path; //
	private String method = "preview"; //download 为下载
	private String file_download_name = ""; //图片下载名称
	
	private ServletContext servletContext;
	
	public ImageRender(String file_path){
		if (file_path == null) {
			throw new IllegalArgumentException("file can not be null.");
		}
		this.file_path = file_path;
		this.servletContext =  JFinal.me().getServletContext();
	}
	
	public ImageRender(String file_path, String method){
		if (file_path == null) {
			throw new IllegalArgumentException("file can not be null.");
		}
		if(method.equals("download")){
			this.method = "download";
		}
		this.file_path = file_path;
		this.servletContext =  JFinal.me().getServletContext();
	}	

	@Override
	public void render() {
		// TODO Auto-generated method stub
		String file_extension = getFormate(file_path);
		String web_root_path = PathKit.getWebRootPath();
		//String baseUploadPath = 
				//默认上传路径
		String base_upload_path = PropKit.use("config.properties").get("baseUploadPath", "../upload_files");
		String file_full_path = web_root_path + "/" + base_upload_path + "/" + file_path;
		
		//String contentType = servletContext.getMimeType(file_full_path);

		BufferedImage image = null;
		File file = null;
		try{
			file = new File(file_full_path);
			if(file == null){

			}
		}catch(Exception e){
			
		}

		try {


			image = ImageIO.read(file);
			//ImageIo.get
			//image.ori
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//response.setStatus(404);//直接返回空
			RenderFactory.me().getErrorRender(404).setContext(request, response).render();//返回404错误页面

			return;
			//e1.printStackTrace();
		}

		//response.setHeader("Pragma","no-cache");
		//response.setHeader("Cache-Control","no-cache");
		int cache_time = 60 * 60 * 24 * 7; //一个月 
		response.setHeader("Cache-Control","max-age=" + cache_time);// 一天86400
		//response.setDateHeader("Expires", 0);
		String mimiType = getImageMimeType(file_extension);
		response.setContentType(mimiType);
		
		//如果是下载，作为文件下载，于源文件略不同，有压缩
		if(method.equals("download")){
			//String download_file_name = "图片.jpg";
			String download_file_name = getFileName(file_path);//作为下载文件的名称
			String filename = "";
			try {
				filename = new String(download_file_name.getBytes(), "ISO8859-1");//防止windows下下载的文件中文乱码
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.setHeader("Content-disposition", "attachment; filename=" + filename);
		}

		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
			ImageIO.write(image, file_extension, sos);
		} catch (IOException e) {
			if (getDevMode()) {
				throw new RenderException(e);
			}
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			if (sos != null) {
				try {sos.close();} catch (IOException e) {LogKit.logNothing(e);}
			}
		}
	}
	
	/**
	 * abc.jpg return jpg, www.abc.png return png
	 * @param ImageName
	 * @return
	 */
	public static String getFormate(String ImageName) {

	    return (ImageName.substring(ImageName.lastIndexOf('.') + 1, ImageName.length()).toLowerCase());
	}
	
	/**
	 * public/abc.jpg return abc.jpg
	 * @param ImageName
	 * @return
	 */
	public static String getFileName(String ImageName) {

	    return ImageName.substring(ImageName.lastIndexOf('/') + 1, ImageName.length());
	}	
	
	public static String getImageMimeType(String file_extension){
		String mime_type = "";
		switch(file_extension.toLowerCase()){
		case "png":
			mime_type = "image/jpeg";
			break;
		case "gif":
			mime_type = "image/gif";
			break;
		case "jpg":
		case "jpeg":
		default:
			mime_type = "image/jpeg";
		}
		return mime_type;
	}

}
