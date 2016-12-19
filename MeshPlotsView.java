package passionlife.skylife.com.testrxjava.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by youke on 2016/12/19.
 * lol英雄属性网状图
 */
public class MeshPlotsView extends View {

    //
    private Paint mMeshPaint = new Paint();
    private Paint mConveragePaint = new Paint();
    private Paint mTextPaint = new Paint();
    //中心点x
    private float mCenterX;
    //中心点y
    private float mCenterY;
    //属性数
    private int mCount=6;
    //网格最大半径
    private float mRadius;
    //一份的角度，用于计算每一个点的想x,y坐标
    private float angle = (float) (2 * Math.PI / mCount);

    //默认的宽高
    private int mDefWith = 500;

    //属性值
    private String[] propertyDesc;
    private ArrayList<Integer> propertys;

    public MeshPlotsView(Context context) {
        super(context);
    }

    public MeshPlotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //蜘蛛网画笔填充样式
        mMeshPaint.setStyle(Paint.Style.STROKE);
        mMeshPaint.setAntiAlias(true);

        //覆盖区域画笔
        mConveragePaint.setStyle(Paint.Style.FILL);
        mConveragePaint.setColor(Color.RED);

        //属性文字画笔
        mTextPaint.setTextSize(20);
    }

    //
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRadius = Math.min(mCenterX, mCenterY) / 3 * 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (mCount!=0 && propertyDesc!=null){
            drawMesh(canvas);
            drawLine(canvas);
            drawText(canvas);

            if (propertys != null) {
                drawCoverage(canvas);
            }
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //针对wrap_content属性时设置一个默认大小
        if (withMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            withSize = mDefWith;
            heightSize = mDefWith;
        } else if (withMode == MeasureSpec.AT_MOST) {
            withSize = mDefWith;
        } else if (heightMode ==MeasureSpec.AT_MOST){
            heightSize = mDefWith;
        }

        setMeasuredDimension(withSize, heightSize);
    }

    /**
     * 画蜘蛛网
     */
    private void drawMesh(Canvas canvas) {
        float radius;
        Path path = new Path();
        for (int i = 1; i < mCount; i++) {
            //每一个小网格的半径
            radius = mRadius / (mCount - 1) * i;
            //重置路径
            path.reset();

            for (int j = 0; j < mCount; j++) {
                //每一个画网格都把起点放置在多边形的起点
                if (j == 0) {
                    path.moveTo(mCenterX + radius, mCenterY);
                } else {
                    //确定每一个点的坐标
                    float x = (float) (mCenterX + radius * Math.cos(angle * j));
                    float y = (float) (mCenterY + radius * Math.sin(angle * j));
                    //根据坐标画线
                    path.lineTo(x, y);
                }
            }
            //闭合路径，连接path的最初点和最终点
            path.close();
            canvas.drawPath(path, mMeshPaint);
        }

    }


    /**
     * 画连接蜘蛛网的线
     */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < mCount; i++) {
            path.reset();
            //每一条线的起点都是中心点
            path.moveTo(mCenterX, mCenterY);
            //终点是半径的sin和cos的值
            float x = (float) (mCenterX + mRadius * Math.cos(angle * i));
            float y = (float) (mCenterY + mRadius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mMeshPaint);
        }
    }


    /**
     * 画每一个顶点代表的属性
     */
    private void drawText(Canvas canvas) {
        float currAngele;
        float defInset = 10;
        for (int i = 0; i < mCount; i++) {
            float x = (float) (mCenterX + mRadius * Math.cos(angle * i));
            float y = (float) (mCenterY + mRadius * Math.sin(angle * i));
            currAngele = angle * i;
            if (currAngele >= 0 && currAngele < Math.PI / 2) {
                //在第四象限
                x += defInset;
                if (currAngele != 0) {
                    y += defInset;
                } else {
                    y += defInset / 2;
                }
            } else if (currAngele >= Math.PI / 2 && currAngele < Math.PI) {
                //三象限
                float textWith = mTextPaint.measureText(propertyDesc[i]);
                x -= (defInset + textWith);
                y += defInset;
            } else if (currAngele >= Math.PI && currAngele < Math.PI / 2 * 3) {
                //二象限
                float textWith = mTextPaint.measureText(propertyDesc[i]);
                x -= (defInset + textWith);
                if (y == 0) {
                    y -= defInset / 2;
                } else {
                    y += defInset;
                }

            } else {
                //一象限
                x += defInset;
                y -= defInset;
            }
            canvas.drawText(propertyDesc[i], x, y, mTextPaint);
        }
    }


    /**
     * 画属性覆盖区域
     */
    private void drawCoverage(Canvas canvas) {
        Path path = new Path();
        mConveragePaint.setAlpha(255);
        for (int i = 0; i < mCount; i++) {
            float x = (float) (mCenterX + mRadius * propertys.get(i) / 100 * Math.cos(angle * i));
            float y = (float) (mCenterY + mRadius * propertys.get(i) / 100 * Math.sin(angle * i));
            if (i == 0) {
                path.moveTo(x, mCenterY);
            } else {
                path.lineTo(x, y);
            }

            //画点
            canvas.drawCircle(x, y, mRadius/50, mConveragePaint);
        }

        path.close();
        //画线，透明度
        mConveragePaint.setAlpha(127);
        canvas.drawPath(path, mConveragePaint);
    }

    /**
     * 设置属性所占百分比
     */
    public void setPropertys(ArrayList<Integer> propertysPercent) {
        this.propertys = propertysPercent;
        invalidate();
    }

    /**
    *设置覆盖区域的颜色
    */
    public void setmConverageColor(int color){
        mConveragePaint.setColor(color);
    }

    /**
    *设置网格颜色
    */
    public void setMeshPlotsColor(int color){
        mMeshPaint.setColor(color);
    }

    /**
    *设置属性值颜色
    */
    public void setPropertyTextColor(int color){
        mTextPaint.setColor(color);
    }

    /**
    *设置最大属性数
    */
    public void setMaxPropertyCount(int count){
        mCount = count;
        angle = (float) (2 * Math.PI / mCount);
    }

    /**
    *设置属性数据
    */
    public void setMaxPropertyDesc(String[] propertyDesc){
        this.propertyDesc = propertyDesc;
    }
}
