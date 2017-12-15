package com.reptile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CountTime {
	public static boolean getCountTime(String timeCnt) throws ParseException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String newTime = sdf.format(date);
		Date newDate = sdf.parse(newTime);
		Date cntDate = sdf.parse(timeCnt);
		int days = (int) ((newDate.getTime()-cntDate.getTime())/(1000*60*60*24));
		if(days>30) {
			return true;
		}else {
			return false;
		}
		
	}
}
