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

}
