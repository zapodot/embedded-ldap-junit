package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertArrayEquals;

public class EmbeddedLdapServerStandardContext {

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder.newInstance()
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
    public void testUsingDefaultDomain() throws Exception {
        assertArrayEquals(new String[]{FakeEmbeddedLdapBuilder.DEFAULT_DOMAIN},
                          embeddedLdapRule.ldapConnection().getRootDSE().getNamingContextDNs());


    }
}
