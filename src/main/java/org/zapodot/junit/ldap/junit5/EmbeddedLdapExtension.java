package org.zapodot.junit.ldap.junit5;

import org.junit.jupiter.api.extension.*;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

/**
 * A JUnit 5 extension that can be registered with @{@link RegisterExtension}
 * (supports both {@code static} and instance fields).
 */
public interface EmbeddedLdapExtension extends EmbeddedLdapServer, Extension,
        BeforeEachCallback, AfterEachCallback,
        BeforeAllCallback, AfterAllCallback {

}
