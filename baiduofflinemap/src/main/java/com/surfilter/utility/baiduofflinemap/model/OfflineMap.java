package com.surfilter.utility.baiduofflinemap.model;

/**
 * @author taoshuangxi
 * @date 2018-08-06
 * @description: 百度离线地图封装类
 */
public class OfflineMap {
	
	private Double minX; // 最小平面坐标X
	private Double minY; // 最小平面坐标Y
	private Double maxX; // 最大平面坐标X
	private Double maxY; // 最大平面坐标Y
	private Integer[] zoom; // 缩放级别

	public OfflineMap(){
	}
	
	public Double getMinX() {
		return minX;
	}
	public void setMinX(Double minX) {
		this.minX = minX;
	}
	public Double getMinY() {
		return minY;
	}
	public void setMinY(Double minY) {
		this.minY = minY;
	}
	public Double getMaxX() {
		return maxX;
	}
	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}
	public Double getMaxY() {
		return maxY;
	}
	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}
	public Integer[] getZoom() {
		return zoom;
	}
	public void setZoom(Integer... zoom) {
		this.zoom = zoom;
	}

}
