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

    @Test(expected = IllegalStateException.class)
    public void testIllegalPort() throws Exception {
        EmbeddedLdapRuleBuilder.newInstance().bindingToPort(Integer.MIN_VALUE).build();

    }
}