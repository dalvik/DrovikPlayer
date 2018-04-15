package com.android.library.ui.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.BuildConfig;
import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibCheckListActivity extends BaseCompatActivity implements OnItemClickListener {

    public static final int CODE_REQUEST = 10001;
    public static final String LIB_EXTRA_CHECK_LIST_DATA = "check_data";
    public static final String LIB_EXTRA_CHECKED_LIST_DATA = "checked_data";
    
    private ArrayList<LibCheckListCheckedImg> mCheckedList;
	private LibCheckListHorizontalListView mCheckHorizontalListView;
	private LibCheckListCheckedImgAdapter mCheckedAdapter;
	private TextView mSubmmitButton;
	
	private LibCheckListAdapter mCheckAdapter;
	private ListView mCheckListView;
	private List<LibCheckListCheckedImg> mCheckList;
	
	private boolean hasMeasured = false;
	private int maxCount = 5;// 这里要是可以根据屏幕计算出来最多容纳多少item就好了.没有找到合适的方法.
	private int countChecked = 0;// 得到选中的数量.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.Ext.init(getApplication());
        x.Ext.setDebug(BuildConfig.DEBUG);
		setContentView(R.layout.lib_layout_checklist_main_demo);
		mCheckHorizontalListView = (LibCheckListHorizontalListView) this.findViewById(R.id.lib_id_checkedlist_image_list);
		mSubmmitButton = (TextView) findViewById(R.id.lib_id_checkedlist_sure_button);
		mCheckListView = (ListView) this.findViewById(R.id.lib_id_checklist_listview);
		Intent intent = getIntent();
        if(intent != null){
            mCheckList = intent.getParcelableArrayListExtra(LIB_EXTRA_CHECK_LIST_DATA);
            if(mCheckList != null && mCheckList.size()>0){
                init();
            } else {
                LibCheckListActivity.this.finish();
            }
        } else {
            LibCheckListActivity.this.finish();
        }
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long index) {
		List<HashMap<String, Object>> list = mCheckAdapter.getData();
		Map<Integer, Boolean> checkedMap = mCheckAdapter.getCheckedMap();
		HashMap<String, Object> map = list.get(position);
		RelativeLayout layout = (RelativeLayout) view;
		LinearLayout layout2 = (LinearLayout) layout.getChildAt(1);
		String id = "" + map.get("id");
		String name = "" + map.get("name");
		String image = "" + map.get("image");

		CheckBox ckb = (CheckBox) layout2.getChildAt(0);
		if (ckb.isChecked()) {
			checkedMap.put(Integer.parseInt(id), false);
			removeByName(name);
			mCheckedAdapter.notifyDataSetChanged();
		} else {
			mCheckHorizontalListView.setSelection(mCheckAdapter.getCount()-1);
			checkedMap.put(Integer.parseInt(id), true);
			addToCheckedList(name, id, image);
			mCheckedAdapter.notifyDataSetChanged();
			mCheckHorizontalListView.setSelection(mCheckedAdapter.getCount()-1);
		}
		mCheckAdapter.notifyDataSetChanged();
	}

	private void init(){
	    mCheckAdapter = new LibCheckListAdapter(LibCheckListActivity.this, fillData());
        mCheckListView.setAdapter(mCheckAdapter);
        mCheckListView.setOnItemClickListener(this);
        mCheckedList = new ArrayList<LibCheckListCheckedImg>();
        initCheckedList();
        mCheckedAdapter = new LibCheckListCheckedImgAdapter(LibCheckListActivity.this, mCheckedList);
        mCheckHorizontalListView.setAdapter(mCheckedAdapter); 
        mCheckHorizontalListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LibCheckListCheckedImg img = mCheckedList.get(position);
                Map<Integer, Boolean> checkedMap = mCheckAdapter.getCheckedMap();
                if (!"default".equals(img.getId())) {
                    checkedMap.put(Integer.parseInt(img.getId()), false);
                    mCheckAdapter.notifyDataSetChanged();
                    removeByName(img.getName());
                }
                mCheckedAdapter.notifyDataSetChanged();
            }
            
        });
        mSubmmitButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int index = findCheckedPositionByName("default");
                if (index != -1){
                    mCheckedList.remove(index);
                }
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(LIB_EXTRA_CHECKED_LIST_DATA, mCheckedList);
                i.putExtras(bundle);
                LibCheckListActivity.this.setResult(RESULT_OK, i);
                LibCheckListActivity.this.finish();
            }
        });
	}
	
	private List<HashMap<String, Object>> fillData() {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for(LibCheckListCheckedImg img:mCheckList){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", img.getId());
            map.put("name", img.getName());
            map.put("image", img.getImage());
            data.add(map);
        }
        return data;
    }
	
    private int findCheckedPositionByName(String name) {
        int i = -1;
        for (LibCheckListCheckedImg m : mCheckedList) {
            i++;
            if (name.equals(m.getName())) {
                return i;
            }
        }
        return -1;
    }

	private void initCheckedList() {
        LibCheckListCheckedImg img = new LibCheckListCheckedImg();
        img.setId("default");
        img.setName("default");
        img.setImage("none");
        mCheckedList.add(img);
    }

    private void removeByName(String _name) {
        mCheckedList.remove(findCheckedPositionByName(_name));
        if (mCheckedList.size() == 4) {
            if (findCheckedPositionByName("default") == -1) {
                LibCheckListCheckedImg img = new LibCheckListCheckedImg();
                img.setId("default");
                img.setName("default");
                img.setImage("none");
                mCheckedList.add(img);
            }
        }
        mSubmmitButton.setText(getText(R.string.lib_string_checkedlist_sure_button) +"(" + --countChecked + ")");
    }

    private void addToCheckedList(String _name, String _id, String _image) {
        LibCheckListCheckedImg map2 = new LibCheckListCheckedImg();
        map2.setName(_name);
        map2.setId(_id);
        map2.setImage(_image);
        if (mCheckedList.size() < maxCount) {
            mCheckedList.add(mCheckedList.size() - 1, map2);
        } else {
            mCheckedList.add(map2);
        }

        if (mCheckedList.size() == maxCount + 1) {
            if (findCheckedPositionByName("default") != -1){
                mCheckedList.remove(findCheckedPositionByName("default"));
            }
        } else if (mCheckedList.size() == maxCount - 1) {
            if (findCheckedPositionByName("default") == -1) {
                LibCheckListCheckedImg img = new LibCheckListCheckedImg();
                img.setId("default");
                img.setName("default");
                img.setName("none");
                mCheckedList.add(img);
            }
        }
        mSubmmitButton.setText(getText(R.string.lib_string_checkedlist_sure_button) + "(" + ++countChecked + ")");
    }

}