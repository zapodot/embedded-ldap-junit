package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapServerWithSpacesTest {

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder
            .newInstance()
            .usingDomainDsn("dc=zapodot,dc=org")
            .importingLdifs("folder with space/example.ldif")
            .build();

    @Before
    public void setup() throws LDAPException {
        ((EmbeddedLdapServerImpl) embeddedLdapRule).startEmbeddedLdapServer();
    }

    @After
    public void teardown() throws LDAPException {
        ((EmbeddedLdapServerImpl) embeddedLdapRule).takeDownEmbeddedLdapServer();
    }

    @Test
    public void testIsUp() throws Exception {
        assertNotNull(embeddedLdapRule.ldapConnection().getRootDSE());

    }
}
