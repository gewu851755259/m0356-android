package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2017/1/9.
 */
public class CountAnalysisActivity extends Activity {

    private MyShopApplication app;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, CountAnalysisActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyShopApplication) getApplication();
        loadGoodsFlow();

    }

    private void loadGoodsFlow(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("stattype", "year");
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_FLOW, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

}
