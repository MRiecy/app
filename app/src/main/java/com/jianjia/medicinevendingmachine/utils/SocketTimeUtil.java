package com.jianjia.medicinevendingmachine.utils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SocketTimeUtil {
    private Map<String, Long> addMap;
    private Map<String, Long> resultMap;

    public SocketTimeUtil() {
        addMap = new HashMap<>();
        resultMap = new HashMap<>();
    }

    public void clock(String key) {
        long time = System.currentTimeMillis() + 15 * 1000;
        addMap.put(key, time);
    }

    public void remove(String key) {
        addMap.remove(key);
    }

    @Nullable
    public List<String> isAllTimeOut() {
        resultMap.putAll(addMap);
        addMap.clear();
        ArrayList<String> lStrings = new ArrayList<>();
        if (resultMap.size() != 0) {
            Iterator<Map.Entry<String, Long>> lIterator = resultMap.entrySet().iterator();
            while (lIterator.hasNext()) {
                Map.Entry<String, Long> lNext = lIterator.next();
                long lL = System.currentTimeMillis();
                long lValue = (long) lNext.getValue();
                if (lValue <= lL) {
                    lStrings.add(lNext.getKey());
                }
            }
            resultMap.clear();
        }
        return lStrings;
    }
}

