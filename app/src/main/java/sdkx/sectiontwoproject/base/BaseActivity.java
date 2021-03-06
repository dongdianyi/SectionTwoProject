package sdkx.sectiontwoproject.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jaeger.library.StatusBarUtil;
import com.kongqw.serialportlibrary.SerialPort;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;

import java.io.File;

import butterknife.ButterKnife;
import sdkx.sectiontwoproject.DiaLogActivity;
import sdkx.sectiontwoproject.view.IView;

import static sdkx.sectiontwoproject.util.UtilLog.showLogE;
import static sdkx.sectiontwoproject.util.UtilLog.showToast;


public abstract class BaseActivity<T> extends AppCompatActivity implements IView, OnOpenSerialPortListener {
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideTopUIMenu();
        hideBottomUIMenu();
        super.onCreate(savedInstanceState);



//        //横屏设置
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        //竖屏设置
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        //默认设置
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

//设置布局
        if (intiLayout() != 0) {
            setContentView(intiLayout());
            ButterKnife.bind(this);
//            MyApplication.getInstance().addActivity(this);
        }
        //设置数据
        initData();
        //监听器
        setListener();
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideBottomUIMenu() {
        //全屏 | View.SYSTEM_UI_FLAG_FULLSCREEN
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //可调起虚拟按键进行返回
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            decorView.setSystemUiVisibility(uiOptions);

            //调起虚拟按键时会立刻隐藏
            Window _window = getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
    }

    /**
     * 沉浸式导航栏
     */
    public void hideTopUIMenu() {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        //透明沉浸
        StatusBarUtil.setTranslucent(this);
    }


    /**
     * 设置布局
     *
     * @return
     */
    public abstract int intiLayout();

    /**
     * 设置数据
     */
    public abstract void initData();

    /**
     * 设置监听
     */
    public void setListener() {
    }

//    /**
//     * @param activity 当前Activity
//     * @param isShow   是否显示关闭图标
//     * @param context  中间显示的文本内容
//     */
//    public  void showDialog(Activity activity, boolean isShow, String context) {
//        View rootView = View.inflate(this, R.layout.popwindow_view, null);
//        TextView preTv = rootView.findViewById(R.id.pre_tv);
//        TextView contentTv = rootView.findViewById(R.id.content_tv);
//        ImageView closeIv = rootView.findViewById(R.id.close_iv);
//        contentTv.setText(context);
//        if (isShow) {
//            closeIv.setVisibility(View.VISIBLE);
//        } else {
//            closeIv.setVisibility(View.GONE);
//        }
////        PopupWindow dialog=new PopupWindow(rootView);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(rootView);
//        final AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawableResource(R.mipmap.popwindow_bg);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
//        preTv.setFocusable(true);
//        preTv.getBackground().setAlpha(120);
//        preTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShow) {
//                    dialog.dismiss();
//                    finish();
//                    System.exit(0);
//                } else {
//                    dialog.dismiss();
//                    startActivity(new Intent(activity, MainActivity.class));
//                    MyApplication.getInstance().exit();
//                }
//            }
//        });
//        closeIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        //隐藏虚拟键
//        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//
//                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        //布局位于状态栏下方
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        //隐藏导航栏
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE;
//                if (Build.VERSION.SDK_INT >= 19) {
//                    uiOptions |= 0x00001000;
//                } else {
//                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
//                }
//                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
//            }
//        });
//
//
//        WindowManager.LayoutParams params =
//                dialog.getWindow().getAttributes();
//        params.dimAmount = 0.9f;
//        params.width = MyApplication.getInstance().getWidth() / 2;
//        params.height = MyApplication.getInstance().getHeight() / 3;
//        dialog.getWindow().setAttributes(params);
//    }
    /**
     * @param activity 当前Activity
     * @param isShow   是否显示关闭图标
     * @param context  中间显示的文本内容
     */
    public  void showDialog(Activity activity, boolean isShow, String context) {
        Intent intent=new Intent(activity,DiaLogActivity.class);
        intent.putExtra("isShow",isShow);
        intent.putExtra("context",context);
        startActivity(intent);

    }

    @Override
    public void toActivityData(String flag, String object) {
    }

    @Override
    public void fail(String flag, Throwable t) {
        showLogE("请求数据失败："+flag,t.getMessage());
    }


    /**
     * 显示提示框
     *
     * @param title   title
     * @param message message
     */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 发送数据
     *
     * @param
     */
    public void onSend(String sendContent,SerialPortManager serialPortManager) {
        if (TextUtils.isEmpty(sendContent)) {
            showLogE("串口：","onSend: 发送内容为 null");
            return;
        }

        byte[] sendContentBytes = sendContent.getBytes();

        boolean sendBytes = serialPortManager.sendBytes(sendContentBytes);
        showLogE("串口：","onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }


    @Override
    public void onSuccess(File file) {

    }

    @Override
    public void onFail(File file, Status status) {

    }
}


