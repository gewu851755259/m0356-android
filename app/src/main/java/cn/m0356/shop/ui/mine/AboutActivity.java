package cn.m0356.shop.ui.mine;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.SPUtils;
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
    private ToggleButton autoDownloadToggle;

    /**
     * remain
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_about);
        MyExceptionHandler.getInstance().setContext(this);
        initTitle();
        tvVersion = (TextView) findViewById(R.id.txt_version);
        rl_check_up = (RelativeLayout) findViewById(R.id.rl_check_up);
        ivBarCode = (ImageView) findViewById(R.id.iv_bar_code);
        autoDownloadToggle = (ToggleButton) findViewById(R.id.auto_download_toggle);
        boolean isAutoDownload = (Boolean) SPUtils.get(getApplicationContext(), SPUtils.ATTR_AUTO_DOWNLOAD, Boolean.TRUE);
        autoDownloadToggle.setChecked(isAutoDownload);
        if(getVersion() != null)
            tvVersion.setText(getVersion());
        rl_check_up.setOnClickListener(this);
        autoDownloadToggle.setOnClickListener(this);
        loadBarCode();
    }

    private void initTitle() {
        setCommonHeader("关于我们");
        showBtnRight(); // 显示标题栏右侧按钮
        setBtnRightImgResource(R.drawable.share_about);
        setBtnRightClickListener(this);
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
            return "当前版本号：V" + version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_check_up:
                versionUpdate();
                break;

            case R.id.auto_download_toggle:
                SPUtils.put(getApplicationContext(), SPUtils.ATTR_AUTO_DOWNLOAD, autoDownloadToggle.isChecked());
                break;

            case R.id.btnRight:
                /**开启默认分享面板，分享列表**/
                new ShareAction(AboutActivity.this)
                        .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();
                break;

            default:
                break;
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

    //设置多平台分享监听
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(AboutActivity.this, Constants.BAR_CODE);
            if (share_media != SHARE_MEDIA.SMS) {
                new ShareAction(AboutActivity.this).setPlatform(share_media).setCallback(umShareListener)
                        .withText("晋城购，同城网购直通车。\n" + SPUtils.get(getApplicationContext(), SPUtils.ATTR_DOWNLOAD_URL, ""))
                        .withTitle(getString(R.string.more_aboutus_appname))
                        .withTargetUrl((String) SPUtils.get(getApplicationContext(), SPUtils.ATTR_DOWNLOAD_URL, ""))
                        .withMedia(image)
                        .share();
            } else {
                new ShareAction(AboutActivity.this).setPlatform(share_media).setCallback(umShareListener)
                        .withText("晋城购，同城网购直通车。\n" + SPUtils.get(getApplicationContext(), SPUtils.ATTR_DOWNLOAD_URL, ""))
                        .withTitle(getString(R.string.more_aboutus_appname))
                        .withTargetUrl((String) SPUtils.get(getApplicationContext(), SPUtils.ATTR_DOWNLOAD_URL, ""))
                        .withMedia(image)
                        .share();

            }
        }
    };

    //设置单个监听是否分享成功
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(AboutActivity.this, "QQ分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(AboutActivity.this, "新浪微博分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(AboutActivity.this, "QQ空间分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(AboutActivity.this, "微信分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(AboutActivity.this, "微信朋友圈分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(AboutActivity.this, "短信分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {

            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(AboutActivity.this, "QQ分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(AboutActivity.this, "新浪微博分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(AboutActivity.this, "QQ空间分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(AboutActivity.this, "微信分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(AboutActivity.this, "微信朋友圈分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(AboutActivity.this, "短信分享失败啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(AboutActivity.this, "QQ分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(AboutActivity.this, "新浪微博分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(AboutActivity.this, "QQ空间分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(AboutActivity.this, "微信分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(AboutActivity.this, "微信朋友圈分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(AboutActivity.this, "短信分享取消了", Toast.LENGTH_SHORT).show();
            }
        }
    };

}