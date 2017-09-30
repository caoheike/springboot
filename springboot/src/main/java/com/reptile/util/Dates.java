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
}
