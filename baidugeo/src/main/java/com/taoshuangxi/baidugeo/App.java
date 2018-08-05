package com.taoshuangxi.baidugeo;

import com.taoshuangxi.baidugeo.service.ServiceInfoGeoService;
import org.apache.log4j.Logger;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 * @descrption: 主程序启动类
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args){
        if(args != null && args.length == 2){
            ServiceInfoGeoService serviceInfoGeoService = new ServiceInfoGeoService(args[0],args[1]);
            serviceInfoGeoService.startService();
            logger.info("百度坐标抓取程序启动成功");
        }else{
            logger.error("参数错误: 当前支持两个参数 excel D://service.xls");
        }
    }

}
