package org.zapodot.junit.ldap.junit5;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EmbeddedLdapExtensionBuilderTest {

    @Test
    public void bindingToLegalPort() {
        assertNotNull(EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(9999));
    }

    @Test(expected = IllegalStateException.class)
    public void testPrematureLdapConnection() throws Exception {
        EmbeddedLdapExtensionBuilder.newInstance().build().ldapConnection();

    }

    @Test(expected = IllegalStateException.class)
    public void testPrematureContext() throws Exception {
        EmbeddedLdapExtensionBuilder.newInstance().build().context();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownLDIF() {
        EmbeddedLdapExtensionBuilder.newInstance().importingLdifs("nonExisting.ldif").build();

    }

    @Test
    public void testNullLDIF() {
        assertNotNull(EmbeddedLdapExtensionBuilder.newInstance().importingLdifs(null).build());

    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalDSN() {
        EmbeddedLdapExtensionBuilder.newInstance().usingBindDSN("bindDsn").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPort() {
        EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(Integer.MIN_VALUE).build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaNotFound() {
        EmbeddedLdapExtensionBuilder.newInstance().withSchema("non-existing-schema.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsNotAFile() {
        EmbeddedLdapExtensionBuilder.newInstance().withSchema("folder").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsInvalid() {
        EmbeddedLdapExtensionBuilder.newInstance().withSchema("invalid.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaFileUnsupportedIsInvalid() {
        EmbeddedLdapExtensionBuilder.newInstance().withSchema("\"#%¤&&%/¤##¤¤").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort() {
        EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(Integer.MAX_VALUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBindAddress() {
        EmbeddedLdapExtensionBuilder.newInstance().bindingToAddress("åpsldfåpl");

    }


}
