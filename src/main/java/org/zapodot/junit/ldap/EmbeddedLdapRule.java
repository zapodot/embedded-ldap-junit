package org.zapodot.junit.ldap;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import org.junit.rules.TestRule;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

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
    LDAPInterface ldapConnection() throws LDAPException;

    /**
     * For tests depending on the standard Java JNDI API
     *
     * @return a shared Context connected to the in-memory LDAP server
     * @throws NamingException if context can not be created
     */
    Context context() throws NamingException;

    /**
     * Like {@link #context()}, but returns a DirContext
     *
     * @return a DirContext connected to the in-memory LDAP server
     * @throws NamingException if a LDAP failure happens during DirContext creation
     */
    DirContext dirContext() throws NamingException;
}
