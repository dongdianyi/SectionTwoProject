package sdkx.sectiontwoproject.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import sdkx.sectiontwoproject.R;
import sdkx.sectiontwoproject.StartActivity;
import sdkx.sectiontwoproject.base.BaseActivity;
import sdkx.sectiontwoproject.bean.Car;
import sdkxsoft.com.pojo.XyPojo;
import sdkxsoft.com.tools.LngLatToXYTools;

public class MyViewCar extends View {

    private int result;
    private Bitmap bitmap, bitmapPoint;
    private Paint paint;
    private Path mPath;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float alpha;
    private String carType = "";
    private double gpswidth;
    private double gpslength;
    private double length;
    private double width;
    private XyPojo xyPojo;
    private float scaleWidth;
    private float scaleHeight;
    private Matrix matrix;
    List<Float>xyPojoXY;

    private Context context;


    public MyViewCar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        //画笔设置
        paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);//空心
        paint.setStrokeWidth(1f);

        mPath = new Path();
        xyPojoXY=new ArrayList<>();

    }

    public void getCar(Car car, LngLatToXYTools lngLatToXYTools) {
        carType = car.getData().getCarType();
        length = car.getData().getLength() * lngLatToXYTools.getxScaling();
        width = car.getData().getWidth() * lngLatToXYTools.getyScaling();

        gpswidth = width * car.getData().getGpswidth();
        gpslength = length * (1 - car.getData().getGpslength());
        Log.e("车大小:", length + "---" + width);
        Log.e("gps车大小:", gpswidth + "---" + gpslength);
        invalidate();

    }

    /**
     *
     * @param xyPojo
     * @param angle 角度
     */
    public void getPoint(XyPojo xyPojo, double angle) {

        this.xyPojo = xyPojo;
        alpha = (float) angle;
        //添加真实经纬度到集合
        Log.e("Gps经纬度", xyPojo.getX() + "---" + xyPojo.getY());
        Log.e("Gps坐标", (float) (double) xyPojo.getX() + "---" + (float) (double) xyPojo.getY());
        Log.e("车左上", (int) (xyPojo.getX() - gpslength) + "---" + (int) (xyPojo.getY() - gpswidth));
//        this.xyPojo2 = getNewPoint(xyPojo.getX(), xyPojo.getY(), angle, Math.sqrt(gpswidth * gpswidth + gpslength * gpslength));
//        Log.e("旋转之后的点：", this.xyPojo2.getX() + "---" + this.xyPojo2.getY());

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measured(widthMeasureSpec), measured(heightMeasureSpec));
    }

    /***
     * Mode
     * size
     * @param measureSpec
     * @return
     */
    private int measured(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
            return specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(200000, specSize);
                return Math.min(200000, specSize);
            }
        }
        result = 0;
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmapPoint = BitmapFactory.decodeResource(getResources(), R.mipmap.equipment_point);
        //车的模型图片

        if (carType.equals("K1")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.k1);
        }
        if (carType.equals("K2")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.k2);
        }
        if (carType.equals("G1")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.g1);
        }
        if (carType.equals("G2")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.g2);
        }
        if (carType.equals("L")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.l);
        }
        if (carType.equals("R")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.r);
        }
        if (carType.equals("S")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.s);
        }
        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int widthPoint = bitmapPoint.getWidth() / 2;
        int heightPoint = bitmapPoint.getHeight() / 2;

        // 计算缩放比例
        scaleWidth = ((float) this.length) / width;
        scaleHeight = ((float) this.width) / height;
        //设置铺满画定位圆点
        if (xyPojo != null) {
            canvas.save();
            canvas.drawBitmap(bitmapPoint, (float) (double) xyPojo.getX() - widthPoint, (float) (double) xyPojo.getY() - heightPoint, paint);

            //保存运行轨迹的点
            xyPojoXY.add((float) (double) xyPojo.getX());
            xyPojoXY.add((float) (double) xyPojo.getY());

            //车缩放移动旋转
            //车的运行轨迹
            matrix = new Matrix();
            matrix.reset();
            matrix.postScale(scaleWidth, scaleHeight);

            matrix.postTranslate((float) (xyPojo.getX() - gpslength), (float) (xyPojo.getY() - gpswidth));
            matrix.postRotate(alpha, (float) (double) xyPojo.getX() , (float) (double) xyPojo.getY());


            canvas.drawBitmap(bitmap, matrix, paint);
            matrix.reset();
            canvas.restore();

            paint.reset();
            paint.setColor(Color.rgb(77, 211, 213));
            paint.setStyle(Paint.Style.STROKE);//空心


            //不抗锯齿
            paint.setAntiAlias(false);
            //画虚线
            paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
            mPath.reset();
            mPath.moveTo(xyPojoXY.get(0), xyPojoXY.get(1));
            for (int i = 2; i < xyPojoXY.size()-1; i=i+2) {
                mPath.lineTo(xyPojoXY.get(i),xyPojoXY.get(i+1));
            }
            canvas.drawPath(mPath, paint);

        }

        Log.e("屏幕尺寸", (Math.sqrt(1920 * 1920 + 1080 * 1080) / 25.4) + "result" + result);
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /***
     * onTouch事件将点存入graphics集合中
     * @param
     * @return
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                over = false;
//                graphics.clear();
//                of = 0;
//                graphics.add(new PointF(x, y));
//                touch_start(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                graphics.add(new PointF(x, y));
//                touch_move(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                over = true;
//                touch_up();
//                invalidate();
//                break;
//        }
//
//        return true;
//
//    }
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        //mPath.lineTo(mX, mY);
    }

    //旋转
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();

        Matrix matrix = new Matrix();
//        matrix.postScale(-1, 1);
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.setRotate(alpha);
        matrix.setTranslate((float) (xyPojo.getX() - gpslength), (float) (xyPojo.getY() - gpswidth));
//        matrix.postRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);

        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    public XyPojo getNewPoint(Double lng, Double lat, Double angle, Double bevel) {
        //在Flash中顺时针角度为正，逆时针角度为负
        //换算过程中先将角度转为弧度
        Double radian = Math.toRadians(angle - 90);
        Double xMargin = Math.cos(radian) * bevel;
        Double yMargin = Math.sin(radian) * bevel;
        if ((angle) > 0 && (angle) < 180) {
            yMargin = -yMargin;
        }
//        if (angle < 90 || angle > 270) {
//            xMargin = -xMargin;
//        }

        return new XyPojo((lng - xMargin), (lat + yMargin));
    }

}
