package com.android.library.thirdpay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ActivityUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class DonateActivity extends BaseCompatActivity implements View.OnClickListener{
    
    public static final int DONATE_REQUEST_CODE = 1000;
    
    private ListView mDonateListView;
    private EditText mDonateValue;
    private EditText mDonateMessage;
    private TextView mDonateNumber;
    private String mCurrentAmount = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_ui_donate);
        setTitle(R.string.pay_donate_title);
        setActionBarBackground(R.color.base_content_background);
        initUI();
    }

    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.lib_id_pay_donate_submit){
            Intent intent = new Intent(activity, PayActivity.class);
            ActivityUtils.startActivityForResult(activity, intent, DONATE_REQUEST_CODE);
            DonateActivity.this.finish();
        }
    }

    private void initUI(){
        mDonateValue = (EditText) findViewById(R.id.lib_id_pay_donate_value);
        mDonateValue.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(mCurrentAmount)) {
                    mDonateValue.removeTextChangedListener(this);
                    String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    if (cleanString.equals("") || new BigDecimal(cleanString).toString().equals("0")) {
                        mDonateValue.setText(null);
                        mDonateNumber.setText("");
                    } else {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance(Locale.CHINA).format((parsed / 100));
                        mCurrentAmount = formatted;
                        mDonateValue.setText(formatted);
                        mDonateValue.setSelection(formatted.length());
                        mDonateNumber.setText(formatted);
                    }
                    mDonateValue.addTextChangedListener(this);
                }
            }
        });
        mDonateMessage = (EditText) findViewById(R.id.lib_id_pay_donate_message);
        mDonateNumber = (TextView) findViewById(R.id.lib_id_pay_donate_number);
        findViewById(R.id.lib_id_pay_donate_submit).setOnClickListener(this);
        mDonateListView = (ListView) findViewById(R.id.lib_id_pay_donate_list);
    }
}
