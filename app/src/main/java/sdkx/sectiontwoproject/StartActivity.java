package sdkx.sectiontwoproject;


import android.util.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import sdkx.sectiontwoproject.app.MyApplication;
import sdkx.sectiontwoproject.base.BaseActivity;
import sdkx.sectiontwoproject.bean.Car;
import sdkx.sectiontwoproject.bean.Filed;
import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.model.NoHttpRx;
import sdkx.sectiontwoproject.myview.MyView;
import sdkx.sectiontwoproject.myview.MyViewCar;
import sdkx.sectiontwoproject.util.CrossBoundary;
import sdkxsoft.com.CarTrajectory;
import sdkxsoft.com.pojo.SitePort;
import sdkxsoft.com.pojo.XyPojo;
import sdkxsoft.com.tools.LngLatToXYTools;
import sdkxsoft.com.tools.PathInOrderPro;

public class StartActivity extends BaseActivity<String> {


    @BindView(R.id.linear)
    LinearLayout linear;

    @BindView(R.id.myView)
    MyView myView;
    @BindView(R.id.myview_car)
    MyViewCar myviewCar;
    private int[] location;
    NoHttpRx noHttpRx;

    List<SitePort> mList;
    private LngLatToXYTools lngLatToXYTools;
    private Map map;

    public CrossBoundary crossBoundary;
    private String locationStr = "", locationStr1 = "";
    private String fireStr = "", fireStr1 = "";
    StringBuffer stringBuffer;
    private boolean isShow = false;

    private PathInOrderPro pathInOrderPro;
    private Filed filed;

    @Override
    public int intiLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void initData() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth() - 250, MyApplication.getInstance().getHeight() - 250);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);

        crossBoundary = new CrossBoundary();
        stringBuffer = new StringBuffer();
        serialPort();

        location = new int[2];
        myView.getLocationOnScreen(location);
        myView.post(new Runnable() {
            @Override
            public void run() {
//                Log.e("myView宽高：", myView.getWidth() + "---" + myView.getHeight());
//                lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth()-20, myView.getHeight()-20);

            }
        });

//        showDialog(this,false,"恭喜您本次考试以通过，从此您就是有证的人");


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
                map.put("androidId", "12345678");
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
            } catch (JsonSyntaxException e) {
                Log.e("车解析异常：", e.getMessage());
            }

        }
    }

    public void serialPort() {
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
                                    locationStr = locationStr1 + new String(bytes);
                                    if (locationStr.contains("$")) {
                                        if (locationStr.indexOf("$") != locationStr.lastIndexOf("$")) {
                                            locationStr1 = locationStr.substring(locationStr.indexOf("$"), locationStr.lastIndexOf("$") - 1);
                                            locationStr = "";
                                        } else {
                                            locationStr1 = locationStr;
                                        }

                                    } else {
                                        locationStr1 = locationStr;
                                    }
                                    Log.e("定位数据是否正确：", crossBoundary.checkData(locationStr1) + "");
                                    if (crossBoundary.checkData(locationStr1)) {
                                        String[] arr = locationStr1.split(",");
//                                    if (arr.length <= 20)
//                                        return;

                                        try {
                                            double lng = Double.parseDouble(arr[2]);
                                            double lat = Double.parseDouble(arr[3]);
                                            double angle = Double.parseDouble(arr[5]);
                                            Log.e("定位截取的数据", "经度：" + lng + "纬度" + lat);
                                            if (lngLatToXYTools != null && myviewCar != null && crossBoundary != null && pathInOrderPro != null) {
                                                if (crossBoundary.isCross(locationStr1) && !isShow) {
                                                    //true越出边界
                                                    showDialog(StartActivity.this, false, "您已越出边界，考试结束");
                                                    isShow = true;
                                                    myviewCar.transferData();
                                                }

                                                if (!pathInOrderPro.detectionPath(lng, lat)) {
                                                    //false未按规定路线行驶
                                                    showDialog(StartActivity.this, false, "您未按规定路线行驶，考试结束");
                                                    isShow = true;
                                                    myviewCar.transferData();
                                                }
                                                myviewCar.getPoint(lngLatToXYTools.getLngLatToXyPro(lng, lat), angle);
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.e("定位串口数据NumberFormat：", e.getMessage());
                                        } catch (NullPointerException e) {
                                            Log.e("定位串口数据NullPointer：", e.getMessage());

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
                    .openSerialPort(new File("/dev/ttyS3"), 115200);
//                        .openSerialPort(devices.get(i).getFile(), 9600);

            Log.e("串口", "onCreate: openSerialPort = " + openSerialPort);


            //熄火串口信息

            // 打开串口
//            for (int i = 0; i < devices.size(); i++) {
//                Log.e("onCreate: device =", "onCreate: device root= " + devices.get(i).getRoot()
//                        + "\nonCreate: device name= " + devices.get(i).getName()
//                        + "\nonCreate: device file= " + devices.get(i).getFile());
            boolean openSerialPort2 = mSerialPortManager.setOnOpenSerialPortListener(this)
                    .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes) {
                            Log.e("熄火串口", "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("熄火串口", "onDataReceived [ String ]: " + new String(bytes));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fireStr = fireStr1 + new String(bytes);
                                    if (fireStr.contains("AA4412")) {
                                        if (fireStr.indexOf("AA4412") != fireStr.lastIndexOf("AA4412")) {
                                            fireStr1 = fireStr.substring(fireStr.indexOf("AA4412"), fireStr.lastIndexOf("AA4412") - 1);
                                            fireStr = "";
                                        } else {
                                            fireStr1 = fireStr;
                                        }

                                    } else {
                                        fireStr1 = fireStr;
                                    }
                                    Log.e("熄火数据是否正确：", crossBoundary.checkDataStr(fireStr1) + "");
                                    if (crossBoundary.checkDataStr(fireStr1)) {
                                        try {
                                            Log.e("熄火截取的数据", "是否：" + fireStr1);
                                            if (fireStr1.equals("AA441204")) {
                                                //true越出边界
                                                showDialog(StartActivity.this, false, "您已熄火，考试结束");
                                                isShow = true;
                                                myviewCar.transferData();
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.e("熄火串口数据NumberFormat：", e.getMessage());
                                        } catch (NullPointerException e) {
                                            Log.e("熄火串口数据NullPointer：", e.getMessage());

                                        }
                                    }
                                }
                        });
                    }

            @Override
            public void onDataSent ( byte[] bytes){
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
}
