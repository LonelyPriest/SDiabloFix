package com.sdiablofix.dt.sdiablofix.fragment;


import static com.sdiablofix.dt.sdiablofix.utils.DiabloUtils.addCell;
import static com.sdiablofix.dt.sdiablofix.utils.DiabloUtils.makeToast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.client.StockClient;
import com.sdiablofix.dt.sdiablofix.db.DiabloDBManager;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcode;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBigType;
import com.sdiablofix.dt.sdiablofix.entity.DiabloColor;
import com.sdiablofix.dt.sdiablofix.entity.DiabloDevice;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloShop;
import com.sdiablofix.dt.sdiablofix.request.GetStockByBarcodeRequest;
import com.sdiablofix.dt.sdiablofix.request.StockFixRequest;
import com.sdiablofix.dt.sdiablofix.response.GetStockByBarcodeResponse;
import com.sdiablofix.dt.sdiablofix.response.StockFixResponse;
import com.sdiablofix.dt.sdiablofix.rest.StockInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloAlertDialog;
import com.sdiablofix.dt.sdiablofix.utils.DiabloButton;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEditTextDialog;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloError;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;
import com.sdiablofix.dt.sdiablofix.utils.ScannerInterface;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BatchStockFix extends Fragment {
    private final String LOG_TAG = "BatchStockFix:";
    private final int REQUEST_CODE = 0xff;
    private DiabloShop mCurrentShop;
    private DiabloBigType mCurrentBigType;
    private String [] mTitles;

    private String [] mFixDevice;
    private Integer mCurrentDevice = 0;

    /*iData scanner*/
    private ScannerInterface mIDataBarcodeScan;
    private IntentFilter mIDataScannerResultIntentFilter;
    private BroadcastReceiver mIDataScanReceiver;
    private boolean mRegistered = false;

    /*phone scanner*/
    Intent mPhoneIntent;

    private TableLayout mTable;
    private TableRow mCurrentRow;
    private TableRow mPageInfoRow;

    private EditText mBarCodeScanView;
    private EditText mStyleNumberScanView;
    private TextView mFixCoutView;
    private SwipyRefreshLayout mTableSwipe;
    private Integer mCurrentPage;
    private Integer mTotalPage;

    private DiabloBarcode mBarcode;
    private List<DiabloBarcodeStock> mBarcodeStocks;
    private final StockFixHandler mHandler = new StockFixHandler(this);

    private StockFixRequest.StockFixBase mStockFixBase;
    private SparseArray<DiabloButton> mButtons;

    private ArrayList<TableRow> mTableRows;

    private static final String IDATA_RES_ACTION = "android.intent.action.SCANRESULT";

    public BatchStockFix() {
        // Required empty public constructor
    }

    public static BatchStockFix newInstance() {
        return new BatchStockFix();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // europa, carme, ceres
        // DiabloUtils.playSound(getContext(), R.raw.europa);

        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();

        mRegistered = false;

        mCurrentShop = DiabloProfile.instance().getShop(DiabloProfile.instance().getLoginShop());
        mCurrentBigType = DiabloProfile.instance().getBigTypes().get(0);

        mTitles = getResources().getStringArray(R.array.thead_fix);
        mFixDevice = getResources().getStringArray(R.array.fix_device);

        String deviceId = DiabloUtils.getAndroidId(getContext());

        DiabloDevice device = DiabloDBManager.instance().getDevice(deviceId);
        if (null == device ){
            if (null != deviceId && !"".equals(deviceId)) {
                DiabloDBManager.instance().addDevice(deviceId, 0);
            }
        } else {
            mCurrentDevice = device.getDevice();
        }

        String autoBarcode = DiabloProfile.instance().getConfig(DiabloEnum.SETTING_AUTO_BARCODE, DiabloEnum.DIABLO_CONFIG_YES);

        mBarcode = new DiabloBarcode(autoBarcode);
        mButtons = new SparseArray<>();
        mButtons.put(R.id.stock_fix_save, new DiabloButton(getContext(), R.id.stock_fix_save));
        mButtons.get(R.id.stock_fix_save).disable();

        // initIDataScanner();
        mPhoneIntent = new Intent(getActivity(), CaptureActivity.class);
        ZXingLibrary.initDisplayOpinion(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_fix, container, false);
        mBarCodeScanView = (EditText)view.findViewById(R.id.fix_barcode);
        mBarCodeScanView.setInputType(InputType.TYPE_NULL);
        mBarCodeScanView.setTextColor(Color.GRAY);
        mBarCodeScanView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mStyleNumberScanView = (EditText)view.findViewById(R.id.fix_styleNumber);
        mStyleNumberScanView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mStyleNumberScanView.setTextColor(Color.BLUE);
        // mFixCoutView.setTextColor(Color.RED);
        // mFixCoutView.setText("0");

        mFixCoutView = (TextView)view.findViewById(R.id.fix_count);

        mTable = (TableLayout) view.findViewById(R.id.t_stock_fix);
        ((TableLayout)view.findViewById(R.id.t_stock_fix_head)).addView(addTitle());

        mTableSwipe = (SwipyRefreshLayout) view.findViewById(R.id.t_stock_fix_swipe);
        mTableSwipe.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mTableSwipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP){
                    if (!mCurrentPage.equals(DiabloEnum.DEFAULT_PAGE)){
                        mCurrentPage--;
                        pageChanged();
                        mTableSwipe.setRefreshing(false);
                    } else {
                        DiabloUtils.makeToast(
                            getContext(),
                            getContext().getResources().getString(R.string.refresh_top),
                            Toast.LENGTH_SHORT);
                        mTableSwipe.setRefreshing(false);
                    }

                } else if (direction == SwipyRefreshLayoutDirection.BOTTOM){
                    if (mCurrentPage.equals(mTotalPage)) {
                        DiabloUtils.makeToast(
                            getContext(),
                            getContext().getResources().getString(R.string.refresh_bottom),
                            Toast.LENGTH_SHORT);
                        mTableSwipe.setRefreshing(false);
                    } else {
                        mCurrentPage++;
                        pageChanged();
                        mTableSwipe.setRefreshing(false);
                    }

                }
            }
        });

        init();

//        String device = DiabloProfile.instance().getConfig(
//        DiabloEnum.DIABLO_SCANNER_DEVICE, DiabloEnum.DIABLO_DEFAULT_SCANNER_DEVICE);
        if (0 == mCurrentDevice) {
            initIDataScanner();
        } else {
            initPhoneScanner();
        }
        return view;
    }

    private void colorTable() {
        for (int i=0; i<DiabloEnum.DEFAULT_ITEMS_PER_PAGE; i++) {
            TableRow row = mTableRows.get(i);
            if (i % 2 == 0) {
                row.setBackgroundResource(R.color.bootstrap_brand_warning);
            } else {
                row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
            }
        }
    }

    private void init() {
        mBarcodeStocks = new ArrayList<>();
        mStockFixBase = new StockFixRequest.StockFixBase();
        mStockFixBase.setBigType(DiabloEnum.INVALID_INDEX);
        mStockFixBase.setShop(mCurrentShop.getShop());
        mStockFixBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());

        mTableRows = new ArrayList<>();
        for (int i=0; i<DiabloEnum.DEFAULT_ITEMS_PER_PAGE; i++) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.table_row_bg);
            mTableRows.add(0, row);

            if (i % 2 == 0) {
                row.setBackgroundResource(R.color.bootstrap_brand_warning);
            } else {
                row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
            }

            mTable.addView(row, 0);
        }

        mPageInfoRow = new TableRow(getContext());
        mTable.addView(mPageInfoRow);

        mFixCoutView.setText(String.valueOf(getFixStocksCount()));
        mStyleNumberScanView.setText(null);
        // pagination
        mTotalPage = 0;
        mCurrentPage = 1;

        initTitle();
    }

    private void initPhoneScanner() {
        mBarCodeScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.CAMERA},1);
                } else {
                    startActivityForResult(mPhoneIntent, REQUEST_CODE);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(mPhoneIntent, REQUEST_CODE);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String scanResult = bundle.getString(CodeUtils.RESULT_STRING);
                    mBarCodeScanView.setText(scanResult);
                    mBarCodeScanView.invalidate();
                    mBarcode.correctBarcode(mBarCodeScanView.getText().toString());

                    Integer index = check_exist(mBarcode);
                    if (!index.equals(DiabloEnum.INVALID_INDEX)) {
                        start_check_direct(new DiabloBarcodeStock(mBarcodeStocks.get(index)));
                    } else {
                        start_check_backend();
                    }
                    // Toast.makeText(getContext(), "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    DiabloUtils.makeToast(getContext(), "解码失败");
                }
            }
        }
    }

    private void initIDataScanner() {
        mIDataBarcodeScan = new ScannerInterface(getContext());
        mIDataBarcodeScan.setOutputMode(0);
        mIDataBarcodeScan.enableFailurePlayBeep(true);

        mIDataScannerResultIntentFilter = new IntentFilter();
        mIDataScannerResultIntentFilter.addAction(IDATA_RES_ACTION);

        mIDataScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("value");

                if (intent.getAction().equals(IDATA_RES_ACTION)){
                    if(scanResult.length() > 0){
                        // DiabloUtils.playSound(getContext(), R.raw.wire_charging_start);
                        mBarCodeScanView.setText(scanResult);
                        mBarCodeScanView.invalidate();
                        mBarcode.correctBarcode(mBarCodeScanView.getText().toString());

                        Integer index = check_exist(mBarcode);
                        if (!index.equals(DiabloEnum.INVALID_INDEX)) {
                            start_check_direct(new DiabloBarcodeStock(mBarcodeStocks.get(index)));
                        } else {
                            start_check_backend();
                        }
                    }else{
                       DiabloUtils.makeToast(getContext(), "解码失败");
                    }
                }


            }
        };

        if (!mRegistered) {
            getContext().registerReceiver(mIDataScanReceiver, mIDataScannerResultIntentFilter);
        }
        mRegistered = true;
    }

    private Integer check_exist(DiabloBarcode barcode) {
        Integer index = DiabloEnum.INVALID_INDEX;
        for (int i = 0; i<mBarcodeStocks.size(); i++) {
            if (mBarcodeStocks.get(i).getBarcode().equals(barcode.getCut())) {
                index = i;
                break;
            }
        }

        return index;
    }

    private void start_check_direct(DiabloBarcodeStock stock) {
        stock.setCorrectBarcode(mBarcode.getCorrect());

        if (stock.getFree().equals(DiabloEnum.DIABLO_FREE)) {
            stock.setColor(DiabloEnum.DIABLO_FREE_COLOR);
            stock.setSize(DiabloEnum.DIABLO_FREE_SIZE);
        } else {
            String colorSize = stock.getCorrectBarcode().substring(
                stock.getBarcode().length(), stock.getCorrectBarcode().length());

            Integer colorLength = 0;
            DiabloColor color = null;
            if (colorSize.length() == DiabloEnum.DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE) {
                colorLength = 3;
            } else if(colorSize.length() == DiabloEnum.DIABLO_EXT_BARCODE_LENGTH_OF_COLOR_SIZE) {
                if (colorSize.startsWith("0")) {
                    colorLength = 3;
                } else {
                    // try color first
                    color = DiabloProfile.instance().getColorByBarcode(DiabloUtils.toInteger(colorSize.substring(0, 4)));
                    if (null != color) {
                        colorLength = 4;
                        // try size more than 99
//                        if (DiabloUtils.toInteger(
//                            colorSize.substring(3, colorSize.length())) > DiabloEnum.DIABLO_SIZE_TO_BARCODE.length) {
//                            colorLength = 4;
//                        } else {
//                            // size first, only one merchant's color more than 999
//                            colorLength = 3;
//                        }

                    } else  {
                        colorLength = 3;
                    }
                }
            }

            Integer colorBarcode = DiabloUtils.toInteger(colorSize.substring(0, colorLength));
            Integer sizeIndex = DiabloUtils.toInteger(colorSize.substring(colorLength, colorSize.length()));
            if (null == color) {
                color = DiabloProfile.instance().getColorByBarcode(colorBarcode);
            }
            stock.setColor(color.getColorId());
            if (sizeIndex == 0) {
                stock.setSize(DiabloEnum.DIABLO_FREE_SIZE);
            } else {
                stock.setSize(DiabloEnum.DIABLO_SIZE_TO_BARCODE[sizeIndex]);
            }
        }

        stock.setFix(1);

        Message message = Message.obtain(mHandler);
        message.what = DiabloEnum.STOCK_FIX;
        message.obj = stock;
        message.sendToTarget();
    }

    private void start_check_backend() {
        StockInterface face = StockClient.getClient().create(StockInterface.class);
        Call<GetStockByBarcodeResponse> call = face.getStockByBarcode(
            DiabloProfile.instance().getToken(),
            new GetStockByBarcodeRequest(
                    mBarcode.getCut(),
                    mCurrentShop.getShop(),
                    mCurrentBigType.getctype()));

        call.enqueue(new Callback<GetStockByBarcodeResponse>() {
            @Override
            public void onResponse(final Call<GetStockByBarcodeResponse> call,
                                   Response<GetStockByBarcodeResponse> response) {
                Log.d(LOG_TAG, "success to get stock by barcode");
                Log.d(LOG_TAG, "response = " + response.toString());
                if (!response.isSuccessful()) {
                    DiabloUtils.error_alarm(getContext(), 9903, R.raw.wire_charging_start);
                } else {
                    GetStockByBarcodeResponse stockBody = response.body();
                    if (stockBody.getCode().equals(DiabloEnum.SUCCESS)) {
                        DiabloBarcodeStock stock = stockBody.getBarcodeStock();
//                    if (null == stock.getStyleNumber()) {
//                        DiabloUtils.error_alarm(getContext(), 9901, R.raw.europa);
//                    } else {
                        start_check_direct(stock);
                        // }
                    } else {
                        DiabloUtils.error_alarm(getContext(), stockBody.getCode(), R.raw.europa);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetStockByBarcodeResponse> call, Throwable t) {
                Log.d(LOG_TAG, "failed to get stock by barcode");
                DiabloUtils.error_alarm(getContext(), 500, R.raw.ceres);
            }
        });
    }

//    private void check_second(final DiabloBarcodeStock stock) {
//        // query real stock use color and size
//        Integer count = getStockNoteCount(stock);
//        if (!DiabloEnum.INVALID.equals(count)) {
//            stock.setFix(1);
//            stock.setCount(count);
//            Message message = Message.obtain(mHandler);
//            message.what = DiabloEnum.STOCK_FIX;
//            message.obj = stock;
//            message.sendToTarget();
//        } else {
//            final StockInterface face = StockClient.getClient().create(StockInterface.class);
//            Call<GetStockNoteResponse> call1 = face.getStockNote(
//                DiabloProfile.instance().getToken(),
//                new GetStockNoteRequest(
//                    mCurrentShop.getShop(),
//                    stock.getStyleNumber(),
//                    stock.getBrandId(),
//                    stock.getColor(),
//                    stock.getSize()));
//
//            call1.enqueue(new Callback<GetStockNoteResponse>() {
//                @Override
//                public void onResponse(Call<GetStockNoteResponse> call, Response<GetStockNoteResponse> response) {
//                    Log.d(LOG_TAG, "success to get stock note");
//                    if (!response.isSuccessful()) {
//                        DiabloUtils.error_alarm(getContext(), 9903, R.raw.wire_charging_start);
//                    } else {
//                        DiabloStockNote stockNote = response.body().getStockNote();
//                        if (null == stockNote.getStyleNumber()) {
//                            DiabloUtils.error_alarm(getContext(), 9901, R.raw.europa);
//                        } else {
//                            // DiabloUtils.playSound(getContext(), R.raw.elara);
//
//                            stock.setFix(1);
//                            stock.setCount(stockNote.getCount());
//                            // handler
//                            Message message = Message.obtain(mHandler);
//                            message.what = DiabloEnum.STOCK_FIX;
//                            message.obj = stock;
//                            message.sendToTarget();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GetStockNoteResponse> call, Throwable t) {
//                    Log.d(LOG_TAG, "failed to get stock note");
//                    DiabloUtils.error_alarm(getContext(), 500, R.raw.ceres);
//                }
//            });
//        }
//    }

    private void initTitle() {
        ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (null != bar && null != bar.getTitle()) {
            bar.setTitle(
                getResources().getString(R.string.title_stock_fix)
                    + "(" + mCurrentShop.getName() + "-" + mStockFixBase.getShortDatetime() + ")");
        }
    }



    private TableRow addTitle(){
        TableRow row = new TableRow(this.getContext());
        for (String title: mTitles){
            TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TextView cell = new TextView(this.getContext());
            // font
            cell.setTypeface(null, Typeface.BOLD);
            cell.setTextColor(Color.BLACK);

            if (getResources().getString(R.string.barcode).equals(title)){
                lp.weight = 1.2f;
            } else if (getResources().getString(R.string.order_id).equals(title)){
                lp.weight = 0.5f;
            } else if (getResources().getString(R.string.style_number_brand).equals(title)){
                lp.weight = 0.8f;
            } else if (getResources().getString(R.string.fix_count).equals(title)) {
                lp.weight = 0.5f;
            }

            cell.setLayoutParams(lp);
            cell.setText(title);
            cell.setTextSize(16);
            row.addView(cell);
        }

        return row;
    }

    public void calcPageInfo() {
        Integer pages = DiabloUtils.calcPage(mBarcodeStocks.size(), DiabloEnum.DEFAULT_ITEMS_PER_PAGE);
        if (pages > 0 && !mTotalPage.equals(pages)) {
            mTotalPage = pages;
            TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            String pageInfo = mCurrentPage.toString() + "/" + mTotalPage.toString();
            mPageInfoRow.removeAllViews();
            DiabloUtils.formatPageInfo(DiabloUtils.addCell(getContext(), mPageInfoRow, pageInfo, lp));
        }
    }

    public void pageChanged() {
        int startPage = (mCurrentPage-1) * DiabloEnum.DEFAULT_ITEMS_PER_PAGE;
        for (int i=0; i<DiabloEnum.DEFAULT_ITEMS_PER_PAGE; i++) {
            TableRow row = mTableRows.get(i);
            unregisterForContextMenu(row);
            row.removeAllViews();
            if (i + startPage < mBarcodeStocks.size()) {
                DiabloBarcodeStock s = mBarcodeStocks.get(i + startPage);
                addRow(s, row);
            }
        }

        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        String pageInfo = mCurrentPage.toString() + "/" + mTotalPage.toString();
        mPageInfoRow.removeAllViews();
        DiabloUtils.formatPageInfo(DiabloUtils.addCell(getContext(), mPageInfoRow, pageInfo, lp));
    }

    private void addHead() {
        for (int i=0; i<DiabloEnum.DEFAULT_ITEMS_PER_PAGE; i++) {
            TableRow row = mTableRows.get(i);
            unregisterForContextMenu(row);
            row.removeAllViews();
            if (i < mBarcodeStocks.size()) {
                DiabloBarcodeStock s = mBarcodeStocks.get(i);
                addRow(s, row);
            }
        }

        calcPageInfo();
    }

    private void addCurrentPage() {
        int startPage = (mCurrentPage-1) * DiabloEnum.DEFAULT_ITEMS_PER_PAGE;
        for (int i=0; i<DiabloEnum.DEFAULT_ITEMS_PER_PAGE; i++) {
            TableRow row = mTableRows.get(i);
            unregisterForContextMenu(row);
            row.removeAllViews();
            if (i + startPage < mBarcodeStocks.size()) {
                DiabloBarcodeStock s = mBarcodeStocks.get(i + startPage);
                addRow(s, row);
            }
        }

        calcPageInfo();
    }

    private TableRow addRow(DiabloBarcodeStock stock, TableRow row) {
        TextView cell = null;
        for (String title: mTitles){
            TableRow.LayoutParams lp = DiabloUtils.createTableRowParams(1f);
            lp.setMargins(0, 1, 0, 1);
            if (getResources().getString(R.string.order_id).equals(title)) {
                lp.weight = 0.5f;
                cell = addCell(getContext(), row, stock.getOrderId(), lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.magenta));
            }
            else if (getResources().getString(R.string.barcode).equals(title)){
                lp.weight = 1.2f;
                cell = addCell(getContext(), row, stock.getCorrectBarcode(), lp);
            }
            else if (getResources().getString(R.string.fix_count).equals(title)) {
                lp.weight = 0.5f;
                cell = addCell(getContext(), row, stock.getFix(), lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.blueLight));
            }
            else if (getResources().getString(R.string.style_number_brand).equals(title)){
                lp.weight = 0.8f;
                cell = addCell(getContext(),
                    row,
                    stock.getStyleNumber() + "/" + stock.getFixPos().toString() + "-" + stock.getAmount(),
                    lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
            else if (getResources().getString(R.string.color_size).equals(title)) {
                if (stock.getFree().equals(DiabloEnum.DIABLO_FREE)) {
                    cell = addCell(getContext(), row, "F/F-" + stock.getCSFixPos().toString(), lp);
                }
                else {
                    String desc = DiabloProfile.instance().getColor(stock.getColor()).getName();
                    desc += stock.getSize().equals(DiabloEnum.DIABLO_FREE_SIZE) ? "F" : stock.getSize();
                    desc += "-" + stock.getCSFixPos().toString();
                    cell = addCell(getContext(), row, desc, lp);
                }
            }

            if (null != cell ) {
                cell.setBackgroundResource(R.drawable.table_cell_bg);
                cell.setTag(title);
            }
        }

        row.setTag(stock);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorTable();
                v.setBackgroundResource(R.color.bootstrap_brand_info);
            }
        });

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // SaleDetailResponse.SaleDetail d = (SaleDetailResponse.SaleDetail)view.getTag();
                DiabloBarcodeStock s = ((DiabloBarcodeStock)view.getTag());
                mStyleNumberScanView.setText(s.getStyleNumber() + "/" + s.getFixPos() + "-" + s.getAmount());
                colorTable();
                view.setBackgroundResource(R.color.bootstrap_brand_info);
                view.showContextMenu();
                return true;
            }
        });
        registerForContextMenu(row);
        // mTable.addView(row, 0);
        return row;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        for (Integer i=0; i<mButtons.size(); i++){
            Integer key = mButtons.keyAt(i);
            DiabloButton button = mButtons.get(key);
            menu.findItem(button.getResId()).setEnabled(button.isEnabled());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_stock_fix, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.fix_select_shop: {
                final List<DiabloShop> shops = DiabloProfile.instance().getSortShop();
                String[] titles = new String[shops.size()];
                for (int i = 0; i < shops.size(); i++) {
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
                            mStockFixBase.setShop(mCurrentShop.getShop());
                            initTitle();
                        }
                    });
                builder.create().show();
            }
            break;
            case R.id.fix_select_ctype: {
                final List<DiabloBigType> bigTypes = DiabloProfile.instance().getBigTypes();

                String[] titles = new String[bigTypes.size()];
                for (int i = 0; i < bigTypes.size(); i++) {
                    titles[i] = bigTypes.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ic_filter_tilt_shift_black_24dp);
                builder.setTitle(getContext().getResources().getString(R.string.select_good_ctype));
                builder.setSingleChoiceItems(titles, bigTypes.indexOf(mCurrentBigType),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mCurrentBigType = bigTypes.get(which);
                            mStockFixBase.setBigType(mCurrentBigType.getctype());
                            initTitle();
                        }
                    });
                builder.create().show();
            }
            break;
            case R.id.stock_fix_save:
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "库存盘点",
                    "确认要生成盘点差异单吗，确认后将无法在原来基础上继续盘点？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            mBarCodeScanView.setText(null);
                            // mStockFixBase.setTotal(mBarcodeStocks.size());
                            mStockFixBase.setTotal(getFixStocksCount());
                            StockFixRequest request = new StockFixRequest(mStockFixBase);
                            for (DiabloBarcodeStock stock: mBarcodeStocks) {
                                StockFixRequest.StockFix stockFix = new StockFixRequest.StockFix();
                                stockFix.setStyleNumber(stock.getStyleNumber());
                                stockFix.setBrand(stock.getBrandId());
                                stockFix.setFix(stock.getFix());
                                stockFix.setColor(stock.getColor());
                                stockFix.setSize(stock.getSize());

                                stockFix.setType(stock.getTypeId());
                                stockFix.setFirm(stock.getFirmId());
                                stockFix.setSeason(stock.getSeason());
                                stockFix.setYear(stock.getYear());
                                stockFix.setTagPrice(stock.getTagPrice());

                                request.addStock(stockFix);
                            }

                            mButtons.get(R.id.stock_fix_save).disable();
                            makeToast(getContext(), "操作成功，请等待盘点结果", Toast.LENGTH_LONG);
                            StockInterface face = StockClient.getClient().create(StockInterface.class);
                            Call<StockFixResponse> call = face.fixStock(DiabloProfile.instance().getToken(), request);
                            call.enqueue(new Callback<StockFixResponse>() {
                                @Override
                                public void onResponse(Call<StockFixResponse> call, Response<StockFixResponse> response) {
                                    StockFixResponse result = response.body();
                                    if ( DiabloEnum.HTTP_OK == response.code() && result.getCode().equals(DiabloEnum.SUCCESS)) {
                                        new DiabloAlertDialog(
                                            getContext(),
                                            false,
                                            getResources().getString(R.string.stock_fix),
                                            getResources().getString(R.string.success_to_fix_stock)
                                                + result.getRsn()
                                                + getResources().getString(R.string.query_stock_fix_difference),
                                            new DiabloAlertDialog.OnOkClickListener() {
                                                @Override
                                                public void onOk() {
                                                    // clear  draft
                                                    DiabloDBManager.instance().clearDraft(
                                                        mCurrentShop.getShop(), DiabloEnum.SCAN_FIX);
                                                    mTable.removeAllViews();
                                                    init();
                                                }})
                                            .create();

                                    }
                                    else {
                                        mButtons.get(R.id.stock_fix_save).enable();
                                        String error = "";
                                        if (!DiabloEnum.HTTP_OK.equals(response.code())) {
                                            error += "系统故障，返回码：" + DiabloUtils.toString(response.code());
                                        } else {
                                            if (!DiabloEnum.SUCCESS.equals(result.getCode())) {
                                                error += "盘点失败：" + DiabloError.getError(result.getCode())
                                                    + result.getError();
                                            }
                                        }
                                        new DiabloAlertDialog(
                                            getContext(),
                                            getResources().getString(R.string.stock_fix), error).create();
                                    }
                                }

                                @Override
                                public void onFailure(Call<StockFixResponse> call, Throwable t) {
                                    mButtons.get(R.id.stock_fix_save).enable();
                                    new DiabloAlertDialog(
                                        getContext(),
                                        getResources().getString(R.string.stock_fix),
                                        "盘点失败：" + DiabloError.getError(99)).create();
                                }
                            });
                        }
                    }).create();
                break;
            case R.id.stock_fix_draft:
                // recover from db
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "盘点草稿",
                    "草稿会覆盖现有内容，确定要获取草稿吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            StockFixRequest.StockFixBase base = DiabloDBManager.instance()
                                .getFixBase(mCurrentShop.getShop(), DiabloEnum.SCAN_FIX);
                            if (null != base) {
                                mStockFixBase = base;
                                mStockFixBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());
                                initTitle();
                                mTotalPage = 0;
                                mCurrentPage = 1;
                                List<DiabloBarcodeStock> stocks = DiabloDBManager.instance().listFixDetail(mCurrentShop.getShop());
                                if (0 != stocks.size()) {
                                    // mTable.removeAllViews();
                                    mBarcodeStocks.clear();
                                    for (int i = 0; i<stocks.size(); i++) {
                                        if (null == stocks.get(i).getOrderId()) {
                                            stocks.get(i).setOrderId(i + 1);
                                        }
                                        mBarcodeStocks.add(0, stocks.get(i));
                                    }
                                    mFixCoutView.setText(String.valueOf(getFixStocksCount()));

                                    addHead();
                                    mButtons.get(R.id.stock_fix_save).enable();
                                }
                            } else {
                                makeToast(getContext(), "店铺对应的草稿不存在，请确认店铺是否选择正确", Toast.LENGTH_LONG);
                            }

                        }
                    }).create();
                break;

            case R.id.fix_clear_draft:
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "清除草稿",
                    "草稿清除后不可恢复，确定要清除吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            DiabloDBManager.instance().clearDraft(mCurrentShop.getShop(), DiabloEnum.SCAN_FIX);
                            mButtons.get(R.id.stock_fix_save).disable();
                            makeToast(getContext(), "清除草稿成功", Toast.LENGTH_LONG);
                        }
                    }).create();
                break;
            case R.id.fix_device_mode:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ic_directions_subway_black_24dp);
                builder.setTitle(getContext().getResources().getString(R.string.select_fix_device));
                builder.setSingleChoiceItems(mFixDevice, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mCurrentDevice = which;
                            // initTitle();
                        }
                    });
                builder.create().show();
                break;
//            case R.id.fix_logout:
//                ((MainActivity)getActivity()).logout();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mCurrentRow = (TableRow)v;
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_on_stock_fix, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final DiabloBarcodeStock stock = (DiabloBarcodeStock)mCurrentRow.getTag();
        final Integer orderId = stock.getOrderId();

        if (getResources().getString(R.string.delete) == item.getTitle()){
            unregisterForContextMenu(mCurrentRow);
            mCurrentRow.removeAllViews();
//            int child = mTable.getChildCount();
//            for (int i=0; i<child; i++){
//                View row = mTable.getChildAt(i);
//                if (row instanceof TableRow) {
//                    if ( ((DiabloBarcodeStock)row.getTag()).getOrderId().equals(orderId) ){
//                        // mTable.removeView(row);
//                        break;
//                    }
//                }
//            }

            // delete from lists
            int index = DiabloEnum.INVALID_INDEX;
            for (int i=0; i<mBarcodeStocks.size(); i++){
                if (mBarcodeStocks.get(i).getOrderId().equals(orderId)){
                    index = i;
                    break;
                }
            }

            DiabloBarcodeStock deletedStock = mBarcodeStocks.get(index);
            for (int j=index - 1; j >= 0; j--) {
                if (mBarcodeStocks.get(j).getStyleNumber().equals(deletedStock.getStyleNumber())
                    && mBarcodeStocks.get(j).getBrandId().equals(deletedStock.getBrandId())) {
                    mBarcodeStocks.get(j).setFixPos(mBarcodeStocks.get(j).getFixPos() - deletedStock.getFix());

                    if (mBarcodeStocks.get(j).getColor().equals(deletedStock.getColor())
                        && mBarcodeStocks.get(j).getSize().equals(deletedStock.getSize())) {
                        mBarcodeStocks.get(j).setCSFixPos(mBarcodeStocks.get(j).getCSFixPos() - deletedStock.getFix());
                    }
                }
            }

            mBarcodeStocks.remove(index);

            // reorder
            for (int i=0; i<mBarcodeStocks.size(); i++) {
                mBarcodeStocks.get(i).setOrderId(mBarcodeStocks.size() - i);
            }

            mFixCoutView.setText(String.valueOf(getFixStocksCount()));

            addCurrentPage();

            // reorder
//            for (int i=0; i<mTable.getChildCount(); i++){
//                View row = mTable.getChildAt(i);
//                if (row instanceof TableRow) {
//                    Integer newOrderId = ((DiabloBarcodeStock)row.getTag()).getOrderId();
//                    ((TextView)((TableRow) row).getChildAt(0)).setText(DiabloUtils.toString(newOrderId));
//                    if (newOrderId % 2 == 0) {
//                        row.setBackgroundResource(R.color.bootstrap_brand_warning);
//                    } else {
//                        row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
//                    }
//                }
//            }

            if (index == 0) {
                DiabloDBManager.instance().deleteFixDetail(mCurrentShop.getShop(), orderId);
            } else {
                DiabloDBManager.instance().replaceFixDetail(mCurrentShop.getShop(), mBarcodeStocks);
            }

            if (0 == mBarcodeStocks.size()) {
                mButtons.get(R.id.stock_fix_save).disable();
            }
        }
        else if (getResources().getString(R.string.modify) == item.getTitle()) {
            new DiabloEditTextDialog(getContext(), true, String.valueOf(stock.getFix()), new DiabloEditTextDialog.OnOkClickListener() {
                @Override
                public void onOk(EditText editText) {
                    Integer oldFix = stock.getFix();
                    Integer newFix = DiabloUtils.toInteger(editText.getText().toString());
                    if (!newFix.equals(oldFix)) {
                        // DiabloDBManager.instance().updateFixDetail(mCurrentShop.getShop(), orderId, newFix);
                        unregisterForContextMenu(mCurrentRow);
                        mCurrentRow.removeAllViews();

                        int index = DiabloEnum.INVALID_INDEX;
                        for (int i = 0; i < mBarcodeStocks.size(); i++) {
                            if (mBarcodeStocks.get(i).getOrderId().equals(orderId)) {
                                index = i;
                                break;
                            }
                        }

                        DiabloBarcodeStock modifyStock = mBarcodeStocks.get(index);
                        modifyStock.setFix(newFix);
                        List<DiabloBarcodeStock> updates = new ArrayList<>();
                        for (int j = index; j >= 0; j--) {
                            if (mBarcodeStocks.get(j).getStyleNumber().equals(modifyStock.getStyleNumber())
                                && mBarcodeStocks.get(j).getBrandId().equals(modifyStock.getBrandId())) {
                                mBarcodeStocks.get(j).setFixPos(mBarcodeStocks.get(j).getFixPos() + (newFix - oldFix));

                                if (mBarcodeStocks.get(j).getColor().equals(modifyStock.getColor())
                                    && mBarcodeStocks.get(j).getSize().equals(modifyStock.getSize())) {
                                    mBarcodeStocks.get(j).setCSFixPos(mBarcodeStocks.get(j).getCSFixPos() + (newFix - oldFix));
                                }

                                updates.add(mBarcodeStocks.get(j));
                            }
                        }

                        DiabloDBManager.instance().updateFixDetail(mCurrentShop.getShop(), updates);

                        mFixCoutView.setText(String.valueOf(getFixStocksCount()));

                        addCurrentPage();
                    }

                }
            }).create();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "stockFix onResume...");
        super.onResume();
//        if (!mRegistered) {
//            getContext().registerReceiver(mIDataScanReceiver, mIDataScannerResultIntentFilter);
//        }
//        mRegistered = true;
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "stockFix onPause...");
//        if (mRegistered) {
//            getContext().unregisterReceiver(mIDataScanReceiver);
//        }
//        mRegistered = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "stockFix onDestroy...");
        super.onDestroy();
        if (mRegistered) {
            getContext().unregisterReceiver(mIDataScanReceiver);
        }
        mRegistered = false;
    }

    private Integer getFixStocksCount() {
        int fixStock = 0;
        for(DiabloBarcodeStock stock: mBarcodeStocks) {
            fixStock += stock.getFix();
        }

        return fixStock;
    }

    private Integer getPosInStocks(DiabloBarcodeStock stock) {
        int pos = 1;
        for (int i=0; i<mBarcodeStocks.size(); i++) {
            DiabloBarcodeStock s = mBarcodeStocks.get(i);
            if (s.getStyleNumber().equals(stock.getStyleNumber())
                && s.getBrandId().equals(stock.getBrandId())) {
                pos = s.getFixPos() + 1;
                break;
            }
        }

        return  pos;
    }

    private Integer getCSPosInStocks(DiabloBarcodeStock stock) {
        int pos = 1;
        for (int i=0; i<mBarcodeStocks.size(); i++) {
            DiabloBarcodeStock s = mBarcodeStocks.get(i);
            if (s.getStyleNumber().equals(stock.getStyleNumber())
                && s.getBrandId().equals(stock.getBrandId())
                && s.getColor().equals(stock.getColor())
                && s.getSize().equals(stock.getSize())) {
                pos = s.getCSFixPos() + 1;
                break;
            }
        }

        return  pos;
    }

    private Integer getStockNoteCount(DiabloBarcodeStock stock) {
        Integer count = DiabloEnum.INVALID;
        for (int i=0; i<mBarcodeStocks.size(); i++) {
            DiabloBarcodeStock s = mBarcodeStocks.get(i);
            if (s.getStyleNumber().equals(stock.getStyleNumber())
                && s.getBrandId().equals(stock.getBrandId())
                && s.getColor().equals(stock.getColor())
                && s.getSize().equals(stock.getSize())) {
                count = s.getCount();
                break;
            }
        }
        return count;
    }

//    private Integer getAllStockNoteFix(DiabloBarcodeStock stock) {
//        Integer fix = 0;
//        for (int i=0; i<mBarcodeStocks.size(); i++) {
//            DiabloBarcodeStock s = mBarcodeStocks.get(i);
//            if (s.getStyleNumber().equals(stock.getStyleNumber())
//                && s.getBrandId().equals(stock.getBrandId())
//                && s.getColor().equals(stock.getColor())
//                && s.getSize().equals(stock.getSize())) {
//                fix += s.getFix();
//            }
//        }
//        return fix;
//    }

    private static class StockFixHandler extends Handler {
        WeakReference<Fragment> mFragment;
        StockFixHandler(Fragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DiabloEnum.STOCK_FIX){
                final BatchStockFix f = ((BatchStockFix)mFragment.get());
                DiabloBarcodeStock stock = (DiabloBarcodeStock) msg.obj;
                stock.setFixPos(f.getPosInStocks(stock));
                stock.setCSFixPos(f.getCSPosInStocks(stock));
                f.mBarcodeStocks.add(0, stock);

                if (null == stock.getOrderId() || 0 == stock.getOrderId()) {
                    stock.setOrderId(f.mBarcodeStocks.size());
                }

//                if (stock.getCSFixPos() == 1 || stock.getFixPos() == 1) {
//                    DiabloUtils.playSound(f.getContext(), R.raw.elara);
//                }

                // fix count more than real stock, alarm
                if (stock.getFixPos() > stock.getAmount()) {
                    DiabloUtils.playSound(f.getContext(), R.raw.carme);
                } else {
                    if (stock.getCSFixPos() == 1) {
                        DiabloUtils.playSound(f.getContext(), R.raw.elara);
                    }
                }
                
                f.mFixCoutView.setText(String.valueOf(f.getFixStocksCount()));
                f.mStyleNumberScanView.setText(
                    stock.getStyleNumber() + "/" + stock.getFixPos() + "-" + stock.getAmount());

                f.addHead();

                if (1 == f.mBarcodeStocks.size()) {
                    f.mButtons.get(R.id.stock_fix_save).enable();
                    if ( null == DiabloDBManager.instance().getFixBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_FIX) ){
                        DiabloDBManager.instance().addBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_FIX);
                    } else {
                        DiabloDBManager.instance().updateBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_FIX);
                    }
                }

                DiabloDBManager.instance().addFix(f.mCurrentShop.getShop(), stock);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DiabloUtils.hiddenKeyboard(getContext(), mBarCodeScanView);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(LOG_TAG, "stockFix hidden==" + (hidden ? "hidden" : "show"));
        super.onHiddenChanged(hidden);
        if (!hidden) {
            DiabloUtils.hiddenKeyboard(getContext(), mBarCodeScanView);
            getContext().registerReceiver(mIDataScanReceiver, mIDataScannerResultIntentFilter);
            mRegistered = true;

        } else {
            getContext().unregisterReceiver(mIDataScanReceiver);
            mRegistered = false;
        }
    }
}
