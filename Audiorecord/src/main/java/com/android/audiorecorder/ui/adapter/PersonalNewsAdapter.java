package com.android.audiorecorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.PersonalNewsResp;
import com.android.audiorecorder.utils.DateUtil;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.adapter.BaseListAdapter;

public class PersonalNewsAdapter extends BaseListAdapter<PersonalNewsResp> {


	private String mTempDate;
	
    public PersonalNewsAdapter(BaseCommonActivity activity, boolean chooseContacts) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_his_personal_news_item, null);
            holder = new Holder();
            holder.date = (TextView) convertView.findViewById(R.id.his_personal_news_create_time_tv);
            holder.iconIv0 = (ImageView) convertView.findViewById(R.id.personal_news_item_image_0);
            holder.iconIv1 = (ImageView) convertView.findViewById(R.id.personal_news_item_image_1);
            holder.iconIv2 = (ImageView) convertView.findViewById(R.id.personal_news_item_image_2);
            holder.iconIv3 = (ImageView) convertView.findViewById(R.id.personal_news_item_image_3);
            holder.summary = (TextView) convertView.findViewById(R.id.his_personal_news_summary_tv);
            holder.count = (TextView) convertView.findViewById(R.id.his_personal_news_image_tv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        PersonalNewsResp resp = items.get(position);
        
        String date = DateUtil.formatMMDD(resp.newsTime);
        
        if(mTempDate == null || !mTempDate.equalsIgnoreCase(date)) {
        	mTempDate = date;
        	holder.date.setText(date);
        	holder.date.setVisibility(View.VISIBLE);
        }
        
        if(resp.newsType == 0){//text
        	holder.summary.setText(resp.newsContent);
        	holder.summary.setVisibility(View.VISIBLE);
        } else if(resp.newsType == 1){//imge and text
        	
        } else if(resp.newsType == 2) {//share
        	
        }
        /*if(resp.userHeader != null){
        	BitmapMgr.loadViewBitmap(activity, holder.iconIv0, resp.userHeader);
        }*/
        return convertView;
    }


    class Holder {
    	TextView date;
        ImageView iconIv0;
        ImageView iconIv1;
        ImageView iconIv2;
        ImageView iconIv3;
        
        TextView summary;
        TextView count;
        
    }
}
