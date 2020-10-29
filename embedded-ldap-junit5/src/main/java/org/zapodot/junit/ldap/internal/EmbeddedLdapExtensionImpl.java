package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.zapodot.junit.ldap.junit5.EmbeddedLdapExtension;

import java.util.List;

public class EmbeddedLdapExtensionImpl extends EmbeddedLdapServerImpl implements EmbeddedLdapExtension {

    public static EmbeddedLdapExtension createForConfiguration(final InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig,
                                                               final AuthenticationConfiguration authenticationConfiguration,
                                                               final List<String> ldifs) {
        try {
            return new EmbeddedLdapExtensionImpl(createServer(inMemoryDirectoryServerConfig, ldifs),
                    authenticationConfiguration);
        } catch (LDAPException e) {
            throw new IllegalStateException("Can not initiate in-memory LDAP server due to an exception", e);
        }
    }

    private boolean isStartedBeforeAll = false;

    public EmbeddedLdapExtensionImpl(InMemoryDirectoryServer inMemoryDirectoryServer, AuthenticationConfiguration authenticationConfiguration1) {
        super(inMemoryDirectoryServer, authenticationConfiguration1);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        isStartedBeforeAll = true;
        startEmbeddedLdapServer();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        takeDownEmbeddedLdapServer();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if (!isStartedBeforeAll) {
            startEmbeddedLdapServer();
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (!isStartedBeforeAll) {
            takeDownEmbeddedLdapServer();
        }
    }
}
