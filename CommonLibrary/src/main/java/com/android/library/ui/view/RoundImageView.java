package com.android.library.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.library.BaseApplication;
import com.android.library.R;
import com.android.library.net.utils.LogUtil;


/**
 * 如果需要整成圆形的，设置roundWidth 和 roundHeight为0
 */
public class RoundImageView extends ImageView {

    public static final int ALL = 0;
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 4;
    /**
     * 圆角半径
     */
    private int roundWidth = 0;
    /**
     * 圆角类型
     */
    private int roundType = -1;
    private boolean isRound = false;
    private Drawable curDrawable = null; // 当前的drawable
    private BitmapDrawable roundDrawble = null; // 圆角处理后的drawable

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundImageView(Context context, boolean isRound) {
        super(context);
        this.isRound = isRound;
        init(context, null);
    }

    public RoundImageView(Context context) {
        this(context, false);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
            roundWidth = a.getDimensionPixelSize(R.styleable.RoundImageView_roundWidth, roundWidth);
            roundType = a.getInteger(R.styleable.RoundImageView_roundType, roundType);
            if (0 == roundWidth) {
                isRound = true;
            }
            a.recycle();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(toRoundCorner(bm));
        bm = null;
    }

    @Override
    public void setImageResource(int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId);
        setImageDrawable(drawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(toRoundBitmapDrawable(drawable));
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(toRoundBitmapDrawable(background));
    }

    private BitmapDrawable toRoundBitmapDrawable(Drawable inDrawable) {
        if (curDrawable == inDrawable) {
            // 传入的drawable 和当前的drawable一样，直接返回当前的round drawable
            LogUtil.i("toRoundBitmapDrawable", "toRoundBitmapDrawable same!!!");
            return roundDrawble;
        }
        curDrawable = inDrawable;
        BitmapDrawable bd = (BitmapDrawable) inDrawable;
        Bitmap bm = bd.getBitmap();
        Bitmap newBitmap = toRoundCorner(bm);
        roundDrawble = new BitmapDrawable(BaseApplication.curContext.getResources(), newBitmap);
        return roundDrawble;
    }

    /**
     * 将图片圆角显示
     *
     * @param input
     * @return
     */

    public Bitmap toRoundCorner(Bitmap input) {
        if (null == input) {
            return null;
        }

        if (roundType != -1) {
            return clip(roundType, input, roundWidth);
        }

        int bitmapW = input.getWidth();
        int bitmapH = input.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapW, bitmapH, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // final int color = 0xff424242;
        final int color = 0xffffffff;
        final Rect rect = new Rect(0, 0, bitmapW, bitmapH);
        final RectF rectF = new RectF(rect);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        canvas.drawARGB(0, 0, 0, 0);
        if (isRound) {
            roundWidth = bitmapH / 2;
        }

        canvas.drawRoundRect(rectF, roundWidth, roundWidth, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(input, rect, rect, paint);
        return bitmap;
    }

    /**
     * 指定图片的切边，对图片进行圆角处理
     *
     * @param type    具体参见：{@link #ALL} , {@link #TOP} ,
     *                {@link #LEFT} , {@link #RIGHT} ,
     *                {@link #BOTTOM}
     * @param bitmap  需要被切圆角的图片
     * @param roundPx 要切的像素大小
     * @return
     */
    private Bitmap clip(int type, Bitmap bitmap, int roundPx) {
        try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。
            // 最后把源图片帖上。
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();

            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(paintingBoard);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            if (TOP == type) {
                clipTop(canvas, paint, roundPx, width, height);
            } else if (LEFT == type) {
                clipLeft(canvas, paint, roundPx, width, height);
            } else if (RIGHT == type) {
                clipRight(canvas, paint, roundPx, width, height);
            } else if (BOTTOM == type) {
                clipBottom(canvas, paint, roundPx, width, height);
            } else {
                clipAll(canvas, paint, roundPx, width, height);
            }

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            // 帖子图
            final Rect src = new Rect(0, 0, width, height);
            final Rect dst = src;
            canvas.drawBitmap(bitmap, src, dst, paint);
            return paintingBoard;
        } catch (Exception exp) {
            exp.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 左面两个角
     *
     * @param canvas
     * @param paint
     * @param offset
     * @param width
     * @param height
     */
    private void clipLeft(Canvas canvas, Paint paint, int offset, int width, int height) {
        Rect block = new Rect(offset, 0, width, height);
        canvas.drawRect(block, paint);
        RectF rectF = new RectF(0, 0, offset * 2, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    /**
     * 右面两个角
     *
     * @param canvas
     * @param paint
     * @param offset
     * @param width
     * @param height
     */
    private void clipRight(Canvas canvas, Paint paint, int offset, int width, int height) {
        Rect block = new Rect(0, 0, width - offset, height);
        canvas.drawRect(block, paint);
        RectF rectF = new RectF(width - offset * 2, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    /**
     * 上面两个角
     *
     * @param canvas
     * @param paint
     * @param offset
     * @param width
     * @param height
     */
    private void clipTop(Canvas canvas, Paint paint, int offset, int width, int height) {
        Rect block = new Rect(0, offset, width, height);
        canvas.drawRect(block, paint);
        RectF rectF = new RectF(0, 0, width, offset * 2);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    /**
     * 下面两个角
     *
     * @param canvas
     * @param paint
     * @param offset
     * @param width
     * @param height
     */
    private void clipBottom(final Canvas canvas, Paint paint, int offset, int width, int height) {
        Rect block = new Rect(0, 0, width, height - offset);
        canvas.drawRect(block, paint);
        RectF rectF = new RectF(0, height - offset * 2, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    /**
     * 四角都是圆角
     *
     * @param canvas
     * @param paint
     * @param offset
     * @param width
     * @param height
     */
    private void clipAll(Canvas canvas, Paint paint, int offset, int width, int height) {
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }
}
