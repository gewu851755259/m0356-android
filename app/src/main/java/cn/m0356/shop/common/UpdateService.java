package cn.m0356.shop.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Url;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.internal.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.m0356.shop.R;
import cn.m0356.shop.http.RemoteDataHandler;

/**
 * Created by jiangtao on 2017/1/16.
 */
public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void asyncDownload(String url, String version) {
        new MyAsyncTask().execute(url, version);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        String version = intent.getStringExtra("version");
        // 异步下载
        asyncDownload(url, version);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private class MyAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            // 取出特殊字符
            String vs = params[1].replace(".", "");

            File newFile = new File(SystemHelper.obtainApkPath(vs));
            if(newFile.exists())
                return vs;
            try {
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                InputStream is = conn.getInputStream();
                // 名字_ 添加判断 确保安装文件完整
                File file = new File(SystemHelper.obtainApkPath(vs) + "_");
                if(file.exists()){ // 存在下载失败
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(file, true);
                byte[] buff = new byte[10 * 1024];
                int length;
                while ((length = is.read(buff)) != -1){
                    fos.write(buff, 0, length);
                }
                fos.flush();
                fos.close();
                is.close();

                if(file.exists()){
                    // 下载完成，修改名称
                    file.renameTo(new File(SystemHelper.obtainApkPath(vs)));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return vs;
        }
        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);
//            Toast.makeText(MyShopApplication.context, "成功", Toast.LENGTH_SHORT).show();
            SystemHelper.showNotify(UpdateService.this, version);
            stopSelf();
        }
    }



}
