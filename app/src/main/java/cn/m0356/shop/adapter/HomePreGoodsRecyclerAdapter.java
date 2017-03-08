package cn.m0356.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.ui.home.SubjectWebActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;

/**
 * Created by minla on 2017/3/6.
 */
public class HomePreGoodsRecyclerAdapter extends RecyclerView.Adapter<HomePreGoodsRecyclerAdapter.PreGoodsViewHolder> {

    public static class PreGoodsViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageViewImagePic01;
        final TextView textViewTitle;
        final TextView textViewPrice;

        public PreGoodsViewHolder(View itemView, ImageView imageViewImagePic01, TextView textViewTitle, TextView textViewPrice) {
            super(itemView);
            this.imageViewImagePic01 = imageViewImagePic01;
            this.textViewTitle = textViewTitle;
            this.textViewPrice = textViewPrice;
        }
    }

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<HomeGoodsList> preGoodsLists;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public HomePreGoodsRecyclerAdapter(Context context, ArrayList<HomeGoodsList> preGoodsLists) {
        this.context = context;
        this.preGoodsLists = preGoodsLists;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public PreGoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.tab_home_item_pregoods_recyclerview_item, parent, false);
        final ImageView imageViewImagePic01 = (ImageView) view.findViewById(R.id.ImageViewImagePic01);
        final TextView textViewTitle = (TextView) view.findViewById(R.id.TextViewTitle);
        final TextView textViewPrice = (TextView) view.findViewById(R.id.TextViewPrice);
        return new PreGoodsViewHolder(view, imageViewImagePic01, textViewTitle, textViewPrice);
    }

    @Override
    public void onBindViewHolder(PreGoodsViewHolder holder, int position) {
        HomeGoodsList bean = preGoodsLists.get(position);

        holder.textViewTitle.setText(bean.getGoods_name());
        holder.textViewPrice.setText("￥" + bean.getGoods_promotion_price());

        imageLoader.displayImage(bean.getGoods_image(), holder.imageViewImagePic01, options, animateFirstListener);
        OnImageViewClick(holder.imageViewImagePic01, "goods", bean.getGoods_id());
    }

    @Override
    public int getItemCount() {
        return preGoodsLists.size();
    }

    public void OnImageViewClick(View view, final String type, final String data) {
        view.setOnClickListener(new View.OnClickListener() {
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
