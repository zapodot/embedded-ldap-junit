package org.zapodot.junit.ldap;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapRuleBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testPrematureLdapConnection() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().build().ldapConnection();

    }

    @Test(expected = IllegalStateException.class)
    public void testPrematureContext() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().build().context();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownLDIF() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().importingLdifs("nonExisting.ldif").build();

    }

    @Test
    public void testNullLDIF() throws Exception {
        assertNotNull(EmbeddedLdapRuleBuilder.newInstance().importingLdifs(null).build());

    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalDSN() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().usingBindDSN("bindDsn").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPort() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().bindingToPort(Integer.MIN_VALUE).build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaNotFound() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().withSchema("non-existing-schema.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsNotAFile() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().withSchema("folder").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsInvalid() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().withSchema("invalid.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().bindingToPort(Integer.MAX_VALUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBindAddress() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().bindingToAddress("åpsldfåpl");

    }


}