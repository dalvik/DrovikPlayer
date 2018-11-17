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

import butterknife.BindView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CityFragment extends BasePager {

    @BindView(R2.id.hot_city_list)
    RecyclerView mAllCitiesRecyclerView;
    @BindView(R2.id.tv_letter_overlay)
    TextView mTvLetterOverlay;
    @BindView(R2.id.side)
    SideLetterBar mSide;
    @BindView(R2.id.searchTextView)
    EditText mSearchTextView;
    @BindView(R2.id.action_empty_btn)
    ImageButton mActionEmptyBtn;
    @BindView(R2.id.search_result_view)
    RecyclerView mSearchResultView;
    @BindView(R2.id.empty_view)
    LinearLayout mEmptyView;
    private SearchModel mSearchModel;


    private BaseRecyclerAdapter mSearchResultAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_search, container, false);
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


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAllCitiesRecyclerView.setLayoutManager(linearLayoutManager);
        BaseRecyclerAdapter citiesAdapter = new BaseRecyclerAdapter(this);
        citiesAdapter.registerHolder(HeaderHolder.class, new HeaderData());
        citiesAdapter.registerHolder(CityHolder.class, allInfoDatas);
        mAllCitiesRecyclerView.setAdapter(citiesAdapter);

        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        mSearchResultView.setLayoutManager(resultLayoutManager);
        mSearchResultAdapter = new BaseRecyclerAdapter(this);
        mSearchResultAdapter.registerHolder(CityHolder.class, R.layout.city_item_city);
        mSearchResultView.setAdapter(mSearchResultAdapter);

        mSide.setOverlay(mTvLetterOverlay);
        mSide.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                linearLayoutManager.scrollToPositionWithOffset(getLetterPosition(letter, allInfoDatas), 0);
            }
        });
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
