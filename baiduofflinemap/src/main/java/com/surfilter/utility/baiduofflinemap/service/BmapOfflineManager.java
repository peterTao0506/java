package com.surfilter.utility.baiduofflinemap.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.surfilter.utility.baiduofflinemap.model.OfflineMap;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


/**
 * 百度离线地图下载类
 * @author ycl
 * @dete 2015-05-12
 *
 */
public class BmapOfflineManager {
	private static final Logger logger = Logger.getLogger(BmapOfflineManager.class);
	
	public BmapOfflineManager(){}

	private final String[] domain = {"online0.map.bdimg.com", "online1.map.bdimg.com", "online2.map.bdimg.com", "online3.map.bdimg.com", "online4.map.bdimg.com"};
	private final String tile = "http://{0}/tile/?qt=tile&x={1}&y={2}&z={3}&styles=pl&scaler=1&udt=20141103";
	private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * domain.length);
	
	private String tileDir;
	private boolean working = false;
	
	private int getTile(double axis, int zoom){
		return (int) Math.floor(axis * Math.pow(2, zoom - 18) / 256);
	}
	
	// 负坐标需要将负号（-）转成大写字母 M
	private String getTileUrl(int x, int y, int z){
		String dmn = domain[(x + y) % domain.length];
		return MessageFormat.format(tile, dmn, (x+""), (y+""), (z+""));
	}
	
	private String getTilePath(int x, int y, int z){
		return tileDir+"/tile/"+z+"/"+y+"/"+x+".png";
	}
	
	/**
	 * 坐标参数是经纬度转换成的平面坐标
	 * 只支持中国地图（东北半球）
	 * @param map 
	 * @param dir 离线地图文件存储路径
	 */
	public synchronized boolean offline(final OfflineMap map, String dir){
		if(map.getMaxY().doubleValue()<0 || map.getMaxX().doubleValue()<0){
			logger.warn("坐标参数不在要求范围内！");
			return false;
		}
		if(map.getMinX().doubleValue()<0)map.setMinX(0d);
		if(map.getMinY().doubleValue()<0)map.setMinY(0d);
		this.tileDir = dir;
		new Thread(new Runnable() {
			@Override
			public void run() {
				download(map);
			}
		}).start();
		logger.info("启动程序开始下载百度离线地图");
		return true;
	}
	
	private void download(OfflineMap map){
		logger.info("==========开始下载离线地图==========");
		long t = System.currentTimeMillis();
		List<Future<?>> tasks = new ArrayList();
		for(int z : map.getZoom()){
			int minX = getTile(map.getMinX(), z);
			int minY = getTile(map.getMinY(), z);
			int maxX = getTile(map.getMaxX(), z);
			int maxY = getTile(map.getMaxY(), z);
			for(int y=minY;y<=maxY;y++){
				for(int x=minX;x<=maxX;x++){
					tasks.add(pool.submit(new DownloadThread(x, y, z)));
				}
			}
		}
		for(Future<?> f : tasks){
			try {f.get();} catch (Exception e) {}
		}
		try{
			pool.shutdown();
		}catch (Exception ex){

		}
		logger.info("离线地图下载耗时 "+(System.currentTimeMillis()-t)+" 毫秒");
		logger.info("==========离线地图下载结束==========");
	}
	
	public class DownloadThread implements Runnable {
		private int x;
		private int y;
		private int z;
		public DownloadThread(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		@Override
		public void run() {
			String imageFileName = getTilePath(x, y, z);
			File img = new File(imageFileName);
			if(img.exists()){
				logger.info("当前地图瓦片已经存在, path="+img.getPath());
				return;
			}
			img.getParentFile().mkdirs();
			String url = getTileUrl(x, y, z);
			logger.info("url: "+url);
			HttpURLConnection conn = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				input = conn.getInputStream();
				output = new FileOutputStream(img);
				IOUtils.copyLarge(input, output);
			} catch (Exception e) {
				logger.error("下载地图瓦片"+imageFileName+"失败", e);
			} finally {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		}
	}

}
