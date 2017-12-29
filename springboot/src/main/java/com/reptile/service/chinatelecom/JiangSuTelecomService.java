package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 江苏电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class JiangSuTelecomService {
    private Logger logger= LoggerFactory.getLogger(JiangSuTelecomService.class);
    public Map<String, Object> getDetailMes(HttpServletRequest request, String phoneNumber, String userPassword,String longitude,String latitude,String uuid) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushSocket.pushnew(map, uuid, "1000","登录中");
        PushState.state(phoneNumber, "callLog",100);
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        List<String> dataList=new ArrayList<String>();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNumber, "callLog",200);
            PushSocket.pushnew(map, uuid, "3000","操作异常!");       
            } else {

            	PushSocket.pushnew(map, uuid, "2000","登录成功");
            	WebClient webClient = (WebClient) attribute;
            try {
            	Thread.sleep(2000);
            	PushSocket.pushnew(map, uuid, "5000","获取数据中"); 
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/frontLinkSkip.do?method=skip&shopId=10011&toStUrl=http://js.189.cn/service/bill?tabFlag=billing4"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);

                SimpleDateFormat sim=new SimpleDateFormat("yyyyMMdd");

                Calendar cal=Calendar.getInstance();
                String endTime=sim.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH,1);
                String startTime=sim.format(cal.getTime());

                WebRequest request1=new WebRequest(new URL("http://js.189.cn/queryVoiceMsgAction.action"));
                request1.setHttpMethod(HttpMethod.POST);
                List<NameValuePair> list=new ArrayList<NameValuePair>();
                list.add(new NameValuePair("inventoryVo.accNbr",phoneNumber));
                list.add(new NameValuePair("inventoryVo.getFlag","0"));
                list.add(new NameValuePair("inventoryVo.begDate",startTime));
                list.add(new NameValuePair("inventoryVo.endDate",endTime));
                list.add(new NameValuePair("inventoryVo.family","4"));
                list.add(new NameValuePair("inventoryVo.accNbr97",""));
                list.add(new NameValuePair("inventoryVo.productId","4"));
                list.add(new NameValuePair("inventoryVo.acctName",phoneNumber));
                request1.setRequestParameters(list);
                UnexpectedPage page = webClient.getPage(request1);
                dataList.add(page.getWebResponse().getContentAsString());
                Thread.sleep(500);
                int boundCount=5;
                for (int i=0;i<boundCount;i++){

                    cal.add(Calendar.DAY_OF_MONTH,-1);
                    endTime=sim.format(cal.getTime());
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    startTime=sim.format(cal.getTime());

                    request1=new WebRequest(new URL("http://js.189.cn/queryVoiceMsgAction.action"));
                    request1.setHttpMethod(HttpMethod.POST);
                    list=new ArrayList<NameValuePair>();
                    list.add(new NameValuePair("inventoryVo.accNbr",phoneNumber));
                    list.add(new NameValuePair("inventoryVo.getFlag","1"));
                    list.add(new NameValuePair("inventoryVo.begDate",startTime));
                    list.add(new NameValuePair("inventoryVo.endDate",endTime));
                    list.add(new NameValuePair("inventoryVo.family","4"));
                    list.add(new NameValuePair("inventoryVo.accNbr97",""));
                    list.add(new NameValuePair("inventoryVo.productId","4"));
                    list.add(new NameValuePair("inventoryVo.acctName",phoneNumber));
                    request1.setRequestParameters(list);
                    page = webClient.getPage(request1);
                    dataList.add(page.getWebResponse().getContentAsString());
                    Thread.sleep(500);
                }
                PushSocket.pushnew(map, uuid, "6000","获取数据成功"); 
                map.put("data",dataList);
                map.put("errorCode","0000");
                map.put("errorInfo","成功");
                map.put("flag","4");
                map.put("UserPassword",userPassword);
                map.put("UserIphone",phoneNumber);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);
                Resttemplate rest=new Resttemplate();
                map= rest.SendMessage(map, ConstantInterface.port + "HSDC/message/telecomCallRecord");
                String errorCode="errorCode";
                String resultValid="0000";
            	if(map.get(errorCode).equals(resultValid)) {
					PushSocket.pushnew(map, uuid, "8000","认证成功");
					 PushState.state(phoneNumber, "callLog",300);
				}else {
					PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
					 PushState.state(phoneNumber, "callLog",200);
				}
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  江苏详单获取  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushState.state(phoneNumber, "callLog",200);
                PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
            }finally {
                if(webClient!=null){
                    webClient.close();
                }
            }
        }
        return map;
    }

    /**
     * 获取当前日期及前5个月的月初月末
     */
    public static void main(String[] args) {
        SimpleDateFormat sim=new SimpleDateFormat("yyyyMMdd");
        Calendar cal=Calendar.getInstance();
        Date time = cal.getTime();
        String endTime=sim.format(time);
        cal.set(Calendar.DAY_OF_MONTH,1);
        time=cal.getTime();
        String startTime=sim.format(time);
        System.out.println(startTime+"   "+endTime);
        int boundCount=5;
        for(int i=0;i<boundCount;i++){
          cal.add(Calendar.DAY_OF_MONTH,-1);
          endTime=sim.format(cal.getTime());
          cal.set(Calendar.DAY_OF_MONTH,1);
          startTime=sim.format(cal.getTime());
          System.out.println(startTime+"   "+endTime);
        }
    }
}
