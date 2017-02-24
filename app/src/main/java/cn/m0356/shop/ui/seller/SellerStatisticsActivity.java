package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerConsultBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2017/1/11.
 */
public class SellerStatisticsActivity extends Activity {

    private MyShopApplication app;
    private TextView tvOrderamount, tvOrdermembernum, tvOrdernum,
            tvOrdergoodsnum,
            tvAvgorderamount, tvAvggoodsprice, tvGcollectnum,
            tvGoodsnum, tvStore_collect, tvHothour;

    public static void start(Context cxt){
        Intent intent = new Intent(cxt, SellerStatisticsActivity.class);
        cxt.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_statistics);
        app = (MyShopApplication) getApplication();
        findView();
        loadData();
    }

    private void findView() {
        tvOrdergoodsnum = (TextView) findViewById(R.id.tv_sta_ordergoodsnum);
        tvOrderamount = (TextView) findViewById(R.id.tv_sta_orderamount);
        tvOrdermembernum = (TextView) findViewById(R.id.tv_sta_ordermembernum);
        tvOrdernum = (TextView) findViewById(R.id.tv_sta_ordernum);
        tvAvgorderamount = (TextView) findViewById(R.id.tv_sta_avgorderamount);
        tvAvggoodsprice = (TextView) findViewById(R.id.tv_sta_avggoodsprice);
        tvGcollectnum = (TextView) findViewById(R.id.tv_sta_gcollectnum);
        tvGoodsnum = (TextView) findViewById(R.id.tv_sta_goodsnum);
        tvStore_collect = (TextView) findViewById(R.id.tv_sta_store_collect);
        tvHothour = (TextView) findViewById(R.id.id_sta_hothour);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadData(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("store_id", app.getStore_id());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_GENERAL, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        JSONObject jo = new JSONObject(data.getJson()).getJSONObject("statnew_arr");
                        initView(jo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    private void initView(JSONObject jo) throws JSONException {
        // 近30天下单金额
        String orderamount = jo.getString("orderamount");
        // 近30天下单会员数
        String ordermembernum = jo.getString("ordermembernum");
        // 近30天下单量
        String ordernum = jo.getString("ordernum");
        // 近30天下单商品数
        String ordergoodsnum = jo.getString("ordergoodsnum");
        // 平均客单价
        String avgorderamount = jo.getString("avgorderamount");
        // 平均价格
        String avggoodsprice = jo.getString("avggoodsprice");
        // 商品收藏量
        String gcollectnum = jo.getString("gcollectnum");
        // 商品总数
        String goodsnum = jo.getString("goodsnum");
        // 店铺收藏量
        String store_collect = jo.getString("store_collect");
        // 下单高峰期
        String hothour = jo.getString("hothour");
        tvOrderamount.setText(orderamount + "元");
        tvOrdermembernum.setText(ordermembernum + "");
        tvOrdernum.setText(ordernum + "");
        tvOrdergoodsnum.setText(ordergoodsnum + "");
        tvAvgorderamount.setText(avgorderamount + "元");
        tvAvggoodsprice.setText(avggoodsprice + "元");
        tvGcollectnum.setText(gcollectnum + "");
        tvGoodsnum.setText(goodsnum + "");
        tvStore_collect.setText(store_collect + "");
        tvHothour.setText(hothour + "");
    }

}
