package com.android.library.thirdpay;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.dialog.ConfirmDialog;

public class PayActivity extends BaseCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final int PAY_ALI = 1;
    private static final int PAY_WECHAT = 0;
    //private TreatResp treat;
    private ListView listView;
    //private PayAdapter adapter;
    private TextView priceTv;
    private RadioGroup payWayRg;
    private Button payBtn;

    private View IconLl;

    //private PayBean payBean;
    // 1 支付宝   0 微信
    private int payWay = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);
        setTitle(R.string.pay_title);
        setActionBarBackground(R.color.base_content_background);
        initUI();
    }

    @Override
    protected void setBackView(TextView back) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDlg();
            }
        });
    }

    @Override
    protected boolean initIntent() {
        /*payBean = (PayBean) getIntent().getSerializableExtra(ActivityUtil.INTENT_BEAN);
        if (payBean == null || payBean.isEmptyItem()) {
            return false;
        }*/
        return true;
    }

    private void initUI() {
        // 详情列表
        float totalPrice = 0;
        /*adapter = new PayAdapter(activity);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        adapter.setItems(payBean.items);

        for (PayItem item : payBean.items) {
            totalPrice += item.price;
        }*/
        // 总价格
        priceTv = (TextView) findViewById(R.id.priceTv);
        priceTv.setText(getString(R.string.pay_total_price, String.format("%.2f", totalPrice)));
        //支付方式
        payWayRg = (RadioGroup) findViewById(R.id.payWayRg);
        payWayRg.setOnCheckedChangeListener(this);
        //确认支付
        payBtn = (Button) findViewById(R.id.payBtn);
        payBtn.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.weChatRb) {
            payWay = PAY_WECHAT;
        } else if (checkedId == R.id.aliPayRb) {
            payWay = PAY_ALI;
        }
    }

    @Override
    public void onBackPressed() {
        showConfirmDlg();
    }

    private void showConfirmDlg(){
        showConfirmDialog(R.string.pay_confirm_content, R.string.pay_confirm_title,R.string.pay_confirm_ok, R.string.pay_confirm_cancel, new ConfirmDialog.OnResultListener() {
            @Override
            public void onConfirm() {
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.payBtn) {
            switch (payWay) {
                case PAY_WECHAT:
                    doWeChatPay();
                    break;
                case PAY_ALI:
                    doAliPay();
                    break;
            }
        }
    }


    public void doAliPay() {
        /*PayUtil.staryAliPay(activity, payBean.name, payBean.price, payBean.orderId, new AliPay.Callback() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast("支付成功ali");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onPaying() {
                ToastUtils.showToast("支付处理中ali");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed() {
                ToastUtils.showToast("支付失败ali");
            }
        });*/
    }

    private void doWeChatPay() {
        /*PayUtil.staryWeChatPay(activity, payBean.name, payBean.orderId, payBean.price, new WeChatPay.Callback() {
            @Override
            public void onFailed() {
                ToastUtils.showToast("支付失败onFailedWexin");

            }

            @Override
            public void onSuccess() {
                ToastUtils.showToast("支付成功Wexin");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancel() {
                ToastUtils.showToast("支付取消Wexin");
            }

            @Override
            public void onError() {
                ToastUtils.showToast("支付失败onErrorWexin");
            }
        });*/
    }
}
