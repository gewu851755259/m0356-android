package cn.m0356.shop.ui.mine.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.adapter.RedpacketListViewAdapter;
import cn.m0356.shop.bean.RedpacketInfo;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/10/11.
 */
public class MyRedpacketListFragment extends Fragment implements XListView.IXListViewListener {

    private Handler mXLHandler;
    private RedpacketListViewAdapter adapter;
    private XListView listViewID;
    private MyShopApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redpacket_list, null);

        myApplication = (MyShopApplication) getActivity().getApplication();

        listViewID = (XListView) view.findViewById(R.id.listViewID);
        adapter = new RedpacketListViewAdapter(getActivity());
        mXLHandler = new Handler();
        listViewID.setAdapter(adapter);
        listViewID.setXListViewListener(this);
        listViewID.setPullLoadEnable(false);
        loadingListData();

        return view;
    }

    @Override
    public void onRefresh() {
        //下拉刷新
        mXLHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingListData();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 加载列表数据
     */
    public void loadingListData() {
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
                    Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
