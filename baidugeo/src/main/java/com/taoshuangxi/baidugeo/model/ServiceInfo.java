package com.taoshuangxi.baidugeo.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 * @description: 场所实体类
 */
public class ServiceInfo {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "场所编码")
    private String serviceCode;
    @ApiModelProperty(value = "场所名称")
    private String serviceName;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "场所类型")
    private String serviceType;
    @ApiModelProperty(value = "场所地图纬度")
    private String xpoint;// 纬度
    @ApiModelProperty(value = "场所地图纬度")
    private String ypoint;// 经度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getXpoint() {
        return xpoint;
    }

    public void setXpoint(String xpoint) {
        this.xpoint = xpoint;
    }

    public String getYpoint() {
        return ypoint;
    }

    public void setYpoint(String ypoint) {
        this.ypoint = ypoint;
    }

}
