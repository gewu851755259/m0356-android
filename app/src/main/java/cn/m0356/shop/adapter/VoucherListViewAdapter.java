package cn.m0356.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.VoucherList;
import cn.m0356.shop.common.ShopHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 收货地址列表适配器
 *
 * @author KingKong·HE
 * @Time 2014-1-6 下午12:06:09
 */
public class VoucherListViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VoucherList> voucherLists;

    public VoucherListViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return voucherLists == null ? 0 : voucherLists.size();
    }

    @Override
    public Object getItem(int position) {
        return voucherLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<VoucherList> getVoucherLists() {
        return voucherLists;
    }

    public void setVoucherLists(ArrayList<VoucherList> voucherLists) {
        this.voucherLists = voucherLists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listivew_voucher_list_item, parent, false);
            holder = new ViewHolder();
            holder.ivStoreAvatar = (ImageView) convertView.findViewById(R.id.ivStoreAvatar);
            holder.storeNameID = (TextView) convertView.findViewById(R.id.storeNameID);
            holder.priceID = (TextView) convertView.findViewById(R.id.priceID);
            holder.manID = (TextView) convertView.findViewById(R.id.manID);
            holder.endTimeID = (TextView) convertView.findViewById(R.id.endTimeID);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VoucherList bean = voucherLists.get(position);

        ShopHelper.loadImage(holder.ivStoreAvatar, bean.getStore_avatar_url());
        holder.storeNameID.setText(bean.getStore_name() == null ? "" : bean.getStore_name());
        holder.priceID.setText("￥" + (bean.getVoucher_price() == null ? "" : bean.getVoucher_price()));
        holder.manID.setText("满" + (bean.getVoucher_limit() == null ? "" : bean.getVoucher_limit()) + "可用");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = "有效期至：" + sdf.format(new Date(Long.parseLong(bean.getVoucher_end_date() == null ? "0" : bean.getVoucher_end_date()) * 1000));
        holder.endTimeID.setText(endTime);

        return convertView;
    }

    class ViewHolder {
        ImageView ivStoreAvatar;
        TextView storeNameID, priceID, manID, endTimeID;
    }
}
