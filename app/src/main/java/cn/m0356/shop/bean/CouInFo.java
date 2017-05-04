/**
 * ProjectName:AndroidShopNC2014Moblie
 * PackageName:net.shopnc.android.model
 * FileNmae:ManSongInFo.java
 */
package cn.m0356.shop.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 加价购Bean
 *
 * @author yml
 * @Time 2017年04月14日 下午4:44:35
 * @E-mail
 */
public class CouInFo {
    public static class Attr {
        public static final String COU_ID = "cou_id";
        public static final String XLEVEL = "xlevel";
        public static final String MINCOST = "mincost";
        public static final String MAXCOU = "maxcou";
        public static final String SKU_ID = "sku_id";
        public static final String SKU_NAME = "sku_name";
        public static final String PRICE = "price";
    }

    private String cou_id;
    private String xlevel;
    private String mincost;
    private String maxcou;
    private String sku_id;
    private String sku_name;
    private String price;

    public CouInFo() {
    }

    public CouInFo(String cou_id, String xlevel, String mincost, String maxcou,
                   String sku_id, String sku_name, String price) {
        super();
        this.cou_id = cou_id;
        this.xlevel = xlevel;
        this.mincost = mincost;
        this.maxcou = maxcou;
        this.sku_id = sku_id;
        this.sku_name = sku_name;
        this.price = price;
    }

    public static ArrayList<CouInFo> newInstanceList(String json) {
        ArrayList<CouInFo> cous = new ArrayList<CouInFo>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                String cou_id = obj.optString(Attr.COU_ID);
                String xlevel = obj.optString(Attr.XLEVEL);
                String mincost = obj.optString(Attr.MINCOST);
                String maxcou = obj.optString(Attr.MAXCOU);
                String sku_id = obj.optString(Attr.SKU_ID);
                String sku_name = obj.optString(Attr.SKU_NAME);
                String price = obj.optString(Attr.PRICE);
                CouInFo bean = new CouInFo(cou_id, xlevel, mincost, maxcou, sku_id, sku_name, price);
                cous.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cous;
    }

    public String getCou_id() {
        return cou_id;
    }

    public String getXlevel() {
        return xlevel;
    }

    public String getMincost() {
        return mincost;
    }

    public String getMaxcou() {
        return maxcou;
    }

    public String getSku_id() {
        return sku_id;
    }

    public String getPrice() {
        return price;
    }

    public String getSku_name() {
        return sku_name;
    }

    @Override
    public String toString() {
        return "CouInFo{" +
                "cou_id='" + cou_id + '\'' +
                ", xlevel='" + xlevel + '\'' +
                ", mincost='" + mincost + '\'' +
                ", maxcou='" + maxcou + '\'' +
                ", sku_id='" + sku_id + '\'' +
                ", sku_name='" + sku_name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
