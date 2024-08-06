package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class EmbeddedLdapServerWithListeningAddressProvidedTest {

    public static InetAddress inetAddress;

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder
            .newInstance()
            .usingDomainDsn("dc=zapodot,dc=org")
            .importingLdifs("example.ldif")
            .bindingToAddress(inetAddress.getHostAddress())
            .build();

    @Before
    public void setup() throws LDAPException {
        ((EmbeddedLdapServerImpl) embeddedLdapRule).startEmbeddedLdapServer();
    }

    @After
    public void teardown() throws LDAPException {
        ((EmbeddedLdapServerImpl) embeddedLdapRule).takeDownEmbeddedLdapServer();
    }

    @BeforeClass
    public static void setupAddress() throws Exception {
        inetAddress = InetAddress.getLocalHost();
    }

    @Test
    public void testLookupAddress() throws Exception {
        assertEquals(inetAddress.getHostAddress(),
                     embeddedLdapRule.unsharedLdapConnection().getConnectedAddress());

    }
}
