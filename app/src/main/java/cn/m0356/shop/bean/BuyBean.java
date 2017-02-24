package cn.m0356.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiangtao on 2016/11/9.
 */
public class BuyBean implements Parcelable {
    public HashMap<String, String> map;

    public BuyBean(HashMap<String, String> map) {
        this.map = map;
    }


    public static final Creator<BuyBean> CREATOR = new Creator<BuyBean>() {
        @Override
        public BuyBean createFromParcel(Parcel in) {
            BuyBean bean = new BuyBean(in.readHashMap(HashMap.class.getClassLoader()));
            return bean;
        }

        @Override
        public BuyBean[] newArray(int size) {
            return new BuyBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(map);
    }
}
