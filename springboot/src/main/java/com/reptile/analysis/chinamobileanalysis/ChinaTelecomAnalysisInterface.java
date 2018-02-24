package com.reptile.analysis.chinamobileanalysis;

import java.util.List;
import java.util.Map;

public interface ChinaTelecomAnalysisInterface {
    /**
     * 解析xml形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public List<Map<String,String>> analysisXml(List<String> data,String phoneNumber,String...agrs);

    /**
     * 解析json形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public List<Map<String,String>> analysisJson(List<String> data,String phoneNumber,String...agrs);

    /**
     * 解析Html形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public List<Map<String,String>> analysisHtml(List<String> data,String phoneNumber,String...agrs);
}
