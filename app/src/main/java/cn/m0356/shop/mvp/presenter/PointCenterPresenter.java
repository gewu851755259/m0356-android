package cn.m0356.shop.mvp.presenter;


import android.content.Context;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.m0356.shop.bean.Mine;
import cn.m0356.shop.bean.ProdgoodsBean;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.mvp.view.IPointCenterView;

/**
 * Created by yml on 2017/3/31.
 */
public class PointCenterPresenter extends BasePresenter<IPointCenterView> {

    private MyShopApplication myApplication;

    public PointCenterPresenter(Context context) {
        myApplication = (MyShopApplication) context.getApplicationContext();
    }

    /**
     * 初始化加载我的信息
     */
    public void loadMemberInfo() {
        String url = Constants.URL_MYSTOIRE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {

                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("member_info");
                        Mine bean = Mine.newInstanceList(objJson);
                        if (bean != null) {
                            getMvpView().setMemberInfo(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(myApplication, json);
                }
            }
        });
    }

    /**
     * 读取积分
     */
    public void loadPoint() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_MY_ASSET + "&fields=point", params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        getMvpView().setPointValue("积分:" + obj.optString("point"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 读取经验
     */
    public void loadExp() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_MY_EXP, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONObject expObj = obj.getJSONObject("member_exp");
                        getMvpView().setExpInfo(expObj.getString("exppoints"), expObj.getString("less_exppoints"),
                                expObj.getString("upgrade"), expObj.getString("upgrade_exppoints"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取热门代金券，此接口加载的都是积分兑换的代金券
     *
     * @param isLoadHot 是否只加载热门代金券
     */
    public void loadHotVoucherList(boolean isLoadHot) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (isLoadHot)
            params.put("recommend", "1");
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_POINTS_VOUCHER_LIST, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONArray expObj = new JSONArray();
                        if (obj.has("recommend_voucher")) {
                            expObj = obj.getJSONArray("recommend_voucher");
                        } else if (obj.has("voucher")) {
                            expObj = obj.getJSONArray("voucher");
                        }
                        List<VoucherRedpacketBean> vouchers = new ArrayList<VoucherRedpacketBean>();
                        for (int i = 0; i < expObj.length(); i++) {
                            Gson gson = new Gson();
                            VoucherRedpacketBean bean = gson.fromJson(expObj.getJSONObject(i).toString(), VoucherRedpacketBean.class);
                            vouchers.add(bean);
                        }
                        getMvpView().updateVoucherList(vouchers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取热门红包，此接口加载的都是积分兑换的红包
     *
     * @param isLoadHot 是否只加载热门红包
     */
    public void loadHotRptList(boolean isLoadHot) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (isLoadHot)
            params.put("recommend", "1");
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_POINTS_RPACKET_LIST, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONArray expObj = new JSONArray();
                        if (obj.has("recommend_rpacket")) {
                            expObj = obj.getJSONArray("recommend_rpacket");
                        } else if (obj.has("rpacket")) {
                            expObj = obj.getJSONArray("rpacket");
                        }
                        List<VoucherRedpacketBean> redpackets = new ArrayList<VoucherRedpacketBean>();
                        for (int i = 0; i < expObj.length(); i++) {
                            Gson gson = new Gson();
                            VoucherRedpacketBean bean = gson.fromJson(expObj.getJSONObject(i).toString(), VoucherRedpacketBean.class);
                            redpackets.add(bean);
                        }
                        getMvpView().updateRedpacketList(redpackets);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取热门礼品
     *
     * @param isLoadHot 是否只加载热门礼品
     */
    public void loadHotProdgoodsList(boolean isLoadHot) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (isLoadHot)
            params.put("recommend", "1");
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_POINTS_PRODGOODS_LIST, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONArray expObj = new JSONArray();
                        if (obj.has("recommend_pgoods")) {
                            expObj = obj.getJSONArray("recommend_pgoods");
                        } else if (obj.has("pgoods")) {
                            expObj = obj.getJSONArray("pgoods");
                        }
                        LogHelper.d("Presenter" , json);
                        List<ProdgoodsBean> prodgoods = new ArrayList<ProdgoodsBean>();
                        for (int i = 0; i < expObj.length(); i++) {
                            Gson gson = new Gson();
                            ProdgoodsBean bean = gson.fromJson(expObj.getJSONObject(i).toString(), ProdgoodsBean.class);
                            prodgoods.add(bean);
                        }
                        getMvpView().updateProdgoodsList(prodgoods);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
