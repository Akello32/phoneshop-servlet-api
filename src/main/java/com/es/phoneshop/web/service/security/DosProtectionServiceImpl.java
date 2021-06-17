package com.es.phoneshop.web.service.security;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DosProtectionServiceImpl implements DosProtectionService {
    private static final long THRESHOLD = 20;

    private Map<String, Long> countMap = new ConcurrentHashMap<>();

    private DosProtectionServiceImpl() {
    }

    private static class SingletonHolder {
        public static final DosProtectionServiceImpl INSTANCE = new DosProtectionServiceImpl();
    }

    public static DosProtectionServiceImpl getInstance() {
        return DosProtectionServiceImpl.SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip, HttpSession session) {
        Long start = (Long) session.getAttribute("start");
        Long count = countMap.get(ip);
        if (count == null) {
            if (start == null) {
                start = System.currentTimeMillis();
                session.setAttribute("start", start);
            }
            count = 1L;
        } else {
            if (System.currentTimeMillis() < start + 60*1000) {
                if (count > THRESHOLD) {
                    return false;
                }
                count++;
            } else {
                count = 1L;
                session.setAttribute("start", System.currentTimeMillis());
            }
        }

        countMap.put(ip, count);
        return true;
    }
}
