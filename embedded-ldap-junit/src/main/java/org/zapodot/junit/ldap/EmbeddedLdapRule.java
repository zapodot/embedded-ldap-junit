package org.zapodot.junit.ldap;

import org.junit.rules.TestRule;

/**
 * A JUnit rule that may be used as either a @Rule or a @ClassRule
 */
public interface EmbeddedLdapRule extends EmbeddedLdapServer, TestRule {

}
