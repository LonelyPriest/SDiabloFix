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

import com.example.iscandemo.ScannerInerface;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.activity.MainActivity;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloShop;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockFix#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockFix extends Fragment {
    private DiabloShop mCurrentShop;
    private String [] mTitles;

    /*scanner*/
    private IntentFilter mScannerResultIntentFilter;
    private BroadcastReceiver mScanReceiver;


    EditText mBarCodeScanView;

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
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_fix, container, false);
        mBarCodeScanView = (EditText)view.findViewById(R.id.fix_barcode);
        
        ((TableLayout)view.findViewById(R.id.t_stock_fix_head)).addView(addHead());
//        Spinner shopSpinner = (Spinner) view.findViewById(R.id.fix_select_shop);
//        ShopAdapter adapter = new ShopAdapter(
//            getContext(),
//            android.R.layout.simple_spinner_item,
//            DiabloProfile.instance().getSortShop());
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        shopSpinner.setAdapter(adapter);
//
//
//        shopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
