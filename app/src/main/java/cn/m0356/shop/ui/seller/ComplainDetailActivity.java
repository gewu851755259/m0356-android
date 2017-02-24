package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.ComplainDetailBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * 投诉详情页
 * Created by jiangtao on 2017/1/12.
 */
public class ComplainDetailActivity extends Activity implements View.OnClickListener {

    private MyShopApplication app;
    private TextView tvState, tvSub, tvImg, tvTime, tvContent;
    private LinearLayout ll_mes_container;
    private String complain_id;

    public static void start(Context context, String complain_id){
        Intent intent = new Intent(context, ComplainDetailActivity.class);
        intent.putExtra("complain_id", complain_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_detail);

        app = (MyShopApplication) getApplication();
        complain_id = getIntent().getStringExtra("complain_id");
        initView();
        loadData(complain_id);

    }

    private void initView() {
        tvState = (TextView) findViewById(R.id.tv_complain_detail_state);
        tvSub = (TextView) findViewById(R.id.tv_complain_detail_sub);
        tvImg = (TextView) findViewById(R.id.tv_complain_detail_img);
        tvTime = (TextView) findViewById(R.id.tv_complain_detail_time);
        tvContent = (TextView) findViewById(R.id.tv_complain_detail_content);
        ll_mes_container = (LinearLayout) findViewById(R.id.ll_mes_container);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadData(String complain_id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("store_id", app.getStore_id());
        map.put("key", app.getSeller_key());
        map.put("complain_id", complain_id);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_COMPLAIN_DETAIL, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    String json = data.getJson();

                    Gson gson = new Gson();
                    ComplainDetailBean complainDetailBean = gson.fromJson(json, ComplainDetailBean.class);
                    updateUI(complainDetailBean);

                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    private void updateUI(ComplainDetailBean complainDetailBean) {

        tvState.setText(complainDetailBean.complain_info.complain_state_text);
        tvContent.setText(complainDetailBean.complain_info.complain_content);
        tvSub.setText(complainDetailBean.complain_info.complain_subject_content);
        tvTime.setText(SystemHelper.getTimeStr(complainDetailBean.complain_info.complain_datetime));
        if(!TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic1)
                || !TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic2)
                || !TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic3)){
            tvImg.setText("请通过pc端浏览器查看");
        } else {
            tvImg.setText("暂无图片");
        }

        // 通过申诉时间 判断卖家是否申诉
        /*if(TextUtils.isEmpty(complainDetailBean.complain_info.appeal_datetime)
                && TextUtils.isEmpty(complainDetailBean.complain_info.final_handle_datetime)){*/
        if(complainDetailBean.complain_info.complain_state.equals("20")){
            // 未申诉
            View view = View.inflate(ComplainDetailActivity.this, R.layout.view_complain_appeal_detail, null);
            ll_mes_container.addView(view);
            initAppealDetailView(view);

        } else {
            // 已申诉

            if(complainDetailBean.complain_info.complain_state.equals("99")){
                initFinalMessage(complainDetailBean);
                return;
            }

            View view = View.inflate(ComplainDetailActivity.this, R.layout.view_complain_appeal_message, null);
            ll_mes_container.addView(view);
            initAppealMessageView(view, complainDetailBean);
        }
    }

    private void initFinalMessage(ComplainDetailBean complainDetailBean) {

        View view = View.inflate(ComplainDetailActivity.this, R.layout.view_complain_result, null);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_complain_final_message);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_complain_final_time);
        tvMsg.setText(complainDetailBean.complain_info.final_handle_message);
        tvTime.setText(SystemHelper.getTimeStr(complainDetailBean.complain_info.final_handle_datetime));
        ll_mes_container.addView(view);
    }

    // 初始化申诉信息view
    private void initAppealMessageView(View view, ComplainDetailBean complainDetailBean) {
        Button btnSubmit = (Button) view.findViewById(R.id.btn_complain_appeal_submit);
        Button btnRefresh = (Button) view.findViewById(R.id.btn_complain_appeal_refresh);
        Button btnHandle = (Button) view.findViewById(R.id.btn_complain_appeal_handle);

        final EditText etContent = (EditText) view.findViewById(R.id.et_complain_appeal_content);

        TextView tvContent = (TextView) view.findViewById(R.id.tv_complain_appeal_content);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_complain_appeal_time);
        TextView tvImg = (TextView) view.findViewById(R.id.tv_complain_appeal_img);
        // 添加message容器
        final LinearLayout llMessage = (LinearLayout) view.findViewById(R.id.ll_complain_appeal_message);
        // 发送message容器
        LinearLayout sendContainer = (LinearLayout) view.findViewById(R.id.et_complain_appeal_send_container);
        // 按钮容器
        LinearLayout btnContainer = (LinearLayout) view.findViewById(R.id.ll_complain_appeal_btn_container);

        tvContent.setText(complainDetailBean.complain_info.complain_content);
        tvTime.setText(SystemHelper.getTimeStr(complainDetailBean.complain_info.complain_datetime));
        if(!TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic1)
                || !TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic2)
                || !TextUtils.isEmpty(complainDetailBean.complain_info.complain_pic3)){
            tvImg.setText("请通过pc端浏览器查看");
        } else {
            tvImg.setText("暂无图片");
        }

        if(complainDetailBean.complain_info.complain_state.equals("99")){ // 已关闭
            sendContainer.setVisibility(View.GONE);
            btnContainer.setVisibility(View.GONE);
        }
//        btnHandle.
        btnHandle.setOnClickListener(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                if(TextUtils.isEmpty(content))
                    Toast.makeText(ComplainDetailActivity.this, "请填写信息", Toast.LENGTH_SHORT).show();
                else
                    publishMessage(content, llMessage);

            }
        });
        btnRefresh.setTag(llMessage);
        btnRefresh.setOnClickListener(this);
        loadAppealMessage(llMessage);

    }

    private void loadAppealMessage(final LinearLayout llMessage) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("complain_id", complain_id);
        map.put("store_id", app.getStore_id());
        map.put("key", app.getSeller_key());

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_COMPLAIN_TALK, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        JSONArray ja = new JSONArray(data.getJson());
                        llMessage.removeAllViews();
                        if(ja.length() <= 0){ // 如果没有消息
                            addEmptyTextView(llMessage);
                        } else{
                            // 有消息 输出
                            addMessageToContainer(ja, llMessage);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else{
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    private void addMessageToContainer(JSONArray ja, LinearLayout llMessage) throws JSONException {
        for(int i = 0; i < ja.length(); i++){
            JSONObject jsonObject = ja.getJSONObject(i);
            String message = jsonObject.getString("talk");
            TextView tv = new TextView(ComplainDetailActivity.this);
            tv.setText(message);
            llMessage.addView(tv);
        }
        Toast.makeText(ComplainDetailActivity.this, "消息输出成功", Toast.LENGTH_SHORT).show();
    }

    // 添加消息为空的提示TextView
    private void addEmptyTextView(LinearLayout llMessage) {
        TextView tv = new TextView(ComplainDetailActivity.this);
        tv.setText("目前没有对话");
        llMessage.addView(tv);
    }

    // 初始化申诉详情view
    private void initAppealDetailView(View view) {
        Button btn = (Button) view.findViewById(R.id.btn_complain_appeal_submit);
        final EditText etContent = (EditText) view.findViewById(R.id.et_complain_appeal_content);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                if(TextUtils.isEmpty(content))
                    Toast.makeText(ComplainDetailActivity.this, "请填写申述内容", Toast.LENGTH_SHORT).show();
                saveAppealContent(content);
            }
        });
    }

    // 提交申诉
    private void saveAppealContent(String content) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("appeal_message", content);
        map.put("complain_id", complain_id);
        map.put("key", app.getSeller_key());
        map.put("store_id", app.getStore_id());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_COMPLAIN_APPEAL_SAVE, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                     if(data.getJson().equals("1")) {
                         Toast.makeText(ComplainDetailActivity.this, "提交申诉成功", Toast.LENGTH_SHORT).show();
                         finish();
                     }
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });

    }

    private void submitHandle(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("complain_id", complain_id);
        map.put("store_id", app.getStore_id());
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_COMPLAIN_APPLY_HANDLE, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.getJson().equals("1"))
                        Toast.makeText(ComplainDetailActivity.this, "提交仲裁成功", Toast.LENGTH_SHORT).show();
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }

    private void publishMessage(String content, final LinearLayout ll){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("complain_id", complain_id);
        map.put("complain_talk", content);
        map.put("store_id", app.getStore_id());
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STATISTICS_COMPLAIN_PUBLISH_MESSAGE, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    if(data.getJson().equals("1"))
                        Toast.makeText(ComplainDetailActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    loadAppealMessage(ll);
                } else {
                    ShopHelper.showApiError(app, data.getJson());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_complain_appeal_handle:
                // 提交仲裁
                submitHandle();
                break;
            /*case R.id.btn_complain_appeal_submit:
                // 发布对话
                break;*/
            case R.id.btn_complain_appeal_refresh:
                LinearLayout ll = (LinearLayout) v.getTag();
                loadAppealMessage(ll);
                break;
        }
    }
}
