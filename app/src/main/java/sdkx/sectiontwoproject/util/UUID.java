package sdkx.sectiontwoproject.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.provider.Settings;
import java.lang.reflect.Method;

import sdkx.sectiontwoproject.app.MyApplication;

import static sdkx.sectiontwoproject.util.UtilLog.showLogE;

public class UUID {
    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    @SuppressLint({"NewApi", "MissingPermission"})
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showLogE("读取设备序列号异常：" , e.toString());
        }
        assert serial != null;
        if (serial.equals("")) {
            //如果序列号为空则用androidid
            serial = Settings.System.getString(MyApplication.applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return serial;
    }
}
