package cn.m0356.shop.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/12/8.
 */
public class SellerDelGoodsDialog extends DialogFragment implements View.OnClickListener {

    private TextView tv_notice;
    private Button btnOk, btnCancel;
    private String id;
    private String key;
    private String goodsName;
    private OnSuccessListener mListener;

    public static SellerDelGoodsDialog newInstance(String key) {
        SellerDelGoodsDialog f = new SellerDelGoodsDialog();
        Bundle args = new Bundle();
        args.putString("key", key);
        f.setArguments(args);
        return f;
    }

    public void setListener(OnSuccessListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_seller_del_goods, container);
        tv_notice = (TextView) view.findViewById(R.id.tv_notice);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        initData();
        return view;
    }

    private void initData() {
        Bundle arguments = getArguments();
        this.key = arguments.getString("key");
        tv_notice.setText("确定要删除商品：" + goodsName);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_ok){
            delGoods();
        } else if(v.getId() == R.id.btn_cancel){
            this.dismiss();
        }
    }

    private void delGoods() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("commonids", id);

        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_DROP, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    if(data.getJson().equals("1")){
                        Toast.makeText(MyShopApplication.context, "删除成功", Toast.LENGTH_SHORT).show();
                        if(mListener != null)
                            mListener.success();
                    }
                } else {
                    ShopHelper.showApiError(getActivity(), data.getJson());
                }
                dismiss();
            }
        });
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

    public void show(FragmentManager fragmentManager, String goodsName, String id) {
        this.show(fragmentManager, goodsName);
        this.id = id;
        this.goodsName = goodsName;
    }

    public interface OnSuccessListener{
        void success();
    }

}
