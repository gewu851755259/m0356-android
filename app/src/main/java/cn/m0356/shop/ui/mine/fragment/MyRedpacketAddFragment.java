package cn.m0356.shop.ui.mine.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.mine.RedpacketListActivity;

/**
 * Created by jiangtao on 2016/10/11.
 */
public class MyRedpacketAddFragment extends Fragment implements View.OnClickListener {
    private MyShopApplication myApplication;
    private EditText etPwdCode;
    private EditText etCode;
    private ImageView ivCode;
    private Button btnSubmit;
    private String codeKey;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redpacket_add, null);

        myApplication = (MyShopApplication) getActivity().getApplicationContext();

        etPwdCode = (EditText) view.findViewById(R.id.etPwdCode);
        etCode = (EditText) view.findViewById(R.id.etCode);
        ivCode = (ImageView) view.findViewById(R.id.ivCode);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        ivCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSeccodeCode();
            }
        });

        initSubmitButton();
        loadSeccodeCode();

        return view;
    }

    /**
     * 提交按钮状态
     */
    private void initSubmitButton() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPwdCode.getText().toString().equals("") || etCode.getText().toString().equals("")) {
                    btnSubmit.setActivated(false);
                } else {
                    btnSubmit.setActivated(true);
                }
            }
        };
        etPwdCode.addTextChangedListener(textWatcher);
        etCode.addTextChangedListener(textWatcher);
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
                    ShopHelper.showApiError(getActivity(), json);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {  // 没有判断按钮id
        if (btnSubmit.isActivated()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("key", myApplication.getLoginKey());
            params.put("pwd_code", etPwdCode.getText().toString());
            params.put("codekey", codeKey);
            params.put("captcha", etCode.getText().toString());
            RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_REDPACKET_ADD, params, myApplication, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        ShopHelper.showMessage(getActivity(), "红包领取成功");
                    } else {
                        ShopHelper.showApiError(getActivity(), json);
                        loadSeccodeCode();
                    }
                }
            });
        }
    }
}
