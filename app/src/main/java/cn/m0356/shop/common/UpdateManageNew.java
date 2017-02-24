package cn.m0356.shop.common;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import cn.m0356.shop.bean.IMFriendsList;
import cn.m0356.shop.ui.mine.UpdateManager;

/**
 * Created by jiangtao on 2017/1/16.
 */
public class UpdateManageNew {
    /**
     *
     * @param context
     * @param newVersion
     * @param url
     * @param oldVersion
     */
    public UpdateManageNew(Context context, String newVersion, String url, String oldVersion){
        checkOldVersion(oldVersion);
        if(SystemHelper.getNetworkType(context) == 1){
            if(!checkFileExist(context, newVersion)){
                startService(context, url, newVersion);
            }
        } else{
            if(!checkFileExist(context, newVersion)){
                UpdateManager mUpdateManager = new UpdateManager(context, url);
                mUpdateManager.checkUpdateInfo();
            }
        }
    }

    private boolean checkFileExist(Context context, String newVersion){
        String vs = newVersion.replace(".", "");
        File file = new File(SystemHelper.obtainApkPath(vs));
        if(file.exists()) {
            SystemHelper.showNotify(context, vs);
            return true;
        } else {
            return false;
        }
    }

    private void checkOldVersion(String oldVersion) {
        // 旧版version
        String oldV = oldVersion.replace(".", "");
        // 旧版文件file
        File oldFile = new File(SystemHelper.obtainApkPath(oldV));
        // 如果旧版存在 执行删除
        if(oldFile.exists())
            oldFile.delete();
    }

    private void startService(Context context, String url, String version) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra("url", url);
        intent.putExtra("version", version);
        context.startService(intent);
    }
}
