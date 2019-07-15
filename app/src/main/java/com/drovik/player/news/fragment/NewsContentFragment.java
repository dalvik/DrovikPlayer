package com.drovik.player.news.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.blankj.utilcode.util.ScreenUtils;
import com.drovik.player.R;
import com.drovik.player.news.base.AppBarStateChangeListener;
import com.drovik.player.news.base.BaseFragment;
import com.drovik.player.news.base.SettingUtil;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.contract.INewsContent;
import com.drovik.player.news.utils.Constant;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;

public class NewsContentFragment extends BaseFragment<INewsContent.Presenter> implements INewsContent.View {

    private static final String TAG = "NewsContentFragment";
    private static final String IMG = "img";
    // 新闻链接 标题 头条号 文章号 媒体名
    private String shareUrl;
    private String shareTitle;
    private String mediaUrl;
    private String mediaId;
    private String mediaName;
    private String imgUrl;
    private boolean isHasImage;
    private MultiNewsArticleDataBean bean;

    private WebView webView;
    private NestedScrollView scrollView;
    private INewsContent.Presenter presenter;
    private ContentLoadingProgressBar progressBar;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final static int MSG_REQUEST_AD = 1000;
    private final static int MSG_HIDE_AD = 1001;
    private final static String adUnitId = "4EB378DDD1ACCC98DB5430437962ACF8";
    private IFLYBannerAd bannerView;
    private LinearLayout bannerAdLayout;
    private Context mContext;

    public static NewsContentFragment newInstance(Parcelable dataBean, String imgUrl) {
        NewsContentFragment instance = new NewsContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG, dataBean);
        bundle.putString(IMG, imgUrl);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    protected int attachLayoutId() {
        imgUrl = getArguments().getString(IMG);
        isHasImage = !TextUtils.isEmpty(imgUrl);
        return isHasImage ? R.layout.fragment_news_content_img : R.layout.fragment_news_content;
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        try {
            bean = bundle.getParcelable(TAG);
            presenter.doLoadData(bean);
            shareUrl = !TextUtils.isEmpty(bean.getShare_url()) ? bean.getShare_url() : bean.getDisplay_url();
            shareTitle = bean.getTitle();
            mediaName = bean.getMedia_name();
            mediaUrl = "http://toutiao.com/m" + bean.getMedia_info().getMedia_id();
            mediaId = bean.getMedia_info().getMedia_id();
            mContext = getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isHasImage) {
            //ImageLoader.loadCenterCrop(getActivity(), bundle.getString(IMG), imageView, R.mipmap.error_image, R.mipmap.error_image);
            appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                @Override
                public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                    if (state == State.EXPANDED) {
                        // 展开状态
                        collapsingToolbarLayout.setTitle("");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }
                    } else if (state == State.COLLAPSED) {
                        // 折叠状态

                    } else {
                        // 中间状态
                        collapsingToolbarLayout.setTitle(mediaName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }
                    }
                }
            });
        } else {
            //toolbar.setTitle(mediaName);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isHasImage) {
            appBarLayout.setExpanded(false);
        }
    }

    @Override
    protected void initView(View view) {
        webView = view.findViewById(R.id.webview);
        initWebClient();

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                onHideLoading();
            }
        });

        progressBar = view.findViewById(R.id.pb_progress);
        int color = SettingUtil.getInstance().getColor();
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        progressBar.show();

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(SettingUtil.getInstance().getColor());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                presenter.doLoadData(bean);
            }
        });

        if (isHasImage) {
            appBarLayout = view.findViewById(R.id.app_bar_layout);
            collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
            imageView = view.findViewById(R.id.iv_image);
        }
        setHasOptionsMenu(true);
        bannerAdLayout = (LinearLayout)view.findViewById(R.id.ad_container);
        sendHandlerMessage(MSG_REQUEST_AD, 1000);
    }

    private void initWebClient() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        settings.setBuiltInZoomControls(false);
        // 缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启DOM storage API功能
        settings.setDomStorageEnabled(true);
        // 开启application Cache功能
        settings.setAppCacheEnabled(true);
        // 判断是否为无图模式
        settings.setBlockNetworkImage(SettingUtil.getInstance().getIsNoPhotoMode());
        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.FAR;
        switch (screenDensity)
        {
            case DisplayMetrics.DENSITY_LOW:
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
            default:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break;
        }
        settings.setDefaultZoom(zoomDensity);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 不调用第三方浏览器即可进行页面反应
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                onHideLoading();
                super.onPageFinished(view, url);
                String javascript = "javascript:function ResizeImages() {" +
                        "var myimg,oldwidth;" +
                        "var maxwidth = document.body.clientWidth;" +
                        "for(i=0;i <document.images.length;i++){" +
                        "myimg = document.images[i];" +
                        "if(myimg.width > maxwidth){" +
                        "oldwidth = myimg.width;" +
                        "myimg.width = maxwidth;" +
                        "}" +
                        "}" +
                        "}";
                view.loadUrl(javascript);
                view.loadUrl("javascript:ResizeImages();");
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 90) {
                    onHideLoading();
                } else {
                    onShowLoading();
                }
            }
        });
    }

    @Override
    public void onSetWebView(String url, boolean flag) {
        // 是否为头条的网站
        if (flag) {
            webView.setBackgroundColor(getResources().getColor(R.color.base_bg));
            webView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
        } else {
            /*
               ScrollView 嵌套 WebView, 导致部分网页无法正常加载
               如:https://temai.snssdk.com/article/feed/index/?id=11754971
               最佳做法是去掉 ScrollView, 或使用 NestedScrollWebView
             */
            if (shareUrl.contains("temai.snssdk.com")) {
                webView.getSettings().setUserAgentString(Constant.USER_AGENT_PC);
            }
            webView.loadUrl(shareUrl);
        }
    }

    @Override
    public void onShowNetError() {
        Snackbar.make(scrollView, R.string.network_error, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void setPresenter(INewsContent.Presenter presenter) {
        if (null == presenter) {
            this.presenter = new NewsContentPresenter(this);
        }
    }

    @Override
    public void onShowLoading() {
        progressBar.show();
    }

    @Override
    public void onHideLoading() {
        progressBar.hide();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_browser, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_open_comment:
                NewsCommentActivity.launch(bean.getGroup_id() + "", bean.getItem_id() + "");
                break;

            case R.id.action_share:
                IntentAction.send(getActivity(), shareTitle + "\n" + shareUrl);
                break;*/

            /*case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(shareUrl)));
                break;*/

            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            /*case R.id.action_open_media_home:
                MediaHomeActivity.launch(mediaId);
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeMessages(MSG_REQUEST_AD);
    }

    private void initAd() {

        bannerView = IFLYBannerAd.createBannerAd(getActivity(), adUnitId);
        bannerView.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(mContext));
        //广告容器添加bannerView
        bannerAdLayout.removeAllViews();
        bannerAdLayout.addView(bannerView);
        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
    }

    private void hideAd() {
        if (bannerView != null) {
            bannerView.destroy();
        }
        bannerAdLayout.setVisibility(View.GONE);
    }

    private void sendHandlerMessage(int what, long delayed) {
        mHandler.removeMessages(what);
        mHandler.sendEmptyMessageDelayed(what, delayed);
    }

    //3秒钟后请求，显示1分钟自动隐藏，15分钟重新请求
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_REQUEST_AD:
                    initAd();
                    break;
                case MSG_HIDE_AD:
                    hideAd();
                    sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
                    break;
                default:
                    break;
            }
        }
    };


    private IFLYAdListener mAdListener = new IFLYAdListener() {

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerAdLayout.setVisibility(View.VISIBLE);
            bannerView.showAd();
            sendHandlerMessage(MSG_HIDE_AD, 60*1000);//1分钟后自动隐藏
            LogUtil.d(TAG, "==> onAdReceive");
            //Toast.makeText(getActivity(), "onAdReceive", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onAdFailed(AdError error) {
            //获取广告失败
            //Toast.makeText(getActivity(), "onAdFailed", Toast.LENGTH_SHORT).show();
            LogUtil.d(TAG, "==> onAdFailed: " + error.getErrorDescription() + " " + error.getErrorCode());
            sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            LogUtil.d(TAG, "==> onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            LogUtil.d(TAG, "==> onAdClose");
        }

        /**
         * 广告曝光
         */
        @Override
        public void onAdExposure() {
            LogUtil.d(TAG, "==> onAdExposure");
        }

        /**
         * 下载确认
         */
        @Override
        public void onConfirm() {
            LogUtil.d(TAG, "==> onConfirm");
        }

        /**
         * 下载取消
         */
        @Override
        public void onCancel() {
            LogUtil.d(TAG, "==> onCancel");
        }
    };
}
