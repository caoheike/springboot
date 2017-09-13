package com.reptile.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(  prefix = "manInfo" )

public class application {
	 private String ip;
	 private String sendip;
	 private String port;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSendip() {
		return sendip;
	}
	public void setSendip(String sendip) {
		this.sendip = sendip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}


}
