package sdkx.sectiontwoproject.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.multidex.MultiDex;

import com.liqi.nohttputils.RxNoHttpUtils;
import com.liqi.nohttputils.nohttp.NoHttpInit;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

public class MyApplication extends Application {
    //全局Context
    public static Context applicationContext;
    public static MyApplication instance;
    private List<Activity> mList = new ArrayList<>();
    private int width;
    private int height;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        //屏幕适配
        configUnits();
        //屏幕信息
        screen();
        //网络请求初始化
        noHttp();

        MultiDex.install(this);

    }

    private void noHttp() {
        try {
            // InputStream inputStream =getApplicationContext().getAssets().open("srca.cer");
            //初始化nohttp（在此处其实可以调用setDialogGetListener设置全局请求加载框）
            RxNoHttpUtils.rxNoHttpInit(getApplicationContext())
                    //是否维护Cookie
                    .setCookieEnable(false)
                    //是否缓存进数据库DBCacheStore
                    .setDbEnable(true)
                    //是否开启debug调试
                    .isDebug(true)
                    //设置debug打印Name
                    .setDebugName("NoHttpUtils")
                    //设置全局连接超时时间。单位秒，默认30s。
                    //.setConnectTimeout(40)
                    //设置全局服务器响应超时时间，单位秒，默认30s。
                    //.setReadTimeout(40)
                    //设置全局默认加载对话框
                    //.setDialogGetListener("全局加载框获取接口")
                    //设置底层用那种方式去请求
                    .setRxRequestUtilsWhy(NoHttpInit.OKHTTP)
                    //设置下载线程池并发数量
                    .setThreadPoolSize(3)
                    //设置网络请求队列并发数量
                    .setRunRequestSize(4)
                    //设置带证书安全协议请求
                    //.setInputStreamSSL(new InputStream())
                    //设置无证书安全协议请求
                    //.setInputStreamSSL()
                    //添加全局请求头
                    //.addHeader("app>>head","app_head_global")
                    //添加全局请求参数-只支持String类型
                    // .addParam("app_param","app_param_global")
                    //设置Cookie管理监听。
                    // .setCookieStoreListener(new DBCookieStore.CookieStoreListener())
                    //设置主机验证
                    // .setHostnameVerifier(new HostnameVerifier())
                    //设置全局重试次数，配置后每个请求失败都会重试设置的次数。
                    //.setRetry(5)
                    .setAnUnknownErrorHint("全局未知错误提示语")
                    //开始初始化Nohttp
                    .startInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

    //将启动的进程添加进入list中
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (mList != null) {
            mList.remove(activity);
        }
    }

    //将list中的activity全部销毁
    public void exit() {
        for (Activity activity : mList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void exit(Activity act) {
        for (Activity activity : mList) {
            if (activity != act) {
                activity.finish();
            }
        }
    }

    private void configUnits() {
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)
//                .setSupportSP(false)//sp是对字体的支持 如果打开则需要使用mm来定义字体的大小
                .setSupportSubunits(Subunits.MM);
        //打开对fragment修改尺寸的支持
//        AutoSizeConfig.getInstance().setCustomFragment(true);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void screen() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度（像素）
        width = dm.widthPixels;
        // 屏幕高度（像素）
        height = dm.heightPixels;
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        // 屏幕密度dpi（120 / 160 / 240）
        int densityDpi = dm.densityDpi;
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int wscreenWidth = (int) (width / density);  // 屏幕宽度(dp)
        // 屏幕高度(dp)
        int screenHeight = (int) (height / density);

        Log.e("h_bl", "屏幕宽度（像素）：" + width);
        Log.e("h_bl", "屏幕高度（像素）：" + height);
        Log.e("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.e("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.e("h_bl", "屏幕宽度（dp）：" + wscreenWidth);
        Log.e("h_bl", "屏幕高度（dp）：" + screenHeight);
        Log.e("屏幕尺寸", (Math.sqrt(1920 * 1920 + 1080 * 1080) / densityDpi) + "");
    }
}
