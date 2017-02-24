package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.RefundBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/12/19.
 */
public class RefundReturnDetailActivity extends Activity {
    private RefundBean bean;
    private LinearLayout ll_container;
    private MyShopApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_return_detail);
        bean = getIntent().getParcelableExtra("data");
        app = (MyShopApplication) getApplication();
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initBuyerInfo();
        if(bean.seller_state.equals("1")){ // 待审核
            initCheckLayout();
        } else if(bean.seller_state.equals("2") || bean.seller_state.equals("3")){ // 同意 或不同意
            initSellerAgree();
            initAdmin();
        }
    }

    private void initCheckLayout() {
        View view = View.inflate(this, R.layout.layout_seller_check, null);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_group);
        final RadioButton rb1 = (RadioButton) view.findViewById(R.id.rg_b1);
        RadioButton rb2 = (RadioButton) view.findViewById(R.id.rg_b2);
        Button btn = (Button) view.findViewById(R.id.btn_ok);
        final EditText et_msg = (EditText) view.findViewById(R.id.et_meg);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seller_state = rb1.isChecked() ? "2" : "3";
                String seller_message = et_msg.getText().toString();
                submit(seller_state, seller_message);
            }
        });
        ll_container.addView(view);

    }

    private void submit(String seller_state, String seller_message) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("seller_state", seller_state);
        map.put("seller_message", seller_message);
        map.put("store_id", app.getStore_id());
        map.put("refund_id", bean.refund_id);
        map.put("key", app.getSeller_key());

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_REFUND_OP, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    if(data.getJson().equals("1")){ // 提交成功
                        finish();
                    }
                }else {
                    ShopHelper.showApiError(RefundReturnDetailActivity.this, data.getJson());
                }
            }
        });
    }

    private void initAdmin() {
        View view = View.inflate(this, R.layout.layout_admin, null);
        TextView tv_state = (TextView) view.findViewById(R.id.tv_admin_state);
        if(bean.refund_state.equals("3")){ // 已完成
            tv_state.setText("已完成");
            TextView tv_meg = (TextView) view.findViewById(R.id.tv_admin_meg);
            tv_meg.setText(bean.admin_message);
        } else if(bean.refund_state.equals("1") || bean.refund_state.equals("2")){ // 待处理
            tv_state.setText("待处理");
            view.findViewById(R.id.ll_admin_meg).setVisibility(View.GONE);
        }
        ll_container.addView(view);
    }

    private void initSellerAgree() {
        View view = View.inflate(this, R.layout.layout_seller, null);
        TextView tv_meg = (TextView) view.findViewById(R.id.tv_seller_meg);
        TextView tv_state = (TextView) view.findViewById(R.id.tv_seller_state);
        tv_meg.setText(bean.seller_message);
        tv_state.setText(bean.seller_state.equals("2") ? "同意" : "不同意");
        ll_container.addView(view);

    }

    private void initBuyerInfo() {
        View view = View.inflate(this, R.layout.layout_buyer, null);
        TextView tv_by_amount = (TextView) view.findViewById(R.id.tv_by_amount);
        TextView tv_by_message = (TextView) view.findViewById(R.id.tv_by_message);
        TextView tv_by_nickname = (TextView) view.findViewById(R.id.tv_by_nickname);
        TextView tv_by_reason = (TextView) view.findViewById(R.id.tv_by_reason);
        TextView tv_by_sn = (TextView) view.findViewById(R.id.tv_by_sn);
        tv_by_amount.setText(bean.refund_amount);
        tv_by_message.setText(bean.buyer_message);
        tv_by_nickname.setText(bean.buyer_name);
        tv_by_reason.setText(bean.reason_info);
        tv_by_sn.setText(bean.refund_sn);
        ll_container.addView(view);
    }
}
