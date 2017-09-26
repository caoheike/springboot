package com.reptile.springboot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlS;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import scala.annotation.migration;

public class lbtest1 {
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webclient=new WebClient(BrowserVersion.EDGE);
		webclient.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage htmlpage=  webclient.getPage("https://www.taobao.com/");
		HtmlTextInput htmlTextInput= (HtmlTextInput) htmlpage.getElementById("q");
//		HtmlForm htmlForm=(HtmlForm) htmlpage.getElementById("J_TSearchForm");
//		htmlForm.getInputByValue("搜索");
	
		
//		HtmlInput inputByValue = htmlForm.getInputByValue("搜索");
		  
	      DomNodeList<DomElement> elementsByTagName = htmlpage.getElementsByTagName("button");
	      

	     for (Iterator iterator = elementsByTagName.iterator(); iterator.hasNext();) {
			DomElement domElement = (DomElement) iterator.next();
			System.out.println(domElement);
			
		}
		//htmlForm.getButtonByName("");
		//HtmlSubmitInput htmlSubmitInput =htmlpage.getElementByName("");
		//System.out.println(htmlForm.asText());
		
		
//		HtmlPage page=webclient.getPage("https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&wq=%E6%89%8B%E6%9C%BA&pvid=ab4a304f67ba498d9cf35ed744b3511c");
//		HtmlDivision divison= (HtmlDivision) page.getElementById("service-2014");
	//	System.out.println(divison.asXml());
		//System.out.println(page.asText());
		
		//http://query.xazfgjj.gov.cn/index.jsp?urltype=tree.TreeTempUrl&wbtreeid=1172
//		HtmlPage page= webclient.getPage("https://s.taobao.com/search?q="+123+"&imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&spm=a21bo.1000386.201856-taobao-item.1&ie=utf8&initiative_id=tbindexz_20170818");
//		System.out.println(page.asXml());
		
	}
}
