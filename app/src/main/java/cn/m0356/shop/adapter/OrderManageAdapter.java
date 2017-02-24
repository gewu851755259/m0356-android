package cn.m0356.shop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerOrderBean;
import cn.m0356.shop.common.SystemHelper;

/**
 * Created by jiangtao on 2016/12/6.
 */
public class OrderManageAdapter extends BaseAdapter {

    private SimpleDateFormat sdf;
    private Context context;
    private List<SellerOrderBean> data;
    private View.OnClickListener mClickListener;

    private DecimalFormat df = new DecimalFormat("#.00");

    public OrderManageAdapter(Context context) {
        data = new ArrayList<SellerOrderBean>();
        this.context = context;

    }

    /**
     * 添加并刷新数据
     * @param data
     */
    public void addData(List<SellerOrderBean> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    /*@Override
    public int getItemViewType(int position) {
        return Integer.parseInt(data.get(position).order_state);
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }*/

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SellerOrderBean sellerOrderBean = data.get(position);
        PayViewHolder vh;
        if(convertView == null){
            convertView = View.inflate(context,
                    R.layout.item_order_manage_nopay, null);
            vh = new PayViewHolder();
            vh.benCancel = (Button) convertView.findViewById(R.id.btn_order_cancel);
            vh.btnSend = (Button) convertView.findViewById(R.id.btn_order_send);
            vh.tv_order_date = (TextView) convertView.findViewById(R.id.tv_order_date);
            vh.tv_order_state = (TextView) convertView.findViewById(R.id.tv_order_state);
            vh.ll_order_goods = (LinearLayout) convertView.findViewById(R.id.ll_order_goods);
            //vh.tv_order_goods_price = (TextView) convertView.findViewById(R.id.tv_order_goods_price);
            vh.tv_order_goods_amount = (TextView) convertView.findViewById(R.id.tv_order_amount);
            vh.rl_btn_tab = (RelativeLayout) convertView.findViewById(R.id.rl_btn_tab);
            vh.tv_order_payment = (TextView) convertView.findViewById(R.id.tv_order_payment);
            vh.tv_ziti = (TextView) convertView.findViewById(R.id.tv_ziti);

            // 点击事件
            if(mClickListener != null) {
                vh.btnSend.setOnClickListener(mClickListener);
                vh.benCancel.setOnClickListener(mClickListener);
            }

            //vh.tv_order_goods_count = (TextView) convertView.findViewById(R.id.tv_order_count);
            //vh.tv_order_goods_name = (TextView) convertView.findViewById(R.id.tv_order_goods_name);
            //vh.iv_order_goods_img = (ImageView) convertView.findViewById(R.id.iv_order_img);
            convertView.setTag(vh);
        } else{
            vh = (PayViewHolder) convertView.getTag();
        }

        vh.tv_order_state.setText(sellerOrderBean.state_desc);
        vh.tv_order_payment.setText(sellerOrderBean.payment_name);
        //vh.tv_order_goods_name.setText(sellerOrderBean.goods_list.get(0).goods_name);
        //vh.tv_order_goods_price.setText(sellerOrderBean.goods_amount);
        //vh.tv_order_goods_count.setText(sellerOrderBean.goods_list.size() + "");
        // 日期
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String str = sdf.format(Long.parseLong(sellerOrderBean.add_time) * 1000);*/
        vh.tv_order_date.setText(SystemHelper.getTimeStr(sellerOrderBean.add_time));


        // 加载商品
        List<SellerOrderBean.GoodsListBean> goods_list = sellerOrderBean.goods_list;
        vh.ll_order_goods.removeAllViews();
        // 商品数量
        int num = 0;
        // 商品价格
        double price = 0.0;
        String priceToStr;
        for(int i = 0; i < goods_list.size(); i++){
            View view = View.inflate(context, R.layout.layout_seller_goods, null);
            // 将商品数据加载到布局
            loadGoodsData(view, goods_list.get(i));
            vh.ll_order_goods.addView(view);
            num += Integer.parseInt(goods_list.get(i).goods_num);
            // 商品实际支付
            price += Double.parseDouble(goods_list.get(i).goods_pay_price);
        }
        if(!TextUtils.isEmpty(sellerOrderBean.shipping_fee)){
            price += Double.parseDouble(sellerOrderBean.shipping_fee);
        }
        priceToStr = df.format(price);
        // 自提
        if(sellerOrderBean.is_ziti.equals("0")){
            // 非自提
            vh.tv_ziti.setText("");
        } else {
            vh.tv_ziti.setText("取货方式：上门自提");
        }
        // 添加文本样式
        String numStr = "共" + num + "件商品.";
        String priceStr = "合计" + priceToStr + "元.";
        String shippingStr = "含运费" + sellerOrderBean.shipping_fee + "元";

        SpannableStringBuilder sb = new SpannableStringBuilder(priceStr);
        // 样式
        TextAppearanceSpan colorSpan = new TextAppearanceSpan(context, R.style.red_style);
        // 大小
        //AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(15, true);
        sb.setSpan(colorSpan, 2, priceStr.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //sb.setSpan(sizeSpan, 2, priceStr.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        vh.tv_order_goods_amount.setText("");
        vh.tv_order_goods_amount.append(numStr);
        vh.tv_order_goods_amount.append(sb);
        vh.tv_order_goods_amount.append(shippingStr);

        modifyButton(sellerOrderBean.order_state, vh);
        vh.benCancel.setTag(sellerOrderBean.order_id);
        vh.btnSend.setTag(sellerOrderBean.order_id);
        /*switch (type){
            case 40: // 交易成功
                break;
            case 20: // 已付款，待发货
                break;
            case 10: // 已产生 未支付
                break;
            case 30: // 待收获
                break;
        }*/
        return convertView;
    }

    private void modifyButton(String order_state, PayViewHolder vh) {
        // 恢复所有状态
        vh.rl_btn_tab.setVisibility(View.VISIBLE);
        vh.btnSend.setVisibility(View.VISIBLE);
        vh.benCancel.setVisibility(View.VISIBLE);
        // RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vh.btnSend.getLayoutParams();
        // layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        if(order_state.equals("10")){ // 已产生，未支付
            //  发货订单隐藏
            vh.btnSend.setVisibility(View.GONE);
        } else if(order_state.equals("20")){ // 已付款，待发货

        } else if(order_state.equals("30")){ // 待收货
            vh.btnSend.setVisibility(View.GONE);
        } else if(order_state.equals("40")){ // 交易成功
            vh.rl_btn_tab.setVisibility(View.GONE);
            // layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        } else if(order_state.equals("0")){ // 取消
            vh.rl_btn_tab.setVisibility(View.GONE);
        }

        // vh.btnSend.setLayoutParams(layoutParams);
    }

    private void loadGoodsData(View view, SellerOrderBean.GoodsListBean goodsListBean) {
        TextView tv_order_goods_price = (TextView) view.findViewById(R.id.tv_order_goods_price);
        TextView tv_order_goods_count = (TextView) view.findViewById(R.id.tv_order_count);
        TextView tv_order_goods_name = (TextView) view.findViewById(R.id.tv_order_goods_name);
        ImageView iv_order_goods_img = (ImageView) view.findViewById(R.id.iv_order_img);
        tv_order_goods_count.setText("X" + goodsListBean.goods_num);
        tv_order_goods_name.setText(goodsListBean.goods_name);
        tv_order_goods_price.setText(goodsListBean.goods_price);
        //带缓存的商品图
        Glide.with(context)
                .load(goodsListBean.image_240_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher)  //设置占位图
                .error(R.drawable.ic_launcher)      //加载错误图
                .into(iv_order_goods_img);
    }

    public void clear() {
        data.clear();
    }

    private class PayViewHolder{
        //public ImageView iv_order_goods_img;
       // public TextView tv_order_goods_name;
       // public TextView tv_order_goods_count;
        public TextView tv_order_goods_amount;
        public TextView tv_order_payment;
        //public TextView tv_order_goods_price;
        public TextView tv_order_state;
        public TextView tv_order_date;
        public Button btnSend, benCancel;
        public LinearLayout ll_order_goods;
        public TextView tv_ziti;
        public RelativeLayout rl_btn_tab;

    }
}
