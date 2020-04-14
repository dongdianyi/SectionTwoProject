package sdkx.sectiontwoproject.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 截屏管理类
 * //非视频类型截屏
 * ShotScreenManager.getInstance().picShotScreen(activity,filePath,70);
 * //视频类型截屏,截出的图片是全黑的,只能用视频指定时间,抽取出一阵图片的方式用作截屏
 * //time:视频啊播放的时间已经乘以了1000
 * ShotScreenManager.getInstance().videoShotScreen(videoPath,filePath,2,70);
 */
public class ShotScreenManager {
    private static final String TAG = "ShotScreenManager";

    private static ShotScreenManager instance;

    public static ShotScreenManager getInstance() {
        if (instance == null) {
            synchronized (ShotScreenManager.class) {
                if (instance == null) {
                    instance = new ShotScreenManager();
                }
            }
        }
        return instance;
    }

    public void videoShotScreen(String videoSource,String saveFilePath,int time,int options){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoSource);
        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//时长(毫秒)
        //视频时长要大于截图的时长  同时传入时长要大于0
        if (duration >= time * 1000 && time > 0){
            Bitmap bitmap = retriever.getFrameAtTime(time * 1000,
                    //OPTION_NEXT_SYNC,与传入时间相近的一帧视频图片
                    MediaMetadataRetriever.OPTION_NEXT_SYNC);
            if (bitmap != null) {
                try {
                    compressAndGenImage(bitmap,saveFilePath,options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Log.e(TAG,"the time is out of video");
            return;
        }

    }

    /**
     * 截屏
     * @param activity
     * @param saveFilePath
     * @param options
     * @return
     */
    public Bitmap picShotScreen(Activity activity, String saveFilePath, int options){
        if (activity == null){
            Log.e(TAG,"screenShot--->activity is null");
            return null;
        }
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取屏幕宽和高
        int width = getSceenWidth(activity);
        int height = getSceenHeight(activity);

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height );
        } catch (Exception e) {
            e.printStackTrace();
        }
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        if (null != bitmap){
            try {
                compressAndGenImage(bitmap,saveFilePath,options);
                Log.e(TAG,"--->截图保存地址：" + saveFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取屏幕高度
     * @param activity
     * @return
     */
    public int getSceenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight() ;
    }

    /**
     * 获取屏幕宽度
     * @param activity
     * @return
     */
    public int getSceenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth() ;
    }

    /**
     * 带压缩的保存图片
     * @param image
     * @param outPath
     * @param options 压缩比例
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath,int options) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        Log.d(TAG,"compressAndGenImage--->文件大小：" + os.size()+"，压缩比例：" + options);
        fos.flush();
        fos.close();
    }

}
