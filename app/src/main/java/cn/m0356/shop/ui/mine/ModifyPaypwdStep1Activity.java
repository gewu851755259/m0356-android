package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 修改支付密码第一步
 *
 * @author dqw
 * @date 2015/9/2
 */
public class ModifyPaypwdStep1Activity extends BaseActivity {

    MyShopApplication myApplication;
    TextView tvMobile;
    EditText etCode, etSmsCaptcha;
    ImageView ivCode;
    Button btnGetSmsCaptcha, btnNext;
    String codeKey;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_paypwd_step1);
        setCommonHeader("修改支付密码");
        MyExceptionHandler.getInstance().setContext(this);
        myApplication = (MyShopApplication) getApplicationContext();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String captcha = etCode.getText().toString();
                String smsCaptcha = etSmsCaptcha.getText().toString();
                if (mobile.length() > 0 && captcha.length() > 0 && smsCaptcha.length() > 0) {
                    btnNext.setActivated(true);
                } else {
                    btnNext.setActivated(false);
                }
            }
        };

        mobile = getIntent().getStringExtra("mobile");
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvMobile.setText(mobile);
        etCode = (EditText) findViewById(R.id.etCode);
        etCode.addTextChangedListener(textWatcher);
        etSmsCaptcha = (EditText) findViewById(R.id.etSmsCaptcha);
        etSmsCaptcha.addTextChangedListener(textWatcher);
        ivCode = (ImageView) findViewById(R.id.ivCode);
        ivCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSeccodeCode();
            }
        });
        btnGetSmsCaptcha = (Button) findViewById(R.id.btnGetSmsCaptcha);
        btnGetSmsCaptcha.setActivated(true);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setActivated(false);

        loadSeccodeCode();
    }

    /**
     * 加载图片验证码
     */
    private void loadSeccodeCode() {
        etCode.setText("");
        RemoteDataHandler.asyncDataStringGet(Constants.URL_SECCODE_MAKECODEKEY, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        codeKey = obj.getString("codekey");
                        ShopHelper.loadImage(ivCode, Constants.URL_SECCODE_MAKECODE + "&k=" + codeKey);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(ModifyPaypwdStep1Activity.this, json);
                }
            }

        });
    }

    /**
     * 获取短信验证码点击
     */
    public void btnGetSmsCaptchaClick(View view) {
        if (!btnGetSmsCaptcha.isActivated()) {
            return;
        }

        String captcha = etCode.getText().toString();
        if (captcha.equals("")) {
            ShopHelper.showMessage(ModifyPaypwdStep1Activity.this, "请输入图形验证码");
            loadSeccodeCode();
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("captcha", captcha);
        params.put("codekey", codeKey);
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_ACCOUNT_MODIFY_PAYPWD_STEP2, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int smsTime = jsonObject.optInt("sms_time");
                        ShopHelper.showMessage(ModifyPaypwdStep1Activity.this, "验证码发送成功");
                        ShopHelper.btnSmsCaptchaCountDown(ModifyPaypwdStep1Activity.this, btnGetSmsCaptcha, smsTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(ModifyPaypwdStep1Activity.this, json);
                    loadSeccodeCode();
                }
            }

        });
    }

    /**
     * 验证手机
     */
    public void btnNextClick(View view) {
        if (!btnNext.isActivated()) {
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("auth_code", etSmsCaptcha.getText().toString());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_ACCOUNT_MODIFY_PAYPWD_STEP3, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    startActivity(new Intent(ModifyPaypwdStep1Activity.this, ModifyPaypwdStep2Activity.class));
                    finish();
                } else {
                    loadSeccodeCode();
                    ShopHelper.showApiError(ModifyPaypwdStep1Activity.this, json);
                }
            }
        });
    }
}


