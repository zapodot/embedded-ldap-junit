package org.zapodot.junit.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.rules.TestRule;

import javax.naming.Context;
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
     * @return a shared InitialDirContext connected to the in-memory LDAP server
     * @throws NamingException if a context can not be created
     */
    InitialDirContext initialDirContext() throws NamingException;

    /**
     * Like {@link #initialDirContext()}, but returns a Context
     *
     * @return a shared Context connected to the in-memory LDAP server
     * @throws NamingException if context can not be created
     */
    Context context() throws NamingException;
}
