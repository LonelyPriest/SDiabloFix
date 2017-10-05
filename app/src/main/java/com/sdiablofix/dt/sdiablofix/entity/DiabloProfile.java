package com.sdiablofix.dt.sdiablofix.entity;

import android.content.res.Resources;
import android.util.Log;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloProfile {
    private static final String LOG_TAG = "Profile:";
    private static final String mSessionId = DiabloEnum.SESSION_ID;
    private static DiabloProfile mProfile;

    private DiabloProfile() {

    }

    synchronized public static DiabloProfile instance() {
        if ( null == mProfile){
            mProfile = new DiabloProfile();
        }
        return mProfile;
    }
    private String mToken;
    // server
    private String mServer;
    // resource handler
    private Resources mResource;

    public void setToken(String token){
        mToken = DiabloProfile.mSessionId + "=" + token;
    }

    public String getToken(){
        return this.mToken;
    }

    public void setServer(final String server) {
        this.mServer = server;
    }
    public final String getServer(){
        return mServer;
    }

    public void setResource (Resources resource) {
        this.mResource = resource;
    }

    public Resources getResource() {
        return mResource;
    }

    // login information
    private Integer mLoginShop = DiabloEnum.INVALID_INDEX;
    // private Integer mLoginFirm = DiabloEnum.INVALID_INDEX;
    private Integer mLoginEmployee = DiabloEnum.INVALID_INDEX;
    private Integer mLoginRetailer = DiabloEnum.INVALID_INDEX;
    private Integer mLoginType = DiabloEnum.INVALID_INDEX;
    private List<DiabloRight> mLoginRights;
    private List<DiabloShop> mLoginShops;


    private List<Integer> mAvailableShopIds = new ArrayList<>();
    private List<Integer> mShopIds = new ArrayList<>();
    private List<Integer> mBadRepoIds = new ArrayList<>();
    private List<Integer> mRepoIds = new ArrayList<>();

    private List<DiabloShop> mSortAvailableShop = new ArrayList<>();
    private List<DiabloShop> mSortShop = new ArrayList<>();
    private List<DiabloShop> mSortBadRepo = new ArrayList<>();
    private List<DiabloShop> mSortRepo = new ArrayList<>();

    // employee
    private List<DiabloEmployee> mEmployees = new ArrayList<>();

    // base settings
    private List<DiabloBaseSetting> mBaseSettings;

    // color
    private List<DiabloColor> mColors = new ArrayList<>();

    // size group
    private List<DiabloSizeGroup> mSizeGroups = new ArrayList<>();

    // matched stocks
    private List<DiabloMatchStock> mMatchStocks;

    // brand
    private List<DiabloBrand> mBrands = new ArrayList<>();

    // type
    private List<DiabloType> mDiabloTypes = new ArrayList<>();

    // firm
    private List<DiabloFirm> mFirms = new ArrayList<>();

    // class kind
    private List<DiabloColorKind> mColorKinds;

    // years
    private String [] mDiabloYears;

    // sale type
    private String [] mSaleTypes;

    // stock type
    private String [] mStockTypes;

    public void clear(){
        Log.d(LOG_TAG, "clear called");
        mLoginShop = DiabloEnum.INVALID_INDEX;
        // mLoginFirm = DiabloEnum.INVALID_INDEX;
        mLoginEmployee = DiabloEnum.INVALID_INDEX;
        mLoginRetailer = DiabloEnum.INVALID_INDEX;
        mLoginType = DiabloEnum.INVALID_INDEX;
        mLoginRights = null;
        mLoginShops = null;

        mAvailableShopIds.clear();
        mShopIds.clear();
        mBadRepoIds.clear();
        mRepoIds.clear();

        mSortAvailableShop.clear();
        mSortShop.clear();
        mSortBadRepo.clear();
        mSortRepo.clear();

        // employee
        mEmployees.clear();

        // base settings
        mBaseSettings.clear();

        // color
        mColors.clear();

        // size group
        mSizeGroups.clear();

        // matched stocks
        if (null != mMatchStocks) {
            mMatchStocks.clear();
        }

        // brands
        if (null != mBrands) {
            mBrands.clear();
        }

        // types
        if (null != mDiabloTypes) {
            mDiabloTypes.clear();
        }

        // firms
        clearFirms();

        // color kind
        if (null != mColorKinds) {
            mColorKinds.clear();
        }
    }

    /*
    * Login user info
    * */
    public Integer getLoginShop() {
        if ( mLoginShop.equals(DiabloEnum.INVALID_INDEX) ){
            mLoginShop = DiabloProfile.instance().getAvailableShopIds().get(0);
        }
        return mLoginShop;
    }

    public void setLoginShop(Integer loginShop) {
        this.mLoginShop = loginShop;
    }

//    public Integer getLoginFirm() {
//        return this.mLoginFirm;
//    }
//
//    public void setLoginFirm(Integer loginFirm) {
//        this.mLoginFirm = loginFirm;
//    }

    public Integer getLoginEmployee() {
        return this.mLoginEmployee;
    }

    public void setLoginEmployee(Integer loginEmployee) {
        this.mLoginEmployee = loginEmployee;
    }

    public Integer getLoginRetailer() {
        return this.mLoginRetailer;
    }

    public void setLoginRetailer(Integer loginRetailer) {
        this.mLoginRetailer = loginRetailer;
    }

    public Integer getLoginType() {
        return this.mLoginType;
    }

    public void setLoginType(Integer loginType) {
        this.mLoginType = loginType;
    }

    public List<DiabloRight> getLoginRights(){
        return mLoginRights;
    }

    public void setLoginRights(List<DiabloRight> loginRights) {
        this.mLoginRights = loginRights;
    }

    public void setLoginShops(List<DiabloShop> loginShops) {
        this.mLoginShops = loginShops;
    }

    public List<Integer> getAvailableShopIds() {
        return mAvailableShopIds;
    }

    public List<Integer> getShopIds() {
        return mShopIds;
    }

    public List<Integer> getBadRepoIds() {
        return mBadRepoIds;
    }

    public List<Integer> getRepoIds() {
        return mRepoIds;
    }

    public List<DiabloShop> getSortShop() {
        return mSortShop;
    }

    public List<DiabloShop> getSortRepo() {
        return mSortRepo;
    }

    public List<DiabloShop> getSortBadRepo() {
        return mSortBadRepo;
    }

    public List<DiabloShop> getSortAvailableShop() {
        return mSortAvailableShop;
    }

    public void initLoginUser(){
        setAllAvailableShop();
        setAllShop();
        setAllRepo();
        setAllBadRepo();
    }

    // shop without any repo bind and repo only
    private void setAllAvailableShop(){
        for (DiabloShop shop: this.mLoginShops){
            if ( ((shop.getType().equals(DiabloEnum.SHOP_ONLY)
                && shop.getRepo().equals(DiabloEnum.BIND_NONE))
                || shop.getType().equals(DiabloEnum.REPO_ONLY)) ){
                if (!this.mAvailableShopIds.contains(shop.getShop())){
                    if (shop.getShop().equals(this.mLoginShop)){
                        this.mAvailableShopIds.add(0, shop.getShop());
                    } else {
                        this.mAvailableShopIds.add(shop.getShop());
                    }
                }

                if (!this.mSortAvailableShop.contains(shop)) {
                    if (shop.getShop().equals(this.mLoginShop)){
                        this.mSortAvailableShop.add(0, shop);
                    } else {
                        this.mSortAvailableShop.add(shop);
                    }
                }
            }
        }
    }

    // shop or shop that bind to repo
    private void setAllShop(){
        for (DiabloShop shop: this.mLoginShops){
            if ( shop.getType().equals(DiabloEnum.SHOP_ONLY) ){
                if (!this.mShopIds.contains(shop.getShop())){
                    if (shop.getShop().equals(this.mLoginShop)){
                        this.mShopIds.add(0, shop.getShop());
                    } else {
                        this.mShopIds.add(shop.getShop());
                    }
                }

                if (!this.mSortShop.contains(shop)){
                    if (shop.getShop().equals(this.mLoginShop)){
                        this.mSortShop.add(0, shop);
                    } else {
                        this.mSortShop.add(shop);
                    }
                }
            }
        }
    }

    // repo only
    private void setAllRepo(){
        for (DiabloShop shop: this.mLoginShops){
            if ( shop.getType().equals(DiabloEnum.REPO_ONLY ) ){
                if (!this.mRepoIds.contains(shop.getShop())){
                    this.mRepoIds.add(shop.getShop());
                }

                if (!this.mSortRepo.contains(shop)){
                    this.mSortRepo.add(shop);
                }
            }
        }
    }

    // bad repo only
    private void setAllBadRepo(){
        for (DiabloShop shop: this.mLoginShops){
            if ( shop.getType().equals(DiabloEnum.REPO_BAD) ){
                if (!this.mBadRepoIds.contains(shop.getShop())){
                    this.mBadRepoIds.add(shop.getShop());
                }
                if (!this.mSortBadRepo.contains(shop)){
                    this.mSortBadRepo.add(shop);
                }
            }
        }
    }

    /*
    * Employee
    * */
    public void setEmployees(List<DiabloEmployee> employees) {
        for(DiabloEmployee e: employees){
            if (e.getId().equals(mLoginEmployee)){
                mEmployees.add(0, e);
            } else {
                mEmployees.add(e);
            }
        }
    }

    public List<DiabloEmployee> getEmployees(){
        return this.mEmployees;
    }

    public Integer getIndexOfEmployee(Integer employeeId){
        Integer index = DiabloEnum.INVALID_INDEX;
        for (Integer i=0; i<mEmployees.size(); i++){
            if (mEmployees.get(i).getId().equals(employeeId)){
                index = i;
                break;
            }
        }

        return index;
    }

    /*
    * Base setting
    * */

//    public List<BaseSetting> getBaseSettings() {
//        return this.mBaseSettings;
//    }

    public void setBaseSettings(List<DiabloBaseSetting> baseSettings) {
        this.mBaseSettings = baseSettings;
    }

    public String getConfig(String name, String defaultValue){
        return this.getConfig(DiabloEnum.INVALID_INDEX, name, defaultValue);
    }

    public String getConfig(Integer shop, String name, String defaultValue){
        String find = defaultValue;
        for (DiabloBaseSetting base: mBaseSettings){
            if (base.getShop().equals(shop) && base.getEName().equals(name)){
                find = base.getValue();
            }
        }

        if (find.equals(defaultValue)){
            for (DiabloBaseSetting base: mBaseSettings){
                if (base.getShop().equals(DiabloEnum.INVALID_INDEX) && base.getEName().equals(name)){
                    find = base.getValue();
                }
            }
        }

        return find;
    }

    /**
     * color
     */
    public void setColors(List<DiabloColor> colors) {
        for(DiabloColor c: colors){
            mColors.add(c);
        }

        // add free color
        // mColors.add(new DiabloColor());

    }

    public void addColor(DiabloColor color) {
        if (null != getColor(color.getColorId())) {
            mColors.add(0, color);
        }
    }

    public final List<DiabloColor> getColors() {
        return mColors;
    }

    public final String getColorName(Integer colorId){
        String name = DiabloEnum.EMPTY_STRING;

        for (int i = 0; i < mColors.size(); i++) {
            if (mColors.get(i).getColorId().equals(colorId)){
                name = mColors.get(i).getName();
                break;
            }
        }
        return name;
    }

    public final DiabloColor getColor(Integer colorId) {
        DiabloColor found = null;
        for (int i = 0; i < mColors.size(); i++) {
            if (mColors.get(i).getColorId().equals(colorId)){
                found = mColors.get(i);
                break;
            }
        }

        return null == found ? new DiabloColor() : found;
    }

    public final DiabloColor getColorByBarcode(Integer barcode) {
        DiabloColor found = null;
        for (int i = 0; i < mColors.size(); i++) {
            if (mColors.get(i).getColorBarcode().equals(barcode)){
                found = mColors.get(i);
                break;
            }
        }

        return null == found ? new DiabloColor() : found;
    }


    /**
     * Size Group
     */
    public void setSizeGroups(List<DiabloSizeGroup> sizeGroups){
        for (DiabloSizeGroup s: sizeGroups){
            mSizeGroups.add(s);
        }

        // add free group
        DiabloSizeGroup freeSizeGroup = new DiabloSizeGroup();
        freeSizeGroup.initWithFreeSizeGroup();
        mSizeGroups.add(freeSizeGroup);

        for (DiabloSizeGroup s: mSizeGroups){
            s.genSortedSizeNames();
        }
    }

    public final List<DiabloSizeGroup> getSizeGroups() {
        return mSizeGroups;
    }

    public final DiabloSizeGroup getSizeGroup(Integer groupId) {
        DiabloSizeGroup find = null;
        for (DiabloSizeGroup group: mSizeGroups) {
            if (group.getGroupId().equals(groupId)) {
                find = group;
            }
        }

        return find;
    }

    public final ArrayList<String> genSortedSizeNamesByGroups(String sizeGroups){
        ArrayList<String> sizes = new ArrayList<>();
        String [] groups = sizeGroups.split(DiabloEnum.SIZE_SEPARATOR);
        for (String gId : groups) {
            for (DiabloSizeGroup s: mSizeGroups){
                if (DiabloUtils.toInteger(gId).equals(s.getGroupId())){
                    for (String name: s.getSortedSizeNames()){
                        if (!sizes.contains(name)){
                            sizes.add(name);
                        }
                    }
                }
            }
        }

        return sizes;
    }

    /**
     * match stock
     */
    public List<DiabloMatchStock> getMatchStocks() {
        return mMatchStocks;
    }

    public void setMatchStocks(List<DiabloMatchStock> mMatchStocks) {
        this.mMatchStocks = new ArrayList<>(mMatchStocks);
    }

    public DiabloMatchStock getMatchStock(String styleNumber, Integer brandId){
        DiabloMatchStock stock = null;
        for (DiabloMatchStock m: mMatchStocks){
            if (styleNumber.equals(m.getStyleNumber())
                && brandId.equals(m.getBrandId())){
                stock = m;
                break;
            }
        }

        return stock;
    }

    public void addMatchStock(DiabloMatchStock stock) {
        if ( null == getMatchStock(stock.getStyleNumber(), stock.getBrandId()) ) {
            mMatchStocks.add(0, stock);
        }
    }

    public DiabloMatchStock removeMatchStock(String styleNumber, Integer brandId) {
        DiabloMatchStock stock = getMatchStock(styleNumber, brandId);
        if (null != stock) {
            mMatchStocks.remove(stock);
        }

        return stock;
    }

    /**
     * brands
     */
    public void  setBrands(List<DiabloBrand> brands) {
        for (DiabloBrand brand: brands) {
            String py = DiabloUtils.toPinYinWithFirstCharacter(brand.getName());
            brand.setPy(py);
            mBrands.add(brand);
        }
        // this.mBrands = new ArrayList<>(brands);
    }

    public void addBrand(DiabloBrand brand) {
        if (null == getBrand(brand.getId())) {
            String py = DiabloUtils.toPinYinWithFirstCharacter(brand.getName());
            brand.setPy(py);
            this.mBrands.add(0, brand);
        }
    }

    public List<DiabloBrand> getBrands() {
        return mBrands;
    }

    public DiabloBrand getBrand(Integer brandId) {
        DiabloBrand brand = null;
        for (DiabloBrand b: mBrands) {
            if (b.getId().equals(brandId)) {
                brand = b;
                break;
            }
        }

        return brand;
    }

    public DiabloBrand getBrand(String name) {
        DiabloBrand brand = null;
        for (DiabloBrand b: mBrands) {
            if (b.getName().equals(name)) {
                brand = b;
                break;
            }
        }

        return brand;
    }

    /**
     * types
     */
    public void setDiabloTypes(List<DiabloType> types) {
        for (DiabloType t: types) {
            String py = DiabloUtils.toPinYinWithFirstCharacter(t.getName());
            t.setPy(py);
            mDiabloTypes.add(t);
        }
        // this.mDiabloTypes = new ArrayList<>(types);
    }

    public void addDiabloType(DiabloType type) {
        if (null == getDiabloType(type.getId())) {
            String py = DiabloUtils.toPinYinWithFirstCharacter(type.getName());
            type.setPy(py);
            this.mDiabloTypes.add(0, type);
        }
    }

    public List<DiabloType> getDiabloTypes() {
        return mDiabloTypes;
    }

    public DiabloType getDiabloType(Integer typeId) {
        DiabloType type = null;
        for (DiabloType t: mDiabloTypes) {
            if (t.getId().equals(typeId)) {
                type = t;
                break;
            }
        }

        return type;
    }

    public DiabloType getDiabloType(String name) {
        DiabloType type = null;
        for (DiabloType t: mDiabloTypes) {
            if (t.getName().equals(name)) {
                type = t;
                break;
            }
        }

        return type;
    }

    /**
     * firms
     */
    public void setFirms(List<DiabloFirm> firms) {
        for (DiabloFirm f: firms) {
            String py = DiabloUtils.toPinYinWithFirstCharacter(f.getName());
            f.setPy(py);
            mFirms.add(f);
        }
        // this.mFirms = new ArrayList<>(firms);
    }

    public void clearFirms() {
        if (null != mFirms) {
            mFirms.clear();
        }
    }

    public void addFirm(DiabloFirm firm) {
        if (null == getFirm(firm.getId())){
            String py = DiabloUtils.toPinYinWithFirstCharacter(firm.getName());
            firm.setPy(py);
            this.mFirms.add(0, firm);
        }
    }

    public List<DiabloFirm> getFirms() {
        return mFirms;
    }

    public DiabloFirm getFirm(Integer firmId) {
        DiabloFirm firm = null;
        for (DiabloFirm f: mFirms) {
            if (f.getId().equals(firmId)) {
                firm = f;
                break;
            }
        }

        return firm;
    }

    public DiabloFirm getFirm(String name) {
        DiabloFirm firm = null;
        for (DiabloFirm f: mFirms) {
            if (f.getName().equals(name)) {
                firm = f;
                break;
            }
        }

        return firm;
    }

    /**
     * color kind
     */

    public final List<DiabloColorKind> getColorKinds() {
        return mColorKinds;
    }

    public final DiabloColorKind getColorKind(Integer kindId) {
        DiabloColorKind kind = null;
        for (DiabloColorKind k: mColorKinds) {
            if (k.getId().equals(kindId)) {
                kind = k;
            }
        }

        return kind;
    }

    public void setColorKinds(List<DiabloColorKind> colorKinds) {
        this.mColorKinds = new ArrayList<>(colorKinds);
    }

}
