package com.reptile.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import com.gargoylesoftware.htmlunit.html.HtmlImage;

public class ImgUtil {
		
	/**
	 * 保存验证码图片,返回图片本地存储绝对路径+图片名
	 * @param htmlImg 图片流
	 * @param prefix  图片名称前缀
	 * @param verifyImagesPath 图片保存的绝对路径
	 * @param suffix 图片名称后缀 如png
	 * @return
	 * @throws IOException
	 */
	public static String saveImg(HtmlImage htmlImg,String prefix, String verifyImagesPath,String suffix) throws IOException{
	  
		File file = new File(verifyImagesPath + File.separator);
       	if (!file.exists()) {
       		file.mkdir();
       	}
	   	ImageReader imgReader = htmlImg.getImageReader();
	    BufferedImage bufferedImage  = ImageIO.read((ImageInputStream)imgReader.getInput());
		String fileName = prefix + System.currentTimeMillis()+"."+suffix;
		ImageIO.write(bufferedImage, suffix, new File(file,fileName));
	   	String filePath = verifyImagesPath + File.separator + fileName;
		return filePath;
	}
	/**
	 * 保存验证码图片,返回浏览器可访问到图片的地址
	 * @param htmlImg 图片流
	 * @param prefix  图片名称前缀
	 * @param verifyImagesPath 图片在项目中的相对路径 如：/verifyImages
	 * @param suffix 图片名称后缀 如png
	 * @param request 
	 * @return
	 * @throws IOException
	 */
	public static String saveImg(HtmlImage htmlImg,String prefix, String verifyImagesPath,String suffix,HttpServletRequest request) throws IOException{
	    String verifyImages = request.getSession().getServletContext().getRealPath(verifyImagesPath);
		File file = new File(verifyImages + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}
		ImageReader imgReader = htmlImg.getImageReader();
		BufferedImage bufferedImage  = ImageIO.read((ImageInputStream)imgReader.getInput());
		String fileName = prefix + System.currentTimeMillis()+"."+suffix;
		ImageIO.write(bufferedImage, suffix, new File(file,fileName));
		
		String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + verifyImagesPath + File.separator + fileName;
		return filePath;
	}
	
}
