package cn.m0356.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import cn.m0356.shop.MainFragmentManager;
import cn.m0356.shop.R;
import cn.m0356.shop.bean.NavigationList;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.ui.home.SearchActivity;
import cn.m0356.shop.ui.mine.RegisterMobileActivity;
import cn.m0356.shop.ui.mine.SigninActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.VoucherActivity;

/**
 * 首页Navigation适配器
 *
 * @author yml
 */
public class HomeNavMyGridViewListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NavigationList> navDatas;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private MyShopApplication myApplication;

    public HomeNavMyGridViewListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.myApplication = (MyShopApplication) context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return navDatas == null ? 0 : navDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return navDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<NavigationList> getHomeNavList() {
        return navDatas;
    }

    public void setHomeNavList(ArrayList<NavigationList> navDatas) {
        this.navDatas = navDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.tab_home_item_nav_gridview_item, null);
            holder = new ViewHolder();
            holder.navAllBgImg = (ImageView) convertView.findViewById(R.id.home_navigation_allimg);
            holder.navBgLayout = (RelativeLayout) convertView.findViewById(R.id.home_navigation_layout);
            holder.navBgImg = (ImageView) convertView.findViewById(R.id.home_navigation_img);
            holder.navBgText = (TextView) convertView.findViewById(R.id.home_navigation_text);
            holder.navTitle = (TextView) convertView.findViewById(R.id.home_navigation_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationList bean = navDatas.get(position);
        holder.navTitle.setText(bean.getImage_title());
        imageLoader.displayImage(bean.getImage(), holder.navAllBgImg, options, animateFirstListener);
        OnImageViewClick(holder.navAllBgImg, bean.getImage_title(), bean.getType(), bean.getData());

        return convertView;
    }

    class ViewHolder {
        ImageView navBgImg, navAllBgImg;
        RelativeLayout navBgLayout;
        TextView navTitle, navBgText;
    }

    public void OnImageViewClick(View view, final String title, final String type, final String data) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("store")) {
                    goToStore(data);
                    return;
                }
                if (title.equals("分类")) {
                    Intent intent = new Intent(context, MainFragmentManager.class);
                    myApplication.sendBroadcast(new Intent(Constants.SHOW_Classify_URL));
                    context.startActivity(intent);
                } else if (title.equals("领券")) {
                    if (ShopHelper.isLogin(context, myApplication.getLoginKey()))
                        VoucherActivity.start(context);
                } else if (title.equals("我的晋城购")) {
                    Intent intent = new Intent(context, MainFragmentManager.class);
                    myApplication.sendBroadcast(new Intent(Constants.SHOW_Mine_URL));
                    context.startActivity(intent);
                } else if (title.equals("注册")) {
                    if (ShopHelper.isLogin(context, myApplication.getLoginKey())) {
                        context.startActivity(new Intent(context, RegisterMobileActivity.class));
                    }
                } else if (title.equals("签到")) {
                    if (ShopHelper.isLogin(context, myApplication.getLoginKey())) {
                        context.startActivity(new Intent(context, SigninActivity.class));
                    }
                } else {
                    context.startActivity(new Intent(context, SearchActivity.class));
                }
            }
        });
    }

    /**
     * 进入指定店铺
     *
     * @param store_id 店铺id
     */
    public void goToStore(String store_id) {
        Intent intent = new Intent(context, newStoreInFoActivity.class);
        intent.putExtra("store_id", store_id);
        context.startActivity(intent);
    }
}
