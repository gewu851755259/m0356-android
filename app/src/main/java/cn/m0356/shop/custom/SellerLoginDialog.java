package cn.m0356.shop.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerLoginInfo;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.SellerHelper;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.seller.SellerMainActivity;

/**
 * Created by jiangtao on 2016/11/28.
 */
public class SellerLoginDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private EditText etUserName, etPassword;
    private Button btnLogin;

    public SellerLoginDialog(Context context) {
        super(context, R.style.MyDialog);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_seller_login, null);
        this.setContentView(view);
        // 设置 dialog 位于屏幕底部，并且设置出入动画
        setBottomLayout();
        init(view);
    }

    private void init(View view) {
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etUserName = (EditText) view.findViewById(R.id.etUsername);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogin.setActivated(true);
        btnLogin.setOnClickListener(this);
    }

    /**
     * 设置 dialog 位于屏幕底部，并且设置出入动画
     */
    private void setBottomLayout() {
        Window win = getWindow();
        if (win != null) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            // dialog 布局位于底部
            win.setGravity(Gravity.BOTTOM);
            // 设置进出场动画
            win.setWindowAnimations(R.style.Animation_Bottom);
        }
    }

    @Override
    public void onClick(View v) {
        String username = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        SellerHelper.login(username, password, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    // 登录成功  保存信息
                    Gson gson = new Gson();
                    SellerLoginInfo sellerLoginInfo = gson.fromJson(data.getJson(), SellerLoginInfo.class);
                    SellerHelper.saveSellerInfo(sellerLoginInfo);
                    enterSellerMain();
                    dismiss();
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });

    }

    private void enterSellerMain() {
        SellerMainActivity.startSellerMainActivity(getContext());
    }
}
