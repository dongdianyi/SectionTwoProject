package sdkx.sectiontwoproject;


import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

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
    private String s = "";
    StringBuffer stringBuffer;

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
            Filed filed = gson.fromJson(object, Filed.class);
            for (int i = 0; i < filed.getData().size(); i++) {
                mList.add(new SitePort(filed.getData().get(i).getLng(), filed.getData().get(i).getLat()));
            }
            Log.e("myView宽高：", myView.getWidth() + "---" + myView.getHeight());
            lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth()-20, myView.getHeight()-40);
            List<XyPojo> xyPojos = lngLatToXYTools.siteAdjust(mList);
            Log.e("lngLatToXYTools缩放比例：",lngLatToXYTools.getxScaling()+"---"+lngLatToXYTools.getyScaling());
            //传入点的集合
            RelativeLayout.LayoutParams l=new RelativeLayout.LayoutParams((int)(double)xyPojos.get(0).getX()+50,(int)(double)xyPojos.get(0).getY()+50);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            myView.setLayoutParams(l);
            myviewCar.setLayoutParams(l);
            myView.getPoints(xyPojos);
            map.put("androidId", "12345678");
            noHttpRx.postHttp("车", HttpUrl.GETCAR_URL, map, null);
        }
        if (flag.equals("车")) {
            Car car=gson.fromJson(object,Car.class);
            myviewCar.getCar(car,lngLatToXYTools);


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
            boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                    .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes) {
                            Log.e("串口", "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                            Log.e("串口", "onDataReceived [ String ]: " + new String(bytes));
                            final byte[] finalBytes = bytes;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    s = new String(finalBytes);
                                    Log.e("数据是否正确：",crossBoundary.checkData(s)+"");
                                    if (crossBoundary.checkData(s)) {

                                        String []arr=s.split(",");
                                        try {
                                            double lng=Double.parseDouble(arr[2]);
                                            double lat=Double.parseDouble(arr[3]);
                                            double angle=Double.parseDouble(arr[5]);
                                            Log.e("截取的数据","经度："+lng+"纬度"+lat);
                                            myviewCar.getPoint(lngLatToXYTools.getLngLatToXyPro(lng,lat),angle);
                                        }catch (NumberFormatException e){
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

            Log.e("串口", "onCreate: openSerialPort = " + openSerialPort);
        }

//        }
    }
}
