package cn.m0356.shop.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class HomeNoticeList {
    private static List<HomeNotice> list;
    public static List<HomeNotice> newInstance(JSONArray array){
        try {
            list = new ArrayList<HomeNotice>();
            for(int i = 0; i<array.length();i++){
                    JSONObject notice = array.getJSONObject(i);
                    HomeNotice homeNotice = new HomeNotice();
                    homeNotice.ac_id = notice.getString("ac_id");
                    homeNotice.ac_parent_id = notice.getString("ac_parent_id");
                    homeNotice.article_id = notice.getString("article_id");
                    homeNotice.article_time = notice.getString("article_time");
                    homeNotice.article_title = notice.getString("article_title");
                    homeNotice.article_url = notice.getString("article_url");
                    list.add(homeNotice);
                }
            }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
