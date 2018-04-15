package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;

public class UpdateDialog extends ConfirmDialog {
    private final static String ARGS_CONTENT_TXT = "ARGS_CONTENT_TXT";

    private String content;

    public static UpdateDialog newInstance(int titleId, String content, int confirm, int cancelId, boolean cancelable, OnResultListener listener) {
        UpdateDialog dialog = new UpdateDialog();
        dialog.listener = listener;

        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NO_TITLE);
        //bundle.putInt(ARGS_THEME, org.xl.common.R.style.WaitingDialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, cancelable);
        bundle.putInt(ARGS_TITLE, titleId);
        bundle.putString(ARGS_CONTENT_TXT, content);
        bundle.putInt(ARGS_CONFIRM, confirm);
        bundle.putInt(ARGS_CANCEL, cancelId);
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString(ARGS_CONTENT_TXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView contentTv = (TextView) view.findViewById(R.id.contentTv);
        contentTv.setText(content);
        if (cancel == 0) {
            view.findViewById(R.id.cancelBtn).setVisibility(View.GONE);
        }
        return view;
    }
}
