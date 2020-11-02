package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertArrayEquals;

public class EmbeddedLdapServerMultipleDSNs {

    public static final String DSN_ROOT_ONE = "dc=zapodot,dc=com";
    public static final String DSN_ROOT_TWO = "dc=zapodot,dc=org";

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder.newInstance()
                                                                      .usingDomainDsn(DSN_ROOT_ONE)
                                                                      .usingDomainDsn(DSN_ROOT_TWO)
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
    public void testCheckNamingContexts() throws Exception {
        final LDAPInterface ldapConnection = embeddedLdapRule.ldapConnection();
        final String[] namingContextDNs = ldapConnection.getRootDSE().getNamingContextDNs();
        assertArrayEquals(new String[]{DSN_ROOT_ONE, DSN_ROOT_TWO}, namingContextDNs);

    }
}
