package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.library.R;

public class TextDialog extends ConfirmDialog {

    public static TextDialog newInstance(int titleId, int contentId, int confirmId, boolean cancelable) {
        TextDialog dialog = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NORMAL);
        bundle.putInt(ARGS_THEME, R.style.WaitingDialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, cancelable);
        bundle.putInt(ARGS_TITLE, titleId);
        bundle.putInt(ARGS_CONTENT, contentId);
        bundle.putInt(ARGS_CONFIRM, confirmId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.cancelBtn).setVisibility(View.GONE);
        return view;
    }
}
