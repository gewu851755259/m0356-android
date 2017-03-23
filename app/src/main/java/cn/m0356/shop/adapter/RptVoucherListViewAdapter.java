package cn.m0356.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.common.LogHelper;

/**
 * Created by yml on 2017/3/22.
 * 领券中心红包和代金券混合列表
 */
public class RptVoucherListViewAdapter extends BaseAdapter {

    private List<VoucherRedpacketBean> data;
    private OnVoucherRedpacketListener mListener;
    private Context mContext;

    public RptVoucherListViewAdapter(Context mContext, List<VoucherRedpacketBean> data) {
        this.mContext = mContext;
        if (data == null) {
            this.data = new ArrayList<VoucherRedpacketBean>();
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

    /**
     * 领取免费红包成功之后列表的刷新
     */
    public void getRptSuccessUpdate(int position) {
        VoucherRedpacketBean bean = this.data.get(position);
        bean.rpacket_t_giveout = Integer.parseInt(bean.rpacket_t_giveout) + 1 + "";
        bean.rpacket_t_isreceive = "1";
        notifyDataSetChanged();
    }

    /**
     * 领取免费代金券成功之后列表的刷新
     */
    public void getVoucherSuccessUpdate(int position) {
        VoucherRedpacketBean bean = this.data.get(position);
        bean.voucher_t_giveout = Integer.parseInt(bean.voucher_t_giveout) + 1 + "";
        bean.voucher_t_isreceive = "1";
        notifyDataSetChanged();
    }

    public void update(List<VoucherRedpacketBean> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 加载更多数据之后的刷新
     *
     * @param data 新加载的数据
     */
    public void addData(List<VoucherRedpacketBean> data) {
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
            convertView = View.inflate(mContext, R.layout.item_voucher, null);
            vh = new ViewHolder();
            vh.tvLimit = (TextView) convertView.findViewById(R.id.tv_voucher_limit);
            vh.tvTime = (TextView) convertView.findViewById(R.id.tv_voucher_time);
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_voucher_title);
            vh.tvUsed = (TextView) convertView.findViewById(R.id.tv_voucher_used);
            vh.tvAmount = (TextView) convertView.findViewById(R.id.tv_voucher_amount);
            vh.btn = (Button) convertView.findViewById(R.id.btn_voucher);
            vh.iv = (ImageView) convertView.findViewById(R.id.iv_voucher_img);
            vh.tvLeve = (TextView) convertView.findViewById(R.id.tv_voucher_leve);
            vh.tvType = (TextView) convertView.findViewById(R.id.tv_voucher_type);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final VoucherRedpacketBean voucherRedpacketBean = data.get(position);
        if (voucherRedpacketBean.rpacket_t_id != null && !"".equals(voucherRedpacketBean.rpacket_t_id)
                && Integer.parseInt(voucherRedpacketBean.rpacket_t_id) > 0) { // 免费红包类型
            vh.tvTitle.setText(voucherRedpacketBean.rpacket_t_title);
            vh.tvLimit.setText("购物满" + voucherRedpacketBean.rpacket_t_limit + "元可用");
            vh.tvTime.setText("有效期：" + voucherRedpacketBean.rpacket_t_start_date
                    + "~" + voucherRedpacketBean.rpacket_t_end_date);
            vh.tvAmount.setText(voucherRedpacketBean.rpacket_t_price);
            vh.tvLeve.setText(voucherRedpacketBean.rpacket_t_mgradelimittext);
            vh.tvUsed.setText(voucherRedpacketBean.rpacket_t_giveout + "人已领取");
            vh.iv.setImageResource(R.drawable.triangle_red);
            vh.tvType.setText("红 包");
            if ("1".equals(voucherRedpacketBean.rpacket_t_isreceive)) {
                vh.btn.setText("使 用");
                vh.btn.setBackgroundResource(R.drawable.shape_green_btn);
            } else {
                vh.btn.setText("立即领取");
                vh.btn.setBackgroundResource(R.drawable.shape_red_btn);
            }
            //带缓存的商品图
            /*Glide.with(VoucherActivity.this)
                    .load(voucherRedpacketBean.rpacket_t_customimg_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.triangle_red)  //设置占位图
                    .error(R.drawable.triangle_red)      //加载错误图
                    .into(vh.iv);*/

            vh.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("0".equals(voucherRedpacketBean.rpacket_t_isreceive) && mListener != null)
                        mListener.getRptClick(position);
                    else if ("1".equals(voucherRedpacketBean.rpacket_t_isreceive) && mListener != null)
                        mListener.applyRptClick(position);
                }
            });
        } else if (voucherRedpacketBean.voucher_t_id != null && !"".equals(voucherRedpacketBean.voucher_t_id)
                && Integer.parseInt(voucherRedpacketBean.voucher_t_id) > 0) { // 免费代金券类型
            vh.tvTitle.setText(voucherRedpacketBean.voucher_t_storename);
            vh.tvLimit.setText("购物满" + voucherRedpacketBean.voucher_t_limit + "元可用");
            vh.tvTime.setText("有效期至" + voucherRedpacketBean.voucher_t_end_date);
            vh.tvAmount.setText(voucherRedpacketBean.voucher_t_price);
            vh.tvLeve.setText(voucherRedpacketBean.voucher_t_mgradelimittext);
            vh.tvUsed.setText(voucherRedpacketBean.voucher_t_giveout + "人已领取");
            vh.iv.setImageResource(R.drawable.triangle_orangle);
            vh.tvType.setText("代金券");
            if ("1".equals(voucherRedpacketBean.voucher_t_isreceive)) {
                vh.btn.setText("使 用");
                vh.btn.setBackgroundResource(R.drawable.shape_green_btn);
            } else {
                vh.btn.setText("立即领取");
                vh.btn.setBackgroundResource(R.drawable.shape_red_btn);
            }
            //带缓存的商品图
            /*Glide.with(VoucherActivity.this)
                    .load(voucherRedpacketBean.rpacket_t_customimg_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.triangle_red)  //设置占位图
                    .error(R.drawable.triangle_red)      //加载错误图
                    .into(vh.iv);*/

            vh.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("0".equals(voucherRedpacketBean.voucher_t_isreceive) && mListener != null)
                        mListener.getVoucherClick(position);
                    else if ("1".equals(voucherRedpacketBean.voucher_t_isreceive) && mListener != null)
                        mListener.applyVoucherClick(position);
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tvTitle, tvLimit, tvUsed, tvTime, tvAmount, tvLeve, tvType;
        public Button btn;
        public ImageView iv;
    }
}
