package com.surfilter.utility.baiduofflinemap;

import com.surfilter.utility.baiduofflinemap.model.OfflineMap;
import com.surfilter.utility.baiduofflinemap.model.Vector2D;
import com.surfilter.utility.baiduofflinemap.service.BmapOfflineManager;
import com.surfilter.utility.baiduofflinemap.util.MocatorUtil;

/**
 * @author taoshuangxi
 * @date 2018-08-06
 * @description:下载百度地图离线瓦片主程序
 *
 */
public class App {
    /**
     *离线瓦片层级
     */
    private static final Integer[] zooms = {6,7,8,9,10,11,12,13,14,15,16,17,18,19};

    /**
     * 下列经纬度是驻马店市经纬度
     */
    private static Vector2D leftBottom = new Vector2D(113.49437,32.268715);
    private static Vector2D leftTop = new Vector2D(113.501269,33.583669);
    private static Vector2D rightBottom = new Vector2D(114.917861,32.10639);
    private static Vector2D rightTop =new Vector2D(114.784481,33.52783);

    public static void main( String[] args ){
        leftBottom = MocatorUtil.lonLat2Mercator(leftBottom);
        leftTop = MocatorUtil.lonLat2Mercator(leftTop);
        rightBottom = MocatorUtil.lonLat2Mercator(rightBottom);
        rightTop = MocatorUtil.lonLat2Mercator(rightTop);

        OfflineMap offlineMap = new OfflineMap();
        offlineMap.setMaxX(getMax(leftBottom.getX(), leftTop.getX(), rightBottom.getX(), rightTop.getX()));
        offlineMap.setMinX(getMin(leftBottom.getX(), leftTop.getX(), rightBottom.getX(), rightTop.getX()));
        offlineMap.setMaxY(getMax(leftBottom.getY(),leftTop.getY(), rightBottom.getY(), rightTop.getY()));
        offlineMap.setMinY(getMin(leftBottom.getY(),leftTop.getY(), rightBottom.getY(), rightTop.getY()));
        offlineMap.setZoom(zooms);

        BmapOfflineManager offlineManager = new BmapOfflineManager();

        String userDir = System.getProperty("user.dir");
        String outputDir = userDir +"/bmap";
        offlineManager.offline(offlineMap,outputDir);

    }

    private static Double getMax(Double one, Double two, Double three, Double forth){
        Double max = one > two ? one : two;
        max = max > three ? max : three;
        max = max > forth ? max : forth;
        return max;
    }

    private static Double getMin(Double one, Double two, Double three, Double forth){
        Double min = one < two ? one : two;
        min = min < three ? min : three;
        min = min < forth ? min : forth;
        return min;
    }

}
