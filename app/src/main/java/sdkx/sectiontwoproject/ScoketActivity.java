package sdkx.sectiontwoproject;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import java.net.URI;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdkx.sectiontwoproject.http.HttpUrl;
import sdkx.sectiontwoproject.util.JWebSocketClient;

public class ScoketActivity extends AppCompatActivity {

    @BindView(R.id.send)
    TextView send;
    @BindView(R.id.get)
    TextView get;
    @BindView(R.id.content)
    TextView content;
    private JWebSocketClient client;
    private SoundPool mSoundPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoket);
        ButterKnife.bind(this);
        //初始化webSocket
        initSocketClient();
        //播放音频文件
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes aab = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build() ;
            mSoundPool = new SoundPool.Builder()
                     .setMaxStreams(10)
                     .setAudioAttributes(aab)
                     .build();
        }else{
            mSoundPool = new SoundPool(60, AudioManager.STREAM_MUSIC,8) ;
        }
        //创建一个HashMap对象,将要播放的音频流保存到HashMap对象中
        final HashMap<Integer, Integer> soundmap = new HashMap<Integer, Integer>();
        soundmap.put(0, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(1, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(2, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(3, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(4, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(5, mSoundPool.load(this, R.raw.music12613, 1));
        soundmap.put(6, mSoundPool.load(this, R.raw.music12613, 1));
        mSoundPool.play(soundmap.get(0), 1, 1, 0, 0, 1);  //播放所选音频
    }


    @OnClick({R.id.send, R.id.get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (client != null && client.isOpen()) {
                    client.send("你好");
                }
                break;
            case R.id.get:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        closeConnect();
        super.onDestroy();
    }



    /**
     * 断开连接
     */
    public  void closeConnect() {
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

    private  final long HEART_BEAT_RATE = 5 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            content.setText(msg.obj.toString());
        }
    };
    private  Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("JWebSocketClientService", "心跳包检测websocket连接状态");
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

    public  void initSocketClient() {
        URI uri = URI.create(HttpUrl.WEBSOCKET_URL + HttpUrl.ANDROIDID);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                Log.e("接收到的消息", message);
                Message message1=new Message();
                message1.obj=message;
                mHandler.sendMessage(message1);
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
    private  void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



}
