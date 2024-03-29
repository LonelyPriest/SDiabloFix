package com.sdiablofix.dt.sdiablofix.utils;

import android.content.res.Resources;
import android.util.SparseArray;

import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloError {
    private static SparseError mSparseError;

    private DiabloError() {

    }

    private static SparseError instance() {
        if (null == mSparseError) {
            mSparseError = new SparseError();
        }

        return mSparseError;
    }

    private static class SparseError {
        private SparseArray<String> mErrors = new SparseArray<>();

        SparseError() {
            Resources r = DiabloProfile.instance().getResource();
            mErrors.put(99, r.getString(R.string.network_invalid));
            mErrors.put(500, r.getString(R.string.internal_error));
            mErrors.put(598, r.getString(R.string.authen_error));
            mErrors.put(1101, r.getString(R.string.invalid_name_password));
            mErrors.put(9009, r.getString(R.string.network_unreachable));
            mErrors.put(9001, r.getString(R.string.database_error));
            mErrors.put(9901, r.getString(R.string.empty_barcode_stock));
            mErrors.put(9902, r.getString(R.string.empty_good_big_type));
            mErrors.put(9903, r.getString(R.string.failed_to_fetch_stock));
            mErrors.put(9904, r.getString(R.string.more_than_stock));
            mErrors.put(9905, r.getString(R.string.reject_with_different_firm));
            mErrors.put(9906, r.getString(R.string.reject_with_empty_firm));
            mErrors.put(9907, r.getString(R.string.reject_with_empty_price));

            mErrors.put(1105, r.getString(R.string.login_user_active));
            mErrors.put(1106, r.getString(R.string.login_exceed_user));
            mErrors.put(1107, r.getString(R.string.login_no_user_fire));
            mErrors.put(1199, r.getString(R.string.login_out_of_service));

            mErrors.put(2014, r.getString(R.string.empty_barcode_stock));
            mErrors.put(2023, r.getString(R.string.both_ctype_and_stock_not_bind));
            mErrors.put(2024, r.getString(R.string.both_ctype_and_type_not_bind));

            mErrors.put(200, r.getString(R.string.failed_to_get_employee));
            mErrors.put(201, r.getString(R.string.failed_to_get_base_setting));
            mErrors.put(203, r.getString(R.string.failed_to_get_color));
            mErrors.put(204, r.getString(R.string.failed_to_get_size_group));
            mErrors.put(205, r.getString(R.string.failed_to_get_stock));
            mErrors.put(206, r.getString(R.string.failed_to_get_brand));
            mErrors.put(207, r.getString(R.string.failed_to_get_good_type));
            mErrors.put(208, r.getString(R.string.failed_to_get_firm));
            mErrors.put(210, r.getString(R.string.failed_to_get_color_kind));
            mErrors.put(211, r.getString(R.string.failed_to_get_user_info));
            mErrors.put(212, r.getString(R.string.failed_to_get_big_type));
        }
    }

    public static String getError(Integer code){
        return instance().mErrors.get(code);
    }
}
