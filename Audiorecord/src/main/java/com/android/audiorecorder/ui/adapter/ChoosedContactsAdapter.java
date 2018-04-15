package com.android.audiorecorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.adapter.BaseListAdapter;
import com.android.library.utils.TextUtils;

import java.util.ArrayList;

public class ChoosedContactsAdapter extends BaseListAdapter<FriendCircleFriendDetailResp> {


    public ChoosedContactsAdapter(BaseCommonActivity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.choosed_contacts_item, null);
            holder = new Holder();
            holder.iconIv = (ImageView) convertView.findViewById(R.id.iconIv);
            holder.nickNameTv = (TextView) convertView.findViewById(R.id.nickNameTv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        FriendCircleFriendDetailResp resp = items.get(position);
        holder.nickNameTv.setText(TextUtils.isEmpty(resp.nickName) ? resp.nickName : resp.nickName);

        //HeadUtil.loadHeadIcon(resp.headIcon, holder.iconIv, resp.sex);
        return convertView;
    }

    public void removeItem(FriendCircleFriendDetailResp contactResp) {
        try {
            items.remove(contactResp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public ArrayList<FriendCircleFriendDetailResp> getItems() {

        return (ArrayList<FriendCircleFriendDetailResp>) items;
    }

    class Holder {

        ImageView iconIv;
        TextView nickNameTv;

    }
}
