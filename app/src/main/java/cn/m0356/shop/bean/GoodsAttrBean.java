package cn.m0356.shop.bean;

import java.util.List;

/**
 * Created by jiangtao on 2017/1/4.
 */
public class GoodsAttrBean {

    public int attr_id;
    public String attr_name;

    public List<ValueBean> value;

    public static class ValueBean {
        public String attr_value_id;
        public String attr_value_name;
    }
}
