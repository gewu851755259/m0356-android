package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerConsultBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyCustomProgressDialog;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * 咨询列表
 * Created by jiangtao on 2017/1/9.
 */
public class ConsultListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, XListView.IXListViewListener {

    public static final String CONSULT_TYPE_ALL = "";
    // 未回复
    public static final String CONSULT_TYPE_TO_REPLY = "to_reply";
    // 已回复咨询
    public static final String CONSULT_TYPE_REPLIED = "replied";

    private MyShopApplication app;

    private XListView lv;
    private Button btnAll, btnNoReply, btnReply;

    // 加载中对话框
    private MyCustomProgressDialog proDialog;

    private MyAdapter mAdapter;

    private String type;

    public static void start(Context context, String type){
        Intent intent = new Intent(context, ConsultListActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_list);
        app = (MyShopApplication) getApplication();
        lv = (XListView) findViewById(R.id.lv_consult);
        lv.setPullLoadEnable(false);
        lv.setXListViewListener(this);
        mAdapter = new MyAdapter();
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);

        btnAll = (Button) findViewById(R.id.btnAll);
        btnNoReply = (Button) findViewById(R.id.btnNoReply);
        btnReply = (Button) findViewById(R.id.btnReply);

        btnNoReply.setOnClickListener(this);
        btnReply.setOnClickListener(this);
        btnAll.setOnClickListener(this);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        proDialog = new MyCustomProgressDialog();
        String type = getIntent().getStringExtra("type");
        this.type = type;
        loadList(type);

    }

    // TODO: 2017/1/9 未做分页处理
    private void loadList(String type) {

        changeTab(type);

        proDialog.show(getFragmentManager(), "progress");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("store_id", app.getStore_id());
        if(!TextUtils.isEmpty(type)){
            map.put("type", type);
        }
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_LIST, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        ArrayList<SellerConsultBean> list = new ArrayList<SellerConsultBean>();
                        JSONArray ja = new JSONArray(data.getJson());
                        for(int i = 0; i < ja.length(); i++){
                            Gson gson = new Gson();
                            SellerConsultBean sellerConsultBean = gson.fromJson(ja.getJSONObject(i).toString(), SellerConsultBean.class);
                            list.add(sellerConsultBean);
                        }
                        mAdapter.addData(list);
                        lv.stopRefresh();
                        lv.setRefreshTime(SystemHelper.getTimeStr(System.currentTimeMillis() + ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
                proDialog.dismiss();
            }
        });
    }

    private void changeTab(String type) {
        clearState();
        this.type = type;
        if(type.equals(CONSULT_TYPE_ALL)){
            btnAll.setActivated(true);
        } else if(type.equals(CONSULT_TYPE_REPLIED)){
            btnReply.setActivated(true);
        } else if(type.equals(CONSULT_TYPE_TO_REPLY)){
            btnNoReply.setActivated(true);
        }
    }

    private void clearState() {
        btnReply.setActivated(false);
        btnNoReply.setActivated(false);
        btnAll.setActivated(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnAll:
                loadList(CONSULT_TYPE_ALL);
                break;
            case R.id.btnNoReply:
                loadList(CONSULT_TYPE_TO_REPLY);
                break;
            case R.id.btnReply:
                loadList(CONSULT_TYPE_REPLIED);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //SellerConsultBean item = mAdapter.getItem(position);
        View tagView = view.findViewById(R.id.tv_goods_name);
        SellerConsultBean item = (SellerConsultBean) tagView.getTag();
        ConsultDetailActivity.start(this, item);
    }

    @Override
    public void onRefresh() {
        loadList(type);
    }

    @Override
    public void onLoadMore() {}

    private class MyAdapter extends BaseAdapter {

        private ArrayList<SellerConsultBean> list;

        public MyAdapter() {
            list = new ArrayList<SellerConsultBean>();
        }

        public void addData(ArrayList<SellerConsultBean> list){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public SellerConsultBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView == null){
                convertView = View.inflate(ConsultListActivity.this, R.layout.item_consult, null);
                vh = new ViewHolder();
                vh.tvGoodName = (TextView) convertView.findViewById(R.id.tv_goods_name);
                vh.tvConsultReply = (TextView) convertView.findViewById(R.id.tv_consult_reply);
                vh.tvConsultContent = (TextView) convertView.findViewById(R.id.tv_consult_content);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            SellerConsultBean item = getItem(position);
            vh.tvGoodName.setText(item.goods_name);
            vh.tvConsultContent.setText(item.consult_content);
            vh.tvGoodName.setTag(item);
            if(!TextUtils.isEmpty(item.consult_reply)){
                vh.tvConsultReply.setText(item.consult_reply);
            }
            return convertView;
        }
    }

    class ViewHolder{
        TextView tvGoodName, tvConsultReply, tvConsultContent;
    }

}
