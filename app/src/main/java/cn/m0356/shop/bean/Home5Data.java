package cn.m0356.shop.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mr on 2016/8/31.
 */
public class Home5Data {
    public static class Attr{
        public static final String ITEM = "item";
        public static final String TITLE = "title";
    }

    public static class ItemData{
        public static final String IMAGE = "image";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String IMGTITLE= "image_title";
    }

    private String title;
    private String image;
    private String type;
    private String data;
    private String item;
    private String image_title;

    public Home5Data() {
    }

    public Home5Data(String image, String type, String data,String image_title) {
        super();
        this.image = image;
        this.type = type;
        this.data = data;
        this.image_title = image_title;
    }

    public Home5Data(String title, String item) {
        super();
        this.title = title;
        this.item = item;
    }

    public static Home5Data newInstanceDetelis(String json){
        Home5Data bean = null;
        try {
            JSONObject obj = new JSONObject(json);
            if(obj.length()> 0){
                String item = obj.optString(Attr.ITEM);
                String title = obj.optString(Attr.TITLE);
                bean = new Home5Data(title, item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public static ArrayList<Home5Data> newInstanceList(String jsonDatas){
        ArrayList<Home5Data> list = new ArrayList<Home5Data>();

        try {
            JSONArray arr = new JSONArray(jsonDatas);
            int size = null == arr ? 0 : arr.length();
            for(int i = 0; i < size; i++){
                JSONObject obj = arr.getJSONObject(i);
                String image = obj.optString(ItemData.IMAGE);
                String type = obj.optString(ItemData.TYPE);
                String data = obj.optString(ItemData.DATA);
                String image_title = obj.optString(ItemData.IMGTITLE);
                list.add(new Home5Data(image, type, data,image_title));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setImgTitle(String image_title){ this.image_title = image_title;}

    public String getImgTitle(){return  image_title;}



    @Override
    public String toString() {
        return "Home5Data [title=" + title + ", image=" + image
                + ", type=" + type + ", data=" + data + ", item="
                + item + ", image_title="+image_title+"]";
    }
}
