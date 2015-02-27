package org.zapodot.junit.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.rules.TestRule;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

/**
 * A JUnit rule that may be used as either a @Rule or a @ClassRule
 */
public interface EmbeddedLdapRule extends TestRule {

    /**
     * For tests depending on the UnboundID LDAP SDK
     *
     * @return a shared LDAPConnection
     * @throws LDAPException i a connection can not be opened
     */
    LDAPConnection ldapConnection() throws LDAPException;

    /**
     * For tests depending on the standard Java JNDI API
     *
     * @return a shared InitialDirContext
     * @throws NamingException if a context can not be established
     */
    InitialDirContext initialDirContext() throws NamingException;
}
