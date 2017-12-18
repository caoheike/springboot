package com.reptile.contorller;


import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
  
public class ReadImg {  
  
   public static void main(String[] args) throws TesseractException {
	   File imageFile = new File("C://img/bb.png");  
       Tesseract instance = new Tesseract();

       //将验证码图片的内容识别为字符串 
       String result = instance.doOCR(imageFile);
       System.out.println(result);
}
}  