package com.sdiablofix.dt.sdiablofix.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sdiablofix.dt.sdiablofix.R;


/**
 * Created by buxianhui on 2020/4/14.
 */

public class DiabloEditTextDialog extends AlertDialog {

    private Context mContext;
    private boolean mNegativeButton;
    private EditText mViewEditText;
    private String mEditTextValue;

    private OnOkClickListener mOnOkClickListener;

    public DiabloEditTextDialog(Context context, boolean negativeButton, String editTextvalue, OnOkClickListener listener) {
        super(context);
        this.mContext = context;
        this.mNegativeButton = negativeButton;
        this.mEditTextValue = editTextvalue;
        this.mOnOkClickListener = listener;
    }

    public void create() {
        DiabloAlertDialog.Builder builder = new DiabloAlertDialog.Builder(mContext);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if ( null != mOnOkClickListener ) {
                    mOnOkClickListener.onOk(mViewEditText);
                }
            }
        });

        if (mNegativeButton) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
        }

        // AlertDialog dialog = builder.create();
        View dialogLayout = LayoutInflater.from(mContext).inflate(R.layout.modify_fix_count, null);
        mViewEditText = (EditText) dialogLayout.findViewById(R.id.modify_fix_count);
        mViewEditText.setText(mEditTextValue);
        builder.setView(dialogLayout);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface OnOkClickListener {
        void onOk(EditText editText);
    }
}
