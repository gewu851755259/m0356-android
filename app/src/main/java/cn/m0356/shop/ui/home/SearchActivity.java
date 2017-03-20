package cn.m0356.shop.ui.home;

import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.adapter.SearchGridViewAdapter;
import cn.m0356.shop.adapter.SearchListViewAdapter;
import cn.m0356.shop.bean.Search;
import cn.m0356.shop.common.DatabaseHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.common.T;
import cn.m0356.shop.custom.MyGridView;
import cn.m0356.shop.custom.MyListView;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;
import cn.m0356.shop.ui.type.SearchStoreActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * 商品搜索
 *
 * @author dqw
 * @Time 2015/7/10
 */
public class SearchActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    private MyShopApplication myApplication;

    private String searchHotName;
    private String searchHotValue;

    private EditText etSearchText;
    private Button btnSearch;

    //搜索关键词
    private MyGridView gvSearchKeyList;
    private SearchGridViewAdapter searchKeyListAdapter;

    //历史搜索
    private MyListView searchListView;
    private SearchListViewAdapter adapter;
    private RuntimeExceptionDao<Search, Integer> searchDAO;

    private LinearLayout llHistory;

    // 搜索店铺提示
    private TextView tvHintText;
    // 默认隐藏
    private LinearLayout ll_input_hint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        myApplication = (MyShopApplication) getApplicationContext();
        MyExceptionHandler.getInstance().setContext(this);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvHintText = (TextView) findViewById(R.id.tv_search_hint);
        ll_input_hint = (LinearLayout) findViewById(R.id.ll_input_hint);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etSearchText = (EditText) findViewById(R.id.etSearchText);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    ll_input_hint.setVisibility(View.VISIBLE);
                    tvHintText.setText("点击搜索\"" + s.toString() + "\"店铺");
                } else {
                    ll_input_hint.setVisibility(View.GONE);
                }
            }
        });
        tvHintText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 搜索店铺
                enterQueryStore();
            }
        });
        btnSearch = (Button) findViewById(R.id.btnSearch);

        //搜索关键词
        gvSearchKeyList = (MyGridView) findViewById(R.id.gvSearchKeyList);
        searchKeyListAdapter = new SearchGridViewAdapter(SearchActivity.this);
        gvSearchKeyList.setAdapter(searchKeyListAdapter);
        searchKeyListAdapter.setSearchLists(myApplication.getSearchKeyList());
        searchKeyListAdapter.notifyDataSetChanged();
        gvSearchKeyList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String keyword = myApplication.getSearchKeyList().get(i);
                showGoodsList(keyword);
            }
        });

        //历史搜索
        llHistory = (LinearLayout) findViewById(R.id.llHistory);
        searchListView = (MyListView) findViewById(R.id.searchListView);
        adapter = new SearchListViewAdapter(SearchActivity.this);
        searchDAO = getHelper().getSearchDataDao();
        searchListView.setAdapter(adapter);
        final List<Search> sList = queryAll();
        adapter.setSearchLists(sList);
        adapter.notifyDataSetChanged();
        if (sList.size() <= 0) {
            llHistory.setVisibility(View.GONE);
        } else {
            llHistory.setVisibility(View.VISIBLE);
        }

        //设置热门搜索词
        searchHotName = myApplication.getSearchHotName();
        searchHotValue = myApplication.getSearchHotValue();
        if (searchHotName != null && !searchHotName.equals("")) {
            etSearchText.setHint(searchHotName);
        } else {
            etSearchText.setHint(R.string.default_search_text);
        }

        //清空搜索历史
        Button btnClearHistory = (Button) findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDAO.delete(sList);
                llHistory.setVisibility(View.GONE);
            }
        });

        //搜索按钮点击
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(-1 != SystemHelper.getNetworkType(SearchActivity.this)){
                    String keyword = etSearchText.getText().toString();
                    if (keyword.equals("") || keyword.equals("") || keyword == null) {
//                        if (searchHotValue.equals("") || searchHotValue == null) {
//                        if (searchHotValue == null || searchHotValue.equals("")) {
                        if (TextUtils.isEmpty(searchHotValue)) {
                            Toast.makeText(SearchActivity.this, "内容不能为空",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            keyword = searchHotValue;
                        }
                    }
                    //最多保留10条搜索记录
                    if (sList.size() > 7) {
                        searchDAO.delete(sList.get(0));
                    }
                    searchDAO.createIfNotExists(new Search(keyword));
                    showGoodsList(keyword);
                }else {
                    T.showShort(SearchActivity.this,getString(R.string.network_inavaible));
                }

            }
        });

        //历史搜索记录点击
        searchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Search bean = (Search) searchListView.getItemAtPosition(arg2);
                showGoodsList(bean.getSearchKeyWord());
            }
        });
    }

    private void enterQueryStore() {
        Intent intent = new Intent();
        intent.setClass(this, SearchStoreActivity.class);
        intent.putExtra("keyword", etSearchText.getText().toString());
        startActivity(intent);
    }

    /**
     * 显示商品搜索结果列表
     */
    private void showGoodsList(String keyword) {
        Intent intent = new Intent(SearchActivity.this,
                GoodsListFragmentManager.class);
        intent.putExtra("keyword", keyword);
        SearchActivity.this.startActivity(intent);
        SearchActivity.this.finish();
    }

    /**
     * 查询所有的
     */
    private List<Search> queryAll() {
        List<Search> searchList = searchDAO.queryForAll();
        return searchList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "查询界面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "查询界面");
    }

}
