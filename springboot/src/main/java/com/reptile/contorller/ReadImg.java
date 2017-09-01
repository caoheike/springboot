package com.reptile.contorller;


import java.awt.Color;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  


import javax.imageio.ImageIO;  
  


import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.methods.GetMethod;  
import org.apache.commons.io.IOUtils;  
  
public class ReadImg {  
  
   public static void main(String[] args) throws TesseractException {
	   File imageFile = new File("C://img/bb.png");  
       Tesseract instance = new Tesseract();

       //将验证码图片的内容识别为字符串 
       String result = instance.doOCR(imageFile);
       System.out.println(result);
}
}  