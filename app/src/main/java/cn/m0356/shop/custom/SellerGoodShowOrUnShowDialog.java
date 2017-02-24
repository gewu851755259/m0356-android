package cn.m0356.shop.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cn.m0356.shop.R;

/**
 * Created by jiangtao on 2016/12/8.
 */
public class SellerGoodShowOrUnShowDialog extends DialogFragment implements View.OnClickListener {

    private TextView tv_notice;
    private Button btnOk, btnCancel;
    private OnOpListener mListener;
    private String name;
    private String type;

    public static SellerGoodShowOrUnShowDialog newInstance(String name, String type) {
        SellerGoodShowOrUnShowDialog f = new SellerGoodShowOrUnShowDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    public void setListener(OnOpListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_seller_goods_op, container);
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
        name = arguments.getString("name");
        type = arguments.getString("type");
        tv_notice.setText("确定要" + type + "商品：" + name);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_ok){
            if(mListener != null)
                mListener.op();
        } else if(v.getId() == R.id.btn_cancel){
            this.dismiss();
        }
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

    public void show(FragmentManager fragmentManager, String name, String type) {
        this.name = name;
        this. type = type;
        this.show(fragmentManager, "op");
        tv_notice.setText("确定要" + type + "商品：" + name);
    }

    public interface OnOpListener {
        void op();
    }

}
