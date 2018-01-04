package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.*;
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

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 陕西电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
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
    public Map<String, Object>  telecomLogin(HttpServletRequest request, String phoneNumber, String serverPwd,String longitude,String latitude,String uuid) throws IOException, InterruptedException {

        Map<String, Object> map = new HashMap<String, Object>(16);
        Thread.sleep(2000);
        List<String> dataList = new ArrayList<String>();
        HttpSession session = request.getSession();
        PushState.state(phoneNumber, "callLog",100);
        PushSocket.pushnew(map, uuid, "1000","登录中");

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(phoneNumber, "callLog",200,"登录失败，操作异常");
            PushSocket.pushnew(map, uuid, "3000","登录失败，操作异常");
        } else {
        	PushSocket.pushnew(map, uuid, "2000","登录成功");
            WebClient webClient = (WebClient) attribute;
            try {
            	Thread.sleep(2000);
            	PushSocket.pushnew(map, uuid, "5000","获取数据中");
                HtmlPage logi = webClient.getPage("http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000202");
                Thread.sleep(3000);

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
                HtmlPage infopage = webClient.getPage(webRequest);
                String judge="无话单记录";
                if(!infopage.asXml().contains("无话单记录")&&!infopage.asXml().contains("欢迎登录")){
                    dataList.add(infopage.asXml());
                }
                Thread.sleep(1000);
                int count=5;
                for (int i = 0; i < count; i++) {
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
                    infopage = webClient.getPage(webRequest);
                    Thread.sleep(1000);
                    if(!infopage.asXml().contains("无话单记录")&&!infopage.asXml().contains("欢迎登录")){
                        dataList.add(infopage.asXml());
                    }

                }
                if(dataList.size()<3){
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "数据过程中出现未知错误!请重试！");
                    PushState.state(phoneNumber, "callLog",200,"数据过程中出现未知错误!请重试！");
                    PushSocket.pushnew(map, uuid, "7000","数据过程中出现未知错误!请重试！");
                    return map;
                }
                	PushSocket.pushnew(map, uuid, "6000","获取数据成功");
                    map.put("UserIphone",phoneNumber);
                    map.put("UserPassword",serverPwd);
                    //经度
                    map.put("longitude", longitude);
                    //纬度
                    map.put("latitude", latitude);
                    map.put("data",dataList);
                    map.put("flag","0");
                    Resttemplate resttemplate=new Resttemplate();
                    map= resttemplate.SendMessage(map, ConstantInterface.port+"/HSDC/message/telecomCallRecord");
                    String resultCon="0000";
                    String errorCodeInfo="errorCode";
                    if(resultCon.equals(map.get(errorCodeInfo))) {
    					PushSocket.pushnew(map, uuid, "8000","认证成功");
    					PushState.state(phoneNumber, "callLog",300);
    				}else {
    					PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
    					PushState.state(phoneNumber, "callLog",200,map.get("errorInfo").toString());
    				}
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  陕西详单获取  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushState.state(phoneNumber, "callLog",200,"网络连接异常!");
                PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
            }finally {
                if(webClient!=null){
                    webClient.close();
                }
            }
        }
        return map;
    }
}


