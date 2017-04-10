package cn.m0356.shop.mvp.view;

import android.widget.ProgressBar;

import java.util.List;

import cn.m0356.shop.bean.Mine;
import cn.m0356.shop.bean.ProdgoodsBean;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.mvp.base.IMvpView;

/**
 * Created by yml on 2017/3/31.
 */

public interface IPointCenterView extends IMvpView {

    /**
     * 设置用户信息
     */
    void setMemberInfo(Mine bean);

    /**
     * 设置积分值
     */
    void setPointValue(String pointValue);

    /**
     * 设置经验值相关
     */
    void setExpInfo(String exp, String less_exp, String upgrade, String upgrade_exp);

    /**
     * 更新热门代金券列表
     */
    void updateVoucherList(List<VoucherRedpacketBean> vouchers);

    /**
     * 更新热门代金券列表
     */
    void updateRedpacketList(List<VoucherRedpacketBean> redpackets);

    /**
     * 更新热门礼品列表
     */
    void updateProdgoodsList(List<ProdgoodsBean> prodgoods);

}
