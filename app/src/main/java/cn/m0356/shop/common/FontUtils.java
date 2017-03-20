package cn.m0356.shop.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;

/**
 * Created by yml on 2017/3/20.
 */
public class FontUtils {

    /**
     * stxihei.ttf -------> 华文细黑
     * youyuan.ttf -------> 幼圆
     * kaiti.TTF   -------> 楷体
     */
    private static ArrayMap<String, Typeface> typefaces = new ArrayMap<String, Typeface>();

    public static Typeface getTypeface(Context context, String fontName) {
        if (typefaces.containsKey(fontName)) {
            return typefaces.get(fontName);
        }
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
        typefaces.put(fontName, typeface);
        return typeface;
    }

}
