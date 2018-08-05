package com.taoshuangxi.baidugeo.service;

import com.taoshuangxi.baidugeo.model.ServiceInfo;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 * @description: 多线程获取经纬度
 * 百度经纬度API参考: http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
 */
public class ServiceInfoGeoRunnable implements Runnable{
    private static final Logger logger = Logger.getLogger(ServiceInfoGeoRunnable.class);

    private LinkedBlockingDeque<ServiceInfo> serviceInfoQueue;
    private LinkedBlockingDeque<ServiceInfo> geoqueue;

    /**
     * 重要的事情说三遍： 请参考百度经纬度API开发者文档申请 http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
     * 重要的事情说三遍： 请参考百度经纬度API开发者文档申请 http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
     * 重要的事情说三遍： 请参考百度经纬度API开发者文档申请 http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
     */
    private static final String appKey = "你的百度开发者APP Key";
    private static final String appSecureKey = "你的百度发开者App Secure Key";

    private static final String addressPrefix ="河南省驻马店市";
    private static final String addressPrefix1 ="河南";
    private static final String addressPrefix2 ="驻马店";

    public ServiceInfoGeoRunnable(LinkedBlockingDeque<ServiceInfo> blockingQueue, LinkedBlockingDeque<ServiceInfo> geoQueue){
        this.serviceInfoQueue = blockingQueue;
        this.geoqueue = geoQueue;
    }

    @Override
    public void run() {
        logger.info("爬取坐标线程启动执行");
        while (!serviceInfoQueue.isEmpty()){
            try {
                ServiceInfo serviceInfo = serviceInfoQueue.poll(1, TimeUnit.SECONDS);
                logger.info("开始爬取场所"+serviceInfo.getServiceCode()+"经纬度");

                String address = serviceInfo.getAddress();
                if(address == null){
                    address = serviceInfo.getServiceName();
                }
                if(!address.startsWith(addressPrefix1) && !address.startsWith(addressPrefix2)){
                    address = addressPrefix+ address;
                }
                String snCode = getSnCode(address);

                URL myURL = null;
                String lat = null,lng = null;
                try{
                    String url = String.format("http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s&sn=%s",URLEncoder.encode( address,"UTF-8"),appKey,snCode);
                    myURL = new URL(url);
                } catch (Exception e) {
                    logger.error("调用百度api请求地址不正确", e);
                }
                try {
                    URLConnection httpsConn = (URLConnection) myURL.openConnection();
                    if (httpsConn != null) {
                        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                        BufferedReader br = new BufferedReader(insr);
                        String data = null;
                        if ((data = br.readLine()) != null) {
                            logger.info("==========" + data);
                            if (data.indexOf("\"lat\":") > 0) {
                                lat = data.substring(data.indexOf("\"lat\":") + ("\"lat\":").length(),
                                        data.indexOf("},\"precise\""));
                                lng = data.substring(data.indexOf("\"lng\":") + ("\"lng\":").length(),
                                        data.indexOf(",\"lat\""));
                            }
                        }
                        insr.close();
                    }
                } catch (Exception e) {
                    logger.error("调用百度api获取经纬度出错", e);
                }
                serviceInfo.setXpoint(lng);
                serviceInfo.setYpoint(lat);
                logger.info("---爬取场所"+serviceInfo.getServiceCode()+"--经度："+ lng +"--纬度：" + lat);
                geoqueue.put(serviceInfo);
                sleep(1000);//百度api限额，并发太高会失败
            } catch (InterruptedException e) {
                logger.error("线程出错", e);
            }
        }
    }

    private String getSnCode(String address){
        StringBuffer queryString = new StringBuffer();
        try {
            queryString.append("/geocoder/v2/?address=").append(URLEncoder.encode( address,"UTF-8"));
            queryString.append("&output=").append(URLEncoder.encode( "json","UTF-8"));
            queryString.append("&ak=").append(URLEncoder.encode( appKey,"UTF-8"));
            queryString.append(appSecureKey);
            String tempStr = URLEncoder.encode(queryString.toString(), "UTF-8");
            return MD5(tempStr);
        } catch (UnsupportedEncodingException e) {
            logger.error("获取api的sn code出错",e);
        }
        return "";
    }

    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
