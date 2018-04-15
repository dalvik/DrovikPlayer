package com.android.library.net.base;

import com.android.library.BaseApplication;
import com.android.library.R;
import com.android.library.net.manager.AbstractDataManager;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.utils.ActivityUtils;
import com.android.library.ui.utils.ToastUtils;

public class DataManagerUICallBack implements IDataCallback {

    private UICallBackListener uiListener;

    public DataManagerUICallBack(UICallBackListener listener) {
        uiListener = listener;
    }

    public static DataManagerUICallBack newInstance(UICallBackListener listener) {
        return new DataManagerUICallBack(listener);
    }
    
    @Override
    public void onCallback(int what, int result, int arg2, Object obj) {
        if (uiListener == null) {
            return;
        }
        uiListener.onHandleUI();
        try {
            switch (result) {
                case AbstractDataRequestListener.RESULT_PARSEERR:
                    ToastUtils.showToast(R.string.data_parse_err);
                    break;
                case AbstractDataRequestListener.RESULT_NET_ERROR:
                    ToastUtils.showToast(R.string.no_net);
                    uiListener.onNetError(what);
                    break;
                default:
                    if (result < 400) {//服务端定400以内为异常(按需修改)
                        if (result == AbstractDataManager.RESULT_SID_TIMEOUT) {
                            // 会话超时
                            onSessionTimeOut();
                            uiListener.onSessionTimeOut();
                        } else {
                            /*if (BaseApplication.DEBUG) {
                                ToastUtils.showToast(((AbstractNetData) obj).desc);
                            }*/
                            ToastUtils.showToast(R.string.system_err);
                        }
                    } else {
                        if (!uiListener.onHandleBiz(what, result, arg2, obj)) {
                            //ToastUtils.showToast(((AbstractNetData) obj).desc);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.client_err);
        }
    }

    /**
     * 回话失效
     */
    private void onSessionTimeOut() {
        if (BaseApplication.curContext instanceof BaseCommonActivity) {
            ((BaseCommonActivity) BaseApplication.curContext).finishAll();
        }
        ActivityUtils.gotoLogin(BaseApplication.curContext);
    }
    
    public interface UICallBackListener {
        /**
         * 处理业务逻辑
         *
         * @param what
         * @param result
         * @param arg2
         * @param obj
         * @return 如果返回ture 表示,已经处理相关数据, false父类继续处理(打印未知错误信息)
         */
        boolean onHandleBiz(int what, int result, int arg2, Object obj);

        /**
         * 处理UI相关
         */
        void onHandleUI();

        /**
         * 无网络
         */
        void onNetError(int what);

        /**
         * 会话失效
         */
        void onSessionTimeOut();
    }
    
}
