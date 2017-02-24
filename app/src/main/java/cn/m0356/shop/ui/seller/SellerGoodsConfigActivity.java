package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.m0356.shop.R;

/**
 * Created by jiangtao on 2017/1/3.
 */
public class SellerGoodsConfigActivity extends Activity implements View.OnClickListener {

    private TextView tvSpecName;
    private EditText etPrice, etMarketPrice, etStorage, etAlarm;
    private Button btnSave;
    private String specId;
    private String barcode;
    private String sku;
    // 全局 库存
    private String stock;
    private String spec;

    public static void start(Activity context, String strage, String spec, String json){
        Intent intent = new Intent(context, SellerGoodsConfigActivity.class);
        intent.putExtra("spec", spec);
        intent.putExtra("json", json);
        intent.putExtra("strage", strage);
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_goods_config);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        spec = intent.getStringExtra("spec");
        String json = intent.getStringExtra("json");
        String strage = intent.getStringExtra("strage");
        tvSpecName.setText(spec);
        parseData(json, strage);

    }

    private void parseData(String json, String strage) {
        try {
            JSONObject jo = new JSONObject(json);
            // 市场价
            String marketprice = jo.getString("marketprice");
            // 价格
            String price = jo.getString("price");
            // 库存
            stock = jo.getString("stock");
            // 预警
            String alarm = jo.getString("alarm");
            // spec id
            this.specId = jo.getString("spec_ids");
            this.barcode = jo.getString("barcode");
            this.sku = jo.getString("sku");
            etAlarm.setText(alarm);
            etMarketPrice.setText(marketprice);
            etPrice.setText(price);
            etStorage.setText(stock);

            if(!TextUtils.isEmpty(strage)){
                int strageInt = Integer.parseInt(strage);
                if(strageInt > 0)
                    etStorage.setText(strageInt + "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        tvSpecName = (TextView) findViewById(R.id.tv_seller_spec_name);
        etPrice = (EditText) findViewById(R.id.et_seller_edit_price);
        etMarketPrice = (EditText) findViewById(R.id.et_seller_edit_market_price);
        etStorage = (EditText) findViewById(R.id.et_seller_edit_storage);
        etAlarm = (EditText) findViewById(R.id.et_seller_edit_alarm);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnSave.setActivated(true);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSave){
            Intent intent = new Intent();
            intent.putExtra("spec_ids", specId);
            intent.putExtra("alarm", etAlarm.getText().toString());
            intent.putExtra("barcode", barcode);
            intent.putExtra("marketprice", etMarketPrice.getText().toString());
            intent.putExtra("price", etPrice.getText().toString());
            intent.putExtra("sku", sku);
            String stock = etStorage.getText().toString();
            intent.putExtra("stock", stock);
            intent.putExtra("color_id", specId);
            intent.putExtra("spec", spec);

            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
