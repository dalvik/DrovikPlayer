package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.China;
import com.android.library.ui.utils.China.Province;
import com.android.library.ui.utils.DataResource;
import com.android.library.ui.view.WheelView;
import com.android.library.ui.view.wheelview.TosAdapterView;
import com.android.library.ui.view.wheelview.TosAdapterView.OnItemSelectedListener;
import com.android.library.ui.view.wheelview.WheelBean;
import com.android.library.ui.view.wheelview.WheelTextAdapter;

import java.util.ArrayList;

public class CenterChoosAddressActivity extends BaseCompatActivity implements View.OnClickListener {
    private TextView addressTV;
    private ImageView choosaddressIV;

    public final static String RESULT_ADDRESS = "address";

    private WheelView provinceCodeWv;
    private WheelView cityCodeWv;
    private China china;

    private WheelTextAdapter adapterpro, adaptercit;
    private ArrayList<WheelBean> provinces;
    private ArrayList<WheelBean> citys;

    private UserResp userResp;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_address);
        setTitle(R.string.center_choos_address);
        mUserDao = new UserDao();
        userResp = mUserDao.getUser(this);
        initUI();
        initView();
    }

    private void initUI() {
        //显示选择的居住地
        addressTV = (TextView) findViewById(R.id.addressTv);
        //addressTV.setText(APPLocation.getCityName());
        //省市
        provinceCodeWv = (WheelView) findViewById(R.id.provinceCodeWv);
        cityCodeWv = (WheelView) findViewById(R.id.cityCodeWv);

        provinces = new ArrayList<WheelBean>();
        adapterpro = new WheelTextAdapter(this, 200);
        provinceCodeWv.setAdapter(adapterpro);
        provinceCodeWv.setOnItemSelectedListener(new OnItemSelectedListener() {
            
            public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id){
                Province province = china.provinces.get(position);
                updateCity(province);
            }

            @Override
            public void onNothingSelected(TosAdapterView<?> parent) {
            }
        });
        cityCodeWv.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id){
                addressTV.setText(adaptercit.getItem(position).mText);
            }

            @Override
            public void onNothingSelected(TosAdapterView<?> parent) {
            }
        });

        citys = new ArrayList<WheelBean>();
        adaptercit = new WheelTextAdapter(this, 200);
        cityCodeWv.setAdapter(adaptercit);

        //点击图片 定位地址
        choosaddressIV = (ImageView) findViewById(R.id.choosaddressIV);
        choosaddressIV.setOnClickListener(this);
    }

    //获取数据
    private void initView() {
        china = DataResource.getInstance().getChina();
        for (China.Province province : china.provinces) {
            WheelBean pro = new WheelBean(Integer.valueOf(province.code), province.province, province);
            provinces.add(pro);

        }
        adapterpro.setItems(provinces);
        if(provinces.size()>0){
            Province province = china.provinces.get(0);
            updateCity(province);
        }
    }


    @Override
    public void onClick(View v) {
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
                Intent intent = new Intent();
                intent.putExtra(RESULT_ADDRESS, addressTV.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updateCity(Province province) {
        citys.clear();
        for (China.City city : province.citys) {
            WheelBean cit = new WheelBean(Integer.valueOf(city.code), city.city, city);
            citys.add(cit);
        }
        adaptercit.setItems(citys);
        if(citys.size()>0){
            addressTV.setText(citys.get(0).mText);
        }
    }
}
