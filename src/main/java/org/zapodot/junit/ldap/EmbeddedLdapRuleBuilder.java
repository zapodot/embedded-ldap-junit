package org.zapodot.junit.ldap;

import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import org.zapodot.junit.ldap.internal.AuthenticationConfiguration;
import org.zapodot.junit.ldap.internal.EmbeddedLdapRuleImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EmbeddedLdapRuleBuilder {

    public static final String DEFAULT_DOMAIN = "dc=example,dc=com";
    public static final String DEFAULT_BIND_DSN = "cn=Directory manager";
    public static final String DEFAULT_BIND_CREDENTIALS = "password";
    public static final String LDAP_SERVER_LISTENER_NAME = "test-listener";
    private String domainDsn = DEFAULT_DOMAIN;

    private String bindDSN = DEFAULT_BIND_DSN;

    private String bindCredentials = DEFAULT_BIND_CREDENTIALS;

    private List<String> ldifsToImport = new LinkedList<>();

    private Integer port = null;

    private AuthenticationConfiguration authenticationConfiguration;

    public EmbeddedLdapRuleBuilder() {
    }

    public static EmbeddedLdapRuleBuilder newInstance() {
        return new EmbeddedLdapRuleBuilder();
    }

    public EmbeddedLdapRuleBuilder usingDomainDsn(final String domainDsn) {
        this.domainDsn = domainDsn;
        return this;
    }

    public EmbeddedLdapRuleBuilder usingBindDSN(final String bindDSN) {
        this.bindDSN = bindDSN;
        return this;
    }

    public EmbeddedLdapRuleBuilder usingBindCredentials(final String bindCredentials) {
        this.bindCredentials = bindCredentials;
        return this;
    }

    public EmbeddedLdapRuleBuilder bindingToPort(final int port) {
        this.port = Integer.valueOf(port);
        return this;
    }

    public EmbeddedLdapRuleBuilder importingLdifs(final String... ldifFiles) {
        if(ldifFiles != null) {
            ldifsToImport.addAll(Arrays.asList(ldifFiles));
        }
        return this;
    }

    public EmbeddedLdapRule build() {
        Objects.requireNonNull(bindDSN, "\"bindDSN\" can not be null");
        return EmbeddedLdapRuleImpl.createForConfiguration(createInMemoryServerConfiguration(),
                                                           authenticationConfiguration,
                                                           ldifsToImport);
    }

    private InMemoryDirectoryServerConfig createInMemoryServerConfiguration() {
        try {
            final InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig =
                    new InMemoryDirectoryServerConfig(domainDsn);

            if(bindCredentials != null) {
                this.authenticationConfiguration = new AuthenticationConfiguration(bindDSN, bindCredentials);
                inMemoryDirectoryServerConfig.addAdditionalBindCredentials(bindDSN, bindCredentials);
            }

            if(port != null) {
                inMemoryDirectoryServerConfig.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig(
                        LDAP_SERVER_LISTENER_NAME,
                                                                                                         port));
            } else {
                inMemoryDirectoryServerConfig.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig(LDAP_SERVER_LISTENER_NAME));
            }
            return inMemoryDirectoryServerConfig;
        } catch (LDAPException e) {
            throw new IllegalStateException("Could not create configuration for the in-memory LDAP instance due to an exception", e);
        }
    }



}
