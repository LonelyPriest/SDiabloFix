package com.sdiablofix.dt.sdiablofix.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iscandemo.ScannerInerface;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.activity.MainActivity;
import com.sdiablofix.dt.sdiablofix.client.StockClient;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcode;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloShop;
import com.sdiablofix.dt.sdiablofix.request.GetStockByBarcodeRequest;
import com.sdiablofix.dt.sdiablofix.response.GetStockByBarcodeResponse;
import com.sdiablofix.dt.sdiablofix.rest.StockInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloError;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFix extends Fragment {
    private final String LOG_TAG = "StockFix:";
    private DiabloShop mCurrentShop;
    private String [] mTitles;

    /*scanner*/
    private IntentFilter mScannerResultIntentFilter;
    private BroadcastReceiver mScanReceiver;

    EditText mBarCodeScanView;

    private DiabloBarcode mBarcode;

    public StockFix() {
        // Required empty public constructor
    }

    public static StockFix newInstance() {
        return new StockFix();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();

        mCurrentShop = DiabloProfile.instance().getSortShop().get(0);
        mTitles = getResources().getStringArray(R.array.thead_fix);

        String autoBarcode = DiabloProfile.instance().getConfig(
            DiabloEnum.SETTING_AUTO_BARCODE,
            DiabloEnum.DIABLO_CONFIG_YES);


        mBarcode = new DiabloBarcode(autoBarcode);

        ScannerInerface scanner = new ScannerInerface(getContext());
        scanner.open();
        scanner.setOutputMode(1);

        mScannerResultIntentFilter = new IntentFilter("android.intent.action.SCANRESULT");

        mScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("value");
                mBarCodeScanView.setText(scanResult);
                mBarCodeScanView.invalidate();
                // add row
                mBarcode.correctBarcode(mBarCodeScanView.getText().toString());

                StockInterface face = StockClient.getClient().create(StockInterface.class);
                Call<GetStockByBarcodeResponse> call = face.getStockByBarcode(
                    DiabloProfile.instance().getToken(),
                    new GetStockByBarcodeRequest(mBarcode.getCut(), mCurrentShop.getShop()));

                call.enqueue(new Callback<GetStockByBarcodeResponse>() {
                    @Override
                    public void onResponse(final Call<GetStockByBarcodeResponse> call,
                                           Response<GetStockByBarcodeResponse> response) {
                        Log.d(LOG_TAG, "success to get stock by barcode");
                        DiabloBarcodeStock stock = response.body().getBarcodeStock();
                        if (null == stock.getStyleNumber()) {
                            DiabloUtils.makeToast(getContext(), DiabloError.getError(9901), Toast.LENGTH_LONG);
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<GetStockByBarcodeResponse> call, Throwable t) {
                        Log.d(LOG_TAG, "failed to get stock by barcode");
                        DiabloUtils.makeToast(getContext(), DiabloError.getError(500));
                    }
                });

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_fix, container, false);
        mBarCodeScanView = (EditText)view.findViewById(R.id.fix_barcode);

        ((TableLayout)view.findViewById(R.id.t_stock_fix_head)).addView(addHead());
        return view;
    }

    private TableRow addHead(){
        TableRow row = new TableRow(this.getContext());
        for (String title: mTitles){
            TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TextView cell = new TextView(this.getContext());
            // font
            cell.setTypeface(null, Typeface.BOLD);
            cell.setTextColor(Color.BLACK);

            if (getResources().getString(R.string.barcode).equals(title)){
                lp.weight = 1.5f;
            } else if (getResources().getString(R.string.order_id).equals(title)){
                lp.weight = 0.8f;
            }

            cell.setLayoutParams(lp);
            cell.setText(title);
            cell.setTextSize(16);
            row.addView(cell);
        }

        return row;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_stock_fix, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.fix_select_shop:
                final List<DiabloShop> shops = DiabloProfile.instance().getSortShop();
                String [] titles = new String[shops.size()];
                for (int i=0; i< shops.size(); i++) {
                    titles[i] = shops.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ic_local_airport_black_24dp);
                builder.setTitle(getContext().getResources().getString(R.string.select_shop));
                builder.setSingleChoiceItems(titles, shops.indexOf(mCurrentShop),
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        mCurrentShop = shops.get(i);
                    }
                });
                builder.create().show();
                break;
            case R.id.fix_logout:
                ((MainActivity)getActivity()).logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mScanReceiver, mScannerResultIntentFilter);
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mScanReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mScanReceiver = null;
        mScannerResultIntentFilter = null;
        super.onDestroy();
    }
}
