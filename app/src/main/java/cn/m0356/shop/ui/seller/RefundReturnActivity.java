package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.ui.seller.Fragment.RefundReturnFragment;

/**
 * Created by jiangtao on 2016/12/9.
 */
public class RefundReturnActivity extends Activity implements View.OnClickListener {
    public static final String TYPE_RETURN = "type_return";
    public static final String TYPE_RETURN_LOCK = "type_return_lock";
    public static final String TYPE_REFUND = "type_refund";
    public static final String TYPE_REFUND_LOCK = "type_refund_lock";

    private ViewPager vp;
    private List<RefundReturnFragment> list;
    private Button btnRefund, btnRefundLock, btnReturn, btnReturnLock;

    private MyShopApplication app;

    public static void start(Context context, int pos) {
        Intent intent = new Intent(context, RefundReturnActivity.class);
        intent.putExtra("type", pos);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_return);
        findView();
        initData();
        addListener();
    }

    private void initData() {
        app = (MyShopApplication) getApplication();

        list = new ArrayList<RefundReturnFragment>();
        RefundReturnFragment refundFragment = RefundReturnFragment.newInstance(TYPE_REFUND_LOCK, app.getSeller_key(), app.getStore_id());
        RefundReturnFragment refundLockFragment = RefundReturnFragment.newInstance(TYPE_REFUND, app.getSeller_key(), app.getStore_id());
        RefundReturnFragment returnFragment = RefundReturnFragment.newInstance(TYPE_RETURN_LOCK, app.getSeller_key(), app.getStore_id());
        RefundReturnFragment returnLockFragment = RefundReturnFragment.newInstance(TYPE_RETURN, app.getSeller_key(), app.getStore_id());

        list.add(refundFragment);
        list.add(refundLockFragment);
        list.add(returnFragment);
        list.add(returnLockFragment);

        vp.setAdapter(new MyAdapter(getFragmentManager()));

        int type = getIntent().getIntExtra("type", 0);
        addState(type);
        vp.setCurrentItem(type, false);

    }

    private void addListener() {
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addState(position);
            }
        });
        btnRefundLock.setOnClickListener(this);
        btnRefund.setOnClickListener(this);
        btnReturnLock.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
    }

    private void findView() {
        vp = (ViewPager) findViewById(R.id.vp_refund_return);
        btnRefund = (Button) findViewById(R.id.btn_refund);
        btnRefundLock = (Button) findViewById(R.id.btn_refund_lock);
        btnReturn = (Button) findViewById(R.id.btn_return);
        btnReturnLock = (Button) findViewById(R.id.btn_return_lock);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refund:
                vp.setCurrentItem(1, false);
                break;
            case R.id.btn_refund_lock:
                vp.setCurrentItem(0, false);
                break;
            case R.id.btn_return:
                vp.setCurrentItem(3, false);
                break;
            case R.id.btn_return_lock:
                vp.setCurrentItem(2, false);
                break;
        }
    }

    private class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private void clearState(){
        btnRefundLock.setActivated(false);
        btnRefund.setActivated(false);
        btnReturn.setActivated(false);
        btnReturnLock.setActivated(false);
    }

    private void addState(int pos){
        clearState();
        switch (pos){
            case 0:
                btnRefundLock.setActivated(true);
                break;
            case 1:
                btnRefund.setActivated(true);
                break;
            case 2:
                btnReturnLock.setActivated(true);
                break;
            case 3:
                btnReturn.setActivated(true);
                break;
        }
    }

}
