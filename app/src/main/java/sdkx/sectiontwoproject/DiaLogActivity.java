package sdkx.sectiontwoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;

public class DiaLogActivity extends Activity {
    @BindView(R.id.dialog_relative)
    RelativeLayout dialogRelative;
    @BindView(R.id.content_tv)
    TextView contentTv;
    @BindView(R.id.close_iv)
    ImageView closeIv;
    private boolean isShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideTopUIMenu();
        hideBottomUIMenu();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dia_log);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);
        Intent intent=getIntent();
        isShow = getIntent().getBooleanExtra("isShow",false);
        contentTv.setText(getIntent().getStringExtra("context"));
        if (isShow) {
            closeIv.setVisibility(View.VISIBLE);
        } else {
            closeIv.setVisibility(View.GONE);
        }
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = MyApplication.getInstance().getWidth() / 2;
        lp.height = MyApplication.getInstance().getHeight() / 3;
        win.setAttributes(lp);


    }

    @OnClick({R.id.pre_tv, R.id.close_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pre_tv:
                if (isShow) {
                    MyApplication.getInstance().exit();
                    System.exit(0);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    MyApplication.getInstance().exit();
                }
                break;
            case R.id.close_iv:
               finish();
                break;
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
}
