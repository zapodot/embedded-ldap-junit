package org.zapodot.junit.ldap.internal.unboundid;

import com.unboundid.ldap.sdk.LDAPConnection;

public interface LDAPConnectionProxy {

    LDAPConnection getLdapConnection();

    void setLdapConnection(final LDAPConnection ldapConnection);
}
