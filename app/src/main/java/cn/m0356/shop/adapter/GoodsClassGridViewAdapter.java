package cn.m0356.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.GoodsClassInfo;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.SystemHelper;

import java.util.ArrayList;

/**
 * 商品分类适配器
 *
 * @author dqw
 * @Time 2015-7-2
 */
public class GoodsClassGridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodsClassInfo> goodsClassInfoList;
    private int selectedItem;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public GoodsClassGridViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsClassInfoList == null ? 0 : goodsClassInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsClassInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedItem(int positon) {
        selectedItem = positon;
    }

    public void setGoodsClassInfoList(ArrayList<GoodsClassInfo> goodsClassInfoList) {
        this.goodsClassInfoList = goodsClassInfoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.gridview_goods_class_item, null);
            holder = new ViewHolder();
            holder.tvGoodsClassName = (TextView) convertView.findViewById(R.id.tvGoodsClassName);
            holder.ivGoodsClassImage = (ImageView) convertView.findViewById(R.id.ivGoodsClassImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GoodsClassInfo goodsClassInfo = goodsClassInfoList.get(position);
        holder.tvGoodsClassName.setText(goodsClassInfo.getGcName());
        imageLoader.displayImage(goodsClassInfo.getGcPic(), holder.ivGoodsClassImage, options, animateFirstListener);
        return convertView;
    }

    class ViewHolder {
        TextView tvGoodsClassName;
        ImageView ivGoodsClassImage;
    }

}
