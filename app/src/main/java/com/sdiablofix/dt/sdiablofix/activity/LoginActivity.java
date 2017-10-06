package com.sdiablofix.dt.sdiablofix.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.adapter.OnAdjustDropDownViewListener;
import com.sdiablofix.dt.sdiablofix.adapter.StringArrayAdapter;
import com.sdiablofix.dt.sdiablofix.client.AuthenRightClient;
import com.sdiablofix.dt.sdiablofix.client.BaseSettingClient;
import com.sdiablofix.dt.sdiablofix.client.EmployeeClient;
import com.sdiablofix.dt.sdiablofix.client.FirmClient;
import com.sdiablofix.dt.sdiablofix.client.GoodClient;
import com.sdiablofix.dt.sdiablofix.client.LoginClient;
import com.sdiablofix.dt.sdiablofix.db.DiabloDBManager;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBaseSetting;
import com.sdiablofix.dt.sdiablofix.entity.DiabloBrand;
import com.sdiablofix.dt.sdiablofix.entity.DiabloColor;
import com.sdiablofix.dt.sdiablofix.entity.DiabloEmployee;
import com.sdiablofix.dt.sdiablofix.entity.DiabloFirm;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.entity.DiabloSizeGroup;
import com.sdiablofix.dt.sdiablofix.entity.DiabloType;
import com.sdiablofix.dt.sdiablofix.entity.DiabloUser;
import com.sdiablofix.dt.sdiablofix.response.LoginResponse;
import com.sdiablofix.dt.sdiablofix.response.LoginUserInfoResponse;
import com.sdiablofix.dt.sdiablofix.rest.AuthenRightInterface;
import com.sdiablofix.dt.sdiablofix.rest.BaseSettingInterface;
import com.sdiablofix.dt.sdiablofix.rest.EmployeeInterface;
import com.sdiablofix.dt.sdiablofix.rest.FirmInterface;
import com.sdiablofix.dt.sdiablofix.rest.GoodInterface;
import com.sdiablofix.dt.sdiablofix.rest.LoginInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloError;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final static String LOG_TAG = "LOGIN:";

    BootstrapButton mBtnLogin;
    TextInputLayout mLoginWrap;
    TextInputLayout mPasswordWrap;
    Spinner mViewServer;
    Context mContext;

    String mName;
    String mPassword;
    String [] mServers;

    // private ProgressDialog mLoadingDialog;
    private Dialog mLoadingDialog;

    private LoginInterface mFace;

    private final LoginHandler mLoginHandler = new LoginHandler(this);

    private LoginListener createLoginListener(final DiabloUser user) {
        return new LoginListener() {
            @Override
            public void onLogin() {
                Call<LoginResponse> call = mFace.login(mName, mPassword, DiabloEnum.TABLET, DiabloEnum.DIABLO_TRUE);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        LoginResponse body = response.body();
                        Integer code = body.getCode();
                        switch (code) {
                            case 0:
                                startLogin(user, body.getToken());
                                break;
                            default:
                                loginError(code);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        mBtnLogin.setClickable(true);
                        loginError(9009);
                    }
                });
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init db
        DiabloDBManager.instance().init(this);

        mContext = this;

        DiabloProfile.instance().setServer(getString(R.string.diablo_production_server));
        DiabloProfile.instance().setResource(getResources());
        // mFace = WLoginClient.getClient().create(WLoginInterface.class);
        // DiabloProfile.instance().setContext(this.getApplicationContext());

        mLoginWrap = (TextInputLayout) findViewById(R.id.login_name_holder);
        mPasswordWrap = (TextInputLayout) findViewById(R.id.login_password_holder);
        mViewServer = (Spinner)findViewById(R.id.spinner_select_server);

        // default production server
        mServers = getResources().getStringArray(R.array.servers);
        StringArrayAdapter adapter = new StringArrayAdapter(
            mContext,
            R.layout.diablo_spinner_item,
            mServers);

        adapter.setDropDownViewListener(new OnAdjustDropDownViewListener() {
            @Override
            public void setDropDownVerticalOffset() {
                mViewServer.setDropDownVerticalOffset(mViewServer.getHeight());
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewServer.setAdapter(adapter);

        mViewServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    DiabloProfile.instance().setServer(getString(R.string.diablo_production_server));
                }
                else if (1 == position) {
                    DiabloProfile.instance().setServer(getString(R.string.diablo_test_server));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DiabloUser user = DiabloDBManager.instance().getFirstLoginUser();
        if (null != user) {
            if (null != mLoginWrap.getEditText())
                mLoginWrap.getEditText().setText(user.getName());
            if (null != mPasswordWrap.getEditText())
                mPasswordWrap.getEditText().setText(user.getPassword());
        }

        // InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //
        mBtnLogin = (BootstrapButton) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLoginWrap.getEditText()) {
                    mName = mLoginWrap.getEditText().getText().toString();
                }

                if (null != mPasswordWrap.getEditText()) {
                    mPassword = mPasswordWrap.getEditText().getText().toString();
                }

                if (mName.trim().equals("")) {
                    if (null != mLoginWrap.getEditText()) {
                        mLoginWrap.getEditText().setError(getResources().getString(R.string.login_name));
                    }
                }
                else if (mPassword.trim().equals("")) {
                    if (null != mPasswordWrap.getEditText()) {
                        mPasswordWrap.getEditText().setError(getResources().getString(R.string.login_password));
                    }
                }
                else {
                    // login
                    mBtnLogin.setClickable(false);
                    LoginClient.resetClient();
                    mFace = LoginClient.getClient().create(LoginInterface.class);
                    Call<LoginResponse> call = mFace.login(mName, mPassword, DiabloEnum.TABLET, DiabloEnum.DIABLO_FALSE);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            LoginResponse body = response.body();
                            Integer code = body.getCode();
                            switch (code){
                                case 0:
                                    startLogin(user, body.getToken());
                                    break;
                                case 1105:
                                    loginError(1105, createLoginListener(user));
                                    break;
                                case 1106:
                                    loginError(1106, createLoginListener(user));
                                    break;
                                default:
                                    loginError(code);
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            mBtnLogin.setClickable(true);
                            loginError(9009);
                        }
                    });
                }
            }
        });

        BootstrapButton btnClear = (BootstrapButton) findViewById(R.id.btn_clear_login);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLoginWrap.getEditText())
                    mLoginWrap.getEditText().setText("");
                if (null != mPasswordWrap.getEditText())
                    mPasswordWrap.getEditText().setText("");

                DiabloDBManager.instance().clearUser();
            }
        });
    }

    private void startLogin(DiabloUser user, String token) {
       DiabloProfile.instance().setToken(token);
        if (null == user) {
            DiabloDBManager.instance().addUser(mName, mPassword);
        }
        else {
            if(!user.getName().equals(mName)){
                DiabloDBManager.instance().addUser(mName, mPassword);
            } else {
                if (!user.getPassword().equals(mPassword)) {
                    DiabloDBManager.instance().updateUser(mName, mPassword);
                }
            }
        }

        mLoadingDialog = DiabloUtils.createLoadingDialog(mContext);
        mLoadingDialog.show();
        getLoginUserInfo();
    }

    public void gotoMain(){
        DiabloDBManager.instance().close();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        mLoadingDialog.dismiss();
    }

    public void loginError(Integer code) {
        loginError(code, null);
    }

    public void loginError(Integer code, final LoginListener listener){
        if (null != mLoadingDialog){
            mLoadingDialog.dismiss();
        }

        mBtnLogin.setClickable(true);

        String error = DiabloError.getError(code);
        new MaterialDialog.Builder(mContext)
            .title(R.string.user_login)
            .content(error)
            // .contentColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
            .positiveText(getString(R.string.login_ok))
            .positiveColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (null != listener) {
                        listener.onLogin();
                    }
                }
            })
            .negativeText(getString(R.string.login_cancel))
            .negativeColor(ContextCompat.getColor(mContext, R.color.colorGray))
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            })
            .show();
    }

    @Override
    public void onBackPressed() {

    }

    private void getLoginUserInfo(){
        // RightClient.resetClient();
        AuthenRightInterface rightInterface = AuthenRightClient.getClient().create(AuthenRightInterface.class);
        Call<LoginUserInfoResponse> rightCall = rightInterface.getLoginUserInfo(DiabloProfile.instance().getToken());

        rightCall.enqueue(new Callback<LoginUserInfoResponse>() {
            @Override
            public void onResponse(Call<LoginUserInfoResponse> call, Response<LoginUserInfoResponse> response) {
                Log.d(LOG_TAG, "success to get login information");
                LoginUserInfoResponse user = response.body();
                DiabloProfile.instance().setLoginEmployee(user.getLoginEmployee());
                DiabloProfile.instance().setLoginType(user.getLoginType());
                DiabloProfile.instance().setLoginShop(user.getLoginShop());
                DiabloProfile.instance().setLoginShops(user.getShops());
                DiabloProfile.instance().setLoginRights(user.getRights());
                DiabloProfile.instance().initLoginUser();

                Message message = Message.obtain(mLoginHandler);
                message.what = 10;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<LoginUserInfoResponse> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 11;
                message.sendToTarget();
            }
        });
    }

    private void getEmployee(){
        // EmployeeClient.resetClient();
        EmployeeInterface face = EmployeeClient.getClient().create(EmployeeInterface.class);
        Call<List<DiabloEmployee>> call = face.listEmployee(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloEmployee>>() {
            @Override
            public void onResponse(Call<List<DiabloEmployee>> call, Response<List<DiabloEmployee>> response) {
                Log.d(LOG_TAG, "success to get employee");
                DiabloProfile.instance().setEmployees(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 20;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloEmployee>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 21;
                message.sendToTarget();
            }
        });
    }

    private void getBaseSetting(){
        BaseSettingInterface face = BaseSettingClient.getClient().create(BaseSettingInterface.class);
        Call<List<DiabloBaseSetting>> call = face.listBaseSetting(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloBaseSetting>>() {
            @Override
            public void onResponse(Call<List<DiabloBaseSetting>> call, Response<List<DiabloBaseSetting>> response) {
                Log.d(LOG_TAG, "success to get base setting");
                DiabloProfile.instance().setBaseSettings(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 40;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloBaseSetting>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 41;
                message.sendToTarget();
            }
        });
    }

    private void getColor(){
        // WGoodClient.resetClient();
        GoodInterface face = GoodClient.getClient().create(GoodInterface.class);
        Call<List<DiabloColor>> call = face.listColor(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloColor>>() {
            @Override
            public void onResponse(Call<List<DiabloColor>> call, Response<List<DiabloColor>> response) {
                Log.d(LOG_TAG, "success to get color");
                DiabloProfile.instance().setColors(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 50;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloColor>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 51;
                message.sendToTarget();
            }
        });
    }

    private void getSizeGroup(){
        // WGoodClient.resetClient();
        GoodInterface face = GoodClient.getClient().create(GoodInterface.class);
        Call<List<DiabloSizeGroup>> call = face.listSizeGroup(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloSizeGroup>>() {
            @Override
            public void onResponse(Call<List<DiabloSizeGroup>> call, Response<List<DiabloSizeGroup>> response) {
                Log.d(LOG_TAG, "success to get size group");
                DiabloProfile.instance().setSizeGroups(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 60;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloSizeGroup>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 61;
                message.sendToTarget();
            }
        });
    }

//    private void getAllMatchStock(){
//        // StockClient.resetClient();
//        StockInterface face = StockClient.getClient().create(StockInterface.class);
//        Integer loginShop = DiabloProfile.instance().getLoginShop();
//        Call<List<DiabloMatchStock>> call = face.matchAllStock(
//            DiabloProfile.instance().getToken(),
//            new MatchStockRequest(loginShop, DiabloEnum.USE_REPO));
//        call.enqueue(new Callback<List<DiabloMatchStock>>() {
//            @Override
//            public void onResponse(Call<List<DiabloMatchStock>> call, Response<List<DiabloMatchStock>> response) {
//                Log.d(LOG_TAG, "success to get match stock");
//                DiabloProfile.instance().setMatchStocks(response.body());
//                Message message = Message.obtain(mLoginHandler);
//                message.what = 70;
//                message.sendToTarget();
//            }
//
//            @Override
//            public void onFailure(Call<List<DiabloMatchStock>> call, Throwable t) {
//                Log.d(LOG_TAG, "failed to get match stock");
//                Message message = Message.obtain(mLoginHandler);
//                message.what = 71;
//                message.sendToTarget();
//            }
//        });
//    }

    private void getBrand(){
        // WGoodClient.resetClient();
        GoodInterface face = GoodClient.getClient().create(GoodInterface.class);
        Call<List<DiabloBrand>> call = face.listBrand(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloBrand>>() {
            @Override
            public void onResponse(Call<List<DiabloBrand>> call, Response<List<DiabloBrand>> response) {
                Log.d(LOG_TAG, "success to get brand");
                DiabloProfile.instance().setBrands(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 80;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloBrand>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 81;
                message.sendToTarget();
            }
        });
    }

    private void getType(){
        // WGoodClient.resetClient();
        GoodInterface face = GoodClient.getClient().create(GoodInterface.class);
        Call<List<DiabloType>> call = face.listType(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloType>>() {
            @Override
            public void onResponse(Call<List<DiabloType>> call, Response<List<DiabloType>> response) {
                Log.d(LOG_TAG, "success to get type");
                DiabloProfile.instance().setDiabloTypes(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 90;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloType>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 91;
                message.sendToTarget();
            }
        });
    }

    private void getFirm() {
        // FirmClient.resetClient();
        FirmInterface face = FirmClient.getClient().create(FirmInterface.class);
        Call<List<DiabloFirm>> call = face.listFirm(DiabloProfile.instance().getToken());
        call.enqueue(new Callback<List<DiabloFirm>>() {
            @Override
            public void onResponse(Call<List<DiabloFirm>> call, Response<List<DiabloFirm>> response) {
                Log.d(LOG_TAG, "success to get firm");
                DiabloProfile.instance().setFirms(response.body());
                Message message = Message.obtain(mLoginHandler);
                message.what = 100;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<List<DiabloFirm>> call, Throwable t) {
                Message message = Message.obtain(mLoginHandler);
                message.what = 101;
                message.sendToTarget();
            }
        });
    }

    private static class LoginHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        private LoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                switch (msg.what){
                    case 10: // get employee
                        activity.getEmployee();
                        break;
                    case 20: // get base setting
                        activity.getBaseSetting();
                        break;
                    case 40: // color
                        activity.getColor();
                        break;
                    case 50: // size group
                        activity.getSizeGroup();
                        break;
//                    case 60: // all stocks
//                        activity.getAllMatchStock();
//                        break;
                    case 60: // brand
                        activity.getBrand();
                        break;
                    case 80: // stock type
                        activity.getType();
                        break;
                    case 90: // firm
                        activity.getFirm();
                        break;
                    case 100: //
                        activity.gotoMain();
                        break;

                    case 11: // failed to get user data
                        activity.loginError(211);
                        break;
                    case 21: // failed to get employee
                        activity.loginError(200);
                        break;
                    case 41: // failed to get base setting
                        activity.loginError(201);
                        break;
                    case 51: // failed to get color
                        activity.loginError(203);
                    case 61: // failed to get size group
                        activity.loginError(204);
                        break;
//                    case 71: // failed to get match stock
//                        activity.loginError(205);
//                        break;
                    case 81: // failed to get brand
                        activity.loginError(206);
                        break;
                    case 91: // failed to get type
                        activity.loginError(207);
                        break;
                    case 101: // failed to get firm
                        activity.loginError(208);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // DiabloDBManager.instance().close();
    }

    private interface LoginListener {
        void onLogin();
    }
}