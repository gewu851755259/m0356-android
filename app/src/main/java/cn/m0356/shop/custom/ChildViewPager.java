package cn.m0356.shop.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangtao on 2016/9/5.
 */
public class ChildViewPager extends ViewPager {
    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        List<ViewParent> vpParent = getVpParent(this);
       /* if(getCurrentItem() == getAdapter().getCount()-1) {
            eachRequest(false, vpParent);
            return super.onTouchEvent(event);
        }*/
        // 这里子类必须向父类请求是否拦截，如果只靠父viewpager判断 有bug
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                eachRequest(true, vpParent);
                break;
            case MotionEvent.ACTION_MOVE:
                eachRequest(true, vpParent);
                break;
            case MotionEvent.ACTION_CANCEL:
                eachRequest(false, vpParent);
                break;
            case MotionEvent.ACTION_UP:
                eachRequest(false, vpParent);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取viewpager父控件
     */
    public List<ViewParent> getVpParent(ViewParent view) {
        List<ViewParent> parents = new ArrayList<ViewParent>();
        ViewParent parent = view.getParent();
        if (parent != null) {
            parents.add(parent);
            if (parent instanceof ViewPager) {
                return parents;
            } else {
                return getVpParent(parent);
            }
        } else {
            return parents;
        }
    }

    public void eachRequest(boolean b, List<ViewParent> vpParent){
        for(ViewParent vp:vpParent){
            vp.requestDisallowInterceptTouchEvent(b);
        }
    }

}
