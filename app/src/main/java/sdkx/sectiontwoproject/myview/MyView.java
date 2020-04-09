package sdkx.sectiontwoproject.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import sdkx.sectiontwoproject.R;
import sdkxsoft.com.pojo.XyPojo;


public class MyView extends View {

    private int result;
    private Paint paint;
    private Bitmap bitmap;
    private float[] pointX,pointY;
    private final int color;
    private final boolean centerInParent;

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        color = Color.rgb(77, 211, 213);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        centerInParent = ta.getBoolean(R.styleable.MyView_centerInParent,true);
    }

    public void getPoints(List<XyPojo> points) {
        //传入点集合需要重新绘制
        pointX=new float[points.size()];
        pointY=new float[points.size()];
        for (int i = 0; i < points.size(); i++) {
            pointX[i]=(float)(double)points.get(i).getX();
            pointY[i]=(float)(double)points.get(i).getY();

            Log.e("库坐标"+(i+1),pointX[i]+"---"+pointY[i]);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measured(widthMeasureSpec), measured(heightMeasureSpec));
    }

    private int measured(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
            return result;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(200000, specSize);
                return result;
            }
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //初始化下方图片得到宽高
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_line);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //初始化画笔
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);

        //车库
        if (pointX == null||pointY==null) {
            return;
        }
        if (pointX.length != 8||pointY.length!=8) {
            return;
        }
        paint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(pointX[0], pointY[0]);
        for (int i = 1; i < pointX.length; i++) {
            path.lineTo(pointX[i], pointY[i]);
        }

        path.close();
        //画线
        canvas.drawPath(path, paint);

        //不抗锯齿
        paint.setAntiAlias(false);
        //画虚线
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        Path dashPath = new Path();
        dashPath.moveTo(pointX[5], pointY[5]);
        dashPath.lineTo(pointX[2], pointY[2]);
        canvas.drawPath(dashPath, paint);

        //画点画数字
        paint.reset();
        paint.setColor(color);
        paint.setTextSize(15);
        paint.setStrokeWidth(1f);
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < pointX.length; i++) {
            canvas.drawCircle(pointX[i], pointY[i], 5, paint);
        }
        canvas.drawText("1",pointX[0],pointY[0]+20,paint);
        canvas.drawText("2",pointX[1]-5,pointY[1]-20,paint);
        canvas.drawText("3",pointX[2]+20,pointY[2]-20,paint);
        canvas.drawText("4",pointX[3]+20,pointY[3],paint);
        canvas.drawText("5",pointX[4]-20,pointY[4],paint);
        canvas.drawText("6",pointX[5]-20,pointY[5]-20,paint);
        canvas.drawText("7",pointX[6],pointY[6]-20,paint);
        canvas.drawText("8",pointX[7],pointY[7]+20,paint);
//        //底部图片
        Rect rect = new Rect(0, 0, width, height);
        Rect mDestRect = new Rect((int) pointX[7], (int) pointY[7], (int) pointX[0], (int)pointY[0]+10);
        canvas.drawBitmap(bitmap, rect, mDestRect, paint);
        paint.reset();


    }
}
