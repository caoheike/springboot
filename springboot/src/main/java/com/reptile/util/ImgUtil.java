package com.reptile.util;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

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

		String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + verifyImagesPath + "/" + fileName;
		return filePath;
	}
	
	
	/**
	 * 返回打码后的验证码
	 * @param element
	 * @param driver
	 * @param path 图片绝对地址
	 * @param prefix 
	 * @param suffix
	 * @return
	 * @throws Exception
	 */
	public static String saveImg(WebElement element, WebDriver driver,String path,String prefix,String suffix) throws Exception{
		File file = new File(path + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}
        BufferedImage bufferedImage = createElementImages(driver, element);
		String fileName = prefix + System.currentTimeMillis()+"."+suffix;
		ImageIO.write(bufferedImage, suffix, new File(file,fileName));
		
		Map<String, Object> imagev = MyCYDMDemo.Imagev(file + "/" +fileName);
        String code = imagev.get("strResult").toString();//读取图片验证码
		return code;
	}
		
	
	  /**
	   * 截取验证码图片
	   * @param driver
	   * @param webElement
	   * @return
	   * @throws IOException
	   */
	  private static BufferedImage createElementImages(WebDriver driver,WebElement webElement)  
	      throws IOException {  
	    // 获得webElement的位置和大小。  
	    Point location = webElement.getLocation();  
	    Dimension size = webElement.getSize();  
	    // 创建全屏截图。  
	    BufferedImage originalImage =  ImageIO.read(new ByteArrayInputStream(takeScreenshot(driver)));  
	    // 截取webElement所在位置的子图。  
	    BufferedImage croppedImage = originalImage.getSubimage(  
	        location.getX() - size.getWidth()/2 - 10,location.getY(), size.getWidth(), size.getHeight()); 	 
	    return croppedImage;  
	  }
	  
	  
	  private static byte[] takeScreenshot(WebDriver driver) throws IOException {  
	      WebDriver augmentedDriver = new Augmenter().augment(driver);  
	    return ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.BYTES);  
      } 
	
}
