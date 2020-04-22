package sdkx.sectiontwoproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static sdkx.sectiontwoproject.util.UtilLog.showLogE;

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

    public void videoShotScreen(String videoSource, String saveFilePath, int time, int options) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoSource);
        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//时长(毫秒)
        //视频时长要大于截图的时长  同时传入时长要大于0
        if (duration >= time * 1000 && time > 0) {
            Bitmap bitmap = retriever.getFrameAtTime(time * 1000,
                    //OPTION_NEXT_SYNC,与传入时间相近的一帧视频图片
                    MediaMetadataRetriever.OPTION_NEXT_SYNC);
            if (bitmap != null) {
                try {
                    compressAndGenImage(bitmap, saveFilePath, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showLogE(TAG, "the time is out of video");
            return;
        }

    }

    /**
     * 截屏
     *
     * @param activity
     * @param saveFilePath
     * @param options
     * @return
     */
    public Bitmap picShotScreen(Activity activity, String saveFilePath, int options) {
        if (activity == null) {
            showLogE(TAG, "screenShot--->activity is null");
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
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        if (null != bitmap) {
            try {
                compressAndGenImage(bitmap, saveFilePath, options);
                showLogE(TAG, "--->截图保存地址：" + saveFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public int getSceenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public int getSceenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 带压缩的保存图片
     *
     * @param image
     * @param outPath
     * @param options 压缩比例
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath, int options) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        Log.d(TAG, "compressAndGenImage--->文件大小：" + os.size() + "，压缩比例：" + options);
        fos.flush();
        fos.close();
    }


    /**
     * （layout）转化成图片（bitmap）
     * 布局文件没有超出屏幕高度
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * 布局超出了屏幕的高度（Scrollview处理）：
     **/
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap可以再生成图片
     *
     * @param photoBitmap
     * @param path
     * @param photoName
     */
    /** 保存方法 */
    public static void saveBitmap(Context context, Bitmap bmp) {
        if (!checkSDCardAvailable()) {
            return;
        }
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "sectiontwo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
    }
    /**文件存储
     * */
    public static void saveFile(String content) {
        if (!checkSDCardAvailable()) {
            return;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "sectiontwo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "Log.txt";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file,true);
            OutputStreamWriter osw=new OutputStreamWriter(fos);
            osw.write(content);
            osw.flush();
            osw.close();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //检查是否有SD卡
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                //有损体积较小黑色背景
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //无损体积较大透明背景
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
