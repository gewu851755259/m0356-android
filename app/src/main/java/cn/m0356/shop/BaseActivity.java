package cn.m0356.shop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by dqw on 2015/5/25.
 */
public class BaseActivity extends Activity {
    protected ImageButton btnBack;
    protected ImageView btnRight;
    protected TextView tvCommonTitle;
    protected TextView tvCommonTitleBorder;
    private LinearLayout llListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置Activity通用标题文字
     */
    protected void setCommonHeader(String title) {
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnRight = (ImageView) findViewById(R.id.btnRight);
        tvCommonTitle = (TextView) findViewById(R.id.tvCommonTitle);
        tvCommonTitleBorder = (TextView) findViewById(R.id.tvCommonTitleBorder);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvCommonTitle.setText(title);
    }

    /**
     * 空列表背景
     */
    protected void setListEmpty(int resId, String title, String subTitle) {
        llListEmpty = (LinearLayout) findViewById(R.id.llListEmpty);
        ImageView ivListEmpty = (ImageView) findViewById(R.id.ivListEmpty);
        ivListEmpty.setImageResource(resId);
        TextView tvListEmptyTitle = (TextView) findViewById(R.id.tvListEmptyTitle);
        TextView tvListEmptySubTitle = (TextView) findViewById(R.id.tvListEmptySubTitle);
        tvListEmptyTitle.setText(title);
        tvListEmptySubTitle.setText(subTitle);
    }

    /**
     * 显示空列表背景
     */
    protected void showListEmpty() {
        llListEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏空列表背景
     */
    protected void hideListEmpty() {
        llListEmpty.setVisibility(View.GONE);
    }

    /**
     * 隐藏返回按钮
     */
    protected void hideBack() {
        btnBack.setVisibility(View.INVISIBLE);
    }

    /**
     * 隐藏分隔线
     */
    protected void hideCommonHeaderBorder() {
        tvCommonTitleBorder.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示右侧按钮
     */
    protected void showBtnRight() {
        btnRight.setVisibility(View.VISIBLE);
    }

    /**
     * 显示右侧按钮
     */
    protected void hideBtnRight() {
        btnRight.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置右侧按钮的背景
     * @param resId 背景资源id
     */
    protected void setBtnRightImgResource(int resId){
        btnRight.setImageResource(resId);
    }

    /**
     * 设置右侧按钮点击事件
     * @param l
     */
    protected void setBtnRightClickListener(View.OnClickListener l){
        btnRight.setOnClickListener(l);
    }

}
