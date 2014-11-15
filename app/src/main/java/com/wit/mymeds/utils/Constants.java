package com.wit.mymeds.utils;

import com.wit.mymeds.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diogo on 04/11/2014.
 */
public class Constants {

    public static final int ADD_MED_ACTIVITY = 777;

    public static final Map<Integer, Integer> iconIdMap = new HashMap<Integer, Integer>();

    static {
        iconIdMap.put(R.integer.red_icon_id, R.drawable.pills_red_icon);
        iconIdMap.put(R.integer.blue_icon_id, R.drawable.pills_blue_icon);
        iconIdMap.put(R.integer.grey_icon_id, R.drawable.pills_grey_icon);
    }

    public static final int EDIT_MED_ACTIVITY = 888;
}
