package com.android.audiorecorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.adapter.BaseListAdapter;

public class MessageListAdapter extends BaseListAdapter<Object> {

    public MessageListAdapter(BaseCommonActivity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_message_list_item, null);
            holder = new Holder();
            holder.oldTv = (TextView) convertView.findViewById(R.id.oldTv);
            holder.newTv = (TextView) convertView.findViewById(R.id.newTv);
            holder.timeLl = (LinearLayout) convertView.findViewById(R.id.timeLl);
            /*holder.addrTv = (TextView) convertView.findViewById(R.id.addrTv);
            holder.ageTv = (TextView) convertView.findViewById(R.id.ageTv);
            holder.allowanceLl = (LinearLayout) convertView.findViewById(R.id.allowanceLl);
            holder.allowanceTv = (TextView) convertView.findViewById(R.id.allowanceTv);
            holder.billTv = (TextView) convertView.findViewById(R.id.billTv);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.distanceTv);
            holder.nicknameTv = (TextView) convertView.findViewById(R.id.nicknameTv);
            holder.iconIv = (ImageView) convertView.findViewById(R.id.iconIv);
            holder.sexIv = (ImageView) convertView.findViewById(R.id.sexIv);
            holder.crownIv = (ImageView) convertView.findViewById(R.id.crownIv);
            holder.themeTv = (TextView) convertView.findViewById(R.id.themeTv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
            holder.typeIv = (ImageView) convertView.findViewById(R.id.typeIv);*/
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        /*TreatResp treat = items.get(position);
        //newTip oldTip
        switch (treat.tipType) {
            case TreatResp.TIP_TYPE_NEW:
                holder.oldTv.setVisibility(View.GONE);
                holder.newTv.setText(activity.getString(R.string.treatList_new_tip, 5, "美食,KTV"));
                break;
            case TreatResp.TIP_TYPE_OLD:
                holder.newTv.setVisibility(View.GONE);
                holder.oldTv.setText(R.string.treatList_old_tip);
                break;
        }

        //类型
        switch (treat.bizCategory) {
            case RespConstants.BizCategory.CATEGORY_BAR:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_bar);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_bar_bg);
                break;
            case RespConstants.BizCategory.CATEGORY_KTV:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_ktv);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_ktv_bg);
                break;
            case RespConstants.BizCategory.CATEGORY_COFFEE:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_coffee);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_coffee_bg);
                break;
            case RespConstants.BizCategory.CATEGORY_FOOD:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_food);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_food_bg);
                break;
            case RespConstants.BizCategory.CATEGORY_MOVIE:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_movie);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_movie_bg);
                break;
            default:
                holder.typeIv.setBackgroundResource(R.drawable.treat_list_item_other);
                holder.timeLl.setBackgroundResource(R.drawable.treat_list_item_other_bg);
                break;
        }
        // 标题
        holder.themeTv.setText(treat.theme);
        //时间
        holder.timeTv.setText(DateFormat.getTreatListDate(treat.activityTime));
        //付款
        String bill = activity.getString(R.string.treatList_bill_male_you);
        switch (treat.bill) {
            case RespConstants.Bill.BILL_AA:
                bill = activity.getString(R.string.treatList_bill_aa);
                break;
            case RespConstants.Bill.BILL_MALE_AA:
                bill = activity.getString(R.string.treatList_bill_male_aa);
                break;
            case RespConstants.Bill.BILL_YOU:
                bill = activity.getString(R.string.treatList_bill_male_you);
                break;
            case RespConstants.Bill.BILL_ME:
                bill = activity.getString(R.string.treatList_bill_me);
                break;
        }
        // 类型
        String type = "";
        switch (treat.smallType) {
            case RespConstants.TreatSmallType.OFFLINE_DATING:
                type = activity.getString(R.string.treat_type_dating);
                break;
            case RespConstants.TreatSmallType.OFFLINE_SMALLPARTY:
                type = activity.getString(R.string.treat_type_smallParty);
                break;
            case RespConstants.TreatSmallType.OFFLINE_BIGPARTY:
                type = activity.getString(R.string.treat_type_party);
                break;
        }
        holder.billTv.setText(activity.getString(R.string.treatList_bill, bill, type));
        //地点
        holder.addrTv.setText(treat.bizAddr);
        //距离
        holder.distanceTv.setText(MapUtil.getDistanceFormat(treat.bizLat, treat.bizLng));
        //补贴
        if (treat.subsidy > 0) {
            holder.allowanceTv.setText("" + treat.subsidy);
            holder.allowanceLl.setVisibility(View.VISIBLE);
        } else {
            holder.allowanceLl.setVisibility(View.GONE);
        }
        //头像 性别
        final UserResp user = treat.users;
        HeadUtil.loadHeadIcon(user.headIcon,holder.iconIv,user.sex);
        holder.iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.gotoHisPersonalCeneterActivity(activity,user.userId);
            }
        });
        //皇冠
        if (user.crown > 0) {
            holder.crownIv.setBackgroundResource(R.drawable.treat_list_item_crown);
        } else {
            holder.crownIv.setBackgroundResource(R.drawable.treat_list_item_pigeon);
        }
        //昵称
        holder.nicknameTv.setText(user.nickName);
        //年龄
        String age = DateFormat.getAge(new Date(user.birthday)) + "岁";
        String starsign = DateFormat.getStarSign(new Date(user.birthday));
        holder.ageTv.setText(activity.getString(R.string.treatList_age, age, starsign));*/
        return convertView;
    }

    class Holder {
        public TextView newTv;
        public TextView oldTv;
        public TextView themeTv;
        public ImageView typeIv;
        public LinearLayout timeLl;
        public LinearLayout allowanceLl;
        public TextView timeTv;
        public TextView billTv;
        public TextView addrTv;
        public TextView distanceTv;
        public TextView allowanceTv;
        public ImageView iconIv;
        public ImageView crownIv;
        public TextView nicknameTv;
        public TextView ageTv;
        public ImageView sexIv;
    }
}
