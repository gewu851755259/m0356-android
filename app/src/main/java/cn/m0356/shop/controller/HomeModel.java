package cn.m0356.shop.controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.m0356.shop.bean.AdvertList;
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
}
