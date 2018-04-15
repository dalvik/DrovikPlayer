package com.android.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 悬浮菜单
 *
 * @author Sean.xie <br/>
 *         create at 2014年5月19日 下午6:44:23
 */
public class PopupMenuView extends LinearLayout {

    public PopupMenuView(Context context) {
        super(context);
    }

    public PopupMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 添加菜单
     *
     * @param menuLayout
     */
    public void addMenus(int menuLayout, int[] menus, final PopupMenuListener menuListener) {
        View popupMenu = LayoutInflater.from(getContext()).inflate(menuLayout, this);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuListener != null) {
                    menuListener.onClick(v);
                }
            }
        };
        for (int menu : menus) {
            popupMenu.findViewById(menu).setOnClickListener(listener);
        }
    }

    /**
     * 设置TextView 内容
     *
     * @param resId
     * @param text
     */
    public void setText(int resId, String text) {
        ((TextView) findViewById(resId)).setText(text);
    }

    /**
     * 悬浮菜单监听 com.wuyou.ui.view.PopupMenuListener
     *
     * @author Sean.xie <br/>
     *         create at 2014年5月19日 下午7:15:16
     */
    public static interface PopupMenuListener {
        void onClick(View v);
    }
}
