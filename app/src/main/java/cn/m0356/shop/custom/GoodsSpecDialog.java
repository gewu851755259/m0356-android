package cn.m0356.shop.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.m0356.shop.R;
import cn.m0356.shop.bracode.core.ViewfinderView;
import cn.m0356.shop.ui.seller.SellerGoodsConfigActivity;

/**
 * Created by jiangtao on 2016/12/8.
 */
public class GoodsSpecDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private ListView lv_spec;
    private List<Map<String, String>> mList;
    private HashMap<String, String> strageMap;

    public static GoodsSpecDialog newInstance() {
        GoodsSpecDialog f = new GoodsSpecDialog();

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_goods_spec, container);
        lv_spec = (ListView) view.findViewById(R.id.lv_spec);
        lv_spec.setAdapter(new MyAdapter(getActivity(), 0, this.mList));
        lv_spec.setOnItemClickListener(this);

        return view;
    }




    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void show(FragmentManager fm, List<Map<String, String>> mList, HashMap<String, String> strageMap){
        this.show(fm, "spec");
        this.mList = mList;
        this.strageMap = strageMap;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, String> map = mList.get(position);
        String json = map.get("json");
        String spec = map.get("spec");
        String strage = strageMap.get(spec);
        SellerGoodsConfigActivity.start(getActivity(),strage, spec, json);

    }

    private class MyAdapter extends ArrayAdapter<Map<String, String>>{

        public MyAdapter(Context context, int textViewResourceId, List<Map<String, String>> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getActivity(), R.layout.item_goods_spec, null);
            Map<String, String> item = getItem(position);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setText(item.get("spec"));
            view.setTag(item.get("json"));
            return view;
        }
    }


}
