package com.reptile.util;

import java.io.IOException;
import java.io.InputStream;
/**
 * 
 * @author liubin
 * 
 *
 */
public class InvokeBat4 {
	  public static void runbat() {
		  	//操作D盘下的processKill.bat
		  	//processKill.bat文件为关闭浏览器进程的文件
		  	/**
		  	 * processKill文件内容
		  	 * taskkill /f /im IEDriverServer.exe /t
		  	 * taskkill /f /im chromedriver.exe /t
			 * taskkill /f /im chrome.exe /t
			 * taskkill /f /im iexplore.exe /t
			 * taskkill /f /im cmd.exe /t
			 * 这些命令只适合windows下，最后一行是关闭cmd这个进程
		  	 */
	        String cmd = "cmd /k start D:\\processKill.bat";
	        try {
	            Process ps = Runtime.getRuntime().exec(cmd);
	            InputStream in = ps.getInputStream();
	            int c;
	            while ((c = in.read()) != -1) {
	            }
	            in.close();
	            ps.waitFor();
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
	        catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	    }
}
