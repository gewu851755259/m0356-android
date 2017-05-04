package cn.m0356.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.ProdgoodsBean;
import cn.m0356.shop.common.LogHelper;

/**
 * Created by yml on 2017/4/5.
 * 热门礼品列表
 */
public class ProdgoodsListViewAdapter extends BaseAdapter {

    private List<ProdgoodsBean> data;
    private OnVoucherRedpacketListener mListener;
    private Context mContext;

    public ProdgoodsListViewAdapter(Context mContext, List<ProdgoodsBean> data) {
        this.mContext = mContext;
        if (data == null) {
            this.data = new ArrayList<ProdgoodsBean>();
        } else {
            this.data = data;
        }
    }

    public interface OnVoucherRedpacketListener {
        void getRptClick(int position); // 领取免费红包

        void applyRptClick(int position); // 使用免费红包

        void getVoucherClick(int position); // 领取免费代金券

        void applyVoucherClick(int position); // 使用免费代金券
    }

    public void setOnVoucherRedpacketListener(OnVoucherRedpacketListener listener) {
        this.mListener = listener;
    }

    public void update(List<ProdgoodsBean> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 加载更多数据之后的刷新
     *
     * @param data 新加载的数据
     */
    public void addData(List<ProdgoodsBean> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_prodgood, null);
            vh = new ViewHolder();
            vh.tvLimit = (TextView) convertView.findViewById(R.id.tv_gift_limit);
            vh.tvTime = (TextView) convertView.findViewById(R.id.tv_gift_time);
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_gift_title);
            vh.tvPrice = (TextView) convertView.findViewById(R.id.tv_gift_price);
            vh.tvPoint = (TextView) convertView.findViewById(R.id.tv_gift_point);
            vh.tvAmount = (TextView) convertView.findViewById(R.id.tv_gift_amount);
            vh.btn = (Button) convertView.findViewById(R.id.btn_gift);
            vh.iv = (ImageView) convertView.findViewById(R.id.iv_gift_img);
            vh.tvLeve = (TextView) convertView.findViewById(R.id.tv_gift_level);
            vh.tvType = (TextView) convertView.findViewById(R.id.tv_gift_type);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final ProdgoodsBean prodgoodsBean = data.get(position);
        ImageLoader.getInstance().displayImage(prodgoodsBean.pgoods_image, vh.iv);
        vh.tvTitle.setText(prodgoodsBean.pgoods_name);
        vh.tvLeve.setText(prodgoodsBean.pgoods_limitgradename);
        vh.tvPrice.setText("原价：" + prodgoodsBean.pgoods_price + "元");
        vh.tvPoint.setText("兑换积分：" + prodgoodsBean.pgoods_points);
        vh.tvLimit.setText("限量兑换：此礼品每人限兑" + prodgoodsBean.pgoods_limitnum + "个");
        return convertView;
    }

    private class ViewHolder {
        public TextView tvTitle, tvLimit, tvPrice, tvPoint, tvTime, tvAmount, tvLeve, tvType;
        public Button btn;
        public ImageView iv;
    }
}
