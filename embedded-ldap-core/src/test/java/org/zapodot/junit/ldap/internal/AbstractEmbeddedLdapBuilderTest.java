package org.zapodot.junit.ldap.internal;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AbstractEmbeddedLdapBuilderTest {

    @Test
    public void bindingToLegalPort() {
        assertNotNull(FakeEmbeddedLdapBuilder.newInstance().bindingToPort(9999));
    }

    @Test(expected = IllegalStateException.class)
    public void testPrematureLdapConnection() throws Exception {
        FakeEmbeddedLdapBuilder.newInstance().build().ldapConnection();

    }

    @Test(expected = IllegalStateException.class)
    public void testPrematureContext() throws Exception {
        FakeEmbeddedLdapBuilder.newInstance().build().context();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownLDIF() {
        FakeEmbeddedLdapBuilder.newInstance().importingLdifs("nonExisting.ldif").build();

    }

    @Test
    public void testNullLDIF() {
        assertNotNull(FakeEmbeddedLdapBuilder.newInstance().importingLdifs(null).build());

    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalDSN() {
        FakeEmbeddedLdapBuilder.newInstance().usingBindDSN("bindDsn").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPort() {
        FakeEmbeddedLdapBuilder.newInstance().bindingToPort(Integer.MIN_VALUE).build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaNotFound() {
        FakeEmbeddedLdapBuilder.newInstance().withSchema("non-existing-schema.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsNotAFile() {
        FakeEmbeddedLdapBuilder.newInstance().withSchema("folder").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaIsInvalid() {
        FakeEmbeddedLdapBuilder.newInstance().withSchema("invalid.ldif").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSchemaFileUnsupportedIsInvalid() {
        FakeEmbeddedLdapBuilder.newInstance().withSchema("\"#%¤&&%/¤##¤¤").build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort() {
        FakeEmbeddedLdapBuilder.newInstance().bindingToPort(Integer.MAX_VALUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBindAddress() {
        FakeEmbeddedLdapBuilder.newInstance().bindingToAddress("åpsldfåpl");

    }


}

