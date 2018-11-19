package com.drovik.player.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.ui.pager.BasePager;
import com.drovik.player.R;

import java.lang.reflect.Field;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CityFragment extends BasePager {

    private RecyclerView mAllCitiesRecyclerView;
    private TextView mTvLetterOverlay;
    private SideLetterBar mSide;
    private EditText mSearchTextView;
    private ImageButton mActionEmptyBtn;
    private RecyclerView mSearchResultView;
    private LinearLayout mEmptyView;
    private SearchModel mSearchModel;

    private BaseRecyclerAdapter mSearchResultAdapter;

    public CityFragment() {
    }

    public static CityFragment newInstance() {
        CityFragment fragment = new CityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_search, container, false);
        init(view);
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {

    }

    public void onAllCities(final List<CityInfoData> allInfoDatas) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mAllCitiesRecyclerView.setLayoutManager(linearLayoutManager);
        BaseRecyclerAdapter citiesAdapter = new BaseRecyclerAdapter(getActivity());
        citiesAdapter.registerHolder(HeaderHolder.class, new HeaderData());
        citiesAdapter.registerHolder(CityHolder.class, allInfoDatas);
        mAllCitiesRecyclerView.setAdapter(citiesAdapter);

        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(getActivity());
        mSearchResultView.setLayoutManager(resultLayoutManager);
        mSearchResultAdapter = new BaseRecyclerAdapter(getActivity());
        mSearchResultAdapter.registerHolder(CityHolder.class, R.layout.item_city_city);
        mSearchResultView.setAdapter(mSearchResultAdapter);

        mSide.setOverlay(mTvLetterOverlay);
        mSide.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                //linearLayoutManager.scrollToPositionWithOffset(getLetterPosition(letter, allInfoDatas), 0);
            }
        });
    }

    private void init(View view) {
        mAllCitiesRecyclerView = (RecyclerView) view.findViewById(R.id.hot_city_list);
        mTvLetterOverlay = (TextView) view.findViewById(R.id.tv_letter_overlay);
        mSide = (SideLetterBar) view.findViewById(R.id.side);
        mSearchTextView = (EditText) view.findViewById(R.id.searchTextView);
        mActionEmptyBtn = (ImageButton) view.findViewById(R.id.action_empty_btn);
        mSearchResultView = (RecyclerView) view.findViewById(R.id.search_result_view);
        mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view);
        initSearchView();
    }

    private void initSearchView() {
        setCursorDrawable(R.drawable.city_color_cursor_white);

        mSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence text = mSearchTextView.getText();
                boolean hasText = !TextUtils.isEmpty(text);
                if (hasText) {
                    mActionEmptyBtn.setVisibility(VISIBLE);
                } else {
                    mActionEmptyBtn.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    mActionEmptyBtn.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mSearchResultView.setVisibility(View.GONE);
                } else {
                    mActionEmptyBtn.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.VISIBLE);
                    mSearchModel.matchCities(keyword);
                }
            }
        });
    }

    private void setCursorDrawable(int drawable) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mSearchTextView, drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
