package com.demo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.utils.DateUtil;

import java.util.Calendar;

/**
 * 
 * @description: 更新生日
 * @author: 23536
 * @date: 2016年5月26日 上午11:11:01
 */
public class CenterUpdtBirthdayActivity extends BaseCompatActivity {

    public final static String RESULT_BIRTHDAY = "birthday";
    //private UserResp userResp;

    private DatePicker birthdayDP;
    private TextView ageTV;
    private TextView constellationtv;
    private TextView birthdayTv;
    private ImageView constellationIv;

    int[] constellation = new int[]{
            R.drawable.center_constellation_aquarius,
            R.drawable.center_constellation_pisces,
            R.drawable.center_constellation_aries,
            R.drawable.center_constellatio_taurus,
            R.drawable.center_constellation_gemini,
            R.drawable.center_constellation_cancer,
            R.drawable.center_constellatio_leo,
            R.drawable.center_constellation_virgo,
            R.drawable.center_constellation_libra,
            R.drawable.center_constellation_scorpio,
            R.drawable.center_constellation_sagittarius,
            R.drawable.center_constellation_capricornus};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_birthday);
        setTitle(R.string.center_updata_birthday);
        initUI();
        initView();
    }

    private void initUI() {
        //userResp = AppApplication.getUser();
        birthdayTv = (TextView) findViewById(R.id.birthdayTv);//生日
        //birthdayTv.setText(DateUtil.formatYMD(new Date(userResp.birthday)));


        ageTV = (TextView) findViewById(R.id.ageTv);//年龄
        constellationtv = (TextView) findViewById(R.id.constellationTV);//星座
        constellationIv = (ImageView) findViewById(R.id.constellationIV);//星座图片

        birthdayDP = (DatePicker) findViewById(R.id.birthdayDP);//日历选择
    }

    private void initView() {
        //展示年龄、星座
        /*int age = DateFormat.getAge(new Date(userResp.birthday));
        if (age >= 0) {
            ageTV.setText(activity.getString(R.string.center_updata_age, age));
        }

        int index = DateFormat.getStarSignIndex(new Date(userResp.birthday));
        constellationtv.setText(DateFormat.constellationArr[index]);
        constellationIv.setBackgroundResource(constellation[index]);*/

        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(userResp.birthday);
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH);
        int days = calendar.get(Calendar.DAY_OF_MONTH);

        birthdayDP.init(years, months, days, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                updateDisplay(year, monthOfYear, dayOfMonth);
            }
        });
    }

    //设定时间显示的方法
    private void updateDisplay(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        String birthday = DateUtil.getFormatDate(calendar.getTime(), activity.getString(R.string.center_updata_birthday_pattern));
        birthdayTv.setText(birthday);

        //展示年龄、星座
        /*int age = DateFormat.getAge(calendar.getTime());
        if (age >= 0) {
            ageTV.setText(activity.getString(R.string.center_updata_age, age));
        }
        int index = DateFormat.getStarSignIndex(calendar.getTime());
        constellationtv.setText(DateFormat.constellationArr[index]);
        constellationIv.setBackgroundResource(constellation[index]);*/
    }

    @Override
    protected void setBackView(TextView back) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void setOptionView(TextView option) {
        super.setOptionView(option);
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.save);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthday = birthdayTv.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(RESULT_BIRTHDAY, birthday);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
