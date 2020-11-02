package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapServerNoAuthTest {

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder
            .newInstance()
            .usingBindCredentials(null)
            .usingDomainDsn("dc=zapodot,dc=org")
            .importingLdifs("example.ldif")
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
    public void testConnect() throws Exception {
        assertNotNull(embeddedLdapRule.dirContext().search("cn=Sondre Eikanger Kvalo,ou=people,dc=zapodot,dc=org", null));

    }
}
