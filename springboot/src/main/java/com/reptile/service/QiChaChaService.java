package com.reptile.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
/**
 * 
 * @author 刘彬
 * @date 2017/12/12
 * 
 *
 */

@Service
public class QiChaChaService {
	
	
	public  Map<String,Object>qichacha(HttpServletRequest request,String findName){
		 try {
			 	System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);
				WebDriver driver = new ChromeDriver();

				driver.get("http://www.qichacha.com/");
				driver.navigate().refresh();
				//隐式等待
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				driver.findElement(By.id("searchkey")).sendKeys(findName);
				
				driver.findElement(By.id("V3_Search_bt")).click();
				Map<Object,String>map=new HashMap<Object,String>(50);
				WebElement searchlist=driver.findElement(By.id("searchlist"));
				List<WebElement> a= searchlist.findElements(By.tagName("a"));
				for (int i = 0; i < a.size(); i++) {
					if (a.get(i).getText().contains(findName)) {
						a.get(i).click();
						break;
					}
				}
				Thread.sleep(3000);
				
				String currentHandle = driver.getWindowHandle();  
		        Set<String> handles = driver.getWindowHandles();  
		        System.out.println("currentHandle:"+currentHandle+"handles:"+handles.toString()+"driver.getTitle():"+driver.getTitle());
		        for (String s : handles) {  
		            if (s.equals(currentHandle))  
		                continue;  
		            else {  
		                driver.switchTo().window(s);
		                if (driver.getTitle().contains(findName)) {  
		                	System.out.println(driver.getTitle());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[1]/span")).getText()+"====公司名称===");
		                	map.put("companyName", driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[1]/span")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[4]/span")).getText()+"====电话===");
		                	map.put("phone", driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[4]/span")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='company-top']/div/div[2]/div[3]/span[2]/a")).getText()+"====官网===");
		                	map.put("officialWebsite", driver.findElement(ByXPath.xpath("//*[@id='company-top']/div/div[2]/div[3]/span[2]/a")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='company-top']/div/div[2]/div[3]/span[4]/a")).getText()+"====邮箱===");
		                	map.put("email", driver.findElement(ByXPath.xpath("//*[@id='company-top']/div/div[2]/div[3]/span[4]/a")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[3]/span")).getText()+"====地址===");
		                	map.put("address", driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[3]/span")).getText());
		                	
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[5]/td[2]")).getText()+"====税号===");
		                	map.put("taxpayerNumber", driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[5]/td[2]")).getText());
		                	
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[6]/span")).getText()+"====银行账户===");
		                	map.put("bankAccount", driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[6]/span")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[5]/span")).getText()+"====开户行===");
		                	map.put("account", driver.findElement(ByXPath.xpath("//*[@id='fapiao-title']/div[2]/div[3]/p[5]/span")).getText());
		                	
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[3]/td[2]")).getText()+"====注册资本===");
		                	map.put("registered", driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[3]/td[2]")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[3]/td[4]")).getText()+"====成立时间===");
		                	map.put("establishmentTime", driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[3]/td[4]")).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[4]/td[2]")).getText()+"====经营状态===");
		                	map.put("operating", driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[4]/td[2]")).getText());
		                	
		                	
		                	System.out.println(driver.findElements(ByClassName.className("ma_left")).get(39).getText()+"====经营范围===");
		                	map.put("businessScope", driver.findElements(ByClassName.className("ma_left")).get(39).getText());
		                	
		                	System.out.println(driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[2]/td[1]/div/div[2]/a[1]")).getText()+"====法定代表人===");
		                	map.put("legalPerson", driver.findElement(ByXPath.xpath("//*[@id='Cominfo']/table/tbody/tr[2]/td[1]/div/div[2]/a[1]")).getText());
		                	
		                	Document page1= Jsoup.parse(driver.getPageSource());
		                	Elements table = page1.getElementsByClass("m_changeList");
			                Elements tr =table.get(1).getElementsByTag("tr");
			                List<Map<String,Object>> trs = new ArrayList<Map<String,Object>>();
		                    for (int i = 1; i < tr.size(); i++) {
		                	 	Elements td =  tr.get(i).select("td");
								Map<String,Object> tds=new HashMap<String,Object>(124);
							for (int n = 0; n< td.size(); n++) {
								switch (n) {
								case 0:
									tds.put("shareholders", td.get(n).text());
									break;
								case 1:
									tds.put("stake", td.get(n).text());
									break;
								case 2:
									tds.put("subscribedCapitalContribution", td.get(n).text());
									break;
								case 3:
									tds.put("dateOfPaymentOfCapitalContribution", td.get(n).text());
									break;
								case 4:
									tds.put("typesOfShareholders", td.get(n).text());
									break;
									
								default:
									break;
								}
							}
								trs.add(tds);
							}
		                    Elements trj =table.get(2).getElementsByTag("tr");
		                    for ( int i=1; i<trj.size(); i++){  
								Elements tdj =  trj.get(i).select("td");  
								Map<String,Object> tdsj=new HashMap<String,Object>(125);
								for (int n = 0; n< tdj.size(); n++) {
									switch (n) {
									case 0:
										tdsj.put("name", tdj.get(n).text());
										break;
									case 1:
										tdsj.put("duty", tdj.get(n).text());
										break;
									
										
									default:
										break;
									}
								}
								trs.add(tdsj);
							}
		               System.out.println(trs);
		               System.out.println(map);
		               driver.close();
		               Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
		            }  else{
		            	System.out.println("没找到");
		            }
		          }
		        } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}
