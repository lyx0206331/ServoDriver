package com.adrian.servodriver.pojo;

/**
 * Created by qing on 2017/6/22.
 */

public class ParamBean {
    private int segId;  //段号
    private int indexId;    //编号
    private String paramName;   //参数名称
    private String maxValue; //最大值
    private String minValue; //最小值
    private String defValue; //默认值
    private String curValue; //当前值
    private String unit;    //单位

    public int getSegId() {
        return segId;
    }

    public void setSegId(int segId) {
        this.segId = segId;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public String getCurValue() {
        return curValue;
    }

    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
