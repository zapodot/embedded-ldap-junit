package org.zapodot.junit.ldap.internal.jndi;

import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.AllArguments;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.Origin;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.RuntimeType;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.This;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.lang.reflect.Method;

public class InitialDirContextInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDirContextInterceptor.class);

    public static void close() throws NamingException {
        LOGGER.debug("close() was called in InitialDirContext. Will be ignored");
    }

    @RuntimeType
    public static Object intercept(@Origin(cacheMethod = true) Method method,
                                   @This InitialDirContextProxy delegator,
                                   @AllArguments Object[] arguments) throws Exception {
        return method.invoke(delegator.getDelegatedInitialDirContext(), arguments);
    }
}
