package com.reptile.util;

import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class discriminate {
	 public static void main(String[] args) throws TesseractException {
		  File imageFile = new File("E:\\2.png");  
	
	       Tesseract instances = new Tesseract();
	       instances.setDatapath("C:\\Users\\Administrator\\springboot\\tessdata"); 
	       //将验证码图片的内容识别为字符串 
	       String result = instances.doOCR(imageFile);
	       System.out.println(result);
	}
       
       
       
       
}
