package cn.m0356.shop.bean;

import java.util.List;

/**
 * Created by jiangtao on 2016/12/6.
 */
public class SellerOrderBean {

    public String add_time;
    public String api_pay_time;
    public String buyer_email;
    public String buyer_id;
    public String buyer_name;
    public String buyer_phone;
    public String chain_code;
    public String chain_id;
    public String delay_time;
    public String delete_state;
    public String evaluation_again_state;
    public String evaluation_state;
    public String finnshed_time;
    public String goods_amount;
    public int goods_count;
    public boolean if_chain_receive;
    public boolean if_deliver;
    public boolean if_lock;
    public boolean if_modify_price;
    public boolean if_store_cancel;
    public boolean if_store_send;
    public String is_ziti;
    public String lock_state;
    public String operation;
    public String order_amount;
    public String order_from;
    public String order_id;
    public String order_sn;
    public String order_state;
    public String order_type;
    public String pay_sn;
    public String payment_code;
    public String payment_name;
    public String payment_time;
    public String pd_amount;
    public String rcb_amount;
    public String refund_amount;
    public String refund_state;
    public String rpt_amount;
    public String shipping_fee;
    public String state_desc;
    public String store_id;
    public String store_name;


    public List<ExtendOrderGoodsBean> extend_order_goods;

    public List<GoodsListBean> goods_list;

    public static class ExtendOrderGoodsBean {
        public String buyer_id;
        public String commis_rate;
        public String gc_id;
        public String goods_contractid;
        public String goods_id;
        public String goods_image;
        public String goods_name;
        public String goods_num;
        public String goods_pay_price;
        public String goods_price;
        public String goods_spec;
        public String goods_type;
        public String order_id;
        public String promotions_id;
        public String rec_id;
        public String store_id;
    }

    public static class GoodsListBean {
        public String buyer_id;
        public String commis_rate;
        public String gc_id;
        public String goods_contractid;
        public String goods_id;
        public String goods_image;
        public String goods_name;
        public String goods_num;
        public String goods_pay_price;
        public String goods_price;
        public String goods_spec;
        public String goods_type;
        public String goods_type_cn;
        public String goods_url;
        public String image_240_url;
        public String image_60_url;
        public String order_id;
        public String promotions_id;
        public String rec_id;
        public String store_id;
    }
}
