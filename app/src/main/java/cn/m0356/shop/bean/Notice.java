package cn.m0356.shop.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/26.
 */
public class Notice {
    public String ac_id;
    public String article_show;
    public String article_time;
    public String article_content;
    public String article_title;
    public static Notice newInstance(String json){
        try {
            Notice notice = new Notice();
            JSONObject jo = new JSONObject(json);
            notice.ac_id = jo.getString("ac_id");
            notice.article_content = jo.getString("article_content");
            notice.article_show = jo.getString("article_show");
            notice.article_time = jo.getString("article_time");
            notice.article_title = jo.getString("article_title");
            return notice;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
