package com.reptile.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 
 * @author bigyoung
 * @deprecated 页面解析工具
 *
 */
public class htmlUtil {

	public String Resolve(HtmlPage page) {

		HtmlTable table = (HtmlTable) page.getElementById("table");

		return table.asXml();

	}

	public List getsix() {
		List list = new ArrayList();

		for (int i = -2; i < 1; i++) {

			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM");
			Calendar calendar = Calendar.getInstance();
			// 将calendar装换为Date类型
			Date date = calendar.getTime();
			// 将date类型转换为BigDecimal类型（该类型对应oracle中的number类型）
			BigDecimal time01 = new BigDecimal(matter.format(date));
			// 获取当前时间的前6个月
			calendar.add(Calendar.MONTH, i);
			Date date02 = calendar.getTime();
			BigDecimal time02 = new BigDecimal(matter.format(date02));

			list.add(time02);

		}
		return list;

	}


	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());

		return lastDayOfMonth;
	}

	public static List<Map> liantong() {
		List list = new ArrayList();

		for (int i = -2; i < 1; i++) {

			SimpleDateFormat matter = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat mattery = new SimpleDateFormat("yyyy");
			SimpleDateFormat matterm = new SimpleDateFormat("MM");
			Calendar calendar = Calendar.getInstance();
			// 将calendar装换为Date类型
			Date date = calendar.getTime();
			// 将date类型转换为BigDecimal类型（该类型对应oracle中的number类型）
			BigDecimal time01 = new BigDecimal(matter.format(date));
			// 获取当前时间的前6个月
			calendar.add(Calendar.MONTH, i);
			Date date02 = calendar.getTime();
			BigDecimal time02 = new BigDecimal(matter.format(date02));
			String s1 = mattery.format(date02);
			String s2 = matterm.format(date02);
			String end = getLastDayOfMonth(Integer.valueOf(s1), Integer.valueOf(s2));
			Map map = new HashMap();
			map.put("begin", s1 + "-" + s2 + "-01");
			map.put("end", end);
			list.add(map);

		}
		return list;

	}

	/**
	 * 中国移动验证码抓取
	 * 
	 * @param
	 * @throws IOException
	 */
	public void MobileCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();
		HtmlPage page = (HtmlPage) session.getAttribute("CodePage");
		WebClient webClient = (WebClient) session.getAttribute("WebClient");
		HtmlImage valiCodeImg = (HtmlImage) page.getElementById("verifyImg");
		ImageReader imageReader = valiCodeImg.getImageReader();
		BufferedImage bufferedImage = imageReader.read(0);
		BufferedImage inputbig = new BufferedImage(80, 40, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = (Graphics2D) inputbig.getGraphics();
		g.drawImage(bufferedImage, 0, 0, 80, 40, null); // 画图
		g.dispose();
		inputbig.flush();

		request.getSession().setAttribute("webClient", webClient);
		request.getSession().setAttribute("page", page);

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		ImageIO.write(inputbig, "png", response.getOutputStream());
	}

	public static void main(String[] args) {
		List<?> list = new htmlUtil().liantong();
		System.out.println(list.toString());
	}

}
