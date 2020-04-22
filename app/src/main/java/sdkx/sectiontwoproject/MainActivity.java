package sdkx.sectiontwoproject;


import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;
import sdkx.sectiontwoproject.bean.PerIn;
import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.model.NoHttpRx;
import sdkx.sectiontwoproject.util.SingleClick;

import static sdkx.sectiontwoproject.http.HttpUrl.ANDROIDID;
import static sdkx.sectiontwoproject.util.UtilLog.showLogE;
import static sdkx.sectiontwoproject.util.UtilLog.showToast;

public class MainActivity extends BaseActivity {

    @BindView(R.id.exit_linear)
    LinearLayout exitLinear;
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.pre_tv)
    TextView preTv;
    private NoHttpRx noHttpRx;
    private Map map;

    @Override
    public int intiLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        MyApplication.getInstance().addActivity(this);

      /*  RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(needPermissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            showLogE("startActivity", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            showLogE("startActivity", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            showLogE("startActivity", permission.name + " is denied.");
                        }
                    }
                });*/
        exitLinear.getBackground().setAlpha(51);
        preTv.getBackground().setAlpha(120);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth() / 2, MyApplication.getInstance().getHeight() / 3);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);
        showLogE("SERIALNUMBER", ANDROIDID);
    }

    @SingleClick
    @OnClick({R.id.exit_linear, R.id.pre_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit_linear:
                //退出程序
                showDialog(this, true, getString(R.string.sure_exit));
                break;
            case R.id.pre_tv:
                //跳转个人信息界面
                noHttpRx = new NoHttpRx(this);
                map = new HashMap();
                map.put("androidId", HttpUrl.ANDROIDID);
                noHttpRx.postHttpJson("考生信息", HttpUrl.PREPAREEXAM_URL, JSON.toJSONString(map), null);

                break;
        }
    }

    @Override
    public void toActivityData(String flag, String object) {
        super.toActivityData(flag, object);
        showLogE("请求数据：" + flag, object);
        Gson gson = new Gson();
        if (gson.fromJson(object, PerIn.class).getData() == null) {
            showToast("请求数据为空");
            return;
        }
        Intent intent = new Intent(this, PerInfActivity.class);
        intent.putExtra("perin", object);
        startActivity(intent);
        MyApplication.getInstance().exit();

    }

}
