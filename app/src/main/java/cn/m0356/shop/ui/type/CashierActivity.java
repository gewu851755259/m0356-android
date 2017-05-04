package cn.m0356.shop.ui.type;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.PayDemoActivity;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.MyPayInfoBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.mine.BindMobileActivity;
import cn.m0356.shop.ui.mine.ModifyPaypwdStep1Activity;
import cn.m0356.shop.ui.mine.OrderListActivity;
import cn.m0356.shop.ui.mine.PayMentWebActivity;

/**
 * 收银台界面
 * Created by jiangtao on 2016/11/9.
 */
public class CashierActivity extends Activity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvToOrderList, tvAvailPr, tvAvailRc;
    private MyShopApplication myApplication;
    private LinearLayout llContainer;
    private TextView tvPrice, tv_notice, tv_modi_pwd, tv_no_pwd;
    private Button btn_pay;
    private EditText et_pwd;
    // 预存款
    private CheckBox availablePredepositID;
    // 充值卡
    private CheckBox availableRCBalanceID;
    // 站内付款
    private LinearLayout ll_use_inner;

    private LinearLayout ll_pre;
    private LinearLayout ll_rc;

    private LinearLayout ll_input;

    //private BuyBean bean;

    private String if_pd_pay = "0";//记录是否充值卡支付  1-使用 0-不使用
    private String if_rcb_pay = "0";//记录是否预存款支付 1-使用 0-不使用
    private String globalSn;
    /*private String rc;
    private String pre;*/

    private MyBroadcastReceiver myReceiver;
    private String paySn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        MyExceptionHandler.getInstance().setContext(this);
        findView();
        initData();
        setListener();
    }


    private void findView() {
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvToOrderList = (TextView) findViewById(R.id.tv_order_list);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        tvPrice = (TextView) findViewById(R.id.tv_buy_price);
        tv_modi_pwd = (TextView) findViewById(R.id.tv_modi_pwd);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        availablePredepositID = (CheckBox) findViewById(R.id.availablePredepositID);
        availableRCBalanceID = (CheckBox) findViewById(R.id.availableRCBalanceID);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_no_pwd = (TextView) findViewById(R.id.tv_no_pwd);
        ll_use_inner = (LinearLayout) findViewById(R.id.ll_use_inner);

        ll_pre = (LinearLayout) findViewById(R.id.ll_pr);
        ll_rc = (LinearLayout) findViewById(R.id.ll_rc);

        tvAvailPr = (TextView) findViewById(R.id.tv_avail_pr);
        tvAvailRc = (TextView) findViewById(R.id.tv_avail_rc);

        ll_input = (LinearLayout) findViewById(R.id.ll_input);

    }

    private void initData() {
        myApplication = (MyShopApplication) getApplication();

        myReceiver = new MyBroadcastReceiver();
        // 获取订单总价
        tvPrice.setText("￥" + getIntent().getDoubleExtra("AllPrice", -1) + "元");
        // 获取参数
        //bean = getIntent().getParcelableExtra("params");
        // 获取第三方支付方式
        //loadingPaymentListData();
        paySn = getIntent().getStringExtra("paySn");
        // 加载第三方支付方式
        //loadingPaymentListData_v2(paySn);
        loadingPaymentListData();

        // 根据站内余额修改UI
        String pre = getIntent().getStringExtra("pre");
        String rc = getIntent().getStringExtra("rc");

        checkUI(pre, rc);

    }

    private void checkUI(String pre, String rc) {
        availablePredepositID.setChecked(false);
        availableRCBalanceID.setChecked(false);
        if (TextUtils.isEmpty(pre) || pre.equals("null") || pre.equals("0.0") || pre.equals("0.00")) {
            //availablePredepositID.setVisibility(View.GONE);
            //ll_pre.setVisibility(View.GONE);
            tvAvailPr.setText("可用充值卡余额 ￥0.00");
            availablePredepositID.setClickable(false);
            availablePredepositID.setTextColor(Color.parseColor("#BEBEBE"));
        } else {
            //availablePredepositID.setVisibility(View.VISIBLE);
            //ll_pre.setVisibility(View.VISIBLE);
            String str = "可用充值卡余额 <font color=red>￥" + pre + "</font>";
            tvAvailPr.setText(Html.fromHtml(str));
            availablePredepositID.setClickable(true);
            availablePredepositID.setTextColor(Color.parseColor("#000000"));
        }
        if (TextUtils.isEmpty(rc) || rc.equals("null") || rc.equals("0.0") || rc.equals("0.00")) {
            //availableRCBalanceID.setVisibility(View.GONE);
            tvAvailRc.setText("可用预存款余额 ￥0.00");
            availableRCBalanceID.setClickable(false);
            availableRCBalanceID.setTextColor(Color.parseColor("#BEBEBE"));
            //ll_rc.setVisibility(View.GONE);
        } else {
            //availableRCBalanceID.setVisibility(View.VISIBLE);
            //ll_rc.setVisibility(View.VISIBLE);
            String str = "可用预存款余额 <font color=red>￥" + rc + "</font>";
            tvAvailRc.setText(Html.fromHtml(str));
            availableRCBalanceID.setClickable(true);
            availableRCBalanceID.setTextColor(Color.parseColor("#000000"));

        }

        if ((TextUtils.isEmpty(rc) && TextUtils.isEmpty(pre)) ||
                (pre.equals("null") && rc.equals("null")) || (pre.equals("0.00") && rc.equals("0.00"))) {
            //ll_use_inner.setVisibility(View.GONE);
            tv_notice.setVisibility(View.VISIBLE);
            tv_notice.setText("余额不足");
            btn_pay.setActivated(false);
            ll_input.setVisibility(View.GONE);
            availablePredepositID.setClickable(false);
            availableRCBalanceID.setClickable(false);
            tv_modi_pwd.setVisibility(View.GONE);
        } else {
            //ll_use_inner.setVisibility(View.VISIBLE);
            btn_pay.setActivated(true);
            /*availableRCBalanceID.setClickable(true);
            availablePredepositID.setClickable(true);*/
        }
    }


    private void loadPayPwdInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncPostDataString(Constants.URL_MEMBER_ACCOUNT_GET_PAYPWD_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(data.getJson());
                        boolean state = obj.getBoolean("state");
                        if (state) {
                            // 已设置支付密码
                            showModifyPwd();
                            tv_no_pwd.setVisibility(View.GONE);
                        } else {
                            // 未设置支付密码
                            btn_pay.setActivated(false);
                            tv_no_pwd.setVisibility(View.VISIBLE);
                            tv_no_pwd.setText("");
                            checkBingMobile();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(CashierActivity.this, data.getJson());
                }
            }
        });
    }

    private void showModifyPwd() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncPostDataString(Constants.URL_MEMBER_ACCOUNT_GET_MOBILE_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        final JSONObject obj = new JSONObject(data.getJson());
                        if (obj.getBoolean("state")) {
                            final String mobile = obj.getString("mobile");
                            btn_pay.setActivated(true);
                            tv_modi_pwd.setText(Html.fromHtml("<font color=#4a8bde>忘记支付密码？</font>"));
                            tv_modi_pwd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CashierActivity.this, ModifyPaypwdStep1Activity.class);
                                    intent.putExtra("mobile", mobile);
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(CashierActivity.this, data.getJson());
                }
            }
        });
    }

    /**
     * 检测用户是否绑定手机
     */
    private void checkBingMobile() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncPostDataString(Constants.URL_MEMBER_ACCOUNT_GET_MOBILE_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {

                        final JSONObject obj = new JSONObject(data.getJson());
                        if (obj.getBoolean("state")) {
                            /*// 已绑定手机'
                            Intent intent = new Intent(CashierActivity.this, ModifyPaypwdStep1Activity.class);
                            intent.putExtra("mobile", obj.getString("mobile"));
                            startActivity(intent);*/
                            final String mobile = obj.getString("mobile");
                            /*tv_notice.setText(Html.fromHtml("您尚未设置支付密码，<font color='#4a8bde'>点击设置</font>"));
                            tv_notice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CashierActivity.this, ModifyPaypwdStep1Activity.class);
                                    intent.putExtra("mobile", mobile);
                                    startActivity(intent);
                                }
                            });*/
                            tv_no_pwd.setText(Html.fromHtml("您尚未设置支付密码，<font color='#4a8bde'>点击设置</font>"));
                            tv_no_pwd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CashierActivity.this, ModifyPaypwdStep1Activity.class);
                                    intent.putExtra("mobile", mobile);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            // 未绑定手机
                            //startActivity(new Intent(CashierActivity.this, BindMobileActivity.class));
                            tv_no_pwd.setText(Html.fromHtml("您尚未绑定手机，<font color='#4a8bde'>点击绑定手机</font>"));
                            tv_no_pwd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(CashierActivity.this, BindMobileActivity.class));
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(CashierActivity.this, data.getJson());
                }
            }
        });
    }

    /**
     * 获取支付方式，此方法可获取未支付金额、可用预存款等信息
     * @param paySn
     */
    private void loadingPaymentListData_v2(String paySn) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        map.put("pay_sn", paySn);
        RemoteDataHandler.asyncPostDataString(Constants.URL_PAY_LIST, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    MyPayInfoBean myPayInfoBean = gson.fromJson(data.getJson(), MyPayInfoBean.class);
                    // 更新界面
                    updateUI(myPayInfoBean);
                } else {
                    otherResponse(data);
                }
            }
        });
    }

    private void updateUI(MyPayInfoBean myPayInfoBean) {

        loadPayUI(myPayInfoBean.pay_info.payment_list);

        /*double pay_amount = Double.parseDouble(myPayInfoBean.pay_info.pay_amount);
        if (pay_amount > 0) { // 站内余额不足 使用第三方进行支付
            String pay_notice_Str = getResources().getString(R.string.pay_notice);
            String pay_notice = String.format(pay_notice_Str, String.valueOf(pay_amount));
            // 设置通知
            tv_notice.setText(pay_notice);
            // 该接口不能获取微信支付 暂停使用
//            updatePayList(myPayInfoBean);
            globalSn = myPayInfoBean.pay_info.pay_sn;
        } else {
            Toast.makeText(CashierActivity.this, "支付成功，跳转至订单列表", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CashierActivity.this, OrderListActivity.class);
            CashierActivity.this.startActivity(intent);
            CashierActivity.this.finish();
        }*/

    }

    private void loadPayUI(List<MyPayInfoBean.PayInfoBean.PaymentListBean> payment_list) {
        for(MyPayInfoBean.PayInfoBean.PaymentListBean bean : payment_list){
            View view = View.inflate(CashierActivity.this, R.layout.item_pay_mode, null);
            ImageView iv = (ImageView) view.findViewById(R.id.itemImage);
            TextView tv = (TextView) view.findViewById(R.id.itemText);
            view.setTag(bean.payment_code); // 将value附加到view
            if (bean.payment_code.equals("wxpay")) {
                iv.setImageResource(R.drawable.sns_weixin_icon);
                tv.setText("微信支付");
                llContainer.addView(view);
            } /*else if (Values.equals("alipay")) {
                iv.setImageResource(R.drawable.zhifubao_appicon);
                tv.setText("支付宝");
                llContainer.addView(view);
            } */ else if (bean.payment_code.equals("alipay_native")) {//TODO Modify 支付宝原生支付
                iv.setImageResource(R.drawable.pay);
                tv.setText("支付宝");
                llContainer.addView(view);
            } else {
                iv = null;
                tv = null;
            }
            setItemOnClick(view);
        }
    }

    private void updatePayList(MyPayInfoBean myPayInfoBean) {
        List<MyPayInfoBean.PayInfoBean.PaymentListBean> payment_list = myPayInfoBean.pay_info.payment_list;
        for (int i = 0; i < payment_list.size(); i++) {
            View view = View.inflate(CashierActivity.this, R.layout.item_pay_mode, null);
            ImageView iv = (ImageView) view.findViewById(R.id.itemImage);
            TextView tv = (TextView) view.findViewById(R.id.itemText);
        }

        /*ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < size; i++) {
            String Values = arr.getString(i);
            View view = View.inflate(CashierActivity.this, R.layout.item_pay_mode, null);
            ImageView iv = (ImageView) view.findViewById(R.id.itemImage);
            TextView tv = (TextView) view.findViewById(R.id.itemText);
            if (Values.equals("wxpay")) {
                iv.setImageResource(R.drawable.sns_weixin_icon);
                tv.setText("微信支付");
                llContainer.addView(view);
            } else if (Values.equals("alipay")) {
                iv.setImageResource(R.drawable.zhifubao_appicon);
                tv.setText("支付宝");
                llContainer.addView(view);
            } else if (Values.equals("alipay_native")) {//TODO Modify 支付宝原生支付
                iv.setImageResource(R.drawable.pay);
                tv.setText("原生支付");
                llContainer.addView(view);
            } else {
                iv = null;
                tv = null;
            }
            setItemOnClick(Values, view, paySn);
        }*/
    }

    // 已修改接口
    private void otherResponse(ResponseData data) {
        if (!TextUtils.isEmpty(data.getJson())) {
            try {
                JSONObject objError = new JSONObject(data.getJson());
                String error = objError.getString("error");

                if (error.equals("订单重复支付")) {
                    // 使用站内余额支付完成，直接跳转订单列表
                    Intent intent = new Intent(CashierActivity.this, OrderListActivity.class);
                    CashierActivity.this.startActivity(intent);
                    CashierActivity.this.finish();
                    return;
                }

                if (error != null) {
                    Toast.makeText(MyShopApplication.context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                Toast.makeText(MyShopApplication.context, R.string.load_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    private void setListener() {
        btnBack.setOnClickListener(this);
        tvToOrderList.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        availableRCBalanceID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if_rcb_pay = "1";
                    ll_input.setVisibility(View.VISIBLE);
                    tv_notice.setVisibility(View.VISIBLE);
                    tv_modi_pwd.setVisibility(View.VISIBLE);

                } else {
                    if_rcb_pay = "0";
                    if(!availablePredepositID.isChecked()){
                        ll_input.setVisibility(View.GONE);
                        tv_modi_pwd.setVisibility(View.GONE);
                        //tv_notice.setVisibility(View.GONE);
                    }
                }
            }
        });
        availablePredepositID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if_pd_pay = "1";
                    ll_input.setVisibility(View.VISIBLE);
                    tv_notice.setVisibility(View.VISIBLE);
                    tv_modi_pwd.setVisibility(View.VISIBLE);
                } else {
                    if_pd_pay = "0";
                    if(!availableRCBalanceID.isChecked()){
                        tv_modi_pwd.setVisibility(View.GONE);
                        ll_input.setVisibility(View.GONE);
                        //tv_notice.setVisibility(View.GONE);
                    }
                }
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:  // 返回
                finish();
                break;
            case R.id.tv_order_list: // 前往订单中心
                Intent intent = new Intent(this, OrderListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_pay: // 确认支付按钮
                //通过预存款或余额支付
                useInnerMoney();
                break;
        }
    }

    private void useInnerMoney() {
        if(!btn_pay.isActivated())
            return;
        // 使用内部存款需验证密码
        String psw = et_pwd.getText().toString();
        if (TextUtils.isEmpty(psw)) {
            Toast.makeText(CashierActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        // 使用站内余额支付，value传入null或""即可
        checkPassword(psw);
        //checkPassword(psd);
    }

/*    private void pay(String paySn) {
        // 加载支付方式
        loadingPaymentListData(paySn);
    }*/

    /**
     * 验证支付密码
     *
     * @param password 支付密码
     */
    public void checkPassword(final String password) {
        String url = Constants.URL_CHECK_PASSWORD;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("password", password);
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    if (json.equals("1")) {
                        ll_input.setVisibility(View.GONE);
                        String rcb_pay = availableRCBalanceID.isChecked() ? "1" : "0";
                        String pd_pay = availablePredepositID.isChecked() ? "1" : "0";
                        payNew(password, rcb_pay, pd_pay);
                    }

                } else {
                    if (TextUtils.isEmpty(json)) {
                        Toast.makeText(MyShopApplication.context, "error:check password is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(json);
                        String error = obj.getString("error");
                        if (error != null) {
                            Toast.makeText(MyShopApplication.context, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MyShopApplication.context, "error:check password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 使用站内余额支付
     * @param password 支付密码
     * @param rcb_pay
     * @param pd_pay
     */
    private void payNew(String password, String rcb_pay, String pd_pay){
        String url = Constants.URL_PAY_NEW;
        // key
        url += "&key=" + myApplication.getLoginKey();
        // 订单号
        url += "&pay_sn=" + paySn;
        // 如果使用站内余额，需要支付密码
        url += "&password=" + password;
        url += "&rcb_pay=" + rcb_pay;
        url += "&pd_pay=" + pd_pay;
        url += "&style=" + 1;
        //url += "&payment_code=" + payment_code;
        LogHelper.e("CashierActivity" , url);
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){

                    try {
                        JSONObject object = new JSONObject(data.getJson());
                        // 需要第三方支付
                        double aDouble = object.getJSONObject("data").getDouble("api_pay_amount");
                        if(aDouble > 0){
                            // 站内余额不足 使用第三方进行支付
                            String pay_notice_Str = getResources().getString(R.string.pay_notice);
                            String pay_notice = String.format(pay_notice_Str, "<font size='20px' color='#ff0000'>" + String.valueOf(aDouble) + "</font>");
                            // 设置通知
                            tv_notice.setText(Html.fromHtml(pay_notice));
                        } else {
                            // 支付完成
                            Toast.makeText(MyShopApplication.context, "使用站内余额支付完成", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CashierActivity.this, OrderListActivity.class);
                            CashierActivity.this.startActivity(intent);
                            CashierActivity.this.finish();
                        }
                        // 隐藏修改支付密码
                        tv_modi_pwd.setVisibility(View.GONE);
                        loadInnerMoney(paySn);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });

    }


   /* private void sendBuyStep2Data(final String pwd, final View view) {
        bean.map.put("pd_pay", if_pd_pay);
        bean.map.put("rcb_pay", if_rcb_pay);
        bean.map.put("password", pwd);
        RemoteDataHandler.asyncPostDataString(Constants.URL_BUY_STEP2, bean.map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    // 提交订单成功 生成订单号
                    try {
                        String paySn = new JSONObject(data.getJson()).getString("pay_sn");
                        if (TextUtils.isEmpty(pwd)) { // 获取订单号成功 直接使用第三方支付
                            enterPay(((String) view.getTag()), paySn);
                        } else {
                            // 根据订单号获取支付方式等数据
                            loadingPaymentListData_v2(paySn);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(CashierActivity.this, data.getJson());
                }
            }
        });
    }*/

    /**
     * 获取可用支付方式
     */
    public void loadingPaymentListData() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ORDER_PAYMENT_LIST, params, myApplication,
                new RemoteDataHandler.Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                parsePayList(json);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void parsePayList(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        // 20161129
        String JosnObj = jsonObject.getString("payment_list");
        JSONArray arr = new JSONArray(JosnObj);
        Log.d("huting====pay", arr.toString());

        int size = (null == arr ? 0 : arr.length());
        ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < size; i++) {
            String Values = arr.getString(i);
            View view = View.inflate(CashierActivity.this, R.layout.item_pay_mode, null);
            ImageView iv = (ImageView) view.findViewById(R.id.itemImage);
            TextView tv = (TextView) view.findViewById(R.id.itemText);
            view.setTag(Values); // 将value附加到view
            if (Values.equals("wxpay")) {
                iv.setImageResource(R.drawable.sns_weixin_icon);
                tv.setText("微信支付");
                llContainer.addView(view);
            } /*else if (Values.equals("alipay")) {
                iv.setImageResource(R.drawable.zhifubao_appicon);
                tv.setText("支付宝");
                llContainer.addView(view);
            } */ else if (Values.equals("alipay_native")) {//TODO Modify 支付宝原生支付
                iv.setImageResource(R.drawable.pay);
                tv.setText("支付宝");
                llContainer.addView(view);
            } else {
                iv = null;
                tv = null;
            }
            setItemOnClick(view);
        }
    }

    /**
     * 更新站内余额显示
     * @param paySn
     */
    private void loadInnerMoney(final String paySn){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        map.put("pay_sn", paySn);
        RemoteDataHandler.asyncPostDataString(Constants.URL_PAY_LIST, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    MyPayInfoBean myPayInfoBean = gson.fromJson(data.getJson(), MyPayInfoBean.class);
                    String pre = myPayInfoBean.pay_info.member_available_pd;
                    if(pre.equals("null") || pre.equals("0") || pre.equals("0.00")) {
                        availablePredepositID.setClickable(false);
                        availablePredepositID.setTextColor(Color.parseColor("#BEBEBE"));
                        pre = "￥0.00";
                    } else {
                        pre = "<font color=red>￥" + pre + "</font>";
                        availablePredepositID.setTextColor(Color.parseColor("#000000"));
                    }
                    String rcb = myPayInfoBean.pay_info.member_available_rcb;
                    if(rcb.equals("null") || rcb.equals("0") || rcb.equals("0.00")) {
                        availableRCBalanceID.setClickable(false);
                        availableRCBalanceID.setTextColor(Color.parseColor("#BEBEBE"));
                        rcb = "￥0.00";
                    } else {
                        rcb = "<font color=red>￥" + rcb + "</font>";
                        availableRCBalanceID.setTextColor(Color.parseColor("#000000"));
                    }
                    tvAvailPr.setText(Html.fromHtml("可用充值卡余额 " + pre));
                    tvAvailRc.setText(Html.fromHtml("可用预存款余额 " + rcb));
                    //checkUI(pre, rcb);
                    availableRCBalanceID.setChecked(false);
                    availablePredepositID.setChecked(false);
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });
    }

    /**
     * 为第三方支付item设置点击事件
     *
     * @param view
     */
    private void setItemOnClick(View view) {
        final String value = (String) view.getTag();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPay(value, paySn);
                // 判断用户是否选择站内余额支付
                /*if(availableRCBalanceID.isChecked() || availablePredepositID.isChecked()){
                    // 验证支付密码
                    String pwd = et_pwd.getText().toString();
                    if(TextUtils.isEmpty(pwd)){
                        Toast.makeText(MyShopApplication.context, "请填写支付密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    checkPassword(pwd, value);
                } else {*/
                    // 直接跳转第三方直接
                    //payNew("", value, "0", "0");
                //}
            }
        });

        /*if (values.equals("wxpay")) { // 微信支付
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingWXPaymentData(paySn);
                }
            });
        } else if (values.equals("alipay")) { // 支付宝支付

        } else if (values.equals("alipay_native")) { //TODO Modify 支付宝原生支付
        }*/

    }


    /**
     * 进入第三方支付
     *
     * @param values
     * @param paySn
     */
    private void enterPay(String values, String paySn) {
        if (values.equals("wxpay")) {
            loadingWXPaymentData(paySn);
        } else if (values.equals("alipay")) {
            Intent intent = new Intent(CashierActivity.this,
                    PayMentWebActivity.class);
            intent.putExtra("pay_sn", paySn);
            startActivityForResult(intent, 0);
        } else if (values.equals("alipay_native")) {
            loadingAlipayNativePaymentData(paySn);
        }
    }

    ////////////////////////////////// 微信支付 支付宝支付///////////////////////////////////////

    /**
     * 获取微信参数
     *
     * @param pay_sn 支付编号
     */
    public void loadingWXPaymentData(String pay_sn) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);
        Log.d("dqw", Constants.URL_MEMBER_WX_PAYMENT);
        Log.d("dqw", myApplication.getLoginKey());
        Log.d("dqw", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_MEMBER_WX_PAYMENT, params, myApplication,
                new RemoteDataHandler.Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String appid = jsonObject.getString("appid");// 微信开放平台appid
                                String noncestr = jsonObject
                                        .getString("noncestr");// 随机字符串
                                String packageStr = jsonObject
                                        .getString("package");// 支付内容
                                String partnerid = jsonObject
                                        .getString("partnerid");// 财付通id
                                String prepayid = jsonObject
                                        .getString("prepayid");// 微信预支付编号
                                String sign = jsonObject.getString("sign");// 签名
                                String timestamp = jsonObject
                                        .getString("timestamp");// 时间戳

                                IWXAPI api = WXAPIFactory.createWXAPI(MyShopApplication.context, appid);

                                PayReq req = new PayReq();
                                req.appId = appid;
                                req.partnerId = partnerid;
                                req.prepayId = prepayid;
                                req.nonceStr = noncestr;
                                req.timeStamp = timestamp;
                                req.packageValue = packageStr;
                                req.sign = sign;
                                req.extData = "app data"; // optional

                                Log.d("huting----------", req.toString());

                                Toast.makeText(MyShopApplication.context, "正常调起支付",
                                        Toast.LENGTH_SHORT).show();
                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                api.sendReq(req);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    /**
     * 获取支付宝原生支付的参数
     */
    public void loadingAlipayNativePaymentData(String pay_sn) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);

        Log.d("huting", Constants.URL_ALIPAY_NATIVE_GOODS);
        Log.d("huting", myApplication.getLoginKey());
        Log.d("huting", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ALIPAY_NATIVE_GOODS, params, myApplication,
                new RemoteDataHandler.Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String signStr = jsonObject.optString("signStr");
                                Log.d("huting-----nativePay", signStr);
                                PayDemoActivity payDemoActivity = new PayDemoActivity(CashierActivity.this, signStr);
                                payDemoActivity.setCallback(new MyCallback() {

                                    @Override
                                    public void onFinish() {
                                        Intent intent = new Intent(CashierActivity.this, OrderListActivity.class);
                                        CashierActivity.this.startActivity(intent);
                                        CashierActivity.this.finish();
                                        finish();
                                    }
                                });
                                payDemoActivity.doPay();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 加载支付密码信息
        loadPayPwdInfo();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.PAYMENT_SUCCESS);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { // alipay_native
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(CashierActivity.this, OrderListActivity.class);
                CashierActivity.this.startActivity(intent);
                CashierActivity.this.finish();
            }
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    public interface MyCallback {
        void onFinish();
    }

}
