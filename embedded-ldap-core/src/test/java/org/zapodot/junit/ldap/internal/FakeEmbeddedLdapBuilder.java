package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;
import org.zapodot.junit.ldap.EmbeddedLdapServer;

import static org.zapodot.junit.ldap.internal.EmbeddedLdapServerImpl.createServer;

class FakeEmbeddedLdapBuilder extends AbstractEmbeddedLdapBuilder<FakeEmbeddedLdapBuilder> {

  static FakeEmbeddedLdapBuilder newInstance() {
    return new FakeEmbeddedLdapBuilder();
  }

  @Override
  protected FakeEmbeddedLdapBuilder getThis() {
    return this;
  }

  EmbeddedLdapServer build() {
    try {
      InMemoryDirectoryServer server = createServer(createInMemoryServerConfiguration(), ldifsToImport);
      return new EmbeddedLdapServerImpl(
          server,
          authenticationConfiguration) {

      };
    } catch (LDAPException e) {
      throw new IllegalStateException("Can not initiate in-memory LDAP server due to an exception", e);
    }
  }
}
