package com.android.audiorecorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.PersonalNewsResp;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.adapter.BaseListAdapter;
import com.android.library.ui.manager.BitmapMgr;

public class PersonalDetailMessageAdapter extends BaseListAdapter<PersonalNewsResp> {


    public PersonalDetailMessageAdapter(BaseCommonActivity activity, boolean chooseContacts) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_personal_detail_message_item, null);
            holder = new Holder();
            holder.iconIv = (ImageView) convertView.findViewById(R.id.iconIv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        PersonalNewsResp resp = items.get(position);
        if(resp.userHeader != null){
        	BitmapMgr.loadViewBitmap(activity, holder.iconIv, resp.userHeader);
        }
        return convertView;
    }


    class Holder {
        ImageView iconIv;
    }
}
