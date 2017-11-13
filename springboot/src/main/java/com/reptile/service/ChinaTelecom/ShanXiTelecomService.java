package com.reptile.service.ChinaTelecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.Resttemplate;
import com.reptile.util.talkFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ShanXiTelecomService {
    private Logger logger= LoggerFactory.getLogger(ShanXiTelecomService.class);
    /**
     * 西安电信
     *
     * @param request
     * @param
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, Object>  TelecomLogin(HttpServletRequest request, String phoneNumber, String serverPwd,String longitude,String latitude,String UUID) throws IOException, InterruptedException {

        Map<String, Object> map = new HashMap<String, Object>();
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
        } else {
            try {
                WebClient webClient = (WebClient) attribute;

                HtmlPage logi = webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
                WebRequest webRequest = new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));

                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");

                Calendar cal = Calendar.getInstance();
                String endTime = sim.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, 1);
                String startTime = sim.format(cal.getTime());

                List<NameValuePair> reqParamsinfo = new ArrayList<NameValuePair>();
                reqParamsinfo.add(new NameValuePair("currentPage", "1"));
                reqParamsinfo.add(new NameValuePair("pageSize", "100"));
                reqParamsinfo.add(new NameValuePair("effDate", startTime));
                reqParamsinfo.add(new NameValuePair("expDate", endTime));
                reqParamsinfo.add(new NameValuePair("serviceNbr", phoneNumber));
                reqParamsinfo.add(new NameValuePair("operListID", "1"));
                reqParamsinfo.add(new NameValuePair("isPrepay", "0"));
                reqParamsinfo.add(new NameValuePair("pOffrType", "481"));
                webRequest.setHttpMethod(HttpMethod.POST);
                webRequest.setRequestParameters(reqParamsinfo);
                HtmlPage Infopage = webClient.getPage(webRequest);
                System.out.print(Infopage.asXml());
                if(!Infopage.asXml().contains("无话单记录")){
                    dataList.add(Infopage.asXml());
                }
                Thread.sleep(1000);
                for (int i = 0; i < 5; i++) {
                    cal.add(Calendar.DAY_OF_MONTH,-1);
                    endTime=sim.format(cal.getTime());
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    startTime=sim.format(cal.getTime());

                    webRequest = new WebRequest(new URL("http://sn.189.cn/service/bill/feeDetailrecordList.action"));
                    reqParamsinfo = new ArrayList<NameValuePair>();
                    reqParamsinfo.add(new NameValuePair("currentPage", "1"));
                    reqParamsinfo.add(new NameValuePair("pageSize", "1000"));
                    reqParamsinfo.add(new NameValuePair("effDate", startTime));
                    reqParamsinfo.add(new NameValuePair("expDate", endTime));
                    reqParamsinfo.add(new NameValuePair("serviceNbr", phoneNumber));
                    reqParamsinfo.add(new NameValuePair("operListID", "1"));
                    reqParamsinfo.add(new NameValuePair("isPrepay", "0"));
                    reqParamsinfo.add(new NameValuePair("pOffrType", "481"));
                    webRequest.setHttpMethod(HttpMethod.POST);
                    webRequest.setRequestParameters(reqParamsinfo);
                    Infopage = webClient.getPage(webRequest);
                    System.out.print(Infopage.asXml());
                    Thread.sleep(1000);
                    if(!Infopage.asXml().contains("无话单记录")){
                        dataList.add(Infopage.asXml());
                    }

                }
                if(dataList.size()!=0){
                	//---------------推-------------------
                	 PushSocket.push(map, UUID, "0000");
    					//---------------推-------------------
                    map.put("UserIphone",phoneNumber);
                    map.put("UserPassword",serverPwd);
                    map.put("longitude", longitude);//经度
                    map.put("latitude", latitude);//纬度
                    map.put("data",dataList);
                    map.put("flag","0");
                    Resttemplate resttemplate=new Resttemplate();
                    map= resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
                }else{
                	//---------------推-------------------
                	 PushSocket.push(map, UUID, "0001");
    					//---------------推-------------------
                    map.put("errorCode", "0005");
                    map.put("errorInfo", "业务办理失败！");
                }

            } catch (Exception e) {
                logger.warn(e.getMessage()+"  陕西详单获取  mrlu",e);
                e.printStackTrace();
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }
}


