package com.reptile.util;

import java.io.IOException;
import java.io.InputStream;

public class InvokeBat4 {
	  public static void runbat() {
	        String cmd = "cmd /k start D:\\processKill.bat";// pass
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
