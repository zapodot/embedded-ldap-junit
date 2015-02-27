package org.zapodot.junit.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.rules.TestRule;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public interface EmbeddedLdapRule extends TestRule {

    LDAPConnection ldapConnection() throws LDAPException;

    InitialDirContext initialDirContext() throws NamingException;
}
