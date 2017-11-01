package com.reptile.util;

import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.internal.WrapsDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class RobotUntil {
    //单个 按键
    public static void keyPress(Robot r, int key) {
        r.keyPress(key);
        r.keyRelease(key);
        r.delay(100);
    }
    public static void keyPressWithAlt(Robot r, int key) {
        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_ALT);
        r.delay(100);
    }

    //截取验证码
    public static String getImgFileByScreenshot(WebElement element, WebDriver driver,File file) throws Exception {
        if (element == null) throw new NullPointerException("图片元素失败");
        WrapsDriver wrapsDriver = (WrapsDriver) element; //截取整个页面
        File scrFile = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
        String code = "";

            BufferedImage img = ImageIO.read(scrFile);
            int screenshotWidth = img.getWidth();
            org.openqa.selenium.Dimension dimension = driver.manage().window().getSize(); //获取浏览器尺寸与截图的尺寸
            double scale = (double) dimension.getWidth() / screenshotWidth;
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();
            Point point = element.getLocation();
            int subImgX = (int) (point.getX() / scale); //获得元素的坐标
            int subImgY = (int) (point.getY() / scale);
            int subImgWight = (int) (eleWidth / scale) + 10; //获取元素的宽高
            int subImgHeight = (int) (eleHeight / scale) + 10; //精准的截取元素图片，
            BufferedImage dest = img.getSubimage(subImgX, subImgY, subImgWight, subImgHeight);
            File file1=new File(file,"zgyh"+System.currentTimeMillis()+".png");
            ImageIO.write(dest, "png", file1);
            System.out.println(file1.getAbsolutePath());
        Map<String, Object> imagev = MyCYDMDemo.Imagev(file1.getAbsolutePath());
        code =imagev.get("strResult").toString();
        return code;
    }

    public static String getImgFileByScreenshot5(WebElement element, WebDriver driver,File file) throws Exception {
        if (element == null) throw new NullPointerException("图片元素失败");
        WrapsDriver wrapsDriver = (WrapsDriver) element; //截取整个页面
        File scrFile = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
        String code = "";

        BufferedImage img = ImageIO.read(scrFile);
        int screenshotWidth = img.getWidth();
        org.openqa.selenium.Dimension dimension = driver.manage().window().getSize(); //获取浏览器尺寸与截图的尺寸
        double scale = (double) dimension.getWidth() / screenshotWidth;
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();
        Point point = element.getLocation();
        int subImgX = (int) (point.getX() / scale); //获得元素的坐标
        int subImgY = (int) (point.getY() / scale);
        int subImgWight = (int) (eleWidth / scale) + 10; //获取元素的宽高
        int subImgHeight = (int) (eleHeight / scale) + 10; //精准的截取元素图片，
        BufferedImage dest = img.getSubimage(subImgX, subImgY, subImgWight, subImgHeight);
        File file1=new File(file,"zgyh"+System.currentTimeMillis()+".png");
        ImageIO.write(dest, "png", file1);
        System.out.println(file1.getAbsolutePath());
        Map<String, Object> imagev = MyCYDMDemo.getCode(file1.getAbsolutePath());
        code =imagev.get("strResult").toString();
        return code;
    }
}
