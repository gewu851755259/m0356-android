package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.ExchangePhotoAdapter;
import cn.m0356.shop.bean.GoodsListBean;
import cn.m0356.shop.bean.ImageFileBean;
import cn.m0356.shop.bean.ReasonBean;
import cn.m0356.shop.bean.TuiOrderBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.FileUtils;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.T;
import cn.m0356.shop.custom.NCDialog;
import cn.m0356.shop.custom.PhotoBottomDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ncinterface.INCOnDialogConfirm;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderGoodsTuiGoodsActivity extends BaseActivity {
    private String orderId;
    private String orderGoodsId;
    private MyShopApplication myApplication;
    private LayoutInflater inflater;
    private EditText textNum;
    private TextView textOrderStoreName;
    private EditText editSeason,textSum;
    private LinearLayout lll;
    private LinearLayout addViewID;
    private Spinner spinner1;
    private String reason_id;
    private GridView gv_photo;
    private PhotoBottomDialog bottomDialog;
    private ExchangePhotoAdapter adapter;
    private List<ImageFileBean> images;
    private NCDialog ncDialog;
    public static final int COMMENT_SUCCESS = 122;
    public static final int SELELCT_FILE_TO_UPLOAD_ITEM = 108;
    private static final int FLAG_CHOOSE_PHONE = 6;
    private String payPrice;
    private String refund_type="2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_goods_tui_goods);
        setCommonHeader("商品退货");
        inflater = LayoutInflater.from(OrderGoodsTuiGoodsActivity.this);
        orderId=getIntent().getStringExtra("order_id");
        orderGoodsId=getIntent().getStringExtra("order_goods_id");
        myApplication = (MyShopApplication) getApplicationContext();
        MyExceptionHandler.getInstance().setContext(this);
        initViews();
        loadDatas();

    }

    public void initViews(){
        lll = (LinearLayout) findViewById(R.id.lll);
        addViewID = (LinearLayout) lll.findViewById(R.id.addViewID);
        textNum=(EditText)findViewById(R.id.textNum);
        textOrderStoreName=(TextView)findViewById(R.id.textOrderStoreName);
        editSeason=(EditText)findViewById(R.id.editSeason);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        gv_photo = (GridView)findViewById(R.id.gv_photo);
        adapter=new ExchangePhotoAdapter(OrderGoodsTuiGoodsActivity.this,images);
        gv_photo.setAdapter(adapter);
        textSum=(EditText)findViewById(R.id.textSum);
    }


    public void loadDatas(){
        String url= Constants.URL_ORDER_REFUND_SOME+ "&key=" + myApplication.getLoginKey() + "&order_id=" + orderId+"&order_goods_id="+orderGoodsId;
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        final ArrayList<ReasonBean> reasons= new ArrayList<ReasonBean>(com.alibaba.fastjson.JSONObject.parseArray(obj.getString("reason_list"), ReasonBean.class));
                        final ArrayList<String> list=new ArrayList();
                        for (ReasonBean r:reasons){
                            list.add(r.getReason_info());
                        }
                        ArrayAdapter adapterSp=new ArrayAdapter(OrderGoodsTuiGoodsActivity.this,android.R.layout.simple_spinner_item,list);
                        spinner1.setAdapter(adapterSp);
                        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String info=list.get(i);
                                for (ReasonBean r:reasons){
                                    if(r.getReason_info().equals(info)){
                                        reason_id=r.getReason_id();
                                    }else{
                                        continue;
                                    }
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        TuiOrderBean order = com.alibaba.fastjson.JSONObject.parseObject(obj.getString("order"), TuiOrderBean.class);

                        final GoodsListBean goods = com.alibaba.fastjson.JSONObject.parseObject(obj.getString("goods"), GoodsListBean.class);


                        textOrderStoreName.setText(order.getStore_name());

                        //显示店铺商品列表
                        View orderGoodsListView = inflater.inflate(
                                R.layout.listivew_order_goods_item, null);
                        addViewID.addView(orderGoodsListView);

                        ImageView imageGoodsPic = (ImageView) orderGoodsListView
                                .findViewById(R.id.imageGoodsPic);
                        TextView textGoodsName = (TextView) orderGoodsListView
                                .findViewById(R.id.textGoodsName);
                        TextView textGoodsPrice = (TextView) orderGoodsListView
                                .findViewById(R.id.textGoodsPrice);
                        TextView textGoodsNUM = (TextView) orderGoodsListView
                                .findViewById(R.id.textGoodsNUM);
                        TextView textGoodsSPec = (TextView) orderGoodsListView.findViewById(R.id.textGoodsSPec);
                        LinearLayout llGift = (LinearLayout) orderGoodsListView.findViewById(R.id.llGift);
                        llGift.setVisibility(View.GONE);
                        textGoodsName.setText(goods.getGoods_name());
                        textGoodsPrice.setText("￥" + goods.getGoods_price());
                        textGoodsNUM.setText("×" + goods.getGoods_num());
                        Glide.with(OrderGoodsTuiGoodsActivity.this).load(goods.getGoods_img_360()).into(imageGoodsPic);
                        if (goods.getGoods_spec() == null) {
                            textGoodsSPec.setVisibility(View.GONE);
                        } else {
                            textGoodsSPec.setText(goods.getGoods_spec());
                        }
                        textGoodsName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(OrderGoodsTuiGoodsActivity.this, GoodsDetailsActivity.class);
                                i.putExtra("goods_id", goods.getGoods_id());
                                OrderGoodsTuiGoodsActivity.this.startActivity(i);
                            }
                        });

                        payPrice=goods.getGoods_pay_price();
                        textNum.setText("￥" + payPrice);


                        gv_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                if(images==null||i==images.size()){
                                    bottomDialog = new PhotoBottomDialog(OrderGoodsTuiGoodsActivity.this);
                                    bottomDialog.show();
                                }else{
                                    ncDialog = new NCDialog(OrderGoodsTuiGoodsActivity.this);
                                    ncDialog.setText1("确认删除该图片?");
                                    ncDialog.setOnDialogConfirm(new INCOnDialogConfirm() {
                                        @Override
                                        public void onDialogConfirm() {
                                            images.remove(i);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    ncDialog.showPopupWindow();
                                }
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(OrderGoodsTuiGoodsActivity.this, json);
                }
            }
        });


    }


    /**
     * 上传图片
     * @param file
     */
    public void uploadImage(File file){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("key", myApplication.getLoginKey());
        HashMap<String, File> fileMap = new HashMap<String, File>();
        fileMap.put("refund_pic", file);

        RemoteDataHandler.asyncMultipartPostString(Constants.URL_ORDER_EXCHANGE_PHOTO,params,fileMap,new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    ImageFileBean image= com.alibaba.fastjson.JSONObject.parseObject(json,ImageFileBean.class);
                    if (images != null) {
                        images.add(image);
                    } else {
                        images = new ArrayList<ImageFileBean>();
                        images.add(image);
                    }
                    adapter.setData(images);
                    adapter.notifyDataSetChanged();
                }else{
                    T.showShort(OrderGoodsTuiGoodsActivity.this, "文件上传失败");
                }
            }
        });
    }


    //选择照片后的回调方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELELCT_FILE_TO_UPLOAD_ITEM:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    LogHelper.e("uri", uri.toString());

                    String path = FileUtils.getPath(this, uri);

                    File file = new File(path);

                    if (file.length() < 1024 * 1024) {
                        uploadImage(file);
                    } else {
                        T.showShort(OrderGoodsTuiGoodsActivity.this, "图片文件过大，请上传1M以下的图片");
                    }
                }
                break;
            case FLAG_CHOOSE_PHONE:
                if (resultCode == RESULT_OK) {


                    String imgpath = myApplication.getImgPath();
                    LogHelper.e("imgpath",imgpath);
                    File file = new File(imgpath);
                    if (file.length() < 1024 * 1024) {
                        uploadImage(file);
                    } else {
                        T.showShort(OrderGoodsTuiGoodsActivity.this, "图片文件过大，请上传1M以下的图片");
                    }

                }
        }
    }

    public void btnCommitExchange(View v){
        String url=Constants.URL_ORDER_EXCHANGE_SOME;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("order_id", orderId);
        params.put("order_goods_id",orderGoodsId);
        params.put("refund_amount",textNum.getText().toString());
        params.put("goods_num",textSum.getText().toString());
        params.put("reason_id",reason_id);
        params.put("buyer_message",editSeason.getText().toString());
        params.put("refund_type",refund_type);

        if(images!=null){
            for (int position=0;position<images.size();position++){
                ImageFileBean i=images.get(position);
                params.put("refund_pic["+position+"]",i.getPic());
            }
        }

        RemoteDataHandler.asyncLoginPostDataString(url,params,myApplication,new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (json.equals("1")) {
                    setResult(COMMENT_SUCCESS);
                    T.showShort(OrderGoodsTuiGoodsActivity.this, "退货成功");
                }else{
                    T.showShort(OrderGoodsTuiGoodsActivity.this, json);
                }
                finish();
            }
        });

    }


}
