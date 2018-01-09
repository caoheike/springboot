package com.reptile.service.chinatelecom;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.ConstantInterface;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 山东电信
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class ShanDongTelecomService {
    private Logger logger= LoggerFactory.getLogger(ShanDongTelecomService.class);
    //山东省

    /**
     * 获得图片验证码
     *
     * @param request
     * @param
     * @return
     */
    public Map<String, Object> getImageCode(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, String> dataMap = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("GBmobile-webclient");

        if (attribute == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                WebRequest requests = new WebRequest(new URL("http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10016&toStUrl=http://sd.189.cn/selfservice/account/returnAuth?columnId=0210"));
                requests.setHttpMethod(HttpMethod.GET);
                HtmlPage page1 = webClient.getPage(requests);
                Thread.sleep(2000);
                File file = new File(request.getServletContext().getRealPath("/SDDXimageCode"));
                if (!file.exists()) {
                    file.mkdirs();
                }

                String fileName = "SD" + System.currentTimeMillis() + ".png";
                HtmlImage imageCode = (HtmlImage) page1.getElementById("rand_rn");
                BufferedImage read = imageCode.getImageReader().read(0);
                ImageIO.write(read, "png", new File(file, fileName));

                dataMap.put("CodePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/SDDXimageCode/" + fileName);
                map.put("data", dataMap);
                map.put("errorCode", "0000");
                map.put("errorInfo", "验证码获取成功!");
                session.setAttribute("SDDXwebclient", webClient);
                session.setAttribute("SDDXhtmlPage", page1);
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  山东获取验证码  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }


//    public Map<String, Object> reGetImageCode(HttpServletRequest request) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        Map<String, String> dataMap = new HashMap<String, String>();
//        HttpSession session = request.getSession();
//
//        Object attribute = session.getAttribute("SDDXwebclient");
//
//        if (attribute == null) {
//            map.put("errorCode", "0001");
//            map.put("errorInfo", "操作异常!");
//        } else {
//            try {
//                WebClient webClient = (WebClient) attribute;
//
//
//                File file = new File(request.getServletContext().getRealPath("/SDDXimageCode"));
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//                String fileName = "SD" + System.currentTimeMillis() + ".png";
//
//                UnexpectedPage page1 = webClient.getPage("http://sd.189.cn/selfservice/validatecode/codeimg.jpg?" + Math.random());
//                InputStream inputStream = page1.getInputStream();
//                BufferedImage read = ImageIO.read(inputStream);
//                ImageIO.write(read, "png", new File(file, fileName));
//
//                dataMap.put("CodePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/SDDXimageCode/" + fileName);
//                map.put("data", dataMap);
//                map.put("errorCode", "0000");
//                map.put("errorInfo", "验证码获取成功!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                map.put("errorCode", "0001");
//                map.put("errorInfo", "网络连接异常!");
//            }
//        }
//        return map;
//    }

    public Map<String, Object> sendPhoneCode(HttpServletRequest request, String imageCode) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, String> dataMap = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("SDDXwebclient");
        Object htmlpage = session.getAttribute("SDDXhtmlPage");

        if (attribute == null || htmlpage == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                HtmlPage page = (HtmlPage) htmlpage;
                page.getElementById("validatecode_2busi").setAttribute("value", imageCode);
                HtmlPage sendMesPage = page.getElementById("getDynamicHref_rn").click();
                Thread.sleep(500);
                String validateError="您输入的验证码错误！";
                if (sendMesPage.asText().contains(validateError)) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "您输入的验证码错误!");
                    return map;
                }
                validateError="验证码不能为空";
                if (sendMesPage.asText().contains(validateError)) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "验证码不能为空!");
                    return map;
                }

                HtmlPage easyDialogYesBtn = sendMesPage.getElementById("easyDialogYesBtn").click();
                Thread.sleep(500);
                validateError="短信随机密码已发送到您的手机";
                if (sendMesPage.asText().contains(validateError)) {
                    map.put("errorCode", "0000");
                    map.put("errorInfo", "短信发送成功");

                } else {
                    map.put("errorCode", "0002");
                    map.put("errorInfo", "短信发送失败");
                }
                session.setAttribute("SDDXsendMesPage", easyDialogYesBtn);
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  山东发送手机验证码  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
            }
        }
        return map;
    }

    public Map<String, Object> getDetailMes(HttpServletRequest request,String userIphone, String imageCode, String userName,
                                            String userCard, String phoneCode, String userPassword,String longitude,String latitude,String uuid){
        Map<String, Object> map = new HashMap<String, Object>(16);
        PushSocket.pushnew(map, uuid, "1000","登录中");
        PushState.state(userIphone, "callLog",100);
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        List<String> listData = new ArrayList<String>();
        HttpSession session = request.getSession();

        Object attribute = session.getAttribute("SDDXwebclient");
        Object htmlpage = session.getAttribute("SDDXsendMesPage");

        if (attribute == null || htmlpage == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "操作异常!");
            PushState.state(userIphone, "callLog",200,"登录失败，操作异常!");
            PushSocket.pushnew(map, uuid, "3000","登录失败，操作异常!");
        } else {
            try {
                WebClient webClient = (WebClient) attribute;
                HtmlPage page = (HtmlPage) htmlpage;

                page.getElementById("validatecode_2busi").setAttribute("value", imageCode);
                page.getElementById("username_2busi").setAttribute("value", userName);
                page.getElementById("credentials_no_2busi").setAttribute("value", userCard);
                page.getElementById("randomcode_2busi").setAttribute("value", phoneCode);

                HtmlPage aa = page.getElementById("submit_btn_rn").click();
                Thread.sleep(1000);
                String   validateError="您的客户信息校验成功，您可继续办理相关业务";
                if (!aa.asText().contains(validateError)) {
                    map.put("errorCode", "0001");
                    map.put("errorInfo", "您的用户信息校验未通过，请确认后重新输入");
                    PushSocket.pushnew(map, uuid, "3000","您的用户信息校验未通过，请确认后重新输入");
                    return map;
                }
                PushSocket.pushnew(map, uuid, "2000","登录成功");
                
                HtmlPage resultPage = aa.getElementById("easyDialogYesBtn").click();
                Thread.sleep(2000);
                PushSocket.pushnew(map, uuid, "5000","获取数据中");
                List<String> list = new ArrayList<String>();
                CollectingAlertHandler alert = new CollectingAlertHandler(list);
                webClient.setAlertHandler(alert);
                Calendar cal = Calendar.getInstance();

                SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
                String date = sim.format(cal.getTime());
                int boundCount=6;
                for (int i = 0; i < boundCount; i++) {
                    resultPage.executeJavaScript("\tvar params = {\n" +
                            "    \taccNbr: \""+userIphone+"\",//固定电话需带区号，移动电话不带区号\n" +
                            "\t    billingCycle:'" + date + "',//详单月份：格式yyyyMM\n" +
                            "\t    pageRecords:'20',//每页记录数：当值为-1时表示查询所有记录\n" +
                            "\t    pageNo:-1 + '',//页码：值为-1时表示查询所有记录\n" +
                            "\t    qtype:\"0\",\n" +
                            "\t    totalPage:\"3\" + '',\n" +
                            "\t    queryType:\"4\" + ''\n" +
                            "   \t};\n" +
                            "\tvar url = selfServCommon.rootPath + \"/bill/queryBillDetail\";\n" +
                            "\tvar myArray=new Array();\n" +
                            "\t$.callServiceAsJson(url, params, {\n" +
                            "\t\tcallback : function(response) {\n" +
                            "\t\t\tvar jsonObj = response.result.items;\n" +
                            "\t\t\tvar len=jsonObj.length\n" +
                            "\t\t\tfor(var i=0;i<len;i++){\n" +
                            "          var str=\"{ billingNbr:'\"+jsonObj[i].billingNbr+\"',callType:'\"+jsonObj[i].callType+\"',calledNbr:'\"+jsonObj[i].calledNbr+\"',callingNbr:'\"+jsonObj[i].callingNbr+\n" +
                            "          \"',charge:'\"+jsonObj[i].charge+\"',duration:'\"+jsonObj[i].duration+\"',intf_charge:'\"+jsonObj[i].intf_charge\n" +
                            "          +\"',intf_startTime:'\"+jsonObj[i].intf_startTime+\"',position:'\"+jsonObj[i].position+\"',startTime:'\"+jsonObj[i].startTime+\"',eventType:'\"+jsonObj[i].eventType+\"'}\"\n" +
                            "      myArray[i]=str;\t\t}\n" +
                            "      alert(myArray)\n" +
                            "\t\t}\n" +
                            "\t});");
                    cal.add(Calendar.MONTH,-1);
                    date=sim.format(cal.getTime());
                    Thread.sleep(4000);
                }
                for (int i = 0; i < list.size(); i++) {
                    listData.add(list.get(i));
                }
                
                PushSocket.pushnew(map, uuid, "6000","获取数据成功"); 
                map.put("UserPassword", "'"+userPassword+"'");
                map.put("UserIphone", userIphone);
                //经度
                map.put("longitude", longitude);
                //纬度
                map.put("latitude", latitude);
                map.put("flag", "3");
                map.put("data", listData);
                map.put("errorCode", "0000");
                map.put("errorInfo", "'查询成功'");
                webClient.close();
                Resttemplate resttemplate=new Resttemplate();
                map = resttemplate.SendSDYDMessage(map, ConstantInterface.port+"/HSDC/message/SdTelecomCallRecord");
                validateError="errorCode";
                String resultCount="0000";
                if(map.get(validateError).equals(resultCount)) {
					PushSocket.pushnew(map, uuid, "8000","认证成功");
					 PushState.state(userIphone, "callLog",300);
				}else {
					PushSocket.pushnew(map, uuid, "9000",map.get("errorInfo").toString());
					 PushState.state(userIphone, "callLog",200,map.get("errorInfo").toString());
				}
            } catch (Exception e) {
                logger.warn(e.getMessage()+"  山东获取详单信息  mrlu",e);
                map.put("errorCode", "0001");
                map.put("errorInfo", "网络连接异常!");
                PushState.state(userIphone, "callLog",200,"网络连接异常!");
                PushSocket.pushnew(map, uuid, "9000","网络连接异常!");
            }
        }
        return map;
    }
}
