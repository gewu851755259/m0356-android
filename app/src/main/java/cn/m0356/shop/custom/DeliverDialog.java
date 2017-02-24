package cn.m0356.shop.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import cn.m0356.shop.R;
import cn.m0356.shop.bracode.core.QRCodeReader;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/12/8.
 */
public class DeliverDialog extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup rg_deliver;
    private EditText et_deliver_explain, et_code;
    private TextView tvMemberInfo, tvExpressName;
    private LinearLayout llCode;
    private RadioButton rb;
    private RadioButton rb_1;
    private Button btn;
    private OnOrderSendListener mListener;
    private String key;
    private String orderId;
    private String expressId;
    private String expressCode;

    public static DeliverDialog newInstance(/*String orderId, String storeId, String key*/) {
        DeliverDialog f = new DeliverDialog();

        /*Bundle args = new Bundle();
        args.putString("orderId", orderId);
        args.putString("storeId", storeId);
        args.putString("key", key);
        f.setArguments(args);*/
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_deliver_name, container);
        rg_deliver = (RadioGroup) view.findViewById(R.id.rg_deliver);
        et_deliver_explain = (EditText) view.findViewById(R.id.et_deliver_explain);
        et_code = (EditText) view.findViewById(R.id.et_deliver_code);
        rb = (RadioButton) view.findViewById(R.id.rb_other);
        tvMemberInfo = (TextView) view.findViewById(R.id.tv_member_info);
        btn = (Button) view.findViewById(R.id.btn_ok);
        llCode = (LinearLayout) view.findViewById(R.id.ll_code);
        tvExpressName = (TextView) view.findViewById(R.id.tv_name);
        rb_1 = (RadioButton) view.findViewById(R.id.rb_1);
        btn.setOnClickListener(this);
        rg_deliver.setOnCheckedChangeListener(this);
        initData();
        return view;
    }

    private void initData() {
        /*Bundle arguments = getArguments();
        String orderId = arguments.getString("orderId");
        String storeId = arguments.getString("storeId");
        String key = arguments.getString("key");
        loadAddressInfo(orderId, storeId, key);*/

    }

    private void loadAddressInfo(String orderId, String storeId, String key) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("order_id", orderId);
        map.put("store_id", storeId);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){

                    try {
                        JSONObject obj = new JSONObject(data.getJson());
                        JSONObject extend_order_common = obj.getJSONObject("extend_order_common");
                        JSONObject reciver_info = extend_order_common.getJSONObject("reciver_info");
                        String reciver_name = extend_order_common.getString("reciver_name");
                        String phone = reciver_info.getString("phone");
                        String address = reciver_info.getString("address");
                        tvMemberInfo.setText(reciver_name + " " + phone + " " + address);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(getActivity(), data.getJson());
                }
            }
        });
    }

    public void setListener(OnOrderSendListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_ok){
            deliverSend();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_other){
            //  无需物流运输服务
            llCode.setVisibility(View.GONE);
        } else {
            llCode.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void show(FragmentManager fragmentManager, String deliver, String id, String store_id, String seller_key) {
        this.show(fragmentManager, deliver);
        this.key = seller_key;
        this.orderId = id;

        // 重置一些变量
        expressId = "-1";
        expressCode = "";

        loadAddressInfo(id, store_id, seller_key);
        loadExpressInfo(key);
    }

    public interface OnOrderSendListener{
        void send(String reason);
    }

    /**
     * 发货
     */
    private void deliverSend(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("order_id", orderId);
        map.put("deliver_explain", et_deliver_explain.getText().toString());

        if(rb_1.isChecked() && !TextUtils.isEmpty(et_code.getText().toString())){
            if(TextUtils.isEmpty(expressCode) || expressId.equals("-1")){
                Toast.makeText(MyShopApplication.context, "未设置默认物流信息信息，请前往后台设置", Toast.LENGTH_LONG).show();
                return;
            }
            map.put("shipping_code", expressCode);
            map.put("shipping_express_id", expressId);
        }

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_ORDER_DELIVER_SEND, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){

                    try {
                        if(data.getJson().equals("1")){
                            Toast.makeText(MyShopApplication.context, "发货成功", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject object = new JSONObject(data.getJson());
                            if(object.has("error")){
                                Toast.makeText(MyShopApplication.context, object.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(getActivity(), data.getJson());
                }
                DeliverDialog.this.dismiss();
            }
        });
    }

    /**
     * 加载默认物流信息
     * @param key
     */
    private void loadExpressInfo(String key){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_ORDER_DEFAULT_EXPRESS, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){

                    try {
                        JSONArray ary = new JSONObject(data.getJson()).getJSONArray("express_array");
                        if(ary.length() < 0){
                            Toast.makeText(MyShopApplication.context, "未设置默认物流公司", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject jsonObject = ary.getJSONObject(0);
                        String expressName = jsonObject.getString("e_name");
                        String expressCode = jsonObject.getString("e_code");
                        String id = jsonObject.getString("id");
                        tvExpressName.setText(expressName);
                        DeliverDialog.this.expressId = id;
                        DeliverDialog.this.expressCode = expressCode;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(getActivity(), data.getJson());
                }
            }
        });
    }


}
