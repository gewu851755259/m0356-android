package cn.m0356.shop.ui.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;

/**
 * 在线帮助界面
 * 
 * @author KingKong·HE
 * @Time 2014年2月14日 下午4:10:35
 * @E-mail hjgang@bizpoer.com
 */
public class HelpActivity extends BaseActivity {
	private ProgressBar progressHelp;
	private WebView webviewHelp;
	private String loadUrl = "";
	private MyShopApplication myShopApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadUrl = getIntent().getStringExtra("loadUrl");
		myShopApplication = (MyShopApplication) getApplicationContext();
		setContentView(R.layout.more_help);
		MyExceptionHandler.getInstance().setContext(this);
		progressHelp = (ProgressBar) findViewById(R.id.helpweb_progress_bar);
		webviewHelp = (WebView) findViewById(R.id.webviewHelp);
		WebSettings settings = webviewHelp.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setDisplayZoomControls(false);
		settings.setJavaScriptEnabled(true);
		if (Constants.URL_HELP.equals(loadUrl)) {
			setCommonHeader("在线帮助");
			webviewHelp.loadUrl(loadUrl);
		} else if (Constants.URL_MEMBER_EXP_HELP.equals(loadUrl)) {
			setCommonHeader("经验等级");
			webviewHelp.loadUrl(loadUrl + myShopApplication.getLoginKey());
		} else if (Constants.URL_MEMBER_POINT_HELP.equals(loadUrl)) {
			setCommonHeader("积分获取");
			webviewHelp.loadUrl(loadUrl);
		}
		webviewHelp.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressHelp.setVisibility(View.GONE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressHelp.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadUrl("file:///android_asset/error.html");
			}
		});
		webviewHelp.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressHelp.setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
			}
		});
	}
}
