package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.FavoritesGoodsGridViewAdapter;
import cn.m0356.shop.bean.FavoritesList;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.custom.NCDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ncinterface.INCOnDialogConfirm;
import cn.m0356.shop.ncinterface.INCOnItemDel;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * 暂时废弃
 * @author dqw
 * @Time 2015/8/12
 */
public class FavGoodsListActivity extends BaseActivity {

    private MyShopApplication myApplication;
    private Handler mXLHandler;
    public int pageno = 1;
    private ArrayList<FavoritesList> favoritesLists;
    private FavoritesGoodsGridViewAdapter adapter;
    private GridView gvFavGoods;
    private INCOnItemDel incOnItemDel;
    boolean isHasMore = true;
    boolean isLastRow = false;
    private NCDialog ncDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favgoods_list_view);
        MyExceptionHandler.getInstance().setContext(this);
        myApplication = (MyShopApplication) getApplicationContext();
        mXLHandler = new Handler();

        setTabButton();

        gvFavGoods = (GridView) findViewById(R.id.gvFavGoods);
        favoritesLists = new ArrayList<FavoritesList>();
        incOnItemDel = new INCOnItemDel() {
            @Override
            public void onDel(String id, int position) {
                deleteFav(id, position);
            }
        };
        adapter = new FavoritesGoodsGridViewAdapter(FavGoodsListActivity.this, incOnItemDel);
        gvFavGoods.setAdapter(adapter);

        //下拉加载更多
        gvFavGoods.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (isHasMore && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    isLastRow = false;
                    pageno += 1;
                    loadingfavoritesListData();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
            }
        });

        loadingfavoritesListData();

        setCommonHeader("");
        setListEmpty(R.drawable.nc_icon_fav_goods, "您还没有关注任何商品", "可以去看看哪些商品值得收藏");
    }

    /**
     * 设置头部切换按钮
     */
    private void setTabButton() {
        Button btnFavGoods = (Button) findViewById(R.id.btnFavGoods);
        btnFavGoods.setActivated(true);
        Button btnFavStore = (Button) findViewById(R.id.btnFavStore);
        btnFavStore.setActivated(false);
        btnFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavGoodsListActivity.this, FavStoreListActivity.class));
                finish();
            }
        });
    }

    /**
     * 加载列表数据
     */
    public void loadingfavoritesListData() {
        String url = Constants.URL_FAVORITES_LIST + "&curpage=" + pageno + "&page=" + Constants.PAGESIZE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {

                    isHasMore = data.isHasMore();

                    if (pageno == 1) {
                        favoritesLists.clear();
                        hideListEmpty();
                    }

                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("favorites_list");
                        ArrayList<FavoritesList> fList = FavoritesList.newInstanceList(objJson);
                        if (fList.size() > 0) {
                            favoritesLists.addAll(fList);
                            adapter.setfList(favoritesLists);
                            adapter.notifyDataSetChanged();
                        } else {
                            showListEmpty();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(FavGoodsListActivity.this, json);
                }
            }
        });
    }

    /**
     * 收藏删除
     *
     * @param fav_id 商品ID
     */
    public void deleteFav(final String fav_id, final int position) {

        ncDialog = new NCDialog(FavGoodsListActivity.this);
        ncDialog.setText1("确认删除该商品收藏?");
        ncDialog.setOnDialogConfirm(new INCOnDialogConfirm() {
            @Override
            public void onDialogConfirm() {
                String url = Constants.URL_DELETE_FAV;

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fav_id", fav_id);
                params.put("key", myApplication.getLoginKey());

                RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            Toast.makeText(FavGoodsListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            adapter.removeItem(position);
                            adapter.notifyDataSetChanged();
                        } else {
                            ShopHelper.showApiError(FavGoodsListActivity.this, json);
                        }
                    }
                });
            }
        });
        ncDialog.showPopupWindow();
            }
        }

