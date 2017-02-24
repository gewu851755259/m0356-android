package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerConsultBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * 咨询详情
 * Created by jiangtao on 2017/1/10.
 */
public class ConsultDetailActivity extends Activity implements View.OnClickListener {

    private MyShopApplication app;

    private TextView tvGoodName, tvUserName, tvConsultTime, tvConsultContent, tvReplyContent
            , tv_drop;
    private Button btnReply;
    private EditText etReplyContent;
    private LinearLayout llReply, llNoReply;
    private String consult_id;

    public static void start(Context context, SellerConsultBean bean){
        Intent intent = new Intent(context, ConsultDetailActivity.class);
        intent.putExtra("data", bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_detail);
        initView();
        initData();
    }

    private void initData() {
        app = (MyShopApplication) getApplication();
        SellerConsultBean bean = getIntent().getParcelableExtra("data");
        tvGoodName.setText(bean.goods_name);
        String name = bean.member_name;
        this.consult_id = bean.consult_id;
        // 匿名咨询
        if(bean.isanonymous.equals("1")){
            name = name.charAt(0) + "***";
        }
        tvUserName.setText(name);
        tvConsultTime.setText(SystemHelper.getTimeStr(bean.consult_addtime));
        tvConsultContent.setText(bean.consult_content);
        if(TextUtils.isEmpty(bean.consult_reply)){
            // 客服回复为空
            llNoReply.setVisibility(View.VISIBLE);
            llReply.setVisibility(View.GONE);
            btnReply.setOnClickListener(this);
        } else {
            llReply.setVisibility(View.VISIBLE);
            llNoReply.setVisibility(View.GONE);
            tvReplyContent.setText(bean.consult_reply);
        }
    }

    private void initView() {
        tvConsultContent = (TextView) findViewById(R.id.tv_consult_content);
        tvConsultTime = (TextView) findViewById(R.id.tv_consult_time);
        tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
        tvReplyContent = (TextView) findViewById(R.id.tv_reply_content);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        btnReply = (Button) findViewById(R.id.btn_reply);
        etReplyContent = (EditText) findViewById(R.id.et_reply_content);
        llNoReply = (LinearLayout) findViewById(R.id.ll_no_reply);
        llReply = (LinearLayout) findViewById(R.id.ll_reply);
        tv_drop = (TextView) findViewById(R.id.tv_drop);
        tv_drop.setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_reply){
            String content = etReplyContent.getText().toString();
            if(TextUtils.isEmpty(content)){
                Toast.makeText(app, "请填写回复内容", Toast.LENGTH_SHORT).show();
            } else {
                reply(content);
            }
        } else if(v.getId() == R.id.tv_drop){
            showDropDialog();
        }
    }

    private void showDropDialog() {
        new AlertDialog.Builder(this).setMessage("确定要删除？")
                .setTitle("提示")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        consultDrop();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void consultDrop() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("id", consult_id);
        map.put("key", app.getSeller_key());
        map.put("store_id", app.getStore_id());

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_DROP, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.getJson().equals("1")){
                        Toast.makeText(app, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    private void reply(String content) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("consult_id", consult_id);
        map.put("key", app.getSeller_key());
        map.put("store_id", app.getStore_id());
        map.put("content", content);

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_REPLY, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.getJson().equals("1")){
                        Toast.makeText(app, "回复成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }
}
