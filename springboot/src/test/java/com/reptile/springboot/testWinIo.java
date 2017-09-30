package com.reptile.springboot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.sun.jna.NativeLibrary;

import net.minidev.json.JSONArray;

public class testWinIo {
	public static void main(String[] args) {
		
		JSONObject jsonObj = new JSONObject(); 
		try {
			jsonObj.put("users", "1111");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(jsonObj.toString()); 
		
	}
}
