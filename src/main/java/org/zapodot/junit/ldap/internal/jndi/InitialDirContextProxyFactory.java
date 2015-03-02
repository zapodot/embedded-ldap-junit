package org.zapodot.junit.ldap.internal.jndi;

import com.google.common.primitives.Primitives;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.ClassLoadingStrategy;
import net.bytebuddy.instrumentation.FieldAccessor;
import net.bytebuddy.instrumentation.MethodDelegation;
import net.bytebuddy.modifier.Visibility;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class InitialDirContextProxyFactory {
    private final static Class<? extends InitialDirContext> proxyType =
            new ByteBuddy().subclass(InitialDirContext.class)
                           .method(any())
                           .intercept(MethodDelegation.to(
                                   InitialDirContextInterceptor.class))
                           .defineField("delegatedInitialDirContext",
                                        InitialDirContext.class,
                                        Visibility.PRIVATE)
                           .implement(InitialDirContextProxy.class)
                           .intercept(FieldAccessor.ofBeanProperty())
                           .make()
                           .load(InitialDirContextProxyFactory.class
                                         .getClassLoader(),
                                 ClassLoadingStrategy.Default.WRAPPER)
                           .getLoaded();

    public static Context createContextProxy(final InitialDirContext initialDirContext) {
        try {
            return new ByteBuddy()
                    .subclass(Context.class)
                    .name(new NamingStrategy.PrefixingRandom("DelegatingContext"))
                    .method(isDeclaredBy(Context.class))
                    .intercept(MethodDelegation.to(initialDirContext))
                    .make()
                    .load(InitialDirContextProxyFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not wrap Context", e);
        }
    }

    public static InitialDirContext createProxy(final InitialDirContext initialDirContext) {

        try {
            final InitialDirContext initialDirContextDelegator = getDeclaredConstructor().newInstance(true);
            ((InitialDirContextProxy) initialDirContextDelegator).setDelegatedInitialDirContext(initialDirContext);
            return initialDirContextDelegator;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<? extends InitialDirContext> getDeclaredConstructor() {
        try {
            return proxyType.getDeclaredConstructor(Primitives.unwrap(Boolean.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Can not find a default constructor for proxy class", e);
        }
    }

}
