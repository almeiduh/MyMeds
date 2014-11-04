package com.wit.mymeds.utils;

import com.wit.mymeds.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diogo on 04/11/2014.
 */
public class Constants {

    public static final Map<Integer, Integer> iconIdMap = new HashMap<Integer, Integer>();

    static {
        iconIdMap.put(1, R.drawable.pills_red_icon);
        iconIdMap.put(2, R.drawable.pills_blue_icon);
        iconIdMap.put(3, R.drawable.pills_grey_icon);
    }
}
