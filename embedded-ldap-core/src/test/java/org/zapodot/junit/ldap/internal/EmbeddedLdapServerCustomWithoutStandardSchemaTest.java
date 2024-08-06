package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapServerCustomWithoutStandardSchemaTest {

    private final EmbeddedLdapServer embeddedLdapRule = FakeEmbeddedLdapBuilder.newInstance()
                                                                      .withoutDefaultSchema()
                                                                      .withSchema("standard-schema.ldif")
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
    public void testFindCustomAttribute() throws Exception {
        final AttributeTypeDefinition changelogAttribute =
                embeddedLdapRule.ldapConnection().getSchema().getAttributeType("changelog");
        assertNotNull(changelogAttribute);

    }
}
