package sdkx.sectiontwoproject.util;

import android.util.Log;
import android.widget.Toast;

import sdkx.sectiontwoproject.app.MyApplication;

import static sdkx.sectiontwoproject.util.ShotScreenManager.saveFile;

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
     * @param flag    flag
     * @param content content
     */
    public static void showLogE(String flag, String content) {
        Log.e(flag, content);
        //文件存储
//        saveFile(content);
    }


}
