package cn.m0356.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.bean.NavigationList;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.ui.home.SubjectWebActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;

/**
 * 首页Navigation适配器
 *
 * @author KingKong-HE
 * @Time 2015年1月4日
 * @Email KingKong@QQ.COM
 */
public class HomeNavMyGridViewListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NavigationList> navDatas;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public HomeNavMyGridViewListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return navDatas == null ? 0 : navDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return navDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<NavigationList> getHomeNavList() {
        return navDatas;
    }

    public void setHomeNavList(ArrayList<NavigationList> navDatas) {
        this.navDatas = navDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.tab_home_item_nav_gridview_item, null);
            holder = new ViewHolder();
            holder.ImageViewImagePic01 = (ImageView) convertView.findViewById(R.id.ImageViewImagePic01);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationList bean = navDatas.get(position);

        imageLoader.displayImage(bean.getImage(), holder.ImageViewImagePic01, options, animateFirstListener);
        OnImageViewClick(holder.ImageViewImagePic01, bean.getType(), bean.getData());

        return convertView;
    }

    class ViewHolder {
        ImageView ImageViewImagePic01;
    }

    public void OnImageViewClick(View view, final String type, final String data) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("keyword")) {//搜索关键字
                    Intent intent = new Intent(context, GoodsListFragmentManager.class);
                    intent.putExtra("keyword", data);
                    intent.putExtra("gc_name", data);
                    context.startActivity(intent);
                } else if (type.equals("special")) {//专题编号
                    Intent intent = new Intent(context, SubjectWebActivity.class);
                    intent.putExtra("data", Constants.URL_SPECIAL + "&special_id=" + data + "&type=html");
                    context.startActivity(intent);
                } else if (type.equals("goods")) {//商品编号
                    Intent intent = new Intent(context, GoodsDetailsActivity.class);
                    intent.putExtra("goods_id", data);
                    context.startActivity(intent);
                } else if (type.equals("url")) {//地址
                    Intent intent = new Intent(context, SubjectWebActivity.class);
                    intent.putExtra("data", data);
                    context.startActivity(intent);
                } else if (type.equals("store")) { // 店铺
                    //Toast.makeText(getActivity(), "store", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, newStoreInFoActivity.class);
                    intent.putExtra("store_id", data);
                    context.startActivity(intent);
                } else if (type.equals("category")) { // 分类
                    Intent intent = new Intent(context, GoodsListFragmentManager.class);
                    intent.putExtra("gc_id", data);
                    context.startActivity(intent);
                }
            }
        });
    }
}