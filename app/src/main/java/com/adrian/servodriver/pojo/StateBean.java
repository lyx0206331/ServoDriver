package com.adrian.servodriver.pojo;

/**
 * Created by ranqing on 2017/6/27.
 */

public class StateBean {

    private String stateName;
    private int stateValue;
    private String stateUnit;

    public StateBean() {
    }

    public StateBean(String stateName, int stateValue, String stateUnit) {
        this.stateName = stateName;
        this.stateValue = stateValue;
        this.stateUnit = stateUnit;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public int getStateValue() {
        return stateValue;
    }

    public void setStateValue(int stateValue) {
        this.stateValue = stateValue;
    }

    public String getStateUnit() {
        return stateUnit;
    }

    public void setStateUnit(String stateUnit) {
        this.stateUnit = stateUnit;
    }
}
