package sdkx.sectiontwoproject;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;

public class PerInfActivity extends BaseActivity {

    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_portrait_iv)
    ImageView headPortraitIv;
    @BindView(R.id.start_tv)
    TextView startTv;
    @BindView(R.id.sex_tv)
    TextView sexTv;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.model_tv)
    TextView modelTv;
    @BindView(R.id.id_num_tv)
    TextView idNumTv;
    @BindView(R.id.num_tv)
    TextView numTv;
    @BindView(R.id.room_tv)
    TextView roomTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.linear)
    RelativeLayout linear;

    @Override
    public int intiLayout() {
        return R.layout.activity_per_inf;
    }

    @Override
    public void initData() {

        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth()-250, MyApplication.getInstance().getHeight()-250);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);

        nameTv.getBackground().setAlpha(51);
        sexTv.getBackground().setAlpha(51);
        dateTv.getBackground().setAlpha(51);
        modelTv.getBackground().setAlpha(51);
        idNumTv.getBackground().setAlpha(51);
        numTv.getBackground().setAlpha(51);
        roomTv.getBackground().setAlpha(51);
        typeTv.getBackground().setAlpha(51);
        startTv.getBackground().setAlpha(120);
        nameTv.setText(Html.fromHtml("<b>考生姓名: </b>张丽丽"));
        sexTv.setText(Html.fromHtml("<b>性别: </b>女"));
        dateTv.setText(Html.fromHtml("<b>考试日期: </b>2020-10-10"));
        modelTv.setText(Html.fromHtml("<b>考试机型: </b>G1"));
        idNumTv.setText(Html.fromHtml("<b>身份证号: </b>1212121212121"));
        numTv.setText(Html.fromHtml("<b>考试次数: </b>第二次"));
        roomTv.setText(Html.fromHtml("<b>考试考场: </b>第二考场"));
        typeTv.setText(Html.fromHtml("<b>报考类别: </b>初次申领考试"));
    }


    @OnClick(R.id.start_tv)
    public void onViewClicked() {
        //跳转开始考试界面
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

}
