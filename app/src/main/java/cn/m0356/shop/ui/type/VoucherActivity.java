package cn.m0356.shop.ui.type;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.MainFragmentManager;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.RptVoucherListViewAdapter;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.store.newStoreInFoActivity;

/**
 * Created by jiangtao on 2017/1/18.
 */
public class VoucherActivity extends BaseActivity implements RptVoucherListViewAdapter.OnVoucherRedpacketListener, XListView.IXListViewListener {

    private XListView lv;
    private MyShopApplication app;
    private RptVoucherListViewAdapter mMyAdapter;
    private int curpage = 1;

    public static void start(Context context) {
        Intent intent = new Intent(context, VoucherActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        app = (MyShopApplication) getApplication();

        setCommonHeader("领券中心");
        setListEmpty(-1, "当前没有可用红包及店铺优惠券哦", "");
        lv = (XListView) findViewById(R.id.lv_voucher_list);
        mMyAdapter = new RptVoucherListViewAdapter(this, null);
        mMyAdapter.setOnVoucherRedpacketListener(this);
        lv.setAdapter(mMyAdapter);
        lv.setXListViewListener(this);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData(true);
    }

    private void loadData(final boolean isRefresh) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getLoginKey());
        RemoteDataHandler.asyncPostDataString(Constants.URL_REDPACKET_LIST + curpage, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == 200) {
                    if (data.isHasMore())
                        lv.setPullLoadEnable(true);
                    else
                        lv.setPullLoadEnable(false);
                    parseJson(data.getJson(), isRefresh);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String updataTime = sdf.format(new Date(System.currentTimeMillis()));
                    lv.setRefreshTime(updataTime);
                    lv.stopRefresh();
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                    lv.stopRefresh();
                }
            }
        });

    }


    private void parseJson(String json, boolean isRefresh) {
        try {
            List<VoucherRedpacketBean> data = new ArrayList<VoucherRedpacketBean>();
            JSONArray redpacket_arr = new JSONObject(json).getJSONArray("rptList");
            for (int i = 0; i < redpacket_arr.length(); i++) {
                Gson gson = new Gson();
                VoucherRedpacketBean bean = gson.fromJson(redpacket_arr.getJSONObject(i).toString(), VoucherRedpacketBean.class);
                data.add(bean);
            }
            JSONArray voucher_arr = new JSONObject(json).getJSONArray("voucherList");
            for (int i = 0; i < voucher_arr.length(); i++) {
                Gson gson = new Gson();
                VoucherRedpacketBean bean = gson.fromJson(voucher_arr.getJSONObject(i).toString(), VoucherRedpacketBean.class);
                data.add(bean);
            }
            if (data.size() <= 0) {
                showListEmpty();
                if (lv.getVisibility() != View.GONE)
                    lv.setVisibility(View.GONE);
            } else {
                hideListEmpty();
                if (lv.getVisibility() != View.VISIBLE)
                    lv.setVisibility(View.VISIBLE);
            }
            if (isRefresh) {
                mMyAdapter.update(data);
                curpage = 1;
            } else {
                mMyAdapter.addData(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRptClick(int position) {
        saveRedpacket(position);
    }

    @Override
    public void applyRptClick(int position) {
        Intent intent = new Intent(this, MainFragmentManager.class);
        getApplicationContext().sendBroadcast(new Intent(Constants.SHOW_HOME_URL));
        startActivity(intent);
    }

    @Override
    public void getVoucherClick(int position) {
        saveVoucher(position);
    }

    @Override
    public void applyVoucherClick(int position) {
        Intent intent = new Intent(VoucherActivity.this, newStoreInFoActivity.class);
        intent.putExtra("store_id", ((VoucherRedpacketBean) (mMyAdapter.getItem(position))).voucher_t_store_id);
        startActivity(intent);
    }

    /**
     * 领取免费红包
     *
     * @param position 领取成功数据更新哪一条
     */
    private void saveRedpacket(final int position) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getLoginKey());
        map.put("tid", ((VoucherRedpacketBean) (mMyAdapter.getItem(position))).rpacket_t_id);
        map.put("member_id", app.getMemberID());
        map.put("member_name", app.getUserName());
        RemoteDataHandler.asyncPostDataString(Constants.URL_REDPACKET_SVAE, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == 200) {
                    if (data.getJson().equals("1")) {
                        Toast.makeText(VoucherActivity.this, "领取成功", Toast.LENGTH_SHORT).show();
                        mMyAdapter.getRptSuccessUpdate(position);
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    /**
     * 领取免费代金券
     *
     * @param position 领取成功数据更新哪一条
     */
    private void saveVoucher(final int position) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", app.getLoginKey());
        params.put("tid", ((VoucherRedpacketBean) (mMyAdapter.getItem(position))).voucher_t_id);
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_VOUCHER_FREE_ADD, params, app, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    LogHelper.d("VoucherActivity", "saveVoucher ----- " + position);
                    ShopHelper.showMessage(VoucherActivity.this, "领取成功");
                    mMyAdapter.getVoucherSuccessUpdate(position);
                } else {
                    ShopHelper.showApiError(VoucherActivity.this, json);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        curpage++;
        loadData(false);
    }

}
