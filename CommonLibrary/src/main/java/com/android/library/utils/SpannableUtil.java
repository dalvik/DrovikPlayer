package com.android.library.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;

import java.util.Locale;

//import android.graphics.Rasterizer;
//import android.text.style.RasterizerSpan;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SpannableUtil {
    /**
     * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含两端start和end所在的端点
     * Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点
     * Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含端start，但不包含end所在的端点
     * Spanned.SPAN_INCLUSIVE_INCLUSIVE--- 包含两端start和end所在的端点
     **/
    /**
     * 仅实现了大部分效果 有些些方法还需调整 后面有点偷懒 直接给了构造方法的参数 没有多写几个重载来更好地支持自定义
     **/
    /**
     * 1、BackgroundColorSpan 背景色 2、ClickableSpan 文本可点击，有点击事件
     * 3、ForegroundColorSpan 文本颜色（前景色） 4、MaskFilterSpan
     * 修饰效果，如模糊(BlurMaskFilter)、浮雕(EmbossMaskFilter) (经测试无效，不知如何生效 硬件加速关闭也没有效果)
     * 5、MetricAffectingSpan 父类，一般不用 6、RasterizerSpan 光栅效果 (经测试无效，不知如何生效
     * 硬件加速关闭也没有效果) 7、StrikethroughSpan 删除线（中划线） 8、SuggestionSpan 相当于占位符
     * 9、UnderlineSpan 下划线 10、AbsoluteSizeSpan 绝对大小（文本字体） 11、DynamicDrawableSpan
     * 设置图片，基于文本基线或底部对齐。 12、ImageSpan 图片 13、RelativeSizeSpan 相对大小（文本字体）
     * 14、ReplacementSpan 父类，一般不用 15、ScaleXSpan 基于x轴缩放 16、StyleSpan 字体样式：粗体、斜体等
     * 17、SubscriptSpan 下标（数学公式会用到） 18、SuperscriptSpan 上标（数学公式会用到）
     * 19、TextAppearanceSpan 文本外貌（包括字体、大小、样式和颜色） (本demo中未实现) 20、TypefaceSpan
     * 文本字体 21、URLSpan 文本超链接
     **/
    /**
     * 1 BackgroundColorSpan 背景色
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param color
     * @return
     */
    public static SpannableString setBackgroundColorSpan(String content, int startIndex, int endIndex, int color) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new BackgroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 2 ClickableSpan 文本可点击，有点击事件 去下划线的可点击span
     * 
     * @param content
     * @param startIndex
     * @param endIndex
     * @param url
     * @return
     */
    public static SpannableString setClickableSpan(String content, int startIndex, int endIndex, final String url) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                // super.updateDrawState(ds);
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                // url处理 跳转网页或其他
                Uri uri = Uri.parse(url);
                Context context = widget.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
                Log.i("span", url);
            }

        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 2重载 ClickableSpan 文本可点击，有点击事件 去下划线的可点击span
     * 
     * @param content
     * @param startIndex
     * @param endIndex
     * @param clickableSpan
     *            自己重写ClickableSpan的 updateDrawState与onClick方法
     * @return
     */
    public static SpannableString setClickableSpan(String content, int startIndex, int endIndex,
                                                   ClickableSpan clickableSpan) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        if (null != clickableSpan) {
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.i("span", "clicked");
                }
            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    /**
     * 3 ForegroundColorSpan 前景色
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param color
     * @return
     */
    public static SpannableString setForegroundColorSpan(String content, int startIndex, int endIndex, int color) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 17 SubscriptSpan 下标（数学公式会用到）
     * 
     * @param content
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static SpannableString setSubscriptSpan(String content, int startIndex, int endIndex) {
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return null;
        }

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new SubscriptSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 18 SuperscriptSpan 上标（数学公式会用到）
     * 
     * @param content
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static SpannableString setSuperscriptSpan(String content, int startIndex, int endIndex) {
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return null;
        }

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new SuperscriptSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 4 (经测试无效，不知如何生效 硬件加速关闭也没有效果) MaskFilterSpan
     * 修饰效果，如模糊(BlurMaskFilter)、浮雕(EmbossMaskFilter)
     * 
     * @param content
     * @param startIndex
     * @param endIndex
     * @param blurMaskFilter
     *            模糊
     * @param embossMaskFilter
     *            浮雕
     * @param maskFilter
     *            其他自定义效果
     * @return
     */
    public static SpannableString setMaskFilterSpan(String content, int startIndex, int endIndex,
                                                    BlurMaskFilter blurMaskFilter, EmbossMaskFilter embossMaskFilter, MaskFilter maskFilter) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        if (null == blurMaskFilter) {
            spannableString.setSpan(new BlurMaskFilter(50, Blur.SOLID), startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(blurMaskFilter, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (null == embossMaskFilter) {
            spannableString.setSpan(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, (float) 3.5), startIndex,
                    endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(embossMaskFilter, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (null != maskFilter) {
            spannableString.setSpan(maskFilter, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    /**
     * 6 (经测试无效，不知如何生效 硬件加速关闭也没有效果) RasterizerSpan 光栅效果
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @return
     */
    public static SpannableString setRasterizerSpan(String content, int startIndex, int endIndex) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        /*spannableString.setSpan(new RasterizerSpan(new Rasterizer()), startIndex, endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        return spannableString;
    }

    /**
     * 7 StrikethroughSpan 删除线（中划线）
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @return
     */
    public static SpannableString setStrikethroughSpan(String content, int startIndex, int endIndex) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StrikethroughSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 8 SuggestionSpan 替换建议
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @return
     */
    public static SpannableString setSuggestionSpan(String content, int startIndex, int endIndex) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new SuggestionSpan(Locale.CHINESE,
                new String[] { "SuggestionSpan1", "SuggestionSpan2", "SuggestionSpan3" },
                SuggestionSpan.FLAG_EASY_CORRECT), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 9 UnderlineSpan 下划线
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @return
     */
    public static SpannableString setUnderlineSpan(String content, int startIndex, int endIndex) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 10 AbsoluteSizeSpan 绝对大小（文本字体）(不建议使用，因为此处指定的字号大小以px为单位)
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param size
     *            字号，单位px
     * @return
     */
    public static SpannableString setAbsoluteSizeSpan(String content, int startIndex, int endIndex, int size) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan(size), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 11 DynamicDrawableSpan 设置图片，基于文本基线或底部对齐
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param drawable
     *            显示的图片
     * @return
     */
    public static SpannableString setDynamicDrawableSpan(String content, int startIndex, int endIndex,
                                                         final Drawable drawable) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        // 基线对齐
        spannableString.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {

            @Override
            public Drawable getDrawable() {
                Drawable d = drawable;
                d.setBounds(0, 0, 50, 50);
                return d;
            }

        }, startIndex, startIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 底部对齐
        spannableString.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BOTTOM) {

            @Override
            public Drawable getDrawable() {
                Drawable d = drawable;
                d.setBounds(0, 0, 50, 50);
                return d;
            }

        }, startIndex + 1, startIndex + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 12 ImageSpan 图片
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param drawable
     *            显示的图片
     * @return
     */
    public static SpannableString setImageSpan(String content, int startIndex, int endIndex, final Drawable drawable) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        drawable.setBounds(0, 0, 50, 50);
        spannableString.setSpan(new ImageSpan(drawable), startIndex, startIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 13 RelativeSizeSpan 相对大小（文本字体）
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param size
     *            字缩放倍数
     * @return
     */
    public static SpannableString setRelativeSizeSpan(String content, int startIndex, int endIndex, float size) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new RelativeSizeSpan(size), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
    // ScaleXSpan 基于x轴缩放

    /**
     * 15 ScaleXSpan 基于x轴缩放
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param size
     *            x轴缩放倍数
     * @return
     */
    public static SpannableString setScaleXSpan(String content, int startIndex, int endIndex, float size) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ScaleXSpan(size), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 16 StyleSpan 字体样式：粗体、斜体等
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param flag
     *            Typeface.xxx
     * @return
     */
    public static SpannableString setStyleSpan(String content, int startIndex, int endIndex, int flag) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StyleSpan(flag), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 19 TextAppearanceSpan 文本字体
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param context
     * @return
     */
    public static SpannableString setTextAppearanceSpan(String content, int startIndex, int endIndex,
                                                        Context activity) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new TextAppearanceSpan(activity, android.R.style.TextAppearance_Holo_Large_Inverse),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 20 TypefaceSpan 文本字体
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param family
     *            The font family for this typeface. Examples include
     *            "monospace", "serif", and "sans-serif".
     * @return
     */
    public static SpannableString setTypefaceSpan(String content, int startIndex, int endIndex, String family) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new TypefaceSpan(family), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 21 URLSpan 文本超链接
     * 
     * @param context
     * @param start_index
     * @param end_index
     *            设为0则到最后
     * @param url
     * @return
     */
    public static SpannableString setURLSpan(String content, int startIndex, int endIndex, String url) {
        if (0 == endIndex) {
            endIndex = content.length();
        }
        if (TextUtils.isEmpty(content) || startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return null;
        }

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new URLSpan(url), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
