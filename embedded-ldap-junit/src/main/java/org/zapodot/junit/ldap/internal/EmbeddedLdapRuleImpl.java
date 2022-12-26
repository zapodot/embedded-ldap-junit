package org.zapodot.junit.ldap.internal;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.zapodot.junit.ldap.EmbeddedLdapRule;

import javax.net.ssl.SSLSocketFactory;
import java.util.List;

public class EmbeddedLdapRuleImpl extends EmbeddedLdapServerImpl implements EmbeddedLdapRule {

    public static EmbeddedLdapRule createForConfiguration(final InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig,
                                                          final AuthenticationConfiguration authenticationConfiguration,
                                                          final List<String> ldifs, boolean useTls, SSLSocketFactory socketFactory) {
        try {
            return new EmbeddedLdapRuleImpl(EmbeddedLdapServerImpl.createServer(inMemoryDirectoryServerConfig, ldifs),
                authenticationConfiguration, useTls, socketFactory);
        } catch (LDAPException e) {
            throw new IllegalStateException("Can not initiate in-memory LDAP server due to an exception", e);
        }
    }

    public EmbeddedLdapRuleImpl(InMemoryDirectoryServer inMemoryDirectoryServer, AuthenticationConfiguration authenticationConfiguration1,
                                boolean useTls, SSLSocketFactory socketFactory) {
        super(inMemoryDirectoryServer, authenticationConfiguration1, useTls, socketFactory);
    }


    @Override
    public Statement apply(final Statement base, final Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startEmbeddedLdapServer();
                try {
                    base.evaluate();
                } finally {
                    takeDownEmbeddedLdapServer();
                }
            }
        };
    }

}
