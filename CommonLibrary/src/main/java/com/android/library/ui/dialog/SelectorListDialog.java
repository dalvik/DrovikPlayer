package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.adapter.SelectAdapter;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 自定义进度条,当发起网络请求时显示该对话框，让用户等待
 */
public class SelectorListDialog extends BaseDialog {

    private int itemsId;

    private boolean isShowCancel;

    private OnSelectedListener listener;

    public static SelectorListDialog newInstance(boolean cancelable, int itemsId, OnSelectedListener listener) {
        SelectorListDialog dialog = new SelectorListDialog();
        dialog.isShowCancel = cancelable;
        dialog.itemsId = itemsId;
        dialog.listener = listener;
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NO_TITLE);
        bundle.putInt(ARGS_THEME, R.style.DialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, false);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selector_list_dialog, null);
        TextView titleTv = (TextView) view.findViewById(R.id.titleTv);
        setText(titleTv, title);
        View cancelView = view.findViewById(R.id.cancelBtn);
        if (isShowCancel) {
            cancelView.setVisibility(View.VISIBLE);
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                }
            });
        }
        // 配置显示的列表
        ListView selectList = (ListView) view.findViewById(R.id.listView);
        final SelectAdapter adapter = new SelectAdapter(activity);
        adapter.addItems((ArrayList<String>) Arrays.asList(activity.getResources().getStringArray(itemsId)));
        selectList.setAdapter(adapter);
        // 设置单选点击监听事件
        selectList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 返回选择的数据
                listener.onSelected(adapter.getItem(position).toString(), position);
                // 关闭Dialog
                dismissAllowingStateLoss();
            }
        });
        return view;
    }

    public static interface OnSelectedListener {
        void onSelected(String item, int index);
    }
}
