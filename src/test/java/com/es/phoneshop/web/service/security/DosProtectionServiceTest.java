package com.es.phoneshop.web.service.security;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosProtectionServiceTest {
    private DosProtectionServiceImpl service = DosProtectionServiceImpl.getInstance();

    @Test
    public void testIsAllowed() {
        assertTrue(service.isAllowed("secondIp"));
    }

    @Test
    public void testIsAllowedWithStart() {
        assertTrue(service.isAllowed("ip"));
        assertTrue(service.isAllowed("ip"));
    }

    @Test
    public void testIsAllowedReturnFalse() {
        for (int i = 0; i < 21; i++) {
            service.isAllowed("ip");
        }

        assertFalse(service.isAllowed("ip"));
    }

    @Test
    public void testIsAllowedWithSleep() throws InterruptedException {
        service.isAllowed("ip");

        TimeUnit.SECONDS.sleep(61);

        assertTrue(service.isAllowed("ip"));
    }
}

