package cn.m0356.shop.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 首页导航Bean
 *
 * @author KingKong·HE
 * @Time 2014年1月17日 下午4:44:35
 * @E-mail hjgang@bizpoer.com
 */
public class NavigationList {
    public static class Attr {
        public static final String IMAGE = "image";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String IMAGE_TITLE = "image_title";
    }

    private String image;
    private String type;
    private String data;
    private String image_title;

    public NavigationList() {
    }

    public NavigationList(String image, String type, String data, String image_title) {
        super();
        this.image = image;
        this.type = type;
        this.data = data;
        this.image_title = image_title;
    }


    public static ArrayList<NavigationList> newInstanceList(String jsonDatas) {
        ArrayList<NavigationList> AdvertDatas = new ArrayList<NavigationList>();

        try {
            JSONArray arr = new JSONArray(jsonDatas);
            int size = null == arr ? 0 : arr.length();
            for (int i = 0; i < size; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String image = obj.optString(Attr.IMAGE);
                String data = obj.optString(Attr.DATA);
                String type = obj.optString(Attr.TYPE);
                String image_title = obj.optString(Attr.IMAGE_TITLE);
                AdvertDatas.add(new NavigationList(image, type, data, image_title));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return AdvertDatas;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage_title() {
        return image_title;
    }

    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }

    @Override
    public String toString() {
        return "AdvertList [image=" + image + ", type=" + type + ", data="
                + data + "]";
    }
}
