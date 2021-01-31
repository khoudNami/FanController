package com.example.fancontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class DialView extends View {

    private static int SELECTION_COUNT = 4;
    private int mSelectionCount = 4;
    private float mWidth;
    private float mHeight;
    private Paint mTextPaint;
    private Paint mDialPaint;
    private int mFanOnColor;
    private int mFanOffColor;
    private float mRadius;
    private int mActiveSelection;//position of the indicator {0,1,2,3}

    // String buffer for dial labels and float for ComputeXY result.
    private StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];


    public DialView(Context context) {
        super(context);
        init(null);
    }

    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);

        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(Color.GRAY);

        mActiveSelection = 0;

        mFanOnColor = Color.CYAN;
        mFanOffColor = Color.GRAY;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.DialView,
                    0, 0);
            mSelectionCount = typedArray.getInt(R.styleable.DialView_selectionIndicators, mSelectionCount);
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor);
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor);
            mDialPaint.setColor(mFanOffColor);
            typedArray.recycle();
        }


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActiveSelection = (mActiveSelection + 1) % mSelectionCount;
                if (mActiveSelection >= 1) {
                    mDialPaint.setColor(mFanOnColor);
                } else {
                    mDialPaint.setColor(mFanOffColor);
                }
                invalidate();
            }
        });
    }

    public void setSelectionCount(int count) {
        mSelectionCount = count;
        this.mActiveSelection = 0;
        mDialPaint.setColor(mFanOffColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG", "onDraw() called");
        printDivider();

        float cx = mWidth / 2;
        float cy = mHeight / 2;
        printPositions();
        Log.d("TAG", "cx = " + cx + " cy = " + cy);

        canvas.drawCircle(cx, cy, mRadius, mDialPaint);

        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < mSelectionCount; i++) {
            float[] xyData = computeXYForPosition(i, labelRadius, true);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
            Log.d("TAG", x + " " + y);
        }

        final float markerRadius = mRadius - 35;
        float[] xyDataForMarker = computeXYForPosition(mActiveSelection, markerRadius, false);
        float x = xyDataForMarker[0];
        float y = xyDataForMarker[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }

    private float[] computeXYForPosition(final int pos, final float radius, boolean isLabel) {
        float[] result = mTempResult;
        Double startAngle;
        Double angle;
        if (mSelectionCount > 4) {
            startAngle = Math.PI * (3 / 2d);
            angle = startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle * 2))
                    + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle * 2))
                    + (mHeight / 2);
            if ((angle > Math.toRadians(360)) && isLabel) {
                result[1] += 20;
            }
        } else {
            startAngle = Math.PI * (9 / 8d);
            angle = startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle))
                    + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle))
                    + (mHeight / 2);
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("TAG", "onMeasure() called");
        printDivider();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("TAG", "onLayout() called");

        printDivider();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("TAG", "onSizeChanged() called");
        Log.d("TAG", "w" + w + " h" + h + " oldw" + oldw + " oldh" + oldh);
        printDivider();
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }

    private void printDivider() {
        Log.d("TAG", "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void printPositions() {
        Log.d("TAG", "printPositions(): " +
                "getLeft() = " + getLeft()
                + " | getRight() = " + getRight()
                + " | getTop() = " + getTop()
                + " | getBottom() = " + getBottom()
                + " | getHeight() = " + getHeight()
                + " | getWidth() = " + getWidth()
                + " | getMeasuredHeight() = " + getMeasuredHeight()
                + " | getMeasuredWidth() = " + getMeasuredWidth());
    }
}
