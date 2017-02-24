package cn.m0356.shop.ui.type;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.Where;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.Notice;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LoadImage;
import cn.m0356.shop.common.OkHttpUtil;
import cn.m0356.shop.common.ScreenUtil;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyProgressDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/7/26.
 */
public class NoticeActivity extends Activity {

    private TextView tvTitle, tvContent, tvTime;
    private MyProgressDialog progressDialog;
    private Notice notice;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        progressDialog = new MyProgressDialog(this ,"正在加载中...",R.anim.progress_round);
        progressDialog.show();
        String id = getIntent().getStringExtra("notice_id");
        findView();
        initListener();
        loadNoticeDetail(id);

    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadNoticeDetail(String id) {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_DETAIL_NOTICE + id, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                progressDialog.dismiss();
                if(data.getCode() == 200){
                    try {
                        JSONObject jo = new JSONObject(data.getJson());
                        notice = Notice.newInstance(jo.getJSONObject("article_list").toString());
                        updateData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void updateData() {
        tvTitle.setText(notice.article_title);

        //tvContent.setText(Html.fromHtml("<img src=\"/data/upload/shop/editor/20160728165731_53821.jpg\" alt=\"\" />",imageGetter, null));
        tvTime.setText(SystemHelper.getTimeStr(notice.article_time));
        getHtml();
    }

    private void getHtml(){
        new AsyncTask<Void,Void,Spanned>(){

            @Override
            protected Spanned doInBackground(Void... params) {
                Html.ImageGetter imageGetter = new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(final String source) {

                        Drawable drawable = null;
                        try {
                            drawable = Drawable.createFromStream(new URL(Constants.PROTOCOL + Constants.HOST + source).openStream(), "");
                            float scale = (float)( ScreenUtil.getScreenWidth(NoticeActivity.this) / 2) / drawable.getIntrinsicWidth();
                            drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth() * scale), (int)(drawable.getIntrinsicHeight() * scale) );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return drawable;
                    }
                };
                Spanned spanned = Html.fromHtml(notice.article_content, imageGetter, null);
                return spanned;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                tvContent.setText(spanned);
            }
        }.execute();
    }

    private void findView() {
        tvTitle = (TextView) findViewById(R.id.tv_notice_title);
        tvContent = (TextView) findViewById(R.id.tv_notice_content);
        tvTime = (TextView) findViewById(R.id.tv_notice_time);
        btnBack = (ImageButton) findViewById(R.id.btnBack);

    }
}
