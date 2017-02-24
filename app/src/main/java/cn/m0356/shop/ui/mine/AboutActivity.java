package cn.m0356.shop.ui.mine;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * 关于我们页面
 *
 * @author KingKong-HE
 * @Time 2015-2-1
 * @Email KingKong@QQ.COM
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvVersion;
    private RelativeLayout rl_check_up;
    private ImageView ivBarCode;

    /**
     * remain
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_about);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("关于我们");
        tvVersion = (TextView) findViewById(R.id.txt_version);
        rl_check_up = (RelativeLayout) findViewById(R.id.rl_check_up);
        ivBarCode = (ImageView) findViewById(R.id.iv_bar_code);
        if(getVersion() != null)
            tvVersion.setText(getVersion());
        rl_check_up.setOnClickListener(this);
        loadBarCode();

    }

    private void loadBarCode() {
        //带缓存的商品图
        Glide.with(this)
                .load(Constants.BAR_CODE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher)  //设置占位图
                .error(R.drawable.ic_launcher)      //加载错误图
                .into(ivBarCode);
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "版本：" + version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.rl_check_up){
            versionUpdate();
        }
    }

    /**
     * 获取是否有最新版本
     */
    public void versionUpdate() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_VERSION_UPDATE, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("version");
                        String urlJSON = obj.getString("url");
                        String VersionName = SystemHelper.getAppVersionName(AboutActivity.this);
                        if (VersionName.equals(objJson) || VersionName.equals("")) {
								Toast.makeText(AboutActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();;
                        } else {
                            //这里来检测版本是否需要更新
                            UpdateManager mUpdateManager = new UpdateManager(AboutActivity.this, urlJSON);
                            mUpdateManager.checkUpdateInfo();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
						Toast.makeText(AboutActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}