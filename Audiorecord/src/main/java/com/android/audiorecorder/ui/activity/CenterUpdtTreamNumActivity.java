package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;

/**
 * 
 * @description: 修改用户唯一号
 * @author: 23536
 * @date: 2016年5月26日 上午10:56:27
 */
public class CenterUpdtTreamNumActivity extends BaseCompatActivity implements View.OnClickListener {
    private ImageView delBtn;
    private EditText treatnumberEt;

    public final static String RESULT_NUMBER = "treamnum";

    //private UserResp userResp;
    //private CenterUpdataManager centerUpdataManager;
    private int updateTreamNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_updata_number);
        setTitle(R.string.center_updata_number);
        //centerUpdataManager = new CenterUpdataManager(this);
        initUI();
    }

    private void initUI() {
        //userResp = AppApplication.getUser();
        //删除
        delBtn = (ImageView) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);
        //请客号
        treatnumberEt = (EditText) findViewById(R.id.treatnumberEt);
        //treatnumberEt.setText(userResp.treamNum);
        treatnumberEt.setSelection(treatnumberEt.length());
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.delBtn:
                treatnumberEt.setText("");
                break;
        }*/
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
    protected boolean onHandleBiz(int what, int result, Object obj) {
        /*if (what == updateTreamNum) {
            switch (result) {
                case CenterUpdataManager.RESULT_SUCCESS:
                    String treamnum = treatnumberEt.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_NUMBER, treamnum);
                    setResult(RESULT_OK, intent);
                    userResp.treamNum = treamnum;
                    AppApplication.setUser(userResp);
                    ToastUtils.showToast(R.string.updata_success);
                    finish();
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }

    @Override
    protected void setOptionView(TextView option) {
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.save);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String treamnum = treatnumberEt.getText().toString();
                //updateTreamNum = centerUpdataManager.ChangeTreamnum(treamnum);
            }
        });
    }
}
