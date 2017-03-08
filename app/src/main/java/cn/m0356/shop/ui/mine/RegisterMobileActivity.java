package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.NCDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

public class RegisterMobileActivity extends BaseActivity {

    //private Button btnReg, btnRegMb, btnRegSubmit;
    /**
     * 手机号
     */
    private EditText etPhone;
    /**
     * 统一协议按钮
     */
    private ImageButton btnAgree;
    private NCDialog ncDialog;
    /**
     * 获取短信验证码按钮
     */
    private Button btnGetSmsCaptcha;
    private Button btnRegNext;
    /**
     * 验证码
     */
    private EditText etCode;
    /**
     * 短信验证码
     */
    private EditText etSmsCaptcha;
    /**
     * 图片验证码
     */
    private ImageView ivCode;
    private String codeKey;

    private MyTextWatcher mTextWatcher;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);
        MyExceptionHandler.getInstance().setContext(this);

//        btnReg = (Button) findViewById(R.id.btnReg);
//        btnRegMb = (Button) findViewById(R.id.btnRegMb);
        //btnRegSubmit = (Button) findViewById(R.id.btnRegSubmit);
        btnAgree = (ImageButton) findViewById(R.id.btnAgree);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etCode = (EditText) findViewById(R.id.etCode);
        btnGetSmsCaptcha = (Button) findViewById(R.id.btnGetSmsCaptcha);
        etSmsCaptcha = (EditText) findViewById(R.id.etSmsCaptcha);
        mTextWatcher = new MyTextWatcher();
        etSmsCaptcha.addTextChangedListener(mTextWatcher);
        btnRegNext = (Button) findViewById(R.id.btnRegNext);
        ivCode = (ImageView) findViewById(R.id.ivCode);
        ivCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSeccodeCode();
            }
        });

//        btnReg.setActivated(false);
//        btnRegMb.setActivated(true);
        btnAgree.setSelected(true);
        //btnRegSubmit.setActivated(true);
        btnGetSmsCaptcha.setActivated(true);
        loadSeccodeCode();

        setCommonHeader("会员注册");

        //默认选中用户协议
        btnAgree.setSelected(true);
    }

    /*
    * 普通注册按钮
    */
    public void btnRegClick(View v) {
        Intent intent = new Intent(RegisterMobileActivity.this, RegisteredActivity.class);
        startActivity(intent);
        finish();
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
                        imageLoader.displayImage(Constants.URL_SECCODE_MAKECODE + "&k=" + codeKey, ivCode, options, animateFirstListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(RegisterMobileActivity.this, json);
                }
            }

        });
    }


   /* public void btnRegSubmitClick(View v) {
        if (btnRegSubmit.isActivated()) {

            final String phone = etPhone.getText().toString();
            final String code = etCode.getText().toString();


            ///
            String url = Constants.URL_CONNECT_GET_SMS_CAPTCHA + "&phone=" + phone + "&type=1" + "&sec_key=" + codeKey + "&sec_val=" + code;
            RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    final String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        ncDialog = new NCDialog(RegisterMobileActivity.this);
                        ncDialog.setText1("我们将发送验证码短信至:");
                        ncDialog.setText2(phone);
                        ncDialog.setOnDialogConfirm(new INCOnDialogConfirm() {
                            @Override
                            public void onDialogConfirm() {
                                try{
                                    JSONObject obj = new JSONObject(json);
                                    String smsTime = obj.getString("sms_time");
                                    Intent intent = new Intent(RegisterMobileActivity.this, RegisterMobileStep2Activity.class);
                                    intent.putExtra("phone", phone);
                                    intent.putExtra("sms_time", smsTime);
                                    startActivity(intent);
                                    finish();
                                }catch (JSONException e){
                                    ShopHelper.showApiError(RegisterMobileActivity.this, json);
                                    loadSeccodeCode();
                                }
                            }
                        });
                        ncDialog.showPopupWindow();
                    } else {
                        ShopHelper.showApiError(RegisterMobileActivity.this, json);
                        loadSeccodeCode();
                    }
                }
            });

        }
    }*/

    /*
     * 同意协议按钮
     */
    public void btnAgreeClick(View v) {
        if (btnAgree.isSelected()) {
            btnAgree.setSelected(false);
            btnRegNext.setActivated(false);
        } else {
            btnAgree.setSelected(true);
            btnRegNext.setActivated(true);
        }
    }

    /**
     * 用户注册协议
     */
    public void btnMemberDocumentClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.WAP_MEMBER_DOCUMENT));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_mobile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 获取短信验证码点击事件
     *
     * @param view
     */
    public void btnGetSmsCaptchaClick(View view) {
        if (!btnGetSmsCaptcha.isActivated())
            return;
        phone = etPhone.getText().toString();
        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(MyShopApplication.context, "请填写手机号", Toast.LENGTH_SHORT).show();
            loadSeccodeCode();
            return;
        }
        if (phone.length() != 11 || !SystemHelper.isMobileNO(phone)) {
            Toast.makeText(MyShopApplication.context, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            loadSeccodeCode();
            return;
        }

        //验证验证码
        if (code.equals("")) {
            Toast.makeText(RegisterMobileActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
            loadSeccodeCode();
            return;
        }
        //验证验证码
        if (code.length() != 4) {
            Toast.makeText(RegisterMobileActivity.this, "请填写正确的验证码", Toast.LENGTH_SHORT).show();
            loadSeccodeCode();
            return;
        }

        ///////////// 获取短信验证码 //////////////
        getSmsCaptcha(code);

        ///////////////////////////

        // 获取验证码
        /*if (btnGetSmsCaptcha.isActivated()) {
            String url = Constants.URL_CONNECT_GET_SMS_CAPTCHA + "&phone=" + phone + "&type=1";
            RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        try {
                            JSONObject obj = new JSONObject(json);
                            int smsTime = Integer.parseInt(obj.getString("sms_time"));
                            ShopHelper.btnSmsCaptchaCountDown(RegisterMobileActivity.this, btnGetSmsCaptcha, smsTime);
                        } catch (JSONException e) {
                            Toast.makeText(RegisterMobileActivity.this, "短信发送失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ShopHelper.showApiError(RegisterMobileActivity.this, json);
                    }
                }
            });
        }*/
    }

    /**
     * 获取短信验证码
     *
     * @param code
     */
    private void getSmsCaptcha(String code) {
        String url = Constants.URL_CONNECT_GET_SMS_CAPTCHA + "&phone=" + phone + "&type=1" + "&sec_key=" + codeKey + "&sec_val=" + code;
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();

                if (data.getCode() == HttpStatus.SC_OK) {
                    // 获取短信验证码成功
                    try {
                        JSONObject obj = new JSONObject(json);
                        int smsTime = Integer.parseInt(obj.getString("sms_time"));
                        ShopHelper.btnSmsCaptchaCountDown(RegisterMobileActivity.this, btnGetSmsCaptcha, smsTime);
                        Toast.makeText(RegisterMobileActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        ShopHelper.showApiError(RegisterMobileActivity.this, json);
                        loadSeccodeCode();
                    }

                    /*ncDialog = new NCDialog(RegisterMobileActivity.this);
                    ncDialog.setText1("我们将发送验证码短信至:");
                    ncDialog.setText2(phone);
                    ncDialog.setOnDialogConfirm(new INCOnDialogConfirm() {
                        @Override
                        public void onDialogConfirm() {
                            try{
                                JSONObject obj = new JSONObject(json);
                                String smsTime = obj.getString("sms_time");
                                Intent intent = new Intent(RegisterMobileActivity.this, RegisterMobileStep2Activity.class);
                                intent.putExtra("phone", phone);
                                intent.putExtra("sms_time", smsTime);
                                startActivity(intent);
                                finish();
                            }catch (JSONException e){
                                ShopHelper.showApiError(RegisterMobileActivity.this, json);
                                loadSeccodeCode();
                            }
                        }
                    });
                    ncDialog.showPopupWindow();*/
                } else {
                    ShopHelper.showApiError(RegisterMobileActivity.this, json);
                    loadSeccodeCode();
                }
            }
        });
    }


    /**
     * 下一步按钮
     *
     * @param view
     */
    public void btnRegNextClick(View view) {
        if (!btnRegNext.isActivated())
            return;
        String url = Constants.URL_CONNECT_CHECK_SMS_CAPTCHA + "&phone=" + phone + "&captcha=" + etSmsCaptcha.getText().toString() + "&type=1";
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    Intent intent = new Intent(RegisterMobileActivity.this, RegisterMobileStep3Activity.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("captcha", etSmsCaptcha.getText().toString());
                    startActivity(intent);
                    RegisterMobileActivity.this.finish();
                } else {
                    ShopHelper.showApiError(RegisterMobileActivity.this, json);
                }
            }
        });
    }


    ////////////////////////////手机号&图片验证码&短信验证码监听/////////////////////////////////

    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 3 && !TextUtils.isEmpty(etCode.getText().toString())
                    && !TextUtils.isEmpty(etPhone.getText().toString())) {
                btnRegNext.setActivated(true);
            } else {
                btnRegNext.setActivated(false);
            }
        }
    }
    /////////////////////////////////////////////////////////////

}
