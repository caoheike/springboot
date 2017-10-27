package com.reptile.springboot;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.internal.WrapsDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * 截取交通银行软键盘图片
 *
 */
public class SeleniumGetImg {

    public static void getImage() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\iedriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://creditcardapp.bankcomm.com/idm/sso/login.html?service=https://creditcardapp.bankcomm.com/member/shiro-cas");

        driver.findElementById("cardNo").sendKeys("6222520811798888");

        driver.findElement(By.id("cardpassword")).click();
        WebElement element = driver.findElement(By.className("key-pop"));

//        String icbcImg1 = getICBCImg1(element, driver);//返回键盘中数字
        String icbcImg1 = "4567891230";
        String[] split = icbcImg1.split("");//将数字字符串分割为数组

        List<WebElement> li = element.findElements(By.tagName("li"));

        //返回识别的字符串
        String pwd=".@+\\";
        String[] pwdArry = pwd.split("");

        for (int i=0;i<pwdArry.length;i++){   //读取密码的每一位数字，循环找出键盘中对应的数字下标
            String num=pwdArry[i];
            for(int j=0;j<split.length;j++){
                if(num.equals(split[j])){  //数字部分
                    li.get(j).click();
                    Thread.sleep(500);
                    break;
                }
                if(j==split.length-1){   //字符部分
                    int integer = JiaoTongKeyMap.map.get(num);
                    li.get(integer).click();
                    Thread.sleep(500);
                    break;
                }

            }
        }
        driver.findElementById("cardNo").click();
        driver.findElement(By.id("cardLogin")).click();
    }

    public static String getICBCImg(WebElement element, WebDriver driver) {
        if (element == null) throw new NullPointerException("图片元素失败");
        WrapsDriver wrapsDriver = (WrapsDriver) element; //截取整个页面
        File scrFile = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
        String code = "";
        try {
            BufferedImage img = ImageIO.read(scrFile);
            ImageIO.write(img,"png",new File("F:\\img",System.currentTimeMillis()+".png"));
            int screenshotWidth = img.getWidth();
            org.openqa.selenium.Dimension dimension = driver.manage().window().getSize(); //获取浏览器尺寸与截图的尺寸
            double scale = (double) dimension.getWidth() / screenshotWidth;
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();
            Point point = element.getLocation();
            int subImgX = (int) (point.getX() / scale)+12; //获得元素的坐标
            int subImgY = (int) (point.getY() / scale)+6;
            int subImgWight = (int) (eleWidth / scale)-3 ; //获取元素的宽高
            int subImgHeight = (int) (eleHeight / scale) -6; //精准的截取元素图片，
            BufferedImage dest = img.getSubimage(subImgX, subImgY, subImgWight, subImgHeight);
            ImageIO.write(dest, "png", new File("F:\\img",System.currentTimeMillis()+".png"));

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    //截取键盘图片
    public static String getICBCImg1(WebElement element, WebDriver driver) {
        if (element == null) throw new NullPointerException("图片元素失败");
        WrapsDriver wrapsDriver = (WrapsDriver) element; //截取整个页面
        File scrFile = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
        String code = "";
        try {
            BufferedImage img = ImageIO.read(scrFile);
            int screenshotWidth = img.getWidth();
            org.openqa.selenium.Dimension dimension = driver.manage().window().getSize(); //获取浏览器尺寸与截图的尺寸
            double scale = (double) dimension.getWidth() / screenshotWidth;
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();
            Point point = element.getLocation();
            int subImgX = (int) (point.getX() / scale)+10; //获得元素的坐标
            int subImgY = (int) (point.getY() / scale)+27;
            int subImgWight = (int) (eleWidth / scale) ; //获取元素的宽高
            int subImgHeight = (int) (eleHeight / scale)-27 ; //精准的截取元素图片，
            BufferedImage dest = img.getSubimage(subImgX, subImgY, subImgWight, subImgHeight);
            ImageIO.write(dest, "png", new File("F:\\img",System.currentTimeMillis()+".png"));

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static void main(String[] args) throws InterruptedException {
        getImage();
//        String pwd="930229";
//        String[] pwdArry = pwd.split("");
//        for (int i=0;i<pwdArry.length;i++){
//            System.out.println(pwdArry[i]);
//        }
    }
}
