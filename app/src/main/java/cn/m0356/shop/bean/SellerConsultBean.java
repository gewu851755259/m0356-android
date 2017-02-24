package cn.m0356.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jiangtao on 2017/1/10.
 */
public class SellerConsultBean implements Parcelable {

    public String consult_id;
    public String goods_id;
    public String goods_name;
    public String member_id;
    public String member_name;
    public String store_id;
    public String store_name;
    public String ct_id;
    public String consult_content;
    public String consult_addtime;
    public String consult_reply;
    public String consult_reply_time;
    public String isanonymous;

    public SellerConsultBean() {
    }

    protected SellerConsultBean(Parcel in) {
        consult_id = in.readString();
        goods_id = in.readString();
        goods_name = in.readString();
        member_id = in.readString();
        member_name = in.readString();
        store_id = in.readString();
        store_name = in.readString();
        ct_id = in.readString();
        consult_content = in.readString();
        consult_addtime = in.readString();
        consult_reply = in.readString();
        consult_reply_time = in.readString();
        isanonymous = in.readString();
    }

    public static final Creator<SellerConsultBean> CREATOR = new Creator<SellerConsultBean>() {
        @Override
        public SellerConsultBean createFromParcel(Parcel in) {
            return new SellerConsultBean(in);
        }

        @Override
        public SellerConsultBean[] newArray(int size) {
            return new SellerConsultBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(consult_id);
        dest.writeString(goods_id);
        dest.writeString(goods_name);
        dest.writeString(member_id);
        dest.writeString(member_name);
        dest.writeString(store_id);
        dest.writeString(store_name);
        dest.writeString(ct_id);
        dest.writeString(consult_content);
        dest.writeString(consult_addtime);
        dest.writeString(consult_reply);
        dest.writeString(consult_reply_time);
        dest.writeString(isanonymous);
    }
}
