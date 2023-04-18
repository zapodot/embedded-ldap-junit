package org.zapodot.ldap.jupiter;

import org.junit.jupiter.api.extension.*;

/**
 * Extension for JUnit 5 Jupiter
 */
public interface EmbeddedLdapExtension extends Extension, BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {
}
