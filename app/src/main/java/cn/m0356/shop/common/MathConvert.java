package cn.m0356.shop.common;

import java.math.BigDecimal;

/**
 * Created by Mr on 2016/8/5.
 */
public class MathConvert {

    //  解决double 精度问题
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
