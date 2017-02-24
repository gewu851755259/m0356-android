package cn.m0356.shop.ui.home;

import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.custom.RoundProgressBar;
import cn.m0356.shop.ui.store.StoreGoodsListFragmentManager;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;

/**
 * 专题页面
 * @author KingKong-HE
 * @Time 2015-1-26
 * @Email KingKong@QQ.COM
 */
public class SubjectWebActivity extends Activity implements OnClickListener{
	private RoundProgressBar  roundProgressBar;
	private WebView webviewID;
	private String data;
	private boolean forNewStore;
	private String storeId;

	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_view);
		MyExceptionHandler.getInstance().setContext(this);
		data = getIntent().getStringExtra("data");
		forNewStore = getIntent().getBooleanExtra("forNewStore", false);
		storeId = getIntent().getStringExtra("storeId");
		
		webviewID = (WebView) findViewById(R.id.webviewID);
		roundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBarID);
		ImageView imageBack = (ImageView) findViewById(R.id.imageBack);
		
		WebSettings settings = webviewID.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);//根据屏幕缩放
		settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);//根据屏幕缩放
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setAppCacheEnabled(false);
		webviewID.setWebChromeClient(new MyWebChromeClient()); 
		
		webviewID.loadUrl(data);
		
		webviewID.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadUrl("file:///android_asset/error.html");
			}
		});
		
		webviewID.addJavascriptInterface(new Object(){ 
            //这里我定义了一个拨打的方法   
			@JavascriptInterface
            public void mb_special_item_click(String type,String data){ 
            	if(type.equals("keyword")){//搜索关键字
					if(forNewStore){
						Intent intent =new Intent(SubjectWebActivity.this,StoreGoodsListFragmentManager.class);
						intent.putExtra("keyword", data);
						intent.putExtra("store_id", storeId);
						SubjectWebActivity.this.startActivity(intent);
					} else {
						Intent intent = new Intent(SubjectWebActivity.this,GoodsListFragmentManager.class);
						intent.putExtra("keyword", data);
						intent.putExtra("gc_name", data);
						SubjectWebActivity.this.startActivity(intent);
					}

				}else if(type.equals("special")){//专题编号
					if(forNewStore){
						webviewID.loadUrl(Constants.URL_STORE_SPECIAL + "&special_id=" + data + "&type=html&store_id=" + storeId);
					} else {
						webviewID.loadUrl(Constants.URL_SPECIAL+"&special_id="+data+"&type=html");
					}
				}else if(type.equals("goods")){//商品编号
					Intent intent =new Intent(SubjectWebActivity.this,GoodsDetailsActivity.class);
					intent.putExtra("goods_id", data);
					SubjectWebActivity.this.startActivity(intent);
				}else if(type.equals("url")){//地址
					webviewID.loadUrl(data);
				} else if(type.equals("store")){ // 店铺
					Intent intent = new Intent(SubjectWebActivity.this, newStoreInFoActivity.class);
					intent.putExtra("store_id", data);
					startActivity(intent);
				} else if(type.equals("category")){ // 分类
					if(forNewStore) {
						Intent intent = new Intent(SubjectWebActivity.this, StoreGoodsListFragmentManager.class);
						intent.putExtra("stc_id", data);
						intent.putExtra("store_id", storeId);
						SubjectWebActivity.this.startActivity(intent);
					} else {
						Intent intent = new Intent(SubjectWebActivity.this, GoodsListFragmentManager.class);
						intent.putExtra("gc_id", data);
						startActivity(intent);
					}
					/*Intent intent = new Intent(getActivity(), GoodsListFragmentManager.class);
					intent.putExtra("gc_id", data);
					startActivity(intent);*/
				}
            } 
        }, "android"); 
		
		imageBack.setOnClickListener(this);
		
	}
	
	private class MyWebChromeClient extends WebChromeClient {  
	    @Override  
	    public void onProgressChanged(WebView view, int newProgress) {  
	    	roundProgressBar.setProgress(newProgress);  
	        if(newProgress==100){  
	        	roundProgressBar.setVisibility(View.GONE);  
	        }  
	        super.onProgressChanged(view, newProgress);  
	    }  
	      
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageBack:
			
			finish();
			
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onPageStart(this, "专题界面");
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPageEnd(this, "专题界面");
	}
}
