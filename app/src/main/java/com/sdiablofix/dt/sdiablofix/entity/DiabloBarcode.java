package com.sdiablofix.dt.sdiablofix.entity;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/10/4.
 */

public class DiabloBarcode {
    private String mCorrect;
    private String mCut;
    private String mAuto;

    public DiabloBarcode(String auto) {
        this.mAuto = auto;
    }

    public String getCorrect() {
        return mCorrect;
    }

    public String getCut() {
        return mCut;
    }

    public void correctBarcode(String origin) {
        if (DiabloEnum.DIABLO_CONFIG_YES.equals(mAuto)) {
            if (origin.startsWith("1")) {
                mCorrect = origin;
                mCut = origin.substring(0, origin.length() - DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE);
            }
            else if (origin.startsWith("00")) {
                mCorrect = origin.substring(1);
                mCut = origin;
            }
            else if (origin.startsWith("01") && origin.length() > 14) {
                mCorrect = origin.substring(1);
                mCut = origin.substring(1, origin.length() - DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE);
            } else {
                mCorrect = origin;
                mCut = origin;
            }
        } else {
            if (origin.startsWith("00")) {
                mCorrect = origin.substring(1);
                mCut = origin.substring(1, origin.length() - DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE);
            }
            else if (origin.startsWith("0")) {
                mCorrect = origin.substring(1);
                mCut = origin.substring(1, origin.length() - DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE);
            }
            else {
                mCorrect = origin;
                mCut = origin.substring(0, origin.length() - DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE);
            }
        }
    }
}
