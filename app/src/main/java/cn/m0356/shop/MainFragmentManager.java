package cn.m0356.shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.readystatesoftware.viewbadger.BadgeView;

import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.common.UpdateManageNew;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.cart.CartFragment;
import cn.m0356.shop.ui.home.HomeFragment;
import cn.m0356.shop.ui.home.HomeFragment20160804;
import cn.m0356.shop.ui.mine.LoginActivity;
import cn.m0356.shop.ui.mine.MineFragment;
import cn.m0356.shop.ui.mine.UpdateManager;
import cn.m0356.shop.ui.type.OneTypeFragment;
import cn.m0356.shop.ui.type.VoucherActivity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * 底部菜单管理界面
 *
 * @author KingKong-HE
 * @Time 2014-12-30
 * @Email KingKong@QQ.COM
 */
public class MainFragmentManager extends FragmentActivity {

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    // 变量，标识选中的按钮 0首页 1购物车 2晋城购 4注册
    private static int check;

    /**
     * 定义首页、分类、购物车、我的Fragment
     */
    private HomeFragment homeFragment;
    private OneTypeFragment typeFragment;
    private MineFragment mineFragment;
    private CartFragment cartFragment;

    /**
     * 定义首页、分类、购物车、我的tab的图标
     */
    private RadioButton btnHomeID;
    private RadioButton btnClassID;
    private RadioButton btnCartID;
    private RadioButton btnMineID;


    private Button btn3;
    private BadgeView badge;

    private MyShopApplication myApplication;

    /**
     * 对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    // 20161011
    private OnScrollTopListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        MyExceptionHandler.getInstance().setContext(this);
        fragmentManager = getSupportFragmentManager();

        myApplication = (MyShopApplication) getApplicationContext();

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBoradcastReceiver();
        myApplication.getmSocket().connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        myApplication.getmSocket().disconnect();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SHOW_CART_URL)) {
                CartIn();//显示购物车
                btnCartID.setChecked(true);

            } else if (action.equals(Constants.SHOW_HOME_URL)) {
                HomeIn();//显示首页
                btnHomeID.setChecked(true);
            } else if (action.equals(Constants.SHOW_Classify_URL)) {
                TupeIn();//显示分类页面
                btnClassID.setChecked(true);
            } else if (action.equals(Constants.SHOW_Mine_URL)) {
                MineIn();//显示我的商城页面
                btnMineID.setChecked(true);
            } else if (action.equals(Constants.SHOW_CART_NUM)) {
                setCartNumShow();
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SHOW_CART_URL);
        myIntentFilter.addAction(Constants.SHOW_HOME_URL);
        myIntentFilter.addAction(Constants.SHOW_Classify_URL);
        myIntentFilter.addAction(Constants.SHOW_Mine_URL);
        myIntentFilter.addAction(Constants.SHOW_CART_NUM);
        registerReceiver(mBroadcastReceiver, myIntentFilter); //注册广播       
    }


    /**
     * 隐藏所有的fragment
     *
     * @param transaction 用于对fragment进行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (typeFragment != null) {
            transaction.hide(typeFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
        if (cartFragment != null) {
            transaction.hide(cartFragment);
        }
    }

    /**
     * 初始化界面，并设置3个tab的监听
     */
    private void initViews() {
        // //////////////////// find View ////////////////////////////
        btnHomeID = (RadioButton) this.findViewById(R.id.btnHomeID);
        btnClassID = (RadioButton) this.findViewById(R.id.btnClassID);
        btnCartID = (RadioButton) this.findViewById(R.id.btnCartID);
        btnMineID = (RadioButton) this.findViewById(R.id.btnMineID);

        btn3 = (Button) this.findViewById(R.id.btn3);
        badge = new BadgeView(this, btn3);
        badge.setTextSize(10);
        badge.setVisibility(View.GONE);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        if (!(myApplication.getLoginKey().equals("null") || myApplication.getLoginKey().equals(""))) {
            setCartNumShow();
        }

        MyRadioButtonClickListener listener = new MyRadioButtonClickListener();
        btnHomeID.setOnClickListener(listener);
        btnClassID.setOnClickListener(listener);
        btnCartID.setOnClickListener(listener);
        btnMineID.setOnClickListener(listener);


        //首次进入显示首页界面
        HomeIn();

        //判断是否有新版本
        versionUpdate();

    }

    public void setCartNumShow() {

        // 20161102 添加判断登录状态
        if(ShopHelper.isLoginNoSkip(myApplication.getLoginKey())){
            String url = Constants.URL_GET_CART_NUM;
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("key", myApplication.getLoginKey());
            RemoteDataHandler.asyncPostDataString(url, params, new Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        try {
                            JSONObject obj = new JSONObject(json);
                            String num = obj.getString("cart_count");
//                        Log.i("QINM",num);
                            badge.setText(num);
                            badge.show();
                        } catch (JSONException e) {
                            Toast.makeText(MainFragmentManager.this, "获取购物车数量失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        } else {
            badge.hide();
        }

    }

    /**
     * 设置开启的tab首页页面
     */
    public void HomeIn() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            transaction.add(R.id.content, homeFragment);
        } else {
            transaction.show(homeFragment);
        }
        transaction.commitAllowingStateLoss();
        check = 0;
    }

    /**
     * 设置开启的tab分类页面
     */
    public void TupeIn() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (typeFragment == null) {
            typeFragment = new OneTypeFragment();
            transaction.add(R.id.content, typeFragment);
        } else {
            transaction.show(typeFragment);
        }
        transaction.commitAllowingStateLoss();
        check = 1;
    }

    /**
     * 设置开启的tab我的页面
     */
    public void MineIn() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (mineFragment == null) {
            mineFragment = new MineFragment();
            transaction.add(R.id.content, mineFragment);
        } else {
            mineFragment.setLoginInfo();
            transaction.show(mineFragment);
        }
        transaction.commitAllowingStateLoss();
        check = 3;
    }

    /**
     * 设置开启的tab购物车页面
     */
    public void CartIn() {
        // 如果用户登录
        if (ShopHelper.isLogin(MainFragmentManager.this, myApplication.getLoginKey())) {
            // 如果登录
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            hideFragments(transaction);
            if (cartFragment == null) {
                cartFragment = new CartFragment();
                transaction.add(R.id.content, cartFragment);
            } else {
                transaction.show(cartFragment);
            }
            transaction.commitAllowingStateLoss();


        } else {
            // 如果未登录 跳转至登录界面
            // 通过标识恢复按钮选中状态
            if (check == 0)
                btnHomeID.setChecked(true);
            else if (check == 1)
                btnClassID.setChecked(true);
            else if (check == 3)
                btnMineID.setChecked(true);
        }

    }

    /**
     * 获取是否有最新版本
     */
    public void versionUpdate() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_VERSION_UPDATE, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("version");
                        String urlJSON = obj.getString("url");
                        String VersionName = SystemHelper.getAppVersionName(MainFragmentManager.this);
                        if (VersionName.equals(objJson) || VersionName.equals("")) {
//								Toast.makeText(MainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();;
                        } else {
                            //这里来检测版本是否需要更新
                            UpdateManager mUpdateManager = new UpdateManager(MainFragmentManager.this, urlJSON);
                            mUpdateManager.checkUpdateInfo();

                            // TODO: 2017/1/22 后台更新 待修复
                            //new UpdateManageNew(MainFragmentManager.this, objJson, urlJSON, VersionName);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//						Toast.makeText(MainFragmentManager.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setListener(OnScrollTopListener listener) {
        this.listener = listener;
    }

    class MyRadioButtonClickListener implements View.OnClickListener {
        public void onClick(View v) {
            RadioButton btn = (RadioButton) v;
            switch (btn.getId()) {
                case R.id.btnHomeID:
                    if (homeFragment.isVisible()) {
                        if (listener != null)
                            listener.onScrollTop();
                    } else {
                        HomeIn();
                    }
                    break;
                case R.id.btnClassID:
                    TupeIn();
                    break;
                case R.id.btnCartID:
                    CartIn();
                    break;
                case R.id.btnMineID:
                    MineIn();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
//            System.exit(0);
        }
    }

    public static interface OnScrollTopListener {
        void onScrollTop();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "主界面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "主界面");
    }*/

}
