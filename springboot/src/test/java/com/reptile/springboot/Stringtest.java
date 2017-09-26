package com.reptile.springboot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Stringtest {
	public static void main(String[] args) {
		 String str = "<table border='0' cellspacing='0' cellpadding='0' class='tab_one'>"+
				" <tbody>"
				+ ""
		 					;
		 
	        Document doc = Jsoup.parse(str);
	        Elements trs = doc.select("table").select("tr");
	        for(int i = 0;i<trs.size();i++){
	            Elements tds = trs.get(i).select("td");
	            for(int j = 0;j<tds.size();j++){
	           
	                String text = tds.get(j).text();
	                System.out.println(text);
	            }
	        }
	}
}
