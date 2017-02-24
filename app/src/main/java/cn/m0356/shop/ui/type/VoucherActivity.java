package cn.m0356.shop.ui.type;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2017/1/18.
 */
public class VoucherActivity extends Activity implements View.OnClickListener, XListView.IXListViewListener {

    private XListView lv;
    private MyShopApplication app;
    private MyAdapter mMyAdapter;
    private int curpage = 1;

    public static void start(Context context){
        Intent intent = new Intent(context, VoucherActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        app = (MyShopApplication) getApplication();

        lv = (XListView) findViewById(R.id.lv_voucher_list);
        mMyAdapter = new MyAdapter();
        mMyAdapter.setOnClickListener(this);
        lv.setAdapter(mMyAdapter);
        lv.setXListViewListener(this);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData(true);

    }

    private void loadData(final boolean isRefresh) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getLoginKey());
        RemoteDataHandler.asyncPostDataString(Constants.URL_REDPACKET_LIST + curpage, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.isHasMore())
                        lv.setPullLoadEnable(true);
                    else
                        lv.setPullLoadEnable(false);
                    parseJson(data.getJson(), isRefresh);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String updataTime = sdf.format(new Date(System.currentTimeMillis()));
                    lv.setRefreshTime(updataTime);
                    lv.stopRefresh();
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });

    }



    private void parseJson(String json, boolean isRefresh) {
        try {
            JSONArray ja = new JSONObject(json).getJSONArray("rptList");
            List<VoucherRedpacketBean> data = new ArrayList<VoucherRedpacketBean>();
            for(int i = 0; i < ja.length(); i++){
                Gson gson = new Gson();
                VoucherRedpacketBean bean = gson.fromJson(ja.getJSONObject(i).toString(), VoucherRedpacketBean.class);
                data.add(bean);
            }
            if(isRefresh){
                mMyAdapter.update(data);
                curpage = 1;
            } else {
                mMyAdapter.addData(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_voucher){
            String id = (String) v.getTag();
            saveRedpacket(id);
        }
    }

    private void saveRedpacket(String id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getLoginKey());
        map.put("tid", id);
        map.put("member_id", app.getMemberID());
        map.put("member_name", app.getUserName());
        RemoteDataHandler.asyncPostDataString(Constants.URL_REDPACKET_SVAE, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.getJson().equals("1"))
                        Toast.makeText(VoucherActivity.this, "领取成功", Toast.LENGTH_SHORT).show();
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        curpage++;
        loadData(false);
    }

    private class MyAdapter extends BaseAdapter{

        private List<VoucherRedpacketBean> data;
        private View.OnClickListener mListener;

        public MyAdapter() {
            data = new ArrayList<VoucherRedpacketBean>();
        }

        public void setOnClickListener(View.OnClickListener listener){
            this.mListener = listener;
        }

        public void update(List<VoucherRedpacketBean> data){
            this.data.clear();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView == null){
                convertView = View.inflate(VoucherActivity.this, R.layout.item_voucher, null);
                vh = new ViewHolder();

                vh.tvLimit = (TextView) convertView.findViewById(R.id.tv_voucher_limit);
                vh.tvTime = (TextView) convertView.findViewById(R.id.tv_voucher_time);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_voucher_title);
                vh.tvUsed = (TextView) convertView.findViewById(R.id.tv_voucher_used);
                vh.tvAmount = (TextView) convertView.findViewById(R.id.tv_voucher_amount);
                vh.btn = (Button) convertView.findViewById(R.id.btn_voucher);
                vh.iv = (ImageView) convertView.findViewById(R.id.iv_voucher_img);
                vh.tvLeve = (TextView) convertView.findViewById(R.id.tv_voucher_leve);
                if(mListener != null)
                    vh.btn.setOnClickListener(mListener);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            VoucherRedpacketBean voucherRedpacketBean = data.get(position);
            vh.tvTitle.setText(voucherRedpacketBean.rpacket_t_title);
            vh.tvLimit.setText("购物满" + voucherRedpacketBean.rpacket_t_limit + "元可用");
            vh.tvTime.setText("有效期：" + voucherRedpacketBean.rpacket_t_start_date
                +"~" + voucherRedpacketBean.rpacket_t_end_date);
            vh.tvAmount.setText(voucherRedpacketBean.rpacket_t_price);
            vh.tvLeve.setText(voucherRedpacketBean.rpacket_t_mgradelimittext);
            vh.tvUsed.setText(voucherRedpacketBean.rpacket_t_giveout + "人已领取");
            //带缓存的商品图
            /*Glide.with(VoucherActivity.this)
                    .load(voucherRedpacketBean.rpacket_t_customimg_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.triangle_red)  //设置占位图
                    .error(R.drawable.triangle_red)      //加载错误图
                    .into(vh.iv);*/
            vh.btn.setTag(voucherRedpacketBean.rpacket_t_id);

            return convertView;
        }

        public void addData(List<VoucherRedpacketBean> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder{
        public TextView tvTitle, tvLimit, tvUsed, tvTime, tvAmount, tvLeve;
        public Button btn;
        public ImageView iv;
    }

}
