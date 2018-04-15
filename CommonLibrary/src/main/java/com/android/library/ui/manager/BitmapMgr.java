package com.android.library.ui.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.android.library.net.http.IInputStreamParser;
import com.android.library.net.utils.HttpUtil;
import com.android.library.ui.manager.memory.MemoryCache;
import com.android.library.ui.utils.PriorityRunnable;
import com.android.library.utils.ClientInfo;
import com.android.library.utils.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能：定义统一的图片缓存入口，管理所有的图片缓存
 */
public class BitmapMgr {
    public static Animation imgAnimation;
    // 内存Cache,用于 URL和Cache的对应Map

    private static MemoryCache bitmapCache = null;
    // 用于存放ImageView和URL对应的Map
    private static Map<View, String> views = new WeakHashMap<View, String>();
    // 下载中的url, 如果cache中有, 不需要重复下载
    private static HashSet<String> downloadingCache = new HashSet<String>();
    // Resources
    private static Resources resources;
    private static AtomicInteger atomicInteger = new AtomicInteger();

    private static Options decodeOptions;

    static {
        bitmapCache = CacheUtils.createMemoryCache(0);
    }

    public static void init(Resources res) {
        resources = res;
        decodeOptions = new Options();
        decodeOptions.inPreferredConfig = Config.RGB_565;
    }

    /**
     * 加载图片 无默认图片
     *
     * @param iv
     * @param url
     */
    public static void loadBitmap(Context context, ImageView iv, String url) {
        loadBitmap(context, iv, url, 0);
    }

    public static void loadBitmap(Context context, ImageView iv, String url, int defID) {
        loadBitmap(context, iv, url, defID, false, false);
    }

    public static void loadBitmap4BG(Context context, ImageView iv, String url) {
        loadBitmap(context, iv, url, 0);
    }

    public static void loadBitmap4BG(Context context, ImageView iv, String url, int defID) {
        loadBitmap(context, iv, url, defID, true, false);
    }

    public static void loadBigBitmap(Context context, ImageView iv, String url) {
        loadBigBitmap(context, iv, url, 0);
    }

    public static void loadBigBitmap(Context context, ImageView iv, String url, int defID) {
        loadBitmap(context, iv, url, defID, false, true);
    }

    /**
     * 加载View 背景
     *
     * @param iv
     * @param url
     */
    public static void loadViewBitmap(Context context, View iv, String url) {
        loadBitmap(context, iv, url, 0, false, false);
    }

    /**
     * @param iv
     * @return [0] width [1] height
     */
    private static int[] getSize(Context context, View iv) {
        int width = iv.getWidth() != 0 ? iv.getWidth() : (iv.getMeasuredWidth() != 0 ? iv.getMeasuredWidth() : (iv.getLayoutParams().width != 0 ? iv.getLayoutParams().width : (ClientInfo.getInstance(context).width)));
        int height = iv.getHeight() != 0 ? iv.getHeight() : (iv.getMeasuredHeight() != 0 ? iv.getMeasuredHeight() : (iv.getLayoutParams().height != 0 ? iv.getLayoutParams().height : (ClientInfo.getInstance(context).height)));
        return new int[]{width, height};
    }

    /**
     * 加载图片,可设置默认图片
     *
     * @param iv
     * @param url
     * @param defID
     * @param isBG
     */
    public static void loadBitmap(Context context, final View iv, final String url, final int defID, final boolean isBG, boolean isBigPic) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(url)) {
            setImage(iv, bitmap, defID, isBG);
            return;
        }
        views.put(iv, url);

        ImageCallback cb = new ImageCallback() {
            @Override
            public void onLoad(Bitmap bitmap, String url) {
                if (bitmap == null)
                    return;

                String oldurl = views.get(iv);
                if (!TextUtils.isEmpty(oldurl) && oldurl.equals(url)) {
                    setImage(iv, bitmap, defID, isBG);
                }
            }
        };
        int[] size = getSize(context, iv);
        // 异步加载图片
        bitmap = load(context, url, cb, isBigPic, size[0], size[1]);
        // 设置图片
        setImage(iv, bitmap, defID, isBG);
    }

    // 加载的具体操作放这里
    private static Bitmap load(final Context context, final String url, final ImageCallback callback, final boolean isBigPic, final int width, final int height) {
        if (url == null) {
            return null;
        }
        // 从缓存中取
        Bitmap bitmap = bitmapCache.get(url);
        if (bitmap != null) {
            return bitmap;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                callback.onLoad((Bitmap) message.obj, url);
            }
        };

        SmallImageThreadPool.getInstance().execute(new PriorityRunnable() {
            @Override
            public void run() {
                // 从文件缓存读取
                Bitmap bitmap = getBitmapFromFile(context, url, width, height);
                if (bitmap == null) {
                    ImageThreadExecutor executor = null;
                    if (isBigPic) {
                        executor = BigImageThreadExecutor.getInstance();
                    } else {
                        executor = SmallImageThreadPool.getInstance();
                    }
                    // 文件缓存没有，从网络获取
                    executor.execute(new PriorityRunnable() {
                        @Override
                        public void run() {
                            // 判定是否下载
                            if (!views.containsValue(url)) {
                                return;
                            }
                            Bitmap bitmapNet = getBitmapFromNet(context, url, width, height);
                            Message msg = Message.obtain();
                            msg.obj = bitmapNet;
                            handler.sendMessage(msg);
                        }
                    });
                } else {
                    Message msg = Message.obtain();
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }
        });
        return bitmap;
    }

    /**
     * 保存缓存
     *
     * @param url
     * @param bitmap
     */
    private static void putCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            bitmapCache.put(url, bitmap);
        }
    }

    /**
     * 设置Imageview的显示
     *
     * @param iv
     * @param bitmap
     * @param defID
     */
    private static void setImage(View iv, Bitmap bitmap, int defID, boolean isBG) {
        // 如果是空，显示默认图片,设置背景色
        if (iv instanceof ImageView) {
            ImageView imageView = (ImageView) iv;
            if (bitmap == null && defID != 0) {
                if (isBG) {
                    imageView.setBackgroundResource(defID);
                } else {
                    imageView.setImageResource(defID);
                }
            } else if (bitmap != null) {
                if (isBG) {
                    imageView.setBackgroundDrawable(new BitmapDrawable(resources, bitmap));
                } else {
                    imageView.setImageDrawable(new BitmapDrawable(resources, bitmap));
                }
            }
        } else {
            if (bitmap == null && defID != 0) {
                iv.setBackgroundResource(defID);
            } else if (bitmap != null) {
                iv.setBackgroundDrawable(new BitmapDrawable(resources, bitmap));
            }
        }
    }

    /**
     * 从文件获取Bitmap
     *
     * @param url
     * @return
     */
    private static Bitmap getBitmapFromFile(Context context, String url, int width, int height) {
        String imagePath = url;
        if (url.startsWith("http")) {
            imagePath = FileUtil.getImageCacheFileName(context, url);
        }
        Bitmap bitmap = getBitmapFromCachePath(imagePath, width, height);
        putCache(url, bitmap);
        return bitmap;
    }

    /**
     * 加载图片到缓存中
     *
     * @param url
     * @param isSmallIv
     */
    public static boolean loadBitmapToCache(Context context, String url, boolean isSmallIv) {
        if (null == url) {
            return false;
        }

        Bitmap bitmap = bitmapCache.get(url);
        if (bitmap == null) {
            if (!url.startsWith("http")) {
                bitmap = getBitmapFromAssert(url);
            } else {
                if ((bitmap = getBitmapFromFile(context, url, 0, 0)) == null) {
                    bitmap = getBitmapFromNet(context, url, 0, 0);
                }
            }
        }
        return bitmap != null;
    }

    /**
     * 加载Assert下的图片
     *
     * @param fileName
     * @return
     */
    public static Bitmap getBitmapFromAssert(String fileName) {
        InputStream input = null;
        try {
            input = resources.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, decodeOptions);
            putCache(fileName, bitmap);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * 从网络获取图片,并且保存到文件,做文件缓存
     *
     * @param url
     * @return
     */
    private static Bitmap getBitmapFromNet(final Context context, final String url, final int width, final int height) {
        final String imagePath;
        if (downloadingCache.contains(url)) {
            imagePath = FileUtil.getImageCacheFileName(context, url) + atomicInteger.incrementAndGet();
        } else {
            imagePath = FileUtil.getImageCacheFileName(context, url);
            downloadingCache.add(url);
        }
        Bitmap bitmap = getBitmapFromFile(context, url, width, height);
        if (bitmap == null) {
            bitmap = HttpUtil.loadGetRequest(url, new IInputStreamParser<Bitmap>() {
                @Override
                public Bitmap parser(InputStream inputStream) {
                    try {
                        FileUtil.savePng(context, inputStream, url);
                        return decodeBitmap(imagePath, width, height);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        downloadingCache.remove(url);
                    }
                    return null;
                }
            });
            putCache(url, bitmap);
            downloadingCache.remove(url); // add by huzhiyu ：if httputils.get
            // 抛出异常，那么加载不成功的图片，永远无法加载了
        }
        return bitmap;
    }

    /**
     * 从文件中解码Bitmap
     *
     * @param imagePath
     * @return
     */
    private static Bitmap getBitmapFromCachePath(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        File file = new File(imagePath);
        if (file.exists() && file.isFile()) {
            try {
                bitmap = decodeBitmap(imagePath, width, height);
                if (bitmap == null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 同步加载本地以存的图片,防止出现默认图片
     *
     * @param iv
     * @param url
     * @param defID
     */
    public static void loadBitmapSync(Context context, final ImageView iv, final String url, final int defID) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Bitmap bitmap = loadSync(context, iv, url);
        if (bitmap != null) {
            setImage(iv, bitmap, defID, false);
        }
    }

    /**
     * 同步从磁盘加载图片文件
     *
     * @param iv
     * @param url
     * @return
     */
    private static Bitmap loadSync(final Context context, final ImageView iv, final String url) {
        if (url == null) {
            return null;
        }
        views.put(iv, url);

        final ImageCallback callback = new ImageCallback() {
            @Override
            public void onLoad(Bitmap bitmap, String url) {
                if (bitmap == null)
                    return;
                String oldurl = views.get(iv);
                if (!TextUtils.isEmpty(oldurl) && oldurl.equals(url)) {
                    setImage(iv, bitmap, 0, false);
                }
            }
        };
        // 从缓存中取
        Bitmap bitmap = bitmapCache.get(url);
        if (bitmap != null) {
            return bitmap;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                callback.onLoad((Bitmap) message.obj, url);
            }
        };
        if (!url.startsWith("http")) {
            bitmap = getBitmapFromAssert(url);
        } else {
            // 从文件缓存读取
            bitmap = getBitmapFromFile(context, url, 0, 0);

            if (bitmap == null) {
                SmallImageThreadPool.getInstance().execute(new PriorityRunnable() {
                    @Override
                    public void run() {
                        // 文件缓存没有，从网络获取
                        if (downloadingCache.contains(url)) {
                            downloadingCache.remove(url);
                        }
                        Bitmap bitmap = getBitmapFromNet(context, url, 0, 0);
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                });
            }
        }
        return bitmap;
    }

    /**
     * @param bitmap 原图
     * @param width  希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap squareBitmap(Bitmap bitmap, int width) {
        if (null == bitmap || width <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > width && heightOrg > width) {
            // 压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (width * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : width;
            int scaledHeight = widthOrg > heightOrg ? width : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            // 从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - width) / 2;
            int yTopLeft = (scaledHeight - width) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, width, width);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    private static Bitmap decodeBitmap(String imagePath, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 计算压缩比
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateSize(Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (reqHeight == 0 || reqWidth == 0) {
            return inSampleSize;
        }
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = (int) Math.floor((float) height / (float) reqHeight + 0.9);
            } else {
                inSampleSize = (int) Math.floor((float) width / (float) reqWidth + 0.9);
            }
        }
        return inSampleSize;
    }

    public static void removeFromCache(String url){
        bitmapCache.remove(url);
        downloadingCache.remove(url);
    }
    /**
     * 定义图片加载回调接口
     */
    private interface ImageCallback {
        public void onLoad(Bitmap bitmap, String url);
    }
}
