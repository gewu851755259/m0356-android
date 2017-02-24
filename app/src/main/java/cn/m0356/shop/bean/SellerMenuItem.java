package cn.m0356.shop.bean;

/**
 * Created by jiangtao on 2016/12/5.
 */
public class SellerMenuItem {
    public String menuName;
    public String menuDesc;
    public int menuRes;

    public SellerMenuItem(String menuName, String menuDesc, int menuRes) {
        this.menuName = menuName;
        this.menuDesc = menuDesc;
        this.menuRes = menuRes;
    }

    public SellerMenuItem() {
    }
}
