package sdkx.sectiontwoproject;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;
import sdkx.sectiontwoproject.bean.PerIn;
import sdkx.sectiontwoproject.util.SingleClick;

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
    private String perInStr;
    private PerIn perIn;

    @Override
    public int intiLayout() {
        return R.layout.activity_per_inf;
    }

    @Override
    public void initData() {
        MyApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        perInStr = intent.getStringExtra("perin");
        Gson gson = new Gson();
        perIn = gson.fromJson(perInStr, PerIn.class);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth() - 250, MyApplication.getInstance().getHeight() - 250);
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

//        Date date = new Date();

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        formatter.format(date);

        headPortraitIv.setImageBitmap(stringToBitmap(perIn.getData().getPhoto()));

        nameTv.setText(Html.fromHtml("<b>考生姓名: </b>"+perIn.getData().getName()));
        sexTv.setText(Html.fromHtml("<b>性别: </b>"+perIn.getData().getSex()));
        dateTv.setText(Html.fromHtml("<b>考试日期: </b>"+perIn.getData().getTestDate()));
        modelTv.setText(Html.fromHtml("<b>考试机型: </b>"+perIn.getData().getCarType()));
        idNumTv.setText(Html.fromHtml("<b>身份证号: </b>"+perIn.getData().getIDCard()));
        numTv.setText(Html.fromHtml("<b>考试次数: </b>"+perIn.getData().getNum()));
        roomTv.setText(Html.fromHtml("<b>考试考场: </b>"+perIn.getData().getRoomName()));
        typeTv.setText(Html.fromHtml("<b>报考类别: </b>"+perIn.getData().getServiceType()));
    }

    @SingleClick
    @OnClick(R.id.start_tv)
    public void onViewClicked(View view) {
        //跳转开始考试界面
        Intent intent=new Intent(this,StartActivity.class);
        intent.putExtra("id",perIn.getData().getExamineeId());
        startActivity(intent);
        MyApplication.getInstance().exit();
    }

    /**
     * base64转图片
     * @param string  base64串
     * @return
     */
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
