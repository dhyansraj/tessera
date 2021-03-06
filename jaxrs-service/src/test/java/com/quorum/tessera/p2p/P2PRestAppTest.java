package com.quorum.tessera.p2p;

import com.quorum.tessera.p2p.P2PRestApp;
import com.quorum.tessera.service.locator.ServiceLocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class P2PRestAppTest {

    private static final String contextName = "context";

    private ServiceLocator serviceLocator;

    private P2PRestApp p2PRestApp;

    @Before
    public void setUp() {
        serviceLocator = mock(ServiceLocator.class);
        p2PRestApp = new P2PRestApp(serviceLocator, contextName);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(serviceLocator);
    }

    @Test
    public void getSingletons() {
        p2PRestApp.getSingletons();
        verify(serviceLocator).getServices(contextName);
    }
}
