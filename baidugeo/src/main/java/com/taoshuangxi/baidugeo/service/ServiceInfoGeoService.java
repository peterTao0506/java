package com.taoshuangxi.baidugeo.service;

import com.taoshuangxi.baidugeo.dao.ServiceDao;
import com.taoshuangxi.baidugeo.dao.ServiceExcelDao;
import com.taoshuangxi.baidugeo.dao.ServiceMysqlDao;
import com.taoshuangxi.baidugeo.model.ServiceInfo;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 */
public class ServiceInfoGeoService {
    private static final Logger logger = Logger.getLogger(ServiceInfoGeoService.class);

    private LinkedBlockingDeque<ServiceInfo> serviceInfoQueue = new LinkedBlockingDeque<>(5000);

    private LinkedBlockingDeque<ServiceInfo> geoQueue = new LinkedBlockingDeque<>(5000);

    private ExecutorService fixedExecutor = Executors.newFixedThreadPool(10);

    public ServiceInfoGeoService(){
    }

    public ServiceInfoGeoService(String inputType, String inputParam){
        ServiceDao serviceDao;
        if("excel".equals(inputType)){
            serviceDao = new ServiceExcelDao();
        }else{
            serviceDao = new ServiceMysqlDao();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ServiceInfo> serviceInfoList = serviceDao.getServiceInfoList(inputParam);
                if( serviceInfoList!= null && serviceInfoList.size() >0){
                    for(ServiceInfo serviceInfo : serviceInfoList){
                        try {
                            serviceInfoQueue.put(serviceInfo);
                        }catch (Exception ex){
                            logger.error("将原始场所信息放入队列出错",ex);
                        }
                    }
                    logger.info("原始场所信息放入队列，等待程序抓始坐标，场所数"+serviceInfoList.size());
                }
            }
        }).start();
    }


    public void startService() {
        try{
            Thread.sleep(30000);
        }catch (Exception ex){
            logger.error("线程睡眠等待失败", ex);
        }
        for(int i = 0; i < 10; i++){
            ServiceInfoGeoRunnable geoRunnable = new ServiceInfoGeoRunnable(serviceInfoQueue, geoQueue);
            fixedExecutor.execute(geoRunnable);
        }
        new Thread(new ServiceInfoWriterRunnable(geoQueue)).start();
        try {
            fixedExecutor.awaitTermination(15, TimeUnit.MINUTES);
        }catch (Exception ex){
        }
    }

}
