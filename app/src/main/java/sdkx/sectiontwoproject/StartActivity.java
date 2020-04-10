package sdkx.sectiontwoproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.OnClick;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;
import sdkx.sectiontwoproject.bean.Car;
import sdkx.sectiontwoproject.bean.Filed;
import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.model.NoHttpRx;
import sdkx.sectiontwoproject.myview.MyView;
import sdkx.sectiontwoproject.myview.MyViewCar;
import sdkx.sectiontwoproject.util.CrossBoundary;
import sdkx.sectiontwoproject.util.ShotScreenManager;
import sdkxsoft.com.CarTrajectory;
import sdkxsoft.com.pojo.SitePort;
import sdkxsoft.com.pojo.XyPojo;
import sdkxsoft.com.tools.LngLatToXYTools;
import sdkxsoft.com.tools.PathInOrderPro;
import sdkxsoft.com.tools.RotationAngleTools;

public class StartActivity extends BaseActivity<String> {


    @BindView(R.id.linear)
    LinearLayout linear;

    @BindView(R.id.myView)
    MyView myView;
    @BindView(R.id.myview_car)
    MyViewCar myviewCar;
    NoHttpRx noHttpRx;

    List<SitePort> mList;
    private LngLatToXYTools lngLatToXYTools;
    private Map map;

    public CrossBoundary crossBoundary;
    //路线
    private String locationStr = "", locationStr1 = "", locationStr2 = "";
    //熄火
    private String fireStr = "", fireStr1 = "", fireStr2 = "";
    //弹窗是否在展示
    private boolean isShow = false;

    private PathInOrderPro pathInOrderPro;
    private Filed filed;
    //角度
    private RotationAngleTools rotationAngleTools;

    //串口管理
    private SerialPortManager mSerialPortManager, mSerialPortManager2;

    //保存经纬度
    private List<SitePort> lngLatData;
    //不合格数组
    private int gradeArr[];
    private String info="";

    @Override
    public int intiLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void initData() {
        MyApplication.getInstance().addActivity(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth() - 250, MyApplication.getInstance().getHeight() - 250);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);
        //截屏
//        Bitmap bitmap = ShotScreenManager.getInstance().picShotScreen(this, getFilesDir().getAbsolutePath() + "pic.jpg", 70);
//        Log.e("bitmap", bitmap.toString());
        crossBoundary = new CrossBoundary();
        lngLatData = new ArrayList<>();
        gradeArr = new int[]{1, 1, 1, 1, 1};
//        location = new int[2];
//        myView.getLocationOnScreen(location);
//        myView.post(new Runnable() {
//            @Override
//            public void run() {
////                Log.e("myView宽高：", myView.getWidth() + "---" + myView.getHeight());
////                lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth()-20, myView.getHeight()-20);
//
//            }
//        });



        mList = new ArrayList<>();

        noHttpRx = new NoHttpRx(this);
        map = new HashMap();
        noHttpRx.postHttp("考场", HttpUrl.GETFIELD_URL, map, null);


    }

    @Override
    public void toActivityData(String flag, String object) {
        super.toActivityData(flag, object);
        Log.e("请求数据：" + flag, object);
        Gson gson = new Gson();
        if (flag.equals("考场")) {
            try {

                filed = gson.fromJson(object, Filed.class);
                if (filed.getData() == null)
                    return;

                for (int i = 0; i < filed.getData().size(); i++) {
                    mList.add(new SitePort(filed.getData().get(i).getLng(), filed.getData().get(i).getLat()));
                }
                Log.e("myView宽高：", myView.getWidth() + "---" + myView.getHeight());
                lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth() - 20, myView.getHeight() - 40);
                rotationAngleTools = CarTrajectory.getCarPoint(lngLatToXYTools, 90);
                //拿到path对象
                pathInOrderPro = CarTrajectory.getPathOrder(mList);
                //8点坐标
                List<XyPojo> xyPojos = lngLatToXYTools.siteAdjust(mList);

                Log.e("lngLatToXYTools缩放比例：", lngLatToXYTools.getxScaling() + "---" + lngLatToXYTools.getyScaling());
                //传入点的集合
                RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams((int) (double) xyPojos.get(0).getX() + 50, (int) (double) xyPojos.get(0).getY() + 50);
                l.addRule(RelativeLayout.CENTER_IN_PARENT);
                myView.setLayoutParams(l);
                myviewCar.setLayoutParams(l);
                myView.getPoints(xyPojos);
                map.clear();
                map.put("androidId", HttpUrl.ANDROIDID);
                noHttpRx.postHttp("车", HttpUrl.GETCAR_URL, map, null);
            } catch (JsonSyntaxException e) {
                Log.e("场地解析异常：", e.getMessage());
            }
        }
        if (flag.equals("车")) {
            try {
                Car car = gson.fromJson(object, Car.class);
                myviewCar.getCar(car, lngLatToXYTools);
                //录入车辆场地信息
                crossBoundary.setMessage(JSON.toJSONString(filed.getData()), JSON.toJSONString(car.getData().getCarPoints()), "");
                serialPort();
            } catch (JsonSyntaxException e) {
                Log.e("车解析异常：", e.getMessage());
            }

        }
        if (flag.equals("提交数据")) {
            if (!isShow) {
                showDialog(StartActivity.this, false, info);
                isShow = true;
            }
        }

    }

    public void serialPort() {
        lngLatData.clear();

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();

//        Device device = (Device) getIntent().getSerializableExtra(DEVICE);
//        Log.e("onCreate: device =", "onCreate: device = " + device);
        if (devices.size() > 0) {
            mSerialPortManager = new SerialPortManager();

            // 打开串口
//            for (int i = 0; i < devices.size(); i++) {
//                Log.e("onCreate: device =", "onCreate: device root= " + devices.get(i).getRoot()
//                        + "\nonCreate: device name= " + devices.get(i).getName()
//                        + "\nonCreate: device file= " + devices.get(i).getFile());
//            }
            //定位串口信息
            boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                    .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes) {
                            Log.e("定位串口", "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("定位串口", "onDataReceived [ String ]: " + new String(bytes));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    locationStr1 = new String(bytes);
                                    locationStr = locationStr2 + new String(bytes);
                                    if (locationStr.contains("$")) {
                                        Log.e("定位数据$：", locationStr);
                                        Log.e("数据", locationStr.indexOf("$") + "----" + locationStr.indexOf("$", locationStr.indexOf("$") + 1));
                                        if (locationStr.indexOf("$") < locationStr.indexOf("$", locationStr.indexOf("$") + 1)) {
                                            locationStr1 = locationStr.substring(locationStr.indexOf("$"), locationStr.indexOf("$", locationStr.indexOf("$") + 1));
                                            locationStr2 = locationStr.substring(locationStr.indexOf("$", locationStr.indexOf("$") + 1));
                                            Log.e("locationStr", locationStr1 + "\n" + locationStr2);
                                        } else {
                                            locationStr2 = locationStr;
                                            return;
                                        }

                                    } else {
                                        return;
                                    }
                                    Log.e("定位数据：", locationStr1);
                                    Log.e("定位数据是否正确：", crossBoundary.checkData(locationStr1) + "");
                                    if (crossBoundary.checkData(locationStr1)) {
                                        String[] arr = locationStr1.split(",");
//                                    if (arr.length <= 20)
//                                        return;

//                                        try {
                                        double lng = Double.parseDouble(arr[2]);
                                        double lat = Double.parseDouble(arr[3]);
                                        double angle = Double.parseDouble(arr[5]);

                                        //精度为3
//                                        double angle = Double.parseDouble(arr[10]);
//                                        double angle = Double.parseDouble(arr[11]);
                                        Log.e("定位截取的数据", "经度：" + lng + "纬度" + lat + "angle:" + angle);

                                        lngLatData.add(new SitePort(lng, lat, angle));

//                                        if (lngLatToXYTools != null && myviewCar != null) {
                                        myviewCar.getPoint(lngLatToXYTools.getLngLatToXyPro(lng, lat), rotationAngleTools.getCarAnglePro(angle));
//                                        }
//                                        if (crossBoundary != null && pathInOrderPro != null) {
                                        if (crossBoundary.isCross(locationStr1) && !isShow) {
                                            //true越出边界
                                            gradeArr[1] = 0;
                                            submit(getResources().getString(R.string.cross));
                                        }
                                        Boolean msgboo = pathInOrderPro.detectionPath(lng, lat);
                                        if (msgboo != null) {
                                            if (msgboo == false && !isShow) {
                                                //false未按规定路线行驶
                                                gradeArr[0] = 0;
                                                submit(getResources().getString(R.string.no_regulations));
                                            }
                                        }
//                                        }
//                                        } catch (NumberFormatException e) {
//                                            Log.e("定位串口数据NumberFormat：", e.getMessage());
//                                        } catch (NullPointerException e) {
//                                            Log.e("定位串口数据NullPointer：", e.getMessage());
//
//                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onDataSent(byte[] bytes) {
                            Log.e("串口", "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("串口", "onDataSent [ String ]: " + new String(bytes));
                            final byte[] finalBytes = bytes;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(String.format("发送\n%s", new String(finalBytes)));
                                }
                            });
                        }
                    })
                    .openSerialPort(new File("/dev/ttyS2"), 115200);
//                        .openSerialPort(devices.get(i).getFile(), 9600);

            Log.e("串口", "onCreate: openSerialPort = " + openSerialPort);


            //熄火串口信息
            mSerialPortManager2 = new SerialPortManager();
            // 打开串口
//            for (int i = 0; i < devices.size(); i++) {
//                Log.e("onCreate: device =", "onCreate: device root= " + devices.get(i).getRoot()
//                        + "\nonCreate: device name= " + devices.get(i).getName()
//                        + "\nonCreate: device file= " + devices.get(i).getFile());
//            }
            boolean openSerialPort2 = mSerialPortManager2.setOnOpenSerialPortListener(this)
                    .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes) {
                            Log.e("熄火串口", "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("熄火串口", "onDataReceived [ String ]: " + new BigInteger(1, bytes).toString(16));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    fireStr = fireStr2 + new BigInteger(1, bytes).toString(16);
                                    fireStr = fireStr.toUpperCase();
//                                    fireStr1 =   new BigInteger(1, bytes).toString(16);
//                                    fireStr1=fireStr1.toUpperCase();
                                    if (fireStr.contains("AA4412")) {
                                        Log.e("熄火数据AA4412：", fireStr);
                                        Log.e("数据", fireStr.indexOf("AA4412") + "----" + fireStr.indexOf("AA4412", fireStr.indexOf("AA4412") + 1));
                                        if (fireStr.indexOf("AA4412") < fireStr.indexOf("AA4412", fireStr.indexOf("AA4412") + 1)) {
                                            fireStr1 = fireStr.substring(fireStr.indexOf("AA4412"), fireStr.indexOf("AA4412", fireStr.indexOf("AA4412") + 1));
                                            fireStr2 = fireStr.substring(fireStr.indexOf("AA4412", fireStr.indexOf("AA4412") + 1));
                                            Log.e("fireStr", fireStr1 + "\n" + fireStr2);

                                        } else {
                                            fireStr2 = fireStr;
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                    Log.e("熄火数据：", fireStr1);
                                    Log.e("熄火数据是否正确：", crossBoundary.checkDataStr(fireStr1) + "");
                                    if (crossBoundary.checkDataStr(fireStr1)) {
                                        fireStr2 = "";
                                        Log.e("熄火截取的数据", "是否：" + fireStr1);
                                        if (fireStr1.contains("AA441204") && !isShow) {
                                            //熄火
                                            gradeArr[3] = 0;
                                            submit(getResources().getString(R.string.fire));
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onDataSent(byte[] bytes) {
                            Log.e("串口", "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("串口", "onDataSent [ String ]: " + new String(bytes));
                            final byte[] finalBytes = bytes;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(String.format("发送\n%s", new String(finalBytes)));
                                }
                            });
                        }
                    })
                    .openSerialPort(new File("/dev/ttyS4"), 115200);
//                        .openSerialPort(devices.get(i).getFile(), 9600);

            Log.e("串口", "onCreate: openSerialPort2 = " + openSerialPort2);
        }
//        }
    }

    @Override
    protected void onDestroy() {
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
        }
        if (null != mSerialPortManager2) {
            mSerialPortManager2.closeSerialPort();
            mSerialPortManager2 = null;
        }
        super.onDestroy();
    }

    /**
     * 串口打开成功
     *
     * @param device 串口
     */
    @Override
    public void onSuccess(File device) {
//        Toast.makeText(getApplicationContext(), String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
        Log.e("串口打开", String.format("串口 [%s] 打开成功", device.getPath()));
    }

    /**
     * 串口打开失败
     *
     * @param device 串口
     * @param status status
     */
    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                showDialog(this, false, device.getPath() + "没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                showDialog(this, false, device.getPath() + "串口打开失败");
                break;
        }
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
     * 提交数据
     */
    public void submit(String info) {
        this.info = info;
        noHttpRx = new NoHttpRx(this);
        map = new HashMap();
        map.put("sitePort", lngLatData);
        map.put("androidId", HttpUrl.ANDROIDID);
        map.put("gradeArr", gradeArr);
        map.put("info", info);
        noHttpRx.postHttpJson("提交数据", HttpUrl.SETDATA_URL, JSON.toJSONString(map), null);
    }

    @OnClick({R.id.exit})
    public void onViewClicked() {
        submit(getResources().getString(R.string.success));
    }
}
