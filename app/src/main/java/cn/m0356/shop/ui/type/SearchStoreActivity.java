package cn.m0356.shop.ui.type;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.Home1Data;
import cn.m0356.shop.bean.QueryStoreBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ScreenUtil;
import cn.m0356.shop.custom.MyProgressDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.home.SearchActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;

/**
 * Created by jiangtao on 2016/11/25.
 */
public class SearchStoreActivity extends Activity implements View.OnClickListener {
    // 返回按钮
    private ImageButton ibBack;
    // 搜索框
    private TextView tvSearch;
    // 店铺搜索结果列表
    private ListView lv_store;

    private MyProgressDialog progressDialog;
    private List<QueryStoreBean> datas;

    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);
        findView();
        init();
    }

    private void init() {
        progressDialog = new MyProgressDialog(this, "正在加载中...", R.anim.progress_round);
        progressDialog.show();
        String keyword = getIntent().getStringExtra("keyword");
        if(!TextUtils.isEmpty(keyword))
            tvSearch.setText(keyword);
        datas = new ArrayList<QueryStoreBean>();
        mMyAdapter = new MyAdapter();
        mMyAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入店铺
                if(v.getId() == R.id.btn_enter){
                    Intent intent = new Intent(SearchStoreActivity.this, newStoreInFoActivity.class);
                    intent.putExtra("store_id", (String)v.getTag());
                    startActivity(intent);
                    finish();
                } else if(v instanceof LinearLayout){
                    // 进入商品
                    Intent intent = new Intent(SearchStoreActivity.this, GoodsDetailsActivity.class);
                    intent.putExtra("goods_id", (String)v.getTag());
                    startActivity(intent);
                    finish();
                }
            }
        });
        lv_store.setAdapter(mMyAdapter);
        queryStore(keyword);
        ibBack.setOnClickListener(this);
    }


    // 加载数据
    private void queryStore(String keyword) {
        // http://192.168.1.137/mobile/index.php?act=store&op=search_store&keyword=%E6%97%97%E8%88%B0
        String getWord = "";
        try {
            getWord = "keyword=" + URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RemoteDataHandler.asyncDataStringGet(Constants.URL_QUERY_STORE + getWord, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject object = new JSONObject(json);
                        JSONArray ja = new JSONArray(object.getJSONArray("result").toString());
                        for(int i = 0; i < ja.length(); i++){
                            Gson gson = new Gson();
                            QueryStoreBean queryStoreBean = gson.fromJson(ja.getJSONObject(i).toString(), QueryStoreBean.class);
                            datas.add(queryStoreBean);
                        }
                        mMyAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        Toast.makeText(SearchStoreActivity.this, "json format error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void findView() {
        ibBack = (ImageButton) findViewById(R.id.btnBack);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        lv_store = (ListView) findViewById(R.id.lv_store);
        View view = ((ViewGroup)lv_store.getParent()).findViewById(R.id.tv_empty);
        lv_store.setEmptyView(view);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            finish();
        } else if(v.getId() == R.id.tvSearch){
            startActivity(new Intent(SearchStoreActivity.this, SearchActivity.class));
            finish();
        }
    }

    private class MyAdapter extends BaseAdapter{

        private View.OnClickListener listener;

        public void setOnClickListener(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(MyShopApplication.context, R.layout.view_search_store, null);
                holder = new ViewHolder();
                holder.btnEnter = (Button) convertView.findViewById(R.id.btn_enter);
                holder.ivStoreLabel = (ImageView) convertView.findViewById(R.id.iv_store_label);
                holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
                holder.iv3 = (ImageView) convertView.findViewById(R.id.iv3);
                holder.tvFavCount = (TextView) convertView.findViewById(R.id.tv_fav_count);
                holder.tvStoreName = (TextView) convertView.findViewById(R.id.tv_store_name);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.ll1 = (LinearLayout) convertView.findViewById(R.id.ll1);
                holder.ll2 = (LinearLayout) convertView.findViewById(R.id.ll2);
                holder.ll3 = (LinearLayout) convertView.findViewById(R.id.ll3);
                if(listener != null)
                    holder.btnEnter.setOnClickListener(listener);
                holder.ll_img_container = (LinearLayout) convertView.findViewById(R.id.ll_img_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            QueryStoreBean queryStoreBean = datas.get(position);
            // 将店铺id传入view
            holder.btnEnter.setTag(queryStoreBean.store_id);
            // 店铺图片
            if(!TextUtils.isEmpty(queryStoreBean.store_label)){
                Glide.with(SearchStoreActivity.this)
                        .load(queryStoreBean.store_label)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher)  //设置占位图
                        .error(R.drawable.ic_launcher)      //加载错误图
                        .into(holder.ivStoreLabel);
            } else {
                holder.ivStoreLabel.setImageResource(R.drawable.default_store_avatar);
            }

            // 店铺名称
            holder.tvStoreName.setText(queryStoreBean.store_name);
            // 店铺收藏人数
            holder.tvFavCount.setText(queryStoreBean.store_collect + "人收藏");
            // 暂时取消显示店铺分数
            /*queryStoreBean.store_deliverycredit
            holder.tvMark.setText();*/

            // 默认容器隐藏
            holder.ll_img_container.setVisibility(View.GONE);
            initImgContainer(holder);

            if(queryStoreBean.goods.size() > 0){
                holder.ll_img_container.setVisibility(View.VISIBLE);
                for(int i = 0; i < queryStoreBean.goods.size(); i++){
                    LinearLayout linearLayout = (LinearLayout) holder.ll_img_container.getChildAt(i);
                    linearLayout.setVisibility(View.VISIBLE);
                    ImageView iv = (ImageView) linearLayout.getChildAt(0);
                    // 这里主要是动态为imageview设置高度，接口返回图片宽高比例1：1，
                    // （屏幕宽度 - 最外层布局左右margin值）/ 商品个数 = 每个图片宽度 = 每个图片高度
                    iv.setLayoutParams(new LinearLayout.LayoutParams((
                            ScreenUtil.getScreenWidth(SearchStoreActivity.this) -
                                    ScreenUtil.dip2px(10)) / queryStoreBean.goods.size(),
                            (ScreenUtil.getScreenWidth(SearchStoreActivity.this) - ScreenUtil.dip2px(10))
                                    / queryStoreBean.goods.size()));
                    Glide.with(SearchStoreActivity.this)
                            .load(queryStoreBean.goods.get(i).goods_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.nc_icon_cart_white)  //设置占位图
                            .error(R.drawable.nc_icon_cart_white)      //加载错误图
                            .into(iv);
                    TextView tv = (TextView) linearLayout.getChildAt(1);
                    tv.setText("￥" + queryStoreBean.goods.get(i).goods_price);
                    // 将商品id传入view
                    linearLayout.setTag(queryStoreBean.goods.get(i).goods_id);
                    if(listener != null)
                        linearLayout.setOnClickListener(listener);
                }
            }

            return convertView;
        }

        private void initImgContainer(ViewHolder holder) {
            holder.ll1.setVisibility(View.GONE);
            holder.ll2.setVisibility(View.GONE);
            holder.ll3.setVisibility(View.GONE);
        }
    }

    private class ViewHolder{
        public ImageView ivStoreLabel, iv1, iv2, iv3;
        public TextView tvFavCount, tvStoreName, tv1, tv2, tv3;
        public Button btnEnter;
        public LinearLayout ll1, ll2, ll3, ll_img_container;
    }

}
