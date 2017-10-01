package com.sdiablofix.dt.sdiablofix.activity;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;
import com.sdiablofix.dt.sdiablofix.R;
import com.sdiablofix.dt.sdiablofix.db.DiabloDBManager;
import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;
import com.sdiablofix.dt.sdiablofix.fragment.StockFix;
import com.sdiablofix.dt.sdiablofix.fragment.StockIn;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

public class MainActivity extends AppCompatActivity {

    private NavigationTag mCurrentNavTag;
    private String[] mActivityTitles;
    private static SparseArray<NavigationTag> mNavTagMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setContentView(R.layout.activity_main);
        mActivityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        mNavTagMap.put(0, new NavigationTag(0, DiabloEnum.TAG_STOCK_FIX, R.id.nav_stock_fix));
        mNavTagMap.put(1, new NavigationTag(1, DiabloEnum.TAG_STOCK_IN, R.id.nav_stock_in));
        mNavTagMap.put(2, new NavigationTag(2, DiabloEnum.TAG_SALE_IN, R.id.nav_sale_in));
        mNavTagMap.put(3, new NavigationTag(3, DiabloEnum.TAG_SALE_OUT, R.id.nav_sale_out));

        BottomNavigationBar bar = (BottomNavigationBar) findViewById(R.id.navigation);
        bar.setMode(BottomNavigationBar.MODE_FIXED);
        bar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bar.addItem(createBottomNavigationItem(R.drawable.ic_bubble_chart_black_24dp, R.string.title_stock_fix))
            .addItem(createBottomNavigationItem(R.drawable.ic_dashboard_black_24dp, R.string.title_stock_in))
            .addItem(createBottomNavigationItem(R.drawable.ic_add_shopping_cart_black_24dp, R.string.title_sale_in))
            .addItem(createBottomNavigationItem(R.drawable.ic_remove_shopping_cart_black_24dp, R.string.title_sale_out))
            .setFirstSelectedPosition(0)
            .initialise();

        bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                selectMenuItem(position);
                loadFragment();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        if (savedInstanceState == null) {
            selectMenuItem(0);
            loadFragment();
        }
    }

    private void init() {
        DiabloDBManager.instance().init(this);
        DiabloProfile.instance().setResource(getResources());
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(MainActivity.this)));
    }

    private BottomNavigationItem createBottomNavigationItem(@DrawableRes Integer iconRes, @StringRes Integer titleRes) {
       return new BottomNavigationItem(iconRes, getResources().getString(titleRes))
           .setActiveColorResource(R.color.colorPrimaryDark);
    }

//    private String getTitle(@StringRes Integer titleRes){
//        return getResources().getString(titleRes);
//    }

    private void loadFragment() {
        Fragment currentFragment = getCurrentSelectedFragment();
        switchFragment(currentFragment, mCurrentNavTag.getTag());
    }

    private Fragment getCurrentSelectedFragment() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(mCurrentNavTag.getTag());
        if (f == null) {
            if (mCurrentNavTag.getTitleIndex().equals(0)){
                f = new StockFix();
            }
            else if (mCurrentNavTag.getTitleIndex().equals(1)) {
                f = new StockIn();
            }
            else {
                f = new StockFix();
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

        transaction.commitAllowingStateLoss();
    }

    public void selectMenuItem(Integer menuItemIndex){
        mCurrentNavTag = mNavTagMap.get(menuItemIndex);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(mCurrentNavTag.getTitleName());
        }
    }


    private class NavigationTag {
        private Integer titleIndex;
        private String tag;
        private @IdRes Integer resId;

        private NavigationTag(Integer titleIndex, String tag, @IdRes Integer resId){
            this.titleIndex = titleIndex;
            this.tag = tag;
            this.resId = resId;
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
