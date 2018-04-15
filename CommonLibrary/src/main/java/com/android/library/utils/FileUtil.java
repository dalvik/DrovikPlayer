package com.android.library.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.library.net.http.InputStreamParser;
import com.android.library.net.utils.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class FileUtil {
    /**
     * SD卡根目录
     */
    public final static String CFG_PATH_SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * SNMarket 的 SD卡根目录
     */
    public final static String CFG_PATH_SDCARD_DIR = CFG_PATH_SDCARD_ROOT + File.separator + "wqk";
    /**
     * SD卡上image目录
     */
    public final static String CFG_PATH_SDCARD_IMAGE = CFG_PATH_SDCARD_DIR + File.separator + "image";
    /**
     * SD卡上download目录
     */
    public final static String CFG_PATH_SDCARD_DOWNLOAD = CFG_PATH_SDCARD_DIR + File.separator + "download";
    /**
     * SD卡上Statistics目录
     */
    public final static String CFG_PATH_SDCARD_SNMARKET_STC = CFG_PATH_SDCARD_DIR + File.separator + "statistics";

    private static String FIXED_PATH = CFG_PATH_SDCARD_ROOT + File.separator + "Android/data/";
    private static boolean hasClearedAPK = false;

    /**
     * 获取图片缓存目录
     *
     * @return
     */
    public static String getImageCachePath(Context context) {
        String cachePath;
        if (IsCanUseSdCard()) {
            File file = new File(CFG_PATH_SDCARD_IMAGE);
            if (!file.exists()) {
                file.mkdirs();
            }
            cachePath = file.getPath();
        } else {
            cachePath = context.getCacheDir().getPath() + File.separator + "image";
        }
        return cachePath;
    }

    /**
     * 获取数据缓存目录
     */
    public static String getDataCatchPath(Context context) {
        String dataPath;
        if (IsCanUseSdCard()) {
            File file = new File(CFG_PATH_SDCARD_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            dataPath = file.getPath();
        } else {
            dataPath = context.getCacheDir().getPath() + File.separator + "download";
        }
        return dataPath;
    }

    /**
     * 通过Url获取对于缓存文件名，一个url唯一对应一个缓存文件名
     */
    public static String getImageCacheFileName(Context context, String imageUrl) {
        String imagePath;
        String fileName = "";
        if (imageUrl != null && imageUrl.length() != 0) {
            fileName = MD5.toMD5String(imageUrl);
        }
        imagePath = getImageCachePath(context);
        imagePath += File.separator + fileName;
        return imagePath;
    }

    /**
     * 通过文件名，获取一个下载保存路径
     */
    public static String getDownloadCacheFileName(Context context, String name) {
        String dataPath;
        dataPath = getDataCatchPath(context);
        dataPath += File.separator + name;
        return dataPath;
    }

    /**
     * 将文件保存到Data目录
     *
     * @param inStream
     * @param fileName
     * @return
     */
    public static boolean saveToData(Context context, InputStream inStream, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 将obj 存储到data目录
     *
     * @param obj
     * @param fileName
     */
    public static void saveObjToData(Context context, Object obj, String fileName) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fis = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fis);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 取Object
     *
     * @param fileName
     * @return
     */
    public static Object getObjectFromData(Context context, String fileName) {
        Object obj = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(context.openFileInput(fileName));
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * data 目录下去输入流 <BR/>
     * 调用者需要关闭流
     *
     * @param fileName
     * @return
     */
    public static FileInputStream getFromData(Context context, String fileName) {
        FileInputStream is = null;
        try {
            is = context.openFileInput(fileName);
        } catch (IOException e) {
        }
        return is;
    }

    /**
     * 将Data目录下的图片取出
     *
     * @param fileName
     * @return
     */
    public static Bitmap getBitmapFromData(Context context, String fileName) {
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            fis = getFromData(context, fileName);
            bitmap = BitmapFactory.decodeStream(fis);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 判断sd卡是否有用
     */
    public static boolean IsCanUseSdCard() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取下载是临时文件
     *
     * @param pkgName
     * @return
     */
    public static String getDownloadTmpFilePath(String pkgName) {
        return CFG_PATH_SDCARD_DOWNLOAD + File.separator + pkgName + Constants.ANDROID_TEMPAPP_SUFFIX;
    }

    /**
     * 获取本地已下载的游戏APK文件
     *
     * @param gameCode
     * @return
     */
    public static String getGameAPKFilePath(String gameCode) {
        return CFG_PATH_SDCARD_DOWNLOAD + File.separator + gameCode + Constants.ANDROID_APP_SUFFIX;
    }

    /**
     * 获取SD卡有效空间
     *
     * @return
     */
    public static double getSDAvailaleSize() {
        StatFs stat = new StatFs(CFG_PATH_SDCARD_ROOT);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;// 额外预留1M
    }

    public static void savePng(Context context, InputStream input, String imageUrl) {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            int len = -1;
            byte[] b = new byte[1024];
            while ((len = input.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            savePng(context, bos, imageUrl);
        } catch (Exception e) {
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 把PNG数据流保存到图片缓存路径
     *
     * @param pngOS
     * @param imageUrl
     */
    public static void savePng(Context context, ByteArrayOutputStream pngOS, String imageUrl) {
        if (null == pngOS) {
            return;
        }
        String fileName = FileUtil.getImageCacheFileName(context, imageUrl);
        OutputStream outStream = null;
        File pngFile = new File(fileName);
        if (pngFile.exists()) {
            pngFile.delete();
        }

        try {
            outStream = new FileOutputStream(fileName);
            byte[] bytes = pngOS.toByteArray();
            outStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Bitmap保存为PNG
    public static boolean Bitmap2PNG(Bitmap bmp, String filepath) {
        if (bmp == null || filepath == null)
            return false;
        OutputStream stream = null;
        try {
            File file = new File(filepath);
            File dir = new File(file.getParent());
            if (!dir.exists())
                dir.mkdirs();
            if (file.exists())
                file.delete();

            stream = new FileOutputStream(filepath);
            if (bmp.compress(Bitmap.CompressFormat.PNG, 85, stream)) {
                stream.flush();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    // 图片文件转Bitmap
    public static Bitmap PNGToBitmap(String path) {
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.w("FileUtils", path + " is not exits");
            return null;
        }
        Bitmap bm = null;
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inDither = false;
        bfoOptions.inPurgeable = true;
        bfoOptions.inInputShareable = true;
        bfoOptions.inTempStorage = new byte[32 * 1024];

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.w("FileUtils", path + " is not exits");
        }

        try {
            if (fs != null)
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfoOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        return bm;
    }

    /**
     * 清理已下载的APK 清理规则：下载 > 10天的APK，删除，每次运行只清理一次
     */
    public static void clearDownloadAPKFile() {
        if (hasClearedAPK) {
            return;
        } else {
            // 执行清除
            hasClearedAPK = true;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                File loadDir = new File(CFG_PATH_SDCARD_DOWNLOAD);
                File[] files = loadDir.listFiles();
                if (files == null) {
                    return;
                }
                int length = files.length;
                long lastModified = 0;
                long currentTime = System.currentTimeMillis();
                long DOWNLOAD_MAX_TIME = 10 * 24 * 60 * 60 * 1000;

                for (int i = 0; i < length; i++) {
                    File delFile = files[i];
                    if (delFile.isDirectory()) {
                        // 按逻辑没有子目录，除非用户自己添加，故不要去删除用户的数据
                    } else {
                        lastModified = delFile.lastModified();
                        if ((currentTime - lastModified) > DOWNLOAD_MAX_TIME) {
                            delFile.delete();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 保存固定数据
     *
     * @param key
     * @param value
     */
    public static void saveFixedInfo(final Context context, final String key, final String value) {
        // 多处保存，速度会比较慢
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 保存到系统数据
                Settings.System.putString(context.getApplicationContext().getContentResolver(), key,
                        value);
                // 保存到sdcard
                File file = new File(FIXED_PATH);
                if (!file.exists()) {
                    file.mkdirs();
                } else if (file.isFile()) {
                    file.delete();
                    file.mkdirs();
                }
                FileOutputStream fos = null;
                try {
                    String filePath = FIXED_PATH + File.separator + key;
                    fos = new FileOutputStream(new File(filePath));
                    fos.write(value.getBytes());
                } catch (Exception e) {
                } finally {
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                    }
                }
                // 保存到/data/data/
                SharedPreferencesUtil.saveLocalInfo(context, key, value);
            }
        }).start();

    }

    /**
     * 取出固定数据
     *
     * @param key
     * @return
     */
    public static synchronized String getFixedInfo(Context context, String key) {
        // 从系统设置中取
        String value = Settings.System.getString(context.getApplicationContext()
                .getContentResolver(), key);
        // 从sdcard 取
        if (TextUtils.isEmpty(value)) {
            BufferedReader reader = null;
            try {
                String filePath = FIXED_PATH + File.separator + key;
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                value = reader.readLine();
            } catch (Exception e) {
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (Exception e) {
                }
            }
        }
        // 从/data/data/中取
        if (TextUtils.isEmpty(value)) {
            value = SharedPreferencesUtil.readLocalInfo(context, key);
        }
        // 再次保存防止被删
        if (!TextUtils.isEmpty(value)) {
            saveFixedInfo(context, key, value);
        } else {
            value = null;
        }

        return value;
    }

    /**
     * 转换文件格式
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {// 转换文件大小
        if (fileS <= 0) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getTestServerUrl(Context context) {
        String url = null;
        BufferedReader reader = null;
        try {
            String filePath = CFG_PATH_SDCARD_ROOT + File.separator
                    + context.getPackageName().replace('.', '_');
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            url = reader.readLine().trim();
        } catch (Exception e) {
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception e) {
            }
        }

        return url;
    }

    public static <T> T loadAssetsFile(Context context, String fileName, InputStreamParser<T> parse) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            return parse.parser(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
