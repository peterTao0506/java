package com.surfilter.utility.baiduofflinemap.model;

/**
 * @author taoshuangxi
 * @date 2018-08-06
 * @description 坐标类
 */
public class Vector2D {
    private Double X;
    private Double Y;

    public Vector2D(){}

    public Vector2D(Double x, Double y){
        this.X = x;
        this.Y = y;
    }

    public Double getX() {
        return X;
    }

    public void setX(Double x) {
        X = x;
    }

    public Double getY() {
        return Y;
    }

    public void setY(Double y) {
        Y = y;
    }
}
