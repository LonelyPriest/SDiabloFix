package com.sdiablofix.dt.sdiablofix.utils;

import static java.lang.String.format;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.promeg.pinyinhelper.Pinyin;
import com.sdiablofix.dt.sdiablofix.R;

import java.util.Locale;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloUtils {
    public static String toString(Float value){
        if (value == Math.round(value)){
            return format(Locale.CHINA, "%d", Math.round(value));
        }
        return format(Locale.CHINA, "%.2f", value).trim();
    }

    public static String toString(Integer value){
        return format(Locale.CHINA, "%d", value).trim();
    }

    public static Integer toInteger(String value){
        if (!value.isEmpty())
            return Integer.parseInt(value.trim());
        else
            return 0;
    }

    public static Float toFloat(String value){
        if (value.trim().isEmpty()) {
            return 0f;
        }

        try {
            return Float.parseFloat(value.trim());
        } catch (Exception e){
            return 0f;
        }
    }


    public static String toPinYinWithFirstCharacter(String chinese) {
        char [] name = chinese.toCharArray();
        String py = DiabloEnum.EMPTY_STRING;
        for (char c: name) {
            py += Pinyin.toPinyin(c).charAt(0);
        }

        return py;
    }

    public static Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diablo_loading, null);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.diablo_loading);
        ImageView image = (ImageView) view.findViewById(R.id.diablo_loading_image);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading);
        animation.setRepeatCount(Animation.INFINITE);
        image.startAnimation(animation);

        Dialog loadingDialog = new Dialog(context, R.style.LoadingTheme);
        loadingDialog.setCancelable(false);

        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));

        return loadingDialog;
    }
}
