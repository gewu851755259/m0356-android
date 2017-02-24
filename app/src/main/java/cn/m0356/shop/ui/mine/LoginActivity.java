package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.bean.AdvertList;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.FileUtils;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.home.SubjectWebActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 登面
 *
 * @author dqw
 * @Time 2015-6-25
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private MyShopApplication myApplication;

    private EditText etUsername, etPassword;
    private ImageButton btnAutoLogin;
    private Button btnLogin;

    // head image
    private ImageView iv_head;

    private ImageButton btnQQ,btnWeiXin,btnSina;

    //weibo
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    //qq
    public static Tencent mTencent;


    //QQ
    private  String token;
    private String openid;


    private UMShareAPI mShareAPI = null;

    // ImageLoader
    protected ImageLoader imageLoader;
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        mShareAPI = UMShareAPI.get(this);
        imageLoader = ImageLoader.getInstance();
        myApplication = (MyShopApplication) getApplicationContext();
        MyExceptionHandler.getInstance().setContext(this);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setBtnLoginState();
            }
        };
        etUsername = (EditText) findViewById(R.id.etUsername);
        etUsername.addTextChangedListener(textWatcher);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(textWatcher);
        btnAutoLogin = (ImageButton) findViewById(R.id.btnAutoLogin);
        btnAutoLogin.setSelected(true);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setActivated(false);

        btnQQ = (ImageButton)findViewById(R.id.btnQQ);
        btnQQ.setOnClickListener(this);
        btnWeiXin = (ImageButton)findViewById(R.id.btnWeiXin);
        btnWeiXin.setOnClickListener(this);
        btnSina = (ImageButton)findViewById(R.id.btnSina);
        btnSina.setOnClickListener(this);

        iv_head = (ImageView) findViewById(R.id.iv_head);
        loadUIData();
    }

    //返回按钮
    public void btnBackClick(View v) {
        finish();
    }

    //注册按钮
    public void btnRegisterClick(View v) {
        startActivity(new Intent(LoginActivity.this, RegisterMobileActivity.class));
        finish();
    }

    //自动登录选择
    public void btnAutoLoginClick(View v) {
        if (btnAutoLogin.isSelected()) {
            btnAutoLogin.setSelected(false);
        } else {
            btnAutoLogin.setSelected(true);
        }

    }

    //登录
    public void btnLoginClick(View v) {
        if (btnLogin.isActivated()) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username == null || username.trim().equals("")) {
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password == null || password.trim().equals("")) {
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            login(username, password);
        }
    }

    //处理登录按钮状态
    private void setBtnLoginState() {
        if (etUsername.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
            btnLogin.setActivated(false);
        } else {
            btnLogin.setActivated(true);
        }
    }

    //用户登录
    private void login(String username, String password) {
        String url = Constants.URL_LOGIN;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("client", "android");
        RemoteDataHandler.asyncPostDataString(url, params, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    ShopHelper.login(LoginActivity.this, myApplication, json);
                    LoginActivity.this.finish();
                } else {
                    ShopHelper.showApiError(LoginActivity.this, json);
                }
            }
        });
    }

    /**
     * 找回密码按钮点击
     */
    public void btnFindPasswordClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.WAP_FIND_PASSWORD));
        startActivity(intent);
    }



    /**
     * QQ同步登录
     *
     * @param token
     */
    private void loginQq(String token,String openid,String nickname,String avatar) {
        // 20161121 jiangtao
        // 对nickname url编码
        try {
            nickname = URLEncoder.encode(nickname, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = Constants.URL_CONNECT_QQ + "&token=" + token + "&open_id=" + openid + "&nickname=" + nickname + "&avatar=" + avatar +"&client=android";
        Log.e("qq_login_url", url);
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                Log.e("qq_login_response", data.toString());
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    LoginActivity.this.finish();
                    ShopHelper.login(LoginActivity.this, myApplication, json);
                } else {
                    ShopHelper.showApiError(LoginActivity.this, json);
                }
            }
        });
    }


    /**
     * 微博同步登录
     * @param accessToken
     * @param userId
     */
    private void loginWeibo(String accessToken, String userId) {

        // 20161121 jiangtao
        // 对userId url编码
        try {
            userId = URLEncoder.encode(userId, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = Constants.URL_CONNECT_WEIBO + "&accessToken=" + accessToken + "&userID=" + userId + "&client=android";
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                LogHelper.e("json", json);
                LogHelper.e("data", data.toString());
//                Toast.makeText(LoginActivity.this,data.toString(),Toast.LENGTH_LONG).show();
                if (data.getCode() == HttpStatus.SC_OK) {
                    ShopHelper.login(LoginActivity.this, myApplication,json);
                    LoginActivity.this.finish();
                } else {
                    ShopHelper.showApiError(LoginActivity.this, json);
                }
            }
        });
    }

    /**
     * 微信登录
     */
    private void loginWx(String access_token,String openid) {
        String url = Constants.URL_CONNECT_WX + "&access_token=" + access_token + "&openid=" + openid + "&client=android";
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    ShopHelper.login(LoginActivity.this, myApplication, json);
                    LoginActivity.this.finish();
                } else {
                    ShopHelper.showApiError(LoginActivity.this, json);
                }
            }
        });
    }


    //授权
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (data!=null){
                if(platform == SHARE_MEDIA.QQ) {
                    token = data.get("access_token");
                    openid = data.get("openid");
                    mShareAPI.getPlatformInfo(LoginActivity.this, platform, userinfo);

                }else if(platform == SHARE_MEDIA.WEIXIN){
                    String access_token = data.get("access_token");
                    String openid = data.get("openid");
                    loginWx(access_token,openid);

                }else if(platform == SHARE_MEDIA.SINA){
                    String accessToken = data.get("access_token");
                    String userId = data.get("uid");
                    loginWeibo(accessToken,userId);
                }

            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
        }
    };


    //获取用户信息
    private UMAuthListener userinfo = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            String nickname = map.get("screen_name");
            String avatar = map.get("profile_image_url");
            loginQq(token, openid, nickname, avatar);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText( getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View view) {
        SHARE_MEDIA platform = null;
        switch (view.getId()){
            case R.id.btnQQ:
                platform = SHARE_MEDIA.QQ;
                break;
            case R.id.btnWeiXin:
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.btnSina:
                platform = SHARE_MEDIA.SINA;
                break;
        }

        mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
    }


    /////////////////////20161103 jt 用户登录页面中部图片展示更换为广告图片////////////////////////////


    public void loadUIData() {
        boolean b;
        // 从缓存中读取
        try {
            b = loadDataFromLocal();
        } catch (IOException e) {
            e.printStackTrace();
            b = false;
        }

        if(b)
            return;

        RemoteDataHandler.asyncDataStringGet(Constants.URL_HOME, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        String json = data.getJson();
                        JSONArray arr = new JSONArray(json);

                        int size = null == arr ? 0 : arr.length();

                        for (int i = 0; i < size; i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            if(!obj.has("adv_list"))
                                continue;
                            JSONObject jo_adv = obj.getJSONObject("adv_list");
                            if(!jo_adv.has("item"))
                                return;
                            ArrayList<AdvertList> item = AdvertList.newInstanceList(jo_adv.getString("item"));
                            // 随机取出一个
                            AdvertList advertList = item.get(new Random().nextInt(item.size()));
                            initHeadImage(advertList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //Toast.makeText(MyShopApplication.context, R.string.load_error, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MyShopApplication.context, "展示图片加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean loadDataFromLocal() throws IOException {
        File advCacheFile = FileUtils.getAdvCacheFile();
        Map<String, String> cacheData = FileUtils.readCache(advCacheFile);

        if(cacheData == null)
            return false;
        // 5分钟 过期
        if(System.currentTimeMillis() - Long.parseLong(cacheData.get("time")) > 1000 * 60 * 5) // 1478226001714
            return  false;
        ArrayList<AdvertList> data = AdvertList.newInstanceList(cacheData.get("data"));
        initHeadImage(data.get(new Random().nextInt(data.size())));
        return true;
    }

    private void initHeadImage(AdvertList advertList) {
        /*//带缓存的商品图
        Glide.with(LoginActivity.this)
                .load(advertList.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nc_login_pic)  //设置占位图
                .error(R.drawable.nc_login_pic)      //加载错误图
                .into(iv_head);*/

        imageLoader.displayImage(advertList.getImage(), iv_head, options, animateFirstListener);
        OnImageViewClick(iv_head, advertList.getType(), advertList.getData());
    }

    public void OnImageViewClick(View view, final String type, final String data) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("keyword")) {//搜索关键字
                    Intent intent = new Intent(LoginActivity.this, GoodsListFragmentManager.class);
                    intent.putExtra("keyword", data);
                    intent.putExtra("gc_name", data);
                    startActivity(intent);
                } else if (type.equals("special")) {//专题编号
                    Intent intent = new Intent(LoginActivity.this, SubjectWebActivity.class);
                    // http://www.m0356.com/mobile/index.php?act=index&op=special&&special_id=xxx&&type=html
                    intent.putExtra("data", Constants.URL_SPECIAL + "&special_id=" + data + "&type=html");
                    startActivity(intent);
                } else if (type.equals("goods")) {//商品编号
                    Intent intent = new Intent(LoginActivity.this, GoodsDetailsActivity.class);
                    intent.putExtra("goods_id", data);
                    startActivity(intent);
                } else if (type.equals("url")) {//地址
                    Intent intent = new Intent(LoginActivity.this, SubjectWebActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else if(type.equals("store")){ // 店铺
                    //Toast.makeText(getActivity(), "store", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, newStoreInFoActivity.class);
                    intent.putExtra("store_id", data);
                    startActivity(intent);
                } else if(type.equals("category")){ // 分类
                    Intent intent = new Intent(LoginActivity.this, GoodsListFragmentManager.class);
                    intent.putExtra("gc_id", data);
                    startActivity(intent);
                }
            }
        });

    }


}
