package com.android.audiorecorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.activity.FriendCircleContactsActivity;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendSummaryResp;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.adapter.BaseListAdapter;
import com.android.library.utils.TextUtils;

import net.tsz.afinal.FinalBitmap;

public class ContactsAdapter extends BaseListAdapter<FriendCircleFriendSummaryResp> {

    boolean chooseContacts;
    private FinalBitmap mFinalBit;

    public ContactsAdapter(BaseCommonActivity activity, boolean chooseContacts) {
        super(activity);
        this.chooseContacts = chooseContacts;
        mFinalBit = FinalBitmap.create(activity);
        mFinalBit.configLoadfailImage(R.drawable.user_logo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.contacts_item, null);
            holder = new Holder();
            holder.letterTv = (TextView) convertView.findViewById(R.id.letterTv);
            holder.iconIv = (ImageView) convertView.findViewById(R.id.iconIv);
            holder.diamondIv = (ImageView) convertView.findViewById(R.id.diamondIv);
            holder.crownTv = (TextView) convertView.findViewById(R.id.crownTv);
            holder.nicknameTv = (TextView) convertView.findViewById(R.id.nicknameTv);
            holder.line = convertView.findViewById(R.id.line);
            holder.checkedIv = (ImageView) convertView.findViewById(R.id.checkedIv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        FriendCircleFriendSummaryResp resp = items.get(position);
        //加载头像
        //HeadUtil.loadHeadIcon(resp.headIcon, holder.iconIv, resp.sex);
        mFinalBit.display(holder.iconIv, resp.headIcon);
        //index
        if (position == 0) {
            holder.letterTv.setVisibility(View.VISIBLE);
            holder.letterTv.setText(resp.getIndex());
        } else {
            if (items.get(position - 1).getIndex().equals(resp.getIndex())) {
                holder.letterTv.setVisibility(View.GONE);
            } else {
                holder.letterTv.setVisibility(View.VISIBLE);
                holder.letterTv.setText(resp.getIndex());
            }
        }
        //line
        if (items.size() == 1 || position == items.size() - 1) {
            //最后一条数据时或者只有一条数据时不显示line

            holder.line.setVisibility(View.GONE);
        } else {
            if (resp.getIndex().equals(items.get(position + 1).getIndex())) {
                holder.line.setVisibility(View.VISIBLE);
            } else {
                holder.line.setVisibility(View.GONE);
            }
        }
        //选择好友时
        if (chooseContacts) {
            holder.checkedIv.setVisibility(View.VISIBLE);
            if (resp.isChecked) {
                holder.checkedIv.setBackgroundResource(R.drawable.choosed_contacts_checked);
            } else {
                holder.checkedIv.setBackgroundResource(R.drawable.choosed_contacts_normal);
            }
            holder.checkedIv.setTag(resp);
            holder.checkedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendCircleFriendDetailResp resp = (FriendCircleFriendDetailResp) v.getTag();
                    if (resp.isChecked) {
                        resp.isChecked = false;
                        if (activity instanceof FriendCircleContactsActivity) {
                            //((ContactsActivity) activity).removeChoosedItem(resp);
                        }
                    } else {
                        resp.isChecked = true;
                        if (activity instanceof FriendCircleContactsActivity) {
                            //((ContactsActivity) activity).addChoosedItem(resp);
                        }

                    }
                    notifyDataSetChanged();
                }
            });

        }
        holder.letterTv.setText(resp.getIndex());
        holder.nicknameTv.setText(TextUtils.isEmpty(resp.nickName) ? resp.nickName : resp.nickName);
        //皇冠
        /*if (resp.crown > 0) {
            holder.crownTv.setBackgroundResource(R.drawable.nearby_item_crown);
            holder.crownTv.setText("1");
        } else {
            holder.crownTv.setBackgroundResource(R.drawable.nearby_item_pigeon);
            holder.crownTv.setText("" + Math.abs(resp.pigeon));
        }*/
        //土豪


        return convertView;
    }


    class Holder {
        TextView letterTv;
        ImageView iconIv;
        ImageView diamondIv;
        TextView crownTv;
        TextView nicknameTv;
        View line;
        ImageView checkedIv;//表示有没有被选中
    }
}
