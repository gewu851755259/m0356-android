package cn.m0356.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.m0356.shop.bean.CouInFo;

/**
 * 加价购适配器
 */
public class CouListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CouInFo> list;
    private DisplayMetrics metrics;

    public CouListViewAdapter(Context context) {
        this.context = context;
        metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(ArrayList<CouInFo> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = new LinearLayout(context);
            ((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);
            TextView desc = new TextView(context);
            desc.setPadding(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, metrics));
            ((LinearLayout) convertView).addView(desc);
            holder = new ViewHolder();
            holder.tvDesc = desc;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CouInFo bean = list.get(position);
        holder.tvDesc.setText("购满" + bean.getMincost() + "元，再加" + bean.getPrice() + "元，可在购物车换购" + bean.getSku_name());

        return convertView;
    }

    class ViewHolder {
        TextView tvDesc;
    }
}
