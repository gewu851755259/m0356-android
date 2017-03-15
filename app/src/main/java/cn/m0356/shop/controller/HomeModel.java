package cn.m0356.shop.controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.m0356.shop.bean.AdvertList;
import cn.m0356.shop.bean.Home3Data;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.bean.NavigationList;
import cn.m0356.shop.common.FileUtils;

/**
 * Created by minla on 2017/3/3.
 */
public class HomeModel implements IHomeModel {

    @Override
    public ArrayList<AdvertList> analysisAdvList(JSONObject jsonObj , String title) throws IOException, JSONException {
        String advertJson = jsonObj.getString(title);
        JSONObject itemObj = new JSONObject(advertJson);
        String item = itemObj.getString("item");
        FileUtils.saveToLocal(item);
        ArrayList<AdvertList> advertList = new ArrayList<AdvertList>();
        if (!item.equals("[]")) {
            advertList = AdvertList.newInstanceList(item);
        }
        return advertList;
    }

    @Override
    public ArrayList<HomeGoodsList> analysisPreGoodsList(JSONObject jsonObj, String title) throws JSONException, IOException {
        JSONObject itemObj = jsonObj.getJSONObject(title);
        String item = itemObj.getString("item");
        ArrayList<HomeGoodsList> preGoodsList = new ArrayList<HomeGoodsList>();
        if (!item.equals("[]")) {
            preGoodsList = HomeGoodsList.newInstanceList(item);
        }
        return preGoodsList;
    }

    @Override
    public ArrayList<NavigationList> analysisNavList(JSONObject jsonObj, String title) throws JSONException, IOException {
        String navJson = jsonObj.getString(title);
        JSONObject itemObj = new JSONObject(navJson);
        String item = itemObj.getString("item");
        ArrayList<NavigationList> navList = new ArrayList<NavigationList>();
        if (!item.equals("[]")) {
            navList = NavigationList.newInstanceList(item);
        }
        return navList;
    }

    @Override
    public ArrayList<Home3Data> analysisHome13(JSONObject jsonObj , String title) throws JSONException, IOException {
        JSONObject home13Json = jsonObj.getJSONObject(title);
        ArrayList<Home3Data> homeDataList = new ArrayList<Home3Data>();
        Home3Data bean1 = new Home3Data(home13Json.getString("square1_image"), home13Json.getString("square1_type"), home13Json.getString("square1_data"));
        homeDataList.add(bean1);
        Home3Data bean2 = new Home3Data(home13Json.getString("square2_image"), home13Json.getString("square2_type"), home13Json.getString("square2_data"));
        homeDataList.add(bean2);
        Home3Data bean3 = new Home3Data(home13Json.getString("square3_image"), home13Json.getString("square3_type"), home13Json.getString("square3_data"));
        homeDataList.add(bean3);
        Home3Data bean4 = new Home3Data(home13Json.getString("square4_image"), home13Json.getString("square4_type"), home13Json.getString("square4_data"));
        homeDataList.add(bean4);
        Home3Data bean5 = new Home3Data(home13Json.getString("square5_image"), home13Json.getString("square5_type"), home13Json.getString("square5_data"));
        homeDataList.add(bean5);
        Home3Data bean6 = new Home3Data(home13Json.getString("square6_image"), home13Json.getString("square6_type"), home13Json.getString("square6_data"));
        homeDataList.add(bean6);
        Home3Data bean7 = new Home3Data(home13Json.getString("square7_image"), home13Json.getString("square7_type"), home13Json.getString("square7_data"));
        homeDataList.add(bean7);
        Home3Data bean8 = new Home3Data(home13Json.getString("square8_image"), home13Json.getString("square8_type"), home13Json.getString("square8_data"));
        homeDataList.add(bean8);
        return homeDataList;
    }
}
