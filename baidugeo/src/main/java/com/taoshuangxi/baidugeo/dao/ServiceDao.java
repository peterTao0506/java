package com.taoshuangxi.baidugeo.dao;

import com.taoshuangxi.baidugeo.model.ServiceInfo;

import java.util.List;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 */
public abstract class ServiceDao {

    public abstract List<ServiceInfo> getServiceInfoList(String params);
}
