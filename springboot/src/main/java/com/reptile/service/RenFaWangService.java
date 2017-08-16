package com.reptile.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.reptile.util.ConstantInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.reptile.util.CrawlerUtil;
import com.reptile.util.Resttemplate;

@Service
public class RenFaWangService {
	/**
	 * 
	 * 获取验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getImageCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> mapData = new HashMap<String, Object>();
		Map<String, Object> infoMap = new HashMap<String, Object>();

		WebClient webClient = new CrawlerUtil().WebClientNice();

		String verifyImages = request.getSession().getServletContext()
				.getRealPath("/refawangCodeImage");

		File file = new File(verifyImages);
		if (!file.exists()) {
			file.mkdirs();
		}
		BufferedImage read = null;
		HtmlPage page = null;
		try {
			page = webClient
					.getPage("http://zhixing.court.gov.cn/search/index_form.do");
			Thread.sleep(1000);

			// 保存验证码到本地
			HtmlImage captchaImg = (HtmlImage) page
					.getElementById("captchaImg");
			ImageReader imageReader = captchaImg.getImageReader();
			read = imageReader.read(0);
		} catch (Exception e) {
			mapData.put("errorInfor", "系统正在维护！");
			mapData.put("errorCode", "0001");
			return mapData;
		}

		String fileName = System.currentTimeMillis() + "renfawang.png";
		ImageIO.write(read, "png", new File(verifyImages + File.separator
				+ fileName));

//		infoMap.put("fileName", fileName);
//		infoMap.put("path", "/refawangCodeImage");
//		infoMap.put("port", CrawlerUtil.port);
//		infoMap.put("ip", CrawlerUtil.ip);
//		mapData.put("data", infoMap);
		mapData.put("path",CrawlerUtil.ip+":"+CrawlerUtil.port+"/refawangCodeImage/"+fileName);
		request.getSession().setAttribute("rfw-webclient", webClient);
		request.getSession().setAttribute("rfw-page", page);
		return mapData;

	}

	/**
	 * 查询对应条件的所有信息
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param userName
	 * @param idCard
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDeltailData(HttpServletRequest request,
			HttpServletResponse response, String userName, String idCard,
			String code) throws Exception {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> infoMap = new HashMap<String, Object>();

		HttpSession session = request.getSession();
		WebClient webClient = (WebClient) session.getAttribute("rfw-webclient");
		HtmlPage page = (HtmlPage) session.getAttribute("rfw-page");

		if (webClient == null || page == null) {
			dataMap.put("errorinfo", "验证码错误！");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}

		if (code == null ||code.trim().length()==0) {
			dataMap.put("errorinfo", "请输入验证码！");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}
		if (userName == null ||userName.trim().length()==0) {
			dataMap.put("errorinfo", "请输入查询条件!");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}
		// 查询用户名称
		HtmlInput pname = (HtmlInput) page.getElementById("pname");
		if (pname == null) {
			dataMap.put("errorinfo", "查询出现异常，请稍后再试！");
			dataMap.put("errorCode", "0001");
			return dataMap;
		}

		pname.setValueAttribute(userName);

		// 身份证号码
		HtmlInput cardNum = (HtmlInput) page.getElementById("cardNum");
		cardNum.setValueAttribute(idCard);

		// 设置验证码
		HtmlInput j_captcha = (HtmlInput) page.getElementById("j_captcha");
		j_captcha.setValueAttribute(code);

		// 提交
		HtmlButton button = (HtmlButton) page.getElementById("button");
		HtmlPage click = (HtmlPage) button.click();
		
		// 获取页面中所有的信息的id存储在list中
		List<String> idList = new ArrayList();
		
		try {
			// 获取数据列表中的验证码id
			String captchaId = click.getElementById("captchaId").getAttribute(
					"value");
			DomNodeList<DomElement> aList = click.getElementsByTagName("a");
			
			for (DomElement dom : aList) {
				if ("View".equals(dom.getAttribute("class"))) {
					idList.add(dom.getAttribute("id"));
				}
			}
			// 获取所有的查询信息的总页数
			DomElement pagenum = click.getElementById("pagenum");
			String nodeValue = pagenum.getParentNode().asText();
			String[] arrStr = nodeValue.split("/");
			String[] arrs = arrStr[1].split(" ");
			int countPage = Integer.parseInt(arrs[0]);
			HtmlPage pageTemp = click;

			// 循环遍历取得所有的id
			for (int i = 2; i <= countPage; i++) {
				HtmlForm searchForm = (HtmlForm) pageTemp
						.getElementById("searchForm");
				searchForm.getInputByName("currentPage").setValueAttribute(
						String.valueOf(i));
				searchForm.getInputByName("selectCourtId").setValueAttribute(
						"1");
				searchForm.getInputByName("selectCourtArrange")
						.setValueAttribute("1");
				searchForm.getInputByName("pname").setValueAttribute(userName);
				searchForm.getInputByName("cardNum").setValueAttribute(idCard);
				searchForm.getInputByName("j_captcha").setValueAttribute(code);
				searchForm.getInputByName("captchaId").setValueAttribute(
						captchaId);
				HtmlPage newPage = (HtmlPage) pageTemp.executeJavaScript(
						"$('#searchForm').submit();").getNewPage();

				DomNodeList<DomElement> listArr = newPage
						.getElementsByTagName("a");
				for (DomElement dom : listArr) {
					if ("View".equals(dom.getAttribute("class"))) {
						idList.add(dom.getAttribute("id"));
					}
				}
				pageTemp = newPage;
			}

			// 获取对应id的详情
			List<String> listData = new ArrayList<String>();
			for (int i = 0; i < idList.size(); i++) {
				TextPage page1 = webClient
						.getPage("http://zhixing.court.gov.cn/search/newdetail?id="
								+ idList.get(i)
								+ "&j_captcha="
								+ code
								+ "&captchaId=" + captchaId);
				Thread.sleep(500);
				String detailMes = page1.getContent();
				listData.add(detailMes);
			}
			infoMap.put("data", listData);
			infoMap.put("cardNumber", idCard);
			Resttemplate resttemplate=new Resttemplate();
			dataMap = resttemplate.SendMessage(infoMap,
					ConstantInterface.port+"/HSDC/grade/humanLawTwo");
			session.removeAttribute("rfw-webclient");
			session.removeAttribute("rfw-page");
		} catch (Exception e) {
			dataMap.put("errorinfo", "系统繁忙，请稍后再试");
			dataMap.put("errorCode", "0001");
		}
		return dataMap;
	}

	/**
	 * 
	 * 重新获取验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	public Map<String, Object> getNewImageCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		Map<String, Object> mapData = new HashMap<String, Object>();

		HttpSession session = request.getSession();
		WebClient webClient = new CrawlerUtil().WebClientNice();

		HtmlPage page = (HtmlPage) session.getAttribute("rfw-page");
		if (page == null) {
			Map<String, Object> imageCode = getImageCode(request, response);
			return  imageCode;
		}
		try {

			// 获取页面的验证码Id
			DomElement elementValid = page.getElementById("captchaId");
			String idCode = elementValid.getAttribute("value");

			// 获取新的验证码
			String codeUrl = "http://zhixing.court.gov.cn/search/captcha.do?captchaId="
					+ idCode + "&random=" + System.currentTimeMillis();
			UnexpectedPage page2 = webClient.getPage(codeUrl);

			// 保存验证码
			String verifyImages = request.getSession().getServletContext()
					.getRealPath("/refawangCodeImage");

			File file = new File(verifyImages);
			if (!file.exists()) {
				file.mkdirs();
			}

			String fileName = System.currentTimeMillis() + "renfawang.png";
			BufferedImage bi = ImageIO.read(page2.getInputStream());
			ImageIO.write(bi, "png", new File(verifyImages + File.separator
					+ fileName));

//			infoMap.put("fileName", fileName);
//			infoMap.put("path", "/refawangCodeImage");
//			infoMap.put("port", CrawlerUtil.port);
//			infoMap.put("ip", CrawlerUtil.ip);
//			mapData.put("data", infoMap);
			mapData.put("path",CrawlerUtil.ip+":"+CrawlerUtil.port+"/refawangCodeImage/"+fileName);
		} catch (Exception e) {
			mapData.put("errorinfo", "系统异常！");
			mapData.put("errorCode", "0001");
		}
		return mapData;
	}
}
