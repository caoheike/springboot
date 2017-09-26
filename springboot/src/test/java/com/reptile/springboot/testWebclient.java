package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.sf.json.JSONObject;

public class testWebclient {
	
	public static void main(String [] ager) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ImagerUrl","D://CebloginImger.png");
		Map<String,Object> seo=new HashMap<String, Object>();
		seo.put("data", map);
		System.out.println(JSONObject.fromObject(seo));
		
		
	}
}
