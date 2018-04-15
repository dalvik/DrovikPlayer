package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.ChoosedContactsAdapter;
import com.android.audiorecorder.ui.adapter.ContactsAdapter;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendSummaryResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.FriendManager;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.sidebar.az.SideBar;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.ui.view.HorizontalListView;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendCircleContactsActivity extends BaseCompatActivity {//implements RongCloudEvent.HiddenMessageCallback {

    private static ArrayList<FriendCircleFriendSummaryResp> contacts;
    private ContactsAdapter adapter;
    private ListView listView;
    //private DBManager dbManager;
    private FriendManager mFriendManager;
    private SideBar sidebar;
    private HashMap<String, Integer> indexMap = new HashMap<String, Integer>();
    private TextView tipTv;
    private boolean chooseContacts = false;
    private HorizontalListView choosedListView;//选择好友时选中的好友列表
    private ChoosedContactsAdapter choosedContactsAdapter;
    private TextView noCheckedTv;
    private RelativeLayout choosedListViewLayout;
    public static  boolean isNeedRefreshContacts=false;

    private UserDao mDao;
    private UserResp mUserResp;
    
    private int mWhatFriendList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendManager = new FriendManager(this);
        setContentView(R.layout.contacts);
        if (chooseContacts) {
            setTitle(getString(R.string.contacts_title_choose_contacts));
        } else {
            setTitle(R.string.contacts_title);
        }
        loadData();
        initUI();
        isNeedRefreshContacts=false;
       // RongCloudEvent.addHiddenMessageCallback(this);
    }

    @Override
    protected boolean initIntent() {
        chooseContacts = getIntent().getBooleanExtra(ActivityUtil.CHOOSE_CONTACTS, false);
        return true;
    }

    private void loadData() {
        //dbManager.getAllContacts();
        mDao = new UserDao();
        mUserResp = mDao.getUser(this);
        if(!chooseContacts){
            mWhatFriendList = mFriendManager.getFriendList(mUserResp.userCode, 0, 500, 0);
        }
    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.listView);
        if (chooseContacts) {
            choosedListViewLayout = (RelativeLayout) findViewById(R.id.choosedListViewLayout);
            choosedListViewLayout.setVisibility(View.VISIBLE);
            choosedListView = (HorizontalListView) findViewById(R.id.choosedListView);
            choosedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    contacts.get(contacts.indexOf(choosedContactsAdapter.getItems().get(position))).isChecked = false;
                    adapter.notifyDataSetChanged();
                    choosedContactsAdapter.removeItem(position);
                    choosedContactsAdapter.notifyDataSetChanged();
                    setTitle(getString(R.string.checked_count, choosedContactsAdapter.getItems().size()));
                    if (choosedContactsAdapter.getItems().size() == 0) {
                        noCheckedTv.setVisibility(View.VISIBLE);
                    }
                }
            });
            noCheckedTv = (TextView) findViewById(R.id.noCheckedTv);
            noCheckedTv.setVisibility(View.VISIBLE);
            choosedContactsAdapter = new ChoosedContactsAdapter(activity);
            choosedListView.setAdapter(choosedContactsAdapter);
        }
        adapter = new ContactsAdapter(activity, chooseContacts);
        adapter.setItems(contacts);
        listView.setAdapter(adapter);
        tipTv = (TextView) findViewById(R.id.tipUpdateTv);
        sidebar = (SideBar) findViewById(R.id.sidebar);
        sidebar.setTextView(tipTv);
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                Integer positon = indexMap.get(s);
                if (positon != null)
                    listView.setSelection(positon);
            }
        });
    }

    @Override
    protected void setOptionView(TextView option) {
        if (chooseContacts) {
            option.setText(R.string.contacts_choose_finish);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //选择好友完成
                    if (choosedContactsAdapter.getItems().size() == 0) {
                        ToastUtils.showToast(getString(R.string.not_checked_contacts));
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(ActivityUtil.CONTACTS_LIST, choosedContactsAdapter.getItems());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            option.setBackgroundResource(R.drawable.contacts_add);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.gotoContactsAddActivity(activity, mUserResp.userCode);
                }
            });
        }
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        /*if (what == DBManager.WHAT_CONTACT_ALL) {
            contacts = (ArrayList<ContactResp>) obj;
            //点击右边sideBar时跳转的项positon
            initSidarPostion();
            adapter.setItems(contacts);
        }*/
        if (what == mWhatFriendList) {
            BaseData<ArrayList<FriendCircleFriendSummaryResp>> data = (BaseData<ArrayList<FriendCircleFriendSummaryResp>>) obj;
            contacts = data.data;
            //contacts = (ArrayList<ContactResp>) obj;
            //点击右边sideBar时跳转的项positon
            initSidarPostion();
            adapter.setItems(contacts);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNeedRefreshContacts) {
            loadData();
            isNeedRefreshContacts=false;
        }
    }

    private void initSidarPostion() {
        for (FriendCircleFriendSummaryResp contact : contacts) {
            String index = contact.getIndex();
            if (!indexMap.containsKey(index)) {
                indexMap.put(index, contacts.indexOf(contact));
            }else{
                indexMap.remove(index);
                indexMap.put(index, contacts.indexOf(contact));
            }
        }
    }

    /*@Override
    public void onClickItemLong(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClickItem(AdapterView<?> parent, View view, int position, long id) {
        ActivityUtil.gotoHisPersonalCeneterActivity(activity, adapter.getItem(position).userCode);
    }

    @Override
    public void getMoreListItem() {

    }*/


    public static ArrayList<FriendCircleFriendSummaryResp> getItmes() {
        return contacts;
    }

    /**
     * 添加选中的项到列表中
     *
     * @param contactResp
     */
    public void addChoosedItem(FriendCircleFriendDetailResp contactResp) {
        /*if (noCheckedTv.getVisibility() == View.VISIBLE) {
            noCheckedTv.setVisibility(View.GONE);
            choosedListView.setVisibility(View.VISIBLE);
        }
        choosedContactsAdapter.addItem(contactResp);
        setTitle(getString(R.string.checked_count, choosedContactsAdapter.getItems().size()));*/


    }

    /**
     * 移除选中的项
     *
     * @param contactResp
     */
    public void removeChoosedItem(FriendCircleFriendDetailResp contactResp) {
        /*choosedContactsAdapter.removeItem(contactResp);
        setTitle(getString(R.string.checked_count, choosedContactsAdapter.getItems().size()));
        if (choosedContactsAdapter.getItems().size() == 0) {
            noCheckedTv.setVisibility(View.VISIBLE);
        }*/
    }

    /*@Override
    public void callback(HiddenMessage message) {
        if (message.operateType == Constants.SYSTEM_MESSAGE.OPTION_TYPE.DELETE_FRIEND) {
            isNeedRefreshContacts=true;
            onResume();
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //RongCloudEvent.removeHidenMessageCallback(this);
    }
}
