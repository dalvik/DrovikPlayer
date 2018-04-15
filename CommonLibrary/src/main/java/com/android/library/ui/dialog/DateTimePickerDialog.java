package com.android.library.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.library.BaseApplication;
import com.android.library.R;
import com.android.library.ui.view.WheelView;
import com.android.library.ui.view.wheelview.TosAdapterView;
import com.android.library.ui.view.wheelview.TosAdapterView.OnItemSelectedListener;
import com.android.library.ui.view.wheelview.WheelBean;
import com.android.library.ui.view.wheelview.WheelTextAdapter;
import com.android.library.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期选择对话框
 */
public class DateTimePickerDialog extends BaseDialog implements OnClickListener, OnItemSelectedListener {
    public static final String TAG = "DateTimePickerDialog";
    private final static int DATE_COUNT = 3;
    int curDate = 0; // 当前日期索引
    int curHour = 0; // 当前小时索引
    int curMinute = 0; // 当前分钟索引
    ArrayList<WheelBean<Calendar>> dateBeans = new ArrayList<WheelBean<Calendar>>();
    ArrayList<WheelBean<Integer>> hourBeans = new ArrayList<WheelBean<Integer>>();
    ArrayList<WheelBean<Integer>> minuteBeans = new ArrayList<WheelBean<Integer>>();

    /**
     * 时间选择模块 *
     */
    WheelView wheelDate;
    WheelView wheelHour;
    WheelView wheelMinute;
    WheelTextAdapter<Calendar> dateAdapter;
    WheelTextAdapter<Integer> hourAdapter;
    WheelTextAdapter<Integer> minuteAdapter;
    int hour;
    private OnSelectedListener listener;
    private Runnable setHourTask = new Runnable() {
        @Override
        public void run() {
            wheelHour.setSelection(hour);
            hourAdapter.selectPos = hour;
            hourAdapter.notifyDataSetChanged();
        }
    };

    public static DateTimePickerDialog newInstance(OnSelectedListener listener) {
        DateTimePickerDialog dialog = new DateTimePickerDialog();
        dialog.listener = listener;
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, STYLE_NO_TITLE);
        bundle.putInt(ARGS_THEME, R.style.DialogStyle);
        bundle.putBoolean(ARGS_CANCELABLE, false);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_time_dialog, null);

        //日期
        wheelDate = (WheelView) view.findViewById(R.id.date_wheel);

        //小时
        wheelHour = (WheelView) view.findViewById(R.id.hour_wheel);

        //分钟
        wheelMinute = (WheelView) view.findViewById(R.id.minute_wheel);

        Button okBtn = (Button) view.findViewById(R.id.dialog_sure_button);
        okBtn.setOnClickListener(this);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_cancel_button);
        cancelBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 设置适配器
        dateAdapter = new WheelTextAdapter<Calendar>(activity, activity.getResources().getDimensionPixelSize(
                R.dimen.wheel_item_height));
        wheelDate.setAdapter(dateAdapter);
        wheelDate.setSoundEffectsEnabled(true);
        // 设置滑动监听
        wheelDate.setOnItemSelectedListener(this);
        //小时
        hourAdapter = new WheelTextAdapter<Integer>(activity, activity.getResources().getDimensionPixelSize(
                R.dimen.wheel_item_height));
        wheelHour.setAdapter(hourAdapter);
        wheelHour.setSoundEffectsEnabled(true);
        wheelHour.setOnItemSelectedListener(this);
        //分钟
        minuteAdapter = new WheelTextAdapter<Integer>(activity, activity.getResources().getDimensionPixelSize(
                R.dimen.wheel_item_height));
        wheelMinute.setAdapter(minuteAdapter);
        wheelMinute.setSoundEffectsEnabled(true);
        wheelMinute.setOnItemSelectedListener(this);

        initWheelData();
    }

    /**
     * 初始化年月日数据
     */
    private void initWheelData() {
        /** 初始化下标 **/
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(BaseApplication.getRealTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            curDate = 0;
            curHour = 11;
            curMinute = 1; //30分钟
        } else if (hour < 16) {
            curDate = 0;
            curHour = 18;
            curMinute = 0;
        } else {
            curDate = 1;
            curHour = 11;
            curMinute = 1;//30分钟
        }

        initDate();
        initHour();
        initMinute();

    }

    /**
     * 设置日期
     */
    private void initDate() {
        // 先清除所有数据
        dateBeans.clear();
        Calendar calendar = null;
        for (int i = 0; i < DATE_COUNT; i++) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(BaseApplication.getRealTime());
            calendar.add(Calendar.DAY_OF_YEAR, i);
            switch (i) {
                case 0:
                    dateBeans.add(new WheelBean(i, "今天", calendar));
                    break;
                case 1:
                    dateBeans.add(new WheelBean(i, "明天", calendar));
                    break;
                case 2:
                    dateBeans.add(new WheelBean(i, "后天", calendar));
                    break;
            }

        }
        dateAdapter.selectPos = curDate;
        dateAdapter.setItems(dateBeans);
        dateAdapter.notifyDataSetChanged();
        // 设置初始化显示
        wheelDate.setSelection(curDate);
    }

    /**
     * 设置小时
     */
    private void initHour() {
        hourBeans.clear();
        for (int i = 0; i <= 23; ++i) {
            hourBeans.add(new WheelBean(i, String.format("%02d", i), i));
        }
        hourAdapter.selectPos = curHour;
        hourAdapter.setItems(hourBeans);
        hourAdapter.notifyDataSetChanged();
        wheelHour.setSelection(curHour);
    }

    /**
     * 设置分钟
     */
    private void initMinute() {
        minuteBeans.clear();
        int minuteSub = 30;
        // 初始化数据
        for (int i = 0; i < 60; i += minuteSub) {
            minuteBeans.add(new WheelBean(i, String.format("%02d", i), i));
        }
        minuteAdapter.setItems(minuteBeans);
        minuteAdapter.selectPos = curMinute;
        minuteAdapter.notifyDataSetChanged();
        wheelMinute.setSelection(curMinute);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.dialog_sure_button == id) {
            if (listener != null) {
                Calendar calendar = dateBeans.get(curDate).data;
                calendar.set(Calendar.HOUR_OF_DAY, hourBeans.get(curHour).data);
                calendar.set(Calendar.MINUTE, minuteBeans.get(curMinute).data);
                calendar.set(Calendar.SECOND, 0);
                listener.onSelected(calendar);
            }
        } else if (R.id.dialog_cancel_button == id) {
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
        /** 设置滚动后的关联事件 **/
        if (parent == wheelMinute) {
            curMinute = position;
            minuteAdapter.selectPos = curMinute;
            minuteAdapter.notifyDataSetChanged();
        } else if (parent == wheelHour) {
            curHour = position;
            hourAdapter.selectPos = curHour;
            hourAdapter.notifyDataSetChanged();
        } else if (parent == wheelDate) {
            curDate = position;
            dateAdapter.selectPos = curDate;
            dateAdapter.notifyDataSetChanged();
        }

        if (curDate == 0) { //今天  不能选择已经过去的时间
            hour = DateUtil.getHour(new Date(BaseApplication.getRealTime()));
            if (curHour < hour) {
                curHour = hour;
                BaseApplication.handler.removeCallbacks(setHourTask);
                BaseApplication.handler.postDelayed(setHourTask, 100);
            }
        }
    }

    @Override
    public void onNothingSelected(TosAdapterView<?> parent) {
    }

    public static interface OnSelectedListener {
        /**
         * yyyy-MM-dd HH:mm:ss
         *
         * @param calendar
         */
        void onSelected(Calendar calendar);
    }
}
