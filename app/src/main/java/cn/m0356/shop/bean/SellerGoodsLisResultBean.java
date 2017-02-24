package cn.m0356.shop.bean;

import java.util.List;

/**
 * Created by jiangtao on 2016/12/29.
 */
public class SellerGoodsLisResultBean {
    public List<GoodsListBean> goods_list;
    public static class GoodsListBean {
        public String goods_addtime;
        public String goods_commonid;
        public String goods_image;
        public String goods_lock;
        public String goods_name;
        public String goods_price;
        public String goods_state;
        public int goods_storage_sum;
    }
}
