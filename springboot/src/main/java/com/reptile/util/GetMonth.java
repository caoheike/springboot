package com.reptile.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetMonth {
	/**
	 * 获得yyyy/MM格式的上个num个月
	 * @param year
	 * @param month
	 * @return
	 */

	public static  String beforMonth(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy/MM" );
		 cal.set(Calendar.YEAR,year);
		 cal.set(Calendar.MONTH, nowMonth-1);
		 cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());
			
	}

	/**
	 * 获得当前月
	 * @param year
	 * @param month
	 * @return
	 */

	public static  String nowMonth(){
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		return sdf.format(date);
	}	
	
	
	/**
	 * 获得今天
	 * @param year
	 * @param month
	 * @return
	 */

	public static  String today(){
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMMdd" );
		return sdf.format(date);
	}
	/**
	 * 获得上个月
	 * @param year
	 * @param month
	 * @return
	 */

	public static  String beforMon(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		 cal.set(Calendar.YEAR,year);
		 cal.set(Calendar.MONTH, nowMonth-1);
		 cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 一个月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String lastDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);

		return sdf.format(cal.getTime());
	}

	/**
	 * 一个月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String firstDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(cal.getTime());
	}

	
	
	
	/**
	 * n分钟后
	 * @return
	 */
	public static  Calendar  afterDate(int n){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Calendar nowTime = Calendar.getInstance();
		 nowTime.add(Calendar.MINUTE, n);//n分钟后的时间
		return nowTime;
	}
	
}
