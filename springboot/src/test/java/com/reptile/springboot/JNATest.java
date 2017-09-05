package com.reptile.springboot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
public class JNATest {  
	   
    
    public interface User32 extends StdCallLibrary
    {
     User32 INSTANCE = (User32)Native.loadLibrary("User32",User32.class);//加载系统User32 DLL文件，也可以是C++写的DLL文件

     int SendMessageA(int i,int msg,int wparam,String string);
     int FindWindowA(String arg0,String arg1);
     void BlockInput(boolean isBlock);
     int MessageBoxA(int hWnd,String lpText,int lpCaption,int uType);
     int FindWindowA(int hWnd,String lpText,int lpCaption,int uType);
     int SendMessageA(int hWnd, int lpText,int lpCaption,int uType);
    }


//	
//		
//        public static void main(String[] args) throws Exception
//        {
//    int hwnd = User32.INSTANCE.FindWindowA(null, null);
//     //  WinDef.HWND hwnd = com.sun.jna.platform.win32.User32.INSTANCE.FindWindow(null,"www.txt - 记事本");
//         System.setProperty("jna.encoding","GBK");//设置编码，防止乱码
//         User32.INSTANCE.MessageBoxA(0, "看我闪瞎你的狗眼", 0, 0);//调用消息对话框
//         //User32.INSTANCE.SendMessageA(49176,use, 0, 0);
//     User32.INSTANCE.BlockInput(true);//阻塞鼠标键盘的输入
//      //   User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);//关闭显示器
//         Thread.sleep(2000);//间隔2秒
//     //    User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, -1);//打开显示器
//        // Thread.sleep(2000);//间隔2秒
////  User32.INSTANCE.SendMessageA(3278802,0x08,0,0);
//    int status= User32.INSTANCE.SendMessageA(331222,0x000C,0,"大家好我是这样输入的111111");
////        
////        
////         System.out.println(status);
//        
//        }
    
	public static void main(String[] args) {
		int WM_KEYDOWN = 0x0100;
		int WM_KEYUP = 0x0101;
		   int hwnd = User32.INSTANCE.FindWindowA(null, null);
	
		 
		System.setProperty("webdriver.ie.driver", "D:/ie/IEDriverServer.exe");
		WebDriver driver = new InternetExplorerDriver();
		driver.get("https://ebsnew.boc.cn/boc15/login.html");	
		WebElement txtbox = driver.findElement(By.id("txt_username_79443"));
		txtbox.sendKeys("w");
		txtbox.sendKeys(Keys.TAB);
		Actions action = new Actions(driver); 
		//action.contextClick();// 鼠标右键在当前停留的位置做单击操作 
//		  action.keyDown(Keys.SHIFT).sendKeys("adc").perform();  
		//id="input_div_password_79445"
//		action.click(driver.findElement(By.id("input_div_password_79445")));// 鼠标左键点击指定的元素
//		System.out.println("结束");
		   //  WebElement txtboxs = driver.findElement(By.id("input_div_password_79445"));
		    // System.out.println(txtboxs.getText());
		//	System.out.println(driver.getPageSource());
		
		}
}
