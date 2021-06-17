package com.es.phoneshop.web.service.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosProtectionServiceTest {
    private DosProtectionServiceImpl service = DosProtectionServiceImpl.getInstance();

    @Mock
    HttpSession session;

    @Test
    public void testIsAllowed() {
        when(session.getAttribute("start")).thenReturn(null);

        assertTrue(service.isAllowed("secondIp", session));

        verify(session).setAttribute(eq("start"), anyLong());
    }

    @Test
    public void testIsAllowedWithStart() {
        when(session.getAttribute("start")).thenReturn(System.currentTimeMillis());

        assertTrue(service.isAllowed("ip", session));
    }

    @Test
    public void testIsAllowedReturnFalse() {
        when(session.getAttribute("start")).thenReturn(System.currentTimeMillis());

        for (int i = 0; i < 21; i++) {
            service.isAllowed("ip", session);
        }

        assertFalse(service.isAllowed("ip", session));
    }

    @Test
    public void testIsAllowedWithSleep() throws InterruptedException {
        service.isAllowed("ip", session);
        when(session.getAttribute("start")).thenReturn(System.currentTimeMillis() - 60005);

        assertTrue(service.isAllowed("ip", session));
        verify(session, atLeast(2)).setAttribute(eq("start"), anyLong());
    }
}

