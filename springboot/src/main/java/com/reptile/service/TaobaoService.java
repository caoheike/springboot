package com.reptile.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.WebClientFactory;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


@Service
public class TaobaoService {
    private static String loadUrl = "https://login.taobao.com/member/login.jhtml?f=top&redirectURL=https%3A%2F%2Fwww.taobao.com%2F";

    public Map<String, String> loadTaoBao(HttpServletRequest request, String userAccount, String passWord) {
        WebClient webClient = new WebClientFactory().getWebClient();


        try {
            HtmlPage page = webClient.getPage(loadUrl);
            Thread.sleep(4000);
            System.out.println(page.asXml());
            HtmlPage cloPage = page.getAnchorByText("密码登录").click();
            Thread.sleep(3000);
            System.out.println(cloPage.asXml());
            cloPage.getElementByName("TPL_username").setAttribute("value", userAccount);
            cloPage.getElementByName("TPL_username").blur();
            cloPage.getElementByName("TPL_password").setAttribute("value", passWord);


            String attribute = cloPage.getElementById("nocaptcha").getAttribute("style");
            System.out.println(cloPage.asXml());
            //判断页面是否存在滑块验证码
            if (attribute != null && attribute.contains("display: block;")) {
                System.out.println("此处有滑动验证码");
                HtmlPage newPage = (HtmlPage) cloPage.executeJavaScript("\tvar slide1=\tdocument.getElementById(\"nc_1__bg\");\n" +
                        "\tvar slide2=document.getElementById(\"nc_1_n1z\");\n" +
                        "\tslide1.onmousedown\n" +
                        "\tvar i=0;\n" +
                        "function timeOut(){\n" +
                        "\n" +
                        "\tvar slide1=\tdocument.getElementById(\"nc_1__bg\");\n" +
                        "\tvar slide2=document.getElementById(\"nc_1_n1z\");\n" +
                        "\t\n" +
                        "\tslide1.style=\"width: \"+i+\"px;\";\n" +
                        "\tslide2.style=\"left: \"+i+\"px;\";\n" +
                        "\tif(i==258){\n" +
                        "\t\tslide2.onmouseup;\n" +
                        "\t\twindow.clearTimeout(id);\n" +
                        "\t}\n" +
                        "\ti=i+6;\n" +
                        "}\n" +
                        "var id=window.setInterval(timeOut,100)").getNewPage();
                Thread.sleep(5000);
                System.out.println(newPage.asXml());
                HtmlPage hehe = newPage.getElementById("nc_1_n1z").click();
                System.out.println(hehe.asXml());
                boolean flag = hehe.asText().contains("验证通过");
                System.out.println(flag);

            }
            HtmlPage loadedPage = cloPage.getElementById("J_SubmitStatic").click();
            Thread.sleep(15000);
            System.out.println(loadedPage.asXml());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> loadTaoBao1(HttpServletRequest request, String userAccount, String passWord) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\ie\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        ChromeDriver driver = new ChromeDriver(options);

        driver.get(loadUrl);
        Thread.sleep(2000);
        driver.findElementByLinkText("密码登录").click();
        driver.findElementByName("TPL_username").sendKeys(userAccount);
        Thread.sleep(1000);
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.TAB).build().perform();
        Thread.sleep(1000);
        actions.sendKeys(passWord).build().perform();

        String attribute = driver.findElementById("nocaptcha").getAttribute("style");
        if (attribute != null && attribute.contains("display: block;")) {
            System.out.println("此处有滑动验证码");
            Thread.sleep(2000);
                drapSlide(driver, actions);
        }

//        driver.findElementById("J_SubmitStatic").click();

        return null;
    }

    public void drapSlide(ChromeDriver driver, Actions actions) throws InterruptedException {
        WebElement nc_1_n1z = driver.findElementById("nc_1_n1z");
        actions.clickAndHold(nc_1_n1z);
        int y=0;
        int count=0;
        while (true) {
            y= (int)(Math.random()*24);
            actions.moveByOffset(y, (int) Math.random()*5);
            count=count+y;

            Thread.sleep(100);
            if (count > 234) {
                y=258-count;
                actions.moveByOffset(y, (int) Math.random()*5);
                break;
            }
        }
        actions.build().perform();

        Thread.sleep(2000);
        if (!driver.getPageSource().contains("验证通过")) {
            driver.executeScript("noCaptcha.reset(1)");
            Thread.sleep(1000);
            drapSlide(driver, actions);
        }
    }


    public void sikuli(HttpServletRequest request,String userName,String password)  {

    }

    public static void main(String[] args) throws InterruptedException {
        new TaobaoService().loadTaoBao1(null, "路党伟", "wydm7510162");
    }
}
