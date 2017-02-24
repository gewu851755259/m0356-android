package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.ComplainBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * 投诉列表
 * Created by jiangtao on 2017/1/11.
 */
public class ComplainListActivity extends Activity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private MyShopApplication app;

    private XListView lv_complain;

    private MyAdapter mMyAdapter;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, ComplainListActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        app = (MyShopApplication) getApplication();
        lv_complain = (XListView) findViewById(R.id.lv_complain);
        mMyAdapter = new MyAdapter();
        lv_complain.setAdapter(mMyAdapter);
        lv_complain.setPullLoadEnable(false);
        lv_complain.setXListViewListener(this);
        lv_complain.setOnItemClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadData();

    }

    // TODO: 2017/1/11 未做分页处理
    private void loadData() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("store_id", app.getStore_id());
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_COMPLAIN, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        JSONArray ja = new JSONArray(data.getJson());
                        ArrayList<ComplainBean> beanList = new ArrayList<ComplainBean>();
                        for(int i = 0; i < ja.length(); i++){
                            Gson gson = new Gson();
                            ComplainBean complainBean = gson.fromJson(ja.getJSONObject(i).toString(), ComplainBean.class);
                            beanList.add(complainBean);
                        }
                        mMyAdapter.addData(beanList);
                        lv_complain.stopRefresh();
                        lv_complain.setRefreshTime(SystemHelper.getTimeStr(System.currentTimeMillis() + ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder vh = (ViewHolder) view.getTag();
        ComplainBean bean = vh.bean;
        ComplainDetailActivity.start(this, bean.complain_id);
    }

    private class MyAdapter extends BaseAdapter{

        private List<ComplainBean> list;

        public MyAdapter() {
            list = new ArrayList<ComplainBean>();
        }

        public void addData(List<ComplainBean> data){
            this.list.clear();
            this.list.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = View.inflate(ComplainListActivity.this, R.layout.item_complain, null);
                vh.tvGoodsName = (TextView) convertView.findViewById(R.id.tv_complain_goods_name);
                vh.tvMemmberName = (TextView) convertView.findViewById(R.id.tv_complain_member_name);
                vh.tvState = (TextView) convertView.findViewById(R.id.tv_complain_state);
                vh.tvSub = (TextView) convertView.findViewById(R.id.tv_complain_subject);
                vh.tvTime = (TextView) convertView.findViewById(R.id.tv_complain_time);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            ComplainBean complainBean = list.get(position);
            List<ComplainBean.OrderInfoBean.ExtendOrderGoodsBean> extend_order_goods = complainBean.order_info.extend_order_goods;
            if (extend_order_goods.size() > 0) {
                vh.tvGoodsName.setText(extend_order_goods.get(0).goods_name);
            }

            vh.tvTime.setText(SystemHelper.getTimeStr(complainBean.complain_datetime));
            vh.tvSub.setText(complainBean.complain_subject_content);
            vh.tvMemmberName.setText(complainBean.accuser_name);
            vh.bean = complainBean;
            // 投诉状态(10-新投诉/20-投诉通过转给被投诉人/30-被投诉人已申诉/40-提交仲裁/99-已关闭)
            String state = "";
            if (complainBean.complain_state.equals("10")) {
                state = "新投诉";
            } else if (complainBean.complain_state.equals("20")){
                state = "投诉通过转给被投诉人";
            } else if (complainBean.complain_state.equals("30")){
                state = "被投诉人已申诉";
            } else if (complainBean.complain_state.equals("40")){
                state = "提交仲裁";
            } else if (complainBean.complain_state.equals("99")){
                state = "已关闭";
            }
            vh.tvState.setText(state);
            return convertView;
        }
    }

    private class ViewHolder{
        TextView tvGoodsName, tvMemmberName, tvState, tvTime, tvSub;
        ComplainBean bean;
    }

}
