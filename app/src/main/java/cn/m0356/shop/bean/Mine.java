/**
 * ProjectName:AndroidShopNC2014Moblie
 * PackageName:net.shopnc.android.model
 * FileNmae:Login.java
 */
package cn.m0356.shop.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的商城BEAN
 *
 * @author dqw
 * @Time 2015/7/7
 */
public class Mine {

    private String memberName;
    private String memberAvatar;
    private String memberInvite;
    private String levelName;
    private String favStore;
    private String favGoods;
    private String orderNopay;
    private String orderNoreceipt;
    private String orderNotakes;
    private String orderNoeval;
    private String orderReturn;

    public Mine(String memberName, String memberAvatar, String memberInvite,String levelName, String favStore, String favGoods, String orderNopay, String orderNoreceipt, String orderNotakes, String orderNoeval, String orderReturn) {
        this.memberName = memberName;
        this.memberAvatar = memberAvatar;
        this.memberInvite = memberInvite;
        this.levelName = levelName;
        this.favStore = favStore;
        this.favGoods = favGoods;
        this.orderNopay = orderNopay;
        this.orderNoreceipt = orderNoreceipt;
        this.orderNotakes = orderNotakes;
        this.orderNoeval = orderNoeval;
        this.orderReturn = orderReturn;
    }

    public static Mine newInstanceList(String json) {
        Mine bean = null;
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.length() > 0) {
                String memberName = obj.optString("user_name");
                String memberAvatar = obj.optString("avatar");
                String memberInvite = obj.optString("invite_number");
                String levelName = obj.optString("level_name");
                String favStore = obj.optString("favorites_store");
                String favGoods = obj.optString("favorites_goods");
                String orderNopay = obj.optString("order_nopay_count");
                String orderNoreceipt = obj.optString("order_noreceipt_count");
                String orderNotakes = obj.optString("order_notakes_count");
                String orderNoeval = obj.optString("order_noeval_count");
                String orderReturn = obj.optString("return");
                bean = new Mine(memberName, memberAvatar, memberInvite,levelName, favStore, favGoods, orderNopay, orderNoreceipt, orderNotakes, orderNoeval, orderReturn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public String getMemberInvite() {return memberInvite;}

    public void setMemberInvite(String memberInvite) {this.memberInvite = memberInvite;}

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


    public String getMemberAvatar() {
        return memberAvatar;
    }

    public void setMemberAvatar(String memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getFavStore() {
        return favStore;
    }

    public void setFavStore(String favStore) {
        this.favStore = favStore;
    }

    public String getFavGoods() {
        return favGoods;
    }

    public void setFavGoods(String favGoods) {
        this.favGoods = favGoods;
    }

    public String getOrderNopay() {
        return orderNopay;
    }

    public void setOrderNopay(String orderNopay) {
        this.orderNopay = orderNopay;
    }

    public String getOrderNoreceipt() {
        return orderNoreceipt;
    }

    public void setOrderNoreceipt(String orderNoreceipt) {
        this.orderNoreceipt = orderNoreceipt;
    }

    public String getOrderNotakes() {
        return orderNotakes;
    }

    public void setOrderNotakes(String orderNotakes) {
        this.orderNotakes = orderNotakes;
    }

    public String getOrderNoeval() {
        return orderNoeval;
    }

    public void setOrderNoeval(String orderNoeval) {
        this.orderNoeval = orderNoeval;
    }

    public String getOrderReturn() {
        return orderReturn;
    }

    public void setOrderReturn(String orderReturn) {
        this.orderReturn = orderReturn;
    }

    @Override
    public String toString() {
        return "Mine{" +
                "memberName='" + memberName + '\'' +
                ", memberAvatar='" + memberAvatar + '\'' +
                ", levelName='" + levelName + '\'' +
                ", favStore='" + favStore + '\'' +
                ", favGoods='" + favGoods + '\'' +
                ", orderNopay='" + orderNopay + '\'' +
                ", orderNoreceipt='" + orderNoreceipt + '\'' +
                ", orderNotakes='" + orderNotakes + '\'' +
                ", orderNoeval='" + orderNoeval + '\'' +
                ", orderReturn='" + orderReturn + '\'' +
                '}';
    }
}
