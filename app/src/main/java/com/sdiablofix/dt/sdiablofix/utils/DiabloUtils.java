package com.sdiablofix.dt.sdiablofix.utils;

import static java.lang.String.format;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.promeg.pinyinhelper.Pinyin;
import com.sdiablofix.dt.sdiablofix.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloUtils {
    private final static DateFormat mDatetimeFormat
        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static String currentDatetime(){
        Calendar calendar = Calendar.getInstance();
        return mDatetimeFormat.format(calendar.getTime()).trim();
    }

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


    public static void makeToast(Context context, int stringId) {
        Toast.makeText(context, context.getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public static void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void makeToast(Context context, Integer value, int lengthLong){
        Toast toast = Toast.makeText(context, toString(value), lengthLong);
        toast.show();
    }

    public static void makeToast(Context context, String value, int lengthLong){
        Toast toast = Toast.makeText(context, value, lengthLong);
        toast.show();
    }

    public static void makeToast(Context context, Float value, int lengthLong) {
        Toast toast = Toast.makeText(context, toString(value), lengthLong);
        toast.show();
    }


    public static void setError(Context context, Integer titleId, Integer errorCode) {
        new DiabloAlertDialog(
            context,
            context.getResources().getString(titleId),
            DiabloError.getError(errorCode)).create();
    }

    public static void setError(Context context, Integer titleId, Integer errorCode, String extraError) {
        new DiabloAlertDialog(
            context,
            context.getResources().getString(titleId),
            DiabloError.getError(errorCode) + extraError).create();
    }


    public static TableRow.LayoutParams createTableRowParams(Float weight){
        return new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
    }

    public static TextView addCell(Context context, TableRow row, String value, TableRow.LayoutParams lp){
        TextView cell = new TextView(context);
        cell.setLayoutParams(lp);
        cell.setText(value.trim());
        cell.setTextSize(16);
        row.addView(cell);
        return  cell;
    }

    public static TextView addCell(Context context, TableRow row, Integer value, TableRow.LayoutParams lp){
        TextView cell = new TextView(context);
        cell.setLayoutParams(lp);
        cell.setText(DiabloUtils.toString(value).trim());
        cell.setTextSize(16);
        row.addView(cell);
        return  cell;
    }

    public static TextView addCell(Context context, TableRow row, float value, TableRow.LayoutParams lp){
        TextView cell = new TextView(context);
        cell.setLayoutParams(lp);
        cell.setText(DiabloUtils.toString(value).trim());
        cell.setTextSize(16);
        row.addView(cell);
        return  cell;
    }

    public static void formatPageInfo(final TextView cell) {
        cell.setGravity(Gravity.END);
        cell.setTextColor(Color.BLACK);
        cell.setTypeface(null, Typeface.BOLD);
        cell.setTextSize(14);
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

    public static void hiddenKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        // ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static Integer calcPage(Integer totalItems, Integer itemsPerPage) {
        if (totalItems < itemsPerPage) {
            return 1;
        }
        else if (0 == totalItems % itemsPerPage) {
            return totalItems / itemsPerPage;
        }
        else {
            return totalItems / itemsPerPage + 1;
        }
    }


    public static void playSound(Context context, int rawId) {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }

        soundPool.load(context, rawId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 1, 1, 0, 0, 1);
            }
        });
    }

    public static void error_alarm(Context context, Integer code, int rawId) {
        DiabloUtils.playSound(context, rawId);
        DiabloUtils.makeToast(context, DiabloError.getError(code), Toast.LENGTH_LONG);
    }
}
