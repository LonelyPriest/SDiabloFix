package com.sdiablofix.dt.sdiablofix.fragment;


import static com.sdiablofix.dt.sdiablofix.utils.DiabloUtils.addCell;
import static com.sdiablofix.dt.sdiablofix.utils.DiabloUtils.makeToast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloShop;
import com.sdiablofix.dt.sdiablofix.entity.DiabloStockNote;
import com.sdiablofix.dt.sdiablofix.request.GetStockByBarcodeRequest;
import com.sdiablofix.dt.sdiablofix.request.GetStockNoteRequest;
import com.sdiablofix.dt.sdiablofix.request.StockOutRequest;
import com.sdiablofix.dt.sdiablofix.response.GetStockByBarcodeResponse;
import com.sdiablofix.dt.sdiablofix.response.GetStockNoteResponse;
import com.sdiablofix.dt.sdiablofix.response.StockOutResponse;
import com.sdiablofix.dt.sdiablofix.rest.StockInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloAlertDialog;
import com.sdiablofix.dt.sdiablofix.utils.DiabloButton;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloError;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;
import com.sdiablofix.dt.sdiablofix.utils.ScannerInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BatchStockOut#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BatchStockOut extends Fragment {
    private final String LOG_TAG = "BatchStockOut:";
    private DiabloShop mCurrentShop;
    private Integer mFirm;
    private boolean mRejectWithFirm;
    private boolean mRejectWithOrgPrice;
    private DiabloBigType mCurrentBigType;
    private String [] mTitles;

    /*iData scanner*/
    private ScannerInterface mIDataBarcodeScan;
    private IntentFilter mIDataScannerResultIntentFilter;
    private BroadcastReceiver mIDataScanReceiver;
    private boolean mRegistered = false;

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
    private final StockOutHandler mHandler = new StockOutHandler(this);

    private StockOutRequest.StockOutBase mStockBase;
    private SparseArray<DiabloButton> mButtons;

    private ArrayList<TableRow> mTableRows;


    private static final String IDATA_RES_ACTION = "android.intent.action.SCANRESULT";

    public BatchStockOut() {
        // Required empty public constructor
    }

    public static BatchStockOut newInstance() {
        return new BatchStockOut();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // europa, carme, ceres
        // DiabloUtils.playSound(getContext(), R.raw.europa);

        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();
        mRegistered  = false;

        mCurrentShop = DiabloProfile.instance().getShop(DiabloProfile.instance().getLoginShop());
        mCurrentBigType = DiabloProfile.instance().getBigTypes().get(0);
        mTitles = getResources().getStringArray(R.array.thead_fix);

        mButtons = new SparseArray<>();
        mButtons.put(R.id.stock_out_save, new DiabloButton(getContext(), R.id.stock_out_save));
        mButtons.get(R.id.stock_out_save).disable();

        initIDataScanner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_out, container, false);
        mBarCodeScanView = (EditText)view.findViewById(R.id.out_barcode);
        mBarCodeScanView.setInputType(InputType.TYPE_NULL);
        mBarCodeScanView.setTextColor(Color.GRAY);

        mStyleNumberScanView = (EditText)view.findViewById(R.id.out_styleNumber);
        mStyleNumberScanView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mStyleNumberScanView.setTextColor(Color.BLUE);
        // mFixCoutView.setTextColor(Color.RED);
        // mFixCoutView.setText("0");

        mFixCoutView = (TextView)view.findViewById(R.id.out_count);

        mTable = (TableLayout) view.findViewById(R.id.t_stock_out);
        ((TableLayout)view.findViewById(R.id.t_stock_out_head)).addView(addTitle());

        mTableSwipe = (SwipyRefreshLayout) view.findViewById(R.id.t_stock_out_swipe);
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
                        makeToast(
                            getContext(),
                            getContext().getResources().getString(R.string.refresh_top),
                            Toast.LENGTH_SHORT);
                        mTableSwipe.setRefreshing(false);
                    }

                } else if (direction == SwipyRefreshLayoutDirection.BOTTOM){
                    if (mCurrentPage.equals(mTotalPage)) {
                        makeToast(
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
//            DiabloEnum.DIABLO_SCANNER_DEVICE, DiabloEnum.DIABLO_DEFAULT_SCANNER_DEVICE);

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
        mFirm = DiabloEnum.INVALID_INDEX;

        String stockConfig = DiabloProfile.instance().getConfig(
            DiabloEnum.DIABLO_STOCK_CONFIG,
            DiabloEnum.DIABLO_DEFAULT_STOCK_CONFIG_VALUE);
        mRejectWithFirm = stockConfig.length() <= 3 || stockConfig.charAt(2) != DiabloEnum.DIABLO_FALSE;
        mRejectWithOrgPrice = stockConfig.length() <= 4 || stockConfig.charAt(3) != DiabloEnum.DIABLO_FALSE;

        String autoBarcode = DiabloProfile.instance().getConfig(
            DiabloEnum.SETTING_AUTO_BARCODE,
            DiabloEnum.DIABLO_CONFIG_YES);
        mBarcode = new DiabloBarcode(autoBarcode);

        mBarcodeStocks = new ArrayList<>();
        mStockBase = new StockOutRequest.StockOutBase();
        mStockBase.setShop(mCurrentShop.getShop());
        mStockBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());

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
        // pagination
        mTotalPage = 0;
        mCurrentPage = 1;

        initTitle();
    }

    private void initIDataScanner() {
        mIDataBarcodeScan = new ScannerInterface(getContext());
        mIDataBarcodeScan.setOutputMode(1);
        mIDataBarcodeScan.enableFailurePlayBeep(true);

        mIDataScannerResultIntentFilter = new IntentFilter();
        mIDataScannerResultIntentFilter.addAction(IDATA_RES_ACTION);

        mIDataScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult = intent.getStringExtra("value");

                if (intent.getAction().equals(IDATA_RES_ACTION)){
                    if(scanResult.length()>0){
                        // DiabloUtils.playSound(getContext(), R.raw.wire_charging_start);
                        mBarCodeScanView.setText(scanResult);
                        mBarCodeScanView.invalidate();
                        mBarcode.correctBarcode(mBarCodeScanView.getText().toString());
                        process_scan();
                    } else {
                        makeToast(getContext(), "解码失败");
                    }
                }


            }
        };

        if (!mRegistered) {
            getContext().registerReceiver(mIDataScanReceiver, mIDataScannerResultIntentFilter);
        }
        mRegistered = true;
    }

    private void process_scan() {
        final StockInterface face = StockClient.getClient().create(StockInterface.class);
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
                }

                final DiabloBarcodeStock stock = response.body().getBarcodeStock();
                if (null == stock.getStyleNumber()) {
                    DiabloUtils.error_alarm(getContext(), 9901, R.raw.europa);
                } else {
                    check_stock(stock);
                }
            }

            @Override
            public void onFailure(Call<GetStockByBarcodeResponse> call, Throwable t) {
                Log.d(LOG_TAG, "failed to get stock by barcode");
                DiabloUtils.error_alarm(getContext(), 500, R.raw.ceres);
            }
        });
    }

    private void check_stock(final DiabloBarcodeStock stock) {
        if (!mRejectWithFirm && !mRejectWithOrgPrice) {
            stock.setCorrectBarcode(mBarcode.getCorrect());
            start_reject(stock);
        } else if (mRejectWithFirm && mRejectWithOrgPrice) {
            if (stock.getFirmId().equals(DiabloEnum.INVALID_INDEX)) {
                DiabloUtils.error_alarm(getContext(), 9906, R.raw.iapetus);
            } else if (stock.getOrgPrice() <= 0f) {
                DiabloUtils.error_alarm(getContext(), 9907, R.raw.iapetus);
            } else {
                start_reject_with_firm(stock);
            }
        } else if (!mRejectWithFirm) {
            if (stock.getOrgPrice() <= 0f) {
                DiabloUtils.error_alarm(getContext(), 9907, R.raw.iapetus);
            } else {
                stock.setCorrectBarcode(mBarcode.getCorrect());
                start_reject(stock);
            }
        } else {
            if (stock.getFirmId().equals(DiabloEnum.INVALID_INDEX)) {
                DiabloUtils.error_alarm(getContext(), 9907, R.raw.iapetus);
            } else {
                start_reject_with_firm(stock);
            }

        }

    }

    private void start_reject_with_firm(DiabloBarcodeStock stock) {
        if (mFirm.equals(DiabloEnum.INVALID_INDEX)) {
            mFirm = stock.getFirmId();
        }

        if (!mFirm.equals(stock.getFirmId())) {
            DiabloUtils.error_alarm(getContext(), 9905, R.raw.iapetus);
        } else {
            stock.setCorrectBarcode(mBarcode.getCorrect());
            start_reject(stock);
        }
    }

    private void start_reject(final DiabloBarcodeStock stock) {
        if (stock.getFree().equals(DiabloEnum.DIABLO_FREE)) {
            stock.setColor(DiabloEnum.DIABLO_FREE_COLOR);
            stock.setSize(DiabloEnum.DIABLO_FREE_SIZE);
        } else {
            String colorSize = stock.getCorrectBarcode().substring(
                stock.getBarcode().length(), stock.getCorrectBarcode().length());
            Integer colorBarcode = DiabloUtils.toInteger(colorSize.substring(0, 3));
            Integer sizeIndex = DiabloUtils.toInteger(colorSize.substring(3, colorSize.length()));
            DiabloColor color = DiabloProfile.instance().getColorByBarcode(colorBarcode);
            stock.setColor(color.getColorId());
            if (sizeIndex == 0) {
                stock.setSize(DiabloEnum.DIABLO_FREE_SIZE);
            } else {
                stock.setSize(DiabloEnum.DIABLO_SIZE_TO_BARCODE[sizeIndex]);
            }
        }


        // query real stock use color and size
        Integer count = getStockNoteCount(stock);
        if (!DiabloEnum.INVALID.equals(count)) {
            if (getAllStockNoteFix(stock) >= count) {
                DiabloUtils.error_alarm(getContext(), 9904, R.raw.carme);
            } else {
                stock.setFix(1);
                stock.setCount(count);

                Message message = Message.obtain(mHandler);
                message.what = DiabloEnum.STOCK_OUT;
                message.obj = stock;
                message.sendToTarget();
            }
        } else {
            final StockInterface face = StockClient.getClient().create(StockInterface.class);
            Call<GetStockNoteResponse> call1 = face.getStockNote(
                DiabloProfile.instance().getToken(),
                new GetStockNoteRequest(
                    mCurrentShop.getShop(),
                    stock.getStyleNumber(),
                    stock.getBrandId(),
                    stock.getColor(),
                    stock.getSize()));

            call1.enqueue(new Callback<GetStockNoteResponse>() {
                @Override
                public void onResponse(Call<GetStockNoteResponse> call, Response<GetStockNoteResponse> response) {
                    Log.d(LOG_TAG, "success to get stock note");
                    Log.d(LOG_TAG, "response = " + response.toString());
                    if (!response.isSuccessful()) {
                        DiabloUtils.error_alarm(getContext(), 9903, R.raw.wire_charging_start);
                    } else {
                        DiabloStockNote stockNote = response.body().getStockNote();
                        if (null == stockNote.getStyleNumber()) {
                            DiabloUtils.error_alarm(getContext(), 9901, R.raw.europa);
                        } else {
                            if (stockNote.getCount() <=0) {
                                DiabloUtils.error_alarm(getContext(), 9904, R.raw.carme);
                            } else {
                                stock.setFix(1);
                                stock.setCount(stockNote.getCount());
                                // handler
                                Message message = Message.obtain(mHandler);
                                message.what = DiabloEnum.STOCK_OUT;
                                message.obj = stock;
                                message.sendToTarget();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetStockNoteResponse> call, Throwable t) {
                    Log.d(LOG_TAG, "failed to get stock note");
                    DiabloUtils.error_alarm(getContext(), 500, R.raw.ceres);
                }
            });
        }
    }

    private void initTitle() {
        ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (null != bar && null != bar.getTitle()) {
            bar.setTitle(
                getResources().getString(R.string.title_stock_out)
                    + "(" + mCurrentShop.getName() + "-" + mStockBase.getShortDatetime() + ")");
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
                lp.weight = 1.6f;
            } else if (getResources().getString(R.string.order_id).equals(title)){
                lp.weight = 0.5f;
            } else if (getResources().getString(R.string.style_number_brand).equals(title)){
                lp.weight = 0.8f;
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
            DiabloUtils.formatPageInfo(addCell(getContext(), mPageInfoRow, pageInfo, lp));
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
        DiabloUtils.formatPageInfo(addCell(getContext(), mPageInfoRow, pageInfo, lp));
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
                lp.weight = 1.6f;
                cell = addCell(getContext(), row, stock.getCorrectBarcode(), lp);
            }
            else if (getResources().getString(R.string.style_number_brand).equals(title)){
                lp.weight = 0.8f;
                cell = addCell(getContext(),
                    row,
                    stock.getStyleNumber() + "/" + stock.getFixPos() + "-" + stock.getAmount(),
                    lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
            else if (getResources().getString(R.string.color_size).equals(title)) {
                if (stock.getFree().equals(DiabloEnum.DIABLO_FREE)) {
                    cell = addCell(getContext(), row, "F/F", lp);
                }
                else {
                    String desc = DiabloProfile.instance().getColor(stock.getColor()).getName();
                    desc += stock.getSize().equals(DiabloEnum.DIABLO_FREE_SIZE) ? "F" : stock.getSize();
                    cell = addCell(getContext(), row, desc, lp);
                }
            }

            if (null != cell ) {
                cell.setBackgroundResource(R.drawable.table_cell_bg);
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
        inflater.inflate(R.menu.menu_stock_out, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.stock_out_select_shop: {
                if (!mBarcodeStocks.isEmpty()) {
                    new DiabloAlertDialog(getContext(),
                        "采购退货",
                        "非空单情况不允许切换店铺，请先清空单据内空").create();
                } else {
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
                                mStockBase.setShop(mCurrentShop.getShop());
                                initTitle();
                            }
                        });
                    builder.create().show();
                }
            }
            break;
            case R.id.stock_out_save:
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "采购退货",
                    "确认要退货吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            mBarCodeScanView.setText(null);
                            mStockBase.setTotal(mBarcodeStocks.size());
                            mStockBase.setFirm(mFirm);
                            Float shouldPay = 0f;
                            StockOutRequest request = new StockOutRequest(mStockBase);
                            for (DiabloBarcodeStock stock: mBarcodeStocks) {
                                StockOutRequest.StockOut out = new StockOutRequest.StockOut();
                                out.addStockNote(stock.getColor(), stock.getSize(), stock.getFix());
                                out.setStyleNumber(stock.getStyleNumber());
                                out.setBrand(stock.getBrandId());

                                out.setSex(stock.getSex());
                                out.setType(stock.getTypeId());
                                out.setFirm(stock.getFirmId());
                                out.setSeason(stock.getSeason());
                                out.setYear(stock.getYear());
                                out.setTagPrice(stock.getTagPrice());
                                out.setS_group(stock.getsGroup());
                                out.setFree(stock.getFree());
                                out.setAlarm_day(stock.getAlarm_day());

                                out.setPath(stock.getPath());
                                out.setTotal(stock.getFix());

                                out.setTagPrice(stock.getTagPrice());
                                out.setOrgPrice(stock.getOrgPrice());
                                out.setEdiscount(stock.getEdiscount());
                                out.setDiscount(stock.getDiscount());

                                shouldPay += stock.getOrgPrice() * stock.getFix();
                                request.addStock(out);
                            }

                            mStockBase.setShouldPay(-shouldPay);

                            mButtons.get(R.id.stock_out_save).disable();
                            makeToast(getContext(), "操作成功，请等待操作结果", Toast.LENGTH_LONG);
                            StockInterface face = StockClient.getClient().create(StockInterface.class);
                            Call<StockOutResponse> call = face.rejectStock(DiabloProfile.instance().getToken(), request);
                            call.enqueue(new Callback<StockOutResponse>() {
                                @Override
                                public void onResponse(Call<StockOutResponse> call, Response<StockOutResponse> response) {
                                    StockOutResponse result = response.body();
                                    if ( DiabloEnum.HTTP_OK == response.code() && result.getCode().equals(DiabloEnum.SUCCESS)) {
                                        new DiabloAlertDialog(
                                            getContext(),
                                            false,
                                            getResources().getString(R.string.stock_out),
                                            getResources().getString(R.string.success_to_reject_stock)
                                                + result.getRsn()
                                                + getResources().getString(R.string.print_stock_out),
                                            new DiabloAlertDialog.OnOkClickListener() {
                                                @Override
                                                public void onOk() {
                                                    // clear  draft
                                                    DiabloDBManager.instance().clearDraft(mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT);
                                                    mTable.removeAllViews();
                                                    init();
                                                }})
                                            .create();

                                    }
                                    else {
                                        mButtons.get(R.id.stock_out_save).enable();
                                        String error = "";
                                        if (!DiabloEnum.HTTP_OK.equals(response.code())) {
                                            error += "系统错误，返回码：" + DiabloUtils.toString(response.code());
                                        } else {
                                            if (!DiabloEnum.SUCCESS.equals(result.getCode())) {
                                                error += "退货失败：" + DiabloError.getError(result.getCode())
                                                    + result.getError();
                                            }
                                        }
                                        new DiabloAlertDialog(
                                            getContext(),
                                            getResources().getString(R.string.stock_out), error).create();
                                    }
                                }

                                @Override
                                public void onFailure(Call<StockOutResponse> call, Throwable t) {
                                    mButtons.get(R.id.stock_out_save).enable();
                                    new DiabloAlertDialog(
                                        getContext(),
                                        getResources().getString(R.string.stock_out),
                                        "退货失败：" + DiabloError.getError(99)).create();
                                }
                            });
                        }
                    }).create();
                break;
            case R.id.stock_out_draft:
                // recover from db
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "退货草稿",
                    "草稿会覆盖现有内容，确定要获取草稿吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            StockOutRequest.StockOutBase base = DiabloDBManager.instance().getStockOutBase(
                                mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT);
                            if (null != base) {
                                mStockBase = base;
                                mStockBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());
                                initTitle();
                                mTotalPage = 0;
                                mCurrentPage = 1;
                                List<DiabloBarcodeStock> stocks = DiabloDBManager.instance().listReject(mCurrentShop.getShop());
                                if (0 != stocks.size()) {
                                    // mTable.removeAllViews();
                                    mBarcodeStocks.clear();
                                    for (int i = 0; i<stocks.size(); i++) {
                                        if (null == stocks.get(i).getOrderId()) {
                                            stocks.get(i).setOrderId(i + 1);
                                        }
                                        mBarcodeStocks.add(0, stocks.get(i));
                                    }

                                    mFirm = stocks.get(0).getFirmId();
                                    mFixCoutView.setText(String.valueOf(getFixStocksCount()));
                                    addHead();
                                    mButtons.get(R.id.stock_out_save).enable();
                                }
                            } else {
                                makeToast(getContext(), "店铺对应的草稿不存在，请确认店铺是否选择正确", Toast.LENGTH_LONG);
                            }

                        }
                    }).create();
                break;

            case R.id.stock_out_clear_draft:
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "清除草稿",
                    "草稿清除后不可恢复，确定要清除吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            DiabloDBManager.instance().clearDraft(mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT);
                            mButtons.get(R.id.stock_out_save).disable();
                            makeToast(getContext(), "清除草稿成功", Toast.LENGTH_LONG);
                        }
                    }).create();
                break;

            case R.id.stock_out_clear_firm:
                new DiabloAlertDialog(
                    getContext(),
                    true,
                    "重置厂商",
                    "确定要重置厂商吗？",
                    new DiabloAlertDialog.OnOkClickListener() {
                        @Override
                        public void onOk() {
                            if (!mBarcodeStocks.isEmpty()) {
                                DiabloUtils.makeToast(getContext(), "退货单非空，清空退货单后重新操作", Toast.LENGTH_LONG);
                            } else {
                                mFirm = DiabloEnum.INVALID_INDEX;
                            }
                        }
                    }).create();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mCurrentRow = (TableRow)v;
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_on_stock_out, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        DiabloBarcodeStock stock = (DiabloBarcodeStock)mCurrentRow.getTag();
        Integer orderId = stock.getOrderId();

        if (getResources().getString(R.string.delete) == item.getTitle()){
            unregisterForContextMenu(mCurrentRow);
            mCurrentRow.removeAllViews();

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
                    mBarcodeStocks.get(j).setFixPos(mBarcodeStocks.get(j).getFixPos() - 1);
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
                DiabloDBManager.instance().deleteReject(mCurrentShop.getShop(), orderId);
            } else {
                DiabloDBManager.instance().replaceReject(mCurrentShop.getShop(), mBarcodeStocks);
            }

            if (0 == mBarcodeStocks.size()) {
                mFirm = DiabloEnum.INVALID_INDEX;
                mButtons.get(R.id.stock_out_save).disable();
            }

        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onResume() {
        Log.d(LOG_TAG, "stockOut onResume!!!");
        super.onResume();
//        if(!mRegistered) {
//            getContext().registerReceiver(mIDataScanReceiver, mIDataScannerResultIntentFilter);
//        }
//        mRegistered = true;
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "stockOut onPause!!!");
//        if (mRegistered) {
//            getContext().unregisterReceiver(mIDataScanReceiver);
//        }
//        mRegistered = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "stockOut onDestroy...");
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

    private Integer getPosInFixStocks(DiabloBarcodeStock stock) {
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

    private Integer getAllStockNoteFix(DiabloBarcodeStock stock) {
        Integer fix = 0;
        for (int i=0; i<mBarcodeStocks.size(); i++) {
            DiabloBarcodeStock s = mBarcodeStocks.get(i);
            if (s.getStyleNumber().equals(stock.getStyleNumber())
                && s.getBrandId().equals(stock.getBrandId())
                && s.getColor().equals(stock.getColor())
                && s.getSize().equals(stock.getSize())) {
                fix += s.getFix();
            }
        }
        return fix;
    }

    private static class StockOutHandler extends Handler {
        WeakReference<Fragment> mFragment;
        StockOutHandler(Fragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DiabloEnum.STOCK_OUT){
                final BatchStockOut f = ((BatchStockOut)mFragment.get());
                DiabloBarcodeStock stock = (DiabloBarcodeStock) msg.obj;
                stock.setFixPos(f.getPosInFixStocks(stock));
                f.mBarcodeStocks.add(0, stock);

                if (null == stock.getOrderId() || 0 == stock.getOrderId()) {
                    stock.setOrderId(f.mBarcodeStocks.size());
                }

                if(stock.getFixPos() == 1) {
                    DiabloUtils.playSound(f.getContext(), R.raw.elara);
                }

                // fix count more than real stock, alarm
//                if (stock.getFixPos() > stock.getAmount()) {
//                    DiabloUtils.playSound(f.getContext(), R.raw.carme);
//                }

                f.mFixCoutView.setText(String.valueOf(f.getFixStocksCount()));
                f.mStyleNumberScanView.setText(
                    stock.getStyleNumber() + "/" + stock.getFixPos() + "-" + stock.getAmount());

                f.addHead();

                if (1 == f.mBarcodeStocks.size()) {
                    f.mButtons.get(R.id.stock_out_save).enable();
                    if ( null == DiabloDBManager.instance().getStockOutBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT) ){
                        DiabloDBManager.instance().addBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT);
                    } else {
                        DiabloDBManager.instance().updateBase(f.mCurrentShop.getShop(), DiabloEnum.SCAN_STOCK_OUT);
                    }
                }

                DiabloDBManager.instance().addReject(f.mCurrentShop.getShop(), stock);
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
        Log.d(LOG_TAG, "stockOut hidden==" + (hidden ? "hidden" : "show"));
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