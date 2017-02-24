package cn.m0356.shop.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.m0356.shop.R;

/**
 * Created by jiangtao on 2016/12/7.
 */
public class ReasonDialog extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup rg_reason;
    private EditText et_reason;
    private RadioButton rb;
    private Button btn;
    private String reason = "";
    private OnOrderCancelListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_reason_name, container);
        rg_reason = (RadioGroup) view.findViewById(R.id.rg_reason);
        et_reason = (EditText) view.findViewById(R.id.et_reason);
        rb = (RadioButton) view.findViewById(R.id.rb_other);
        btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(this);
        rg_reason.setOnCheckedChangeListener(this);
        return view;
    }

    public void setListener(OnOrderCancelListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(rb.isChecked()){
            reason = et_reason.getText().toString();
        }
        if(mListener != null)
            mListener.cancel(reason);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_other){
            et_reason.setVisibility(View.VISIBLE);
        } else {
            et_reason.setVisibility(View.GONE);
        }

        if(checkedId == R.id.rb_1){
            reason = getString(R.string.order_cancel_1);
        } else if(checkedId == R.id.rb_2){
            reason = getString(R.string.order_cancel_2);
        } else if(checkedId == R.id.rb_3){
            reason = getString(R.string.order_cancel_3);
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
    public interface OnOrderCancelListener{
        void cancel(String reason);
    }
}
