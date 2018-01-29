package com.reptile.analysis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 陕西电信数据解析
 *
 * @author mrlu 1 25
 */
public class ShanXiTelecomAnalysisImp implements ChinaTelecomAnalysisInterface {
    private Logger logger = LoggerFactory.getLogger(ShanXiTelecomAnalysisImp.class);

    @Override
    public  List<Map<String,String>> analysisXml(List<String> data,String phoneNumber, String... agrs) {
        return null;
    }

    @Override
    public  List<Map<String,String>> analysisJson(List<String> data,String phoneNumber, String... agrs) {
        return null;
    }

    @Override
    public  List<Map<String,String>> analysisHtml(List<String> data,String phoneNumber, String... agrs) {

//        for (int i = 0; i < data.size(); i++) {
//            Document parse = Jsoup.parse(new File("f://shanxi.txt"), "gbk");
//            Elements tableList = parse.getElementsByTag("table");
//            if (tableList.isEmpty()) {
//                logger.warn("------电信-------:"+phoneNumber+"该用户第"+i+"次详单页面未捕捉到表单数据，data:"+parse.text());
//                break;
//            }
//            Element table = tableList.get(0);
//            Elements trList = table.getElementsByTag("tr");
//            if (trList.isEmpty()) {
//                logger.warn("------电信-------:"+phoneNumber+"该用户第"+i+"次详单页面未捕捉到详单数据，data:"+parse.text());
//                break;
//            }
//
//        }


        return null;
    }

    public boolean isEmpty(Elements obj) {
        if (obj == null || obj.size() == 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {

        ChinaTelecomAnalysisInterface sx = new ShanXiTelecomAnalysisImp();
        sx.analysisHtml(null, null);

    }
}
