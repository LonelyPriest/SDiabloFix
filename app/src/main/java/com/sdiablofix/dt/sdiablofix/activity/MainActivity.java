package com.sdiablofix.dt.sdiablofix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.client.AuthenRightClient;
import com.sdiablofix.dt.sdiablofix.client.BaseSettingClient;
import com.sdiablofix.dt.sdiablofix.client.EmployeeClient;
import com.sdiablofix.dt.sdiablofix.client.FirmClient;
import com.sdiablofix.dt.sdiablofix.client.GoodClient;
import com.sdiablofix.dt.sdiablofix.client.LoginClient;
import com.sdiablofix.dt.sdiablofix.client.StockClient;
import com.sdiablofix.dt.sdiablofix.db.DiabloDBManager;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.fragment.BatchStockFix;
import com.sdiablofix.dt.sdiablofix.fragment.BatchStockOut;
import com.sdiablofix.dt.sdiablofix.request.LogoutRequest;
import com.sdiablofix.dt.sdiablofix.response.Response;
import com.sdiablofix.dt.sdiablofix.rest.BaseSettingInterface;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "MainActivity:";

    private NavigationView mNavigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    private NavigationTag mCurrentNavTag;
    private String[] mActivityTitles;
    private static SparseArray<NavigationTag> mNavTagMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        ViewGroup.LayoutParams params = mNavigationView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 2;
        mNavigationView.setLayoutParams(params);

        mActivityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        mNavTagMap.put(0, new NavigationTag(0, DiabloEnum.TAG_STOCK_FIX));
        mNavTagMap.put(1, new NavigationTag(1, DiabloEnum.TAG_STOCK_OUT));

//        BottomNavigationBar bar = (BottomNavigationBar) findViewById(R.id.navigation);
//        bar.setMode(BottomNavigationBar.MODE_FIXED);
//        bar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
//
//        bar.addItem(createBottomNavigationItem(R.drawable.ic_bubble_chart_black_24dp, R.string.title_stock_fix))
//            .addItem(createBottomNavigationItem(R.drawable.ic_dashboard_black_24dp, R.string.title_stock_in))
//            .addItem(createBottomNavigationItem(R.drawable.ic_add_shopping_cart_black_24dp, R.string.title_sale_in))
//            .addItem(createBottomNavigationItem(R.drawable.ic_remove_shopping_cart_black_24dp, R.string.title_sale_out))
//            // .addItem(createBottomNavigationItem(R.drawable.ic_directions_bike_black_24dp, R.string.title_logout))
//            .setFirstSelectedPosition(0)
//            .initialise();
//
//        bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position) {
//                selectMenuItem(position);
//                loadFragment();
//            }
//
//            @Override
//            public void onTabUnselected(int position) {
//
//            }
//
//            @Override
//            public void onTabReselected(int position) {
//
//            }
//        });
//
        setUpNavigationView();

        if (savedInstanceState == null) {
            selectMenuItem(0);
            loadFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_stock_fix:
                        selectMenuItem(0);
                        break;
                    case R.id.nav_stock_out:
                        selectMenuItem(1);
                        break;
                    case R.id.nav_logout:
                        logout();
                        break;
                    case R.id.nav_clear_draft:
                        DiabloDBManager.instance().clearAllDraft();
                        DiabloUtils.makeToast(getApplicationContext(), "清除草稿成功", Toast.LENGTH_LONG);
                        break;
//                    case R.id.nav_clear_login_user:
//                        DiabloDBManager.instance().clearUser();
//                        break;
//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        selectMenuItem(0);
                        break;
                }

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle =
            new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        // selectNavMenu();

        // set toolbar title
        // setActionBarTitle(mCurrentNavTag.getTitleIndex());

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        Fragment f = getSupportFragmentManager().findFragmentByTag(mCurrentNavTag.getTag());
        if (null != f && f.isVisible()) {
            drawer.closeDrawers();
            return;
        }

        Fragment currentFragment = getCurrentSelectedFragment();
        switchFragment(currentFragment, mCurrentNavTag.getTag());

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }
    private void init() {
        DiabloDBManager.instance().init(this);
        DiabloProfile.instance().setResource(getResources());
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(MainActivity.this)));
    }

    private void loadFragment() {
        Fragment currentFragment = getCurrentSelectedFragment();
        switchFragment(currentFragment, mCurrentNavTag.getTag());
    }

    private Fragment getCurrentSelectedFragment() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(mCurrentNavTag.getTag());
        if (f == null) {
            if (mCurrentNavTag.getTitleIndex().equals(0)){
                f = new BatchStockFix();
            }
            else if (mCurrentNavTag.getTitleIndex().equals(1)) {
                f = new BatchStockOut();
            }
        }
        return f;
    }

    public void switchFragment(Fragment to, String toTag){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()){
            transaction.add(R.id.frame_container, to, toTag);
        } else {
            transaction.show(to);
        }

        // transaction.hide(from);
        Fragment fragment;
        for (int i=0; i<mNavTagMap.size(); i++) {
            NavigationTag navTag = mNavTagMap.get(i);
            if (!navTag.getTag().equals(toTag)) {
                fragment = getSupportFragmentManager().findFragmentByTag(navTag.getTag());
                if (null != fragment) {
                    transaction.hide(fragment);
                }
            }
        }
        // transaction.replace(R.id.frame_container, to);
        transaction.commitAllowingStateLoss();
    }

    public void selectMenuItem(Integer menuItemIndex){
        mCurrentNavTag = mNavTagMap.get(menuItemIndex);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(mCurrentNavTag.getTitleName());
        }
    }

    public void logout() {
        BaseSettingInterface face = BaseSettingClient.getClient().create(BaseSettingInterface.class);
        Call<Response> call = face.logout(
            DiabloProfile.instance().getToken(), new LogoutRequest("destroy_login_user"));

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d(LOG_TAG, "success to destroy session");
                forceLogout();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                forceLogout();
            }
        });
    }

    private void forceLogout() {
        // clear information
        DiabloDBManager.instance().close();
        DiabloProfile.instance().clear();
        // clear client
        clearClient();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void clearClient() {
        BaseSettingClient.resetClient();
        EmployeeClient.resetClient();
        FirmClient.resetClient();

        AuthenRightClient.resetClient();
        StockClient.resetClient();
        GoodClient.resetClient();
        LoginClient.resetClient();
    }

    private class NavigationTag {
        private Integer titleIndex;
        private String tag;

        private NavigationTag(Integer titleIndex, String tag){
            this.titleIndex = titleIndex;
            this.tag = tag;
        }

        private Integer getTitleIndex() {
            return titleIndex;
        }

        private String getTag() {
            return tag;
        }

        private String getTitleName(){
            return mActivityTitles[titleIndex];
        }
    }

    @Override
    public void onBackPressed() {
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
