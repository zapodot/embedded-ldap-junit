package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertTrue;

public class EmbeddedLdapServerCustomWithoutSchemaTest {

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder.newInstance()
                                                                      .withoutDefaultSchema()
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
    public void testEmptySchema() throws Exception {
        final Schema schema =
                embeddedLdapRule.ldapConnection().getSchema();
        assertTrue(schema.getAttributeTypes().isEmpty());

    }
}
