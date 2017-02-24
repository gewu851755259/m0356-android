package cn.m0356.shop;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mobstat.StatService;

import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;

/**
 * 软件启动界面
 * @author KingKong-HE
 * @Time 2014-12-30
 * @Email KingKong@QQ.COM
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);

		MyExceptionHandler.getInstance().setContext(this);

		// 百度移动统计

		// 打开异常收集开关，默认收集java层异常，如果有嵌入SDK提供的so库，则可以收集native crash异常
		// manifest中设置
		//StatService.setOn(WelcomeActivity.this, StatService.EXCEPTION_LOG);

		// 打开基础统计
		StatService.start(this);

		// 打开调试开关，正式版本请关闭，以免影响性能
		StatService.setDebugOn(false);

		MyShopApplication app = (MyShopApplication) getApplication();
		if(TextUtils.isEmpty(app.getAreaId())){
			app.setAreaId("1337");
		}
        
        //加入定时器 睡眠 2000毫秒 自动跳转页面
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent it=new Intent();
				it.setClass(WelcomeActivity.this,MainFragmentManager.class);
				startActivity(it);
				WelcomeActivity.this.finish();
			}
		}, 1000);
    }

	/*@Override
	protected void onResume() {
		super.onResume();
		StatService.onPageStart(this, "欢迎界面");
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPageEnd(this, "欢迎界面");
	}*/

}
