package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.library.R;


/**
 * 自定义进度条,当发起网络请求时显示该对话框，让用户等待
 */
public class ConfirmDialog extends BaseDialog {

    protected static final String ARGS_CANCEL = "CANCEL";

    protected int cancel;

    protected OnResultListener listener;

    public static ConfirmDialog newInstance(int titleId, int contentId, int confirm, int cancelId, boolean cancelable, OnResultListener listener) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.listener = listener;

        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NO_TITLE);
        bundle.putInt(ARGS_THEME, R.style.WaitingDialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, cancelable);
        bundle.putInt(ARGS_TITLE, titleId);
        bundle.putInt(ARGS_CONTENT, contentId);
        bundle.putInt(ARGS_CONFIRM, confirm);
        bundle.putInt(ARGS_CANCEL, cancelId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancel = getArguments().getInt(ARGS_CANCEL, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        final Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);

        setText(cancelBtn, cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
                dismissAllowingStateLoss();
            }
        });

        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        setText(okBtn, confirm);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm();
                }
                dismissAllowingStateLoss();
            }
        });

        TextView contentTv = (TextView) view.findViewById(R.id.contentTv);
        setText(contentTv, content);

        TextView titleTv = (TextView) view.findViewById(R.id.titleTv);
        setText(titleTv, title);
        return view;
    }

    public static interface OnResultListener {
        void onConfirm();

        void onCancel();
    }

}
