package cn.m0356.shop.common;

import android.app.Application;
import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import cn.m0356.shop.bean.SellerLoginInfo;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/11/29.
 */
public class SellerHelper {
    public static void login(String username, String password, RemoteDataHandler.Callback callback) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("seller_name", username);
        params.put("password", getMD5(password));
        params.put("client", "android");
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_LOGIN, params, callback);
    }

    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 保存登录信息
     * @param info
     */
    public static void saveSellerInfo(SellerLoginInfo info){
        MyShopApplication application = (MyShopApplication) MyShopApplication.context;
        application.setSeller_key(info.key);
        application.setSeller_name(info.seller_name);
        application.setStore_name(info.store_name);
    }

}
