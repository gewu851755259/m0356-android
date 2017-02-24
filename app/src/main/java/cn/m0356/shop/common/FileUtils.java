package cn.m0356.shop.common;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {
    private static final String TAG = FileUtils.class.getName();

    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/formats/";

    public static String FRIENDSPHOTO = Environment.getExternalStorageDirectory()
            + "/images/m0356/";


    public static void saveBitmap(Bitmap bm, String picName) {
        Log.e("", "保存图片");
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    public static boolean copyFile(File from, File to) {
        if (!from.isFile() || !to.isFile())
            return false;

        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    /* Ignore */
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
					/* Ignore */
                }
            }
        }
        return true;
    }

    public static boolean writeFile(byte[] data, File file) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 写入内容
     * @param string 内容
     * @param file 指定文件
     * @param isAppend 是否追加
     * @param hasEnter 是否换行
     * @return
     */
    public static boolean writeFile(String string, File file, boolean isAppend ,boolean hasEnter) {
        FileWriter fileWriter;
        BufferedWriter bw;
        try {
            fileWriter = new FileWriter(file,isAppend);
            bw = new BufferedWriter(fileWriter);
            bw.write(string);
            if(hasEnter)
                bw.newLine();
            fileWriter.flush();
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                Log.e("msg", column_index + "");
                if (cursor.moveToFirst()) {
                    Log.e("msg", cursor.getString(column_index));
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e("msg", "进来2");
            return uri.getPath();
        }

        Log.e("msg", "没进来");
        return null;
    }


    public static String getPathfile(Context context, Uri uri) {

        if ("file".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                Log.e("msg", column_index + "");
                if (cursor.moveToFirst()) {
                    Log.e("msg", cursor.getString(column_index));
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e("msg", "进来2");
            return uri.getPath();
        }

        Log.e("msg", "没进来");
        return null;
    }

    public static File getAdvCacheFile() throws IOException {
        File cacheDir = MyShopApplication.context.getCacheDir();
        File myCacheDir = new File(cacheDir, Constants.FILE_DIR_ADV);
        if (!myCacheDir.exists())
            myCacheDir.createNewFile();
        return myCacheDir;
    }

    public static Map<String, String> readCache(File file){
        BufferedReader reader ;
        Map<String, String> map;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            // 读取第一行时间
            String time = reader.readLine();
            map = new HashMap<String, String>();
            map.put("time", time);

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            map.put("data", sb.toString());
            return map;
        } catch (IOException e){

        }
        return null;
    }

    public static void saveToLocal(String item) throws IOException {
        // 20161104 简单添加到本地缓存 提升登录页展示速度
        File file = FileUtils.getAdvCacheFile();
        // 第一行写入保存时间 第三个参数为false 保证每次刷新都将缓存文件更新
        FileUtils.writeFile(System.currentTimeMillis() + "", file, false, true);
        boolean isSucc = FileUtils.writeFile(item, file, true, false);
    }

}
