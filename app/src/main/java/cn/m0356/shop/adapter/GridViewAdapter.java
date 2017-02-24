package cn.m0356.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.Home5Data;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.ui.home.SubjectWebActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;

/**
 * Created by Mr on 2016/8/31.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Home5Data> home5Datas;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public GridViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return home5Datas == null ? 0 : home5Datas.size();
    }

    @Override
    public Object getItem(int position) {
        return home5Datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public ArrayList<Home5Data> getHome5Datas() {
        return home5Datas;
    }
    public void setHome5Datas(ArrayList<Home5Data> home5Datas) {
        this.home5Datas = home5Datas;
    }
    private class ViewHolder{
        ImageView image;
        TextView tvNav;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.tab_home_item_home5_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.index_home_iv_navsort);
            holder.tvNav = (TextView) convertView.findViewById(R.id.index_home_iv_navtv);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
        }
        Home5Data bean = home5Datas.get(position);
        imageLoader.displayImage(bean.getImage(), holder.image, options, animateFirstListener);
        holder.tvNav.setText(bean.getImgTitle());
        OnImageViewClick(convertView, bean.getType(), bean.getData());
        return convertView;
    }
    public void OnImageViewClick(View view,final String type,final String data){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("keyword")){//搜索关键字
                    try {
                        checkKeyword(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else if(type.equals("special")){//专题编号
                    Intent intent = new Intent(context,SubjectWebActivity.class);
                    intent.putExtra("data", Constants.URL_SPECIAL+"&special_id="+data+"&type=html");
                    context.startActivity(intent);
                }else if(type.equals("goods")){//商品编号
                    Intent intent =new Intent(context,GoodsDetailsActivity.class);
                    intent.putExtra("goods_id", data);
                    context.startActivity(intent);
                }else if(type.equals("url")){//地址
                    Intent intent = new Intent(context,SubjectWebActivity.class);
                    intent.putExtra("data", data);
                    context.startActivity(intent);
                } else if(type.equals("store")){ // 店铺
                    //Toast.makeText(getActivity(), "store", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, newStoreInFoActivity.class);
                    intent.putExtra("store_id", data);
                    context.startActivity(intent);
                } else if(type.equals("category")){ // 分类
                    Intent intent = new Intent(context, GoodsListFragmentManager.class);
                    intent.putExtra("gc_id", data);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void checkKeyword(String data) throws Exception {
        Pattern pattern = Pattern.compile("\\[*.\\]"); // 正则 判断data中是否包含[ ]
        Matcher matcher = pattern.matcher(data);
        if(matcher.find()){ // 非关键字 跳转操作
            executeJump(data);
        } else {
            Intent intent = new Intent(context,GoodsListFragmentManager.class);
            intent.putExtra("keyword", data);
            intent.putExtra("gc_name", data);
            context.startActivity(intent);
        }
    }

    /**
     * 解析数据 执行跳转
     * 数据格式 [com.xx.xx,paramName,paramVale,......]
     * @param str
     * @throws Exception
     */
    private void executeJump(String str) throws Exception {

        Pattern pattern = Pattern.compile("(?<=\\[)(\\S+)(?=\\])");
        Matcher matcher = pattern.matcher(str);
        String strNew = "";
        if(matcher.find()){
            strNew = matcher.group();
            String[] strAry = strNew.split(",");
            String className = strAry[0];
            Intent intent = new Intent();
            intent.setClass(context, Class.forName(className));
            for(int i = 1; i < strAry.length; i+=2){
                intent.putExtra(strAry[i], strAry[i+1]);
            }
            context.startActivity(intent);
        } else {
            throw new Exception("数据格式解析错误");
        }


    }
}
