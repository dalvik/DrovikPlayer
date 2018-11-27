package com.drovik.player.weather;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.ui.pager.FileRecordPager;
import com.android.library.ui.pager.BasePager;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.drovik.player.R;
import com.drovik.player.location.LocationService;
import com.silencedut.taskscheduler.TaskScheduler;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CityFragment extends BasePager implements View.OnClickListener {

    private final static int MSG_UPGRADE_TOP_CITY = 1000;

    private RecyclerView mAllCitiesRecyclerView;
    private TextView mTvLetterOverlay;
    private SideLetterBar mSide;
    private EditText mSearchTextView;
    private ImageButton mActionEmptyBtn;
    private RecyclerView mSearchResultView;
    private LinearLayout mEmptyView;
    //private SearchModel mSearchModel;

    private BaseRecyclerAdapter mSearchResultAdapter;

    private ArrayList<CityInfoData> mCitys;
    private ArrayList<CityInfoData> mSearchCitys;
    private String mHotCityJson;
    private ArrayList<Pair<String, String>> mHeaderData;
    private LocationService locationService;

    private String TAG = "CityFragment";

    public CityFragment() {
    }

    public static CityFragment newInstance(ArrayList<CityInfoData> citys, String topCityJson) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ResourceProvider.CITY_DATA, citys);
        args.putString(ResourceProvider.TOP_CITY_JSON, topCityJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHotCityJson = getArguments().getString(ResourceProvider.TOP_CITY_JSON);
            mCitys = getArguments().getParcelableArrayList(ResourceProvider.CITY_DATA);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_search, container, false);
        mSearchCitys = new ArrayList<>();
        init(view);
        onAllCities(mCitys, mHotCityJson);
        locationService = new LocationService(getActivity());
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        LocationClientOption option = locationService.getDefaultLocationClientOption();
        option.setScanSpan(0);
        locationService.setLocationOption(option);
        locationService.start();// 定位SDK
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener);//注销掉监听
        locationService.stop();//停止定位服务
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.action_back) {
            getActivity().finish();
        } else if (i == R.id.action_empty_btn) {
            mSearchTextView.setText("");

        }
    }
    private void onAllCities(final List<CityInfoData> allInfoDatas, String hotCity) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mAllCitiesRecyclerView.setLayoutManager(linearLayoutManager);
        BaseRecyclerAdapter citiesAdapter = new BaseRecyclerAdapter(getActivity());
        HeaderData headerData = new HeaderData();
        if(!TextUtils.isEmpty(hotCity)) {
            headerData.setData(getHotCity(hotCity));
        }
        citiesAdapter.registerHolder(HeaderHolder.class, headerData);
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
                linearLayoutManager.scrollToPositionWithOffset(getLetterPosition(letter, allInfoDatas), 0);
            }
        });
    }

    private void init(View view) {
        view.findViewById(R.id.action_back).setOnClickListener(this);
        view.findViewById(R.id.action_empty_btn).setOnClickListener(this);
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
                    search(text.toString());
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

    private int getLetterPosition(String letter, List<CityInfoData> allInfoDatas) {
        int position = 0;
        for (CityInfoData cityInfoData : allInfoDatas) {
            if (letter.equalsIgnoreCase(cityInfoData.getInitial())) {
                position = allInfoDatas.indexOf(cityInfoData);
            }
        }
        return position;
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                LocationHotCityEvent event = new LocationHotCityEvent(location.getCity());
                EventBus.getDefault().post(event);
            }
        }

    };

    private ArrayList<Pair<String, String>> getHotCity(String hotCityJson) {
        ArrayList<Pair<String, String>> hotCityList = new ArrayList<>();
        try {
            JSONArray hotCities = new JSONArray(hotCityJson);
            int length = hotCities.length();
            for(int i=0; i<length; i++) {
                JSONObject item = hotCities.getJSONObject(i);
                ICityResponse.Data.BasicData temp = new ICityResponse.Data.BasicData(item.toString());
                Pair<String, String> city = new Pair<>(temp.getLocation(), temp.getCid());
                hotCityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hotCityList;
    }

    private void search(final String key) {
        Log.d(TAG, "==> search key: " + key);
        if(mCitys == null || mCitys.size()==0) {
            return;
        }
        TaskScheduler.execute(new Runnable() {
            @Override
            public void run() {
                mSearchCitys.clear();
                for(CityInfoData data:mCitys) {
                    if(data.getCityNamePinyin().startsWith(key) || data.getCityName().startsWith(key) || data.getCityId().startsWith(key)) {
                        mSearchCitys.add(data);
                    }
                }
                mHandler.sendEmptyMessage(MSG_UPGRADE_TOP_CITY);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPGRADE_TOP_CITY:
                    mSearchResultAdapter.setData(mSearchCitys);
                    mSearchResultAdapter.notifyDataSetChanged();
                    break;
                    default:
                        break;
            }
        }
    };
}
