package org.zapodot.junit.ldap.internal;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.ldap.EmbeddedLdapServer;
import org.zapodot.junit.ldap.internal.jndi.ContextProxyFactory;
import org.zapodot.junit.ldap.internal.unboundid.LDAPInterfaceProxyFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.List;

abstract class EmbeddedLdapServerImpl implements EmbeddedLdapServer {
    private static final String JAVA_RT_CONTROL_FACTORY = "com.sun.jndi.ldap.DefaultResponseControlFactory";

    private static final String JAVA_RT_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private static Logger logger = LoggerFactory.getLogger(EmbeddedLdapServerImpl.class);
    private final InMemoryDirectoryServer inMemoryDirectoryServer;
    private final AuthenticationConfiguration authenticationConfiguration;
    private LDAPConnection ldapConnection;
    private InitialDirContext initialDirContext;
    private boolean isStarted = false;
    private final boolean useTls;
    private final SSLSocketFactory socketFactory;

    public EmbeddedLdapServerImpl(final InMemoryDirectoryServer inMemoryDirectoryServer,
                                  final AuthenticationConfiguration authenticationConfiguration1,
                                  final boolean useTls, SSLSocketFactory socketFactory) {
        this.inMemoryDirectoryServer = inMemoryDirectoryServer;
        this.authenticationConfiguration = authenticationConfiguration1;
        this.useTls = useTls;
        this.socketFactory = socketFactory;
    }

    protected static InMemoryDirectoryServer createServer(final InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig,
                                                        final List<String> ldifs) throws LDAPException {
        final InMemoryDirectoryServer ldapServer =
                new InMemoryDirectoryServer(inMemoryDirectoryServerConfig);
        if (ldifs != null && !ldifs.isEmpty()) {
            for (final String ldif : ldifs) {
                try {
                    ldapServer.importFromLDIF(false, URLDecoder.decode(Resources.getResource(ldif).getPath(),
                            Charsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Can not URL decode path:" + Resources.getResource(ldif).getPath(),
                            e);
                }
            }
        }
        return ldapServer;
    }

    @Override
    public LDAPInterface ldapConnection() throws LDAPException {
        return LDAPInterfaceProxyFactory.createProxy(createOrGetLdapConnection());
    }

    @Override
    public LDAPConnection unsharedLdapConnection() throws LDAPException {
        return createOrGetLdapConnection();
    }

    private LDAPConnection createOrGetLdapConnection() throws LDAPException {
        if (isStarted) {
            if (ldapConnection == null || ! ldapConnection.isConnected()) {
                ldapConnection = inMemoryDirectoryServer.getConnection();
            }
            return ldapConnection;
        } else {
            throw new IllegalStateException(
                    "Can not get a LdapConnection before the embedded LDAP server has been started");
        }
    }

    @Override
    public Context context() throws NamingException {
        return ContextProxyFactory.asDelegatingContext(createOrGetInitialDirContext());
    }

    @Override
    public DirContext dirContext() throws NamingException {
        return ContextProxyFactory.asDelegatingDirContext(createOrGetInitialDirContext());
    }

    @Override
    public int embeddedServerPort() {
        if(isStarted) {
            return inMemoryDirectoryServer.getListenPort();
        } else {
            throw new IllegalStateException("The embedded server must be started prior to accessing the listening port");
        }
    }

    private InitialDirContext createOrGetInitialDirContext() throws NamingException {
        if (isStarted) {
            if (initialDirContext == null) {
                initialDirContext = new InitialDirContext(createLdapEnvironment());
            }
            return initialDirContext;
        } else {
            throw new IllegalStateException(
                    "Can not get an InitialDirContext before the embedded LDAP server has been started");
        }
    }

    public static abstract class AbstractDelegatingSocketFactory extends SocketFactory {
        public static AbstractDelegatingSocketFactory INSTANCE;

        public static AbstractDelegatingSocketFactory getDefault() {
            return INSTANCE;
        }

        protected abstract SocketFactory getDelegate();

        @Override
        public Socket createSocket() throws IOException, UnknownHostException {
            return getDelegate().createSocket();
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return getDelegate().createSocket(host, port);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
            return getDelegate().createSocket(host, port, localHost, localPort);
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return getDelegate().createSocket(host, port);
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
            return getDelegate().createSocket(address, port, localAddress, localPort);
        }
    }

    private Hashtable<String, String> createLdapEnvironment() {
        final Hashtable<String, String> environment = new Hashtable<>();
        if (socketFactory != null) {
            final Class<?> delegator = (new ByteBuddy()).subclass(AbstractDelegatingSocketFactory.class)
                .defineMethod("getDelegate", SocketFactory.class, Modifier.PROTECTED)
                .intercept(FixedValue.value(socketFactory))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
            try {
                final Object instance = delegator.newInstance();
                delegator.getField("INSTANCE").set(instance, instance);
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
            environment.put("java.naming.ldap.factory.socket", delegator.getCanonicalName());
        }
        environment.put(LdapContext.CONTROL_FACTORIES, JAVA_RT_CONTROL_FACTORY);
        environment.put(Context.PROVIDER_URL, String.format("%s://%s:%s",
                                                            useTls ? "ldaps" : "ldap",
                inMemoryDirectoryServer.getListenAddress().getHostName(),
                embeddedServerPort()));
        environment.put(Context.INITIAL_CONTEXT_FACTORY, JAVA_RT_CONTEXT_FACTORY);
        if (authenticationConfiguration != null) {
            environment.putAll(authenticationConfiguration.toAuthenticationEnvironment());
        }
        return environment;
    }

    protected void startEmbeddedLdapServer() throws LDAPException {
        inMemoryDirectoryServer.startListening();
        isStarted = true;
    }

    protected void takeDownEmbeddedLdapServer() {
        try {
            if (ldapConnection != null && ldapConnection.isConnected()) {
                ldapConnection.close();
            }
            if (initialDirContext != null) {
                initialDirContext.close();
            }
        } catch (NamingException e) {
            logger.info("Could not close initial context, forcing server shutdown anyway", e);
        } finally {
            inMemoryDirectoryServer.shutDown(true);
        }

    }
}
