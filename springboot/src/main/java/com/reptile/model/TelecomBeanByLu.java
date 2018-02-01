package com.reptile.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//@ApiModel(value="电信bean")
public class TelecomBeanByLu {
//    @ApiModelProperty(value="手机号" ,required=true)
    private String userName;
//    @ApiModelProperty(value="服务密码" ,required=true)
    private String servePwd;
//    @ApiModelProperty(value="省份id" ,required=true)
    private String provinceId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServePwd() {
        return servePwd;
    }

    public void setServePwd(String servePwd) {
        this.servePwd = servePwd;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    @Override
    public String toString() {
        return "TelecomBeanByLu{" +
                "userName='" + userName + '\'' +
                ", servePwd='" + servePwd + '\'' +
                ", provinceId='" + provinceId + '\'' +
                '}';
    }
}
