package org.zapodot.junit.ldap.internal.unboundid;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPInterface;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassLoadingStrategy;
import net.bytebuddy.instrumentation.FieldAccessor;
import net.bytebuddy.instrumentation.MethodDelegation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

/**
 * Creates a new LDAPInterface proxy that hides potentially harmful methods from the LDAPConnection class.
 *
 * This class is part of the internal API and may thus be changed or removed without warning.
 */
public class LDAPInterfaceProxyFactory {
    private final static Class<? extends LDAPInterface> proxyType = new ByteBuddy().subclass(LDAPInterface.class)
                                                                                .method(isDeclaredBy(LDAPInterface.class))
                                                                                .intercept(MethodDelegation.toInstanceField(LDAPConnection.class, "ldapConnection"))
                                                                                .implement(LDAPConnectionProxy.class)
                                                                                .intercept(FieldAccessor.ofBeanProperty())
                                                                                .make()
                                                                                .load(LDAPInterfaceProxyFactory.class
                                                                                              .getClassLoader(),
                                                                                      ClassLoadingStrategy.Default.WRAPPER)
                                                                                .getLoaded();
    /**
     * Create a proxy that delegates to the provided Connection except for calls to "close()" which will be suppressed.
     * @param connection the connection that is to be used as an underlying connection
     * @return a Connection proxy
     */
    public static LDAPInterface createProxy(final LDAPConnection connection) {
        Objects.requireNonNull(connection, "The \"connection\" argument can not be null");
        return createConnectionProxy(connection);

    }

    private static LDAPInterface createConnectionProxy(final LDAPConnection ldapConnection) {
        final LDAPInterface proxy = createProxyInstance();
        ((LDAPConnectionProxy) proxy).setLdapConnection(ldapConnection);
        return proxy;
    }

    private static LDAPInterface createProxyInstance() {
        final Constructor<? extends LDAPInterface> constructor = createConstructorForProxyClass();
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<? extends LDAPInterface> createConstructorForProxyClass() {
        try {
            return proxyType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Found no default constructor for proxy class", e);
        }
    }


}
