package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.bean.Login;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

import org.apache.http.HttpStatus;

import java.util.HashMap;

public class RegisterMobileStep3Activity extends BaseActivity {

    private MyShopApplication myApplication;
    private EditText etPassword,etInviteNumber, etPassword2;
    private ImageButton btnShowPassowrd;
    private Button btnRegSubmit;

    private String phone;
    private String captcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile_step3);
        MyExceptionHandler.getInstance().setContext(this);
        phone = getIntent().getStringExtra("phone");
        captcha = getIntent().getStringExtra("captcha");

        myApplication = (MyShopApplication) getApplicationContext();
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        etInviteNumber = (EditText) findViewById(R.id.etInviteNumber);
        btnShowPassowrd = (ImageButton) findViewById(R.id.btnShowPassword);
        btnRegSubmit = (Button) findViewById(R.id.btnRegSubmit);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPassword.getText().toString().length() > 0) {
                    btnRegSubmit.setActivated(true);
                } else {
                    btnRegSubmit.setActivated(false);
                }
            }
        });

        setCommonHeader("设置密码");
    }

    /**
     * 显示密码按钮点击
     */
    public void btnShowPasswordClick(View v) {
        if (btnShowPassowrd.isSelected()) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnShowPassowrd.setSelected(false);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            btnShowPassowrd.setSelected(true);
        }
    }

    /**
     * 注册按钮点击
     */
    public void btnRegSubmitClick(View v) {
        if (btnRegSubmit.isActivated()) {
            String password = etPassword.getText().toString();
            String invite_number = etInviteNumber.getText().toString();
            int length = password.length();
            if (length < 6 || length > 20) {
                ShopHelper.showMessage(RegisterMobileStep3Activity.this, "请输入6-20位密码");
                return;
            }

            if(!password.equals(etPassword2.getText().toString())){
                Toast.makeText(RegisterMobileStep3Activity.this, "密码输入不一致请重新输入", Toast.LENGTH_SHORT).show();
                etPassword2.setText("");
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phone", phone);
            params.put("captcha", captcha);
            params.put("password", password);
            params.put("client", "android");
            params.put("invite_number", invite_number);
            RemoteDataHandler.asyncPostDataString(Constants.URL_CONNECT_SMS_REGISTER, params, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        Login login = Login.newInstanceList(json);
                        myApplication.setLoginKey(login.getKey());
                        myApplication.setUserName(login.getUsername());
                        myApplication.setMemberID(login.getUserid());

                        myApplication.loadingUserInfo(login.getKey(), login.getUserid());

                        myApplication.getmSocket().connect();
                        myApplication.UpDateUser();

                        Intent mIntent = new Intent(Constants.LOGIN_SUCCESS_URL);
                        sendBroadcast(mIntent);
                        RegisterMobileStep3Activity.this.finish();
                    } else {
                        ShopHelper.showApiError(RegisterMobileStep3Activity.this, json);
                    }
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_mobile_step3, menu);
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
}
