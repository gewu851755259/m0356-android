package cn.m0356.shop.viewinterface;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by minla on 2017/3/3.
 */
public interface IHomeView {

    // 得到HomeFragment的Activty对象
    Activity getMainActivity();

    // 得到HomeFragment的Handler对象
    Handler getHandler();

    // 得到轮播图底部轮播点的容器
    LinearLayout getDianView();

    LinearLayout getDianLowView();

    // 得到轮播图底部轮播点的ImageView集合
    ArrayList<ImageView> getDianViewList();

    ArrayList<ImageView> getDianViewLowList();

    // 得到轮播列表的控件
    ViewFlipper getAdvListView();

    // 得到高度低轮播列表的控件
    ViewFlipper getAdvListLowView();

    // 得到广告触摸时间
    View.OnTouchListener getAdvTouchListener();

    // 调用HomeFragment原来的按钮点击方法
    void onImageViewClick(ImageView imageView, String type, String data, boolean b);

    void dianSelect();

    // 得到包裹模版的父控件
    LinearLayout getHomeView();
}