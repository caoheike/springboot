package com.reptile.service.accumulationfund;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.WebClientFactory;
import com.reptile.util.application;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class BaoJiFundService {
private Logger logger = LoggerFactory.getLogger(BaoJiFundService.class);
	
	@Autowired
	private application application;
	
	public  Map<String, Object> login(HttpServletRequest request,String idCard,String passWord,String cityCode,String idCardNum){
		WebClient webclient=new WebClientFactory().getWebClient();
		 Map<String, Object> map = new HashMap<String, Object>();
		 Map<String, Object> dataMap = new HashMap<String, Object>();
		
		try {
			webclient.getPage("http://61.134.23.147:7004/wscx/zfbzgl/zfbzsq/index.jsp");
			WebRequest request1 =new WebRequest(new URL("http://61.134.23.147:7004/wscx/zfbzgl/zfbzsq/login_hidden.jsp?pass="+passWord+"&zh="+idCard));
			request1.setHttpMethod(HttpMethod.GET);//提交方式
		 HtmlPage page1=	webclient.getPage(request1);
		  if(page1.asXml().contains("alert")){
			  String tip=page1.asXml().split("alert")[1].split("\\(")[1].split("\\)")[0];
			  map.put("errorCode", "0001");
			  map.put("errorInfo", tip);
			  return map;
		  }else{
			  PushState.state(idCardNum, "accumulationFund", 100);
			  System.out.println("登陆成功");
			  Map<String, String>  basicInfos=new HashMap<String, String>();//存放基本信息
			  String zgzh=page1.getElementByName("zgzh").getAttribute("value");
			  String sfzh=page1.getElementByName("sfzh").getAttribute("value");
			  String zgxm=page1.getElementByName("zgxm").getAttribute("value");
			  String dwbm=page1.getElementByName("dwbm").getAttribute("value");
              String url1="http://61.134.23.147:7004/wscx/zfbzgl/gjjxxcx/gjjxx_cx.jsp?zgzh="+zgzh+"&sfzh="+sfzh+"&zgxm="+URLEncoder.encode(zgxm, "gb2312")+"&dwbm="+dwbm+"&cxyd="+URLEncoder.encode("当前年度", "gb2312")+"&dbname=gjjmx9";
			  WebRequest requests1 = new WebRequest(new URL(url1));
			  requests1.setHttpMethod(HttpMethod.GET);
			  HtmlPage page2=webclient.getPage(requests1);	
			 // System.out.println(page2.asXml());//基本信息
			  HtmlTable table= (HtmlTable) page2.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[4]/tbody/tr/td/table/tbody/tr/td/table[1]").get(0);
			//  System.out.println(table.asXml());//宝鸡公积金基本信息(需要解析)
			  basicInfos=this.parseBasicInfos(table.asXml(), idCard, basicInfos);//基本信息
			  String url="http://61.134.23.147:7004/wscx/zfbzgl/gjjmxcx/gjjmx_cx.jsp";
			  List<NameValuePair> list = new ArrayList<NameValuePair>();
				//设置参数
				list.add(new NameValuePair("zgzh", zgzh));
				list.add(new NameValuePair("sfzh", sfzh));
				list.add(new NameValuePair("zgxm", URLEncoder.encode(zgxm, "gb2312")));
				list.add(new NameValuePair("dwbm", dwbm));
				list.add(new NameValuePair("cxyd", URLEncoder.encode("当前年度", "gb2312")));
				list.add(new NameValuePair("zgzt", null));
		    	HtmlPage detailPage=getPages(webclient, url, list, HttpMethod.POST); //当前年度详单	
			    Thread.sleep(3000);
			  //==============宝鸡所有明细==================
			      List<Map<String, String>> flows=new ArrayList<Map<String,String>>();//存放明细的list
				  HtmlTable tables=   (HtmlTable) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[1]/td/table").get(0);
				  HtmlElement totalPage =(HtmlElement) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[2]/td").get(0);
				  String details=totalPage.asText().split("共")[1].split("页")[0];
				  int num=new Integer(details);
				  if(num==1&&tables.asXml().contains("jtpsoft")){
					  flows=this.parseFlows(detailPage.asXml(), flows);
				  }else{
					 // System.out.println(detailPage.asText()+"-------------"); //当前年度第一页(需要解析)
					  flows=this.parseFlows(detailPage.asXml(), flows);
					  if(num>1&&tables.asXml().contains("jtpsoft")){
						//当前年度其余页
						  for(int j=1;j<num;j++){
							  
							  detailPage=(HtmlPage) detailPage.executeJavaScript("down()").getNewPage();
							  Thread.sleep(200);
							  flows=this.parseFlows(detailPage.asXml(), flows);
						  }
						  
					  }else{
						  flows.add(new HashMap<String, String>());//没有清单
					  }
				  }
				//=========================  其余年度明细==============
				  HtmlSelect select=detailPage.getElementByName("cxydone");
				  int seleNum=select.getChildElementCount();
				  try{
				  for (int i = 1; i < seleNum; i++) {
					   Thread.sleep(500);
					   detailPage=getPages2(webclient, url, select.getOption(i).asText(), select.getOption(i).asText(), "1", details, "当前年度", zgzh, sfzh, zgxm, dwbm, HttpMethod.POST);
					   Thread.sleep(500);
					   
					   tables=   (HtmlTable) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[1]/td/table").get(0);
					   Thread.sleep(500);
					   totalPage =(HtmlElement) detailPage.getByXPath("/html/body/table[2]/tbody/tr[1]/td[2]/table[5]/tbody/tr[2]/td").get(0);
					   Thread.sleep(500);
					   details=totalPage.asText().split("共")[1].split("页")[0];
					   num=new Integer(details);
					  if(num==1&&tables.asXml().contains("jtpsoft")){
						  flows=this.parseFlows(detailPage.asXml(), flows);
					  }else if(num>1){
						  for (int j = 1; j <= num; j++) {
							  Thread.sleep(100);
							  detailPage=getPages2(webclient, url, select.getOption(i).asText(), select.getOption(i).asText(), j+"", details, "当前年度", zgzh, sfzh, zgxm, dwbm, HttpMethod.POST);
							  flows=this.parseFlows(detailPage.asXml(), flows);
						  }
					  }  
				  }
				  dataMap.put("basicInfos", basicInfos);//基本信息
			      dataMap.put("flows", flows);//流水 
			      dataMap.put("loans","");//贷款
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				  map.put("userId", idCardNum);
			      System.out.println(idCardNum);
			      map.put("insertTime", sdf.format(new Date()));
			      map.put("city", cityCode);
			      map.put("cityName", "宝鸡市");
			      map.put("data", dataMap);  
			      //map=new Resttemplate().SendMessage(map,"http://192.168.3.16:8089/HSDC/person/accumulationFund");//张浩敏
			      map=new Resttemplate().SendMessage(map,ConstantInterface.port+"/HSDC/person/accumulationFund");
			      if(map!=null&&"0000".equals(map.get("errorCode").toString())){
				    	PushState.state(idCardNum, "accumulationFund",300);
				    	map.put("errorInfo","查询成功");
				    	map.put("errorCode","0000");
			          
			        }else{
			        	//--------------------数据中心推送状态----------------------
			        	PushState.state(idCardNum, "accumulationFund",200);
			        	//---------------------数据中心推送状态----------------------
			            map.put("errorInfo","查询失败");
			            map.put("errorCode","0001");
			        } 
				  }catch (Exception e) {
						logger.warn("宝鸡市公积金",e);
						map.put("errorCode", "0001");
			            map.put("errorInfo", "数据不全!");
						e.printStackTrace();
					}
			 
		  }
			  
		} catch (Exception e) {
			logger.warn("宝鸡市公积金",e);
			map.put("errorCode", "0001");
            map.put("errorInfo", "网络连接异常!");
			e.printStackTrace();
		}
		return map;
		
	}
	
     /**
	  * 查询其余年度的详单
	  * @param webClient
	  * @param url
	  * @param cxydone
	  * @param cxydtwo
	  * @param yss
	  * @param totalpages
	  * @param cxyd
	  * @param zgzh
	  * @param sfzh
	  * @param zgxm
	  * @param dwbm
	  * @param methot
	  * @return
	  * @throws FailingHttpStatusCodeException
	  * @throws IOException
	  */
	 public  HtmlPage getPages2(WebClient webClient,String url,String cxydone,String cxydtwo,String yss,String totalpages,String cxyd,String zgzh,String sfzh,String zgxm,String dwbm,HttpMethod methot) throws FailingHttpStatusCodeException, IOException{
		   List<NameValuePair> list=new ArrayList<NameValuePair>();
		  list.add(new NameValuePair("cxydone",cxydone ));
		  list.add(new NameValuePair("cxydtwo", cxydtwo));
		  list.add(new NameValuePair("yss",yss ));

		  list.add(new NameValuePair("totalpages",totalpages ));
		  list.add(new NameValuePair("cxyd",URLEncoder.encode(cxyd, "gb2312") ));
		  list.add(new NameValuePair("zgzh",zgzh));
		  
		  
		  list.add(new NameValuePair("sfzh",sfzh ));
		  list.add(new NameValuePair("zgxm",URLEncoder.encode(zgxm, "gb2312") ));
		  list.add(new NameValuePair("dwbm",dwbm));
	
		  return getPages(webClient, url, list, methot);
		 
	 } 

		/***
		 * 获得HtmlPage  
		 * @param webClient
		 * @param url 要访问的url
		 * @param list  参数
		 * @param methot  post、get
		 * @return
		 * @throws FailingHttpStatusCodeException
		 * @throws IOException
		 */
		 public   HtmlPage getPages(WebClient webClient,String url,List<NameValuePair> list,HttpMethod methot) throws FailingHttpStatusCodeException, IOException{
			
			    WebRequest requests = new WebRequest(new URL(url));
	            requests.setRequestParameters(list);
				requests.setHttpMethod(methot);
				
			  return webClient.getPage(requests);
			 
		 }	
	/**
	 * 基本信息的解析
	 * @param tableXml 包含基本信息的table
	 * @param idCard 身份证号
	 * @param basicInfos 存帆数据的map
	 * @return
	 */
        public Map<String, String> parseBasicInfos(String tableXml,String idCard,Map<String, String> basicInfos){
        	 Document  infotable=  Jsoup.parse(tableXml);  
			  Elements trs= infotable.getElementsByTag("tr");
			  Elements tds=null;
			  List<String> basicInfosList=new ArrayList<String>();
			  for (int i = 0; i < trs.size(); i++) {//行
				  tds=trs.get(i).getElementsByTag("td");
				  for (int j = 0; j < tds.size(); j++) {//列
					  //System.out.println(tds.get(j).text()+"---------"+i+"  "+j+"----------");
					  //筛选数据
					  if(i!=4){
						  if(j==1||j==3){
						  basicInfosList.add(tds.get(j).text().replace(" ", "").trim());
						  
						  }  
					  }else{
						  if(j==1||j==4){
								  basicInfosList.add(tds.get(j).text().replace(" ", "").trim());
						  }
					  }
					  
				}
				
			}
			  basicInfos.put("name", basicInfosList.get(0).trim());//姓名
			  basicInfos.put("personFundCard", basicInfosList.get(1).trim());//个人公积金卡号
			  basicInfos.put("idCard", idCard);//身份证号码
			  basicInfos.put("personFundAccount", basicInfosList.get(3).trim());//个人公积金账号
			  basicInfos.put("companyFundAccount","");//单位公积金账号
			  basicInfos.put("companyName",basicInfosList.get(4).trim());//公司名称
			  basicInfos.put("status",basicInfosList.get(7).trim());//状态
			  basicInfos.put("baseDeposit", basicInfosList.get(8).replace(",", "").replace("元", "").trim());//缴费基数
			  String ratio=basicInfosList.get(9).toString();
			  basicInfos.put("companyRatio",ratio.split("\\/")[1].trim());//公司缴费比例
			  basicInfos.put("personRatio", ratio.split("\\/")[0].trim());//个人缴费比例
			  basicInfos.put("personDepositAmount", basicInfosList.get(14).replace(",", "").replace("元", "").trim());//个人缴费金额
			  basicInfos.put("companyDepositAmount",basicInfosList.get(12).replace(",", "").replace("元", "").trim());//公司缴费金额
			  basicInfos.put("balance",basicInfosList.get(19).replace(",", "").replace("元", "").trim());//余额
			  basicInfos.put("lastDepositDate", basicInfosList.get(20).trim());//最后缴费日期	  
			return basicInfos;
        }
     /**   
      * 流水明细解析
      * @param detailXml 要解析的页面xml
      * @param flows 存放流水的list
      * @return
      */
      public List<Map<String, String>>  parseFlows(String detailXml,List<Map<String, String>> flows){
    	  Document  infotable=  Jsoup.parse(detailXml);  
		  Elements trs= infotable.getElementsByClass("jtpsoft");
		  Elements tds=null;
		  for (int i = 2; i < trs.size(); i++) {//行
			  tds=trs.get(i).getElementsByTag("td");
			  Map<String, String> flow=new HashMap<String, String>();
			  if(tds.get(5).text().contains("汇缴")||tds.get(5).text().contains("补缴")){
			   flow.put("operatorDate", tds.get(0).text());//操作时间
			   flow.put("bizDesc", tds.get(5).text());//业务描述
				if(tds.get(5).text().contains("汇缴")){
					flow.put("type", "汇缴");
				}else if (tds.get(5).text().contains("补缴")) {
					flow.put("type", "补缴");
				} else{
					flow.put("type", "");
				}
				flow.put("payMonth", tds.get(0).text().split("-")[0]+tds.get(0).text().split("-")[1]);//缴费月份
				
			    flow.put("amount", tds.get(2).text());
			    flows.add(flow);
		  }
			  
		}
		return flows;
    	  
      }
}
