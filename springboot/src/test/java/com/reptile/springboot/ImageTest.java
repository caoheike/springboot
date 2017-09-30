package com.reptile.springboot;

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
  
import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.methods.GetMethod;  
import org.apache.commons.io.IOUtils; 

public class ImageTest {
	 private static Map<BufferedImage, String> trainMap = null;  
	    private static int index = 0;  
	  
	    public static int isBlack(int colorInt) {  
	        Color color = new Color(colorInt);  
	        if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {  
	            return 1;  
	        }  
	        return 0;  
	    }  
	  
	    public static int isWhite(int colorInt) {  
	        Color color = new Color(colorInt);  
	        if (color.getRed() + color.getGreen() + color.getBlue() > 600) {  
	            return 1;  
	        }  
	        return 0;  
	    }  
	  
	    public static BufferedImage removeBackgroud(String picFile)  
	            throws Exception {  
	        BufferedImage img = ImageIO.read(new File("D://CaptchaImg.png"));  
	        img = img.getSubimage(1, 1, img.getWidth() - 2, img.getHeight() - 2);  
	        int width = img.getWidth();  
	        int height = img.getHeight();  
	        double subWidth = (double) width / 5.0;  
	        for (int i = 0; i < 5; i++) {  
	            Map<Integer, Integer> map = new HashMap<Integer, Integer>();  
	            for (int x = (int) (1 + i * subWidth); x < (i + 1) * subWidth  
	                    && x < width - 1; ++x) {  
	                for (int y = 0; y < height; ++y) {  
	                    if (isWhite(img.getRGB(x, y)) == 1)  
	                        continue;  
	                    if (map.containsKey(img.getRGB(x, y))) {  
	                        map.put(img.getRGB(x, y), map.get(img.getRGB(x, y)) + 1);  
	                    } else {  
	                        map.put(img.getRGB(x, y), 1);  
	                    }  
	                }  
	            }  
	            int max = 0;  
	            int colorMax = 0;  
	            for (Integer color : map.keySet()) {  
	                if (max < map.get(color)) {  
	                    max = map.get(color);  
	                    colorMax = color;  
	                }  
	            }  
	            for (int x = (int) (1 + i * subWidth); x < (i + 1) * subWidth  
	                    && x < width - 1; ++x) {  
	                for (int y = 0; y < height; ++y) {  
	                    if (img.getRGB(x, y) != colorMax) {  
	                        img.setRGB(x, y, Color.WHITE.getRGB());  
	                    } else {  
	                        img.setRGB(x, y, Color.BLACK.getRGB());  
	                    }  
	                }  
	            }  
	        }  
	        return img;  
	    }  
	  
	    public static BufferedImage removeBlank(BufferedImage img) throws Exception {  
	        int width = img.getWidth();  
	        int height = img.getHeight();  
	        int start = 0;  
	        int end = 0;  
	        Label1: for (int y = 0; y < height; ++y) {  
	            for (int x = 0; x < width; ++x) {  
	                if (isBlack(img.getRGB(x, y)) == 1) {  
	                    start = y;  
	                    break Label1;  
	                }  
	            }  
	        }  
	        Label2: for (int y = height - 1; y >= 0; --y) {  
	            for (int x = 0; x < width; ++x) {  
	                if (isBlack(img.getRGB(x, y)) == 1) {  
	                    end = y;  
	                    break Label2;  
	                }  
	            }  
	        }  
	        return img.getSubimage(0, start, width, end - start + 1);  
	    }  
	  
	    public static List<BufferedImage> splitImage(BufferedImage img)  
	            throws Exception {  
	        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();  
	        int width = img.getWidth();  
	        int height = img.getHeight();  
	        List<Integer> weightlist = new ArrayList<Integer>();  
	        for (int x = 0; x < width; ++x) {  
	            int count = 0;  
	            for (int y = 0; y < height; ++y) {  
	                if (isBlack(img.getRGB(x, y)) == 1) {  
	                    count++;  
	                }  
	            }  
	            weightlist.add(count);  
	        }  
	        for (int i = 0; i < weightlist.size();i++) {  
	            int length = 0;  
	            while (i < weightlist.size() && weightlist.get(i) > 0) {  
	                i++;  
	                length++;  
	            }  
	            if (length > 2) {  
	                subImgs.add(removeBlank(img.getSubimage(i - length, 0,  
	                        length, height)));  
	            }  
	        }  
	        return subImgs;  
	    }  
	  
	    public static Map<BufferedImage, String> loadTrainData() throws Exception {  
	        if (trainMap == null) {  
	            Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();  
	            File dir = new File("train3");  
	            File[] files = dir.listFiles();  
	            for (File file : files) {  
	                map.put(ImageIO.read(file), file.getName().charAt(0) + "");  
	            }  
	            trainMap = map;  
	        }  
	        return trainMap;  
	    }  
	  
	    public static String getSingleCharOcr(BufferedImage img,  
	            Map<BufferedImage, String> map) {  
	        String result = "#";  
	        int width = img.getWidth();  
	        int height = img.getHeight();  
	        int min = width * height;  
	        for (BufferedImage bi : map.keySet()) {  
	            int count = 0;  
	            if (Math.abs(bi.getWidth()-width) > 2)  
	                continue;  
	            int widthmin = width < bi.getWidth() ? width : bi.getWidth();  
	            int heightmin = height < bi.getHeight() ? height : bi.getHeight();  
	            Label1: for (int x = 0; x < widthmin; ++x) {  
	                for (int y = 0; y < heightmin; ++y) {  
	                    if (isBlack(img.getRGB(x, y)) != isBlack(bi.getRGB(x, y))) {  
	                        count++;  
	                        if (count >= min)  
	                            break Label1;  
	                    }  
	                }  
	            }  
	            if (count < min) {  
	                min = count;  
	                result = map.get(bi);  
	            }  
	        }  
	        return result;  
	    }  
	  
	    public static String getAllOcr(String file) throws Exception {  
	        BufferedImage img = removeBackgroud(file);  
	        List<BufferedImage> listImg = splitImage(img);  
	        Map<BufferedImage, String> map = loadTrainData();  
	        String result = "";  
	        for (BufferedImage bi : listImg) {  
	            result += getSingleCharOcr(bi, map);  
	        }  
	        ImageIO.write(img, "PNG", new File("result3//" + result + ".png"));  
	        return result;  
	    }  
	  
	    public static void downloadImage() {  
	        HttpClient httpClient = new HttpClient();  
	        GetMethod getMethod = new GetMethod(
	        	"https://cardsonline.spdbccc.com.cn/icard/CaptchaImg?date="
	        		);  
	        for (int i = 0; i < 30; i++) {  
	            try {  
	                // 执行getMethod  
	                int statusCode = httpClient.executeMethod(getMethod);  
	                if (statusCode != HttpStatus.SC_OK) {  
	                    System.err.println("Method failed: "  
	                            + getMethod.getStatusLine());  
	                }  
	                // 读取内容  
	                String picName = "img3//" + i + ".png";  
	                InputStream inputStream = getMethod.getResponseBodyAsStream();  
	                OutputStream outStream = new FileOutputStream(picName);  
	                IOUtils.copy(inputStream, outStream);  
	                outStream.close();  
	                System.out.println(i + "OK!");  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            } finally {  
	                // 释放连接  
	                getMethod.releaseConnection();  
	            }  
	        }  
	    }  
	  
	    public static void trainData() throws Exception {  
	        File dir = new File("temp3");  
	        File[] files = dir.listFiles();  
	        for (File file : files) {  
	            BufferedImage img = removeBackgroud("temp3//" + file.getName());  
	            List<BufferedImage> listImg = splitImage(img);  
	            if (listImg.size() == 5) {  
	                for (int j = 0; j < listImg.size(); ++j) {  
	                    ImageIO.write(listImg.get(j), "PNG", new File("train3//"  
	                            + file.getName().charAt(j) + "-" + (index++)  
	                            + ".png"));  
	                }  
	            }  
	        }  
	    }  
	  
	    /** 
	     * @param args 
	     * @throws Exception 
	     */  
	    public static void main(String[] args) throws Exception {  
	       //  downloadImage(); 
	         trainData();  
	        for (int i = 0; i < 30; ++i) {  
	            String text = getAllOcr("img3//" + i + ".png");  
	            System.out.println(i + ".png = " + text);  
	        }  
	    }  
}
