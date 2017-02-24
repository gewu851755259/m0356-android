package cn.m0356.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jiangtao on 2016/12/16.
 */
public class RefundBean implements Parcelable {
    public String add_time;
    public String admin_message;
    public String admin_time;
    public String buyer_id;
    public String buyer_message;
    public String buyer_name;
    public String commis_rate;
    public String delay_time;
    public String express_id;
    public String goods_id;
    public String goods_name;
    public String goods_num;
    public String goods_state;
    public String order_goods_id;
    public String order_goods_type;
    public String order_id;
    public String order_lock;
    public String order_sn;
    public String pic_info;
    public String reason_id;
    public String reason_info;
    public String receive_time;
    public String refund_amount;
    public String refund_id;
    public String refund_sn;
    /** 申请状态:1为处理中,2为待管理员处理,3为已完成,默认为1 */
    public String refund_state;
    public String refund_type;
    public String return_type;
    public String rpt_amount;
    public String seller_message;
    /** 卖家处理状态:1为待审核,2为同意,3为不同意,默认为1 */
    public String seller_state;
    public String seller_time;
    public String ship_time;
    public String store_id;
    public String store_name;

    public RefundBean() {
    }

    protected RefundBean(Parcel in) {
        add_time = in.readString();
        admin_message = in.readString();
        admin_time = in.readString();
        buyer_id = in.readString();
        buyer_message = in.readString();
        buyer_name = in.readString();
        commis_rate = in.readString();
        delay_time = in.readString();
        express_id = in.readString();
        goods_id = in.readString();
        goods_name = in.readString();
        goods_num = in.readString();
        goods_state = in.readString();
        order_goods_id = in.readString();
        order_goods_type = in.readString();
        order_id = in.readString();
        order_lock = in.readString();
        order_sn = in.readString();
        pic_info = in.readString();
        reason_id = in.readString();
        reason_info = in.readString();
        receive_time = in.readString();
        refund_amount = in.readString();
        refund_id = in.readString();
        refund_sn = in.readString();
        refund_state = in.readString();
        refund_type = in.readString();
        return_type = in.readString();
        rpt_amount = in.readString();
        seller_message = in.readString();
        seller_state = in.readString();
        seller_time = in.readString();
        ship_time = in.readString();
        store_id = in.readString();
        store_name = in.readString();
    }

    public static final Creator<RefundBean> CREATOR = new Creator<RefundBean>() {
        @Override
        public RefundBean createFromParcel(Parcel in) {
            return new RefundBean(in);
        }

        @Override
        public RefundBean[] newArray(int size) {
            return new RefundBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(add_time);
        dest.writeString(admin_message);
        dest.writeString(admin_time);
        dest.writeString(buyer_id);
        dest.writeString(buyer_message);
        dest.writeString(buyer_name);
        dest.writeString(commis_rate);
        dest.writeString(delay_time);
        dest.writeString(express_id);
        dest.writeString(goods_id);
        dest.writeString(goods_name);
        dest.writeString(goods_num);
        dest.writeString(goods_state);
        dest.writeString(order_goods_id);
        dest.writeString(order_goods_type);
        dest.writeString(order_id);
        dest.writeString(order_lock);
        dest.writeString(order_sn);
        dest.writeString(pic_info);
        dest.writeString(reason_id);
        dest.writeString(reason_info);
        dest.writeString(receive_time);
        dest.writeString(refund_amount);
        dest.writeString(refund_id);
        dest.writeString(refund_sn);
        dest.writeString(refund_state);
        dest.writeString(refund_type);
        dest.writeString(return_type);
        dest.writeString(rpt_amount);
        dest.writeString(seller_message);
        dest.writeString(seller_state);
        dest.writeString(seller_time);
        dest.writeString(ship_time);
        dest.writeString(store_id);
        dest.writeString(store_name);
    }

    @Override
    public String toString() {
        return "RefundBean{" +
                "add_time='" + add_time + '\'' +
                ", admin_message='" + admin_message + '\'' +
                ", admin_time='" + admin_time + '\'' +
                ", buyer_id='" + buyer_id + '\'' +
                ", buyer_message='" + buyer_message + '\'' +
                ", buyer_name='" + buyer_name + '\'' +
                ", commis_rate='" + commis_rate + '\'' +
                ", delay_time='" + delay_time + '\'' +
                ", express_id='" + express_id + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", goods_name='" + goods_name + '\'' +
                ", goods_num='" + goods_num + '\'' +
                ", goods_state='" + goods_state + '\'' +
                ", order_goods_id='" + order_goods_id + '\'' +
                ", order_goods_type='" + order_goods_type + '\'' +
                ", order_id='" + order_id + '\'' +
                ", order_lock='" + order_lock + '\'' +
                ", order_sn='" + order_sn + '\'' +
                ", pic_info='" + pic_info + '\'' +
                ", reason_id='" + reason_id + '\'' +
                ", reason_info='" + reason_info + '\'' +
                ", receive_time='" + receive_time + '\'' +
                ", refund_amount='" + refund_amount + '\'' +
                ", refund_id='" + refund_id + '\'' +
                ", refund_sn='" + refund_sn + '\'' +
                ", refund_state='" + refund_state + '\'' +
                ", refund_type='" + refund_type + '\'' +
                ", return_type='" + return_type + '\'' +
                ", rpt_amount='" + rpt_amount + '\'' +
                ", seller_message='" + seller_message + '\'' +
                ", seller_state='" + seller_state + '\'' +
                ", seller_time='" + seller_time + '\'' +
                ", ship_time='" + ship_time + '\'' +
                ", store_id='" + store_id + '\'' +
                ", store_name='" + store_name + '\'' +
                '}';
    }
}
