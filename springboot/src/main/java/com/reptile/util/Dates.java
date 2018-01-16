package com.reptile.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	/**
	 * 获得上个月
	 * @param
	 * @param
	 * @return
	 */

	public static  String beforMonth(int  a){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
	    c.add(Calendar.MONTH, -a);
	    Date m = c.getTime();
	    String mon = format.format(m);
		return mon;
	}
	
	/**
	 * 获取当前时间（年月日时分秒）
	 * @return
	 */
	public static String currentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  hh:mm:ss");
	    String mon = format.format(new Date());
		return mon;
	}
	
	/**
	 * 获取n年前为某年
	 * @return
	 */
	public static String beforeYear(int n){
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, -n);
		Date date = c.getTime();
		String year = format.format(date);
		return year;
	}
	
	/**
	 * 获取当前时间，单位秒
	 * @return
	 */
	public static long getCurrentTime(){
		return new Date().getTime();
	}
	
	/**
	 * 获取三个月之前的时间，单位秒
	 * @return
	 */
	public static long getBeforeTime(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -6);
		return c.getTime().getTime();
	}
	
	/**
	 * 获取当前时间（格式：yyyyMM）
	 * @return
	 */
	public static String getCurrentDate(){
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		String lastTime = sim.format(calendar.getTime());
		return lastTime;
	}

	/**
	 * 判断当前时间是否为运营商账单日
	 * @return
	 */
	public static boolean isBillDate() {
		boolean flag=false;
		long time = System.currentTimeMillis();
		String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date(time));
		String substring = currentTime.substring(6);
		int day = Integer.parseInt(substring);
		if(day>3){
			flag=false;
		}else{
			flag=true;
		}
		return flag;
	}
	
}
