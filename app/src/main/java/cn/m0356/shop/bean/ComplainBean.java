package cn.m0356.shop.bean;

import java.util.List;

/**
 * Created by jiangtao on 2017/1/11.
 */
public class ComplainBean {


    public String complain_id;
    public String order_id;
    public String order_goods_id;
    public String accuser_id;
    public String accuser_name;
    public String accused_id;
    public String accused_name;
    public String complain_subject_content;
    public String complain_subject_id;
    public String complain_content;
    public Object complain_pic1;
    public Object complain_pic2;
    public Object complain_pic3;
    public String complain_datetime;
    public String complain_handle_datetime;
    public String complain_handle_member_id;
    public String appeal_message;
    public String appeal_datetime;
    public Object appeal_pic1;
    public Object appeal_pic2;
    public Object appeal_pic3;
    public Object final_handle_message;
    public Object final_handle_datetime;
    public Object final_handle_member_id;
    public String complain_state;
    public String complain_active;


    public OrderInfoBean order_info;

    public static class OrderInfoBean {
        public String order_id;
        public String order_sn;
        public String pay_sn;
        public Object pay_sn1;
        public String store_id;
        public String store_name;
        public String buyer_id;
        public String buyer_name;
        public String buyer_email;
        public String buyer_phone;
        public String add_time;
        public String payment_code;
        public String payment_time;
        public String finnshed_time;
        public String goods_amount;
        public String order_amount;
        public String rcb_amount;
        public String pd_amount;
        public String shipping_fee;
        public String evaluation_state;
        public String evaluation_again_state;
        public String order_state;
        public String refund_state;
        public String lock_state;
        public String delete_state;
        public String refund_amount;
        public String delay_time;
        public String order_from;
        public String shipping_code;
        public String order_type;
        public String api_pay_time;
        public String chain_id;
        public String chain_code;
        public String rpt_amount;
        public Object trade_no;
        public String is_ziti;
        public String operation;
        public String auto;
        public String state_desc;
        public String payment_name;

        public List<ExtendOrderGoodsBean> extend_order_goods;

        public static class ExtendOrderGoodsBean {
            public String rec_id;
            public String order_id;
            public String goods_id;
            public String goods_name;
            public String goods_price;
            public String goods_num;
            public String goods_image;
            public String goods_pay_price;
            public String store_id;
            public String buyer_id;
            public String goods_type;
            public String promotions_id;
            public String commis_rate;
            public String gc_id;
            public String goods_spec;
            public String goods_contractid;
        }
    }
}
