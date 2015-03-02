package org.zapodot.junit.ldap.internal.unboundid;

import com.unboundid.ldap.sdk.Control;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.AllArguments;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.Origin;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.RuntimeType;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.This;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class LDAPInterfaceInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LDAPInterfaceInterceptor.class);

    public static void close() {
        logger.debug("close() is suppressed");
    }

    public static void close(final Control[] controls) {
        logger.debug("close(Control[]) is suppressed");
    }

    public static void reconnect() {
        logger.debug("reconnect() is suppressed");
    }

    @RuntimeType
    public static Object intercept(@Origin(cacheMethod = true) Method method,
                                   @This LDAPConnectionProxy delegator,
                                   @AllArguments Object[] arguments) throws Exception {
        return method.invoke(delegator.getLdapConnection(), arguments);
    }
}
