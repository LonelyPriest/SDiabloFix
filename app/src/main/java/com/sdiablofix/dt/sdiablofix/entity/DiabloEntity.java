package com.sdiablofix.dt.sdiablofix.entity;

/**
 * Created by buxianhui on 17/9/30.
 */

public abstract class DiabloEntity {
    /**
     *
     * @return the name of dropdown list
     */
    public abstract String getName();

    /**
     *
     * @return the name showed on the edit text when the user click or select the dropdown list
     */
    public abstract String getViewName();
}