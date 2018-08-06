package com.surfilter.utility.baiduofflinemap.util;

import com.surfilter.utility.baiduofflinemap.model.Vector2D;

/**
 * @author taoshuangxi
 * @date 2018-08-06
 * @description: 经纬度与墨卡托坐标互相转换
 */
public abstract class MocatorUtil {
    //经纬度转墨卡托
    public static Vector2D lonLat2Mercator(Vector2D lonLat)
    {
        Vector2D mercator = new Vector2D();
        double x = lonLat.getX() * 20037508.34 / 180;
        double y = Math.log(Math.tan((90 + lonLat.getY()) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.34 / 180;
        mercator.setX(x);
        mercator.setY(y);
        return mercator;
    }
    //墨卡托转经纬度
    public static Vector2D Mercator2lonLat(Vector2D mercator)
    {
        Vector2D lonLat = new Vector2D();
        double x = mercator.getX() / 20037508.34 * 180;
        double y = mercator.getY() / 20037508.34 * 180;
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
        lonLat.setX(x);
        lonLat.setY(y);
        return lonLat;
    }
}
