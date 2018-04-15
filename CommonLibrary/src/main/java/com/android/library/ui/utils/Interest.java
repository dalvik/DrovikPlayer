package com.android.library.ui.utils;


import java.util.ArrayList;
import java.util.HashMap;

public class Interest {
    public ArrayList<String> life;
    public ArrayList<String> sport;
    public ArrayList<String> ent;
    public ArrayList<String> art;


    public HashMap<String, Long> getInterestValue() {
        HashMap<String, Long> interests = new HashMap<String, Long>();
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.addAll(life);
        itemList.addAll(sport);
        itemList.addAll(ent);
        itemList.addAll(art);
        int size = itemList.size();
        for (int i = 0; i < size; i++) {
            interests.put(itemList.get(i), 1l << i);
        }
        return interests;
    }

    public HashMap<Long, String> getInterestName() {
        HashMap<Long, String> interests = new HashMap<Long, String>();
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.addAll(life);
        itemList.addAll(sport);
        itemList.addAll(ent);
        itemList.addAll(art);
        int size = itemList.size();
        for (int i = 0; i < size; i++) {
            interests.put(1l << i, itemList.get(i));
        }
        return interests;
    }

    public String getInterest(long interest) {
        Interest interests = DataResource.getInstance().getInterest();
        HashMap<Long, String> itemMap = interests.getInterestName();
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (long v = 1; v != 0 && interest >= v; v = v << 1) {
            if ((interest & v) == v) {
                count++;
                if (count == 5) {
                    break;
                }
                sb.append(itemMap.get(v)).append("/");
            }
        }
        return sb.substring(0, sb.length() - 1) + (count == 5 ? "..." : "");
    }

}
