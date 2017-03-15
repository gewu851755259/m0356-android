package cn.m0356.shop.controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.m0356.shop.bean.AdvertList;
import cn.m0356.shop.bean.Home3Data;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.bean.NavigationList;

/**
 * Created by minla on 2017/3/3.
 *
 * 读写数据，并没想好怎么用！
 */
public interface IHomeModel {

    // 解析轮播列表的数据
    ArrayList<AdvertList> analysisAdvList(JSONObject jsonObj , String title) throws JSONException, IOException;

    // 解析特惠商品
    ArrayList<HomeGoodsList> analysisPreGoodsList(JSONObject jsonObj , String title) throws JSONException, IOException;

    // 解析导航的数据
    ArrayList<NavigationList> analysisNavList(JSONObject jsonObj , String title) throws JSONException, IOException;

    // 解析首页home13
    ArrayList<Home3Data> analysisHome13(JSONObject jsonObj , String title) throws JSONException, IOException;
}
