package com.sdiablofix.dt.sdiablofix.fragment;


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

import com.example.iscandemo.ScannerInerface;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.activity.MainActivity;
import com.sdiablofix.dt.sdiablofix.client.StockClient;
import com.sdiablofix.dt.sdiablofix.db.DiabloDBManager;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcode;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBigType;
import com.sdiablofix.dt.sdiablofix.entity.DiabloColor;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloShop;
import com.sdiablofix.dt.sdiablofix.request.GetStockByBarcodeRequest;
import com.sdiablofix.dt.sdiablofix.request.StockFixRequest;
import com.sdiablofix.dt.sdiablofix.response.GetStockByBarcodeResponse;
import com.sdiablofix.dt.sdiablofix.response.StockFixResponse;
import com.sdiablofix.dt.sdiablofix.rest.StockInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloAlertDialog;
import com.sdiablofix.dt.sdiablofix.utils.DiabloButton;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloError;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BatchStockFix extends Fragment {
    private final String LOG_TAG = "BatchStockFix:";
    private DiabloShop mCurrentShop;
    private DiabloBigType mCurrentBigType;
    private String [] mTitles;

    /*scanner*/
    private IntentFilter mScannerResultIntentFilter;
    private BroadcastReceiver mScanReceiver;

    TableLayout mTable;
    private TableRow mCurrentRow;
    EditText mBarCodeScanView;

    private DiabloBarcode mBarcode;
    private List<DiabloBarcodeStock> mBarcodeStocks;
    private final StockFixHandler mHandler = new StockFixHandler(this);

    private StockFixRequest.StockFixBase mStockFixBase;
    private SparseArray<DiabloButton> mButtons;

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

        mCurrentShop = DiabloProfile.instance().getShop(DiabloProfile.instance().getLoginShop());
        mCurrentBigType = DiabloProfile.instance().getBigTypes().get(0);

        mTitles = getResources().getStringArray(R.array.thead_fix);

        String autoBarcode = DiabloProfile.instance().getConfig(
            DiabloEnum.SETTING_AUTO_BARCODE,
            DiabloEnum.DIABLO_CONFIG_YES);

        mBarcode = new DiabloBarcode(autoBarcode);
        mButtons = new SparseArray<>();
        mButtons.put(R.id.stock_fix_save, new DiabloButton(getContext(), R.id.stock_fix_save));
        mButtons.get(R.id.stock_fix_save).disable();

        init();

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
                start_check();
            }
        };
    }

    private void start_check() {
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
                DiabloBarcodeStock stock = response.body().getBarcodeStock();
                if (null == stock.getStyleNumber()) {
                    DiabloUtils.makeToast(getContext(), DiabloError.getError(9901), Toast.LENGTH_LONG);
                    // play sound
                    DiabloUtils.playSound(getContext(), R.raw.europa);
                } else {
                    stock.setCorrectBarcode(mBarcode.getCorrect());
                    stock.setFix(1);

                    // handler
                    Message message = Message.obtain(mHandler);
                    message.what = DiabloEnum.STOCK_FIX;
                    message.obj = stock;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(Call<GetStockByBarcodeResponse> call, Throwable t) {
                Log.d(LOG_TAG, "failed to get stock by barcode");
                DiabloUtils.makeToast(getContext(), DiabloError.getError(500));
                DiabloUtils.playSound(getContext(), R.raw.ceres);
            }
        });
    }

    private void init() {
        mBarcodeStocks = new ArrayList<>();
        mStockFixBase = new StockFixRequest.StockFixBase();
        mStockFixBase.setBigType(DiabloEnum.INVALID_INDEX);
        mStockFixBase.setShop(mCurrentShop.getShop());
        mStockFixBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());
        initTitle();
    }

    private void initTitle() {
        ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (null != bar && null != bar.getTitle()) {
            bar.setTitle(
                getResources().getString(R.string.title_stock_fix)
                    + "(" + mCurrentShop.getName() + "-" + mStockFixBase.getShortDatetime() + ")");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_fix, container, false);
        mBarCodeScanView = (EditText)view.findViewById(R.id.fix_barcode);
        mTable = (TableLayout) view.findViewById(R.id.t_stock_fix);
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

    private TableRow addRow(DiabloBarcodeStock stock) {
        mBarcodeStocks.add(stock);
        if (null == stock.getOrderId()) {
            stock.setOrderId(mBarcodeStocks.size());
        }
        TableRow row = new TableRow(this.getContext());
        if ((mBarcodeStocks.size() % 2) == 0) {
            row.setBackgroundResource(R.color.bootstrap_brand_warning);
        } else {
            row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
        }

        TextView cell;
        for (String title: mTitles){
            TableRow.LayoutParams lp = DiabloUtils.createTableRowParams(1f);
            if (getResources().getString(R.string.order_id).equals(title)) {
                lp.weight = 0.5f;
                cell = DiabloUtils.addCell(getContext(), row, stock.getOrderId(), lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.magenta));
            }
            else if (getResources().getString(R.string.barcode).equals(title)){
                lp.weight = 1.6f;
                DiabloUtils.addCell(getContext(), row, stock.getCorrectBarcode(), lp);
            }
            else if (getResources().getString(R.string.style_number_brand).equals(title)){
                lp.weight = 0.8f;
                cell = DiabloUtils.addCell(getContext(), row, stock.getStyleNumber(), lp);
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
            else if (getResources().getString(R.string.color_size).equals(title)) {
                Integer colorId = DiabloEnum.DIABLO_FREE;
                String size = DiabloEnum.DIABLO_FREE_SIZE;
                if (stock.getFree().equals(DiabloEnum.DIABLO_FREE)) {
                    DiabloUtils.addCell(getContext(), row, "均色/均码", lp);
                }
                else {
                    String colorSize = stock.getCorrectBarcode().substring(
                        stock.getBarcode().length(), stock.getCorrectBarcode().length());
                    Integer colorBarcode = DiabloUtils.toInteger(colorSize.substring(0, 3));
                    Integer sizeIndex = DiabloUtils.toInteger(colorSize.substring(3, colorSize.length()));
                    String desc = "";
                    if (colorBarcode.equals(DiabloEnum.DIABLO_FREE)) {
                        desc += "均色";
                    } else {
                        DiabloColor color = DiabloProfile.instance().getColorByBarcode(colorBarcode);
                        if (null != color) {
                            colorId = color.getColorId();
                            desc += color.getName();
                        }
                    }

                    if (sizeIndex.equals(DiabloEnum.DIABLO_FREE)) {
                        desc += "均码";
                    } else {
                        size = DiabloEnum.DIABLO_SIZE_TO_BARCODE[sizeIndex];
                        desc += size;
                    }
                    DiabloUtils.addCell(getContext(), row, desc, lp);
                }
                stock.setColor(colorId);
                stock.setSize(size);
            }
        }

        row.setTag(stock);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<mTable.getChildCount(); i++){
                    View row = mTable.getChildAt(i);
                    if (row instanceof TableRow) {
                        Integer orderId = ((DiabloBarcodeStock)row.getTag()).getOrderId();
                        if ( orderId.equals(((DiabloBarcodeStock)v.getTag()).getOrderId()) ) {
                            v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bootstrap_brand_info));
                        } else {
                            if (orderId % 2 == 0) {
                                row.setBackgroundResource(R.color.bootstrap_brand_warning);
                            } else {
                                row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
                            }
                        }

                    }
                }
            }
        });

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // SaleDetailResponse.SaleDetail d = (SaleDetailResponse.SaleDetail)view.getTag();
                view.showContextMenu();
                return true;
            }
        });
        registerForContextMenu(row);
        mTable.addView(row, 0);
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
                mBarCodeScanView.setText(null);
                mStockFixBase.setTotal(mBarcodeStocks.size());
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
                DiabloUtils.makeToast(getContext(), "操作成功，请等待盘点结果", Toast.LENGTH_LONG);
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
                                        DiabloDBManager.instance().clearFixDraft(mCurrentShop.getShop());
                                        init();
                                        mTable.removeAllViews();
                                    }})
                                .create();

                        }
                        else {
                            mButtons.get(R.id.stock_fix_save).enable();
                            String error = "";
                            if (!DiabloEnum.HTTP_OK.equals(response.code())) {
                                error += "网络故障，HTTP返回码：" + DiabloUtils.toString(response.code());
                            }
                            if (!DiabloEnum.SUCCESS.equals(result.getCode())) {
                                error += "盘点失败：" + DiabloError.getError(result.getCode())
                                    + result.getError();
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
                            StockFixRequest.StockFixBase base = DiabloDBManager.instance().getFixBase(mCurrentShop.getShop());
                            if (null != base) {
                                mStockFixBase = base;
                                mStockFixBase.setEmployee( DiabloProfile.instance().getEmployees().get(0).getNumber());
                                initTitle();
                                List<DiabloBarcodeStock> stocks = DiabloDBManager.instance().listFixDetail(mCurrentShop.getShop());
                                if (0 != stocks.size()) {
                                    mTable.removeAllViews();
                                    mBarcodeStocks.clear();
                                    for (DiabloBarcodeStock stock: stocks) {
                                        addRow(stock);
                                    }
                                    mButtons.get(R.id.stock_fix_save).enable();
                                }
                            } else {
                                DiabloUtils.makeToast(getContext(), "店铺对应的草稿不存在，请确认店铺是否选择正确", Toast.LENGTH_LONG);
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
                            DiabloDBManager.instance().clearFixDraft();
                            mButtons.get(R.id.stock_fix_save).disable();
                            DiabloUtils.makeToast(getContext(), "清除草稿成功", Toast.LENGTH_LONG);
                        }
                    }).create();
                break;
            case R.id.fix_logout:
                ((MainActivity)getActivity()).logout();
                break;
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
        DiabloBarcodeStock stock = (DiabloBarcodeStock)mCurrentRow.getTag();
        Integer orderId = stock.getOrderId();

        if (getResources().getString(R.string.delete) == item.getTitle()){
            mCurrentRow.removeAllViews();
            int child = mTable.getChildCount();
            for (int i=0; i<child; i++){
                View row = mTable.getChildAt(i);
                if (row instanceof TableRow) {
                    if ( ((DiabloBarcodeStock)row.getTag()).getOrderId().equals(orderId) ){
                        mTable.removeView(row);
                        break;
                    }
                }
            }

            // delete from lists
            int index = DiabloEnum.INVALID_INDEX;
            for (int i=0; i<mBarcodeStocks.size(); i++){
                if (mBarcodeStocks.get(i).getOrderId().equals(orderId)){
                    index = i;
                    break;
                }
            }
            boolean replace = index != mBarcodeStocks.size() - 1;
            mBarcodeStocks.remove(index);

            for (int i=0; i<mBarcodeStocks.size(); i++) {
                mBarcodeStocks.get(i).setOrderId(i+1);
            }

            // reorder
            for (int i=0; i<mTable.getChildCount(); i++){
                View row = mTable.getChildAt(i);
                if (row instanceof TableRow) {
                    Integer newOrderId = ((DiabloBarcodeStock)row.getTag()).getOrderId();
                    ((TextView)((TableRow) row).getChildAt(0)).setText(DiabloUtils.toString(newOrderId));
                    if (newOrderId % 2 == 0) {
                        row.setBackgroundResource(R.color.bootstrap_brand_warning);
                    } else {
                        row.setBackgroundResource(R.color.bootstrap_brand_secondary_fill);
                    }
                }
            }

            if (!replace) {
                DiabloDBManager.instance().deleteFixDetail(mCurrentShop.getShop(), orderId);
            } else {
                DiabloDBManager.instance().replaceFixDetail(mCurrentShop.getShop(), mBarcodeStocks);
            }

            if (0 == mBarcodeStocks.size()) {
                mButtons.get(R.id.stock_fix_save).disable();
            }

        }

        return super.onContextItemSelected(item);
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
                f.addRow(stock);
                if (1 == f.mBarcodeStocks.size()) {
                    f.mButtons.get(R.id.stock_fix_save).enable();
                    if ( null == DiabloDBManager.instance().getFixBase(f.mCurrentShop.getShop()) ){
                        DiabloDBManager.instance().addFixBase(f.mCurrentShop.getShop());
                    } else {
                        DiabloDBManager.instance().updateFixBase(f.mCurrentShop.getShop());
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
        super.onHiddenChanged(hidden);
        if (!hidden) {
            DiabloUtils.hiddenKeyboard(getContext(), mBarCodeScanView);
        }
    }
}
