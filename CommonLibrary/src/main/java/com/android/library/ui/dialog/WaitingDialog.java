package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;


/**
 * 自定义进度条,当发起网络请求时显示该对话框，让用户等待
 */
public class WaitingDialog extends BaseDialog {

    public static WaitingDialog newInstance(int contentId) {
        WaitingDialog dialog = new WaitingDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NO_TITLE);
        bundle.putInt(ARGS_THEME, R.style.WaitingDialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, false);
        bundle.putInt(ARGS_CONTENT, contentId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiting_dialog, null);
        TextView contentTv = (TextView) view.findViewById(R.id.contentTv);
        setText(contentTv, content);
        return view;
    }

    protected void setDialogWidth() {
    }
}
