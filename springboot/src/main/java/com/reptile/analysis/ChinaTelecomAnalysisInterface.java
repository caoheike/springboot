package com.reptile.analysis;

import java.util.List;
import java.util.Map;

public interface ChinaTelecomAnalysisInterface {
    /**
     * 解析xml形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public Map<String, Object> analysisXml(List<String> data,String phoneNumber,String...agrs);

    /**
     * 解析json形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public Map<String, Object> analysisJson(List<String> data,String phoneNumber,String...agrs);

    /**
     * 解析Html形式的数据
     * @param data
     * @param agrs
     * @return
     */
    public Map<String, Object> analysisHtml(List<String> data,String phoneNumber,String...agrs);
}
