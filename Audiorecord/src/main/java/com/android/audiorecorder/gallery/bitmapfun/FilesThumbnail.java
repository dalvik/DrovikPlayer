package com.android.audiorecorder.gallery.bitmapfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesThumbnail {
    public static final String TAG = "FilesThumbnail";
    
    private Context mContext;
    
    private static FilesThumbnail mFileThumbnail;
    
    private static final int CACHE_SIZE = 200;
    
    private static final int THUMBNAIL_SIZE = 130;
    
    private BroadcastReceiver sdBroadcastReceiver = null;

    public synchronized static FilesThumbnail getInstance(Context context){
        if(mFileThumbnail ==null){
            mFileThumbnail = new FilesThumbnail(context);
        }
        return mFileThumbnail;
    }
    private FilesThumbnail(Context context){
        mContext = context;
    }
    private void removeThumbnail(Thumbnail thum){
        if(thum!=null){
            File thumFile = new File(thum.thumbnail);
            thumFile.delete();
        }
    }
    public String getThumbnail(String file){
        long time = System.currentTimeMillis();
        return getThumbnail(file,time);
    }
    private String getThumbnail(String path, long time){
        String thumbPath = null;
        if(path != null){
            Thumbnail thumbnail = genThumbnail(path);//gen
            if(thumbnail != null){
                thumbPath = thumbnail.thumbnail;
            }
        }
        return thumbPath;
    }
    public void saveThumbnail(String file){
        long time = System.currentTimeMillis();
        getThumbnail(file,time);
    }
    private Thumbnail newThumbnail(String file){
        Thumbnail thumb = genThumbnail(file);
        return thumb;
    }
    private Thumbnail genThumbnail(String file){
        return createVideoThumbnail(file,THUMBNAIL_SIZE);
    }
    
    private String saveBitmapInFile(Bitmap bitmap, String source, String resolution, String type){
        if(TextUtils.isEmpty(source)) {
            return null;
        }
        File sourceFile = new File(source);
        String parent_name = sourceFile.getParentFile().getName();
        String file_name = sourceFile.getName();
        file_name = file_name.substring(0, file_name.lastIndexOf("."));
        final String PARENT_PATH = FileUtils.getRootPath(mContext) + File.separator + FileProviderService.ROOT + File.separator;//storage/sdcard0/MediaFile
        final String thumbnailFoldPath = PARENT_PATH + FileProviderService.THUMBNAIL + File.separator;//storage/sdcard0/MediaFile/Thumbnail/
        String name = thumbnailFoldPath + parent_name + "_" + file_name + ".jpg";
        Log.d(TAG, "==> thumbnail: " + name);
        File file = new File(thumbnailFoldPath);
        if(!file.exists()){
            file.mkdirs();
        }
        File nomdeia = new File(thumbnailFoldPath, ".nomedia");
        if(!nomdeia.exists()){
            try {
                nomdeia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            File jpg = new File(name);
            if(jpg.exists()){
                jpg.delete();
            }
            FileOutputStream fos = new FileOutputStream(name);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            name = null;
        }
        return name;
    }

    private Thumbnail createVideoThumbnail(String filePath, int size) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        if (bitmap == null) return null;
        // Scale down the bitmap if it is bigger than we need.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int wr = width;
        int hr = height;
        int maxSize = width>height?width:height;
        if (maxSize > size) {
            float scale = (float) size / maxSize;
            width = Math.round(scale * width);
            height = Math.round(scale * height);
            Bitmap smallbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            bitmap.recycle();
            bitmap = smallbitmap;
        }
        Thumbnail thumb = new Thumbnail();
        thumb.width = width;
        thumb.height = height;
        thumb.thumbnail = saveBitmapInFile(bitmap, filePath, "_" +  wr + "x" + hr,"dav");
        bitmap.recycle();
        return thumb;
    }


    public interface Columns{
        public static final String PATH = "_path";
        public static final String THUMBNAIL = "_thumbnail";
        public static final String WIDTH = "_width";
        public static final String HEIGHT = "_height";
        public static final String TIME = "_time";
    }
    
    public static class Thumbnail{
        public String path;
        public String thumbnail;
        public int width;
        public int height;
        public long time;
        @Override
        public String toString() {
            return "Thumbnail: Path="+path
                    +",\nThumbnail="+thumbnail
                    +",\nWidth="+width+", Height="+height+", Time="+time;
        }
    }
    
}
