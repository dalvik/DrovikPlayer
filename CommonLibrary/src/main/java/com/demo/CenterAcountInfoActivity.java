package com.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseSubListActivity;

public class CenterAcountInfoActivity extends BaseSubListActivity implements View.OnClickListener{

    private int whatList;
    //private CenterMyCountManager centerMyCountManager;

    //private CenterAcountInfoAdapter centerAcountInfoAdapter;

    private ListView listView;

    private LinearLayout centerAcountFreezeLayout, centerAcountCashLayout;

    private TextView centerCountContentTv,centerAcountFreezeTv, centerAcountCashTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_acountinfo);
        setTitle(R.string.center_acountinfo_title);

        initUI();
        //centerMyCountManager = new CenterMyCountManager(this);
        showWaitingDialog();
        getList();
    }

    private void initUI(){
        centerCountContentTv = (TextView) findViewById(R.id.centerCountContentTv);
        centerAcountFreezeLayout = (LinearLayout) findViewById(R.id.centerAcountFreezeLayout);
        centerAcountFreezeTv = (TextView) findViewById(R.id.centerAcountFreezeTv);
        centerAcountCashLayout = (LinearLayout) findViewById(R.id.centerAcountCashLayout);
        centerAcountCashTv = (TextView) findViewById(R.id.centerAcountCashTv);
        centerAcountFreezeTv.setText(getString(R.string.center_acountinfo_freeze_btn, 6 + ""));

        centerAcountFreezeLayout.setOnClickListener(this);
        centerAcountCashLayout.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        //centerAcountInfoAdapter = new CenterAcountInfoAdapter(activity);
        //setListView(listView, centerAcountInfoAdapter);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.centerAcountFreezeLayout:
                break;
            case R.id.centerAcountCashLayout:
                break;
        }*/
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (whatList == what) {
            /*switch (result) {
                case CenterMyCountManager.RESULT_SUCCESS:
                    BasePagesResp<CenterMyCountResp> resp = ((BaseData<BasePagesResp<CenterMyCountResp>>) obj).data;
                    if(isFirstPage()){
                        centerAcountInfoAdapter.setItems(resp.rows);
                    }else{
                        centerAcountInfoAdapter.addItems(resp.rows);
                    }
                    setPage(resp.total, resp.length);
                    break;
                default:
                    return false;
            }*/
        }
        return true;
    }

    @Override
    public void reload() {
        getList();
    }

    @Override
    public void onClickItem(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClickItemLong(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void getMoreListItem() {
        getList();
    }

    public void getList() {
        //whatList = centerMyCountManager.getCenterMycountList(listModel.getOffset());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
