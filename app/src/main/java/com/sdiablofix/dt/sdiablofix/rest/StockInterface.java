package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.entity.DiabloMatchStock;
import com.sdiablofix.dt.sdiablofix.request.GetStockByBarcodeRequest;
import com.sdiablofix.dt.sdiablofix.request.MatchStockRequest;
import com.sdiablofix.dt.sdiablofix.response.GetStockByBarcodeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface StockInterface {
    @POST("match_all_w_inventory")
    Call<List<DiabloMatchStock>> matchAllStock(
        @Header("cookie") String token, @Body MatchStockRequest request);

    @POST("get_stock_by_barcode")
    Call<GetStockByBarcodeResponse> getStockByBarcode(
        @Header("cookie") String token, @Body GetStockByBarcodeRequest request);
}
