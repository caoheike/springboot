package com.reptile.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poi {

	
	
	  public static List<Map<String, Object>> getvalues(File file){
		   List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();

		   InputStream is;
	       HSSFSheet sheetMain;

	       try {
	           is = new FileInputStream(file);
	           POIFSFileSystem fs = new POIFSFileSystem(is);
	           HSSFWorkbook wb = new HSSFWorkbook(fs);
	           // 读取第一个Sheet
	           sheetMain = wb.getSheetAt(0);
	           is.close();

	           // 总共的行数
	           int rowLens = sheetMain.getLastRowNum();
	           int colLens = 8;
	           int errCnt = 0;
	           HSSFRow row = null;
	           HSSFCell cell = null;
	           String content = "";
	        
	           
	           
	           for (int rowCount = 3; rowCount <= rowLens; rowCount++) {
	               System.out.println("读取行：" + rowCount);
	               row = sheetMain.getRow(rowCount);
	    	       Map<String,Object> map=new HashMap<String,Object>();
	               if (row != null) {
	                   for (int colCount = 0; colCount < colLens; colCount++) {
	                       System.out.print("行 ：" + rowCount + "；列 ：" + colCount
	                               + "的内容：");
	                       cell = row.getCell((short) colCount);
	                       content = getCellValue(cell).trim();
	                       if (content == "") {
	                           System.out.println("### 发现空异常 ###");
	                       } else {
	                    	    switch (colCount) {
								case 0:
									map.put("CallTime", content);//呼叫时间
									break;
								case 1:
									map.put("CallAddress", content);//呼叫地点
									break;
								case 2:
									map.put("CallWay", content);//通话方式
									break;
								case 3:
									map.put("CallNumber", content);//对方号码
									break;
								case 4:
									map.put("CallDuration", content);//通信时长
									break;
								case 5:
									map.put("CallType", content);//通信类型
									break;
								case 6:
									map.put("CallMoney", content);//实收费用
									break;
								}
	                       
	                       }
	                   }
	            
	     
	               }
	               lists.add(map);
	           }

	       } catch (FileNotFoundException e) {
	           e.printStackTrace();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
		return lists;
	   }

	    public static String getCellValue(HSSFCell cell) {
	        if (cell != null) {
	            switch (cell.getCellType()) {
	            case HSSFCell.CELL_TYPE_BLANK:
	                return "";
	            case HSSFCell.CELL_TYPE_NUMERIC:
	                String strValue = String.valueOf(cell.getNumericCellValue());
	                if (strValue != null && strValue.indexOf(".") != -1
	                        && strValue.indexOf("E") != -1) {
	                    try {
	                        return new DecimalFormat().parse(strValue).toString();
	                    } catch (ParseException e) {
	                        e.printStackTrace();
	                    }
	                } else {
	                    if (strValue.endsWith(".0")) {
	                        return strValue.substring(0, strValue.indexOf(".0"));
	                    } else {
	                        return strValue;
	                    }
	                }
	            case HSSFCell.CELL_TYPE_STRING:
	                return (cell.getStringCellValue() + "").trim();
	            case HSSFCell.CELL_TYPE_FORMULA:
	                return (cell.getCellFormula() + "").trim();
	            case HSSFCell.CELL_TYPE_BOOLEAN:
	                return cell.getBooleanCellValue() + "";
	            case HSSFCell.CELL_TYPE_ERROR:
	                return cell.getErrorCellValue() + "";
	            }
	        }
	        return "";
	    }
}
