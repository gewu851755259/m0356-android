package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Stroke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ScreenUtil;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.custom.GoodsSpecDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.type.FlowLayout;

/**
 * Created by jiangtao on 2016/12/30.
 */
public class SellerGoodsDetailActivity extends Activity implements View.OnClickListener {

    private String commonid;
    private MyShopApplication app;

    private Button btnGoodsEdit;
    private Button btnSave;
    private EditText etName, etJingle, etPrice, etMarketPrice,
        etCostPrice, etAlarm, etFreight, etStorage;
    private RadioButton rb1, rb0;
    private LinearLayout ll_spec_container;

    // spec id，spec name  规格集合 包含具体规格name，不同规格名下的具体规格信息
    private List<Map<String, String>> specMap;
    // spec id， specs  包含每款商品的具体信息
    private List<Map<String, String>> mSpec;

    // 编辑商品sp_name参数集合 加载规格信息时初始化
    // 一级规格id 规格名
    private List<String> sp_name;
    // 一级规格id -> 对应所有规格
    private Map<String, Map<String, String>> sp_value;

    // 属性map 属性id为key
    private Map<String, Map<String, String>> attrMap;

    // 全局提交修改请求参数
    private HashMap<String, String> mParamMap;
    // 选中规格 key为id val为name
    private HashMap<String, String> checkedSpec;
    // 维护总库存
    private HashMap<String, String> strageMap;


    // 已选择的属性
    private JSONArray attr_checked;

    // 提交修改所需参数
    private String goods_commonid;
    private String gc_id;
    private String gc_name;
    private String type_id;
    private String g_serial;
    private String goods_barcode;
    private String g_body;
    private String m_body;
    private String province_id;
    private String city_id;
    private String transport_title;
    private String transport_id;
    private String goods_freight;
    private String goods_vat;
    private String goods_stcids;
    private String goods_image;
    private String goods_state;


    public static void start(Context context, String goods_commonid){
        Intent intent = new Intent(context, SellerGoodsDetailActivity.class);
        intent.putExtra("goods_commonid", goods_commonid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_goods_detail);
        this.commonid = getIntent().getStringExtra("goods_commonid");
        app = (MyShopApplication) getApplication();

        ll_spec_container = (LinearLayout) findViewById(R.id.ll_spec_container);
        btnGoodsEdit = (Button) findViewById(R.id.btnGoodsEdit);
        btnSave = (Button) findViewById(R.id.btnSave);

        etName = (EditText) findViewById(R.id.et_seller_edit_name);
        etJingle = (EditText) findViewById(R.id.et_seller_edit_jingle);
        etPrice = (EditText) findViewById(R.id.et_seller_edit_price);
        etMarketPrice = (EditText) findViewById(R.id.et_seller_edit_market_price);
        etCostPrice = (EditText) findViewById(R.id.et_seller_edit_cost_price);
        etAlarm = (EditText) findViewById(R.id.et_seller_edit_alarm);
        etFreight = (EditText) findViewById(R.id.et_seller_edit_freight);
        etStorage = (EditText) findViewById(R.id.et_seller_edit_storage);

        rb1 = (RadioButton) findViewById(R.id.rb_seller_edit_1);
        rb0 = (RadioButton) findViewById(R.id.rb_seller_edit_0);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGoodsEdit.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSave.setActivated(true);

        specMap = new ArrayList<Map<String, String>>();
        mSpec = new ArrayList<Map<String, String>>();
        sp_name = new ArrayList<String>();
        sp_value = new HashMap<String, Map<String, String>>();
        attrMap = new HashMap<String, Map<String, String>>();
        mParamMap = new HashMap<String, String>();
        checkedSpec = new HashMap<String, String>();
        strageMap = new HashMap<String, String>();

        loadGoodDetailInfo();
    }


    /**
     * 加载详情
     */
    private void loadGoodDetailInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("goods_commonid", commonid);
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        String json = data.getJson();
                        JSONObject jo = new JSONObject(json);
                        // 保存一些参数信息
                        saveAvailData(json);
                        // 提取商品信息 填充控件
                        initView(jo.getJSONObject("goodscommon_info"));
                        String gc_id = jo.getJSONObject("goodscommon_info").getString("gc_id");
                        JSONArray ja = jo.getJSONArray("spec_checked");
                        JSONArray sp_value = jo.getJSONArray("sp_value");
                        // 选中的属性id和属性值id数组
                        attr_checked = jo.getJSONArray("attr_checked");
                        loadGoodTypeInfo(gc_id, ja, sp_value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });
    }

    private void initView(JSONObject goodscommon_info) throws JSONException {
        etName.setText(goodscommon_info.getString("goods_name"));
        etJingle.setText(goodscommon_info.getString("goods_jingle"));
        etPrice.setText(goodscommon_info.getString("goods_price"));
        etMarketPrice.setText(goodscommon_info.getString("goods_marketprice"));
        etCostPrice.setText(goodscommon_info.getString("goods_costprice"));
        etAlarm.setText(goodscommon_info.getString("goods_storage_alarm"));
        etStorage.setText(goodscommon_info.getString("g_storage"));
        // 运费
        if(goodscommon_info.getString("transport_id").equals("0")){
            etFreight.setText(goodscommon_info.getString("goods_freight"));
        } else {
            etFreight.setText("已使用运费模版");
            etFreight.setKeyListener(null);
        }
        // 是否推荐
        if(goodscommon_info.getString("goods_commend").equals("0")){
            rb0.setChecked(true);
        } else {
            rb1.setChecked(true);
        }
    }

    /**
     * 提前可用信息，保存商品参数需要
     * @param json
     */
    private void saveAvailData(String json) throws JSONException {
        JSONObject jo = new JSONObject(json);
        JSONObject goodscommon_info = jo.getJSONObject("goodscommon_info");
        // spu
        goods_commonid = goodscommon_info.getString("goods_commonid");
        // 分类id
        gc_id = goodscommon_info.getString("gc_id");
        // gc_name 分类名称
        gc_name = goodscommon_info.getString("gc_name");
        // TODO: 2017/1/3 商品名称g_name 、商品卖点g_jingle  获取用户输入
        // TODO: 2017/1/3 提交规格spec 、规格名称sp_name 、规格值sp_value
        // type_id 类型编号
        type_id = goodscommon_info.getString("type_id");
        // 商家货号
        g_serial = goodscommon_info.getString("goods_serial");
        // 商品条形码
        goods_barcode = goodscommon_info.getString("goods_barcode");
        // 商品描述
        g_body = goodscommon_info.getString("goods_body");
        // 手机描述
        m_body = goodscommon_info.getString("mobile_body");
        // 所在地一级
        province_id = goodscommon_info.getString("areaid_1");
        // 所在地二级
        city_id = goodscommon_info.getString("areaid_2");
        // 运费id 可根据id为0判断是否显示使用运费模版
        transport_id = goodscommon_info.getString("transport_id");
        // 运费模版标题
        transport_title = goodscommon_info.getString("transport_title");
        // 运费
        goods_freight = goodscommon_info.getString("goods_freight");
        // 是否开局增值税发票
        goods_vat = goodscommon_info.getString("goods_vat");
        // 分类id
        goods_stcids = goodscommon_info.getString("goods_stcids");
        // 商品主图文件名称
        goods_image = goodscommon_info.getString("goods_image");
        // 商品状态
        goods_state = goodscommon_info.getString("goods_state");



    }

    /**
     * 根据分类id 加载规格属性
     * @param gc_id
     * @param ary 商品规格
     * @param sp_value 商品规格具体信息
     */
    private void loadGoodTypeInfo(String gc_id, final JSONArray ary, final JSONArray sp_value){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("gc_id", gc_id);
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_TYPE_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        ArrayList<String> idList = new ArrayList<String>();
                        for(int i = 0; i< ary.length(); i++){
                            // 将所有选中规格id放入集合 方便后期判断
                            String id = ary.getJSONObject(i).getString("id");
                            idList.add(id);
                            checkedSpec.put(id, ary.getJSONObject(i).getString("name"));
                        }
                        JSONObject jo = new JSONObject(data.getJson());
                        // 获取所有规格集合
                        JSONArray spec_list = jo.getJSONArray("spec_list");
                        for(int i = 0; i < spec_list.length(); i++){
                            JSONObject space = spec_list.getJSONObject(i);
                            parseSpace(space, idList);
                        }

                        formatSpValue(sp_value);
                        // 属性集合
                        JSONArray attrAry = jo.getJSONArray("attr_list");
                        for(int i = 0; i < attrAry.length(); i++){
                            JSONObject attr = attrAry.getJSONObject(i);
                            String attr_id = attr.getString("attr_id");
                            String attr_name = attr.getString("attr_name");
                            JSONArray value = attr.getJSONArray("value");
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("attr_id", attr_id);
                            map.put("attr_name", attr_name);
                            map.put("value", value.toString());
                            // id为key 方便后期使用
                            attrMap.put(attr_id, map);
                            /*Gson gson = new Gson();
                            GoodsAttrBean goodsAttrBean = gson.fromJson(attrAry.getJSONObject(i).toString(), GoodsAttrBean.class);
                            attrList.add(goodsAttrBean);*/
                        }
                        // 初始化请求接口中attr参数
                        HashMap<String, String> attr = initAttrParams();
                        initSpecParams();
                        // 添加到全局map
                        mParamMap.putAll(attr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });

    }

    private void initSpecParams() throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();


    }

    /**
     * 初始化attr参数
     */
    private HashMap<String, String> initAttrParams() throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        for(int i = 0; i < attr_checked.length(); i++){
            JSONObject attr = attr_checked.getJSONObject(i);
            // 属性编号
            String attr_id = attr.getString("attr_id");
            // 属性所选值编号
            String attr_value_id = attr.getString("attr_value_id");

            //  attr[206][name]=材质 206为属性编号，“材质”为属性名称
            //  attr[206][2050]=棉 206为属性编号，2050为属性所选值得编号，“棉”为属性所选值的文字
            //attr暂时不做修改，后期添加
            map.put("attr[" + attr_id + "][name]", attrMap.get(attr_id).get("attr_name"));

            JSONArray ary = new JSONArray(attrMap.get(attr_id).get("value"));
            String v = "";
            for(int x = 0; x < ary.length(); x++){
                JSONObject jsonObject = ary.getJSONObject(x);
                if(jsonObject.getString("attr_value_id").equals(attr_value_id)){
                    v = jsonObject.getString("attr_value_name");
                }
            }
            map.put("attr[" + attr_id + "][" + attr_value_id + "]", v);
        }

        /////////////////默认///////////////////////
        /**
         * spec[i_1][alarm] 预警值
         spec[i_1][barcode] 条码
         spec[i_1][marketprice] 市场价
         spec[i_1][price] 商品价格
         spec[i_1][sku] 商家货号
         spec[i_1][stock] 库存
         spec[i_1][color_id] 颜色编号，如果规格中有颜
         spec[i_1][goods_id] 商品id（SKU）
         spec[i_1][sp_value][247]=黑色 具体规格
         spec[i_1][sp_value][434]=XL 具体规格
         */



        return map;
    }

    private void formatSpValue(JSONArray sp_value) throws JSONException {
        for(int i = 0; i< sp_value.length(); i++){
            JSONObject jo = sp_value.getJSONObject(i);
            String id = jo.getString("id");
            String[] spec_idses = jo.getString("spec_ids").split(",");
            String spec = "";
            String ids = "";
            for(int x = 0; x < spec_idses.length; x++){
                String specName = specMap.get(x).get(spec_idses[x]);
                spec += specName;
                if(x != spec_idses.length - 1)
                    spec +=",";
                ids += spec_idses[x];
            }

            for(int x = 0; x < spec_idses.length; x++){
                mParamMap.put("spec[i_" + ids + "][sp_value][" + spec_idses[x] + "]", checkedSpec.get(spec_idses[x]));
            }

            ///params
            mParamMap.put("spec[i_" + ids + "][alarm]", jo.getString("alarm"));
            mParamMap.put("spec[i_" + ids + "][barcode]", jo.getString("barcode"));
            mParamMap.put("spec[i_" + ids + "][marketprice]", jo.getString("marketprice"));
            mParamMap.put("spec[i_" + ids + "][price]", jo.getString("price"));
            mParamMap.put("spec[i_" + ids + "][sku]", jo.getString("sku"));
            mParamMap.put("spec[i_" + ids + "][stock]", jo.getString("stock"));
//            mParamMap.put("spec[i_" + ids + "][color_id]", jo.getString("alarm"));


            Set<Map.Entry<String, String>> entries = mParamMap.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while(iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
            }


            Map<String, String> map = new HashMap<String, String>();
            map.put("id", id);
            map.put("spec", spec);
            map.put("json", jo.toString());
            map.put("stock", jo.getString("stock"));
            mSpec.add(map);

            strageMap.put(spec, jo.getString("stock"));

        }
    }

    private void parseSpace(JSONObject space, List<String> idList) throws JSONException {
        View view = View.inflate(SellerGoodsDetailActivity.this, R.layout.view_spec_layout, null);
        TextView tvKey = (TextView) view.findViewById(R.id.tv_key);
        FlowLayout flVal = (FlowLayout) view.findViewById(R.id.fl_spec);
        String sp_name = space.getString("sp_name");
        // 一级规格编号
        String id = space.getString("sp_id");
        // 提交修改时需要此参数 基数为id 偶数为name
        this.sp_name.add(id);
        this.sp_name.add(sp_name);

        tvKey.setText(sp_name);
        JSONArray value = space.getJSONArray("value");
        Map<String, String> map = new HashMap<String, String>();
        for(int i = 0; i < value.length(); i++){
            CheckBox cb = new CheckBox(SellerGoodsDetailActivity.this);
            cb.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            cb.setBackgroundResource(R.drawable.selector_spce);
            cb.setClickable(false);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(5));
            JSONObject ob = value.getJSONObject(i);
            // 二级规格编号
            String mid = ob.getString("sp_value_id");
            String name = ob.getString("sp_value_name");
            cb.setText(name);
            cb.setTag(mid);
            // 将id name放入map 编辑商品需要
            map.put(mid, name);
            if(idList.contains(mid)){
                cb.setChecked(true);
            }
            flVal.addView(cb, lp);
        }
        ll_spec_container.addView(view);
        specMap.add(map);
        sp_value.put(id, map);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnGoodsEdit){
            if(mSpec.size() < 1){
                Toast.makeText(SellerGoodsDetailActivity.this, "无规格~", Toast.LENGTH_SHORT).show();
                return;
            }
            GoodsSpecDialog dialog = new GoodsSpecDialog();
            dialog.show(getFragmentManager(), mSpec, strageMap);

        } else if(v.getId() == R.id.btnSave){
            saveConfig();
        }
    }

    private void saveConfig() {
        Map<String, String> params = getParams();
        mParamMap.putAll(params);
        String g_commend;
        if(rb1.isChecked()){
            g_commend = "1";
        } else {
            g_commend = "0";
        }
        mParamMap.put("g_commend ", g_commend);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_EDIT, mParamMap, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    Toast.makeText(SellerGoodsDetailActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ShopHelper.showApiError(SellerGoodsDetailActivity.this, data.getJson());
                }
            }
        });
    }

    private Map<String, String> getParams(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());

        // 初始化sp_name参数
        for(int i = 0; i < sp_name.size(); i += 2){
            map.put("sp_name[" + sp_name.get(i) + "]", sp_name.get(i + 1));
        }

        // 构建sp_value参数
        // 一级规格编号
        Set<String> spValueKey = sp_value.keySet();
        for (String key : spValueKey) {
            // 二级规格编号 二级规格名
            Map<String, String> sp_value = this.sp_value.get(key);

            Iterator<Map.Entry<String, String>> iterator = sp_value.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> entry = iterator.next();
                map.put("sp_value[" + key + "][" +
                        entry.getKey() + "]", entry.getValue());
            }
        }

        // 商品 spu
       map.put("commonid", commonid);
        // 商品分类id
         map.put("cate_id", gc_id);
        map.put("cate_name", gc_name);
        map.put("g_name", etName.getText().toString());
        map.put("g_jingle", etJingle.getText().toString());
        map.put("type_id", type_id);
        map.put("g_price", etPrice.getText().toString());
        map.put("g_marketprice", etMarketPrice.getText().toString());
        map.put("g_costprice", etCostPrice.getText().toString());
//        map.put("g_discount", );
        map.put("g_storage", etStorage.getText().toString());
        map.put("g_alarm", etAlarm.getText().toString());
        map.put("g_serial", g_serial);
        map.put("g_barcode", goods_barcode);
        map.put("g_body", g_body);
        map.put("m_body", m_body);
        map.put("province_id", province_id);
        map.put("city_id", city_id);
        map.put("g_freight", goods_freight);
        if(transport_id.equals("0")){
            map.put("freight", "0");
        } else {
            map.put("freight", "1");
        }
        map.put("transport_id", transport_id);
        map.put("transport_title", transport_title);
        map.put("g_vat", goods_vat);
        map.put("sgcate_id", goods_stcids);
//        map.put("g_commend", );
        // 返回完整url 接口接收文件名即可，简单处理
        map.put("image_path", goods_image);
        map.put("g_state", goods_state);
        return map;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            // 相同key会自动覆盖 不作处理
            String spec_ids = data.getStringExtra("spec_ids");
            String alarm = data.getStringExtra("alarm");
            String barcode = data.getStringExtra("barcode");
            String marketprice = data.getStringExtra("marketprice");
            String price = data.getStringExtra("price");
            String sku = data.getStringExtra("sku");
            // 新库存
            String stock = data.getStringExtra("stock");
            //String color_id = data.getStringExtra("color_id");

            // 格式化specid
            String id = "";
            String[] split = spec_ids.split(",");
            for(int i = 0; i < split.length; i++){
                id += split[i];
            }
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("spec[i_" + id + "][alarm]", alarm);
            map.put("spec[i_" + id + "][barcode]", barcode);
            map.put("spec[i_" + id + "][marketprice]", marketprice);
            map.put("spec[i_" + id + "][price]", price);
            map.put("spec[i_" + id + "][sku]", sku);
            map.put("spec[i_" + id + "][stock]", stock);
            for(int i = 0; i < split.length; i++){
                map.put("spec[i_" + id + "][sp_value][" + split[i]+ "]", specMap.get(i).get(split[i]));
            }

            mParamMap.putAll(map);


            String spec = data.getStringExtra("spec");
            strageMap.put(spec, stock);

            int total = 0;
            // 修改库存
            Set<String> strages = strageMap.keySet();
            for (String s :
                    strages) {
                String strageStr = strageMap.get(s);
                if(!TextUtils.isEmpty(strageStr)){
                    total += Integer.parseInt(strageStr);
                }
            }
            etStorage.setText(total + "");

        }
    }
}
