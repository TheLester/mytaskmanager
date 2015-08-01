package com.tuesda.walker.circlerefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhanglei on 15/7/18.
 */
public class AnimationView extends View {

    private static final String TAG = "AnimationView";

    private int PULL_HEIGHT;
    private int PULL_DELTA;
    private float mWidthOffset;



    private AnimatorStatus mAniStatus = AnimatorStatus.PULL_DOWN;

    enum AnimatorStatus {
        PULL_DOWN,
        DRAG_DOWN,
        REL_DRAG,
        SPRING_UP, // rebound to up, the position is less than PULL_HEIGHT
        POP_BALL,
        OUTER_CIR,
        REFRESHING,
        DONE,
        STOP;

        @Override
        public String toString() {
            switch (this) {
                case PULL_DOWN:
                    return "pull down";
                case DRAG_DOWN:
                    return "drag down";
                case REL_DRAG:
                    return "release drag";
                case SPRING_UP:
                    return "spring up";
                case POP_BALL:
                    return "pop ball";
                case OUTER_CIR:
                    return "outer circle";
                case REFRESHING:
                    return "refreshing...";
                case DONE:
                    return "done!";
                case STOP:
                    return "stop";
                default:
                    return "unknown state";
            }
        }
    }


    private Paint mBackPaint;
    private Paint mBallPaint;
    private Paint mOutPaint;
    private Path mPath;


    public AnimationView(Context context) {
        this(context, null, 0);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

        PULL_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
        PULL_DELTA = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        mWidthOffset = 0.5f;
        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setColor(0xff8b90af);

        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mBallPaint.setColor(0xffffffff);
        mBallPaint.setStyle(Paint.Style.FILL);

        mOutPaint = new Paint();
        mOutPaint.setAntiAlias(true);
        mOutPaint.setColor(0xffffffff);
        mOutPaint.setStyle(Paint.Style.STROKE);
        mOutPaint.setStrokeWidth(5);


        mPath = new Path();

    }

    private int mRadius;
    private int mWidth;
    private int mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (height > PULL_DELTA + PULL_HEIGHT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(PULL_DELTA + PULL_HEIGHT, MeasureSpec.getMode(heightMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mRadius = getHeight() / 6;
            mWidth = getWidth();
            mHeight = getHeight();

            if (mHeight < PULL_HEIGHT) {
                mAniStatus = AnimatorStatus.PULL_DOWN;
            }


            switch (mAniStatus) {
                case PULL_DOWN:
                    if (mHeight >= PULL_HEIGHT) {
                        mAniStatus = AnimatorStatus.DRAG_DOWN;
                    }
                    break;
                case REL_DRAG:
                    break;
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mAniStatus) {
            case PULL_DOWN:
                canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
                break;
            case REL_DRAG:
            case DRAG_DOWN:
                drawDrag(canvas);
                break;
            case SPRING_UP:
                drawSpring(canvas, getSpringDelta());
                invalidate();
                break;
            case POP_BALL:
                drawPopBall(canvas);
                invalidate();
                break;
            case OUTER_CIR:
                drawOutCir(canvas);
                invalidate();
                break;
            case REFRESHING:
                drawRefreshing(canvas);
                invalidate();
                break;
            case DONE:
                drawDone(canvas);
                invalidate();
                break;
            case STOP:
                drawDone(canvas);
                break;

        }

        if (mAniStatus == AnimatorStatus.REL_DRAG) {
            ViewGroup.LayoutParams params = getLayoutParams();
            int height;
            // NOTICE: If the height equals mLastHeight, then the requestLayout() will not work correctly
            do {
                height = getRelHeight();
            } while (height == mLastHeight && getRelRatio() != 1);
            mLastHeight = height;
            params.height = PULL_HEIGHT + height;
            requestLayout();
        }


    }

    private void drawDrag(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, PULL_HEIGHT, mBackPaint);

        mPath.reset();
        mPath.moveTo(0, PULL_HEIGHT);
        mPath.quadTo(mWidthOffset * mWidth, PULL_HEIGHT + (mHeight - PULL_HEIGHT) * 2,
                mWidth, PULL_HEIGHT);
        canvas.drawPath(mPath, mBackPaint);
    }

    private void drawSpring(Canvas canvas, int springDelta) {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, PULL_HEIGHT);
        mPath.quadTo(mWidth / 2, PULL_HEIGHT - springDelta,
                mWidth, PULL_HEIGHT);
        mPath.lineTo(mWidth, 0);
        canvas.drawPath(mPath, mBackPaint);

        int curH = PULL_HEIGHT - springDelta / 2;

        if (curH > PULL_HEIGHT - PULL_DELTA / 2) {
            int leftX = (int) (mWidth / 2 - 2 * mRadius + getSprRatio() * mRadius);
            mPath.reset();
            mPath.moveTo(leftX, curH);
            mPath.quadTo(mWidth / 2, curH - mRadius * getSprRatio() * 2,
                    mWidth - leftX, curH);
            canvas.drawPath(mPath, mBallPaint);
        } else {
            canvas.drawArc(new RectF(mWidth / 2 - mRadius, curH - mRadius, mWidth / 2 + mRadius, curH + mRadius),
                    180, 180, true, mBallPaint);
        }

    }

    private void drawPopBall(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, PULL_HEIGHT);
        mPath.quadTo(mWidth / 2, PULL_HEIGHT - PULL_DELTA,
                mWidth, PULL_HEIGHT);
        mPath.lineTo(mWidth, 0);
        canvas.drawPath(mPath, mBackPaint);

        int cirCentStart = PULL_HEIGHT - PULL_DELTA / 2;
        int cirCenY = (int) (cirCentStart - mRadius * 2 * getPopRatio());

        canvas.drawArc(new RectF(mWidth / 2 - mRadius, cirCenY - mRadius, mWidth / 2 + mRadius, cirCenY + mRadius),
                180, 360, true, mBallPaint);

        if (getPopRatio() < 1) {
            drawTail(canvas, cirCenY, cirCentStart + 1, getPopRatio());
        } else {
            canvas.drawCircle(mWidth / 2, cirCenY, mRadius, mBallPaint);
        }


    }

    private void drawTail(Canvas canvas, int centerY, int bottom, float fraction) {
        int bezier1w = (int) (mWidth / 2 + (mRadius * 3 / 4) * (1 - fraction));
        PointF start = new PointF(mWidth / 2 + mRadius, centerY);
        PointF bezier1 = new PointF(bezier1w, bottom);
        PointF bezier2 = new PointF(bezier1w + mRadius / 2, bottom);

        mPath.reset();
        mPath.moveTo(start.x, start.y);
        mPath.quadTo(bezier1.x, bezier1.y,
                bezier2.x, bezier2.y);
        mPath.lineTo(mWidth - bezier2.x, bezier2.y);
        mPath.quadTo(mWidth - bezier1.x, bezier1.y,
                mWidth - start.x, start.y);
        canvas.drawPath(mPath, mBallPaint);
    }

    private void drawOutCir(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, PULL_HEIGHT);
        mPath.quadTo(mWidth / 2, PULL_HEIGHT - (1 - getOutRatio()) * PULL_DELTA,
                mWidth, PULL_HEIGHT);
        mPath.lineTo(mWidth, 0);
        canvas.drawPath(mPath, mBackPaint);
        int innerY = PULL_HEIGHT - PULL_DELTA / 2 - mRadius * 2;
        canvas.drawCircle(mWidth / 2, innerY, mRadius, mBallPaint);
    }

    private int mRefreshStart = 90;
    private int mRefreshStop = 90;
    private int TARGET_DEGREE = 270;
    private boolean mIsStart = true;
    private boolean mIsRefreshing = true;

    private void drawRefreshing(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
        int innerY = PULL_HEIGHT - PULL_DELTA / 2 - mRadius * 2;
        canvas.drawCircle(mWidth / 2, innerY, mRadius, mBallPaint);
        int outerR = mRadius + 10;

        mRefreshStart += mIsStart ? 3 : 10;
        mRefreshStop += mIsStart ? 10 : 3;
        mRefreshStart = mRefreshStart % 360;
        mRefreshStop = mRefreshStop % 360;

        int swipe = mRefreshStop - mRefreshStart;
        swipe = swipe < 0 ? swipe + 360 : swipe;

        canvas.drawArc(new RectF(mWidth / 2 - outerR, innerY - outerR, mWidth / 2 + outerR, innerY + outerR),
                mRefreshStart, swipe, false, mOutPaint);
        if (swipe >= TARGET_DEGREE) {
            mIsStart = false;
        } else if (swipe <= 10) {
            mIsStart = true;
        }
        if (!mIsRefreshing) {
            applyDone();

        }

    }

    // stop refreshing
    public void setRefreshing(boolean isFresh) {
        mIsRefreshing = isFresh;
    }

    private void drawDone(Canvas canvas) {


        int beforeColor = mOutPaint.getColor();
        if (getDoneRatio() < 0.3) {
            canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
            int innerY = PULL_HEIGHT - PULL_DELTA / 2 - mRadius * 2;
            canvas.drawCircle(mWidth / 2, innerY, mRadius, mBallPaint);
            int outerR = (int) (mRadius + 10 + 10 * getDoneRatio() / 0.3f);
            int afterColor = Color.argb((int) (0xff * (1 - getDoneRatio() / 0.3f)), Color.red(beforeColor),
                    Color.green(beforeColor), Color.blue(beforeColor));
            mOutPaint.setColor(afterColor);
            canvas.drawArc(new RectF(mWidth / 2 - outerR, innerY - outerR, mWidth / 2 + outerR, innerY + outerR),
                    0, 360, false, mOutPaint);
        }
        mOutPaint.setColor(beforeColor);


        if (getDoneRatio() >= 0.3 && getDoneRatio() < 0.7) {
            canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
            float fraction = (getDoneRatio() - 0.3f) / 0.4f;
            int startCentY = PULL_HEIGHT - PULL_DELTA / 2 - mRadius * 2;
            int curCentY = (int) (startCentY + (PULL_DELTA / 2 + mRadius * 2) * fraction);
            canvas.drawCircle(mWidth / 2, curCentY, mRadius, mBallPaint);
            if (curCentY >= PULL_HEIGHT - mRadius * 2) {
                drawTail(canvas, curCentY, PULL_HEIGHT, (1 - fraction));
            }
        }

        if (getDoneRatio() >= 0.7 && getDoneRatio() <= 1) {
            float fraction = (getDoneRatio() - 0.7f) / 0.3f;
            canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
            int leftX = (int) (mWidth / 2 - mRadius - 2 * mRadius * fraction);
            mPath.reset();
            mPath.moveTo(leftX, PULL_HEIGHT);
            mPath.quadTo(mWidth / 2, PULL_HEIGHT - (mRadius * (1 - fraction)),
                    mWidth - leftX, PULL_HEIGHT);
            canvas.drawPath(mPath, mBallPaint);
        }

    }

    private int mLastHeight;

    private int getRelHeight() {
        return (int) (mSpriDeta * (1 - getRelRatio()));
    }

    private int getSpringDelta() {
        return (int) (PULL_DELTA * getSprRatio());
    }


    private static long REL_DRAG_DUR = 200;

    private long mStart;
    private long mStop;
    private int mSpriDeta;

    public void releaseDrag() {
        mStart = System.currentTimeMillis();
        mStop = mStart + REL_DRAG_DUR;
        mAniStatus = AnimatorStatus.REL_DRAG;
        mSpriDeta = mHeight - PULL_HEIGHT;
        requestLayout();
    }

    private float getRelRatio() {
        if (System.currentTimeMillis() >= mStop) {
            springUp();
            return 1;
        }
        float ratio = (System.currentTimeMillis() - mStart) / (float) REL_DRAG_DUR;
        return Math.min(ratio, 1);
    }

    private static long SPRING_DUR = 200;
    private long mSprStart;
    private long mSprStop;


    private void springUp() {
        mSprStart = System.currentTimeMillis();
        mSprStop = mSprStart + SPRING_DUR;
        mAniStatus = AnimatorStatus.SPRING_UP;
        invalidate();
    }


    private float getSprRatio() {
        if (System.currentTimeMillis() >= mSprStop) {
            popBall();
            return 1;
        }
        float ratio = (System.currentTimeMillis() - mSprStart) / (float) SPRING_DUR;
        return Math.min(1, ratio);
    }

    private static final long POP_BALL_DUR = 300;
    private long mPopStart;
    private long mPopStop;

    private void popBall() {
        mPopStart = System.currentTimeMillis();
        mPopStop = mPopStart + POP_BALL_DUR;
        mAniStatus = AnimatorStatus.POP_BALL;
        invalidate();
    }

    private float getPopRatio() {
        if (System.currentTimeMillis() >= mPopStop) {
            startOutCir();
            return 1;
        }

        float ratio = (System.currentTimeMillis() - mPopStart) / (float) POP_BALL_DUR;
        return Math.min(ratio, 1);
    }

    private static final long OUTER_DUR = 200;
    private long mOutStart;
    private long mOutStop;

    private void startOutCir() {
        mOutStart = System.currentTimeMillis();
        mOutStop = mOutStart + OUTER_DUR;
        mAniStatus = AnimatorStatus.OUTER_CIR;
        mRefreshStart = 90;
        mRefreshStop = 90;
        TARGET_DEGREE = 270;
        mIsStart = true;
        mIsRefreshing = true;
        invalidate();
    }

    private float getOutRatio() {
        if (System.currentTimeMillis() >= mOutStop) {
            mAniStatus = AnimatorStatus.REFRESHING;
            mIsRefreshing = true;
            return 1;
        }
        float ratio = (System.currentTimeMillis() - mOutStart) / (float) OUTER_DUR;
        return Math.min(ratio, 1);
    }

    private static final long DONE_DUR = 1000;
    private long mDoneStart;
    private long mDoneStop;

    private void applyDone() {
        mDoneStart = System.currentTimeMillis();
        mDoneStop = mDoneStart + DONE_DUR;
        mAniStatus = AnimatorStatus.DONE;
    }

    private float getDoneRatio() {
        if (System.currentTimeMillis() >= mDoneStop) {
            mAniStatus = AnimatorStatus.STOP;
            if (onViewAniDone != null) {
                onViewAniDone.viewAniDone();
            }
            return 1;
        }

        float ratio = (System.currentTimeMillis() - mDoneStart) / (float) DONE_DUR;
        return Math.min(ratio, 1);
    }


    private OnViewAniDone onViewAniDone;

    public void setOnViewAniDone(OnViewAniDone onViewAniDone) {
        this.onViewAniDone = onViewAniDone;
    }

    interface OnViewAniDone {
        void viewAniDone();
    }


    public void setAniBackColor(int color) {
        mBackPaint.setColor(color);
    }

    public void setAniForeColor(int color) {
        mBallPaint.setColor(color);
        mOutPaint.setColor(color);
        setBackgroundColor(color);
    }

    // the height of view is smallTimes times of circle radius
    public void setRadius(int smallTimes) {
        mRadius = mHeight / smallTimes;
    }


}
