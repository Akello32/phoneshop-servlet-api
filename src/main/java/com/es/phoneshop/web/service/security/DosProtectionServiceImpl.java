package com.es.phoneshop.web.service.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DosProtectionServiceImpl implements DosProtectionService {
    private static final long THRESHOLD = 20;

    private static final long MINUTES = 60000;

    private final Map<String, Map<Long, Long>> countMap = new ConcurrentHashMap<>();

    private DosProtectionServiceImpl() {
    }

    private static class SingletonHolder {
        public static final DosProtectionServiceImpl INSTANCE = new DosProtectionServiceImpl();
    }

    public static DosProtectionServiceImpl getInstance() {
        return DosProtectionServiceImpl.SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        Map<Long, Long> timeCountMap = countMap.get(ip);
        Long count;
        Long start;
        if (timeCountMap == null) {
            timeCountMap = new ConcurrentHashMap<>();
            start = System.currentTimeMillis();
            count = 1L;
        } else {
            start = timeCountMap.keySet().iterator().next();
            count = timeCountMap.values().iterator().next();
            if (System.currentTimeMillis() < start + MINUTES) {
                if (count > THRESHOLD) {
                    return false;
                }
                count++;
            } else {
                count = 1L;
                start = System.currentTimeMillis();
            }
        }

        timeCountMap.clear();
        timeCountMap.put(start, count);
        countMap.put(ip, timeCountMap);

        return true;
    }
}