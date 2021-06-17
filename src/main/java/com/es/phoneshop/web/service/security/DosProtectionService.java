package com.es.phoneshop.web.service.security;

import javax.servlet.http.HttpSession;

public interface DosProtectionService {
    boolean isAllowed(String ip, HttpSession session);
}
