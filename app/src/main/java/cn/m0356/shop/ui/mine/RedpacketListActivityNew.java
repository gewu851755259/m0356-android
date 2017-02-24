package cn.m0356.shop.ui.mine;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.ui.mine.fragment.MyRedpacketAddFragment;
import cn.m0356.shop.ui.mine.fragment.MyRedpacketListFragment;

/**
 * 红包列表
 *
 * @author jiangtao
 * @date 2016/10/11
 */

public class RedpacketListActivityNew extends BaseActivity implements XListView.IXListViewListener {


    private MyShopApplication myApplication;
    //private Handler mXLHandler;
    /*private RedpacketListViewAdapter adapter;
    private XListView listViewID;*/

    // 添加红包
    private MyRedpacketAddFragment addFragment;
    // 红包列表
    private MyRedpacketListFragment listFragment;

    // 记录当前显示fragment
    private Fragment currFragment;

    private FragmentManager fragmentManager;

    private Button btnRedpacketList;
    private Button btnRedpacketPasswordAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpacket_list_new);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("");
        setTabButton();

        // 初始化fragment
        addFragment = new MyRedpacketAddFragment();
        listFragment = new MyRedpacketListFragment();

        myApplication = (MyShopApplication) getApplicationContext();
        fragmentManager = getFragmentManager();

        // 默认显示我的红包
        fragmentManager.beginTransaction()
                .add(R.id.fl_redpacket_container, listFragment)
                .commit();
        // 设置当前默认fragment
        currFragment = listFragment;
        btnRedpacketList.setActivated(true);

        /*listViewID = (XListView) findViewById(R.id.listViewID);
        adapter = new RedpacketListViewAdapter(RedpacketListActivityNew.this);
        mXLHandler = new Handler();
        listViewID.setAdapter(adapter);
        listViewID.setXListViewListener(this);
        listViewID.setPullLoadEnable(false);*/
        //loadingListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadingListData();
    }

    /**
     * 设置头部切换按钮
     */

    private void setTabButton() {

        btnRedpacketList = (Button) findViewById(R.id.btnRedpacketList);
        btnRedpacketPasswordAdd = (Button) findViewById(R.id.btnRedpacketPasswordAdd);

        btnRedpacketList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  我的红包按钮 如果当前fragment不是我的红包fragment，则切换
                if(!(currFragment instanceof MyRedpacketListFragment)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_redpacket_container, listFragment)
                            .commit();
                    btnRedpacketList.setActivated(true);
                    btnRedpacketPasswordAdd.setActivated(false);
                    currFragment = listFragment;
                }
            }
        });

        btnRedpacketPasswordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 领取红包按钮  如果当前fragment不是领取红包fragment，则切换
                if(!(currFragment instanceof  MyRedpacketAddFragment)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_redpacket_container, addFragment)
                            .commit();
                    btnRedpacketPasswordAdd.setActivated(true);
                    btnRedpacketList.setActivated(false);
                    currFragment = addFragment;
                }
            }
        });

        /*btnRedpacketList.setActivated(true);
        btnRedpacketPasswordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RedpacketListActivityNew.this, RedpacketPasswordAddActivity.class));
                finish();
            }
        });*/
    }

    /**
     * 加载列表数据
     */
   /* public void loadingListData() {
        String url = Constants.URL_MEMBER_REDPACKET_LIST;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("rp_state", "unused");//unused-未使用 used-已使用 expire-已过期

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String updataTime = sdf.format(new Date(System.currentTimeMillis()));
        listViewID.setRefreshTime(updataTime);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {

                listViewID.stopRefresh();

                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("redpacket_list");
                        ArrayList<RedpacketInfo> redpacketInfoArrayList= RedpacketInfo.newInstanceList(objJson);
                        adapter.setList(redpacketInfoArrayList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RedpacketListActivityNew.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    @Override
    public void onRefresh() {
        //下拉刷新
        /*mXLHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingListData();
            }
        }, 1000);*/
    }

    @Override
    public void onLoadMore() {
        //上拉加载
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_redpacket_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
