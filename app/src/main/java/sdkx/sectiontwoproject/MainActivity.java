package sdkx.sectiontwoproject;


import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @BindView(R.id.exit_linear)
    LinearLayout exitLinear;
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.pre_tv)
    TextView preTv;

    @Override
    public int intiLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        exitLinear.getBackground().setAlpha(51);
        preTv.getBackground().setAlpha(120);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth()/2, MyApplication.getInstance().getHeight()/3);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);
    }

    @OnClick({R.id.exit_linear, R.id.pre_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit_linear:
                //退出程序
                showDialog(this,true,"确定退出系统吗？");
                break;
            case R.id.pre_tv:
                //跳转个人信息界面
                startActivity(new Intent(this, PerInfActivity.class));
                finish();
                break;
        }
    }
}
