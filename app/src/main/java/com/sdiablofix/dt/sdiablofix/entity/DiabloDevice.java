package com.sdiablofix.dt.sdiablofix.entity;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 2021/11/15.
 */

public class DiabloDevice {

    private static DiabloDevice mDiabloDevice;

    private String uuid;
    private Integer device;

    private DiabloDevice() {
        uuid = DiabloEnum.EMPTY_STRING;
        device = DiabloEnum.INVALID_INDEX;
    }

    public static DiabloDevice instance() {
        if (null == mDiabloDevice) {
            mDiabloDevice = new DiabloDevice();
        }

        return mDiabloDevice;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDevice() {
        return device;
    }

    public void setDevice(Integer device) {
        this.device = device;
    }
}
