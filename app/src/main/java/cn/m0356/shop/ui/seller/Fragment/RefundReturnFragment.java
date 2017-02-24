package cn.m0356.shop.ui.seller.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.RefundBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.seller.ABaseFragment;
import cn.m0356.shop.ui.seller.RefundReturnActivity;
import cn.m0356.shop.ui.seller.RefundReturnDetailActivity;

/**
 * Created by jiangtao on 2016/12/9.
 */
public class RefundReturnFragment extends ABaseFragment implements XListView.IXListViewListener {

    private XListView mXListView;
    private MyAdapter mMyAdapter;
    private String key;
    private String storeId;
    private String url;
    private int page = 0;
    private ProgressDialog dialog;
    private MyShopApplication app;

    public static RefundReturnFragment newInstance(String type, String key, String storeId) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("key", key);
        args.putString("storeId", storeId);
        RefundReturnFragment fragment = new RefundReturnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_refund_return, null);
        return view;
    }

    @Override
    public void initData() {
        app = (MyShopApplication) getActivity().getApplication();
        mXListView = (XListView) mRootView.findViewById(R.id.lv_list);
        mMyAdapter = new MyAdapter();
        mXListView.setAdapter(mMyAdapter);
        mXListView.setXListViewListener(this);
        String type = getArguments().getString("type");
        dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);

        if(type.equals(RefundReturnActivity.TYPE_REFUND)){
            url = Constants.SellerManager.URL_SELLER_ORDER_REFUND;
        } else if(type.equals(RefundReturnActivity.TYPE_REFUND_LOCK)){
            url = Constants.SellerManager.URL_SELLER_ORDER_REFUND_LOCK;
        } else if(type.equals(RefundReturnActivity.TYPE_RETURN)){
            url = Constants.SellerManager.URL_SELLER_ORDER_RETURN;
        } else if(type.equals(RefundReturnActivity.TYPE_RETURN_LOCK)){
            url = Constants.SellerManager.URL_SELLER_ORDER_RETURN_LOCK;
        }
        Bundle arguments = getArguments();
        key = arguments.getString("key");
        storeId = arguments.getString("storeId");

        loadListData(url, key, storeId, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //loadListData(url, key, storeId, true);
    }

    private void loadListData(String url, String key, String storeId, final boolean isRefresh) {
        dialog.show();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("store_id", storeId);
        RemoteDataHandler.asyncPostDataString(url + "&curpage=" + page, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                dialog.dismiss();
                if(data.getCode() == HttpStatus.SC_OK){
                    try {
                        if(data.isHasMore()){
                            mXListView.setPullLoadEnable(true);
                        } else {
                            mXListView.setPullLoadEnable(false);
                        }
                        if(isRefresh) {
                            mMyAdapter.clear();
                            mXListView.stopRefresh();
                            mXListView.setRefreshTime(SystemHelper.getTimeStr(System.currentTimeMillis() + ""));
                        }
                        parseData(data.getJson());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });
    }

    private void parseData(String json) throws JSONException {
        ArrayList<RefundBean> list = new ArrayList<RefundBean>();
        JSONArray ary = getJSONArray(json);
        for(int i = 0; i < ary.length(); i++){
            /*RefundBean bean = new RefundBean();
            JSONObject ob = ary.getJSONObject(i);
            bean.add_time = ob.getString("add_time");
            bean.buyer_name = ob.getString("buyer_name");
            bean.goods_name = ob.getString("goods_name");
            bean.order_sn = ob.getString("order_sn");
            bean.refund_amount = ob.getString("refund_amount");
            bean.refund_sn = ob.getString("refund_sn");
            bean.refund_state = ob.getString("refund_state");
            bean.seller_state = ob.getString("seller_state");*/
            Gson gson = new Gson();
            RefundBean refundBean = gson.fromJson(ary.getJSONObject(i).toString(), RefundBean.class);
            list.add(refundBean);
        }
        mMyAdapter.addData(list);
    }

    private JSONArray getJSONArray(String json) throws JSONException {

        String type = getArguments().getString("type");
        String key = "";
        if(type.equals(RefundReturnActivity.TYPE_REFUND) || type.equals(RefundReturnActivity.TYPE_REFUND_LOCK)){
            key = "refund_list";
        } else if(type.equals(RefundReturnActivity.TYPE_RETURN) || type.equals(RefundReturnActivity.TYPE_RETURN_LOCK)){
            key = "return_list";
        }
        return new JSONObject(json).getJSONArray(key);
    }

    @Override
    public void onRefresh() {
        loadListData(url, key, storeId, true);
    }

    @Override
    public void onLoadMore() {
        page++;
        loadListData(url, key, storeId, false);
    }


    private class MyAdapter extends BaseAdapter implements View.OnClickListener {

        private List<RefundBean> list;

        public MyAdapter() {
            list = new ArrayList<RefundBean>();
        }

        public void addData(List<RefundBean> bean){
            list.addAll(bean);
            notifyDataSetChanged();
        }

        public void clear(){
            list.clear();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public RefundBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            RefundBean refundBean = list.get(position);
            if(convertView == null){
                convertView = View.inflate(MyShopApplication.context, R.layout.item_refund, null);
                vh = new ViewHolder();
                vh.adminState = (TextView) convertView.findViewById(R.id.tv_refund_admin_state);
                vh.sellerState = (TextView) convertView.findViewById(R.id.tv_refund_seller_state);
                vh.buyerName = (TextView) convertView.findViewById(R.id.tv_refund_buyer_name);
                vh.goodName = (TextView) convertView.findViewById(R.id.tv_refund_good_name);
                vh.time = (TextView) convertView.findViewById(R.id.tv_refund_time);
                vh.btnReceive = (Button) convertView.findViewById(R.id.btn_receive);
                // xlistview 加入 headeview item 点击事件position 未处理
                convertView.setOnClickListener(this);
                vh.btnReceive.setOnClickListener(this);
                // 将数据放置textview中
                vh.buyerName.setTag(refundBean);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
                vh.buyerName.setTag(refundBean);
            }
            vh.buyerName.setText("卖家会员名：" + refundBean.buyer_name);
            vh.goodName.setText("商品名称：" + refundBean.goods_name);
            if(!TextUtils.isEmpty(refundBean.add_time)){
                String time = SystemHelper.getTimeStr(refundBean.add_time);
                vh.time.setText("申请时间：" + time);
            }
            if(refundBean.seller_state.equals("1")){
                vh.sellerState.setText("处理状态：待审核");
            } else if(refundBean.seller_state.equals("2")){
                vh.sellerState.setText("处理状态：同意");
            } else if(refundBean.seller_state.equals("3")){
                vh.sellerState.setText("处理状态：不同意");
            }

            if(refundBean.refund_state.equals("1")){
                vh.adminState.setText("平台确认：处理中");
            } else if(refundBean.refund_state.equals("2")){
                vh.adminState.setText("平台确认：待管理员处理");
            } else if(refundBean.refund_state.equals("3")){
                vh.adminState.setText("平台确认：已完成");
            }

            // 判断是否显示 收货 按钮
            if(refundBean.seller_state.equals("2") && refundBean.return_type.equals("2") && refundBean.goods_state.equals("2")){
                vh.btnReceive.setVisibility(View.VISIBLE);
                vh.btnReceive.setTag(refundBean);
            } else{
                vh.btnReceive.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public void onClick(View v) {
            if(v instanceof Button){
                // 收货
                receive(v);
                return;
            }
            RefundBean bean = (RefundBean) v.findViewById(R.id.tv_refund_buyer_name).getTag();
            Intent intent = new Intent(getActivity(), RefundReturnDetailActivity.class);
            intent.putExtra("data", bean);
            startActivity(intent);
        }

        private void receive(View v) {
            RefundBean bean = (RefundBean) v.getTag();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("store_id", app.getStore_id());
            map.put("return_id", bean.refund_id);
            map.put("key", app.getSeller_key());
            map.put("return_type", bean.return_type);
            RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_RETURN_RECEIVE, map, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    if(data.getCode() == HttpStatus.SC_OK){
                        if(data.getJson().equals("1")){
                            Toast.makeText(MyShopApplication.context, "收货成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyShopApplication.context, "其他错误", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                    }
                }
            });
        }
    }

    private class ViewHolder{
        TextView time, buyerName, sellerState, adminState, goodName;
        Button btnReceive;
    }

}
