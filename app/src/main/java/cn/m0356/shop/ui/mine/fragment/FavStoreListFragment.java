package cn.m0356.shop.ui.mine.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.FavoritesStoreListViewAdapter;
import cn.m0356.shop.bean.FavStoreList;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.custom.NCDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ncinterface.INCOnDialogConfirm;
import cn.m0356.shop.ncinterface.INCOnItemDel;
import cn.m0356.shop.ui.mine.FavGoodsListActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;

/**
 * Created by jiangtao on 2016/10/25.
 */
public class FavStoreListFragment extends Fragment {

    private MyShopApplication myApplication;
    public int pageno = 1;
    private ArrayList<FavStoreList> favoritesLists;
    private FavoritesStoreListViewAdapter adapter;
    private ListView lvFavStore;
    private INCOnItemDel incOnItemDel;
    boolean isHasMore = true;
    boolean isLastRow = false;
    private NCDialog ncDialog;
    private View view;
    private LinearLayout llListEmpty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fav_store, null);
        myApplication = (MyShopApplication) getActivity().getApplication();
        MyExceptionHandler.getInstance().setContext(getActivity());

        lvFavStore = (ListView) view.findViewById(R.id.lvFavStore);
        favoritesLists = new ArrayList<FavStoreList>();
        incOnItemDel = new INCOnItemDel() {
            @Override
            public void onDel(String id, int position) {
                deleteFav(id, position);
            }
        };
        adapter = new FavoritesStoreListViewAdapter(getActivity(), incOnItemDel);
        lvFavStore.setAdapter(adapter);
        loadingfavoritesListData();//加载列表数据

        lvFavStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final FavStoreList bean = (FavStoreList) lvFavStore.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), newStoreInFoActivity.class);
                intent.putExtra("store_id", bean.getStore_id());
                startActivity(intent);
            }
        });

        lvFavStore.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                if ((firstVisibleItem + visibleItemCount) == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
            }
        });

        setListEmpty(R.drawable.nc_icon_fav_store, "您还没有关注任何店铺", "可以去看看哪些店铺值得收藏");

        return view;
    }

    /**
     * 加载列表数据
     */
    public void loadingfavoritesListData() {
        String url = Constants.URL_STORE_FAV_LIST + "&curpage=" + pageno + "&page=" + Constants.PAGESIZE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
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
                        ArrayList<FavStoreList> fList = FavStoreList.newInstanceList(objJson);
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
                    ShopHelper.showApiError(getActivity(), json);
                }
            }
        });
    }

    /**
     * 删除店铺收藏
     */
    public void deleteFav(final String store_id, final int position) {

        ncDialog = new NCDialog(getActivity());
        ncDialog.setText1("确认删除该店铺收藏?");
        ncDialog.setOnDialogConfirm(new INCOnDialogConfirm() {
            @Override
            public void onDialogConfirm() {
                String url = Constants.URL_STORE_DELETE;

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("store_id", store_id);
                params.put("key", myApplication.getLoginKey());

                RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            Toast.makeText(MyShopApplication.context, "删除成功", Toast.LENGTH_SHORT).show();
                            adapter.removeItem(position);
                            adapter.notifyDataSetChanged();
                        } else {
                            ShopHelper.showApiError(getActivity(), json);
                        }
                    }
                });
            }
        });
        ncDialog.showPopupWindow();

    }

    /**
     * 空列表背景
     */
    protected void setListEmpty(int resId, String title, String subTitle) {
        llListEmpty = (LinearLayout) view.findViewById(R.id.llListEmpty);
        ImageView ivListEmpty = (ImageView) view.findViewById(R.id.ivListEmpty);
        ivListEmpty.setImageResource(resId);
        TextView tvListEmptyTitle = (TextView) view.findViewById(R.id.tvListEmptyTitle);
        TextView tvListEmptySubTitle = (TextView) view.findViewById(R.id.tvListEmptySubTitle);
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


}


