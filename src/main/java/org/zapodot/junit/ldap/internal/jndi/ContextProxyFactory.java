package org.zapodot.junit.ldap.internal.jndi;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * A factory that creates delegating proxys for the Context and DirContext interfaces that delegates to an underlying InitialDirContext
 *
 * This class is part of the internal API and may thus be changed or removed without warning
 */
public class ContextProxyFactory {
    private final static Class<? extends Context> CONTEXT_PROXY_TYPE =
            new ByteBuddy().subclass(Context.class)
                           .name(new NamingStrategy.PrefixingRandom("DelegatingContext"))
                           .method(isDeclaredBy(Context.class).and(not(named("close"))).and(not(isNative())))
                           .intercept(MethodDelegation.toInstanceField(Context.class,
                                                                       "delegatedContext"))
                           .method(isDeclaredBy(Context.class).and(named("close")))
                           .intercept(MethodDelegation.to(ContextInterceptor.class))
                           .implement(ContextProxy.class)
                           .intercept(FieldAccessor.ofBeanProperty())
                           .make()
                           .load(ContextProxyFactory.class
                                         .getClassLoader(),
                                 ClassLoadingStrategy.Default.WRAPPER)
                           .getLoaded();

    private final static Class<? extends DirContext> DIR_CONTEXT_PROXY_TYPE =
            new ByteBuddy().subclass(DirContext.class)
                           .name(new NamingStrategy.PrefixingRandom(
                                   "DelegatingDirContext"))
                           .method(isDeclaredBy(
                                   DirContext.class))
                           .intercept(MethodDelegation
                                              .toInstanceField(
                                                      DirContext.class,
                                                      "delegatedDirContext"))
                           .implement(DirContextProxy.class)
                           .intercept(FieldAccessor.ofBeanProperty())
                           .make()
                           .load(ContextProxyFactory.class
                                         .getClassLoader(),
                                 ClassLoadingStrategy.Default.WRAPPER)
                           .getLoaded();

    public static Context asDelegatingContext(final InitialDirContext initialDirContext) {
        return createProxy(initialDirContext);
    }

    private static Context createProxy(final InitialDirContext initialDirContext) {

        try {
            final Context contextDelegator = getDeclaredConstructor().newInstance();
            ((ContextProxy) contextDelegator).setDelegatedContext(initialDirContext);
            return contextDelegator;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<? extends Context> getDeclaredConstructor() {
        try {
            return CONTEXT_PROXY_TYPE.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Can not find a default constructor for proxy class", e);
        }
    }

    public static DirContext asDelegatingDirContext(final InitialDirContext initialDirContext) {
        try {
            final DirContext dirContext = DIR_CONTEXT_PROXY_TYPE.newInstance();
            ((DirContextProxy) dirContext).setDelegatedDirContext(initialDirContext);
            return dirContext;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not wrap DirContext", e);
        }
    }

}
