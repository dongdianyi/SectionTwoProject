package sdkx.sectiontwoproject;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.liqi.nohttputils.nohttp.NoHttpInit;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
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
import sdkx.sectiontwoproject.bean.ReceiveMessage;
import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.model.NoHttpRx;
import sdkx.sectiontwoproject.myview.MyView;
import sdkx.sectiontwoproject.myview.MyViewCar;
import sdkx.sectiontwoproject.util.CrossBoundary;
import sdkx.sectiontwoproject.util.JWebSocketClient;
import sdkx.sectiontwoproject.util.ShotScreenManager;
import sdkxsoft.com.CarTrajectory;
import sdkxsoft.com.pojo.SitePort;
import sdkxsoft.com.pojo.XyPojo;
import sdkxsoft.com.tools.LngLatToXYTools;
import sdkxsoft.com.tools.PathInOrderPro;
import sdkxsoft.com.tools.RotationAngleTools;

import static sdkx.sectiontwoproject.util.CrossBoundary.ASTERNWAY;
import static sdkx.sectiontwoproject.util.CrossBoundary.OUT_ERROR;
import static sdkx.sectiontwoproject.util.CrossBoundary.PILE_ERROR;
import static sdkx.sectiontwoproject.util.UtilLog.showLogE;
import static sdkx.sectiontwoproject.util.UtilLog.showToast;

public class StartActivity extends BaseActivity<String> {


    @BindView(R.id.linear)
    LinearLayout linear;

    @BindView(R.id.myView)
    MyView myView;
    @BindView(R.id.myview_car)
    MyViewCar myviewCar;
    @BindView(R.id.myView_relative)
    RelativeLayout myViewRelative;
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
    private String info = "";

    //webSocket监测下发信息
    private JWebSocketClient client;
    //学生id
    private String id;

    //终止标记
    private boolean isTermination=false;

    private SoundPool mSoundPool;
    private ReceiveMessage receiveMessage;

    private final int whatNum = 0x0;
    private Car.DataBean.LocationData locationData;
    private Car.DataBean.FlameoutData flameoutData;
    private HashMap<Integer, Integer> soundmap;

    @Override
    public int intiLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void initData() {
        MyApplication.getInstance().addActivity(this);
        id = getIntent().getStringExtra("id");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MyApplication.getInstance().getWidth() - 250, MyApplication.getInstance().getHeight() - 250);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);//居中显示
        linear.setLayoutParams(layoutParams);
        //截屏
       /* Bitmap bitmap = ShotScreenManager.getInstance().picShotScreen(this, getFilesDir().getAbsolutePath() + "pic.jpg", 70);
        Log.e("bitmap", bitmap.toString());
        Bitmap bitmap = ShotScreenManager.getViewBitmap(myViewRelative);
        showLogE("bitmap",bitmap.toString());
        ShotScreenManager.savePhotoToSDCard(bitmap, "/sdcard/file", "img");*/
        crossBoundary = new CrossBoundary();
        lngLatData = new ArrayList<>();
        // 未按规定路线行驶；越界；碰杆；熄火；考官主观判断；
        gradeArr = new int[]{1, 1, 1, 1, 1};
       /* location = new int[2];
        myView.getLocationOnScreen(location);
        myView.post(new Runnable() {
            @Override
            public void run() {
                Log.e("myView宽高：", myView.getWidth() + "---" + myView.getHeight());
                lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth()-20, myView.getHeight()-20);

            }
        });*/


        //播放音频文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aab = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(aab)
                    .build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 8);
        }
        //创建一个HashMap对象,将要播放的音频流保存到HashMap对象中
        //load完成之后才能进行play 可能需要一秒之后才能load完成
        soundmap = new HashMap<Integer, Integer>();
        soundmap.put(0, mSoundPool.load(this, R.raw.no_regulations, 1));
        soundmap.put(1, mSoundPool.load(this, R.raw.cross, 1));
        soundmap.put(2, mSoundPool.load(this, R.raw.bumper_rod, 1));
        soundmap.put(3, mSoundPool.load(this, R.raw.no_fire, 1));
        soundmap.put(4, mSoundPool.load(this, R.raw.no_discipline, 1));
        soundmap.put(5, mSoundPool.load(this, R.raw.success, 1));
        mList = new ArrayList<>();

        noHttpRx = new NoHttpRx(this);

        map = new HashMap();
        map.put("androidId", HttpUrl.ANDROIDID);
        noHttpRx.postHttpJson("考场", HttpUrl.GETFIELD_URL, JSON.toJSONString(map), null);

    }

    @Override
    public void toActivityData(String flag, String object) {
        super.toActivityData(flag, object);
        showLogE("请求数据：" + flag, object);
        Gson gson = new Gson();
        if (flag.equals("考场")) {
            try {

                filed = gson.fromJson(object, Filed.class);
                if (filed.getData() == null)
                    return;

                for (int i = 0; i < filed.getData().size(); i++) {
                    mList.add(new SitePort(filed.getData().get(i).getLng(), filed.getData().get(i).getLat()));
                }
                showLogE("myView宽高：", myView.getWidth() + "---" + myView.getHeight());

                lngLatToXYTools = CarTrajectory.getSiteTools(myView.getWidth() - 20, myView.getHeight() - 40);
                rotationAngleTools = CarTrajectory.getCarPoint(lngLatToXYTools, 90);
                //拿到path对象
                pathInOrderPro = CarTrajectory.getPathOrder(mList);
                //8点坐标
                List<XyPojo> xyPojos = lngLatToXYTools.siteAdjust(mList);

                showLogE("lngLatToXYTools缩放比例：", lngLatToXYTools.getxScaling() + "---" + lngLatToXYTools.getyScaling());

                //传入点的集合
                RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams((int) (double) xyPojos.get(0).getX() + 50, (int) (double) xyPojos.get(0).getY() + 50);
                l.addRule(RelativeLayout.CENTER_IN_PARENT);
                myView.setLayoutParams(l);
                myviewCar.setLayoutParams(l);
                myView.getPoints(xyPojos);
                map.clear();
                map.put("androidId", HttpUrl.ANDROIDID);
                noHttpRx.postHttpJson("车", HttpUrl.GETCAR_URL, JSON.toJSONString(map), null);
            } catch (JsonSyntaxException e) {
                showLogE("场地解析异常：", e.getMessage());

            }
        }
        if (flag.equals("车")) {
            try {
                Car car = gson.fromJson(object, Car.class);
                if (car.getData() == null) {
                    return;
                }
                locationData = car.getData().getLocation();
                flameoutData = car.getData().getFlameout();
                myviewCar.getCar(car, lngLatToXYTools);
                //录入车辆场地信息
                crossBoundary.setMessage(JSON.toJSONString(filed.getData()), JSON.toJSONString(car.getData().getCarPoints()), "");

                //初始化webSocket
                initSocketClient();

                serialPort();
            } catch (JsonSyntaxException e) {
                showLogE("车解析异常：", e.getMessage());

            }

        }
        if (flag.equals("提交数据")) {
            if (isShow) {
                return;
            }
            isShow = true;
            showDialog(this, false, info);
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
                            showLogE("定位串口：", "[ byte[] ]: " + Arrays.toString(bytes) + "\n[ String ]: " + new String(bytes));

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
                                        showLogE("定位数据$：", locationStr + "\n" + locationStr.indexOf("$") + "----" + locationStr.indexOf("$", locationStr.indexOf("$") + 1));

                                        if (locationStr.indexOf("$") < locationStr.indexOf("$", locationStr.indexOf("$") + 1)) {
                                            locationStr1 = locationStr.substring(locationStr.indexOf("$"), locationStr.indexOf("$", locationStr.indexOf("$") + 1));
                                            locationStr2 = locationStr.substring(locationStr.indexOf("$", locationStr.indexOf("$") + 1));
                                            showLogE("locationStr12：", locationStr1 + "\n" + locationStr2);

                                        } else {
                                            locationStr2 = locationStr;
                                            return;
                                        }

                                    } else {
                                        return;
                                    }
                                    showLogE("定位数据：", locationStr1 + "\n定位数据是否正确" + crossBoundary.checkData(locationStr1));

                                    if (crossBoundary.checkData(locationStr1)) {
                                        String[] arr = locationStr1.split(",");

                                        double lng = Double.parseDouble(arr[2]);
                                        double lat = Double.parseDouble(arr[3]);
                                        double angle = Double.parseDouble(arr[5]);

                                        //精度为3
//                                        double angle = Double.parseDouble(arr[10]);
//                                        double angle = Double.parseDouble(arr[11]);
                                        showLogE("定位截取的数据：", "经度：" + lng + "纬度" + lat + "angle:" + angle);
                                        if (isTermination||isShow) {
                                            return;
                                        }
                                        lngLatData.add(new SitePort(lng, lat, angle));

                                        myviewCar.getPoint(lngLatToXYTools.getLngLatToXyPro(lng, lat), rotationAngleTools.getCarAnglePro(angle));
                                            if (crossBoundary.isCross(locationStr1) == OUT_ERROR) {
                                                //越界
                                                gradeArr[1] = 0;
                                                submit(getResources().getString(R.string.cross),1);
                                                return;
                                            }
                                            if (crossBoundary.isCross(locationStr1) == ASTERNWAY) {
                                                //库外倒车 未按规定路线行驶
                                                gradeArr[0] = 0;
                                                submit(getResources().getString(R.string.no_regulations),0);
                                                return;
                                            }
                                            if (crossBoundary.isCross(locationStr1) == PILE_ERROR) {
                                                //碰杆
                                                gradeArr[2] = 0;
                                                submit(getResources().getString(R.string.bumper_rod),2);
                                                return;
                                            }

                                            Boolean msgboo = pathInOrderPro.detectionPath(lng, lat);
                                            if (msgboo != null) {
                                                if (msgboo == false) {
                                                    //false未按规定路线行驶
                                                    gradeArr[0] = 0;
                                                    submit(getResources().getString(R.string.no_regulations),0);
                                                    return;
                                                }
                                            }

                                    }
                                }
                            });
                        }

                        @Override
                        public void onDataSent(byte[] bytes) {

                            showLogE("定位串口：", "[ byte[] ]: " + Arrays.toString(bytes) + "\n[ String ]: " + new String(bytes));
                            final byte[] finalBytes = bytes;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(String.format("发送\n%s", new String(finalBytes)));
                                }
                            });
                        }
                    })
                    .openSerialPort(new File(locationData.getSerialNumber()), locationData.getBaudRate());
//                        .openSerialPort(devices.get(i).getFile(), 9600);

            showLogE("串口：", "onCreate: openSerialPort = " + openSerialPort);


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
                            //串口转大写16进制
                            showLogE("熄火串口：", "[ byte[] ]: " + Arrays.toString(bytes) + "\n[ String ]: " + new BigInteger(1, bytes).toString(16));

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
                                        showLogE("熄火数据AA4412：", fireStr + "\n" + fireStr.indexOf("AA4412") + "----" + fireStr.indexOf("AA4412", fireStr.indexOf("AA4412") + 1));

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
                                    showLogE("熄火数据", fireStr1 + "\n熄火数据是否正确" + crossBoundary.checkDataStr(fireStr1));

                                    if (crossBoundary.checkDataStr(fireStr1)) {
                                        fireStr2 = "";
                                        if (fireStr1.contains("AA441204") && !isShow) {
                                            //熄火
                                            gradeArr[3] = 0;
                                            submit(getResources().getString(R.string.no_fire),3);
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onDataSent(byte[] bytes) {
                            showLogE("熄火串口：", "[ byte[] ]: " + Arrays.toString(bytes) + "\n[ String ]: " + new BigInteger(1, bytes).toString(16));

                            final byte[] finalBytes = bytes;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(String.format("发送\n%s", new String(finalBytes)));
                                }
                            });
                        }
                    })
                    .openSerialPort(new File(flameoutData.getSerialNumber()), flameoutData.getBaudRate());
//                        .openSerialPort(devices.get(i).getFile(), 9600);

            showLogE("串口：", "onCreate: openSerialPort2 = " + openSerialPort2);

        }
//        }
    }

    @Override
    protected void onDestroy() {
        //关闭串口
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
        }
        if (null != mSerialPortManager2) {
            mSerialPortManager2.closeSerialPort();
            mSerialPortManager2 = null;
        }
        //关闭webSocket连接
        closeConnect();
        mSoundPool.release();
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
        showLogE("串口打开：", String.format("串口 [%s] 打开成功", device.getPath()));

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
    public void submit(String info,int index) {
        showLogE("streamId", mSoundPool.play(soundmap.get(index), 1, 1, 0, 0, 1) + "");  //播放所选音频
        this.info = info;
        noHttpRx = new NoHttpRx(this);
        map = new HashMap();
        Bitmap bitmap=ShotScreenManager.getViewBitmap(myViewRelative);
        showLogE("bitmap", ShotScreenManager.bitmapToBase64(bitmap));
        map.put("image", ShotScreenManager.bitmapToBase64(bitmap));
        map.put("sitePort", lngLatData);
        map.put("androidId", HttpUrl.ANDROIDID);
        map.put("gradeArr", gradeArr);
        map.put("info", info);
        noHttpRx.postHttpJson("提交数据", HttpUrl.SETDATA_URL, JSON.toJSONString(map), null);
    }

   /* @SingleClick
    @OnClick({R.id.exit})
    public void onViewClicked(View view) {
        if (!isShow) {
            isShow = true;
            submit(getResources().getString(R.string.success));
        }
    }*/


    /**
     * 断开连接
     */
    public void closeConnect() {
        //移除心跳监测
        mHandler.removeCallbacks(heartBeatRunnable);
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;

        }
    }

    private final long HEART_BEAT_RATE = 5 * 1000;//每隔5秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();

    private Handler mHandlerMessage = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == whatNum) {
                try {
                    receiveMessage = new Gson().fromJson(msg.obj.toString(), ReceiveMessage.class);
                    if (client != null && client.isOpen()) {
                        client.send(JSON.toJSONString(new ReceiveMessage("reply", receiveMessage.getData(), "1212", id)));
                    }
                    if (!isShow) {
                        if (receiveMessage.getData().getState() == 0) {
                            //0考试结束 判断全部点符不符合规定
                            if (isTermination) {
                                //违反考试纪律
                                submit(receiveMessage.getData().getReason(),4);
                            } else {
                                Boolean msgboo = pathInOrderPro.detectionPathAll(lngLatData);
                                if (msgboo != null) {
                                    if (msgboo == false) {
                                        //false未按规定路线行驶
                                        gradeArr[0] = 0;
                                        submit(getResources().getString(R.string.no_regulations),0);
                                    } else {
                                        //成绩合格
                                        submit(getResources().getString(R.string.success),5);
                                    }
                                }
                            }
                            } else {
                                //考试终止
                                isTermination = true;
                                gradeArr[4] = 0;
                            }
                    }
                    } catch(JsonSyntaxException e){
                        showLogE("JsonSyntaxException", e.getMessage());
                    }
                }
            return false;
        }
    });
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            showLogE("JWebSocketClientService", "心跳包检测webSocket连接状态");
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    public void initSocketClient() {
        URI uri = URI.create(HttpUrl.WEBSOCKET_URL + id);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                showLogE("接收到的消息", message);
                Message message1 = new Message();
                message1.what = whatNum;
                message1.obj = message;
                mHandlerMessage.sendMessage(message1);
            }
        };
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    showLogE("JWebSocketClientService", "开启重连");

                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
