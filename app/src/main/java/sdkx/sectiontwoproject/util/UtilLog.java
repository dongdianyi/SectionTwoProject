package sdkx.sectiontwoproject.util;

import android.util.Log;
import android.widget.Toast;

import sdkx.sectiontwoproject.app.MyApplication;

public class UtilLog {
     static Toast mToast;

    /**
     * Toast
     *
     * @param content content
     */
    public static void showToast(String content) {
        if (null == mToast) {
            mToast = Toast.makeText(MyApplication.applicationContext, null, Toast.LENGTH_SHORT);
        }
        mToast.setText(content);
        mToast.show();
    }

    /**
     *
     * @param flag
     * @param content
     */
    public static void showLogE(String flag,String content){
        Log.e(flag, content);
    }


    /**
     * 防止按钮重复点击
     */
    private static final int MIN_DELAY_TIME = 2000;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
