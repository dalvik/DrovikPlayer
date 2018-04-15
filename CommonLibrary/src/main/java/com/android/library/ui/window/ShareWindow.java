package com.android.library.ui.window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.android.library.R;
import com.android.library.ui.activity.BaseCommonActivity;


public class ShareWindow extends BasePopupWindow implements View.OnClickListener {

    private final int contentType;

    public ShareWindow(BaseCommonActivity activity, int contentType) {
        super(activity);
        this.contentType=contentType;
    }

    @Override
    public PopupWindow initWindow() {
        View view = LayoutInflater.from(activity).inflate(R.layout.share, null);
        view.findViewById(R.id.qqIv).setOnClickListener(this);
        view.findViewById(R.id.wxIv).setOnClickListener(this);
        view.findViewById(R.id.wxFridendIv).setOnClickListener(this);
        view.findViewById(R.id.cancelBtn).setOnClickListener(this);
        view.setOnClickListener(this);
        window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        window.setOutsideTouchable(true);
        backgroundAlpha(0.4f);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        window.setAnimationStyle(R.style.UI_HisPersonalCenetr_More_Anim);
        return window;
    }
    //分享渠道
    public static  final int SHARE_TYPE_QQ=1000;
    public static  final int SHARE_TYPE_WX=SHARE_TYPE_QQ+1;
    public static  final int SHARE_TYPE_WXFRIEND=SHARE_TYPE_WX+1;
    //分享什么
    public static  final int SHARE_TYPE_USER=SHARE_TYPE_WXFRIEND+1;//用户
    public static  final int SHARE_TYPE_ACT=SHARE_TYPE_USER+1;//活动
    public static  final int SHARE_TYPE_SHOW=SHARE_TYPE_ACT+1;//秀场

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.cancelBtn:
                window.dismiss();
                break;
        }*/
        if (window.isShowing()) {
            window.dismiss();
        }
        /*switch (v.getId()){
            case R.id.cancelBtn:
                break;
            case R.id.qqIv:
                if(activity instanceof HisPersonalCenterActivity){
                    ((HisPersonalCenterActivity)activity).share(SHARE_TYPE_QQ,contentType);
                }else if(activity instanceof ShowsActivity){
                    ((ShowsActivity)activity).share(SHARE_TYPE_QQ, contentType);

                }
                break;
            case R.id.wxIv:
                if(activity instanceof HisPersonalCenterActivity){
                    ((HisPersonalCenterActivity)activity).share(SHARE_TYPE_WX,contentType);
                }else if(activity instanceof ShowsActivity){
                    ((ShowsActivity)activity).share(SHARE_TYPE_WX, contentType);

                }
                break;
            case R.id.wxFridendIv:
                if(activity instanceof HisPersonalCenterActivity){
                    ((HisPersonalCenterActivity)activity).share(SHARE_TYPE_WXFRIEND,contentType);
                }else if(activity instanceof ShowsActivity){
                    ((ShowsActivity)activity).share(SHARE_TYPE_WXFRIEND, contentType);
                }
                break;
            default:
                break;
        }*/
        window.dismiss();

    }
}



