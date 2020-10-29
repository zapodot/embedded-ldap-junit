package org.zapodot.junit.ldap.junit5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmbeddedLdapExtensionBuilderTest {

    @Test
    void bindingToLegalPort() {
        assertNotNull(EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(9999));
    }

    @Test
    void testPrematureLdapConnection() {
        assertThrows(IllegalStateException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().build().ldapConnection()
        );
    }

    @Test
    void testPrematureContext() {
        assertThrows(IllegalStateException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().build().context()
        );
    }

    @Test
    void testUnknownLDIF() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().importingLdifs("nonExisting.ldif").build()
        );
    }

    @Test
    void testNullLDIF() {
        assertNotNull(EmbeddedLdapExtensionBuilder.newInstance().importingLdifs(null).build());

    }

    @Test
    void testIllegalDSN() {
        assertThrows(IllegalStateException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().usingBindDSN("bindDsn").build()
        );
    }

    @Test
    void testIllegalPort() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(Integer.MIN_VALUE).build()
        );
    }

    @Test
    void testSchemaNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().withSchema("non-existing-schema.ldif").build()
        );
    }

    @Test
    void testSchemaIsNotAFile() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().withSchema("folder").build()
        );
    }

    @Test
    void testSchemaIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().withSchema("invalid.ldif").build()
        );
    }

    @Test
    void testSchemaFileUnsupportedIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().withSchema("\"#%¤&&%/¤##¤¤").build()
        );
    }

    @Test
    void testInvalidPort() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().bindingToPort(Integer.MAX_VALUE)
        );
    }

    @Test
    void testInvalidBindAddress() {
        assertThrows(IllegalArgumentException.class, () ->
            EmbeddedLdapExtensionBuilder.newInstance().bindingToAddress("åpsldfåpl")
        );
    }


}
