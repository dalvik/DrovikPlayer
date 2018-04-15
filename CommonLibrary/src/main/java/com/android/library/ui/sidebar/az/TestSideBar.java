/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: TestSideBar.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年5月10日 下午3:38:17 
 */
package com.android.library.ui.sidebar.az;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.R;
import com.android.library.ui.sidebar.az.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSideBar extends Activity {

    private ListView mListView;
    private SideBar mSideBar;
    private TextView mSelectTextView;
    private List<SortModel> mDataList;
    private SortAdapter mAdapter;
    private CharacterParser mCharacterParser;
    private PinyinComparator mPinyinComparator;
    private ClearEditText mClearEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_lib_sidebar);
        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mSelectTextView = (TextView) findViewById(R.id.dialog);
        mListView = (ListView) findViewById(R.id.list_view);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mSideBar.setTextView(mSelectTextView);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) mAdapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        String[] data = {"王力宏", "汪峰", "王菲","那英", "张伟", "张学友","龚琳娜", "陶喆", "陈丽丽"};
        mCharacterParser = CharacterParser.getInstance();
        mPinyinComparator = new PinyinComparator();
        mDataList = filledData(data);
        // 根据a-z进行排序源数据
        Collections.sort(mDataList, mPinyinComparator);
        mAdapter = new SortAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
    }
    
    /**
     * 为ListView填充数据
     * 
     * @param data
     * @return
     */
    private List<SortModel> filledData(String[] data) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < data.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(data[i]);
            // 汉字转换成拼音
            String pinyin = mCharacterParser.getSelling(data[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }
    
    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mDataList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : mDataList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || mCharacterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, mPinyinComparator);
        mAdapter.updateListView(filterDateList);
    }
}
