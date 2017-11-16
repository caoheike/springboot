package com.reptile.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	/**
	 * 获得上个月
	 * @param year
	 * @param month
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
	
}
