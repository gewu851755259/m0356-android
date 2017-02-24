package cn.m0356.shop.ui.mine;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class OrderWuLiuActivity extends BaseActivity {
    private MyShopApplication myApplication;
    private TextView textWuLiuCompany;
    private TextView textWuLiuId;
    private TextView textWuLiuInfo;
    private String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_wu_liu);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("物流信息");
        myApplication = (MyShopApplication) getApplicationContext();
        orderId=getIntent().getStringExtra("order_id");
        initViews();
        loadWuLiuInfo();
    }

    public void initViews(){
        textWuLiuCompany=(TextView)findViewById(R.id.textWuLiuCompany);
        textWuLiuId=(TextView)findViewById(R.id.textWuLiuId);
        textWuLiuInfo=(TextView)findViewById(R.id.textWuLiuInfo);
    }

    public void loadWuLiuInfo(){
        String url= Constants.URL_ORDER_WULIU_INFO;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("order_id", orderId);
        Log.i("QIN",orderId);
        RemoteDataHandler.asyncLoginPostDataString(url,params,myApplication,new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json=data.getJson();
                Log.i("QIN",json);
                if(data.getCode()== HttpStatus.SC_OK){
                    try{
                        JSONObject obj=new JSONObject(json);
                        textWuLiuCompany.setText(obj.getString("express_name"));
                        textWuLiuId.setText(obj.getString("shipping_code"));
                        textWuLiuInfo.setText(obj.getString("deliver_info"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }else{
//                    ShopHelper.showApiError(OrderWuLiuActivity.this, json);
                }
            }
        });
    }


}
