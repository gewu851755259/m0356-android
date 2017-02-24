package cn.m0356.shop.bean;

import java.util.List;

/**
 * Created by jiangtao on 2016/11/29.
 */
public class MyPayInfoBean {

    /**
     * member_available_pd : 0.00
     * member_available_rcb : 0.93
     * member_paypwd : true
     * pay_amount : 0.01
     * pay_sn : 210533752172066889
     * payed_amount : 0.00
     * payment_list : [{"payment_code":"alipay","payment_name":"支付宝"},{"payment_code":"wxpay_jsapi","payment_name":"微信支付JSAPI"},{"payment_code":"alipay_native","payment_name":"支付宝移动支付"}]
     */

    public PayInfoBean pay_info;

    public static class PayInfoBean {
        public String member_available_pd;
        public String member_available_rcb;
        public boolean member_paypwd;
        public String pay_amount;
        public String pay_sn;
        public String payed_amount;
        /**
         * payment_code : alipay
         * payment_name : 支付宝
         */

        public List<PaymentListBean> payment_list;

        public static class PaymentListBean {
            public String payment_code;
            public String payment_name;
        }
    }
}
