package cn.m0356.shop.ui.mine;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.ui.mine.fragment.FavGoodListFragment;
import cn.m0356.shop.ui.mine.fragment.FavStoreListFragment;
import cn.m0356.shop.ui.mine.fragment.MyRedpacketAddFragment;
import cn.m0356.shop.ui.mine.fragment.MyRedpacketListFragment;

/**
 * 收藏列表
 * 老版本通过两个activity切换，修改为切换fragment
 * @author jiangtao
 * @date 2016/10/25
 */

public class FavListActivity extends BaseActivity {



    // 商品收藏
    private FavGoodListFragment mFavGoodListFragment;
    // 店铺收藏
    private FavStoreListFragment mFavStoreListFragment;

    // 记录当前显示fragment
    private Fragment currFragment;

    private FragmentManager fragmentManager;

    private Button btnFavGoods;
    private Button btnFavStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("");
        setTabButton();

        int page = getIntent().getIntExtra("page", 0);

        // 初始化fragment
        mFavGoodListFragment = new FavGoodListFragment();
        mFavStoreListFragment = new FavStoreListFragment();

        fragmentManager = getFragmentManager();

        // 根据用户点击选择显示不同页面
        fragmentManager.beginTransaction()
                .add(R.id.fl_fav_container, page == 0 ? mFavGoodListFragment : mFavStoreListFragment)
                .commit();

        // 设置当前默认fragment
        currFragment = (page == 0 ? mFavGoodListFragment : mFavStoreListFragment);
        // 设置按钮
        if(page == 0)
            btnFavGoods.setActivated(true);
        else
            btnFavStore.setActivated(true);

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

        btnFavStore = (Button) findViewById(R.id.btnFavStore);
        btnFavGoods = (Button) findViewById(R.id.btnFavGoods);

        btnFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  店铺收藏 按钮
                if(!(currFragment instanceof FavStoreListFragment)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_fav_container, mFavStoreListFragment)
                            .commit();
                    btnFavStore.setActivated(true);
                    btnFavGoods.setActivated(false);
                    currFragment = mFavStoreListFragment;
                }
            }
        });

        btnFavGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 商品收藏
                if(!(currFragment instanceof  FavGoodListFragment)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_fav_container, mFavGoodListFragment)
                            .commit();
                    btnFavGoods.setActivated(true);
                    btnFavStore.setActivated(false);
                    currFragment = mFavGoodListFragment;
                }
            }
        });

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
