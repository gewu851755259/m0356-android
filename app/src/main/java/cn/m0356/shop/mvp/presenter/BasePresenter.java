package cn.m0356.shop.mvp.presenter;

import cn.m0356.shop.mvp.base.IMvpView;
import cn.m0356.shop.mvp.base.IPresenter;

/**
 * Created by yml on 2017/3/31.
 */

public class BasePresenter<T extends IMvpView> implements IPresenter<T> {

    private T mvpView;

    @Override
    public void attachView(T mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        this.mvpView = null;
    }

    public T getMvpView() {
        return mvpView;
    }
}
