package cn.m0356.shop.bean;

/**
 * Created by jiangtao on 2017/1/12.
 */
public class ComplainDetailBean {


    public MemberBean member;

    public ComplainInfoBean complain_info;

    public static class MemberBean {
        public String member_name;
    }

    public static class ComplainInfoBean {
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
        public String complain_pic1;
        public String complain_pic2;
        public String complain_pic3;
        public String complain_datetime;
        public String complain_handle_datetime;
        public String complain_handle_member_id;
        public String appeal_message;
        public String appeal_datetime;
        public String appeal_pic1;
        public String appeal_pic2;
        public String appeal_pic3;
        public String final_handle_message;
        public String final_handle_datetime;
        public String final_handle_member_id;
        public String complain_state;
        public String complain_active;
        public String member_status;
        public String complain_state_text;
    }
}
