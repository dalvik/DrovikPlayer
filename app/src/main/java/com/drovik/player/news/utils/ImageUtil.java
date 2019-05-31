package com.drovik.player.news.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.drovik.player.news.view.CircleTransform;
import com.squareup.picasso.Picasso;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/3/14
 * 描    述：图片加载工具类
 * 修订历史：
 * ================================================
 */
public class ImageUtil {

    /**
     * 加载图片
     */
    public static void loadImgByPicasso(Context context , String path, ImageView target) {
        if(target==null){
            return;
        }
        if(path!=null && path.length()>0){
            Picasso.with(context)
                    .load(path)
                    .into(target);
        }
    }

    /**
     * 加载图片
     */
    public static void loadImgByPicasso(Context context , int path, ImageView target) {
        if(target==null){
            return;
        }
        if(path!=0){
            Picasso.with(context)
                    .load(path)
                    .placeholder(path)
                    .into(target);
        }
    }


    /**
     * 加载本地图片
     * @param context       上下文
     * @param path          路径
     * @param target        view
     */
    public static void loadImgByPicassoWithCircle(Context context , int path, ImageView target) {
        if(target==null){
            return;
        }
        if(path>0){
            Picasso.with(context)
                    .load(path)
                    .placeholder(path)
                    .transform(new CircleTransform())
                    .into(target);
        }
    }


    /**
     * 加载人物，机构logo时，加载失败时显示默认图片
     * @param path          路径
     * @param resId         加载失败时，默认图片
     * @param target        控件
     */
    public static void loadImgByPicassoPerson(Context context , String path, int resId, ImageView target) {
        if(target==null){
            return;
        }
        if(path!=null && path.length()>0){
            Picasso.with(context)
                    .load(path)
                    .error(resId)
                    .placeholder(resId)
                    .transform(new CircleTransform())
                    .into(target);
        }
    }


    /**
     * 加载图片
     * @param resId
     * @param target        控件
     */
    public static void loadImgByPicasso(Context context , String path , int resId, ImageView target) {
        if(target==null){
            return;
        }
        if(path!=null && path.length()>0){
            Picasso.with(context)
                    .load(path)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }else {
            Picasso.with(context)
                    .load(resId)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }
    }


    /**
     * 加载图片
     * @param resId
     * @param target        控件
     */
    public static void loadImgByPicasso(Context context , int path , int resId, ImageView target) {
        if(target==null){
            return;
        }
        if(path>0){
            Picasso.with(context)
                    .load(path)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }else {
            Picasso.with(context)
                    .load(resId)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }
    }


    /**
     * 加载图片
     * @param resId
     * @param target        控件
     */
    public static void loadImgByPicassoError(Context context , String path , int resId, ImageView target) {
        if(target==null){
            return;
        }
        if(path!=null && path.length()>0){
            Picasso.with(context)
                    .load(path)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }else {
            Picasso.with(context)
                    .load(resId)
                    .placeholder(resId)
                    .error(resId)
                    .into(target);
        }
    }


    /**------------------------------Glide加载图片--------------------------------------------------**/


    /**
     * 将gif图转换为静态图
     * @param context
     * @param url
     * @param resId
     * @param imageView
     */
    public static void displayGif(Context context , String url, int resId ,ImageView imageView) {
        if(imageView==null){
            return;
        }
        if(url!=null && url.length()>0){
            /*Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .placeholder(resId)
                    .error(resId)
                    .into(imageView);*/
        }else {
            /*Glide.with(context)
                    .load(resId)
                    .asBitmap()
                    .placeholder(resId)
                    .error(resId)
                    .into(imageView);*/
        }
    }


    /**
     * 加载带有圆角的矩形图片  用glide处理
     *
     * @param path   路径
     * @param round  圆角半径
     * @param resId  加载失败时的图片
     * @param target 控件
     */
    public static void loadImgByPicassoWithRound(final Context activity, String path, final int round, int resId, final ImageView target) {
        if (path != null && path.length() > 0) {
            /*Glide.with(activity)
                    .load(path)
                    .asBitmap()
                    .placeholder(resId)
                    .error(resId)
                    //设置缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new BitmapImageViewTarget(target) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                            //设置圆角弧度
                            circularBitmapDrawable.setCornerRadius(round);
                            target.setImageDrawable(circularBitmapDrawable);
                        }
                    });*/
        }
    }


}
