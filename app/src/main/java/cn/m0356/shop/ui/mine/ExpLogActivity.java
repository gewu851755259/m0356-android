package cn.m0356.shop.ui.mine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.PointLogListViewAdapter;
import cn.m0356.shop.bean.PointLogInfo;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;


/**
 * 用户经验明细列表
 *
 * @author yml
 */
public class ExpLogActivity extends BaseActivity {
    private MyShopApplication myApplication;
    private TextView tvExp, tvStyle;
    private ListView lvExpLog;
    private ArrayList<PointLogInfo> expLogInfoArrayList;
    private PointLogListViewAdapter expLogListViewAdapter;

    int currentPage = 1;
    boolean isHasMore = true;
    boolean isLastRow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_log);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("经验明细");
        myApplication = (MyShopApplication) getApplicationContext();

        tvExp = (TextView) findViewById(R.id.tvPoint);
        tvStyle = (TextView) findViewById(R.id.tvStyle);
        tvStyle.setText("我的经验");

        lvExpLog = (ListView) findViewById(R.id.lvPointLog);
        lvExpLog.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (isHasMore && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    isLastRow = false;
                    currentPage += 1;
                    loadExpLog();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
            }
        });
        expLogInfoArrayList = new ArrayList<PointLogInfo>();
        expLogListViewAdapter = new PointLogListViewAdapter(ExpLogActivity.this);
        lvExpLog.setAdapter(expLogListViewAdapter);

        loadExp();
        loadExpLog();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadExp();
//        loadExpLog();
    }

    /**
     * 读取经验值
     */
    private void loadExp() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_MY_EXP, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONObject expObj = obj.getJSONObject("member_exp");
                        tvExp.setText(expObj.optString("exppoints"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 读取经验明细
     */
    private void loadExpLog() {
        String url = Constants.URL_MEMBER_EXP_LOG + "&curpage=" + currentPage + "&page=" + Constants.PAGESIZE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        LogHelper.d("ExpLog", url + "&key=" + myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    isHasMore = data.isHasMore();

                    if (currentPage == 1) {
                        expLogInfoArrayList.clear();
                    }

                    try {
                        JSONObject obj = new JSONObject(json);
                        String expJson = obj.getString("log_list");
                        ArrayList<PointLogInfo> list = PointLogInfo.newInstanceExpList(expJson);
                        if (list.size() > 0) {
                            expLogInfoArrayList.addAll(list);
                            expLogListViewAdapter.setList(expLogInfoArrayList);
                            expLogListViewAdapter.notifyDataSetChanged();
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(ExpLogActivity.this, json);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_point_log, menu);
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
